package wear.weather.post.ui;

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
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.github.chrisbanes.photoview.PhotoView;
import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomnavigation.LabelVisibilityMode;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;

import wear.weather.R;
import wear.weather.main.ui.MainActivity;

public class ImageDisplayActivity extends AppCompatActivity {

    static Bitmap bm = BitmapFactory.decodeFile(MainActivity.currentPhotoPath);
    static float vH = 0, vW = 0;
    static BitmapFactory.Options bmOptions;
    float cont = 1f;
    float bright = 0f;
    float sat = 1f;

    private final static String TAG = "DEBUG_BOTTOM_NAV_UTIL";

    private Context context;
    static PhotoView imageDisplay;
    static CropImageView cropImageView;
    static Toolbar imgToolbar;
    static ImageButton ibCancel;
    static ImageButton ibSaveChanged;
    static Button btnNext;
    static TextView tvImgEdit;
    static TextView tvEditName;
    static SeekBar seekBarBrightness;
    static SeekBar seekBarContrast;
    static SeekBar seekBarSaturation;
    static SeekBar seekBarRotate;
    static BottomNavigationView optionNavigationView;
    static float iHeight = 0;

    static String editStatus = "";


    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View decorView = getWindow().getDecorView();
        // Hide the status bar.
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        setContentView(R.layout.activity_image__display);
        context = this;
        imgToolbar = findViewById(R.id.img_toolbar);
        imageDisplay = findViewById(R.id.imageDisplay);
        cropImageView = findViewById(R.id.cropImageView);
        ibCancel = findViewById(R.id.ib_cancel);
        ibSaveChanged = findViewById(R.id.ib_save_changed);
        btnNext = findViewById(R.id.btn_next);
        tvImgEdit = findViewById(R.id.tv_img_edit);
        tvEditName = findViewById(R.id.tv_edit_name);
        seekBarBrightness = findViewById(R.id.seekBar_brightness);
        seekBarContrast = findViewById(R.id.seekBar_contrast);
        seekBarSaturation = findViewById(R.id.seekBar_saturation);
        seekBarRotate = findViewById(R.id.seekBar_rotate);

        imgToolbar.setNavigationIcon(R.drawable.ic_back);
        imgToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


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
        bmOptions.inPurgeable = true;
        bm = rotImage(BitmapFactory.decodeFile(MainActivity.currentPhotoPath, bmOptions));
        bmOptions.inJustDecodeBounds = true;
        iHeight = bmOptions.outHeight;

        //set scaled image to imageDisplay
        imageDisplay.setImageBitmap(bm);


