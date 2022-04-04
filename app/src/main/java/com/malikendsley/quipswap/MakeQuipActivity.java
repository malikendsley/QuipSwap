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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_quip);

        Button shareButton = findViewById(R.id.shareButton);
        Button saveButton = findViewById(R.id.clearButton);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        paintView = findViewById(R.id.paintView);
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        paintView.init(metrics);
        dateFormatter = new SimpleDateFormat(
                DATE_FORMAT, Locale.US);


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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_paint, menu);
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