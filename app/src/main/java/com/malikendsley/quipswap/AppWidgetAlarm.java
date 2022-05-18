package com.malikendsley.quipswap;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;

public class AppWidgetAlarm {
    private final int ALARM_ID = 0;

    private final Context mContext;


    public AppWidgetAlarm(Context context) {
        mContext = context;
    }


    public void startAlarm() {
        Calendar calendar = Calendar.getInstance();
        int INTERVAL_MILLIS = 60000;
        calendar.add(Calendar.MILLISECOND, INTERVAL_MILLIS);

        Intent alarmIntent = new Intent(mContext, QuipWidget.class);
        alarmIntent.setAction(QuipWidget.ACTION_AUTO_UPDATE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, ALARM_ID, alarmIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        // RTC does not wake the device up
        alarmManager.setRepeating(AlarmManager.RTC, calendar.getTimeInMillis(), INTERVAL_MILLIS, pendingIntent);
    }


    public void stopAlarm() {
        Intent alarmIntent = new Intent(QuipWidget.ACTION_AUTO_UPDATE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, ALARM_ID, alarmIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }
}
