package com.sf.banbanle;

import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.Notification;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.baidu.android.pushservice.CustomPushNotificationBuilder;
import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
import com.basesmartframe.baseui.BaseActivity;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.sf.banbanle.bean.LoginInfo;
import com.sf.banbanle.bean.UserInfoBean;
import com.sf.banbanle.config.BBLConstant;
import com.sf.banbanle.config.BBLMessageId;
import com.sf.banbanle.config.GlobalInfo;
import com.sf.banbanle.user.ActivityProfile;
import com.sf.utils.baseutil.SFManifestUtil;
import com.sflib.reflection.core.SFIntegerMessage;
import com.sflib.reflection.core.ThreadId;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mac on 16/12/18.
 */

public class NewActivityHome extends BaseBBLActivity implements AdapterView.OnItemClickListener {

    private DrawerLayout mDrawer;
    private ListView mLeftDrawerLv;
    private View mLeftDrawerView;
    private ImageView mPhotoIv;

    private final int mIcon[] = {R.drawable.message_icon, R.drawable.profile_icon, R.drawable.setting_icon, R.drawable.version_icon};
    private final int mContent[] = {R.string.main_menu_task, R.string.main_menu_profile, R.string.main_menu_setting, R.string.main_menu_version};

    private List<Fragment> mFragments = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_home);

        mDrawer = (DrawerLayout) findViewById(R.id.content_dl);
        mLeftDrawerLv = (ListView) findViewById(R.id.left_drawer);
        mLeftDrawerView = findViewById(R.id.left_drawer_rl);
        mPhotoIv = (ImageView) findViewById(R.id.photo_iv);


        mPhotoIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NewActivityHome.this, ActivityProfile.class);
                startActivity(intent);
            }
        });
        mLeftDrawerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawer.closeDrawer(mLeftDrawerView);
            }
        });
        GlobalInfo.getInstance().mLoginInfo.restoreData(BBLConstant.LOGIN_INFO, LoginInfo.class);
        initBDPushNotification();

        mLeftDrawerLv.setAdapter(new DrawerAdapter());
        mLeftDrawerLv.setOnItemClickListener(this);
        getFragmentManager().beginTransaction().replace(R.id.content_fl, new FragmentHome()).commit();
        getFragmentManager().executePendingTransactions();
        loadPhoto();
    }

    @Override
    protected void onCustomActionBarCreated(View rootView) {
        rootView.setBackgroundColor(getResources().getColor(R.color.actionbar_blue));
        ImageView iconIv = (ImageView) rootView.findViewById(R.id.icon_action_iv);
        iconIv.setImageResource(R.drawable.home_menu_icon);
        ImageView plusIv = (ImageView) rootView.findViewById(R.id.plus_iv);
        rootView.findViewById(R.id.action_right_tv).setVisibility(View.GONE);
        plusIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NewActivityHome.this, ActivityEditContent.class);
                startActivity(intent);
            }
        });
        iconIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDrawer.isDrawerOpen(mLeftDrawerView)) {
                    mDrawer.closeDrawer(mLeftDrawerView);
                } else {
                    mDrawer.openDrawer(mLeftDrawerView);
                }
            }
        });

        TextView titleTv = (TextView) rootView.findViewById(R.id.txt_action_tv);
        titleTv.setText("帮帮乐");
    }


    private void loadPhoto() {
        UserInfoBean userInfoBean = GlobalInfo.getInstance().mInfoBean.getValue();
        if (userInfoBean != null) {
            ImageLoader.getInstance().displayImage("http://" + userInfoBean.getUrl(), mPhotoIv);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                mDrawer.openDrawer(mLeftDrawerView);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initBDPushNotification() {
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
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Fragment fragment = null;
        if (position == 0) {

        } else if (position == 1) {

        } else if (position == 2) {

        } else {
            Intent intent = new Intent(this, ActivityVersion.class);
            startActivity(intent);
        }
        mLeftDrawerLv.setItemChecked(position, true);
        mDrawer.closeDrawer(mLeftDrawerView);

    }

    private void hideAllFragmentsExcept(Fragment fragment, String tag) {
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        for (Fragment fg : mFragments) {
            if (fg != fragment && fg.isVisible() && fg.isAdded()) {
                fragmentTransaction.hide(fg);
                fragmentTransaction.commit();
            }
        }
    }


    private void addFragment(Fragment fragment, String tag) {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.add(R.id.content_fl, fragment, tag);
        ft.commit();
        getFragmentManager().executePendingTransactions();
    }

    class DrawerAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mContent.length;
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(NewActivityHome.this).inflate(R.layout.left_drawer_item, null);
            }
            ImageView iconIv = (ImageView) convertView.findViewById(R.id.left_drawer_icon_iv);
            TextView contentTv = (TextView) convertView.findViewById(R.id.left_drawer_content_tv);
            iconIv.setImageResource(mIcon[position]);
            contentTv.setText(mContent[position]);
            return convertView;
        }
    }

    @SFIntegerMessage(messageId = BBLMessageId.LOG_OUT, theadId = ThreadId.MainThread)
    public void onLogOut() {
        finish();
    }
}
