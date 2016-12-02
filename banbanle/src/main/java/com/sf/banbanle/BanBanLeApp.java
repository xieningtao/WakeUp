package com.sf.banbanle;

import android.content.Context;
import android.support.multidex.MultiDex;

import com.basesmartframe.baseapp.BaseApp;
import com.example.androidtv.module.BaseModule;
import com.example.androidtv.module.home.TVGameModule;
import com.maxleap.GetCallback;
import com.maxleap.MLDataManager;
import com.maxleap.MLObject;
import com.maxleap.MaxLeap;
import com.maxleap.exception.MLException;
import com.sf.baidulib.SFBaiduLocationManager;
import com.sf.loglib.L;

/**
 * Created by NetEase on 2016/11/30 0030.
 */

public class BanBanLeApp extends BaseApp {
    public static final String APP_ID = "583e7ef387d4b7a020959690";
    //    public static final String APP_ID_KEY = "MmNsUDJONjlNc2xwNzEtbVY3RE5KUQ";
    public static final String APP_ID_KEY = "ZXc0Ql9MZ2lhOVFwS1ZCOUZFMzBpdw";
    public static final String REST_API_KEY = "YnpmTnZZcG5YcU1XWGhiWkh3RHlKdw";

    public static final String BAICHUAN_APP_KEY = "23553990";
    public static final String BAICHUAN_APP_SECRET = "2cf0d641b20dda4428512992d9b55834";

    public static final String BAIDU_PUSH_APP_KEY = "WEITF2EH9D7yzwdkvMZhxRzI";
    public static final String BAIDU_PUSH_APP_ID = "8985426";
    public static final String BAIDU_PUSH_SECRET_KEY = "92Ip8qpWdGVzGxGle1dBW0ja8lwkOw6X";

    @Override
    public void onCreate() {
        super.onCreate();
        init();
        startModule();
//        doTest();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    private void startModule() {
        BaseModule module = new TVGameModule();
        module.onStart();
    }

    private void init() {
        SFBaiduLocationManager.getInstance().init(getApplicationContext());
        SFBaiduLocationManager.getInstance().requestLocate();
        MaxLeap.initialize(this, APP_ID, APP_ID_KEY, MaxLeap.REGION_CN);
        SpHelper.init(this, "banbanle_global");
    }


    private void doTest() {
        MLDataManager.fetchInBackground(MLObject.createWithoutData("foobar", "123"),
                new GetCallback<MLObject>() {
                    @Override
                    public void done(MLObject mlObject, MLException e) {
                        if (e != null && e.getCode() == MLException.INVALID_OBJECT_ID) {
                            L.debug("MaxLeap", "SDK 成功连接到你的云端应用！");
                        } else {
                            L.debug("MaxLeap", "应用访问凭证不正确，请检查。exception: " + e);
                        }
                    }
                });
    }
}
