package wear.weather.main.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import wear.weather.R
import wear.weather.databinding.ActivityMainBinding
import wear.weather.main.adapter.MainPagerAdapter
import wear.weather.main.model.CurDustData
import wear.weather.retrofit.RetrofitClient
import wear.weather.util.OPEN_AIR_CUR_DUST_URL
import wear.weather.util.OPEN_WEATHER_HOURLY_WEEKLY_URL
import wear.weather.util.OPEN_WEATHER_KEY
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewPagerAdapter: MainPagerAdapter

    // 서버에서 받아온 후 정렬
    private val spinnerItems = mutableListOf("서울, 서교동", "부산", "제주", "위치 추가")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.activity = this
        val spinnerAdapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, spinnerItems)
        binding.mainSpinner.adapter = spinnerAdapter
        setSpinnerEvent()
        viewPagerAdapter = MainPagerAdapter(supportFragmentManager)

        binding.mainTabLayout.setupWithViewPager(binding.mainViewPager, true)
        permissionCheck()

        val location = getCurrentLocation()
        val coder = Geocoder(this, Locale.getDefault())
        val list = coder.getFromLocation(location.latitude, location.longitude, 1)
        getCurDust(list[0].subLocality)

    }

    private fun setSpinnerEvent() {
        binding.mainSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {

                when (position) {
                    // 위치
                    0 -> {

                    }
                    1 -> {

                    }
                    2 -> {

                    }
                    3 -> {
                        val intent = Intent(applicationContext, MainAddLocationActivity::class.java)
//                        intent.putExtra("lat",lat)
//                        intent.putExtra("lot",lot)
                        startActivity(intent)
                        Log.d(TAG, "onItemSelected: ??")
                    }
                    else -> {
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun getCurrentLocation(): Location {
        val locationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER) as Location
    }

    private fun getCurDust(stationName: String) {
        val res: Call<JsonObject> = RetrofitClient
            .getInstance()
            .buildRetrofit(OPEN_AIR_CUR_DUST_URL)
            .getCurDust(stationName)
        res.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                val body = response.body()!!
                val items = body["response"].asJsonObject["body"].asJsonObject["items"].asJsonArray[0].asJsonObject

                no2Value = items["no2Value"].asDouble
                pm2_5Value = items["pm25Value"].asInt
                pm10Value = items["pm10Value"].asInt

                no2Grade = items["no2Grade"].asInt
                pm2_5Grade = items["pm25Grade"].asInt
                pm10Grade = items["pm10Grade"].asInt

                // 추후 수정 필요
                // 미세먼지 데이터를 넣기 위함인데
                // 스레드 관리로 하자
                // 어질어질,,,
                binding.mainViewPager.adapter = viewPagerAdapter
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                Log.d(TAG, "MainFailure: $stationName")
                t.printStackTrace()
            }
        })
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            1000 -> if (grantResults.isNotEmpty()
                && grantResults[0] == PackageManager.PERMISSION_GRANTED
            ) {
                Toast.makeText(this, "승인이 허가되어 있습니다.", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "아직 승인받지 않았습니다.", Toast.LENGTH_LONG).show();
            }
        }

    }

    private fun permissionCheck() {
        val permission =
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        if (permission != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "권한 승인이 필요합니다", Toast.LENGTH_LONG).show()
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {
                Toast.makeText(this, "현재 날씨를 위해 위치 권한이 필요합니다.", Toast.LENGTH_LONG).show()
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    GPS_CHECK
                );
                Toast.makeText(this, "000부분 사용을 위해 카메라 권한이 필요합니다.", Toast.LENGTH_LONG).show();
            }
        }
    }


    companion object {
        private const val TAG = "MainActivity"
        private const val GPS_CHECK = 1000
        var no2Value: Double = 0.0
        var pm2_5Value: Int = 0
        var pm10Value: Int = 0

        var no2Grade: Int = 0
        var pm2_5Grade: Int = -1
        var pm10Grade: Int = 0
    }
}