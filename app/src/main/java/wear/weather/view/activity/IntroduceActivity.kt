package wear.weather.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.viewpager.widget.ViewPager
import wear.weather.R
import wear.weather.adapter.ViewPagerAdapter
import wear.weather.databinding.ActivityIntroduceBinding

class IntroduceActivity : AppCompatActivity() {
    //  viewpager
    private val adapter by lazy { ViewPagerAdapter(supportFragmentManager) }
    private lateinit var viewPager_introduce: ViewPager
    private lateinit var indicator0_iv_main: ImageView
    private lateinit var indicator1_iv_main: ImageView
    private lateinit var indicator2_iv_main: ImageView
    private lateinit var indicator3_iv_main: ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityIntroduceBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewPager_introduce = binding.viewPagerIntroduce
        indicator0_iv_main = binding.indicator0IvMain
        indicator1_iv_main = binding.indicator1IvMain
        indicator2_iv_main = binding.indicator2IvMain
        indicator3_iv_main = binding.indicator3IvMain


        val signInButton:Button = binding.introduceSignInButton

        val pagerAdapter = ViewPagerAdapter(supportFragmentManager)
        pagerAdapter.createFragment()
        viewPager_introduce.adapter = pagerAdapter

        viewPager_introduce.addOnPageChangeListener(object : ViewPager.OnPageChangeListener{
            override fun onPageScrollStateChanged(p0: Int) {
            }

            override fun onPageScrolled(p0: Int, p1: Float, p2: Int) {
            }

            override fun onPageSelected(p0: Int) {
                indicator0_iv_main.setImageDrawable(getDrawable(R.drawable.ellipse18))
                indicator1_iv_main.setImageDrawable(getDrawable(R.drawable.ellipse18))
                indicator2_iv_main.setImageDrawable(getDrawable(R.drawable.ellipse18))
                indicator3_iv_main.setImageDrawable(getDrawable(R.drawable.ellipse18))

                when(p0){
                    0 -> indicator0_iv_main.setImageDrawable(getDrawable(R.drawable.ellipse19))
                    1 -> indicator1_iv_main.setImageDrawable(getDrawable(R.drawable.ellipse19))
                    2 -> {signInButton.visibility = View.INVISIBLE
                        indicator2_iv_main.setImageDrawable(getDrawable(R.drawable.ellipse19))
                    }
                    3 -> {signInButton.visibility = View.VISIBLE
                        indicator3_iv_main.setImageDrawable(getDrawable(R.drawable.ellipse19))}
                }
            }
        })

        signInButton.setOnClickListener{

            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

    }
}