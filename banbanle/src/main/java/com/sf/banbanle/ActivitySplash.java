package com.sf.banbanle;

import android.content.Intent;
import android.os.Bundle;

import com.basesmartframe.baseui.BaseActivity;
import com.maxleap.GetCallback;
import com.maxleap.MLAnonymousUtils;
import com.maxleap.MLObject;
import com.maxleap.MLQuery;
import com.maxleap.MLQueryManager;
import com.maxleap.MLUser;
import com.maxleap.exception.MLException;
import com.sf.banbanle.bean.LoginInfo;
import com.sf.banbanle.bean.UserInfoBean;
import com.sf.banbanle.config.GlobalInfo;
import com.sf.utils.baseutil.NetWorkManagerUtil;
import com.sf.utils.baseutil.SFToast;
import com.sflib.reflection.core.ThreadHelp;

import static com.sf.banbanle.config.BBLConstant.LOGIN_INFO;

/**
 * Created by NetEase on 2016/12/1 0001.
 */

public class ActivitySplash extends BaseActivity {
    private int mDelay = 0;
    private int mSplashTime = 2000;
    private long mStartTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        LoginInfo loginInfo = GlobalInfo.getInstance().mLoginInfo.getValue(LOGIN_INFO, LoginInfo.class);
        if (loginInfo != null&&MLUser.getCurrentUser()!=null) {
            mStartTime = System.currentTimeMillis();
            getUserInfo(loginInfo.userName);
        } else {
            mDelay = mSplashTime;
            toMainActivity();
        }
    }

    private void toMainActivity() {
        ThreadHelp.runInMain(new Runnable() {
            @Override
            public void run() {
                Intent intent = null;
                MLUser currentUser = MLUser.getCurrentUser();
                if (currentUser != null) {
                    if (MLAnonymousUtils.isLinked(currentUser)) {
                        MLUser.logOut();
                        intent = new Intent(ActivitySplash.this, LoginActivity.class);
                    } else {
                        //  普通用户
                        intent = new Intent(ActivitySplash.this, NewActivityHome.class);
                    }
                } else {
                    // 未登录
                    intent = new Intent(ActivitySplash.this, LoginActivity.class);
                }
                startActivity(intent);
                finish();
            }
        }, mDelay);
    }

    private void getUserInfo(final String userName) {
        if (!NetWorkManagerUtil.isNetworkAvailable()) {
            SFToast.showToast(R.string.net_unavailable);
            return;
        }
        MLQuery mlQuery = new MLQuery("UserInfo");
        mlQuery.whereEqualTo("userName", userName);
        MLQueryManager.getFirstInBackground(mlQuery, new GetCallback() {
            @Override
            public void done(MLObject mlObject, MLException e) {
                if (e == null && mlObject != null) {
                    String url = mlObject.getString("url");
                    String nickName = mlObject.getString("nickName");
                    UserInfoBean userInfoBean = new UserInfoBean();
                    userInfoBean.setUserName(userName);
                    userInfoBean.setUrl(url);
                    userInfoBean.setNickName(nickName);
                    userInfoBean.setObjectId(mlObject.getObjectId());
                    GlobalInfo.getInstance().mInfoBean.setValue(userInfoBean);
                    long endTime = System.currentTimeMillis();
                    long det = endTime - mStartTime;
                    if (det > mSplashTime) {
                        mDelay = 0;
                    } else {
                        mDelay = (int) (mSplashTime - det);
                    }
                    toMainActivity();
                } else {
                    SFToast.showToast(R.string.fail_to_get_user_info);
                }
            }
        });
    }
}
