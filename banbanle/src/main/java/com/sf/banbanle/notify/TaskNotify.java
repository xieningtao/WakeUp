package com.sf.banbanle.notify;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.widget.RemoteViews;

import com.sf.banbanle.R;

/**
 * Created by mac on 16/12/18.
 */

public class TaskNotify {

    private NotificationManager mNotificaitonMan;

    public TaskNotify(Context context) {
        mNotificaitonMan = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    public Notification createBasicNotify(Context context, String title, String content) {
        Notification notify = new Notification.Builder(context)
                .setSmallIcon(R.drawable.app_icon)
                .setContentTitle(title)
                .setContentText(content)
                .setDefaults(Notification.DEFAULT_SOUND)
                .build();
         return notify;
    }

    public void doNotify(Notification notification, int notifyId) {
        mNotificaitonMan.notify(notifyId, notification);
    }

    public void doCancel(int notifyId) {
        mNotificaitonMan.cancel(notifyId);
    }
}
