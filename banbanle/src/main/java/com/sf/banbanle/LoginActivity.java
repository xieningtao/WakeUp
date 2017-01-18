package com.sf.banbanle;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.basesmartframe.baseui.BaseActivity;
import com.maxleap.GetCallback;
import com.maxleap.LogInCallback;
import com.maxleap.MLObject;
import com.maxleap.MLQuery;
import com.maxleap.MLQueryManager;
import com.maxleap.MLUser;
import com.maxleap.MLUserManager;
import com.maxleap.exception.MLException;
import com.sf.banbanle.bean.LoginInfo;
import com.sf.banbanle.bean.UserInfoBean;
import com.sf.banbanle.config.BBLConstant;
import com.sf.banbanle.config.GlobalInfo;
import com.sf.loglib.L;
import com.sf.utils.baseutil.SFToast;
import com.sflib.CustomView.baseview.EditTextClearDroidView;

/**
 * Created by NetEase on 2016/11/29 0029.
 */

public class LoginActivity extends BaseActivity {

    private Button mLoginBt;
    private EditTextClearDroidView mUserName;
    private EditTextClearDroidView mPwd;
    private TextView mRegisterTv;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ny_login_activity);
        initView();
    }

    private void initView() {
        mLoginBt = (Button) findViewById(R.id.login_bt);
        mUserName = (EditTextClearDroidView) findViewById(R.id.login_atv);
        mPwd = (EditTextClearDroidView) findViewById(R.id.pwd_cdv);
        mRegisterTv = (TextView) findViewById(R.id.register_tv);

        mLoginBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(mUserName.getEditText().getText())) {
                    SFToast.showToast(R.string.user_name_input_hint);
                    return;
                }

                if (TextUtils.isEmpty(mPwd.getEditText().getText())) {
                    SFToast.showToast(R.string.pwd_input_hint);
                    return;
                }
                if (mProgressDialog == null) {
                    mProgressDialog = new ProgressDialog(LoginActivity.this);
                }
                mProgressDialog.show();
                String userName = mUserName.getEditText().getText().toString();
                String pwd = mPwd.getEditText().getText().toString();
                doLogin(userName, pwd);
            }
        });
        mRegisterTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });
        mPwd.getEditText().setHint(R.string.pwd_input_hint);
        mPwd.getEditText().addTextChangedListener(mTextWatcher);
    }

    private void doLogin(final String userName, final String pwd) {
        MLUserManager.logInInBackground(userName, pwd, new LogInCallback() {
            public void done(MLUser user, MLException e) {
                if (mProgressDialog != null) {
                    mProgressDialog.dismiss();
                }
                if (user != null) {
                    // 登录成功
                    SFToast.showToast(R.string.login_success_tip);
                    LoginInfo loginInfo = new LoginInfo(userName, pwd);
                    GlobalInfo.getInstance().mLoginInfo.setValue(loginInfo, BBLConstant.LOGIN_INFO);
                    getUserInfo(userName);
                } else {
                    // 登录失败
                    SFToast.showToast(R.string.login_fail_tip);
                    L.error(TAG, "doLogin exception: " + e);
                }
            }
        });
    }

    private void getUserInfo(final String userName) {
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
                    Intent intent = new Intent(LoginActivity.this, NewActivityHome.class);
                    startActivity(intent);
                    finish();
                } else {
                    SFToast.showToast(R.string.fail_to_get_user_info);
                }
            }
        });
    }

    private TextWatcher mTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            updateLoginState();
        }
    };

    private void updateLoginState() {
        if (!TextUtils.isEmpty(mUserName.getEditText().getText()) && !TextUtils.isEmpty(mPwd.getEditText().getText())) {
            mLoginBt.setEnabled(true);
        } else {
            mLoginBt.setEnabled(false);
        }
    }

}
