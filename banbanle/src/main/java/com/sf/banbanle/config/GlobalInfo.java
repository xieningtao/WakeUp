package com.sf.banbanle.config;

import com.sf.banbanle.bean.BaiduPushInfo;
import com.sf.banbanle.bean.LoginInfo;
import com.sf.banbanle.bean.UserInfoBean;
import com.sf.banbanle.property.BeanHolder;

/**
 * Created by NetEase on 2016/12/1 0001.
 */

public class GlobalInfo {

    private static GlobalInfo globalInfo = new GlobalInfo();

    private GlobalInfo() {

    }

    public static GlobalInfo getInstance() {
        return globalInfo;
    }


    public final BeanHolder<LoginInfo> mLoginInfo = new BeanHolder<>(null);

    public final BeanHolder<BaiduPushInfo> mPushInfo=new BeanHolder<>(null);

    public final BeanHolder<UserInfoBean> mInfoBean=new BeanHolder<>(null);
}
