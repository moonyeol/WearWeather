package wear.weather.main.ui

import android.content.Intent
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
import wear.weather.detail.ui.DetailActivity

import wear.weather.main.ui.MainActivity.Companion.pm10Grade
import wear.weather.retrofit.RetrofitClient
import wear.weather.util.OPEN_WEATHER_URL
import wear.weather.util.OPEN_WEATHER_KEY
import wear.weather.util.toTemp

class MainFragment00 : Fragment() {

    private lateinit var binding: FragmentMain00Binding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_main_00, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


        val (curLat, curLot) = arrayListOf(
            MainActivity.lat.toString(),
            MainActivity.lot.toString()
        )
        getCurrentWeather(curLat, curLot)
        binding.tvCurFineDustGrade.text = pm10Grade

        binding.btnShowDetail.setOnClickListener {
            Log.d(TAG, "onActivityCreated: lot: ${MainActivity.lat}")

            val intent = Intent(activity!!.applicationContext, DetailActivity::class.java)
            intent.putExtra("lat", MainActivity.lat.toString())
            intent.putExtra("lot", MainActivity.lot.toString())
            startActivity(intent)
        }

    }

    fun setTvCurFineDustGrade() {
        binding.tvCurFineDustGrade.text = pm10Grade
    }

    private fun getCurrentWeather(lat: String, lot: String) {
        val res: Call<JsonObject> = RetrofitClient
            .getInstance()
            .buildRetrofit(OPEN_WEATHER_URL)
            .getCurrentWeather(lat, lot, OPEN_WEATHER_KEY)
        res.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                val body = response.body()!!
                Log.d(TAG, "onResponse: $body")
                val weather = body.get("weather").asJsonArray.get(0).asJsonObject["main"].toString()
                val temps = body.get("main").asJsonObject
                val (temp, feelsLike, tempMax, tempMin) = intArrayOf(
                    (temps.asJsonObject["temp"].asDouble - 273.15).toInt(),
                    (temps.asJsonObject["feels_like"].asDouble - 273.15).toInt(),
                    (temps.asJsonObject["temp_max"].asDouble - 273.15).toInt(),
                    (temps.asJsonObject["temp_min"].asDouble - 273.15).toInt()
                )
                Log.d(TAG, "onResponse: $weather")
                when (weather) {
                    // clear, clouds, rain, snow, thunderstorm, drizzle, atmosphere, extreme, additional
                }
                binding.tvCurPerceivedTemp.text = toTemp(feelsLike)
                binding.tvCurTemp.text = toTemp(temp)
                binding.tvCurMaxMinTemp.text = "${toTemp(tempMax)} / ${toTemp(tempMin)}"

            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                Log.d(TAG, "onFailure: ")
            }
        })
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart: ")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause: ")
    }

    override fun onResume() {
        super.onResume()

/*
        val (curLat, curLot) = arrayListOf(
            MainActivity.lat.toString(),
            MainActivity.lot.toString()
        )
        getCurrentWeather(curLat, curLot)
        binding.tvCurFineDustGrade.text = pm10Grade*/
    }

    companion object {
        private const val TAG = "MainFragment00"
    }
}