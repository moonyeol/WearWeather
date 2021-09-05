package wear.weather.view

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import wear.weather.R
import wear.weather.view.activity.MainActivity


class SplashActivity  : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        window.decorView.apply {
            systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_FULLSCREEN
        }
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        if(FirebaseAuth.getInstance().currentUser != null)
        Handler().postDelayed({
            if(FirebaseAuth.getInstance().currentUser == null){
                startActivity(Intent(this, IntroduceActivity::class.java))
            }else{
                startActivity(Intent(this, MainActivity::class.java))
            }
            finish()},3000)


    }

}