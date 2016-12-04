package com.sf.banbanle.user;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.basesmartframe.baseui.BaseActivity;
import com.sf.banbanle.R;
import com.sf.banbanle.bean.UserInfoBean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by mac on 16/12/4.
 */

public class ActivityFriendsList extends BaseActivity {

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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_user, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = new Intent(this, ActivityAddUser.class);
        startActivityForResult(intent, ADD_USER_REQUEST);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADD_USER_REQUEST && resultCode == RESULT_OK) {

        }
    }
}
