package com.malikendsley.quipswap;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.preference.PreferenceManager;

import java.util.Calendar;

public class AppWidgetAlarm {
    private final int ALARM_ID = 0;

    private final Context mContext;


    public AppWidgetAlarm(Context context) {
        mContext = context;
    }


    @SuppressLint("UnspecifiedImmutableFlag")
    public void startAlarm() {
        Calendar calendar = Calendar.getInstance();
        int INTERVAL_MILLIS;
        String interval = PreferenceManager.getDefaultSharedPreferences(mContext).getString("pref_update_frequency", "error");
        switch (interval) {
            case "often":
                INTERVAL_MILLIS = 60 * 1000;
                break;
            case "every10":
                INTERVAL_MILLIS = 60 * 1000 * 10;
                break;
            case "every30":
                INTERVAL_MILLIS = 60 * 1000 * 30;
                break;
            default:
                INTERVAL_MILLIS = -1;
                break;
        }
        calendar.add(Calendar.MILLISECOND, INTERVAL_MILLIS);

        Intent alarmIntent = new Intent(mContext, QuipWidget.class);
        alarmIntent.setAction(QuipWidget.ACTION_AUTO_UPDATE);
        PendingIntent pendingIntent;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            pendingIntent = PendingIntent.getBroadcast(mContext, ALARM_ID, alarmIntent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_CANCEL_CURRENT);
        } else {
            pendingIntent = PendingIntent.getBroadcast(mContext, ALARM_ID, alarmIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        }

        AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        // RTC does not wake the device up
        alarmManager.setRepeating(AlarmManager.RTC, calendar.getTimeInMillis(), INTERVAL_MILLIS, pendingIntent);
    }


    @SuppressLint("UnspecifiedImmutableFlag")
    public void stopAlarm() {
        Intent alarmIntent = new Intent(QuipWidget.ACTION_AUTO_UPDATE);
        PendingIntent pendingIntent;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            pendingIntent = PendingIntent.getBroadcast(mContext, ALARM_ID, alarmIntent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_CANCEL_CURRENT);
        } else {
            pendingIntent = PendingIntent.getBroadcast(mContext, ALARM_ID, alarmIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        }

        AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }
}
