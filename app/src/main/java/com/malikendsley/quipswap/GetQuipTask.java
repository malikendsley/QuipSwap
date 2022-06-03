package com.malikendsley.quipswap;

import android.appwidget.AppWidgetManager;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.RemoteViews;

import com.google.firebase.database.FirebaseDatabase;
import com.malikendsley.utils.FirebaseHandler;
import com.malikendsley.utils.interfaces.RecentQuipListener;

public class GetQuipTask extends AsyncTask<String, Void, Bitmap> {

    private final RemoteViews views;
    private final int WidgetID;
    private final AppWidgetManager WidgetManager;
    FirebaseHandler mdb2 = new FirebaseHandler(FirebaseDatabase.getInstance().getReference());
    Bitmap defaultBitmap;

    @SuppressWarnings("deprecation")
    public GetQuipTask(RemoteViews views, int appWidgetID, AppWidgetManager appWidgetManager) {
        this.views = views;
        this.WidgetID = appWidgetID;
        this.WidgetManager = appWidgetManager;
    }

    @Override
    protected Bitmap doInBackground(String... strings) {
        //Log.i(TAG, "User: " + strings[0]);
        //Sometimes this task is created before needed, detect this and ignore it
        if (strings[0].equals("WAIT")) {
            cancel(true);
            return null;
        }

        //Log.i(TAG, "background task running");
        mdb2.getLatestQuip(strings[0], new RecentQuipListener() {
            @Override
            public void onRetrieved(Bitmap bitmap) {
                if (bitmap != null) {
                    //Log.i(TAG, "bitmap retrieved");
                    views.setImageViewBitmap(R.id.appwidget_image, bitmap);
                    WidgetManager.updateAppWidget(WidgetID, views);
                }
            }

            @Override
            public void onFailed(Exception e) {
                //Log.i(TAG, "GetQuipTask: Error retrieving, using standard bitmap");
                views.setImageViewBitmap(R.id.appwidget_image, defaultBitmap);
                WidgetManager.updateAppWidget(WidgetID, views);
                //e.printStackTrace();
            }
        });
        //Log.i(TAG, "GetQuipTask: Task Concluding");
        return null;
    }

}
