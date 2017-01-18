package com.sf.banbanle;

import android.os.Bundle;
import android.view.View;

import com.basesmartframe.baseui.BasePullListFragment;
import com.sf.utils.baseutil.UnitHelp;

/**
 * Created by mac on 16/12/25.
 */

abstract public class BaseBBLPullListFragment<T> extends BasePullListFragment<T> {

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getPullToRefreshListView().getRefreshableView().setDivider(getResources().getDrawable(R.drawable.transparent));
        getPullToRefreshListView().getRefreshableView().setDividerHeight(UnitHelp.dip2px(getActivity(),10));
    }
}
