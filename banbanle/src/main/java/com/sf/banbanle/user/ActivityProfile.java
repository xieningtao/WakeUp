package com.sf.banbanle.user;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.basesmartframe.baseui.BaseActivity;
import com.basesmartframe.pickphoto.ActivityFragmentContainer;
import com.basesmartframe.pickphoto.ImageBean;
import com.basesmartframe.pickphoto.PickPhotosFragment;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.maxleap.GetCallback;
import com.maxleap.MLDataManager;
import com.maxleap.MLFile;
import com.maxleap.MLFileManager;
import com.maxleap.MLObject;
import com.maxleap.MLQuery;
import com.maxleap.MLQueryManager;
import com.maxleap.MLUser;
import com.maxleap.SaveCallback;
import com.maxleap.exception.MLException;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.sf.banbanle.BaseBBLActivity;
import com.sf.banbanle.FragmentHome;
import com.sf.banbanle.LoginActivity;
import com.sf.banbanle.R;
import com.sf.banbanle.bean.UserInfoBean;
import com.sf.banbanle.config.BBLConstant;
import com.sf.banbanle.config.BBLMessageId;
import com.sf.banbanle.config.GlobalInfo;
import com.sf.loglib.L;
import com.sf.utils.baseutil.BitmapHelp;
import com.sf.utils.baseutil.SFToast;
import com.sflib.reflection.core.SFBridgeManager;

import java.io.ByteArrayOutputStream;
import java.util.List;

/**
 * Created by mac on 16/12/4.
 */

public class ActivityProfile extends BaseBBLActivity {
    public static final int GET_PHOTO_REQUEST = 102;

    public static final int MODIFY_NICKNAME = 103;
    public static final String CHANNEL_ACTIVITY = "channel_activity";
    public static final String CHANNEL_REGISTER = "channel_register";
    public static final String CHANNEL_HOME = "channel_home";

    public static final String USER_INFO_ID = "user_info_id";


    private String mUserInfoObjectId = "";
    private View mPhotoView;
    private View mNickNameView;

    private ImageView mPhotoIv;
    private TextView mNickNameTv;
    private TextView mAccountTv;

    private Button mLogoutBt;

    private String mUrl = "";
    private MLObject mCurUserInfo;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mPhotoView = findViewById(R.id.photo_rl);
        mNickNameView = findViewById(R.id.nickName_rl);

        mPhotoIv = (ImageView) findViewById(R.id.photo_iv);
        mNickNameTv = (TextView) findViewById(R.id.nickName_tv);
        mAccountTv = (TextView) findViewById(R.id.account_tv);

