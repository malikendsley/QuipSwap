package com.malikendsley.quipswap;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
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

public class MakeQuipActivity extends AppCompatActivity {

    private static final String TAG = "Own";
    PaintView paintView;

    public static File commonDocumentDirPath(String FolderName) {
        File dir;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS) + "/" + FolderName);
        } else {
            dir = new File(Environment.getExternalStorageDirectory() + "/" + FolderName);
        }

        // Make sure the path directory exists.
        if (!dir.exists()) {
            // Make it, if it doesn't exit
            boolean success = dir.mkdirs();
            if (!success) {
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

        shareButton.setOnClickListener(view -> {
            Log.i(TAG, "Attaching Listener");
            //TODO save the image as a bitmap and pass it to the share screen
            paintView.setDrawingCacheEnabled(true);
            Bitmap bm = Bitmap.createBitmap(paintView.getDrawingCache());
            paintView.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
            // keeping this here for posterity but it randomly shifts my layout when used and doesn't break things when removed
            //paintView.layout(0, 0, paintView.getWidth(), paintView.getHeight());
            paintView.buildDrawingCache(true);
            if (bm != null) {
                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE}, 1);
                    }

                    OutputStream fOut;
                    File folder = commonDocumentDirPath("My Quips");
                    if (folder == null) {
                        Log.i(TAG, "Image Write Failed Folder Null");
                    } else {
                        File file = new File(folder, "screenTest.jpg");
                        fOut = new FileOutputStream(file);
                        bm.compress(Bitmap.CompressFormat.JPEG, 85, fOut);
                        fOut.flush();
                        fOut.close();
                        Log.e(TAG, "Image Path : " + MediaStore.Images.Media.insertImage(getContentResolver(), file.getAbsolutePath(), file.getName(), file.getName()));
                    }
                } catch (Exception e) {
                    Log.i(TAG, "Image Write Failed Stack Trace");
                    e.printStackTrace();
                }
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