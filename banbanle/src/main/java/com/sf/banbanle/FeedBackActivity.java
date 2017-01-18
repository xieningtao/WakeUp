package com.sf.banbanle;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.umeng.fb.FeedbackAgent;

/**
 * Created by mac on 16/12/25.
 */

public class FeedBackActivity extends BaseBBLActivity {

    private FeedbackAgent mAgent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAgent=new FeedbackAgent(this);
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
        titleTv.setText("反馈");
        ImageView plusIv = (ImageView) rootView.findViewById(R.id.plus_iv);
        plusIv.setVisibility(View.GONE);
        TextView finishTv = (TextView) rootView.findViewById(R.id.action_right_tv);
        finishTv.setVisibility(View.GONE);
    }


}
