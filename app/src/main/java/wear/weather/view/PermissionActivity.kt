package wear.weather.view

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import wear.weather.R


class PermissionActivity  : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_permission)
        val signInButton: Button = findViewById(R.id.permission_check_button)


        signInButton.setOnClickListener{
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.SET_ALARM,Manifest.permission.CAMERA,Manifest.permission.ACCESS_BACKGROUND_LOCATION),1)
            val intent = Intent(this, BoardInputActivity::class.java)
            startActivity(intent)
        }



    }

}