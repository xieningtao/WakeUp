package com.sf.banbanle;

import android.app.Notification;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.baidu.android.pushservice.CustomPushNotificationBuilder;
import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
import com.basesmartframe.baseui.BaseActivity;
import com.sf.banbanle.bean.LoginInfo;
import com.sf.banbanle.config.BBLConstant;
import com.sf.banbanle.config.GlobalInfo;
import com.sf.banbanle.user.ActivityProfile;
import com.sf.utils.baseutil.SFManifestUtil;

/**
 * Created by NetEase on 2016/11/30 0030.
 */

public class ActivityHome extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        findViewById(R.id.add_alarm_iv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ActivityHome.this, ActivityEditContent.class);
                startActivity(intent);
            }
        });
        GlobalInfo.getInstance().mLoginInfo.restoreData(BBLConstant.LOGIN_INFO, LoginInfo.class);
        PushManager.startWork(getApplicationContext(), PushConstants.LOGIN_TYPE_API_KEY,
                SFManifestUtil.getMetaValue(this, "api_key"));
        Resources resource = this.getResources();
        String pkgName = this.getPackageName();
        CustomPushNotificationBuilder cBuilder = new CustomPushNotificationBuilder(
                resource.getIdentifier(
                        "notification_custom_builder", "layout", pkgName),
                resource.getIdentifier("notification_icon", "id", pkgName),
                resource.getIdentifier("notification_title", "id", pkgName),
                resource.getIdentifier("notification_text", "id", pkgName));
        cBuilder.setNotificationFlags(Notification.FLAG_AUTO_CANCEL);
        cBuilder.setNotificationDefaults(Notification.DEFAULT_SOUND
                | Notification.DEFAULT_VIBRATE);
        cBuilder.setStatusbarIcon(R.drawable.app_icon);
        cBuilder.setLayoutDrawable(R.drawable.app_icon);
        cBuilder.setNotificationSound(Uri.withAppendedPath(
                MediaStore.Audio.Media.INTERNAL_CONTENT_URI, "6").toString());
        PushManager.setNotificationBuilder(this, 1, cBuilder);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.user,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent=new Intent(this, ActivityProfile.class);
        startActivity(intent);
        return super.onOptionsItemSelected(item);
    }
}
