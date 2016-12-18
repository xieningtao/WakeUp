package com.sf.banbanle.alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.sf.banbanle.bean.PushTaskBean;
import com.sf.banbanle.bean.TaskBean;
import com.sf.banbanle.bean.UserInfoBean;
import com.sf.banbanle.config.BBLConstant;

import java.util.Calendar;

import static android.content.Context.ALARM_SERVICE;

/**
 * Created by NetEase on 2016/12/5 0005.
 */

public class BBLAlarmManager {
    private static final String BBL_ALARM_ACTION="com.sf.banbanle.alarm.message";
    private static BBLAlarmManager manager = new BBLAlarmManager();
    public static final String ALARM_ID = "alarm_id";

    private BBLAlarmManager() {

    }

    public static BBLAlarmManager getManager() {
        return manager;
    }

    public void createAlarm(Context context, Calendar calendar, String alarmId, TaskBean taskBean, UserInfoBean userInfoBean,boolean repeat) {
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra(ALARM_ID, alarmId);
        intent.putExtra(BBLConstant.TASK_BEAN,taskBean);
        intent.putExtra(BBLConstant.USER_INFO_BEAN,userInfoBean);
        intent.setAction(BBL_ALARM_ACTION);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, 0);
        AlarmManager am = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pi);//设置闹钟
        if (repeat) {
            am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), (10 * 1000), pi);
        } else {
            am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pi);
        }
    }

    public void createAlarm(Context context, Calendar calendar, String alarmId, PushTaskBean taskBean, boolean repeat) {
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra(ALARM_ID, alarmId);
        intent.putExtra(BBLConstant.PUSH_TASK_BEAN,taskBean);
        intent.setAction(BBL_ALARM_ACTION);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, 0);
        AlarmManager am = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pi);//设置闹钟
        if (repeat) {
            am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), (10 * 1000), pi);
        } else {
            am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pi);
        }
    }

    public void destroyAlarm(Context context, String alarmId) {
        Intent intent = new Intent(context,
                AlarmReceiver.class);
        intent.putExtra(ALARM_ID, alarmId);
        PendingIntent sender = PendingIntent.getBroadcast(
                context, 0, intent, 0);
        // And cancel the alarm.
        AlarmManager am = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        am.cancel(sender);
    }
}
