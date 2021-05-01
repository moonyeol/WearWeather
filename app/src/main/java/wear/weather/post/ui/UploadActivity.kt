package wear.weather.post.ui

import android.graphics.Bitmap
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class UploadActivity : AppCompatActivity() {
    private lateinit var bitmap: Bitmap
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        bitmap = intent.getParcelableExtra<Bitmap>("bm")!!
    }
}