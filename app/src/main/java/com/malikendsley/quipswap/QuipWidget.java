package com.malikendsley.quipswap;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.RemoteViews;

/**
 * Implementation of App Widget functionality.
 * App Widget Configuration implemented in {@link QuipWidgetConfigureActivity QuipWidgetConfigureActivity}
 */
public class QuipWidget extends AppWidgetProvider {

    final static String TAG = "Own";

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        Log.i(TAG, "updateAppWidget called");
        //load the UID that this widget should pull from
        String friendUID = QuipWidgetConfigureActivity.loadFriendUIDPref(context, appWidgetId);
        Log.i(TAG, "Loaded " + friendUID + " from sharedPrefs");
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.quip_widget);

        new GetQuipTask(views, appWidgetId, appWidgetManager).execute(friendUID);

        views.setImageViewBitmap(R.id.appwidget_image, BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_app_logo));
        appWidgetManager.updateAppWidget(appWidgetId, views);

    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        // When the user deletes the widget, delete the preference associated with it.
        for (int appWidgetId : appWidgetIds) {
            QuipWidgetConfigureActivity.deleteTitlePref(context, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}