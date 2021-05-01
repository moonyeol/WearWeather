package wear.weather.main.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import wear.weather.R
import wear.weather.databinding.ActivityMainBinding
import wear.weather.retrofit.RetrofitClient
import wear.weather.upload.ui.ImageDisplayActivity
import wear.weather.util.OPEN_AIR_CUR_DUST_URL
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var mainFragment00: MainFragment00
    private lateinit var mainFragment01: MainFragment01
    private lateinit var location: Location
    private lateinit var pickedImage: Uri


    private var isFineLocationPermissionChecked = true


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

        checkPermissions()

        if (isFineLocationPermissionChecked) {
            Log.d(TAG, "isFineLocationPermissionChecked: $isFineLocationPermissionChecked")
            location = getCurrentLocation()
            setCurrentLocation(location)
            val stationName = getStation()
            getCurDust(stationName)
        }
        setBottomNav()
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

    private fun checkPermissions() {

        val permissionListener = object : PermissionListener {
            override fun onPermissionGranted() {
                isFineLocationPermissionChecked = false
            }

            override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {
                deniedPermissions?.forEach {
                    when (it) {
                        Manifest.permission.ACCESS_FINE_LOCATION -> {
                            isFineLocationPermissionChecked = false
                        }
                    }
                }
            }
        }
        TedPermission.with(this)
            .setPermissionListener(permissionListener)
            .setRationaleMessage(" 이 앱을 사용하기 위해서는 접근 권한이 필요합니다")
            .setDeniedMessage("[설정] -> [권한] 들어가세요")
            .setPermissions(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
            .check()
    }

    private fun getStation(): String {
        val coder = Geocoder(this@MainActivity, Locale.getDefault())
        val list = coder.getFromLocation(lat, lot, 1)
        Log.d(TAG, "getStation: $lat $lot")
        return list[0].subLocality
    }

    @SuppressLint("MissingPermission")
    private fun getCurrentLocation(): Location {
        val locationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val a = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER) as Location
        Log.d(TAG, "getCurrentLocation: ${a.longitude}")
        return locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER) as Location
    }

    private fun setCurrentLocation(location: Location) {
        lat = location.latitude
        lot = location.longitude
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

    companion object {
        lateinit var currentPhotoPath: String
        private const val TAG = "MainActivity"
        private const val GPS_CHECK = 1000
        private const val RESULT_LOAD_IMAGE = 2000
        var no2Value: Double = 0.0
        var pm2_5Value: Int = 0
        var pm10Value: Int = 0
        var no2Grade: Int = 0
        var pm2_5Grade: String = "error"
        var pm10Grade: String = "error"
        var lat: Double = 0.0
        var lot: Double = 0.0

    }
}