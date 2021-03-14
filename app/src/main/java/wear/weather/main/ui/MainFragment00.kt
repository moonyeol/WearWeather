package wear.weather.main.ui

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.TextureView
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import wear.weather.R
import wear.weather.main.model.CurrentWeatherData
import wear.weather.retrofit.RetrofitClient
import wear.weather.util.OPEN_AIR_KEY
import wear.weather.util.OPEN_AIR_URL
import wear.weather.util.OPEN_WEATHER_KEY
import wear.weather.util.OPEN_WEATHER_CUR_URL
import java.text.SimpleDateFormat
import java.util.*

class MainFragment00 : Fragment() {
    private lateinit var tvCurDayTime: TextView
    private lateinit var tvCurWeatherTemp: TextView
    private lateinit var tvCompareYesterdayWeather: TextView
    private lateinit var tvWeekWeather: TextView
    private lateinit var tvCoordi: TextView
    private lateinit var tvCurPerceivedTemp: TextView
    private lateinit var tvCurFineUltraDust: TextView

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
        tvCurFineUltraDust = rootView.findViewById(R.id.tv_cur_fine_ultra_dust)
        return rootView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val location = getCurrentLocation()
        val (curLat, curLot) = arrayListOf<String>(
            location.latitude.toString(),
            location.longitude.toString()
        )
        val geoCoder = Geocoder(activity!!.applicationContext, Locale.getDefault())
        val addresses = geoCoder.getFromLocation(curLat.toDouble(), curLot.toDouble(), 7)
        val address = addresses[0]
        val station = address.subLocality
        getTime()
        getCurrentWeather(curLat, curLot)
        getCurrentDust(station)
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

    private fun getCurrentDust(station: String) {
        val res: Call<JsonObject> = RetrofitClient
            .getInstance()
            .buildRetrofit(OPEN_AIR_URL)
            .getCurrentDust(
                stationName = station,
                dataTerm = "DAILY",
                pageNo = 1,
                numOfRows = 1,
                serviceKey = OPEN_AIR_KEY,
                ver = "1.3",
                returnType = "json"
            )

        res.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                val body = response.body()!!
                Log.d(TAG, "미세먼지 onResponse: ${body.toString()}")
//                tvCurFineUltraDust
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                Log.d(TAG, "미세먼지 onFailure: $t")
            }
        })
    }


    companion object {
        private const val TAG = "MainFragment00"


    }
}