        optionNavigationView = (BottomNavigationView) findViewById(R.id.optionNavigation);
        BottomNavigationMenuView menuView = (BottomNavigationMenuView) optionNavigationView.getChildAt(0);
        try {//Set shifting mode of bottom navigation view as false to see all titles
            Field shiftingMode = menuView.getClass().getDeclaredField("mShiftingMode");
            shiftingMode.setAccessible(true);
            shiftingMode.setBoolean(menuView, false);
            shiftingMode.setAccessible(false);
            for (int i = 0; i < menuView.getChildCount(); i++) {
                BottomNavigationItemView item = (BottomNavigationItemView) menuView.getChildAt(i);
                item.setLabelVisibilityMode(LabelVisibilityMode.LABEL_VISIBILITY_LABELED);
                // Set once again checked value, so view will be updated
                item.setChecked(item.getItemData().isChecked());
            }
        } catch (NoSuchFieldException e) {
            Log.d(TAG, "Unable to get shift mode field");
        } catch (IllegalAccessException e) {
            Log.d(TAG, "Unable to change value of shift mode");
        }
        ibCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayMode();
            }
        });

        ibSaveChanged.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (editStatus.equals("cut") || editStatus.equals("rotate") || editStatus.equals("reverse")) {
                        saveBitmap();
                        if (editStatus.equals("cut")) {
                            cropImageView.setImageBitmap(bm);
                        }
                    } else {
                        saveBitmap();
                    }
                    editStatus = "";
                    displayMode();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(context, UploadActivity.class);
                    intent.putExtra("bm",bm);
                    startActivity(intent);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        //Start respective activities when option is chosen from bottom navigation view
        optionNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()) {

                    case R.id.action_adjustment:
                        editMode("adjustment");
                        tvEditName.setText("자르기");
                        break;
                    case R.id.action_control:
                        editMode("control");
                        tvEditName.setText("밝기");
                        break;

                    case R.id.action_brightness:
                        editStatus = "brightness";
                        seekBarBrightness.setVisibility(View.VISIBLE);
                        seekBarContrast.setVisibility(View.GONE);
                        seekBarSaturation.setVisibility(View.GONE);
                        tvEditName.setText("밝기");
                        break;
                    case R.id.action_contrast:
                        editStatus = "contrast";
                        seekBarBrightness.setVisibility(View.GONE);
                        seekBarContrast.setVisibility(View.VISIBLE);
                        seekBarSaturation.setVisibility(View.GONE);
                        tvEditName.setText("대조");
                        break;
                    case R.id.action_saturation:
                        editStatus = "saturation";
                        seekBarBrightness.setVisibility(View.GONE);
                        seekBarContrast.setVisibility(View.GONE);
                        seekBarSaturation.setVisibility(View.VISIBLE);
                        tvEditName.setText("채도");
                        break;
                    case R.id.action_cut:
                        editStatus = "cut";
                        setCropImageView();
                        seekBarRotate.setVisibility(View.GONE);
                        tvEditName.setText("자르기");
                        break;
                    case R.id.action_rotate:
                        editStatus = "rotate";
                        if (editStatus.equals("cut")) {
                            cropImageView.setVisibility(View.GONE);
                            imageDisplay.setVisibility(View.VISIBLE);
                            imageDisplay.setImageBitmap(bm);
                        }
                        seekBarRotate.setVisibility(View.VISIBLE);
                        tvEditName.setText("회전");
                        break;
                    case R.id.action_reverse:
                        editStatus = "reverse";
                        if (editStatus.equals("cut")) {
                            cropImageView.setVisibility(View.GONE);
                            imageDisplay.setVisibility(View.VISIBLE);
                            imageDisplay.setImageBitmap(bm);
                        }
                        seekBarRotate.setVisibility(View.GONE);
                        setReverse();
                        tvEditName.setText("반전");
                        break;

                }
                return true;
            }
        });

        seekBarBrightness.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                bright = ((255f / 50f) * i) - 255f;
                imageDisplay.setImageBitmap(changeBitmapContrastBrightness(cont, bright, sat));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        seekBarContrast.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                cont = i * (0.1f);
                imageDisplay.setImageBitmap(changeBitmapContrastBrightness(cont, bright, sat));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        seekBarSaturation.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                sat = (float) i / 256f;
                imageDisplay.setImageBitmap(changeBitmapContrastBrightness(cont, bright, sat));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        seekBarRotate.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                cropImageView.setRotatedDegrees(i);
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
            bm = cropImageView.getCroppedImage();
        } else {
            bm = ((BitmapDrawable) imageDisplay.getDrawable()).getBitmap();
        }
        imageDisplay.setImageBitmap(bm);
        Toast.makeText(getApplicationContext(), "변경 내용 저장", Toast.LENGTH_SHORT).show();
    }

    private void setCropImageView() {
        cropImageView.setVisibility(View.VISIBLE);
        cropImageView.setImageBitmap(bm);
        cropImageView.setFixedAspectRatio(false);
        cropImageView.setGuidelines(CropImageView.Guidelines.ON);
    }

    private void displayMode() {
        optionNavigationView.getMenu().clear();
        optionNavigationView.inflateMenu(R.menu.option_img_nav_menu);
        ibCancel.setVisibility(View.GONE);
        ibSaveChanged.setVisibility(View.GONE);
        tvEditName.setVisibility(View.GONE);
        btnNext.setVisibility(View.VISIBLE);
        tvImgEdit.setVisibility(View.VISIBLE);
        imgToolbar.setNavigationIcon(R.drawable.ic_back);
        cropImageView.setVisibility(View.GONE);
        seekBarBrightness.setVisibility(View.GONE);
        seekBarContrast.setVisibility(View.GONE);
        seekBarSaturation.setVisibility(View.GONE);
        seekBarRotate.setVisibility(View.GONE);
    }

    private void editMode(String type) {
        if (type.equals("adjustment")) {
            editStatus = "cut";
            optionNavigationView.getMenu().clear();
            optionNavigationView.inflateMenu(R.menu.option_img_nav_adjustment);
            ibCancel.setVisibility(View.VISIBLE);
            ibSaveChanged.setVisibility(View.VISIBLE);
            tvEditName.setVisibility(View.VISIBLE);
            btnNext.setVisibility(View.GONE);
            tvImgEdit.setVisibility(View.GONE);
            imgToolbar.setNavigationIcon(null);
            cropImageView.setVisibility(View.VISIBLE);
            setCropImageView();
        } else {
            editStatus = "brightness";
            optionNavigationView.getMenu().clear();
            optionNavigationView.inflateMenu(R.menu.option_img_nav_control);
            ibCancel.setVisibility(View.VISIBLE);
            ibSaveChanged.setVisibility(View.VISIBLE);
            tvEditName.setVisibility(View.VISIBLE);
            btnNext.setVisibility(View.GONE);
            tvImgEdit.setVisibility(View.GONE);
            imgToolbar.setNavigationIcon(null);
            seekBarBrightness.setVisibility(View.VISIBLE);
            cropImageView.setVisibility(View.GONE);
        }
    }

    private void setReverse() {
        Matrix sideInversion = new Matrix();
//        sideInversion.setScale(1, -1);  // 상하반전
        sideInversion.setScale(-1, 1);  // 좌우반전
        bm = Bitmap.createBitmap(bm, 0, 0,
                bm.getWidth(), bm.getHeight(), sideInversion, false);
        imageDisplay.setImageBitmap(bm);
    }
}

