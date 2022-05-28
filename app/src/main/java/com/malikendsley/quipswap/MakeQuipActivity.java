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
import android.util.Log;
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
    private static final String TAG = "Own";
    PaintView paintView;

    //colored buttons
    ImageButton redButton;
    ImageButton orangeButton;
    ImageButton yellowButton;
    ImageButton greenButton;
    ImageButton blueButton;
    ImageButton purpleButton;
    private SimpleDateFormat dateFormatter;

    public static File commonDocumentDirPath(String FolderName) {
        File dir;
        dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/" + FolderName);
        Log.i(TAG, "Full path: " + dir);

        if (!dir.exists()) {
            boolean success = dir.mkdirs();
            if (!success) {
                Log.i(TAG, "mkdirs() Failed");
                dir = null;
            }
        }
        return dir;
    }

    @SuppressLint("NonConstantResourceId")
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
        buttonGroup.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            switch (checkedId) {
                case R.id.normalButton:
                    paintView.normal();
                    break;
                case R.id.blurButton:
                    paintView.blur();
                    break;
                case R.id.eraserButton:
                    paintView.setCurrentColor(getResources().getColor(R.color.PureWhite));
                    sizeSlider.setValue(100);
            }
        });

        clearButton.setOnClickListener(view -> paintView.clear());

        redButton.setOnClickListener(view -> {
            paintView.setCurrentColor(getResources().getColor(R.color.Red));
            clearAllSelected();
            redButton.setSelected(true);
        });
        orangeButton.setOnClickListener(view -> {
            paintView.setCurrentColor(getResources().getColor(R.color.Orange));
            clearAllSelected();
            orangeButton.setSelected(true);
        });
        yellowButton.setOnClickListener(view -> {
            paintView.setCurrentColor(getResources().getColor(R.color.Yellow));
            clearAllSelected();
            yellowButton.setSelected(true);
        });
        greenButton.setOnClickListener(view -> {
            paintView.setCurrentColor(getResources().getColor(R.color.Green));
            clearAllSelected();
            greenButton.setSelected(true);
        });
        blueButton.setOnClickListener(view -> {
            paintView.setCurrentColor(getResources().getColor(R.color.Blue));
            clearAllSelected();
            blueButton.setSelected(true);
        });
        purpleButton.setOnClickListener(view -> {
            paintView.setCurrentColor(getResources().getColor(R.color.purple_500));
            clearAllSelected();
            purpleButton.setSelected(true);
        });

        shareButton.setOnClickListener(view -> {
            if (user == null) {
                Log.i(TAG, "Null user, sign up instead");
                startActivity(new Intent(this, SignupActivity.class));
                finish();
                return;
            }
            Intent intent = new Intent(this, ShareQuipActivity.class);
            intent.putExtra("BitmapImage", getBitmap(100));
            Log.i(TAG, "Starting new activity");
            startActivity(intent);
        });

        saveButton.setOnClickListener(view -> {
            String URI = saveImageToMyQuips(100);
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
            builder.setTitle("Help").setMessage("This is where I will explain how to use the quip canvas").setCancelable(true).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    String saveImageToMyQuips(int quality) {
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
                Log.i(TAG, "Image Write Failed Folder Null");
            } else {
                fOut = new FileOutputStream(file);
                //This line writes the Bitmap, do your compression prior to here
                bm.compress(Bitmap.CompressFormat.JPEG, quality, fOut);
                fOut.flush();
                fOut.close();
                Log.e(TAG, "Image written to " + file.getAbsolutePath());
                return file.getAbsolutePath();
            }
        } catch (Exception e) {
            Log.i(TAG, "Image Write Failed Stack Trace");
            e.printStackTrace();
            return null;
        }
        return file.getAbsolutePath();
    }

    private byte[] getBitmap(int quality) {
        paintView.setDrawingCacheEnabled(true);
        Bitmap capture = Bitmap.createBitmap(paintView.getWidth(), paintView.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas captureCanvas = new Canvas(capture);
        paintView.draw(captureCanvas);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        capture.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
        return outputStream.toByteArray();
    }

}