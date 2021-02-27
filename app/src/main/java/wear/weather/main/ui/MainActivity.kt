package wear.weather.main.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.databinding.DataBindingUtil
import androidx.viewpager.widget.ViewPager
import wear.weather.R
import wear.weather.databinding.ActivityMainBinding
import wear.weather.main.adapter.MainPagerAdapter

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewPagerAdapter: MainPagerAdapter

    // 서버에서 받아온 후 정렬
    private val spinnerItems = mutableListOf("서울, 서교동", "부산", "제주")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.activity = this
        val spinnerAdapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, spinnerItems)
        binding.mainSpinner.adapter = spinnerAdapter
        setSpinnerEvent()
        viewPagerAdapter = MainPagerAdapter(supportFragmentManager)
        binding.mainViewPager.adapter = viewPagerAdapter
        binding.mainTabLayout.setupWithViewPager(binding.mainViewPager,true)
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
                    else -> {

                    }
                }

            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }
    }
}