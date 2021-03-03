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
            .buildRetrofit(OPEN_WEATHER_URL)
            .getCurrentWeather(lat, lot, OPEN_WEATHER_KEY)
        res.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                val body = response.body()!!
                Log.d(TAG, "onResponse: ${body.toString()}")
                val weather = body.get("weather").asJsonArray.get(0).asJsonObject["main"].toString()
                val temps = body.get("main").asJsonArray.get(0)
                val (temp, feelsLike, tempCross) = doubleArrayOf(
                    temps.asJsonObject["temp"].asDouble,
                    temps.asJsonObject["feels_like"].asDouble,
                    temps.asJsonObject["temp_max"].asDouble - temps.asJsonObject["temp_min"].asDouble
                )

                tvCurWeatherTemp.text = "$weather, $temp"
                tvCurPerceivedTemp.text = "$tempCross / $feelsLike"
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                Log.d(TAG, "onFailure: ")
            }
        })
    }

    private fun getCurrentDust(station: String) {
        val url =
            "https://openapi.airkorea.or.kr/openapi/services/rest/ArpltnInforInqireSvc/getMsrstnAcctoRltmMesureDnsty?stationName=$station&dataTerm=month&pageNo=1&numOfRows=10&ServiceKey=8EalanJdJ%2Fzj%2FhGCCQYGwLK9uBqvU0RP4EalQSwvmdxC%2FJy6ygdrweLYg0C%2BC%2BsQ8YIzHVOM3DlkPOoZ%2Fnqyew%3D%3D&ver=1.3&_returnType=json/"
//        중간에? 지점: Dnsty? 이거 GET방식이자나 요기 전까지가 BASEURL 수정수정
        val res: Call<JsonObject> = RetrofitClient
            .getInstance()
            .buildRetrofit(url)
            .getCurrentDust()
        res.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                val body = response.body()!!
                Log.d(TAG, "onResponse: ${body.toString()}")
//                tvCurFineUltraDust
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                Log.d(TAG, "onFailure: ")
            }
        })
    }


    companion object {
        private const val TAG = "MainFragment00"
        private const val OPEN_WEATHER_KEY = "9252cb0c939c8030161e7fe49d08e72f"
        private const val OPEN_WEATHER_URL = "https://api.openweathermap.org/data/2.5/"

    }
}