package com.sf.banbanle;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.basesmartframe.baseui.BaseActivity;
import com.maxleap.MLDataManager;
import com.maxleap.MLObject;
import com.maxleap.MLUser;
import com.maxleap.MLUserManager;
import com.maxleap.SaveCallback;
import com.maxleap.SignUpCallback;
import com.maxleap.exception.MLException;
import com.sf.banbanle.bean.LoginInfo;
import com.sf.banbanle.bean.UserInfoBean;
import com.sf.banbanle.config.BBLConstant;
import com.sf.banbanle.config.GlobalInfo;
import com.sf.banbanle.user.ActivityProfile;
import com.sf.loglib.L;
import com.sf.utils.baseutil.SFToast;
import com.sflib.CustomView.baseview.EditTextClearDroidView;

/**
 * Created by NetEase on 2016/11/29 0029.
 */

public class RegisterActivity extends BaseBBLActivity {
    private EditTextClearDroidView mUserName;
    private EditTextClearDroidView mPwd;

    private Button mRegister;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ny_register_activity);
        initial();
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
        titleTv.setText("注册");
        ImageView plusIv = (ImageView) rootView.findViewById(R.id.plus_iv);
        plusIv.setVisibility(View.GONE);
        TextView finishTv = (TextView) rootView.findViewById(R.id.action_right_tv);
        finishTv.setVisibility(View.GONE);
    }

    private void initial() {
        mUserName = (EditTextClearDroidView) findViewById(R.id.phoneNum_atv);
        mPwd = (EditTextClearDroidView) findViewById(R.id.pwd_cdv);
        mRegister = (Button) findViewById(R.id.register_bt);
        mRegister.setOnClickListener(new View.OnClickListener() {
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
                    mProgressDialog = new ProgressDialog(RegisterActivity.this);
                }
                mProgressDialog.show();
                String userName = mUserName.getEditText().getText().toString();
                String pwd = mPwd.getEditText().getText().toString();
                doRegister(userName, pwd);
            }
        });
    }

    private void createUserInfo(String userName) {
        final MLObject mlObject = new MLObject("UserInfo");
        mlObject.put("userName", userName);
        MLDataManager.saveInBackground(mlObject, new SaveCallback() {
            @Override
            public void done(MLException e) {
                if (e != null) {
                    L.error(TAG, "createUserInfo exception: " + e);
                } else {
                    UserInfoBean userInfoBean = GlobalInfo.getInstance().mInfoBean.getValue();
                    if (userInfoBean == null) {
                        userInfoBean = new UserInfoBean();
                    }
                    userInfoBean.setObjectId(mlObject.getObjectId());
                    GlobalInfo.getInstance().mInfoBean.setValue(userInfoBean);
                    Intent intent = new Intent(RegisterActivity.this, ActivityProfile.class);
                    intent.putExtra(ActivityProfile.CHANNEL_ACTIVITY, ActivityProfile.CHANNEL_REGISTER);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

    private void doRegister(final String userName, final String pwd) {
        MLUser user = new MLUser();
        user.setUserName(userName);
        user.setPassword(pwd);
        MLUserManager.signUpInBackground(user, new SignUpCallback() {
            public void done(MLException e) {
                if (mProgressDialog != null) {
                    mProgressDialog.dismiss();
                }
                if (e == null) {
                    // 注册成功
                    SFToast.showToast(R.string.register_success_tip);
                    LoginInfo loginInfo = new LoginInfo(userName, pwd);
                    GlobalInfo.getInstance().mLoginInfo.setValue(loginInfo, BBLConstant.LOGIN_INFO);
                    createUserInfo(userName);
                } else {
                    // 注册失败
                    SFToast.showToast(R.string.register_fail_tip);
                    L.error(TAG, "doRegister exception: " + e);
                }
            }
        });
    }
}
