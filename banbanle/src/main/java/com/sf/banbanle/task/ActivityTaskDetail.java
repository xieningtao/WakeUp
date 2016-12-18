package com.sf.banbanle.task;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.basesmartframe.baseui.BaseActivity;
import com.maxleap.DeleteCallback;
import com.maxleap.GetCallback;
import com.maxleap.MLDataManager;
import com.maxleap.MLObject;
import com.maxleap.MLQuery;
import com.maxleap.MLQueryManager;
import com.maxleap.SaveCallback;
import com.maxleap.exception.MLException;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.sf.banbanle.ActivityEditContent;
import com.sf.banbanle.FragmentHome;
import com.sf.banbanle.R;
import com.sf.banbanle.bean.MLObjectParserUtil;
import com.sf.banbanle.bean.TimeBean;
import com.sf.banbanle.bean.UserInfoBean;
import com.sf.banbanle.config.BBLConstant;
import com.sf.banbanle.config.BBLMessageId;
import com.sf.banbanle.dialog.SFProgressDialog;
import com.sf.banbanle.http.BDPushUtil;
import com.sf.loglib.L;
import com.sf.utils.baseutil.NetWorkManagerUtil;
import com.sf.utils.baseutil.SFToast;
import com.sflib.reflection.core.SFBridgeManager;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

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
    private UserInfoBean mUserInfoBean = new UserInfoBean();
    private String mChannelFrom = "";
    private TextView mStartTimeTv, mEndTimeTv;
    private String mWeekStr[];

    private SFProgressDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        final Intent intent = getIntent();
        if (intent != null) {
            mChannelFrom = intent.getStringExtra(BBLConstant.TO_TASK_DETAIL_CHANNEL);
        }
        mDialog = new SFProgressDialog(this);
        mWeekStr = getResources().getStringArray(R.array.week_day_txt);
        mDetailId = getTaskDetailId();
        mTitleTv = (TextView) findViewById(R.id.title_tv);
        mContentTv = (TextView) findViewById(R.id.content_tv);
        mPhotoIv = (ImageView) findViewById(R.id.photo_iv);
        mDenyBt = (Button) findViewById(R.id.deny_bt);
        mAcceptBt = (Button) findViewById(R.id.accept_bt);
        mStartTimeTv = (TextView) findViewById(R.id.start_time_tv);
        mEndTimeTv = (TextView) findViewById(R.id.end_time_tv);
        mDenyBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!NetWorkManagerUtil.isNetworkAvailable()) {
                    SFToast.showToast(R.string.net_unavailable);
                    return;
                }
                mDialog.setLoadingText(R.string.progress_operating);
                if (BBLConstant.CREATOR.equals(mChannelFrom)) {
                    removeTask();
                } else {
                    updateTaskState(BBLConstant.DENY);
                }
            }
        });

        mAcceptBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String state = "";
                if (BBLConstant.CREATOR.equals(mChannelFrom)) {//跳转到编辑页面
                    Intent editIntent = new Intent(ActivityTaskDetail.this, ActivityEditContent.class);
                    editIntent.putExtra(BBLConstant.TASK_BEAN, MLObjectParserUtil.parserTaskBean(mCurObject));
                    editIntent.putExtra(BBLConstant.USER_INFO_BEAN, mUserInfoBean);
                    startActivity(editIntent);
                    finish();
                    return;
                } else if (BBLConstant.ACCEPT.equals(mChannelFrom)) {
                    state = BBLConstant.FINISH;
                } else {
                    state = BBLConstant.ACCEPT;
                }
                if(!TextUtils.isEmpty(state)) {
                    updateTaskState(state);
                }
            }
        });

        if (BBLConstant.CREATOR.equals(mChannelFrom)) {
            mDenyBt.setText(R.string.task_remove);
            mAcceptBt.setText(R.string.task_modify);
        } else if (BBLConstant.ACCEPT.equals(mChannelFrom)) {
            mDenyBt.setText(R.string.deny_task);
            mAcceptBt.setText(R.string.add_finish);
        }
        if (NetWorkManagerUtil.isNetworkAvailable()) {
            getTaskDetail();
            mDialog.setLoadingText(R.string.progress_loading);
        } else {
            SFToast.showToast(R.string.net_unavailable);
        }
    }


    private void removeTask() {
        final String title = mCurObject.getString("title");
        MLDataManager.deleteInBackground(mCurObject, new DeleteCallback() {
            @Override
            public void done(MLException e) {
                if (e == null) {
                    SFToast.showToast(R.string.task_remove_success);
                    notifyMsgOwner(BBLConstant.REMOVE, title);
                } else {
                    SFToast.showToast(R.string.task_remove_fail);
                    L.error(TAG, "removeTask exception: " + e);
                }
                mDialog.dismiss();
            }
        });
    }





    private void notifyMsgOwner(String state, String taskTitle) {
        JSONObject notification = new JSONObject();
        try {
            notification.put("title", "帮帮乐助手");
            StringBuilder description = new StringBuilder();
            if (BBLConstant.ACCEPT.equals(state)) {
                description.append(mUserInfoBean.getNickName()).append("接受了任务: ").append(taskTitle);
            } else if (BBLConstant.DENY.equals(state)) {
                description.append(mUserInfoBean.getNickName()).append("拒绝了任务: ").append(taskTitle);
            } else if (BBLConstant.REMOVE.equals(state)) {
                description.append(mUserInfoBean.getNickName()).append("删除了任务: ").append(taskTitle);
            } else if (BBLConstant.FINISH.equals(state)) {
                description.append(mUserInfoBean.getNickName()).append("完成了任务: ").append(taskTitle);
            }
            notification.put("description", description);
            notification.put("notification_builder_id", 0);
            notification.put("notification_basic_style", 7);
            notification.put("open_type", 2);
            Intent intent = new Intent(ActivityTaskDetail.this, FragmentHome.class);
            intent.putExtra(FragmentHome.PAGE_INDEX, 0);
            String url = intent.toURI();
            notification.put("pkg_content", url);
        } catch (Exception e) {
            L.error(TAG, "pushToSingleDevice exception: " + e);
        }
        List<String> channelIds = new ArrayList<>();
        channelIds.add(mUserInfoBean.getChannelId());
        BDPushUtil.pushToBatchDevice(channelIds, notification);
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
                            SFBridgeManager.send(BBLMessageId.REFRESH_TASK, 1);
                            notifyMsgOwner(BBLConstant.DENY, mCurObject.getString("title"));
                            finish();
                        } else {
                            SFBridgeManager.send(BBLMessageId.REFRESH_TASK, 2);
                            SFToast.showToast(R.string.accept_task_success);
                            long startTime = mCurObject.getLong("startTime");
                            long endTime = mCurObject.getLong("endTime");
//                            createTimer(mCurObject.getObjectId(), startTime, endTime, 2);
                            notifyMsgOwner(BBLConstant.ACCEPT, mCurObject.getString("title"));
                            finish();
                        }
                    } else {
                        if (BBLConstant.DENY.equals(state)) {
                            SFToast.showToast(R.string.deny_task_fail);
                        } else {
                            SFToast.showToast(R.string.accept_task_fail);
                        }
                        L.error(TAG, "updateTaskState exception: " + e);
                    }
                    mDialog.dismiss();
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

    private TimeBean getTimeBean(Calendar calendar) {
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int week = calendar.get(Calendar.DAY_OF_WEEK);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        String hourMinute = String.format("%1$02d:%2$02d", hour, minute);
        String content = year + "年" + month + "月" + day + "日 " + mWeekStr[week - 1] + " " + hourMinute;
        long time = calendar.getTimeInMillis();
        return new TimeBean(time, content);
    }

    private void updateDetailView(MLObject mlObject) {
        if (mlObject == null) {
            return;
        }
        ImageLoader.getInstance().displayImage("http://" + mlObject.getString("url"), mPhotoIv);
        mTitleTv.setText(mlObject.getString("title"));
        mContentTv.setText(mlObject.getString("content"));
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(mlObject.getLong("startTime"));
        TimeBean startTime = getTimeBean(calendar);
        calendar.setTimeInMillis(mlObject.getLong("endTime"));
        TimeBean endTime = getTimeBean(calendar);
        mStartTimeTv.setText(startTime.mContent);

        mEndTimeTv.setText(endTime.mContent);
    }

    private void getTaskCreatorInfo(final String userName) {
        MLQuery mlQuery = new MLQuery("UserInfo");
        mlQuery.whereEqualTo("userName", userName);
        MLQueryManager.getFirstInBackground(mlQuery, new GetCallback() {
            @Override
            public void done(MLObject mlObject, MLException e) {
                if (e == null && mlObject != null) {
                    String url = mlObject.getString("url");
                    String nickName = mlObject.getString("nickName");
                    mUserInfoBean.setUserName(userName);
                    mUserInfoBean.setUrl(url);
                    mUserInfoBean.setNickName(nickName);
                    mUserInfoBean.setObjectId(mlObject.getObjectId());
                    mUserInfoBean.setChannelId(mlObject.getString("channelId"));
                    mUserInfoBean.setUserId(mlObject.getString("userId"));
                } else {
                    SFToast.showToast(R.string.fail_to_get_user_info);
                }
            }
        });
    }

    private void getTaskDetail() {
        MLQuery mlQuery = new MLQuery("Task");
        MLQueryManager.getInBackground(mlQuery, mDetailId, new GetCallback() {
            @Override
            public void done(MLObject mlObject, MLException e) {
                if (e == null) {
                    updateDetailView(mlObject);
                    mCurObject = mlObject;
                    getTaskCreatorInfo(mCurObject.getString("creator"));
                } else {
                    SFToast.showToast(R.string.get_task_detail_failed);
                }
                mDialog.dismiss();
            }
        });
    }
}
