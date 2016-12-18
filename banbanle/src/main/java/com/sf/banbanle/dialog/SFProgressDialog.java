package com.sf.banbanle.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.sf.banbanle.R;

/**
 * Created by mac on 16/12/18.
 */

public class SFProgressDialog extends Dialog {
    private TextView mLoadingTv;

    public SFProgressDialog(Context context) {
        super(context);
        initView();
    }

    private void initView() {
        setCanceledOnTouchOutside(false);
        setCancelable(false);
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        View rootView = layoutInflater.inflate(R.layout.dialog_progress, null);
        mLoadingTv = (TextView) rootView.findViewById(R.id.progress_txt_tv);
        setContentView(rootView);
    }

    public void setLoadingText(String content) {
        mLoadingTv.setText(content);
    }

    public void setLoadingText(int contentId) {
        mLoadingTv.setText(contentId);
    }
}
