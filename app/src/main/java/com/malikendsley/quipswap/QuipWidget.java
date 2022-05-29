package com.malikendsley.quipswap;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

/**
 * Implementation of App Widget functionality.
 * App Widget Configuration implemented in {@link QuipWidgetConfigureActivity QuipWidgetConfigureActivity}
 */
public class QuipWidget extends AppWidgetProvider {


    public static final String ACTION_AUTO_UPDATE = "AUTO_UPDATE";
    final static String TAG = "Own";

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        Log.i(TAG, "updateAppWidget called");
        //load the UID that this widget should pull from
        String friendUID = QuipWidgetConfigureActivity.loadFriendUIDPref(context, appWidgetId);
        Log.i(TAG, "Loaded " + friendUID + " from sharedPrefs");
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.quip_widget);

        //begin the task that updates a widget
        new GetQuipTask(views, appWidgetId, appWidgetManager).execute(friendUID);

        appWidgetManager.updateAppWidget(appWidgetId, views);

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        if (intent.getAction().equals(ACTION_AUTO_UPDATE)) {
            // DO SOMETHING
            Log.i(TAG, "QuipWidget: Auto Update Detected");
            Toast.makeText(context, "Auto Update...", Toast.LENGTH_SHORT).show();
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            onUpdate(context, appWidgetManager, appWidgetManager.getAppWidgetIds(new ComponentName(context, QuipWidget.class)));

        }
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
    public void onDisabled(Context context) {
        super.onDisabled(context);
        // stop alarm only if all widgets have been disabled
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        ComponentName thisAppWidgetComponentName = new ComponentName(context.getPackageName(), getClass().getName());
        int[] appWidgetIdsAlarm = appWidgetManager.getAppWidgetIds(thisAppWidgetComponentName);
        if (appWidgetIdsAlarm.length == 0) {
            // stop alarm
            AppWidgetAlarm appWidgetAlarm = new AppWidgetAlarm(context.getApplicationContext());
            appWidgetAlarm.stopAlarm();
            //Log.i(TAG, "QuipWidget: Alarm Stopped, onDeleted()");
            Toast.makeText(context, "Alarm Killed...", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
        AppWidgetAlarm appWidgetAlarm = new AppWidgetAlarm(context.getApplicationContext());
        //Log.i(TAG, "QuipWidget: Alarm Starting");
        Toast.makeText(context, "Alarm Starting", Toast.LENGTH_SHORT).show();
        appWidgetAlarm.startAlarm();
    }
}