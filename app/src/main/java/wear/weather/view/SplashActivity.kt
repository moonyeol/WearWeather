package wear.weather.view

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import wear.weather.R


class SplashActivity  : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        window.decorView.apply {

            systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_FULLSCREEN
        }
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        Handler().postDelayed({
            startActivity(Intent(this, IntroduceActivity::class.java))
            finish()},3000)


    }

}