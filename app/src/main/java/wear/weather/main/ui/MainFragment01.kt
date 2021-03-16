package wear.weather.main.ui

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import wear.weather.R
import wear.weather.main.adapter.MainHourlyWeatherAdapter
import wear.weather.main.adapter.MainWeeklyWeatherAdapter
import wear.weather.main.model.HourlyWeatherData
import wear.weather.main.model.WeeklyWeatherData
import wear.weather.retrofit.RetrofitClient
import wear.weather.util.OPEN_WEATHER_HOURLY_WEEKLY_URL
import wear.weather.util.OPEN_WEATHER_KEY
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MainFragment01 : Fragment() {

    private lateinit var recyclerHourlyWeather: RecyclerView
    private lateinit var recyclerWeeklyWeather: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView: ViewGroup =
            inflater.inflate(R.layout.fragment_main_01, container, false) as ViewGroup

        recyclerHourlyWeather = rootView.findViewById(R.id.recycler_hourly_weather)
        recyclerWeeklyWeather = rootView.findViewById(R.id.recycler_weekly_weather)

        recyclerHourlyWeather.layoutManager =
            LinearLayoutManager(this@MainFragment01.context, RecyclerView.HORIZONTAL, false)
        recyclerWeeklyWeather.layoutManager =
            LinearLayoutManager(this@MainFragment01.context, RecyclerView.HORIZONTAL, false)

        return rootView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val location = getCurrentLocation()
        getHourlyWeather(location.latitude.toString(), location.longitude.toString())
        getWeeklyWeather(location.latitude.toString(), location.longitude.toString())
    }

    @SuppressLint("MissingPermission")
    private fun getCurrentLocation(): Location {
        val locationManager =
            activity!!.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER) as Location
    }

    private fun getHourlyWeather(lat: String, lot: String) {
        val hourWeatherArr = ArrayList<HourlyWeatherData>()

        val res: Call<JsonObject> = RetrofitClient
            .getInstance()
            .buildRetrofit(OPEN_WEATHER_HOURLY_WEEKLY_URL)
            .getHourlyWeather(lat, lot, "current,minutely,daily", OPEN_WEATHER_KEY)
        res.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                val body = response.body()!!

                // 시간은 인덱스 별로 1시간 단위
                val hourlyJsonArr = body.get("hourly").asJsonArray
                for (i in 0 until 25 step 3) {
                    val hourlyWeather = hourlyJsonArr[i]
                    val dt = hourlyWeather.asJsonObject["dt"].asLong
                    val simpleDateFormat = SimpleDateFormat("HH")
                    val time = if (i == 0) "지금" else simpleDateFormat.format(Date(dt * 1000))
                    val weather =
                        hourlyWeather.asJsonObject["weather"].asJsonArray[0].asJsonObject["main"].toString()
                    val temp = (hourlyWeather.asJsonObject["temp"].asDouble - 273.15).toInt()
                    hourWeatherArr.add(HourlyWeatherData(time, weather, 1.1, "${temp.toString()}˚"))
                }
                val mainHourlyWeatherAdapter = MainHourlyWeatherAdapter(hourWeatherArr)
                recyclerHourlyWeather.adapter = mainHourlyWeatherAdapter
                mainHourlyWeatherAdapter.notifyDataSetChanged()
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                Log.d(TAG, "onFailure: ")
            }
        })
    }

    private fun getWeeklyWeather(lat: String, lot: String) {
        val weeklyWeatherArr = ArrayList<WeeklyWeatherData>()

        val res: Call<JsonObject> = RetrofitClient
            .getInstance()
            .buildRetrofit(OPEN_WEATHER_HOURLY_WEEKLY_URL)
            .getHourlyWeather(lat, lot, "current,minutely,hourly", OPEN_WEATHER_KEY)
        res.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                val body = response.body()!!
                // 날짜는 인덱스 별로 1일 단위
                val weeklyJsonArr = body.get("daily").asJsonArray
                for (i in 0 until 6) {
                    val weeklyWeather = weeklyJsonArr[i]
                    val dt = weeklyWeather.asJsonObject["dt"].asLong
                    val simpleDateFormat = SimpleDateFormat("yyyyMMdd")
                    val tmpDate = simpleDateFormat.format(Date(dt * 1000))//20210314
                    val date = simpleDateFormat.parse(tmpDate)
                    val calendar = Calendar.getInstance()
                    calendar.time = date
                    val dayOfWeek =
                        if (i == 0) "오늘" else when (calendar.get(Calendar.DAY_OF_WEEK)) {
                            1 -> "일"
                            2 -> "월"
                            3 -> "화"
                            4 -> "수"
                            5 -> "목"
                            6 -> "금"
                            7 -> "토"
                            else -> "error"
                        }
                    val weather =
                        weeklyWeather.asJsonObject["weather"].asJsonArray[0].asJsonObject["main"].toString()
                    val temp = weeklyWeather.asJsonObject["temp"]
                    val maxTemp = (temp.asJsonObject["max"].asDouble - 273.15).toInt()
                    val minTemp = (temp.asJsonObject["min"].asDouble - 273.15).toInt()
                    weeklyWeatherArr.add(
                        WeeklyWeatherData(
                            dayOfWeek,
                            weather,
                            "$maxTemp˚",
                            "$minTemp˚"
                        )
                    )
                }
                val mainWeeklyWeatherAdapter = MainWeeklyWeatherAdapter(weeklyWeatherArr)
                recyclerWeeklyWeather.adapter = mainWeeklyWeatherAdapter
                mainWeeklyWeatherAdapter.notifyDataSetChanged()
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                Log.d(TAG, "onFailure: ")
            }
        })
    }

    companion object {
        private const val TAG = "MainFragment01"
    }
}