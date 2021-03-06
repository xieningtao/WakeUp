package com.sf.banbanle;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.basesmartframe.baseui.BaseActivity;
import com.maxleap.MLDataManager;
import com.maxleap.MLObject;
import com.maxleap.SaveCallback;
import com.maxleap.exception.MLException;
import com.sf.banbanle.bean.BaiduSingleDevicePushBean;
import com.sf.banbanle.bean.LoginInfo;
import com.sf.banbanle.bean.TaskBean;
import com.sf.banbanle.bean.TimeBean;
import com.sf.banbanle.bean.UserInfoBean;
import com.sf.banbanle.config.BBLConstant;
import com.sf.banbanle.config.BBLMessageId;
import com.sf.banbanle.config.GlobalInfo;
import com.sf.banbanle.dialog.SFWheelDateDialog;
import com.sf.banbanle.http.BDPushHandler;
import com.sf.banbanle.http.BDPushUtil;
import com.sf.banbanle.http.HttpUrl;
import com.sf.banbanle.http.SFBDPushRequest;
import com.sf.banbanle.task.ActivityTaskDetail;
import com.sf.banbanle.user.ActivityFriendsList;
import com.sf.httpclient.core.AjaxParams;
import com.sf.httpclient.newcore.MethodType;
import com.sf.httpclient.newcore.SFHttpStringCallback;
import com.sf.httpclient.newcore.SFRequest;
import com.sf.loglib.L;
import com.sf.utils.baseutil.NetWorkManagerUtil;
import com.sf.utils.baseutil.SFToast;
import com.sf.utils.baseutil.StringBuilderHelp;
import com.sflib.CustomView.baseview.EditTextClearDroidView;
import com.sflib.reflection.core.SFBridgeManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by NetEase on 2016/12/1 0001.
 */

public class ActivityEditContent extends BaseBBLActivity {
    private EditTextClearDroidView mContent, mTitle;
    private TextView mUserListTv;
    private Button mAddUserBt;
    public final int ADD_USER_REQUEST = 1010;
    private List<UserInfoBean> mUserList = new ArrayList<>();

