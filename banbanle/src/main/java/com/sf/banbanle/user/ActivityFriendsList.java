package com.sf.banbanle.user;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.basesmartframe.baseui.BaseActivity;
import com.sf.banbanle.BaseBBLActivity;
import com.sf.banbanle.R;
import com.sf.banbanle.bean.UserInfoBean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by mac on 16/12/4.
 */

public class ActivityFriendsList extends BaseBBLActivity {

    public static final int ADD_USER_REQUEST = 1001;
    private Button mAddBt;
    private FragmentFriendsList fragmentFriendsList;
    public static final String SELECTED_USER_INFO = "selected_useer_info";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_list);
        mAddBt = (Button) findViewById(R.id.add_bt);
        fragmentFriendsList = (FragmentFriendsList) getFragmentManager().findFragmentById(R.id.friends_list_fg);
        mAddBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<UserInfoBean> userInfoBeanList = fragmentFriendsList.getAllSelectedItems();
                Intent intent = new Intent();
                intent.putExtra(SELECTED_USER_INFO, (Serializable) userInfoBeanList);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
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
        titleTv.setText("好友列表");
        ImageView plusIv = (ImageView) rootView.findViewById(R.id.plus_iv);
        plusIv.setVisibility(View.GONE);
        TextView finishTv = (TextView) rootView.findViewById(R.id.action_right_tv);
        finishTv.setText("添加用户");
        finishTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ActivityFriendsList.this, ActivityAddUser.class);
                startActivityForResult(intent, ADD_USER_REQUEST);
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADD_USER_REQUEST && resultCode == RESULT_OK) {
            fragmentFriendsList.onActivityResult(requestCode, resultCode, data);
        }
    }
}
