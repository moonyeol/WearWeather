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
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import wear.weather.R
import wear.weather.main.ui.MainActivity.Companion.pm10Value
import wear.weather.main.ui.MainActivity.Companion.pm2_5Grade
import wear.weather.main.ui.MainActivity.Companion.pm2_5Value
import wear.weather.retrofit.RetrofitClient
import wear.weather.util.OPEN_WEATHER_CUR_URL
import wear.weather.util.OPEN_WEATHER_KEY
import java.text.SimpleDateFormat
import java.util.*

class MainFragment00 : Fragment() {
    private lateinit var tvCurDayTime: TextView
    private lateinit var tvCurWeatherTemp: TextView
    private lateinit var tvCompareYesterdayWeather: TextView
    private lateinit var tvWeekWeather: TextView
    private lateinit var tvCoordi: TextView
    private lateinit var tvCurPerceivedTemp: TextView
    private lateinit var tvCurDustGrade: TextView


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView: ViewGroup =
            inflater.inflate(R.layout.fragment_main_00, container, false) as ViewGroup
        tvCurDayTime = rootView.findViewById(R.id.tv_cur_day_time)
        tvCurWeatherTemp = rootView.findViewById(R.id.tv_cur_weather_temp)
        tvCompareYesterdayWeather = rootView.findViewById(R.id.tv_compare_yesterday_weather)
        tvWeekWeather = rootView.findViewById(R.id.tv_week_weather)
        tvCoordi = rootView.findViewById(R.id.tv_coordi)
        tvCurPerceivedTemp = rootView.findViewById(R.id.tv_cur_perceived_temp)
        tvCurDustGrade = rootView.findViewById(R.id.tv_cur_dust_grade)
        return rootView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val location = getCurrentLocation()
        val (curLat, curLot) = arrayListOf<String>(
            location.latitude.toString(),
            location.longitude.toString()
        )
        getTime()
        getCurrentWeather(curLat, curLot)
        tvCurDustGrade.text = pm2_5Grade.toString()
    }

    @SuppressLint("MissingPermission")
    private fun getCurrentLocation(): Location {
        val locationManager =
            activity!!.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER) as Location
    }

    private fun getTime() {
        val now = System.currentTimeMillis()
        val mDate = Date(now)
        val simpleDate = SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
        val time = simpleDate.format(mDate)

        tvCurDayTime.text = time
    }

    private fun getCurrentWeather(lat: String, lot: String) {
        val res: Call<JsonObject> = RetrofitClient
            .getInstance()
            .buildRetrofit(OPEN_WEATHER_CUR_URL)
            .getCurrentWeather(lat, lot, OPEN_WEATHER_KEY)
        res.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                val body = response.body()!!
                Log.d(TAG, "onResponse: ${body.toString()}")
                val weather = body.get("weather").asJsonArray.get(0).asJsonObject["main"].toString()
                val temps = body.get("main").asJsonObject
                val (temp, feelsLike, tempCross) = intArrayOf(
                    (temps.asJsonObject["temp"].asDouble - 273.15).toInt(),
                    (temps.asJsonObject["feels_like"].asDouble - 273.15).toInt(),
                    (temps.asJsonObject["temp_max"].asDouble - temps.asJsonObject["temp_min"].asDouble).toInt()
                )

                tvCurWeatherTemp.text = "날씨: $weather, 기온: $temp"
                tvCurPerceivedTemp.text = "일교차: $tempCross / 체감온도: $feelsLike"
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                Log.d(TAG, "onFailure: ")
            }
        })
    }


    companion object {
        private const val TAG = "MainFragment00"


    }
}