        mLogoutBt = (Button) findViewById(R.id.logout_bt);
        mPhotoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putInt(PickPhotosFragment.MAX_IMAGE_NUM, 1);
                Intent intent = new Intent(ActivityProfile.this, ActivityFragmentContainer.class);
                intent.putExtra(ActivityFragmentContainer.BUNDLE_CONTAINER, bundle);
                intent.putExtra(ActivityFragmentContainer.FRAGMENT_CLASS_NAME, PickPhotosFragment.class.getName());
                startActivityForResult(intent, GET_PHOTO_REQUEST);
            }
        });

        mNickNameView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nickName = "";
                if (!TextUtils.isEmpty(mNickNameTv.getText())) {
                    nickName = mNickNameTv.getText().toString();
                }
                Intent intent = new Intent(ActivityProfile.this, ActivityProfileEditor.class);
                intent.putExtra(ActivityProfileEditor.MODIFY_CONTENT, nickName);
                startActivityForResult(intent, MODIFY_NICKNAME);
            }
        });
        mLogoutBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SFBridgeManager.send(BBLMessageId.LOG_OUT);
                logOut();
                Intent intent = new Intent(ActivityProfile.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
        getProfile();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
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
        titleTv.setText("个人中心");
        ImageView plusIv = (ImageView) rootView.findViewById(R.id.plus_iv);
        plusIv.setVisibility(View.GONE);
        TextView finishTv = (TextView) rootView.findViewById(R.id.action_right_tv);
        finishTv.setText("完成");
        finishTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProfile();
            }
        });
    }


    private void logOut() {
        MLUser.logOut();
        GlobalInfo.getInstance().mLoginInfo.setValue(null, BBLConstant.LOGIN_INFO);
    }

    private void updateProfile() {
        if (!TextUtils.isEmpty(mUrl) && !mUrl.equals(mCurUserInfo.getString("url"))) {
            mCurUserInfo.put("url", mUrl);
        }
        if (!TextUtils.isEmpty(mNickNameTv.getText()) && !mNickNameTv.getText().toString().equals(mCurUserInfo.getString("nickName"))) {
            mCurUserInfo.put("nickName", mNickNameTv.getText().toString());
        }
        MLDataManager.saveInBackground(mCurUserInfo, new SaveCallback() {
            @Override
            public void done(MLException e) {
                if (e == null) {
                    SFToast.showToast(R.string.update_profile_success);
                    String channel = getChannel();
                    if (CHANNEL_REGISTER.equals(channel)) {
                        Intent intent = new Intent(ActivityProfile.this, FragmentHome.class);
                        startActivity(intent);
                        finish();
                    } else {
                        finish();
                    }
                } else {
                    SFToast.showToast(R.string.update_profile_fail);
                    L.error(TAG, "updateProfile exception:" + e);
                }
            }
        });
    }

    private void getProfile() {
        MLQuery mlQuery = new MLQuery("UserInfo");
        String objectId = getUserInfoObjectId();
        MLQueryManager.getInBackground(mlQuery, objectId, new GetCallback() {
            @Override
            public void done(MLObject mlObject, MLException e) {
                if (e == null) {
                    mCurUserInfo = mlObject;
                    updateUserInfo(mlObject);
                } else {
                    L.error(TAG, "getProfile,exception: " + e);
                }
            }
        });
    }

    private void updateUserInfo(MLObject mlObject) {
        UserInfoBean userInfoBean = GlobalInfo.getInstance().mInfoBean.getValue();
        if (userInfoBean == null) {
            userInfoBean = new UserInfoBean();
        }
        userInfoBean.setObjectId(mlObject.getObjectId());
        userInfoBean.setNickName(mlObject.getString("nickName"));
        userInfoBean.setUrl(mlObject.getString("url"));
        userInfoBean.setUserName(mlObject.getString("userName"));
        GlobalInfo.getInstance().mInfoBean.setValue(userInfoBean);

        ImageLoader.getInstance().displayImage("http://" + userInfoBean.getUrl(), mPhotoIv);
        mNickNameTv.setText(userInfoBean.getNickName());
        mAccountTv.setText(userInfoBean.getUserName());
    }

    private void upLoadFile(String filePath) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        Bitmap bitmap = BitmapHelp.decodeFileInSize(filePath, mPhotoIv.getWidth(), mPhotoIv.getHeight());
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte image[] = stream.toByteArray();
        final MLFile mlFile = new MLFile("photo.png", image);
        MLFileManager.saveInBackground(mlFile, new SaveCallback() {
            @Override
            public void done(MLException e) {
                if (e == null) {
                    mUrl = mlFile.getUrl();//上传完成后，得到该文件的下载地址
                    L.info(TAG, "upload photo image: " + mUrl);
                } else {
                    L.error(TAG, "upload photo failed,exception: " + e);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GET_PHOTO_REQUEST && resultCode == RESULT_OK) {
            if (data != null) {
                List<ImageBean> imageBeenList = (List<ImageBean>) data.getSerializableExtra(PickPhotosFragment.CHOOSE_PIC);
                if (imageBeenList != null && !imageBeenList.isEmpty()) {
                    ImageBean imageBean = imageBeenList.get(0);
                    ImageLoader.getInstance().displayImage("file://" + imageBean.getPath(), mPhotoIv);
                    upLoadFile(imageBean.getPath());
                }
            }
        } else if (requestCode == MODIFY_NICKNAME && resultCode == RESULT_OK) {
            if (data != null) {
                String content = data.getStringExtra(ActivityProfileEditor.MODIFY_CONTENT);
                mNickNameTv.setText(content);
            }
        }
    }

    private String getUserInfoObjectId() {
        UserInfoBean userInfoBean = GlobalInfo.getInstance().mInfoBean.getValue();
        if (userInfoBean != null) {
            return userInfoBean.getObjectId();
        }
        return "";
    }

    private String getChannel() {
        Intent intent = getIntent();
        if (intent != null) {
            return intent.getStringExtra(CHANNEL_ACTIVITY);
        }
        return "";
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("ActivityProfile Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }
}
