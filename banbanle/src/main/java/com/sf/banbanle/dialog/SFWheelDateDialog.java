package com.sf.banbanle.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sf.banbanle.R;
import com.sflib.CustomView.wheel.AbstractWheelAdapter;
import com.sflib.CustomView.wheel.WheelView;
import com.sflib.CustomView.wheel.WheelViewAdapter;

import java.util.Calendar;

/**
 * Created by NetEase on 2016/12/6 0006.
 */

public class SFWheelDateDialog extends Dialog {
    private WheelView mFirstWv, mSecondWv, mThirdWv;
    private WheelViewAdapter mFirstAdapter, mSecondAdapter, mThirdAdapter;
    private String mWeekDayStr[];
    private String mMonthStr[];
    private long mCurMillions;
    private onWheelDateDialogClick mDateDialogClick;

    public static interface onWheelDateDialogClick {
        void onCancelClick();

        void onSureClick(long millions);
    }

    public SFWheelDateDialog(Context context) {
        super(context, R.style.base_dialog);
        initView();
    }

    public SFWheelDateDialog(Context context, int theme) {
        super(context, theme);
        initView();
    }

    protected SFWheelDateDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        initView();
    }

    public void setDateDialogClick(onWheelDateDialogClick dateDialogClick) {
        mDateDialogClick = dateDialogClick;
    }

    private void initView() {
        View rootView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_wheel_date, null);
        setContentView(rootView);
        mFirstWv = (WheelView) rootView.findViewById(R.id.first_wv);
        mSecondWv = (WheelView) rootView.findViewById(R.id.second_wv);
        mThirdWv = (WheelView) rootView.findViewById(R.id.third_wv);
        mWeekDayStr = getContext().getResources().getStringArray(R.array.week_day_txt);
        mMonthStr = getContext().getResources().getStringArray(R.array.month_txt);
        mFirstAdapter = new DateWheelAdapter(getContext(), 365, 0);
        mSecondAdapter = new DateWheelAdapter(getContext(), 24, 1);
        mThirdAdapter = new DateWheelAdapter(getContext(), 60, 2);
        mFirstWv.setCyclic(true);
        mSecondWv.setCyclic(true);
        mThirdWv.setCyclic(true);
        mFirstWv.setViewAdapter(mFirstAdapter);
        mSecondWv.setViewAdapter(mSecondAdapter);
        mThirdWv.setViewAdapter(mThirdAdapter);
        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        rootView.findViewById(R.id.cancel_bt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if (mDateDialogClick != null) {
                    mDateDialogClick.onCancelClick();
                }
            }
        });

        rootView.findViewById(R.id.sure_bt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                int firstItem = mFirstWv.getCurrentItem();
                int secondItem = mSecondWv.getCurrentItem();
                int thirdItem = mThirdWv.getCurrentItem()+1;
                long total = firstItem * 1000 * 24 * 60 * 60 + secondItem * 1000 * 60 * 60 + thirdItem * 1000 * 60 + mCurMillions;
                if (mDateDialogClick != null) {
                    mDateDialogClick.onSureClick(total);
                }
            }
        });
    }

    public void setCurrentItem(long millionSeconds) {
        Calendar curCalendar = Calendar.getInstance();
        mCurMillions = curCalendar.getTimeInMillis();
        long det = Math.abs(millionSeconds - mCurMillions);
        int day = (int) (det / (1000 * 60 * 60 * 24));
        int hour = (int) (det / (1000 * 60 * 60) - day * 24);
        int minute = (int) (det / (1000 * 60) - day * 24 * 60 - hour * 60);
        mFirstWv.setCurrentItem(day);
        mSecondWv.setCurrentItem(hour);
        mThirdWv.setCurrentItem(minute);
    }

    private String getWeekDayStr(int position) {
        if (position >= 0 && position < mWeekDayStr.length) {
            return mWeekDayStr[position];
        }
        return "";
    }


    class DateWheelAdapter extends AbstractWheelAdapter {
        private final Context mContext;
        private LayoutInflater mLayoutInflater;
        private int mCount;
        private int mIndex = 0;
        private Calendar mCalendar;
        private long mCurMillis;

        public DateWheelAdapter(Context context, int count, int index) {
            this.mContext = context;
            this.mCount = count;
            this.mIndex = index;
            this.mCalendar = Calendar.getInstance();
            mCurMillis = mCalendar.getTimeInMillis();
            mLayoutInflater = LayoutInflater.from(context);
        }


        @Override
        public int getItemsCount() {
            return mCount;
        }

        public String getCurStr(int position) {
            if (mIndex == 0) {
                mCalendar.setTimeInMillis(mCurMillis);
                mCalendar.add(Calendar.DAY_OF_MONTH, position);
                int month = mCalendar.get(Calendar.MONTH) + 1;
                int day = mCalendar.get(Calendar.DAY_OF_MONTH);
                int weekDay = mCalendar.get(Calendar.DAY_OF_WEEK);
                return month + "月" + day + "日" + " " + getWeekDayStr(weekDay - 1);
            } else if (mIndex == 1) {
                mCalendar.setTimeInMillis(mCurMillis);
                mCalendar.add(Calendar.HOUR_OF_DAY, position);
                int hour = mCalendar.get(Calendar.HOUR_OF_DAY);
                return String.format("%1$,02d", hour) + "时";
            } else {
                mCalendar.setTimeInMillis(mCurMillis);
                mCalendar.add(Calendar.MINUTE, position);
                int minute = mCalendar.get(Calendar.MINUTE) + 1;
                return String.format("%1$,02d", minute) + "分";
            }
        }

        @Override
        public View getItem(int i, View view, ViewGroup viewGroup) {
            if (view == null) {
                view = mLayoutInflater.inflate(R.layout.date_wheel_item, null);
            }
            String content = getCurStr(i);
            TextView contentTv = (TextView) view.findViewById(R.id.date_content_tv);
            contentTv.setText(content);
            return view;
        }

        @Override
        public void setTextColor(View view, boolean current) {
            int color = current ? R.color.txt_main_color : R.color.txt_main_hint_color;
            TextView textView = (TextView) view.findViewById(R.id.date_content_tv);
            if (textView != null) {
                textView.setTextColor(mContext.getResources().getColor(color));
            }
        }

        @Override
        public View getEmptyItem(View convertView, ViewGroup parent) {
            return super.getEmptyItem(convertView, parent);
        }
    }
}
