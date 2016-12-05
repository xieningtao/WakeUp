package com.sf.banbanle;

import android.content.Intent;
import android.os.Bundle;

import com.basesmartframe.baseui.BaseActivity;
import com.maxleap.MLUser;
import com.sflib.reflection.core.ThreadHelp;

/**
 * Created by NetEase on 2016/12/1 0001.
 */

public class ActivitySplash extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        ThreadHelp.runInMain(new Runnable() {
            @Override
            public void run() {
                Intent intent = null;
                MLUser user = MLUser.getCurrentUser();
//                if (user != null) {
//                    intent = new Intent(ActivitySplash.this, ActivityHome.class);
//                } else {
//                    intent = new Intent(ActivitySplash.this, LoginActivity.class);
//                }
                intent = new Intent(ActivitySplash.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        }, 2000);
    }
}
