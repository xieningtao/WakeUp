package com.sf.banbanle;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.basesmartframe.baseadapter.BaseAdapterHelper;
import com.basesmartframe.baseui.BasePullListFragment;
import com.maxleap.im.DataHandler;
import com.maxleap.im.MLParrot;
import com.maxleap.im.ParrotException;
import com.maxleap.im.SimpleDataHandler;
import com.maxleap.im.entity.Message;
import com.nostra13.universalimageloader.utils.L;
import com.sf.banbanle.bean.AlarmBean;
import com.sf.banbanle.bean.LoginInfo;
import com.sf.banbanle.config.BBLConstant;
import com.sf.banbanle.config.GlobalInfo;

/**
 * Created by NetEase on 2016/12/1 0001.
 */

public class FragmentHome extends BasePullListFragment<AlarmBean> {
    @Override
    protected boolean onRefresh() {
        return false;
    }

    @Override
    protected boolean onLoadMore() {
        return false;
    }

    @Override
    protected int[] getLayoutIds() {
        return new int[]{R.layout.alarm_item};
    }

    @Override
    protected void bindView(BaseAdapterHelper baseAdapterHelper, int i, AlarmBean alarmBean) {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        chatLogin();
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
        MLParrot parrot = MLParrot.getInstance();
        parrot.onMessage(new SimpleDataHandler<Message>() {
            @Override
            public void onSuccess(Message message) {
                L.i(TAG, "onMessage message: " + message);
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        MLParrot parrot = MLParrot.getInstance();
        parrot.offMessage(new SimpleDataHandler<Message>() {
            @Override
            public void onSuccess(Message message) {
                L.i(TAG, "offMessage message: " + message);
            }
        });
    }
}
