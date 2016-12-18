package com.sf.banbanle.alarm;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Vibrator;
import android.text.TextUtils;

import com.example.sfchat.media.MediaPlayManager;
import com.sf.banbanle.R;
import com.sf.banbanle.bean.PushTaskBean;
import com.sf.banbanle.bean.TaskBean;
import com.sf.banbanle.config.BBLConstant;
import com.sf.banbanle.notify.TaskNotify;
import com.sf.banbanle.task.ActivityTaskDetail;
import com.sf.loglib.L;

/**
 * Created by NetEase on 2016/12/5 0005.
 */

public class AlarmReceiver extends BroadcastReceiver {
    private final String TAG = getClass().getName();
    private Vibrator mVibrator;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (mVibrator == null) {
            mVibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        }
        L.info(TAG, "onReceive call");
        notifyUser(context, intent);
    }

    private void notifyUser(Context context, Intent intent) {
        doMotion();
        playSound(context, intent);
        showNotify(context, intent);
    }

    private void showNotify(Context context, Intent intent) {
        PushTaskBean taskBean = (PushTaskBean) intent.getSerializableExtra(BBLConstant.PUSH_TASK_BEAN);
        TaskNotify taskNotify = new TaskNotify(context);
        Notification notification = taskNotify.createBasicNotify(context, taskBean.getTitle(), taskBean.getContent());
        Intent newIntent = new Intent(context, ActivityTaskDetail.class);
        newIntent.putExtra(BBLConstant.TO_TASK_DETAIL_CHANNEL,BBLConstant.ASSIGN);
        newIntent.putExtra(ActivityTaskDetail.TASK_ID,taskBean.getId());
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, newIntent, 0);
        notification.contentIntent = pendingIntent;
        taskNotify.doNotify(notification, 1);
    }

    private void playSound(Context context, Intent intent) {
        MediaPlayManager.getInstance().createMediaPlay();
        MediaPlayManager.getInstance().setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                MediaPlayManager.getInstance().getPlayer().setLooping(true);
            }
        });
        PushTaskBean taskBean = (PushTaskBean) intent.getSerializableExtra(BBLConstant.PUSH_TASK_BEAN);
        if (taskBean == null || TextUtils.isEmpty(taskBean.getVideoPath())) {
            AssetFileDescriptor assetFile = context.getResources().openRawResourceFd(R.raw.video1);
//            String path="http://hao.1015600.com/upload/ring/000/975/28dab8e81862c9c36111a9657b9391c7.mp3";
            MediaPlayManager.getInstance().startPlay(assetFile.getFileDescriptor(), assetFile.getStartOffset(), assetFile.getLength());
        } else {
            MediaPlayManager.getInstance().startPlay(taskBean.getVideoPath());
        }

    }

    private void doMotion() {
        long[] pattern = {200, 400, 200, 400, 200, 400, 200, 400, 200, 400};   // 停止 开启 停止 开启
        mVibrator.vibrate(pattern, -1);
    }

    private void startFloatingService(Context context, Intent receiverIntent) {
        Intent intent = new Intent(context, BBLAlarmService.class);
        intent.setAction(BBLAlarmService.ACTION_SERVICE);
        intent.putExtra(BBLAlarmManager.ALARM_ID, receiverIntent.getStringExtra(BBLAlarmManager.ALARM_ID));
        intent.putExtra(BBLConstant.TASK_BEAN, receiverIntent.getSerializableExtra(BBLConstant.TASK_BEAN));
        intent.putExtra(BBLConstant.USER_INFO_BEAN, receiverIntent.getSerializableExtra(BBLConstant.USER_INFO_BEAN));
        context.startService(intent);
    }
}
