package com.malikendsley.quipswap;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.malikendsley.fingerpainting.PaintView;

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
    private SimpleDateFormat dateFormatter;

    public static File commonDocumentDirPath(String FolderName) {
        File dir;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Log.i(TAG, "SDK > R");
            dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/" + FolderName);
        } else {
            Log.i(TAG, "SDK < R");
            dir = new File(Environment.getExternalStorageDirectory() + "/" + FolderName);
        }

        if (!dir.exists()) {
            boolean success = dir.mkdirs();
            if (!success) {
                Log.i(TAG, "mkdirs() Failed");
                dir = null;
            }
        }
        return dir;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_quip);

        Button shareButton = findViewById(R.id.shareButton);
        Button clearButton = findViewById(R.id.clearButton);

        paintView = findViewById(R.id.paintView);
        //TODO Find out what this does exactly
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        paintView.init(metrics);
        dateFormatter = new SimpleDateFormat(
                DATE_FORMAT, Locale.US);


        shareButton.setOnClickListener(view -> {
            paintView.setDrawingCacheEnabled(true);
            Bitmap bm = Bitmap.createBitmap(paintView.getDrawingCache());
            paintView.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
            paintView.buildDrawingCache(true);
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE}, 1);
                }
                OutputStream fOut;
                File folder = commonDocumentDirPath("MyQuips");
                if (folder == null) {
                    Log.i(TAG, "Image Write Failed Folder Null");
                } else {
                    Log.i(TAG, "Folder path: " + folder);
                    File file = new File(folder, "img_" + dateFormatter.format(new Date()) + ".jpg");
                    fOut = new FileOutputStream(file);
                    //This line writes the Bitmap, do your compression prior to here
                    bm.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
                    fOut.flush();
                    fOut.close();
                    //This line also writes a bitmap but to the wrong location
                }
            } catch (Exception e) {
                Log.i(TAG, "Image Write Failed Stack Trace");
                e.printStackTrace();
            }
        });
        clearButton.setOnClickListener(view -> paintView.clear());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.paint_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.normal:
                paintView.normal();
                return true;
            case R.id.blur:
                paintView.blur();
                return true;
            case R.id.clear:
                paintView.clear();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}