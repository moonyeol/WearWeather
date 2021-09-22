package wear.weather.view.activity

import android.content.Context
import android.content.Intent
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.theartofdev.edmodo.cropper.CropImageView
import wear.weather.R
import wear.weather.databinding.ActivityImageDisplayBinding
import wear.weather.test.PhotoTestActivity
import java.io.*
import java.text.SimpleDateFormat
import java.util.*

class ImageDisplayActivity : AppCompatActivity() {
    private lateinit var binding: ActivityImageDisplayBinding

    var cont = 1f
    var bright = 0f
    var sat = 1f
    var editStatus = ""

    private lateinit var mContext: Context
    private lateinit var bitmap: Bitmap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityImageDisplayBinding.inflate(layoutInflater)
        setContentView(binding.root)


        mContext = this
        initToolbar()
        setImageDisplay()
        initButton()
    }

    private fun initToolbar() {
        with(binding.imgToolbar) {
            this.setNavigationIcon(R.drawable.ic_back)
            this.setNavigationOnClickListener { finish() }
        }
    }

    private fun initButton() {
        with(binding) {
            ibCancel.setOnClickListener { displayMode() }
            ibSaveChanged.setOnClickListener {
                saveChangedImage()
                displayMode()
            }
            btnNext.setOnClickListener {
                saveBitmapInCacheDirectory()
                val contentUri = cacheDirectoryFileToUri()
                //            Intent intent = new Intent(mContext, BoardInputActivity.class);
                val intent = Intent(mContext, PhotoTestActivity::class.java)
                intent.putExtra("uri", contentUri)
                startActivity(intent)
            }
            optionNavigation.setOnNavigationItemSelectedListener { item ->
                when (item.itemId) {
                    R.id.action_adjustment -> {
                        editMode("adjustment")
                        tvEditName.text = "자르기"
                        editStatus = "cut"
                    }
                    R.id.action_control -> {
                        editMode("control")
                        tvEditName.text = "밝기"
                        editStatus = "brightness"
                    }
                    R.id.action_brightness -> {
                        editStatus = "brightness"
                        tvEditName.text = "밝기"
                        seekBarBrightness.visibility = View.VISIBLE
                        seekBarContrast.visibility = View.GONE
                        seekBarSaturation.visibility = View.GONE
                    }
                    R.id.action_contrast -> {
                        editStatus = "contrast"
                        tvEditName.text = "대조"
                        seekBarBrightness.visibility = View.GONE
                        seekBarContrast.visibility = View.VISIBLE
                        seekBarSaturation.visibility = View.GONE
                    }
                    R.id.action_saturation -> {
                        editStatus = "saturation"
                        tvEditName.setText("채도")
                        seekBarBrightness.visibility = View.GONE
                        seekBarContrast.visibility = View.GONE
                        seekBarSaturation.visibility = View.VISIBLE
                    }
                    R.id.action_cut -> {
                        editStatus = "cut"
                        tvEditName.text = "자르기"
                        setCropImageView()
                    }
                    R.id.action_rotate -> {
                        editStatus = "rotate"
                        tvEditName.text = "회전"
                        dismissCropImageView()
                        rotImage()
                        setImageDisplay(bitmap)
                    }
                    R.id.action_reverse -> {
                        editStatus = "reverse"
                        tvEditName.text = "반전"
                        dismissCropImageView()
                        setReverse()
                        setImageDisplay(bitmap)
                    }
                }
                true
            }
            //Start respective activities when option is chosen from bottom navigation view
            seekBarBrightness.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                    bright = 255f / 50f * i - 255f
                    imageDisplay.setImageBitmap(
                        changeBitmapContrastBrightness(
                            cont,
                            bright,
                            sat
                        )
                    )
                }

                override fun onStartTrackingTouch(seekBar: SeekBar) {}
                override fun onStopTrackingTouch(seekBar: SeekBar) {}
            })
            binding.seekBarContrast.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                    cont = i * 0.1f
                    imageDisplay.setImageBitmap(
                        changeBitmapContrastBrightness(
                            cont,
                            bright,
                            sat
                        )
                    )
                }

                override fun onStartTrackingTouch(seekBar: SeekBar) {}
                override fun onStopTrackingTouch(seekBar: SeekBar) {}
            })
            binding.seekBarSaturation.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                    sat = i.toFloat() / 256f
                    imageDisplay.setImageBitmap(
                        changeBitmapContrastBrightness(
                            cont,
                            bright,
                            sat
                        )
                    )
                }

                override fun onStartTrackingTouch(seekBar: SeekBar) {}
                override fun onStopTrackingTouch(seekBar: SeekBar) {}
            })
        }

    }

    private fun saveBitmapInCacheDirectory() {
        try {
            val cachePath: File = File(this.getCacheDir(), "images")
            cachePath.mkdirs() // don't forget to make the directory
            val stream =
                FileOutputStream("$cachePath/image.png") // overwrites this image every time
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            stream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun cacheDirectoryFileToUri(): Uri {
        val imagePath: File = File(this.getCacheDir(), "images")
        val newFile = File(imagePath, "image.png")
        //        Log.d(TAG, "cacheDirectoryFileToUri: " + uri.toString());
        return FileProvider.getUriForFile(this, "wear.weather.fileprovider", newFile)
    }

    private fun bitmapToByteArr(): ByteArray? {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        return stream.toByteArray()
    }


    /* private void initializeBitmap() {
        final float targetW = getIntent().getExtras().getInt("width");
        final float targetH = getIntent().getExtras().getInt("height");

        // Get the dimensions of the bitmap
        bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(MainActivity.currentPhotoPath, bitmapOptions);
        float photoW = bitmapOptions.outWidth;
        float photoH = bitmapOptions.outHeight;
        {
            vH = targetH * (0.89f);
            vW = (targetH * (0.89f) / (bitmap.getHeight())) * (bitmap.getWidth());
            if (vW > targetW) {
                vW = targetW;
                vH = (targetW / (bitmap.getWidth())) * (bitmap.getHeight());
            }
        }

        // Determine how much to scale down the image
        float scaleFactor = Math.min(photoW / vW, photoH / vH);

        // Decode the image file into a Bitmap sized to fill the View
        bitmapOptions.inJustDecodeBounds = false;
        bitmapOptions.inSampleSize = (int) scaleFactor;
        bitmap = rotImage(BitmapFactory.decodeFile(MainActivity.currentPhotoPath, bitmapOptions));
        bitmapOptions.inJustDecodeBounds = true;
        iHeight = bitmapOptions.outHeight;
    }*/

    /* private void initializeBitmap() {
        final float targetW = getIntent().getExtras().getInt("width");
        final float targetH = getIntent().getExtras().getInt("height");

        // Get the dimensions of the bitmap
        bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(MainActivity.currentPhotoPath, bitmapOptions);
        float photoW = bitmapOptions.outWidth;
        float photoH = bitmapOptions.outHeight;
        {
            vH = targetH * (0.89f);
            vW = (targetH * (0.89f) / (bitmap.getHeight())) * (bitmap.getWidth());
            if (vW > targetW) {
                vW = targetW;
                vH = (targetW / (bitmap.getWidth())) * (bitmap.getHeight());
            }
        }

        // Determine how much to scale down the image
        float scaleFactor = Math.min(photoW / vW, photoH / vH);

        // Decode the image file into a Bitmap sized to fill the View
        bitmapOptions.inJustDecodeBounds = false;
        bitmapOptions.inSampleSize = (int) scaleFactor;
        bitmap = rotImage(BitmapFactory.decodeFile(MainActivity.currentPhotoPath, bitmapOptions));
        bitmapOptions.inJustDecodeBounds = true;
        iHeight = bitmapOptions.outHeight;
    }*/
    private fun setImageDisplay() {
        val uri: Uri? = intent.getParcelableExtra<Uri>("uri")
        Log.d(TAG, "uriuri: $uri")
        //        Glide.with(this).load(uri).into(binding.imageDisplay);
        Glide.with(this).asBitmap().load(uri).into(object : SimpleTarget<Bitmap?>() {
            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap?>?) {
                bitmap = resource
                binding.imageDisplay.setImageBitmap(resource)
            }
        })
    }

    private fun setImageDisplay(bitmap: Bitmap?) {
        Glide.with(this).load(bitmap).into(binding.imageDisplay)
    }


    //Function to save image
    @Throws(Exception::class)
    private fun saveImage() {
        var fOut: FileOutputStream? = null
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "PNG_" + timeStamp + "_"
        val file2: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val file = File.createTempFile(imageFileName, ".png", file2)
        try {
            fOut = FileOutputStream(file)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        //        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
        try {
            fOut!!.flush()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        try {
            fOut!!.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        try {
            MediaStore.Images.Media.insertImage(
                getContentResolver(),
                file.absolutePath,
                file.name,
                file.name
            )
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
        val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
        val cUri = Uri.fromFile(file)
        mediaScanIntent.data = cUri
        this.sendBroadcast(mediaScanIntent)
        Toast.makeText(getApplicationContext(), "Image Saved to Pictures", Toast.LENGTH_SHORT)
            .show()
    }

    //rotate image if it is incorrectly oriented
    private fun rotImage() {
        val matrix = Matrix()
        // 회전 각도 세팅
        matrix.postRotate(90f)
        bitmap = Bitmap.createBitmap(bitmap!!, 0, 0, bitmap!!.width, bitmap!!.height, matrix, true)
    }

    private fun changeBitmapContrastBrightness(
        contrast: Float,
        brightness: Float,
        saturation: Float
    ): Bitmap? {
        val cm = ColorMatrix(
            floatArrayOf(
                contrast,
                0f,
                0f,
                0f,
                brightness,
                0f,
                contrast,
                0f,
                0f,
                brightness,
                0f,
                0f,
                contrast,
                0f,
                brightness,
                0f,
                0f,
                0f,
                1f,
                0f
            )
        )
        val ret = Bitmap.createBitmap(bitmap!!.width, bitmap!!.height, bitmap!!.config)
        val canvas = Canvas(ret)
        val paint = Paint()
        paint.colorFilter = ColorMatrixColorFilter(cm)
        canvas.drawBitmap(bitmap!!, 0f, 0f, paint)
        cm.setSaturation(saturation)
        paint.colorFilter = ColorMatrixColorFilter(cm)
        canvas.drawBitmap(ret, 0f, 0f, paint)
        bitmap = ret
        return ret
    }

    private fun saveChangedImage() {
        bitmap = if (editStatus == "cut") {
            binding.cropImageView.getCroppedImage()
        } else {
            (binding.imageDisplay.getDrawable() as BitmapDrawable).bitmap
        }
        editStatus = ""
        setImageDisplay(bitmap)
        Toast.makeText(getApplicationContext(), "변경 내용 저장", Toast.LENGTH_SHORT).show()
    }

    private fun setCropImageView() {
        binding.cropImageView.setImageBitmap(bitmap)
        binding.cropImageView.setAspectRatio(3, 4)
        binding.cropImageView.setFixedAspectRatio(true)
        binding.cropImageView.setGuidelines(CropImageView.Guidelines.ON)
        binding.cropImageView.setVisibility(View.VISIBLE)
    }

    private fun dismissCropImageView() {
        binding.cropImageView.setVisibility(View.GONE)
    }

    private fun displayMode() {
        binding.optionNavigation.getMenu().clear()
        binding.optionNavigation.inflateMenu(R.menu.option_img_nav_menu)
        binding.ibCancel.setVisibility(View.GONE)
        binding.ibSaveChanged.setVisibility(View.GONE)
        binding.tvEditName.setVisibility(View.GONE)
        binding.btnNext.setVisibility(View.VISIBLE)
        binding.tvImgEdit.setVisibility(View.VISIBLE)
        binding.imgToolbar.setNavigationIcon(R.drawable.ic_back)
        binding.cropImageView.setVisibility(View.GONE)
        binding.seekBarBrightness.setVisibility(View.GONE)
        binding.seekBarContrast.setVisibility(View.GONE)
        binding.seekBarSaturation.setVisibility(View.GONE)
        binding.seekBarRotate.setVisibility(View.GONE)
    }

    private fun editMode(type: String) {
        if (type == "adjustment") {
            editStatus = "cut"
            binding.optionNavigation.getMenu().clear()
            binding.optionNavigation.inflateMenu(R.menu.option_img_nav_adjustment)
            binding.ibCancel.setVisibility(View.VISIBLE)
            binding.ibSaveChanged.setVisibility(View.VISIBLE)
            binding.tvEditName.setVisibility(View.VISIBLE)
            binding.btnNext.setVisibility(View.GONE)
            binding.tvImgEdit.setVisibility(View.GONE)
            binding.imgToolbar.setNavigationIcon(null)
            setCropImageView()
        } else {
            editStatus = "brightness"
            binding.optionNavigation.getMenu().clear()
            binding.optionNavigation.inflateMenu(R.menu.option_img_nav_control)
            binding.ibCancel.setVisibility(View.VISIBLE)
            binding.ibSaveChanged.setVisibility(View.VISIBLE)
            binding.tvEditName.setVisibility(View.VISIBLE)
            binding.btnNext.setVisibility(View.GONE)
            binding.tvImgEdit.setVisibility(View.GONE)
            binding.imgToolbar.setNavigationIcon(null)
            binding.seekBarBrightness.setVisibility(View.VISIBLE)
            binding.cropImageView.setVisibility(View.GONE)
        }
    }

    private fun setReverse() {
        val sideInversion = Matrix()
        //        sideInversion.setScale(1, -1);  // 상하반전
        sideInversion.setScale(-1f, 1f) // 좌우반전
        bitmap = Bitmap.createBitmap(
            bitmap!!, 0, 0,
            bitmap!!.width, bitmap!!.height, sideInversion, false
        )
    }

    /*class BackRunnable extends Thread {
        @Override
        public void run() {
            imgBytes = bitmapToByteArr();
            Message msg = new Message();
            msg.what = 0;
            msg.obj = imgBytes;
            mHandler.sendMessage(msg);
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
*/
/*
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            if (msg.what == 0) {
                Intent intent = new Intent(context, PhotoTestActivity.class);
                intent.putExtra("bitmap", (byte[]) msg.obj);
                startActivity(intent);
            }
        }
    };
*/

    companion object {
        private const val TAG = "ImageDisplayActivityTmp"
    }

}