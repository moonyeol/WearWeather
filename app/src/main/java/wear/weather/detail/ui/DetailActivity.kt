package wear.weather.detail.ui

import android.content.Context
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import wear.weather.R
import wear.weather.databinding.ActivityDetailBinding
import wear.weather.main.adapter.MainHourlyWeatherAdapter
import wear.weather.main.adapter.MainWeeklyWeatherAdapter
import wear.weather.main.model.HourlyWeatherData
import wear.weather.main.model.WeeklyWeatherData
import wear.weather.main.ui.MainActivity
import wear.weather.main.ui.MainActivity.Companion.currentPhotoPath
import wear.weather.main.ui.MainFragment01
import wear.weather.retrofit.RetrofitClient
import wear.weather.util.OPEN_WEATHER_URL
import wear.weather.util.OPEN_WEATHER_KEY
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class DetailActivity:AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_detail)
        binding.activity = this
        binding.detailToolbar.setNavigationIcon(R.drawable.ic_third)
        binding.detailToolbar.setNavigationOnClickListener {
            finish()
        }
        binding.recyclerHourlyWeather.layoutManager = LinearLayoutManager(this@DetailActivity, RecyclerView.HORIZONTAL, false)
        binding.recyclerWeeklyWeather.layoutManager = LinearLayoutManager(this@DetailActivity, RecyclerView.HORIZONTAL, false)

        val intent = intent
        val (lat,lot) = arrayOf(intent.getStringExtra("lat").toString(),intent.getStringExtra("lot").toString())

        getDailyOrHourlyWeather(lat, lot)
        getWeeklyWeather(lat, lot)

//        binding.tvFineDustValue.text = MainActivity.pm2_5Value.toString()

    }
    private fun getDailyOrHourlyWeather(lat: String, lot: String) {
        val hourWeatherArr = ArrayList<HourlyWeatherData>()

        val res: Call<JsonObject> = RetrofitClient
            .getInstance()
            .buildRetrofit(OPEN_WEATHER_URL)
            .getDailyOrHourlyWeather(lat, lot, "current,minutely,daily", OPEN_WEATHER_KEY)
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

                    val humidity = hourlyWeather.asJsonObject["humidity"].asInt


                    hourWeatherArr.add(HourlyWeatherData(time, weather, 1.1, "$temp˚"))
                }
                val mainHourlyWeatherAdapter = MainHourlyWeatherAdapter(hourWeatherArr)
                binding.recyclerHourlyWeather.adapter = mainHourlyWeatherAdapter
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
            .buildRetrofit(OPEN_WEATHER_URL)
            .getDailyOrHourlyWeather(lat, lot, "current,minutely,hourly", OPEN_WEATHER_KEY)
        res.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                val body = response.body()!!
                // 날짜는 인덱스 별로 1일 단위
                val weeklyJsonArr = body.get("daily").asJsonArray
                for (i in 0 until 8) {

                    val weeklyWeather = weeklyJsonArr[i]
                    val dt = weeklyWeather.asJsonObject["dt"].asLong
                    val simpleDateFormat = SimpleDateFormat("yyyyMMdd")
                    val tmpDate = simpleDateFormat.format(Date(dt * 1000))//20210314

                    Log.d(TAG, "time: $tmpDate i:$i")
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

                    if (i == 0) {
                        // 자외선 습도 풍속
                        val uviValue = weeklyWeather.asJsonObject["uvi"].asInt
                        val windSpeed = weeklyWeather.asJsonObject["wind_speed"].asInt

                        binding.tvUvValue.text = "이게 자외선: $uviValue"
//                        Log.d(TAG, "time: $tmpDate")
                        Log.d(TAG, "uviValue: $uviValue")
                        Log.d(TAG, "windSpeed: $windSpeed")


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
                binding.recyclerWeeklyWeather.adapter = mainWeeklyWeatherAdapter
                mainWeeklyWeatherAdapter.notifyDataSetChanged()
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                Log.d(TAG, "onFailure: ")
            }
        })
    }

    companion object {
        private const val TAG = "DetailActivity"
    }
}