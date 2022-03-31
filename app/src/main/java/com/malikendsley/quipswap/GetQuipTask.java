package com.malikendsley.quipswap;

import android.appwidget.AppWidgetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.RemoteViews;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.malikendsley.firebaseutils.FirebaseHandler;

import java.io.InputStream;

public class GetQuipTask extends AsyncTask<String, Void, Bitmap> {

    final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    private final RemoteViews views;
    private final int WidgetID;
    private final AppWidgetManager WidgetManager;
    FirebaseHandler mdb = new FirebaseHandler(mDatabase);
    //TODO Remove test URL
    String testURL = "https://firebasestorage.googleapis.com/v0/b/quipswap.appspot.com/o/download.jpg?alt=media&token=d81ec89f-0613-485c-b26e-ef723726b5cb";

    public GetQuipTask(RemoteViews views, int appWidgetID, AppWidgetManager appWidgetManager) {
        this.views = views;
        this.WidgetID = appWidgetID;
        this.WidgetManager = appWidgetManager;
    }

    @Override
    protected Bitmap doInBackground(String... strings) {
        //TODO: do the database / FirebaseHandler work to retrieve the appropriate quip and convert it to a bitmap
        try {
            InputStream in = new java.net.URL(testURL).openStream();
            Bitmap bitmap = BitmapFactory.decodeStream(in);
            Log.v("ImageDownload", "download succeeded");
            Log.v("ImageDownload", "Param 0 is: " + strings[0]);
            return bitmap;
            //NOTE:  it is not thread-safe to set the ImageView from inside this method.  It must be done in onPostExecute()
        } catch (Exception e) {
            Log.e("ImageDownload", "Download failed: " + e.getMessage());
        }
        return null;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if (isCancelled()) {
            bitmap = null;
        }

        views.setImageViewBitmap(R.id.appwidget_image, bitmap);
        WidgetManager.updateAppWidget(WidgetID, views);
    }
}
