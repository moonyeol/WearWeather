package wear.weather.post.ui

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import wear.weather.R
import wear.weather.databinding.ActivityUploadBinding

class UploadActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUploadBinding

    private lateinit var bytes: ByteArray
    private lateinit var bitmap: Bitmap
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_upload)
        binding.activity = this

        bytes = intent.getByteArrayExtra("bm")!!
        bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        binding.ivPostImg.setImageBitmap(bitmap)

    }
}