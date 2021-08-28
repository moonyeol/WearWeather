package wear.weather.test

import android.graphics.Bitmap
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import wear.weather.databinding.ActivityPhotoTestBinding

class PhotoTestActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPhotoTestBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPhotoTestBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val intent = intent
//        val bm = intent.getByteArrayExtra("bitmap")
        val bm = intent.getParcelableExtra<Bitmap>("bitmap")
        Glide.with(this).load(bm).into(binding.ivTest)
    }

    companion object {
        private const val TAG = "PhotoTestActivity"
    }
}