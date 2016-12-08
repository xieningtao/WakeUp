package com.sf.banbanle.task;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.basesmartframe.baseui.BaseActivity;
import com.maxleap.GetCallback;
import com.maxleap.MLDataManager;
import com.maxleap.MLObject;
import com.maxleap.MLQuery;
import com.maxleap.MLQueryManager;
import com.maxleap.SaveCallback;
import com.maxleap.exception.MLException;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.sf.banbanle.R;
import com.sf.banbanle.alarm.BBLAlarmManager;
import com.sf.banbanle.config.BBLConstant;
import com.sf.loglib.L;
import com.sf.utils.baseutil.SFToast;

import java.util.Calendar;

/**
 * Created by mac on 16/12/4.
 */

public class ActivityTaskDetail extends BaseActivity {
    public final static String TASK_ID = "task_id";
    private TextView mTitleTv, mContentTv;
    private ImageView mPhotoIv;
    private Button mDenyBt, mAcceptBt;
    private String mDetailId;
    private MLObject mCurObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        mDetailId = getTaskDetailId();
        mTitleTv = (TextView) findViewById(R.id.title_tv);
        mContentTv = (TextView) findViewById(R.id.content_tv);
        mPhotoIv = (ImageView) findViewById(R.id.photo_iv);
        mDenyBt = (Button) findViewById(R.id.deny_bt);
        mAcceptBt = (Button) findViewById(R.id.accept_bt);
        mDenyBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateTaskState(BBLConstant.DENY);
            }
        });

        mAcceptBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateTaskState(BBLConstant.ACCEPT);
            }
        });
        getTaskDetail();
    }

    private long[] getAlarmTime(long startTime, long endTime, int times) {
        if (startTime >= endTime) {
            return new long[]{endTime};
        }
        long det = (endTime - startTime) / times;
        long alarmTime[] = new long[times];
        for (int i = 0; i < times; i++) {
            alarmTime[i] = startTime + det * i;
        }
        return alarmTime;
    }

    private void createTimer(String objectId, long startTime, long endTime, int times) {
        Calendar calendar = Calendar.getInstance();
        long alarmTime[] = getAlarmTime(startTime, endTime, times);
        for (int i = 0; i < alarmTime.length; i++) {
            calendar.setTimeInMillis(alarmTime[i]);
            BBLAlarmManager.getManager().createAlarm(this, calendar, objectId + i, true);
        }
    }

    private void updateTaskState(final String state) {
        if (mCurObject != null) {
            mCurObject.put("state", state);
            MLDataManager.saveInBackground(mCurObject, new SaveCallback() {
                @Override
                public void done(MLException e) {
                    if (e == null) {
                        if (BBLConstant.DENY.equals(state)) {
                            SFToast.showToast(R.string.deny_task_success);
                        } else {
                            SFToast.showToast(R.string.accept_task_success);
                            long startTime = mCurObject.getLong("startTime");
                            long endTime = mCurObject.getLong("endTime");
                            createTimer(mCurObject.getObjectId(), startTime, endTime, 2);
                        }
                    } else {
                        if (BBLConstant.DENY.equals(state)) {
                            SFToast.showToast(R.string.deny_task_fail);
                        } else {
                            SFToast.showToast(R.string.accept_task_fail);
                        }
                        L.error(TAG, "updateTaskState exception: " + e);
                    }
                }
            });
        }
    }

    private String getTaskDetailId() {
        Intent intent = getIntent();
        if (intent != null) {
            return intent.getStringExtra(TASK_ID);
        }
        return "";
    }

    private void updateDetailView(MLObject mlObject) {
        if (mlObject == null) {
            return;
        }
        ImageLoader.getInstance().displayImage("http://" + mlObject.getString("url"), mPhotoIv);
        mTitleTv.setText(mlObject.getString("title"));
        mContentTv.setText(mlObject.getString("content"));
    }

    private void getTaskDetail() {
        MLQuery mlQuery = new MLQuery("Task");
        MLQueryManager.getInBackground(mlQuery, mDetailId, new GetCallback() {
            @Override
            public void done(MLObject mlObject, MLException e) {
                if (e == null) {
                    updateDetailView(mlObject);
                    mCurObject = mlObject;
                } else {
                    SFToast.showToast(R.string.get_task_detail_failed);
                }
            }
        });
    }
}
