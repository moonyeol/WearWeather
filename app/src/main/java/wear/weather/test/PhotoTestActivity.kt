package wear.weather.test

import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import wear.weather.databinding.ActivityPhotoTestBinding
import wear.weather.util.wwLog

class PhotoTestActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPhotoTestBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPhotoTestBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val intent = intent
//        val bm = intent.getByteArrayExtra("bitmap")
//        val bm = intent.getParcelableExtra<Bitmap>("bitmap")
        val uri = intent.getParcelableExtra<Uri>("uri")
        wwLog(TAG,"uri: ${uri.toString()}")
        Glide.with(this).load(uri).into(binding.ivTest)
    }

    companion object {
        private const val TAG = "PhotoTestActivity"
    }
}