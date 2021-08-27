package wear.weather.view.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.view.View;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
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

public class ImageDisplayActivity extends AppCompatActivity {
    private final static String TAG = "DEBUG_BOTTOM_NAV_UTIL";
    private ActivityImageDisplayBinding binding;

    static float vH = 0;
    static float vW = 0;
    float cont = 1f;
    float bright = 0f;
    float sat = 1f;
    static float iHeight = 0;
    static String editStatus = "";

    static BitmapFactory.Options bmOptions;
    static Bitmap bm = BitmapFactory.decodeFile(MainActivity.currentPhotoPath);
    private Context context;
    static byte[] imgBytes;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityImageDisplayBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        context = this;

        binding.imgToolbar.setNavigationIcon(R.drawable.ic_back);
        binding.imgToolbar.setNavigationOnClickListener(v -> finish());


        initializeBitmap();

        setImageDisplay();
        binding.ibCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayMode();
            }
        });
        binding.ibCancel.setOnClickListener(v -> displayMode());
        binding.ibSaveChanged.setOnClickListener(v -> {
                    saveBitmap();
                    displayMode();
                }
        );
        binding.btnNext.setOnClickListener(v -> {
            /*byte[] imgByteArr = bitmapToByteArr();
            Intent intent = new Intent(context, UploadActivity.class);
            intent.putExtra("bm", imgByteArr);
            startActivity(intent);*/
            BackRunnable intentThread = new BackRunnable();
            intentThread.setDaemon(true);
            intentThread.start();
        });

        binding.optionNavigation.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {

                case R.id.action_adjustment:
                    editMode("adjustment");
                    editStatus = "cut";
                    binding.tvEditName.setText("자르기");

                    break;
                case R.id.action_control:
                    editMode("control");
                    editStatus = "brightness";
                    binding.tvEditName.setText("밝기");
                    break;

                case R.id.action_brightness:
                    editStatus = "brightness";
                    binding.seekBarBrightness.setVisibility(View.VISIBLE);
                    binding.seekBarContrast.setVisibility(View.GONE);
                    binding.seekBarSaturation.setVisibility(View.GONE);
                    binding.tvEditName.setText("밝기");
                    break;
                case R.id.action_contrast:
                    editStatus = "contrast";
                    binding.seekBarBrightness.setVisibility(View.GONE);
                    binding.seekBarContrast.setVisibility(View.VISIBLE);
                    binding.seekBarSaturation.setVisibility(View.GONE);
                    binding.tvEditName.setText("대조");
                    break;
                case R.id.action_saturation:
                    editStatus = "saturation";
                    binding.seekBarBrightness.setVisibility(View.GONE);
                    binding.seekBarContrast.setVisibility(View.GONE);
                    binding.seekBarSaturation.setVisibility(View.VISIBLE);
                    binding.tvEditName.setText("채도");
                    break;
                case R.id.action_cut:
                    editStatus = "cut";
                    setCropImageView();
                    binding.seekBarRotate.setVisibility(View.GONE);
                    binding.tvEditName.setText("자르기");
                    break;
                case R.id.action_rotate:
                    editStatus = "rotate";
                    /*if (editStatus.equals("cut")) {
                        Log.d(TAG, "onNavigationItemSelected: rotate");
                        binding.cropImageView.setVisibility(View.VISIBLE);
                        binding.imageDisplay.setVisibility(View.GONE);
                        binding.cropImageView.setImageBitmap(bm);
                    }
                    binding.seekBarRotate.setVisibility(View.VISIBLE);*/
                    binding.tvEditName.setText("회전");
                    break;
                case R.id.action_reverse:
                    editStatus = "reverse";
                    if (editStatus.equals("cut")) {
                        binding.cropImageView.setVisibility(View.GONE);
                        binding.imageDisplay.setVisibility(View.VISIBLE);
                        binding.imageDisplay.setImageBitmap(bm);
                    }
                    binding.seekBarRotate.setVisibility(View.GONE);
                    setReverse();
                    binding.tvEditName.setText("반전");
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
        binding.seekBarRotate.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                binding.cropImageView.setRotatedDegrees(i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });




    /*    ImageView saveDisplayImage = (ImageView) findViewById(R.id.saveImageDisplay);
        saveDisplayImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    saveImage();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        ImageView cancelDisplayImage = (ImageView) findViewById(R.id.cancelImageDisplay);
        cancelDisplayImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = getBaseContext().getPackageManager().getLaunchIntentForPackage(getBaseContext().getPackageName());
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            }
        });*/


    }

    private byte[] bitmapToByteArr() {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] bytes = stream.toByteArray();
        return bytes;
    }


    private void initializeBitmap() {
        final float targetW = getIntent().getExtras().getInt("width");
        final float targetH = getIntent().getExtras().getInt("height");

        // Get the dimensions of the bitmap
        bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(MainActivity.currentPhotoPath, bmOptions);
        float photoW = bmOptions.outWidth;
        float photoH = bmOptions.outHeight;
        {
            vH = targetH * (0.89f);
            vW = (targetH * (0.89f) / (bm.getHeight())) * (bm.getWidth());
            if (vW > targetW) {
                vW = targetW;
                vH = (targetW / (bm.getWidth())) * (bm.getHeight());
            }
        }

        // Determine how much to scale down the image
        float scaleFactor = Math.min(photoW / vW, photoH / vH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = (int) scaleFactor;
        bm = rotImage(BitmapFactory.decodeFile(MainActivity.currentPhotoPath, bmOptions));
        bmOptions.inJustDecodeBounds = true;
        iHeight = bmOptions.outHeight;
    }

    private void setImageDisplay() {
        Glide.with(this).load(bm).into(binding.imageDisplay);
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
        bm.compress(Bitmap.CompressFormat.PNG, 100, fOut);
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
    private Bitmap rotImage(Bitmap bitmap) {
        try {
            ExifInterface exif = new ExifInterface(MainActivity.currentPhotoPath);
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);

            Matrix matrix = new Matrix();

            if (orientation == 3) {
                matrix.postRotate(180);
            } else if (orientation == 6) {
                matrix.postRotate(90);
            } else if (orientation == 8) {
                matrix.postRotate(270);
            }

            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            return bitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private Bitmap changeBitmapContrastBrightness(float contrast, float brightness, float saturation) {
        ColorMatrix cm = new ColorMatrix(new float[]
                {
                        contrast, 0, 0, 0, brightness,
                        0, contrast, 0, 0, brightness,
                        0, 0, contrast, 0, brightness,
                        0, 0, 0, 1, 0
                });

        Bitmap ret = Bitmap.createBitmap(bm.getWidth(), bm.getHeight(), bm.getConfig());

        Canvas canvas = new Canvas(ret);

        Paint paint = new Paint();
        paint.setColorFilter(new ColorMatrixColorFilter(cm));
        canvas.drawBitmap(bm, 0, 0, paint);
        cm.setSaturation(saturation);
        paint.setColorFilter(new ColorMatrixColorFilter(cm));
        canvas.drawBitmap(ret, 0, 0, paint);
        return ret;
    }

    private void saveBitmap() {
        if (editStatus.equals("cut")) {
            bm = binding.cropImageView.getCroppedImage();
        } else {
            bm = ((BitmapDrawable) binding.imageDisplay.getDrawable()).getBitmap();
        }
        editStatus = "";
        binding.imageDisplay.setImageBitmap(bm);
        Toast.makeText(getApplicationContext(), "변경 내용 저장", Toast.LENGTH_SHORT).show();
    }

    private void setCropImageView() {
        binding.cropImageView.setImageBitmap(bm);
        binding.cropImageView.setAspectRatio(3, 4);
        binding.cropImageView.setFixedAspectRatio(true);
        binding.cropImageView.setGuidelines(CropImageView.Guidelines.ON);
        binding.cropImageView.setVisibility(View.VISIBLE);

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
        bm = Bitmap.createBitmap(bm, 0, 0,
                bm.getWidth(), bm.getHeight(), sideInversion, false);
    }

    class BackRunnable extends Thread {
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

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            if (msg.what == 0) {
//                Intent intent = new Intent(context, UploadActivity.class);
//                intent.putExtra("bm", (byte[]) msg.obj);
//                startActivity(intent);
            }
        }
    };


}

