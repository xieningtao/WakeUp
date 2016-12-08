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
    private Calendar mOriginCalendar;
    private long mOriginTime;
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
        mOriginCalendar = Calendar.getInstance();
        //5年前
        mOriginCalendar.add(Calendar.YEAR, -5);
        mOriginTime = mOriginCalendar.getTimeInMillis();
        mFirstAdapter = new DateWheelAdapter(getContext(), Integer.MAX_VALUE, 0);
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
                int hour = mSecondWv.getCurrentItem();
                int minute = mThirdWv.getCurrentItem();
                mOriginCalendar.set(Calendar.HOUR_OF_DAY, hour);
                mOriginCalendar.set(Calendar.MINUTE, minute);
                if (mDateDialogClick != null) {
                    mDateDialogClick.onSureClick(mOriginCalendar.getTimeInMillis());
                }
            }
        });
    }

    public void setCurrentItem(long millionSeconds) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(millionSeconds);
        int detDay = (int) ((calendar.getTimeInMillis() -mOriginTime) / (1000 * 60 * 60 * 24F));
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        mFirstWv.setCurrentItem(detDay);
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

        public DateWheelAdapter(Context context, int count, int index) {
            this.mContext = context;
            this.mCount = count;
            this.mIndex = index;
            mLayoutInflater = LayoutInflater.from(context);
        }


        @Override
        public int getItemsCount() {
            return mCount;
        }

        public String getCurStr(int position) {
            if (mIndex == 0) {
                mOriginCalendar.setTimeInMillis(mOriginTime);
                mOriginCalendar.add(Calendar.DAY_OF_YEAR, position);
                int month = mOriginCalendar.get(Calendar.MONTH) + 1;
                int day = mOriginCalendar.get(Calendar.DAY_OF_MONTH);
                int weekDay = mOriginCalendar.get(Calendar.DAY_OF_WEEK);
                return month + "月" + day + "日" + " " + getWeekDayStr(weekDay - 1);
            } else if (mIndex == 1) {
                return String.format("%1$,02d", position) + "时";
            } else {
                return String.format("%1$,02d", position) + "分";
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
