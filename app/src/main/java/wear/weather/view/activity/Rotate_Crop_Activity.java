/*
package wear.weather.post.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import wear.weather.R;

public class Rotate_Crop_Activity extends AppCompatActivity {

    Bitmap textBit = ImageDisplayActivity.bm;
    CropImageView cropImageView;
    int rot = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rotate__crop_);

        cropImageView = (CropImageView) findViewById(R.id.cropImageView);
        cropImageView.setImageBitmap(textBit);
        cropImageView.setFixedAspectRatio(false);
        cropImageView.setGuidelines(CropImageView.Guidelines.ON);

        final ImageView cropBitmapIcon = (ImageView) findViewById(R.id.cropBitmapIcon);
        cropBitmapIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textBit = ImageDisplayActivity.bm = cropImageView.getCroppedImage();
                (ImageDisplayActivity.imageDisplay).setImageBitmap(ImageDisplayActivity.bm);
                Rect wh = cropImageView.getCropRect();
                ImageDisplayActivity.iHeight = wh.height();
                Toast.makeText(getApplicationContext(), "Crop Applied", Toast.LENGTH_SHORT).show();
            }
        });

        SeekBar rotateBar = (SeekBar) findViewById(R.id.rotateBar);
        rotateBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
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

        ImageView cancelCropIcon = (ImageView) findViewById(R.id.cancelCropIcon);
        cancelCropIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = getBaseContext().getPackageManager()
                        .getLaunchIntentForPackage(getBaseContext().getPackageName());
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            }
        });

        ImageView saveCroppedIcon = (ImageView) findViewById(R.id.saveCropIcon);
        saveCroppedIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textBit = ImageDisplayActivity.bm = cropImageView.getCroppedImage();
                try {
                    saveImage();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        CheckBox fixedAspectRatioCheck = (CheckBox) findViewById(R.id.fixedAspectRatioCheck);
        fixedAspectRatioCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    cropImageView.setFixedAspectRatio(true);
                } else {
                    cropImageView.setFixedAspectRatio(false);
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.rot_right_action, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.rotate_right:
                rot += 90;
                if (rot > 360) {
                    rot -= 360;
                }
                cropImageView.setRotatedDegrees(rot);
                return true;
            case R.id.rotate_left:
                rot += 270;
                if (rot > 360) {
                    rot -= 360;
                }
                cropImageView.setRotatedDegrees(rot);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void saveImage() throws Exception {

        (ImageDisplayActivity.imageDisplay).setImageBitmap(ImageDisplayActivity.bm);
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
        (ImageDisplayActivity.bm).compress(Bitmap.CompressFormat.PNG, 100, fOut);
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
}
*/
