package com.malikendsley.quipswap;

import static com.malikendsley.quipswap.ShareQuipActivity.TAG;

import android.appwidget.AppWidgetManager;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.RemoteViews;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.malikendsley.firebaseutils.FirebaseHandler2;
import com.malikendsley.firebaseutils.secureinterfaces.RecentQuipListener;

public class GetQuipTask extends AsyncTask<String, Void, Bitmap> {

    final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    private final RemoteViews views;
    private final int WidgetID;
    private final AppWidgetManager WidgetManager;
    //TODO migrate complete
    FirebaseHandler2 mdb2 = new FirebaseHandler2(mDatabase);
    Bitmap defaultBitmap;

    @SuppressWarnings("deprecation")
    public GetQuipTask(RemoteViews views, int appWidgetID, AppWidgetManager appWidgetManager) {
        this.views = views;
        this.WidgetID = appWidgetID;
        this.WidgetManager = appWidgetManager;
    }

    @Override
    protected Bitmap doInBackground(String... strings) {
        Log.i(TAG, "User: " + strings[0]);
        //Sometimes this task is created before needed, detect this and ignore it
        if (strings[0].equals("WAIT")) {
            cancel(true);
            return null;
        }

        Log.i(TAG, "background task running");
        mdb2.getLatestQuip(strings[0], new RecentQuipListener() {
            @Override
            public void onRetrieved(Bitmap bitmap) {
                if (bitmap != null) {
                    Log.i(TAG, "bitmap retrieved");
                    views.setImageViewBitmap(R.id.appwidget_image, bitmap);
                    WidgetManager.updateAppWidget(WidgetID, views);
                }
            }

            @Override
            public void onFailed(Exception e) {
                e.printStackTrace();
            }
        });

        if(defaultBitmap == null){
            Log.i(TAG, "BITMAP IS NULL");
        }
        return null;
    }

//    @Override
//    protected void onPostExecute(Bitmap bitmap) {
//        if (isCancelled()) {
//            Log.i(TAG, "cancelled");
//            bitmap = null;
//        }
//        Log.i(TAG, "onPostExecute");
//        views.setImageViewBitmap(R.id.appwidget_image, bitmap);
//        WidgetManager.updateAppWidget(WidgetID, views);
//    }
}
