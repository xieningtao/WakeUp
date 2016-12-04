package com.sf.banbanle;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.basesmartframe.baseadapter.BaseAdapterHelper;
import com.basesmartframe.baseui.BasePullListFragment;
import com.maxleap.FindCallback;
import com.maxleap.MLDataManager;
import com.maxleap.MLObject;
import com.maxleap.MLQuery;
import com.maxleap.MLQueryManager;
import com.maxleap.exception.MLException;
import com.maxleap.im.DataHandler;
import com.maxleap.im.MLParrot;
import com.maxleap.im.ParrotException;
import com.maxleap.im.SimpleDataHandler;
import com.maxleap.im.entity.Message;
import com.nostra13.universalimageloader.utils.L;
import com.sf.banbanle.bean.AlarmBean;
import com.sf.banbanle.bean.LoginInfo;
import com.sf.banbanle.bean.TaskBean;
import com.sf.banbanle.config.BBLConstant;
import com.sf.banbanle.config.GlobalInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by NetEase on 2016/12/1 0001.
 */

public class FragmentHome extends BasePullListFragment<TaskBean> {
    @Override
    protected boolean onRefresh() {
        getTasks(true);
        return true;
    }

    private void getTasks(boolean refresh) {
        final MLQuery<MLObject> task = MLQuery.getQuery("Task");
        LoginInfo loginInfo = GlobalInfo.getInstance().mLoginInfo.getValue();
        if (loginInfo != null) {
            task.whereEqualTo("userName", loginInfo.userName);
            if (!refresh) {
                task.setSkip(getDataSize());
            }
            task.setLimit(10);
            MLQueryManager.findAllInBackground(task, new FindCallback<MLObject>() {
                @Override
                public void done(List<MLObject> list, MLException e) {
                    List<TaskBean> taskBeanList = new ArrayList<TaskBean>();
                    if (e == null && list != null) {
                        for (MLObject object : list) {
                            TaskBean taskBean = new TaskBean();
                            taskBean.setContent(object.getString("content"));
                            taskBean.setState(object.getString("state"));
                            taskBean.setTitle(object.getString("title"));
                            taskBean.setType(object.getInt("type"));
                            taskBean.setUserName(object.getString("userName"));
                            taskBean.setUrl(object.getString("url"));
                            taskBean.setId(object.getObjectId());
                            taskBeanList.add(taskBean);
                        }
                    } else {
                        L.e(TAG, "get task list exception: " + e);
                    }
                    finishRefreshOrLoading(taskBeanList, taskBeanList.isEmpty() || taskBeanList.size() < 10 ? false : true);
                }
            });
        }
    }

    @Override
    protected boolean onLoadMore() {
        getTasks(false);
        return false;
    }

    @Override
    protected int[] getLayoutIds() {
        return new int[]{R.layout.alarm_item};
    }

    @Override
    protected void bindView(BaseAdapterHelper baseAdapterHelper, int i, TaskBean alarmBean) {
        baseAdapterHelper.setImageBuilder(R.id.photo_iv,alarmBean.getUrl());
        baseAdapterHelper.setText(R.id.title_tv,alarmBean.getTitle());
        baseAdapterHelper.setText(R.id.content_tv,alarmBean.getContent());
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        chatLogin();
    }

    private void chatLogin() {
        LoginInfo loginInfo = GlobalInfo.getInstance().mLoginInfo.getValue(BBLConstant.LOGIN_INFO, LoginInfo.class);
        if (loginInfo != null) {
            doChatLogin(loginInfo.userName, loginInfo.pwd);
        } else {
            com.sf.loglib.L.error(TAG, "chatLogin login info is null");
        }
    }

    private void doChatLogin(String username, String password) {
        MLParrot parrot = MLParrot.getInstance();
        parrot.initWithMLUser(BanBanLeApp.APP_ID, BanBanLeApp.REST_API_KEY, username, password);
        parrot.login(new DataHandler<String>() {
            @Override
            public void onSuccess(String id) {
                com.sf.loglib.L.info(TAG, "doChatLogin.onSuccess id: " + id);
            }

            @Override
            public void onError(ParrotException e) {
                com.sf.loglib.L.error(TAG, "doChatLogin.onError exception: " + e);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
//        MLParrot parrot = MLParrot.getInstance();
//        parrot.onMessage(new SimpleDataHandler<Message>() {
//            @Override
//            public void onSuccess(Message message) {
//                L.i(TAG, "onMessage message: " + message);
//            }
//        });
    }

    @Override
    public void onPause() {
        super.onPause();
//        MLParrot parrot = MLParrot.getInstance();
//        parrot.offMessage(new SimpleDataHandler<Message>() {
//            @Override
//            public void onSuccess(Message message) {
//                L.i(TAG, "offMessage message: " + message);
//            }
//        });
    }
}
