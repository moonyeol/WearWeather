package wear.weather.upload.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
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
    private final static String TAG = "DEBUG_BOTTOM_NAV_UTIL";
    static PhotoView imageDisplay;
    static Toolbar imgToolbar;
    static ImageButton ibCancel;
    static ImageButton ibSaveChanged;
    static Button btnNext;
    static TextView tvImgEdit;
    static TextView tvEditName;
    static float iHeight = 0;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View decorView = getWindow().getDecorView();
        // Hide the status bar.
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        setContentView(R.layout.activity_image__display);
        imgToolbar = findViewById(R.id.img_toolbar);
        imageDisplay = findViewById(R.id.imageDisplay);
        ibCancel = findViewById(R.id.ib_cancel);
        ibSaveChanged = findViewById(R.id.ib_save_changed);
        btnNext = findViewById(R.id.btn_next);
        tvImgEdit = findViewById(R.id.tv_img_edit);
        tvEditName = findViewById(R.id.tv_edit_name);


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


        final BottomNavigationView optionNavigationView = (BottomNavigationView) findViewById(R.id.optionNavigation);
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
                optionNavigationView.getMenu().clear();
                optionNavigationView.inflateMenu(R.menu.option_img_nav_menu);
                ibCancel.setVisibility(View.GONE);
                ibSaveChanged.setVisibility(View.GONE);
                tvEditName.setVisibility(View.GONE);
                btnNext.setVisibility(View.VISIBLE);
                tvImgEdit.setVisibility(View.VISIBLE);
                imgToolbar.setNavigationIcon(R.drawable.ic_back);

            }
        });

        ibSaveChanged.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    saveImage();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    saveImage();
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
                    /*case R.id.action_addText:
                        Intent addTextIntent = new Intent(.this, Add_Text_Activity.class);
                        addTextIntent.putExtra("height", targetH);
                        addTextIntent.putExtra("width", targetW);
                        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(ImageDisplayActivity.this, new Pair<View, String>(findViewById(R.id.imageDisplay), (getString(R.string.transition_image))));
                        ActivityCompat.startActivity(ImageDisplayActivity.this, addTextIntent, options.toBundle());
                        break;*/
                    /*case R.id.action_draw:
                        Intent drawIntent = new Intent(ImageDisplayActivity.this, Draw_Activity.class);
                        drawIntent.putExtra("height", targetH);
                        drawIntent.putExtra("width", targetW);
                        ActivityOptionsCompat optionsDraw = ActivityOptionsCompat.makeSceneTransitionAnimation(ImageDisplayActivity.this, new Pair<View, String>(findViewById(R.id.imageDisplay), (getString(R.string.transition_image))));
                        ActivityCompat.startActivity(ImageDisplayActivity.this, drawIntent, optionsDraw.toBundle());
                        break;*/
                    /*case R.id.action_addEmoji:
                        Intent emojiIntent = new Intent(ImageDisplayActivity.this, Emoji_Activity.class);
                        emojiIntent.putExtra("height", targetH);
                        emojiIntent.putExtra("width", targetW);
                        ActivityOptionsCompat optionsEmoji = ActivityOptionsCompat.makeSceneTransitionAnimation(ImageDisplayActivity.this, new Pair<View, String>(findViewById(R.id.imageDisplay), (getString(R.string.transition_image))));
                        ActivityCompat.startActivity(ImageDisplayActivity.this, emojiIntent, optionsEmoji.toBundle());
                        break;*/
                 /*   case R.id.action_rotateCrop:
                        Intent rotCropIntent = new Intent(ImageDisplayActivity.this, Rotate_Crop_Activity.class);
                        rotCropIntent.putExtra("height", targetH);
                        rotCropIntent.putExtra("width", targetW);
                        ActivityOptionsCompat rotCropOptions = ActivityOptionsCompat.makeSceneTransitionAnimation(ImageDisplayActivity.this, new Pair<View, String>(findViewById(R.id.imageDisplay), (getString(R.string.transition_image))));
                        ActivityCompat.startActivity(ImageDisplayActivity.this, rotCropIntent, rotCropOptions.toBundle());
                        break;
                    case R.id.action_tune:
                        Intent tuneIntent = new Intent(ImageDisplayActivity.this, Tune_Activity.class);
                        tuneIntent.putExtra("height", targetH);
                        tuneIntent.putExtra("width", targetW);
                        tuneIntent.putExtra("iHeight", iHeight);
                        ActivityOptionsCompat tuneOptions = ActivityOptionsCompat.makeSceneTransitionAnimation(ImageDisplayActivity.this, new Pair<View, String>(findViewById(R.id.imageDisplay), (getString(R.string.transition_image))));
                        ActivityCompat.startActivity(ImageDisplayActivity.this, tuneIntent, tuneOptions.toBundle());
                        break;*/
                    case R.id.action_adjustment:
                        optionNavigationView.getMenu().clear();
                        optionNavigationView.inflateMenu(R.menu.option_img_nav_adjustment);
                        ibCancel.setVisibility(View.VISIBLE);
                        ibSaveChanged.setVisibility(View.VISIBLE);
                        tvEditName.setVisibility(View.VISIBLE);
                        btnNext.setVisibility(View.GONE);
                        tvImgEdit.setVisibility(View.GONE);
                        imgToolbar.setNavigationIcon(null);
                        break;
                    case R.id.action_control:
                        optionNavigationView.getMenu().clear();
                        optionNavigationView.inflateMenu(R.menu.option_img_nav_control);
                        ibCancel.setVisibility(View.VISIBLE);
                        ibSaveChanged.setVisibility(View.VISIBLE);
                        tvEditName.setVisibility(View.VISIBLE);
                        btnNext.setVisibility(View.GONE);
                        tvImgEdit.setVisibility(View.GONE);
                        imgToolbar.setNavigationIcon(null);
                        break;

                    case R.id.action_brightness:

                        break;
                    case R.id.action_saturation:
                        break;
                    case R.id.action_contrast:
                        break;
                    case R.id.action_cut:
                        break;
                    case R.id.action_rotate:
                        break;
                    case R.id.action_reverse:
                        break;

                }
                return true;
            }
        });

        ImageView saveDisplayImage = (ImageView) findViewById(R.id.saveImageDisplay);
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
        });


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

}

