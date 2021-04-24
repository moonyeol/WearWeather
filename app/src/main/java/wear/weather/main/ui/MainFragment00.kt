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
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import wear.weather.R
import wear.weather.databinding.FragmentMain00Binding
import wear.weather.main.ui.MainActivity.Companion.pm10Grade
import wear.weather.retrofit.RetrofitClient
import wear.weather.util.OPEN_WEATHER_CUR_URL
import wear.weather.util.OPEN_WEATHER_KEY
import wear.weather.util.toTemp
import java.text.SimpleDateFormat
import java.util.*

class MainFragment00 : Fragment() {
    private lateinit var binding: FragmentMain00Binding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_main_00, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val location = getCurrentLocation()
        val (curLat, curLot) = arrayListOf<String>(
            location.latitude.toString(),
            location.longitude.toString()
        )
//        getTime()
        getCurrentWeather(curLat, curLot)
        binding.tvCurFineDustGrade.text = pm10Grade
    }

    @SuppressLint("MissingPermission")
    private fun getCurrentLocation(): Location {
        val locationManager =
            activity!!.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER) as Location
    }

    /*private fun getTime() {
        val now = System.currentTimeMillis()
        val mDate = Date(now)
        val simpleDate = SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
        val time = simpleDate.format(mDate)
    }*/

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
                val (temp, feelsLike, tempMax, tempMin) = intArrayOf(
                    (temps.asJsonObject["temp"].asDouble - 273.15).toInt(),
                    (temps.asJsonObject["feels_like"].asDouble - 273.15).toInt(),
                    (temps.asJsonObject["temp_max"].asDouble - 273.15).toInt(),
                    (temps.asJsonObject["temp_min"].asDouble - 273.15).toInt()
                )
                binding.tvCurPerceivedTemp.text = toTemp(feelsLike)
                binding.tvCurTemp.text = toTemp(temp)
                binding.tvCurMaxMinTemp.text = "${toTemp(tempMax)} / ${toTemp(tempMin)}"
//                tvCurWeatherTemp.text = "날씨: $weather, 기온: $temp"
//                tvCurPerceivedTemp.text = "일교차: $tempCross / 체감온도: $feelsLike"
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