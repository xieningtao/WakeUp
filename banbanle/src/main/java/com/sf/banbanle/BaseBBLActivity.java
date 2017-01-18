package com.sf.banbanle;

import android.app.ActionBar;
import android.os.Bundle;
import android.view.View;
import android.widget.BaseAdapter;

import com.basesmartframe.baseui.BaseActivity;

/**
 * Created by mac on 16/12/25.
 */

abstract public class BaseBBLActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getActionBar().setCustomView(getCustomViewLayoutId());
        onCustomActionBarCreated(getActionBar().getCustomView());
    }

    abstract protected void onCustomActionBarCreated(View rootView);

    protected int getCustomViewLayoutId(){
        return R.layout.action_bar_item;
    }
}
