package com.malikendsley.quipswap;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.malikendsley.firebaseutils.FirebaseHandler;
import com.malikendsley.firebaseutils.interfaces.QuipRetrieveListener;
import com.malikendsley.firebaseutils.schema.SharedQuip;

import java.util.ArrayList;
import java.util.List;

public class QuipWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new QuipRemoteViewsFactory(this.getApplicationContext(), intent);
    }
}

class QuipRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    private static final int mCount = 1;
    private final List<QuipWidgetItem> mWidgetItems = new ArrayList<>();
    private final Context mContext;
    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    FirebaseHandler mdb = new FirebaseHandler(mDatabase);
    //private int mAppWidgetId;

    public QuipRemoteViewsFactory(Context context, Intent intent) {
        mContext = context;
        //mAppWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
    }

    @Override
    public void onCreate() {


        //Google says this apparently gives the empty view time to load, might increase if things get hairy
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDataSetChanged() {
        Log.i("Own", "Dataset Changed for Widget");
    }

    @Override
    public void onDestroy() {
        mWidgetItems.clear();
    }

    @Override
    public int getCount() {
        return mCount;
    }

    //Google claims you can do heavy lifting in this function

    @Override
    public RemoteViews getViewAt(int i) {
        RemoteViews rv = new RemoteViews(mContext.getPackageName(), R.layout.quip_widget);

        mdb.retrieveReceivedQuips(new QuipRetrieveListener() {
            @Override
            public void onRetrieveComplete(ArrayList<SharedQuip> quips) {
                SharedQuip soonest = null;
                for (SharedQuip quip : quips) {
                    if (soonest == null) {
                        soonest = quip;
                    } else {
                        if (Long.parseLong(quip.Timestamp) > Long.parseLong(quip.Timestamp)) {
                            soonest = quip;
                        }
                    }
                }
                //case where there are no retrieved quips
                if (soonest == null) {
                    Log.i("Own", "No quips to this user for widget");
                    mWidgetItems.add(new QuipWidgetItem(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.ic_app_logo)));
                    rv.setImageViewBitmap(R.id.quipWidgetImage, mWidgetItems.get(i).bitmap);
                    return;
                }
                StorageReference httpsReference = FirebaseStorage.getInstance().getReferenceFromUrl(soonest.URI);
                final long FIVE_MEGABYTES = 1024 * 1024 * 5;

                httpsReference.getBytes(FIVE_MEGABYTES).addOnSuccessListener(bytes -> mWidgetItems.add(new QuipWidgetItem(BitmapFactory.decodeByteArray(bytes, 0, bytes.length)))).addOnFailureListener(e -> {
                    //Log.i(TAG, "URL Download Failed");
                    e.printStackTrace();
                    mWidgetItems.add(new QuipWidgetItem(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.ic_app_logo)));
                    rv.setImageViewBitmap(R.id.quipWidgetImage, mWidgetItems.get(i).bitmap);
                });
            }

            @Override
            public void onRetrieveFail(Exception e) {
                e.printStackTrace();
                mWidgetItems.add(new QuipWidgetItem(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.ic_app_logo)));
                rv.setImageViewBitmap(R.id.quipWidgetImage, mWidgetItems.get(i).bitmap);
            }
        });

        try {
            System.out.println("Loading view");
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return rv;
    }

    @Override
    public RemoteViews getLoadingView() {
        //if loading return this view
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