    private View mStartTimeView, mEndTimeView;
    private TextView mStartTimeTv, mEndTimeTv;
    private String mWeekStr[];
    private SFWheelDateDialog mDateDialog;
    private TaskBean mTaskBean;
    private UserInfoBean mUserInfoBean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_content);
        mContent = (EditTextClearDroidView) findViewById(R.id.edit_content);
        mTitle = (EditTextClearDroidView) findViewById(R.id.edit_title);
        mUserListTv = (TextView) findViewById(R.id.user_list_tv);
        mAddUserBt = (Button) findViewById(R.id.add_user_bt);
        mWeekStr = getResources().getStringArray(R.array.week_day_txt);
        mAddUserBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ActivityEditContent.this, ActivityFriendsList.class);
                startActivityForResult(intent, ADD_USER_REQUEST);
            }
        });

        mStartTimeView = findViewById(R.id.start_time_rl);
        mEndTimeView = findViewById(R.id.end_time_rl);
        mStartTimeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimeBean timeBean = (TimeBean) mStartTimeTv.getTag();
                showDateDialog(timeBean.mTime, 0);
            }
        });
        mEndTimeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimeBean timeBean = (TimeBean) mEndTimeTv.getTag();
                showDateDialog(timeBean.mTime, 1);
            }
        });
        mStartTimeTv = (TextView) findViewById(R.id.start_time_tv);
        mEndTimeTv = (TextView) findViewById(R.id.end_time_tv);
        Intent intent = getIntent();
        if (intent != null) {
            mTaskBean = (TaskBean) intent.getSerializableExtra(BBLConstant.TASK_BEAN);
            mUserInfoBean = (UserInfoBean) intent.getSerializableExtra(BBLConstant.USER_INFO_BEAN);
        }
        Calendar calendar = Calendar.getInstance();
        if (mTaskBean != null) {
            calendar.setTimeInMillis(mTaskBean.getStartTime());
        } else {
            calendar.add(Calendar.MINUTE, 20);
        }
        TimeBean startTime = getTimeBean(calendar);
        if (mTaskBean != null) {
            calendar.setTimeInMillis(mTaskBean.getEndTime());
        } else {
            calendar.add(Calendar.MINUTE, 30);
        }
        TimeBean endTime = getTimeBean(calendar);
        mStartTimeTv.setText(startTime.mContent);
        mStartTimeTv.setTag(startTime);
        mEndTimeTv.setText(endTime.mContent);
        mEndTimeTv.setTag(endTime);

        if (mUserInfoBean != null && mTaskBean != null) {
            mAddUserBt.setEnabled(false);
            mUserList.add(mUserInfoBean);
            fillViewWithData();
        }
    }

    @Override
    protected void onCustomActionBarCreated(View rootView) {
        rootView.setBackgroundColor(getResources().getColor(R.color.actionbar_blue));
        ImageView iconIv = (ImageView) rootView.findViewById(R.id.icon_action_iv);
        iconIv.setImageResource(R.drawable.back_icon);
        iconIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        TextView titleTv = (TextView) rootView.findViewById(R.id.txt_action_tv);
        titleTv.setText("任务编辑页");
        ImageView plusIv = (ImageView) rootView.findViewById(R.id.plus_iv);
        plusIv.setVisibility(View.GONE);
        TextView finishTv = (TextView) rootView.findViewById(R.id.action_right_tv);
        finishTv.setText("完成");
        finishTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(mTitle.getEditText().getText())) {
                    SFToast.showToast(R.string.task_title_tips);
                    return;
                }
                if (TextUtils.isEmpty(mContent.getEditText().getText())) {
                    SFToast.showToast(R.string.task_content_tips);
                    return;
                }

                if (!NetWorkManagerUtil.isNetworkAvailable()) {
                    SFToast.showToast(R.string.net_unavailable);
                    return;
                }
                String title = mTitle.getEditText().getText().toString();
                String content = mContent.getEditText().getText().toString();
                addTask(title, content, mUserList.get(0));
            }
        });

    }


    private void fillViewWithData() {
        mTitle.getEditText().setText(mTaskBean.getTitle());
        mContent.getEditText().setText(mTaskBean.getContent());
        mUserListTv.setText(constructUserList());

    }

    private void showDateDialog(long millionSeconds, final int type) {
        if (mDateDialog == null) {
            mDateDialog = new SFWheelDateDialog(this);
        }
        mDateDialog.setDateDialogClick(new SFWheelDateDialog.onWheelDateDialogClick() {
            @Override
            public void onCancelClick() {

            }

            @Override
            public void onSureClick(long millions) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(millions);
                TimeBean timeBean = getTimeBean(calendar);
                if (type == 0) {
                    mStartTimeTv.setText(timeBean.mContent);
                    mStartTimeTv.setTag(timeBean);
                } else {
                    mEndTimeTv.setText(timeBean.mContent);
                    mEndTimeTv.setTag(timeBean);
                }
            }
        });
        mDateDialog.setCurrentItem(millionSeconds);
        mDateDialog.show();
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

    private void addTask(final String title, final String content, final UserInfoBean userInfoBean) {
        final MLObject task = new MLObject("Task");
        if (mTaskBean != null) {
            task.setObjectId(mTaskBean.getId());
        }
        task.put("title", title);
        task.put("content", content);
        task.put("state", BBLConstant.IDLE);
        task.put("acceptor", userInfoBean.getUserName());
        UserInfoBean ownerBean = GlobalInfo.getInstance().mInfoBean.getValue();
        task.put("creator", ownerBean.getUserName());
        task.put("url", ownerBean.getUrl());
        task.put("nickName", ownerBean.getNickName());
        final TimeBean startTime = (TimeBean) mStartTimeTv.getTag();
        task.put("startTime", startTime.mTime);
        final TimeBean endTime = (TimeBean) mEndTimeTv.getTag();
        task.put("endTime", endTime.mTime);
        MLDataManager.saveInBackground(task, new SaveCallback() {
            @Override
            public void done(MLException e) {
                if (e == null) {
                    SFToast.showToast(R.string.add_task_success);
                    List<String> channelIds = new ArrayList<String>();
                    channelIds.add(userInfoBean.getChannelId());
                    pushMsgToDevice(channelIds, title, content, task.getObjectId(), startTime.mTime, endTime.mTime);
                    SFBridgeManager.send(BBLMessageId.REFRESH_TASK, 0);
                    finish();
                } else {
                    L.error(TAG, "save task error: " + e);
                    SFToast.showToast(R.string.add_task_fail);
                }
            }
        });

    }

    private void addTask(final String title, final String content, List<UserInfoBean> userInfoBeen) {
        final List<MLObject> tasks = new ArrayList<>();
        LoginInfo loginInfo = GlobalInfo.getInstance().mLoginInfo.getValue();
        final MLObject myTask = getTask(title, content, loginInfo.userName, true);
        tasks.add(myTask);
        for (UserInfoBean been : userInfoBeen) {
            MLObject otherTask = getTask(title, content, been.getUserName(), false);
            tasks.add(otherTask);
        }
        MLDataManager.saveAllInBackground(tasks, new SaveCallback() {
            @Override
            public void done(MLException e) {
                if (e == null) {
                    SFToast.showToast(R.string.add_task_success);
                    List<String> channelIds = new ArrayList<String>();
                    for (UserInfoBean userInfoBean : mUserList) {
                        channelIds.add(userInfoBean.getChannelId());
                    }
                    pushMsgToDevice(channelIds, title, content, "", 0, 0);
                } else {
                    SFToast.showToast(R.string.add_task_fail);
                    L.error(TAG, "addTask exception: " + e);
                }
            }
        });
    }


    private MLObject getTask(String title, String content, String userName, boolean fromMe) {
        MLObject task = new MLObject("Task");
        task.put("userName", userName);
        task.put("title", title);
        task.put("content", content);
        task.put("state", BBLConstant.IDLE);
        if (fromMe) {
            task.put("type", 0);
        } else {
            task.put("type", 1);
        }
        UserInfoBean userInfoBean = GlobalInfo.getInstance().mInfoBean.getValue();
        task.put("url", userInfoBean.getUrl());
        return task;
    }

    private void pushMsgToDevice(List<String> channelIds, String title, String content, String taskId, long startTime, long endTime) {
        JSONObject notification = new JSONObject();
        try {
            notification.put("title", title);
            if (mTaskBean != null) {
                notification.put("description", "任务被修改了,请注意查看");
            } else {
                notification.put("description", content);
            }
            notification.put("notification_builder_id", 0);
            notification.put("notification_basic_style", 7);
            notification.put("open_type", 2);
            Intent intent = new Intent(ActivityEditContent.this, ActivityTaskDetail.class);
            intent.putExtra(ActivityTaskDetail.TASK_ID, taskId);
            intent.putExtra(BBLConstant.TO_TASK_DETAIL_CHANNEL, BBLConstant.ASSIGN);
            String url = intent.toURI();
            notification.put("pkg_content", url);
            JSONObject jsonCustormCont = new JSONObject();
            jsonCustormCont.put("taskId", taskId); //自定义内容，key-value
            jsonCustormCont.put("startTime", startTime);
            jsonCustormCont.put("endTime", endTime);
            jsonCustormCont.put("videoPath", "");
            notification.put("custom_content", jsonCustormCont);
        } catch (Exception e) {
            L.error(TAG, "pushToSingleDevice exception: " + e);
        }
        BDPushUtil.pushToBatchDevice(channelIds, notification);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADD_USER_REQUEST && resultCode == RESULT_OK) {
            if (data != null) {
                List<UserInfoBean> userInfoBeanList = (List<UserInfoBean>) data.getSerializableExtra(ActivityFriendsList.SELECTED_USER_INFO);
                mUserList.clear();
                mUserList.addAll(userInfoBeanList);
                mUserListTv.setText(constructUserList());
            }
        }
    }

    private String constructUserList() {
        StringBuilder builder = new StringBuilder();
        for (UserInfoBean infoBean : mUserList) {
            builder.append(infoBean.getUserName()).append(";");
        }
        return builder.toString();
    }
}
