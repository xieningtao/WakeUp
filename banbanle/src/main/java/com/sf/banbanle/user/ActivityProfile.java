package com.sf.banbanle.user;

import android.os.Bundle;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.basesmartframe.baseui.BaseActivity;
import com.sf.banbanle.R;

/**
 * Created by mac on 16/12/4.
 */

public class ActivityProfile extends BaseActivity {

    private View mPhotoView;
    private View mNickNameView;

    private ImageView mPhotoIv;
    private TextView mNickNameTv;
    private TextView mAccountTv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mPhotoView=findViewById(R.id.photo_rl);
        mNickNameView=findViewById(R.id.nickName_rl);

        mPhotoIv= (ImageView) findViewById(R.id.photo_iv);
        mNickNameTv= (TextView) findViewById(R.id.nickName_tv);
        mAccountTv= (TextView) findViewById(R.id.account_tv);

        mPhotoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        mNickNameView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}
