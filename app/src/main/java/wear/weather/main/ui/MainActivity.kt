package wear.weather.main.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.*
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.RotateAnimation
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import jp.wasabeef.blurry.Blurry
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import wear.weather.R
import wear.weather.databinding.ActivityMainBinding
import wear.weather.databinding.FragmentLocationListBinding
import wear.weather.post.ui.ImageDisplayActivity
import wear.weather.retrofit.RetrofitClient
import wear.weather.util.OPEN_AIR_CUR_DUST_URL
import java.text.SimpleDateFormat
import java.util.*
import kotlin.system.exitProcess

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var mainFragment00: MainFragment00
    private lateinit var mainFragment01: MainFragment01
    private lateinit var pickedImage: Uri
    private lateinit var fragmentTransaction: FragmentTransaction


    private var beforeRotaion = 0f
    private var isLocationPermissionGranted = false
    private var isLocationListOpen = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate: ")

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.activity = this

        val locationListFragment = MainLocationListFragment()
        val tmpViewFragment = MainTmpFragment()
        supportFragmentManager.beginTransaction().run {
            this.add(R.id.frameLayout_tmp_view, tmpViewFragment)
            this.addToBackStack(null)
            this.commit()
        }

        binding.btnLocation.setOnClickListener {
            if (!isLocationListOpen) {
                openLocationList(locationListFragment)
                rotationButtonAnimation(beforeRotaion + 180)

            } else {
                closeLocationList(locationListFragment)
                rotationButtonAnimation(beforeRotaion + 180)
            }
        }
        binding.tvCurLocation.setOnClickListener {
            if (!isLocationListOpen) {
                openLocationList(locationListFragment)
                rotationButtonAnimation(beforeRotaion + 180)

            } else {
                closeLocationList(locationListFragment)
                rotationButtonAnimation(beforeRotaion + 180)
            }
        }

        setCurrentLocation()
        lastAPICallTime = getLastAPICallTime()
        Log.d(TAG, "호출 시간: $lastAPICallTime")
        setBottomNav()

    }

    private fun openLocationList(locationListFragment: Fragment) {
        fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.setCustomAnimations(
            R.anim.list_up_to_down,
            R.anim.list_down_to_up
        )
        fragmentTransaction.add(R.id.frameLayout_location_list, locationListFragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
        binding.frameLayoutTmpView.visibility = View.VISIBLE
        isLocationListOpen = true
        binding.btnLocation.startAnimation(
            AnimationUtils.loadAnimation(
                applicationContext, R.anim.rotate_anim
            )
        )
    }

    private fun closeLocationList(locationListFragment: Fragment) {
        fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.setCustomAnimations(
            R.anim.list_up_to_down,
            R.anim.list_down_to_up
        )
        fragmentTransaction.remove(locationListFragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
        binding.frameLayoutTmpView.visibility = View.GONE
        isLocationListOpen = false
        binding.btnLocation.startAnimation(
            AnimationUtils.loadAnimation(
                applicationContext, R.anim.rotate_anim
            )
        )
    }

    private fun rotationButtonAnimation(i: Float) {
        RotateAnimation(
            beforeRotaion,
            i,
            Animation.RELATIVE_TO_SELF,
            0.5f,
            Animation.RELATIVE_TO_SELF,
            0.5f
        )
            .run {
                this.duration = 250
                this.fillAfter = true
                binding.btnLocation.startAnimation(this)
                beforeRotaion = i
            }
    }

    private fun getLastAPICallTime(): String {
        val now = System.currentTimeMillis()
        val mDate = Date(now)
        val simpleDate = SimpleDateFormat("yyyyMMddhh")
        return simpleDate.format(mDate)
    }

    private fun setBottomNav() {

        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        mainFragment00 = MainFragment00()
        fragmentTransaction.replace(R.id.main_frame_layout, mainFragment00)
            .commitAllowingStateLoss()

        binding.mainBottomNav.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.menu_first -> {
                    fragmentManager.beginTransaction()
                        .replace(R.id.main_frame_layout, mainFragment00).commit()
                    return@setOnNavigationItemSelectedListener true
                }

                R.id.menu_second -> {
                    mainFragment01 = MainFragment01()
                    fragmentManager.beginTransaction()
                        .replace(R.id.main_frame_layout, mainFragment01).commit()
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.menu_third
                -> {
                    currentPhotoPath = String()
                    val intent = Intent(
                        Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    )
                    startActivityForResult(intent, RESULT_LOAD_IMAGE)
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.menu_fourth
                -> {
                    fragmentManager.beginTransaction()
                        .replace(R.id.main_frame_layout, mainFragment01).commit()
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.menu_fifth
                -> {
                    fragmentManager.beginTransaction()
                        .replace(R.id.main_frame_layout, mainFragment00).commit()
                    return@setOnNavigationItemSelectedListener true
                }
                else -> return@setOnNavigationItemSelectedListener false
            }
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart: ")
    }

    override fun onResume() {
        super.onResume()

    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause: ")
    }

    override fun onStop() {
        super.onStop()

    }

    private fun getStation(lat: Double, lot: Double): String {
        val coder = Geocoder(this@MainActivity, Locale.getDefault())
        val list = coder.getFromLocation(lat, lot, 10)
        Log.d(TAG, "getStation: $lat $lot")
        Log.d(TAG, "getStation: ${list.size}")
        return list[0].subLocality
    }


    private fun setCurrentLocation() {

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {

            val mLocationRequest: LocationRequest = LocationRequest.create()
            mLocationRequest.interval = 60000
            mLocationRequest.fastestInterval = 5000
            mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            val mLocationCallback = object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult?) {
                    if (locationResult == null) {
                        Log.d(TAG, "onLocationResult is null")
                        return
                    }
                    for (location in locationResult.locations) {
                        if (location != null) {
                            lat = location.latitude
                            lot = location.longitude
                            val stationName = getStation(location.latitude, location.longitude)
                            getCurDust(stationName)
                            Log.d(TAG, "onLocationResult: lat: $lat lot:$lot")
                        }
                    }
                }
            }
            LocationServices.getFusedLocationProviderClient(this)
                .requestLocationUpdates(mLocationRequest, mLocationCallback, null)
            LocationServices.getFusedLocationProviderClient(this).lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    lat = location.latitude
                    lot = location.longitude
                    val stationName = getStation(location.latitude, location.longitude)
                    getCurDust(stationName)
                    Log.d(TAG, "setCurrentLocation: lat: $lat lot: $lot")
                }
            }
        }


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
                mainFragment00.setTvCurFineDustGrade()
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                Log.d(TAG, "DustFailure: $stationName")
                Log.d(TAG, "DustFailure: ${t.message}")
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK) {
            pickedImage = data!!.data!!
            val filePath = arrayOf(MediaStore.Images.Media.DATA)
            val cursor = contentResolver.query(pickedImage, filePath, null, null, null)
            cursor!!.moveToFirst()

            currentPhotoPath =
                cursor.getString(cursor.getColumnIndex(filePath[0]))
            val i = Intent(this, ImageDisplayActivity::class.java)
            val height = this.window.decorView.height
            val width = this.window.decorView.width
            i.putExtra("height", height)
            i.putExtra("width", width)
            startActivity(i)
        }
    }

    override fun onBackPressed() {
        if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
            backKeyPressedTime = System.currentTimeMillis();
            Toast.makeText(this, "정말 종료하시겠습니까", Toast.LENGTH_SHORT).show();
        }
        //2번째 백버튼 클릭 (종료)
        else {
            appFinish()
        }

    }

    private fun appFinish() {
        finish()
        exitProcess(0)
    }

    companion object {
        lateinit var currentPhotoPath: String
        private const val TAG = "MainActivity"
        private const val GPS_CHECK = 1000
        private const val RESULT_LOAD_IMAGE = 2000
        var backKeyPressedTime: Long = 0L
        var lat: Double = 0.0
        var lot: Double = 0.0
        var no2Value: Double = 0.0
        var pm2_5Value: Int = 0
        var pm10Value: Int = 0
        var no2Grade: Int = 0
        var pm2_5Grade: String = "error"
        var pm10Grade: String = "error"
        var lastAPICallTime: String = ""
    }
}