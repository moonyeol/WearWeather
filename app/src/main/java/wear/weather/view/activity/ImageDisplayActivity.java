package wear.weather.view.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import wear.weather.R;
import wear.weather.databinding.ActivityImageDisplayBinding;
import wear.weather.test.PhotoTestActivity;

public class ImageDisplayActivity extends AppCompatActivity {
    private static final String TAG = "ImageDisplayActivity";
    private ActivityImageDisplayBinding binding;

    float cont = 1f;
    float bright = 0f;
    float sat = 1f;
    static String editStatus = "";
    private Context mContext;
    private Bitmap bitmap;


    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityImageDisplayBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mContext = this;

        initToolbar();
        setImageDisplay();
        filePathToBitmap();
        initButton();
    }

    private void initToolbar() {
        binding.imgToolbar.setNavigationIcon(R.drawable.ic_back);
        binding.imgToolbar.setNavigationOnClickListener(v -> finish());
    }

    private void initButton() {
        binding.ibCancel.setOnClickListener(v -> displayMode());
        binding.ibSaveChanged.setOnClickListener(v -> {
                    saveChangedImage();
                    displayMode();
                }
        );
        binding.btnNext.setOnClickListener(v -> {
            Glide.with(this).clear(binding.imageDisplay);
//            byte[] imgByteArr = bitmapToByteArr();
//            Log.d(TAG, "initButton: "+imgByteArr.length);
            Intent intent = new Intent(mContext, PhotoTestActivity.class);
//            intent.putExtra("bitmap", imgByteArr);
            startActivity(intent);

        });

        binding.optionNavigation.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {

                case R.id.action_adjustment:
                    editMode("adjustment");
                    binding.tvEditName.setText("자르기");
                    editStatus = "cut";

                    break;
                case R.id.action_control:
                    editMode("control");
                    binding.tvEditName.setText("밝기");
                    editStatus = "brightness";
                    break;

                case R.id.action_brightness:
                    editStatus = "brightness";
                    binding.tvEditName.setText("밝기");
                    binding.seekBarBrightness.setVisibility(View.VISIBLE);
                    binding.seekBarContrast.setVisibility(View.GONE);
                    binding.seekBarSaturation.setVisibility(View.GONE);
                    break;
                case R.id.action_contrast:
                    editStatus = "contrast";
                    binding.tvEditName.setText("대조");
                    binding.seekBarBrightness.setVisibility(View.GONE);
                    binding.seekBarContrast.setVisibility(View.VISIBLE);
                    binding.seekBarSaturation.setVisibility(View.GONE);
                    break;
                case R.id.action_saturation:
                    editStatus = "saturation";
                    binding.tvEditName.setText("채도");
                    binding.seekBarBrightness.setVisibility(View.GONE);
                    binding.seekBarContrast.setVisibility(View.GONE);
                    binding.seekBarSaturation.setVisibility(View.VISIBLE);
                    break;
                case R.id.action_cut:
                    editStatus = "cut";
                    binding.tvEditName.setText("자르기");
                    setCropImageView();
                    break;
                case R.id.action_rotate:
                    editStatus = "rotate";
                    binding.tvEditName.setText("회전");
                    dismissCropImageView();
                    rotImage();
                    setImageDisplay(bitmap);
                    break;
                case R.id.action_reverse:
                    editStatus = "reverse";
                    binding.tvEditName.setText("반전");
                    dismissCropImageView();
                    setReverse();
                    setImageDisplay(bitmap);
                    break;

            }
            return true;
        });
        //Start respective activities when option is chosen from bottom navigation view

        binding.seekBarBrightness.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                bright = ((255f / 50f) * i) - 255f;
                binding.imageDisplay.setImageBitmap(changeBitmapContrastBrightness(cont, bright, sat));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        binding.seekBarContrast.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                cont = i * (0.1f);
                binding.imageDisplay.setImageBitmap(changeBitmapContrastBrightness(cont, bright, sat));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        binding.seekBarSaturation.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                sat = (float) i / 256f;
                binding.imageDisplay.setImageBitmap(changeBitmapContrastBrightness(cont, bright, sat));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

    }

    private byte[] bitmapToByteArr() {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] bytes = stream.toByteArray();
        return bytes;
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

    private void setImageDisplay() {
        Glide.with(this).load(MainActivity.currentPhotoPath).into(binding.imageDisplay);
    }

    private void setImageDisplay(Bitmap bitmap) {
        Glide.with(this).load(bitmap).into(binding.imageDisplay);
    }

    private void filePathToBitmap() {
        Glide.with(this).asBitmap().load(MainActivity.currentPhotoPath).diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).listener(new RequestListener<Bitmap>() {
            @Override
            public boolean onLoadFailed(@Nullable @org.jetbrains.annotations.Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                bitmap = resource;
                return false;
            }
        }).submit();
    }

    //Function to save image
    private void saveImage() throws Exception {
        FileOutputStream fOut = null;

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "PNG_" + timeStamp + "_";
        File file2 = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File file = File.createTempFile(imageFileName, ".png", file2);

        try {
            fOut = new FileOutputStream(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
//        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
        try {
            fOut.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            fOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            MediaStore.Images.Media.insertImage(getContentResolver(), file.getAbsolutePath(), file.getName(), file.getName());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri cUri = Uri.fromFile(file);
        mediaScanIntent.setData(cUri);
        this.sendBroadcast(mediaScanIntent);
        Toast.makeText(getApplicationContext(), "Image Saved to Pictures", Toast.LENGTH_SHORT).show();
    }

    //rotate image if it is incorrectly oriented
    private void rotImage() {
        Matrix matrix = new Matrix();
        // 회전 각도 세팅
        matrix.postRotate(90);
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    private Bitmap changeBitmapContrastBrightness(float contrast, float brightness, float saturation) {
        ColorMatrix cm = new ColorMatrix(new float[]
                {
                        contrast, 0, 0, 0, brightness,
                        0, contrast, 0, 0, brightness,
                        0, 0, contrast, 0, brightness,
                        0, 0, 0, 1, 0
                });

        Bitmap ret = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), bitmap.getConfig());

        Canvas canvas = new Canvas(ret);

        Paint paint = new Paint();
        paint.setColorFilter(new ColorMatrixColorFilter(cm));
        canvas.drawBitmap(bitmap, 0, 0, paint);
        cm.setSaturation(saturation);
        paint.setColorFilter(new ColorMatrixColorFilter(cm));
        canvas.drawBitmap(ret, 0, 0, paint);
        bitmap = ret;
        return ret;
    }

    private void saveChangedImage() {
        if (editStatus.equals("cut")) {
            bitmap = binding.cropImageView.getCroppedImage();
        } else {
            bitmap = ((BitmapDrawable) binding.imageDisplay.getDrawable()).getBitmap();
        }
        editStatus = "";
        setImageDisplay(bitmap);
        Toast.makeText(getApplicationContext(), "변경 내용 저장", Toast.LENGTH_SHORT).show();
    }

    private void setCropImageView() {
        binding.cropImageView.setImageBitmap(bitmap);
        binding.cropImageView.setAspectRatio(3, 4);
        binding.cropImageView.setFixedAspectRatio(true);
        binding.cropImageView.setGuidelines(CropImageView.Guidelines.ON);
        binding.cropImageView.setVisibility(View.VISIBLE);
    }

    private void dismissCropImageView() {
        binding.cropImageView.setVisibility(View.GONE);
    }

    private void displayMode() {
        binding.optionNavigation.getMenu().clear();
        binding.optionNavigation.inflateMenu(R.menu.option_img_nav_menu);
        binding.ibCancel.setVisibility(View.GONE);
        binding.ibSaveChanged.setVisibility(View.GONE);
        binding.tvEditName.setVisibility(View.GONE);
        binding.btnNext.setVisibility(View.VISIBLE);
        binding.tvImgEdit.setVisibility(View.VISIBLE);
        binding.imgToolbar.setNavigationIcon(R.drawable.ic_back);
        binding.cropImageView.setVisibility(View.GONE);
        binding.seekBarBrightness.setVisibility(View.GONE);
        binding.seekBarContrast.setVisibility(View.GONE);
        binding.seekBarSaturation.setVisibility(View.GONE);
        binding.seekBarRotate.setVisibility(View.GONE);
    }

    private void editMode(String type) {
        if (type.equals("adjustment")) {
            editStatus = "cut";
            binding.optionNavigation.getMenu().clear();
            binding.optionNavigation.inflateMenu(R.menu.option_img_nav_adjustment);
            binding.ibCancel.setVisibility(View.VISIBLE);
            binding.ibSaveChanged.setVisibility(View.VISIBLE);
            binding.tvEditName.setVisibility(View.VISIBLE);
            binding.btnNext.setVisibility(View.GONE);
            binding.tvImgEdit.setVisibility(View.GONE);
            binding.imgToolbar.setNavigationIcon(null);
            setCropImageView();
        } else {
            editStatus = "brightness";
            binding.optionNavigation.getMenu().clear();
            binding.optionNavigation.inflateMenu(R.menu.option_img_nav_control);
            binding.ibCancel.setVisibility(View.VISIBLE);
            binding.ibSaveChanged.setVisibility(View.VISIBLE);
            binding.tvEditName.setVisibility(View.VISIBLE);
            binding.btnNext.setVisibility(View.GONE);
            binding.tvImgEdit.setVisibility(View.GONE);
            binding.imgToolbar.setNavigationIcon(null);
            binding.seekBarBrightness.setVisibility(View.VISIBLE);
            binding.cropImageView.setVisibility(View.GONE);
        }
    }

    private void setReverse() {
        Matrix sideInversion = new Matrix();
//        sideInversion.setScale(1, -1);  // 상하반전
        sideInversion.setScale(-1, 1);  // 좌우반전
        bitmap = Bitmap.createBitmap(bitmap, 0, 0,
                bitmap.getWidth(), bitmap.getHeight(), sideInversion, false);
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


}

