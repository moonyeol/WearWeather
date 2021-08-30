package wear.weather.view

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import wear.weather.databinding.ActivityPermissionBinding
import wear.weather.view.activity.MainActivity


class PermissionActivity  : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityPermissionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val signInButton: Button = binding.permissionCheckButton

        signInButton.setOnClickListener{
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.SET_ALARM,Manifest.permission.CAMERA,Manifest.permission.ACCESS_BACKGROUND_LOCATION),1)
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }



    }

}