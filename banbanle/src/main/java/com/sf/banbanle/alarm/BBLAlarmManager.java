package com.sf.banbanle.alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;

import static android.content.Context.ALARM_SERVICE;

/**
 * Created by NetEase on 2016/12/5 0005.
 */

public class BBLAlarmManager {

    public void createAlarm(Context context, Calendar calendar, boolean repeat) {
        Intent intent = new Intent(context, AlarmReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, 0);
        AlarmManager am = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pi);//设置闹钟
        if (repeat) {
            am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), (10 * 1000), pi);
        } else {
            am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pi);
        }
    }

    public void destroyAlarm(Context context){
        Intent intent = new Intent(context,
                AlarmReceiver.class);
        PendingIntent sender = PendingIntent.getBroadcast(
                context, 0, intent, 0);
        // And cancel the alarm.
        AlarmManager am = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        am.cancel(sender);
    }
}
