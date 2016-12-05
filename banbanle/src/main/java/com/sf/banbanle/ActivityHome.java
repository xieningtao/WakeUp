package com.sf.banbanle;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.Notification;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

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
    private ViewPager mViewPager;
    private Button mCreateTaskBt, mAcceptTaskBt, mAssingMe;

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
        mViewPager = (ViewPager) findViewById(R.id.task_vp);
        mCreateTaskBt = (Button) findViewById(R.id.create_task_bt);
        mAcceptTaskBt = (Button) findViewById(R.id.accept_task_bt);
        mAssingMe = (Button) findViewById(R.id.assign_me_bt);
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    mCreateTaskBt.setSelected(true);
                    mAcceptTaskBt.setSelected(false);
                    mAssingMe.setSelected(false);
                } else if (position == 1) {
                    mCreateTaskBt.setSelected(false);
                    mAcceptTaskBt.setSelected(false);
                    mAssingMe.setSelected(true);
                } else {
                    mCreateTaskBt.setSelected(false);
                    mAcceptTaskBt.setSelected(true);
                    mAssingMe.setSelected(false);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mViewPager.setAdapter(new AlarmAdapter(getFragmentManager()));
        mCreateTaskBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewPager.setCurrentItem(0);
            }
        });
        mAcceptTaskBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewPager.setCurrentItem(1);
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
        getMenuInflater().inflate(R.menu.user, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = new Intent(this, ActivityProfile.class);
        startActivity(intent);
        return super.onOptionsItemSelected(item);
    }

    class AlarmAdapter extends FragmentPagerAdapter {


        public AlarmAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = new FragmentAlarmHome();
            Bundle bundle = new Bundle();
            bundle.putInt("position", position);
            fragment.setArguments(bundle);
            return fragment;
        }

        @Override
        public int getCount() {
            return 3;
        }
    }

}
