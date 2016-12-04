package com.sf.banbanle;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.basesmartframe.baseui.BaseActivity;
import com.maxleap.MLDataManager;
import com.maxleap.MLObject;
import com.maxleap.SaveCallback;
import com.maxleap.exception.MLException;
import com.sf.banbanle.bean.BaiduSingleDevicePushBean;
import com.sf.banbanle.bean.LoginInfo;
import com.sf.banbanle.bean.UserInfoBean;
import com.sf.banbanle.config.GlobalInfo;
import com.sf.banbanle.http.BDPushHandler;
import com.sf.banbanle.http.HttpUrl;
import com.sf.banbanle.http.SFBDPushRequest;
import com.sf.banbanle.user.ActivityFriendsList;
import com.sf.httpclient.core.AjaxParams;
import com.sf.httpclient.newcore.MethodType;
import com.sf.httpclient.newcore.SFHttpStringCallback;
import com.sf.httpclient.newcore.SFRequest;
import com.sf.loglib.L;
import com.sf.utils.baseutil.SFToast;
import com.sflib.CustomView.baseview.EditTextClearDroidView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by NetEase on 2016/12/1 0001.
 */

public class ActivityEditContent extends BaseActivity {
    private EditTextClearDroidView mContent, mTitle;
    private TextView mUserListTv;
    private Button mAddUserBt;
    public final int ADD_USER_REQUEST = 1010;
    private List<UserInfoBean> mUserList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_content);
        mContent = (EditTextClearDroidView) findViewById(R.id.edit_content);
        mTitle = (EditTextClearDroidView) findViewById(R.id.edit_title);
        mUserListTv = (TextView) findViewById(R.id.user_list_tv);
        mAddUserBt = (Button) findViewById(R.id.add_user_bt);
        mAddUserBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ActivityEditContent.this, ActivityFriendsList.class);
                startActivityForResult(intent, ADD_USER_REQUEST);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case 0:
                if (TextUtils.isEmpty(mTitle.getEditText().getText())) {
                    SFToast.showToast(R.string.task_title_tips);
                    return true;
                }
                if (TextUtils.isEmpty(mContent.getEditText().getText())) {
                    SFToast.showToast(R.string.task_content_tips);
                    return true;
                }

                String title = mTitle.getEditText().getText().toString();
                String content = mContent.getEditText().getText().toString();
                addTask(title, content, mUserList);
                return true;
        }
        return super.onOptionsItemSelected(item);
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
                    pushToBatchDevice(mUserList,title,content,"");
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
        if (fromMe) {
            task.put("type", 0);
        } else {
            task.put("type", 1);
        }
        UserInfoBean userInfoBean=GlobalInfo.getInstance().mInfoBean.getValue();
        task.put("url",userInfoBean.getUrl());
        return task;
    }


    private void pushToBatchDevice(List<UserInfoBean> infoBeanList,String title,String content,String taskId) {
        AjaxParams ajaxParams = new AjaxParams();
        ajaxParams.put("device_type", "3");
        ajaxParams.put("msg_type", "1");

//        BaiduPushInfo pushInfo = GlobalInfo.getInstance().mPushInfo.getValue();
//        if (pushInfo != null) {
//            ajaxParams.put("channel_id", "4500265865066869730");
//            BaiduPushBean pushBean = new BaiduPushBean();
//            pushBean.setTitle("hello");
//            pushBean.setDescription("description");
//            Gson gson = new Gson();
//            ajaxParams.put("msg", gson.toJson(pushBean));
//        }

        JSONObject notification = new JSONObject();
        try {
//            JSONObject jsonChanneId = new JSONObject();
            JSONArray jsonArray = new JSONArray();
            for (UserInfoBean infoBean : infoBeanList) {
                jsonArray.put(Long.valueOf(infoBean.getChannelId()));
            }
            ajaxParams.put("channel_ids", jsonArray.toString());

            notification.put("title", title);
            notification.put("description", content);
            notification.put("notification_builder_id", 0);
            notification.put("notification_basic_style", 7);
            notification.put("open_type", 1);
            notification.put("url", "http://push.baidu.com");
            JSONObject jsonCustormCont = new JSONObject();
            jsonCustormCont.put("taskId", taskId); //自定义内容，key-value
            notification.put("custom_content", jsonCustormCont);
        } catch (Exception e) {
            L.error(TAG, "pushToSingleDevice exception: " + e);
        }
        ajaxParams.put("msg", notification.toString());
        SFBDPushRequest _request = new SFBDPushRequest(HttpUrl.PUSH_BATCH_DEVICE, MethodType.POST, ajaxParams) {
            @Override
            public Class getClassType() {
                return BaiduSingleDevicePushBean.class;
            }

        };
        BDPushHandler pushHandler = new BDPushHandler(_request, new SFHttpStringCallback<BaiduSingleDevicePushBean>() {

            @Override
            public void onSuccess(SFRequest sfRequest, BaiduSingleDevicePushBean pushBean) {
                L.info(TAG, "pushToSingleDevice,onsuccess: " + pushBean);
            }

            @Override
            public void onFailed(SFRequest sfRequest, Exception e) {
                L.error(TAG, "pushToSingleDevice,onfailed: " + e);
            }
        });
        pushHandler.start();
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
