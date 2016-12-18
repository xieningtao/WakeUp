package com.sf.banbanle;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.basesmartframe.baseui.BaseFragment;
import com.sf.banbanle.config.BBLMessageId;
import com.sf.banbanle.user.ActivityProfile;
import com.sflib.reflection.core.SFIntegerMessage;
import com.sflib.reflection.core.ThreadId;

/**
 * Created by NetEase on 2016/11/30 0030.
 */

public class FragmentHome extends BaseFragment {
    private ViewPager mViewPager;
    private Button mCreateTaskBt, mAcceptTaskBt, mAssingMe;
    public static final String PAGE_INDEX = "page_index";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_home, null);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
    }

    private void initView(View rootView) {
        rootView.findViewById(R.id.add_alarm_iv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ActivityEditContent.class);
                startActivity(intent);
            }
        });
        mViewPager = (ViewPager) rootView.findViewById(R.id.task_vp);
        mCreateTaskBt = (Button) rootView.findViewById(R.id.create_task_bt);
        mAcceptTaskBt = (Button) rootView.findViewById(R.id.accept_task_bt);
        mAssingMe = (Button) rootView.findViewById(R.id.assign_me_bt);
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
                mViewPager.setCurrentItem(2);
            }
        });
        mAssingMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewPager.setCurrentItem(1);
            }
        });

        mCreateTaskBt.setSelected(true);
//        dispatchPage(getIntent());
    }


//    @Override
//    protected void onNewIntent(Intent intent) {
//        super.onNewIntent(intent);
//        dispatchPage(intent);
//    }

    private void dispatchPage(Intent intent) {
        if (intent != null) {
            int pageIndex = intent.getIntExtra(PAGE_INDEX, 0);
            mViewPager.setCurrentItem(pageIndex);
            if (pageIndex == 0) {
                mCreateTaskBt.setSelected(true);
            } else if (pageIndex == 1) {
                mAssingMe.setSelected(true);
            } else {
                mAcceptTaskBt.setSelected(true);
            }
        } else {
            mCreateTaskBt.setSelected(true);
        }
    }

    @SFIntegerMessage(messageId = BBLMessageId.REFRESH_TASK, theadId = ThreadId.MainThread)
    public void onTaskRefresh(int index) {
        mViewPager.setCurrentItem(index);
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.user, menu);
//        return true;
//    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        Intent intent = new Intent(this, ActivityProfile.class);
//        startActivity(intent);
//        return super.onOptionsItemSelected(item);
//    }

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
