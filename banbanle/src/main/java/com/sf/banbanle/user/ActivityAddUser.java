package com.sf.banbanle.user;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.basesmartframe.baseui.BaseActivity;
import com.basesmartframe.baseui.BaseFragment;
import com.maxleap.GetCallback;
import com.maxleap.MLDataManager;
import com.maxleap.MLObject;
import com.maxleap.MLQuery;
import com.maxleap.MLQueryManager;
import com.maxleap.SaveCallback;
import com.maxleap.exception.MLException;
import com.maxleap.im.entity.FriendInfo;
import com.nostra13.universalimageloader.utils.L;
import com.sf.banbanle.R;
import com.sf.banbanle.config.GlobalInfo;
import com.sf.httpclient.newcore.SFTask;
import com.sf.utils.baseutil.SFToast;
import com.sflib.CustomView.baseview.EditTextClearDroidView;

/**
 * Created by mac on 16/12/4.
 */

public class ActivityAddUser extends BaseActivity {

    private EditTextClearDroidView mView;
    private Button mAddUserBt;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_add_user);
        mView = (EditTextClearDroidView) findViewById(R.id.phoneNum_atv);
        mAddUserBt = (Button) findViewById(R.id.add_user_bt);

        mView.getEditText().setHint(R.string.user_name_input_hint);

        mAddUserBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(mView.getEditText().getText())) {
                    SFToast.showToast(R.string.user_name_input_hint);
                }
                String userName = mView.getEditText().getText().toString();
                addUser(userName);
            }
        });
    }

    private void addUser(final String userName) {
        final String curUserName = GlobalInfo.getInstance().mLoginInfo.getValue().userName;
        MLQuery mlQuery = new MLQuery("UserRelation");
        mlQuery.whereEqualTo("userName", curUserName);
        mlQuery.whereEqualTo("friendName", userName);
        MLQueryManager.getFirstInBackground(mlQuery, new GetCallback() {
            @Override
            public void done(MLObject mlObject, MLException e) {
                if (e == null) {
                    SFToast.showToast(R.string.add_exist_user);
                } else {
                    if (e.getCode() == MLException.OBJECT_NOT_FOUND) {
                        checkUserInfo(curUserName, userName);
                    } else {
                        L.e(TAG, "addUser exception: " + e);
                    }
                }
            }
        });
    }

    private void checkUserInfo(final String curUserName, final String userName) {
        MLQuery userInfoQuery = new MLQuery("UserInfo");
        userInfoQuery.whereEqualTo("userName", userName);
        MLQueryManager.getFirstInBackground(userInfoQuery, new GetCallback() {
            @Override
            public void done(MLObject mlObject, MLException e) {
                if (e != null) {
                    SFToast.showToast(R.string.user_not_exist);
                } else {
                    saveRelation(curUserName, userName);
                }
            }
        });
    }

    private void saveRelation(String curUserName, String userName) {
        MLObject object = new MLObject("UserRelation");
        object.put("userName", curUserName);
        object.put("friendName", userName);
        MLDataManager.saveInBackground(object, new SaveCallback() {
            @Override
            public void done(MLException e) {
                if (e == null) {
                    SFToast.showToast(R.string.add_user_success);
                    setResult(RESULT_OK);
                    finish();
                } else {
                    SFToast.showToast(R.string.add_user_fail);
                    L.e(TAG, "addUser exception: " + e);
                }
            }
        });
    }
}
