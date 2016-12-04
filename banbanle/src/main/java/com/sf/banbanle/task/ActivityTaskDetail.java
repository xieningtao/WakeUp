package com.sf.banbanle.task;

import android.os.Bundle;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.basesmartframe.baseui.BaseActivity;
import com.sf.banbanle.R;

/**
 * Created by mac on 16/12/4.
 */

public class ActivityTaskDetail extends BaseActivity {

    private TextView mTitleTv,mContentTv;
    private ImageView mPhotoIv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTitleTv= (TextView) findViewById(R.id.title_tv);
        mContentTv= (TextView) findViewById(R.id.content_tv);
        mPhotoIv= (ImageView) findViewById(R.id.photo_iv);
    }

    private void getTaskDetail(){

    }
}
