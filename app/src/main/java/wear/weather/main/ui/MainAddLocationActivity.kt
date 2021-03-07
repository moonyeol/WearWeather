package wear.weather.main.ui

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import wear.weather.R
import wear.weather.main.adapter.MainAddLocationAdapter
import wear.weather.main.model.CurrentWeatherData
import wear.weather.retrofit.RetrofitClient
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MainAddLocationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_add_location)
        Log.d(Companion.TAG, "onCreate: ??")
        val location = getCurrentLocation()
        getCurrentWeather(location.latitude.toString(), location.longitude.toString())
    }

    @SuppressLint("MissingPermission")
    private fun getCurrentLocation(): Location {
        val locationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER) as Location
    }

    private fun getCurrentWeather(lat: String, lot: String) {
        val res: Call<JsonObject> = RetrofitClient
            .getInstance()
            .buildRetrofit(MainFragment00.OPEN_WEATHER_URL)
            .getCurrentWeather(lat, lot, MainFragment00.OPEN_WEATHER_KEY)
        res.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                val body = response.body()!!
                val weather = body.get("weather").asJsonArray.get(0).asJsonObject["main"].toString()
                val temps = body.get("main").asJsonObject
                val (temp, feelsLike, tempCross) = intArrayOf(
                    (temps.asJsonObject["temp"].asDouble - 273.15).toInt(),
                    (temps.asJsonObject["feels_like"].asDouble - 273.15).toInt(),
                    (temps.asJsonObject["temp_max"].asDouble - temps.asJsonObject["temp_min"].asDouble).toInt()
                )
                val now = System.currentTimeMillis()
                val mDate = Date(now)
                val simpleDate = SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
                val time = simpleDate.format(mDate)

                val list = ArrayList<CurrentWeatherData>()
                list.add(
                    CurrentWeatherData(
                        weather,
                        temp.toDouble(),
                        0.0,
                        0.0,
                        feelsLike.toDouble(),
                        null,
                        null,
                        null,
                        null,
                        "광진구",
                        time
                    )
                )
                val adapter = MainAddLocationAdapter(list)
                val recyclerAddLocation  = findViewById<RecyclerView>(R.id.recycler_add_location)
                recyclerAddLocation.layoutManager = LinearLayoutManager(this@MainAddLocationActivity)
                recyclerAddLocation.adapter = adapter
                adapter.notifyDataSetChanged()

            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {

            }
        })
    }

    companion object {
        private const val TAG = "MainAddLocationActivity"
    }
}