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
        setContentView(R.layout.activity_introduce)
        viewPager_introduce = findViewById(R.id.viewPager_introduce)
        indicator0_iv_main = findViewById(R.id.indicator0_iv_main)
        indicator1_iv_main = findViewById(R.id.indicator1_iv_main)
        indicator2_iv_main = findViewById(R.id.indicator2_iv_main)
        indicator3_iv_main = findViewById(R.id.indicator3_iv_main)

        val signInButton:Button = findViewById(R.id.introduce_sign_in_button)

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