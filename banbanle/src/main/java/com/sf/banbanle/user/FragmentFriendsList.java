package com.sf.banbanle.user;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;

import com.basesmartframe.baseadapter.BaseAdapterHelper;
import com.basesmartframe.baseadapter.checkableadapter.CheckableAdapter;
import com.basesmartframe.baseui.BasePullListFragment;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.maxleap.FindCallback;
import com.maxleap.MLObject;
import com.maxleap.MLQuery;
import com.maxleap.MLQueryManager;
import com.maxleap.exception.MLException;
import com.nostra13.universalimageloader.utils.L;
import com.sf.banbanle.R;
import com.sf.banbanle.bean.LoginInfo;
import com.sf.banbanle.bean.UserInfoBean;
import com.sf.banbanle.config.GlobalInfo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Created by mac on 16/12/4.
 */

public class FragmentFriendsList extends BasePullListFragment<UserInfoBean> {

    private CheckableAdapter mCheckAdapter;

    @Override
    protected boolean onRefresh() {
        MLQuery<MLObject> query = new MLQuery("UserRelation");
        LoginInfo loginInfo = GlobalInfo.getInstance().mLoginInfo.getValue();
        query.whereEqualTo("userName", loginInfo.userName);
        MLQueryManager.findAllInBackground(query, new FindCallback<MLObject>() {
            @Override
            public void done(List<MLObject> list, MLException e) {
                if (e == null && list != null) {
                    L.i(TAG, "friend relation list: " + list);
                    List<String> users = new ArrayList<String>();
                    for (MLObject object : list) {
                        users.add(object.getString("friendName"));
                    }
                    getAllFriendsInfo(users, true);
                } else {
                    L.e(TAG, "friend relation exception: " + e);
                    finishRefreshOrLoading(null,false);
                }
            }
        });

        return true;
    }

    private void getAllFriendsInfo(List<String> users, boolean refresh) {
        MLQuery<MLObject> mlQuery = new MLQuery("UserInfo");
        mlQuery.whereContainedIn("userName", users);
        mlQuery.setLimit(10);
        if (!refresh) {
            mlQuery.setSkip(getDataSize());
        }
        MLQueryManager.findAllInBackground(mlQuery, new FindCallback<MLObject>() {
            @Override
            public void done(List<MLObject> list, MLException e) {
                List<UserInfoBean> userInfoBeanList = new ArrayList<UserInfoBean>();
                if (e == null && list != null) {
                    L.i(TAG, "getAllFriendsInfo list: " + list);
                    for (MLObject object : list) {
                        UserInfoBean userInfoBean = new UserInfoBean();
                        userInfoBean.setUserName(object.getString("userName"));
                        userInfoBean.setNickName(object.getString("nickName"));
                        userInfoBean.setUrl(object.getString("url"));
                        userInfoBean.setChannelId(object.getString("channelId"));
                        userInfoBean.setUserId(object.getString("userId"));
                        userInfoBean.setObjectId(object.getObjectId());
                        userInfoBeanList.add(userInfoBean);
                    }
                }
                finishRefreshOrLoading(userInfoBeanList, list == null || list.size() < 10 ? false : true);
            }
        });
    }

    @Override
    protected boolean onLoadMore() {
        return false;
    }

    @Override
    protected int[] getLayoutIds() {
        return new int[]{R.layout.friend_list_item};
    }

    @Override
    protected BaseAdapter WrapAdapterFactory(Bundle savedInstanceState, BaseAdapter adapter, PullToRefreshListView pullListView) {
        if (mCheckAdapter == null) {
            mCheckAdapter = new CheckableAdapter(savedInstanceState, adapter);
        }
        return mCheckAdapter;
    }

    @Override
    protected void onWrappAdapterCreated(BaseAdapter adapter, PullToRefreshListView pullListView) {
        super.onWrappAdapterCreated(adapter, pullListView);
        if (adapter instanceof CheckableAdapter) {
            CheckableAdapter checkableAdapter = (CheckableAdapter) adapter;
            checkableAdapter.setAdapterView(pullListView.getRefreshableView());
            checkableAdapter.setOnItemClickListener(this);
        }
    }

    @Override
    protected void bindView(BaseAdapterHelper baseAdapterHelper, int i, UserInfoBean userInfoBean) {
        baseAdapterHelper.setImageBuilder(R.id.photo_iv, "http://"+userInfoBean.getUrl());
        baseAdapterHelper.setText(R.id.nickName_tv, userInfoBean.getNickName());
        baseAdapterHelper.setText(R.id.userName_tv, userInfoBean.getUserName());
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    public List<UserInfoBean> getAllSelectedItems() {
        Set<Long> items = mCheckAdapter.getCheckedItems();
        List<UserInfoBean> userInfoBeanList = new ArrayList<>();
        if (items == null || items.isEmpty()) {
            return userInfoBeanList;
        }

        Iterator<Long> position = items.iterator();
        while (position.hasNext()) {
            Long value = position.next();
            Object item = mCheckAdapter.getItem((int) Long.parseLong(value + ""));
            if (item instanceof UserInfoBean) {
                userInfoBeanList.add((UserInfoBean) item);
            }
        }
        return userInfoBeanList;

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==ActivityFriendsList.ADD_USER_REQUEST&&resultCode== Activity.RESULT_OK){
            getPullToRefreshListView().setRefreshing(true);
        }
    }
}
