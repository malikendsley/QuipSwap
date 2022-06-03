package com.malikendsley.quipswap;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.slider.Slider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.malikendsley.fingerpainting.PaintView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MakeQuipActivity extends AppCompatActivity {

    public static final String DATE_FORMAT = "yyyyMMdd_HHmmss";
    //private static final String TAG = "Own";
    PaintView paintView;

    //colored buttons
    ImageButton redButton;
    ImageButton orangeButton;
    ImageButton yellowButton;
    ImageButton greenButton;
    ImageButton blueButton;
    ImageButton purpleButton;
    int lastColor = 1;
    int currentBrush = 1;
    private SimpleDateFormat dateFormatter;

    public static File commonDocumentDirPath(String FolderName) {
        File dir;
        dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/" + FolderName);
        //Log.i(TAG, "Full path: " + dir);

        if (!dir.exists()) {
            boolean success = dir.mkdirs();
            if (!success) {
                //Log.i(TAG, "mkdirs() Failed");
                dir = null;
            }
        }
        return dir;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_quip);


        //ui buttons
        Button shareButton = findViewById(R.id.shareButton);
        Button saveButton = findViewById(R.id.saveButton);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        paintView = findViewById(R.id.paintView);
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        paintView.init(metrics);
        dateFormatter = new SimpleDateFormat(DATE_FORMAT, Locale.US);

        //colored buttons
        redButton = findViewById(R.id.redButton);
        orangeButton = findViewById(R.id.orangeButton);
        yellowButton = findViewById(R.id.yellowButton);
        greenButton = findViewById(R.id.greenButton);
        blueButton = findViewById(R.id.blueButton);
        purpleButton = findViewById(R.id.purpleButton);
        //1 = red, 6 = purple, etc

        //default to red
        redButton.setSelected(true);

        //slider
        Slider sizeSlider = findViewById(R.id.penSizeSlider);
        sizeSlider.addOnChangeListener((slider, value, fromUser) -> paintView.setStrokeWidth(value));

        //clear button
        Button clearButton = findViewById(R.id.canvasClearButton);

        //brush group
        MaterialButtonToggleGroup buttonGroup = findViewById(R.id.brushStyleToggleGroup);

        //undo and redo buttons
        Button undoButton = findViewById(R.id.undoButton);
        Button redoButton = findViewById(R.id.redoButton);

        //button functionality
        undoButton.setOnClickListener(view -> paintView.onClickUndo());
        redoButton.setOnClickListener(view -> paintView.onClickRedo());

        //brush style
        buttonGroup.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                if (checkedId == R.id.normalButton) {
                    paintView.normal();
                    //Log.i(TAG, "Normal pressed, last color: " + lastColor);
                    paintView.setCurrentColor(getResources().getColor(resolveColor(lastColor)));
                    paintView.setStrokeWidth(sizeSlider.getValue());
                    currentBrush = 1;
                } else if (checkedId == R.id.blurButton) {
                    paintView.blur();
                    paintView.setCurrentColor(getResources().getColor(resolveColor(lastColor)));
                    paintView.setStrokeWidth(sizeSlider.getValue());
                    currentBrush = 2;
                } else if (checkedId == R.id.eraserButton) {
                    paintView.setCurrentColor(getResources().getColor(R.color.PureWhite));
                    paintView.setStrokeWidth(100);
                    currentBrush = 3;
                }
            }
        });

        //clear button
        clearButton.setOnClickListener(view -> paintView.clear());

        //color buttons
        redButton.setOnClickListener(view -> {

            if (currentBrush != 3) {
                paintView.setCurrentColor(getResources().getColor(R.color.Red));
            }
            clearAllSelected();
            redButton.setSelected(true);
            lastColor = 1;
        });
        orangeButton.setOnClickListener(view -> {
            if (currentBrush != 3) {
                paintView.setCurrentColor(getResources().getColor(R.color.Orange));
                clearAllSelected();
            }
            orangeButton.setSelected(true);
            lastColor = 2;

        });
        yellowButton.setOnClickListener(view -> {
            if (currentBrush != 3) {
                paintView.setCurrentColor(getResources().getColor(R.color.Yellow));
            }
            clearAllSelected();
            yellowButton.setSelected(true);
            lastColor = 3;
        });
        greenButton.setOnClickListener(view -> {
            if (currentBrush != 3) {
                paintView.setCurrentColor(getResources().getColor(R.color.Green));
            }
            clearAllSelected();
            greenButton.setSelected(true);
            lastColor = 4;
        });
        blueButton.setOnClickListener(view -> {
            if (currentBrush != 3) {
                paintView.setCurrentColor(getResources().getColor(R.color.Blue));
            }
            clearAllSelected();
            blueButton.setSelected(true);
            lastColor = 5;
        });
        purpleButton.setOnClickListener(view -> {
            if (currentBrush != 3) {
                paintView.setCurrentColor(getResources().getColor(R.color.purple_500));
            }
            clearAllSelected();
            purpleButton.setSelected(true);
            lastColor = 6;
        });

        //share and save buttons
        shareButton.setOnClickListener(view -> {
            if (user == null) {
                //Log.i(TAG, "Null user, sign up instead");
                startActivity(new Intent(this, SignupActivity.class));
                finish();
                return;
            }
            Intent intent = new Intent(this, ShareQuipActivity.class);
            intent.putExtra("BitmapImage", getBitmap());
            //Log.i(TAG, "Starting new activity");
            startActivity(intent);
        });

        saveButton.setOnClickListener(view -> {
            String URI = saveImageToMyQuips();
            Toast.makeText(MakeQuipActivity.this, "Image Saved to " + URI, Toast.LENGTH_SHORT).show();
        });
    }


    void clearAllSelected() {
        redButton.setSelected(false);
        orangeButton.setSelected(false);
        yellowButton.setSelected(false);
        greenButton.setSelected(false);
        blueButton.setSelected(false);
        purpleButton.setSelected(false);
    }

    //given 1 through 6 return the right color
    int resolveColor(int colorCode) {
        switch (colorCode) {
            case 1:
                return R.color.Red;
            case 2:
                return R.color.Orange;
            case 3:
                return R.color.Yellow;
            case 4:
                return R.color.Green;
            case 5:
                return R.color.Blue;
            case 6:
                return R.color.purple_500;
            default:
                return -1;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_paint, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.makeQuipHelpMenuButton) {
            //unlikely but if this presents a perf issue can pre-build
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Help").setMessage("In order to use the canvas, draw on the top white portion of the screen.\n\n" +
                    "To select a new color, tap the colored boxes.\n\nThe Hard, Soft, and Erase buttons affect the brush type.\n\n" +
                    "Undo and redo can be used to fix small mistakes." +
                    "\n\nThe clear canvas button clears the canvas back to white.").setCancelable(true).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    String saveImageToMyQuips() {
        paintView.setDrawingCacheEnabled(true);
        Bitmap bm = Bitmap.createBitmap(paintView.getDrawingCache());
        paintView.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        paintView.buildDrawingCache(true);
        OutputStream fOut;
        File folder = commonDocumentDirPath("MyQuips");
        File file = new File(folder, "sent_" + dateFormatter.format(new Date()) + ".jpg");
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE}, 1);
            }
            if (folder == null) {
                //Log.i(TAG, "Image Write Failed Folder Null");
            } else {
                fOut = new FileOutputStream(file);
                //This line writes the Bitmap, do your compression prior to here
                bm.compress(Bitmap.CompressFormat.JPEG, PreferenceManager.getDefaultSharedPreferences(this).getInt("pref_quip_quality", 100), fOut);
                fOut.flush();
                fOut.close();
                //Log.e(TAG, "Image written to " + file.getAbsolutePath());
                return file.getAbsolutePath();
            }
        } catch (Exception e) {
            //Log.i(TAG, "Image Write Failed Stack Trace");
            //e.printStackTrace();
            return null;
        }
        return file.getAbsolutePath();
    }

    private byte[] getBitmap() {
        paintView.setDrawingCacheEnabled(true);
        Bitmap capture = Bitmap.createBitmap(paintView.getWidth(), paintView.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas captureCanvas = new Canvas(capture);
        paintView.draw(captureCanvas);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        capture.compress(Bitmap.CompressFormat.JPEG, PreferenceManager.getDefaultSharedPreferences(this).getInt("pref_quip_quality", 100), outputStream);
        return outputStream.toByteArray();
    }

}