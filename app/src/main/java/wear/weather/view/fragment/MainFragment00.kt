package wear.weather.view.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import wear.weather.databinding.FragmentMain00Binding
import wear.weather.retrofit.RetrofitClient
import wear.weather.util.OPEN_AIR_CUR_DUST_URL
import wear.weather.util.OPEN_WEATHER_KEY
import wear.weather.util.OPEN_WEATHER_URL
import wear.weather.util.toTemp
import wear.weather.view.activity.DetailActivity
import java.util.*

class MainFragment00 : Fragment() {

    private lateinit var binding: FragmentMain00Binding
    var curLat: String = ""
    var curLot: String = ""
    var stationName: String = ""
    var pm2_5Grade: String = ""
    var no2Grade: Int = 0
    var pm10Grade: String = ""
    var no2Value: Double = 0.0
    var pm2_5Value: Int = 0
    var pm10Value: Int = 0


    private lateinit var mActivity: FragmentActivity
    private lateinit var mActivityContext: Context
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentMain00Binding.inflate(layoutInflater, container, false)


        getCurrentLocation()

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        binding.btnShowDetail.setOnClickListener {
            val intent = Intent(mActivityContext, DetailActivity::class.java)
            intent.putExtra("lat", curLat)
            intent.putExtra("lot", curLot)
            startActivity(intent)
        }

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mActivity = activity!!
        mActivityContext = mActivity.applicationContext
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
                Log.d(TAG, "onResponse: tempMax: $tempMax")
                Log.d(TAG, "onResponse: tempMin: $tempMin")
                binding.tvCurPerceivedTemp.text = toTemp(feelsLike)
                binding.tvCurTemp.text = toTemp(temp)
                binding.tvCurMaxMinTemp.text = "${toTemp(tempMax)} / ${toTemp(tempMin)}"
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                Log.d(TAG, "onFailure: ${t.message}")
            }
        })
    }

    @SuppressLint("MissingPermission")
    private fun getCurrentLocation() {
        val mLocationRequest: LocationRequest = LocationRequest.create()
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        val mLocationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                if (locationResult == null) {
                    Log.d(TAG, "onLocationResult is null")
                    return
                }
                for (location in locationResult.locations) {
                    if (location != null) {
                        curLat = location.latitude.toString()
                        curLot = location.longitude.toString()
                        getCurrentWeather(curLat, curLot)
                        stationName = getStation(location.latitude, location.longitude)
                        Log.d(TAG, "stationName: $stationName")
                        getCurDust(stationName)
                    }
                }
            }
        }
        LocationServices.getFusedLocationProviderClient(mActivityContext)
            .requestLocationUpdates(mLocationRequest, mLocationCallback, null)
//            LocationServices.getFusedLocationProviderClient(activityContext).lastLocation.addOnSuccessListener { location -> }

    }

    private fun getStation(lat: Double, lot: Double): String {
        val coder = Geocoder(mActivity.applicationContext, Locale.KOREA)
        val list = coder.getFromLocation(lat, lot, 10)
        Log.d(TAG, "getStation: $lat $lot")
        return list[0].subLocality
    }

    private fun getCurDust(stationName: String) {
        val res: Call<String> = RetrofitClient
            .getInstance()
            .buildRetrofit(OPEN_AIR_CUR_DUST_URL)
            .getCurDust(stationName)
        res.enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                val body = response.body()!!
                var items: JsonObject = JsonObject()
                val jsonObject = JsonParser().let { parser ->
                    items =
                        parser.parse(body).asJsonObject["response"].asJsonObject["body"].asJsonObject["items"].asJsonArray[0].asJsonObject
                }
                Log.d(TAG, "items: ${items["no2Value"]}")
                no2Value = items["no2Value"].asDouble
                pm2_5Value = items["pm25Value"].asInt
                pm10Value = items["pm10Value"].asInt
                no2Grade = items["no2Grade"].asInt
                pm2_5Grade = when (items["pm25Grade"].asInt) {
                    1 -> "좋음"
                    2 -> "보통"
                    3 -> "나쁨"
                    4 -> "매우나쁨"
                    else -> "메서드 호출은 됐음"
                }
                pm10Grade = when (items["pm10Grade"].asInt) {
                    1 -> "좋음"
                    2 -> "보통"
                    3 -> "나쁨"
                    4 -> "매우나쁨"
                    else -> "메서드 호출은 됐음"
                }
                binding.tvCurFineDustGrade.text = pm10Grade

            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                Log.d(TAG, "DustFailure: $stationName")
                Log.d(TAG, "DustFailure: ${t.message}")
                t.printStackTrace()
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
    }

    companion object {
        private const val TAG = "MainFragment00"
    }
}