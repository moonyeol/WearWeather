package wear.weather.view.activity

import android.content.Intent
import android.content.pm.PackageManager
import android.location.*
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.RotateAnimation
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import wear.weather.R
import wear.weather.databinding.ActivityMainBinding
import wear.weather.model.LocationDTO
import wear.weather.model.WeatherDTO
import wear.weather.view.fragment.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.system.exitProcess

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var mainFragment00: MainFragment00
    private lateinit var mainFragment01: MainFragment01
    private lateinit var gridFragment: GridFragment
    private lateinit var userFragment: UserFragment
    private lateinit var pickedImage: Uri
    private lateinit var fragmentTransaction: FragmentTransaction

    private var beforeRotation = 0f
    private var isLocationListOpen = false


    // share data
    private lateinit var weatherDTO: WeatherDTO
    private lateinit var locationDTO: LocationDTO

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate: ")
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)



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
                rotationButtonAnimation(beforeRotation + 180)

            } else {
                closeLocationList(locationListFragment)
                rotationButtonAnimation(beforeRotation + 180)
            }
        }
        binding.tvCurLocation.setOnClickListener {
            if (!isLocationListOpen) {
                openLocationList(locationListFragment)
                rotationButtonAnimation(beforeRotation + 180)

            } else {
                closeLocationList(locationListFragment)
                rotationButtonAnimation(beforeRotation + 180)
            }
        }
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
            beforeRotation,
            i,
            Animation.RELATIVE_TO_SELF,
            0.5f,
            Animation.RELATIVE_TO_SELF,
            0.5f
        )
            .run {
                this.duration = 200
                this.fillAfter = true
                binding.btnLocation.startAnimation(this)
                beforeRotation = i
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
        gridFragment = GridFragment()
        userFragment = UserFragment()
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
                        .replace(R.id.main_frame_layout, gridFragment).commit()
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
                        .replace(R.id.main_frame_layout, userFragment).commit()
                    return@setOnNavigationItemSelectedListener true
                }
                else -> return@setOnNavigationItemSelectedListener false
            }
        }
    }

    fun setWeatherDTO(weatherDTO: WeatherDTO) {
        this.weatherDTO = weatherDTO
    }

    fun getWeatherDTO(): WeatherDTO = this.weatherDTO

    fun setLocationDTO(locationDTO: LocationDTO) {
        this.locationDTO = locationDTO
    }

    fun getLocationDTO(): LocationDTO = this.locationDTO


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
            if (pickedImage == null) {
                Toast.makeText(this, "올바른 사진을 선택해주세요.", Toast.LENGTH_SHORT).show()
            }
            val i = Intent(this, ImageDisplayActivity::class.java)
            val height = this.window.decorView.height
            val width = this.window.decorView.width
            i.putExtra("height", height)
            i.putExtra("width", width)
            i.putExtra("uri", pickedImage)
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
        private const val RESULT_LOAD_IMAGE = 2000
        var backKeyPressedTime: Long = 0L
        var lastAPICallTime: String = ""
    }
}