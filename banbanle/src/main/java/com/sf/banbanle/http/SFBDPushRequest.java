package com.sf.banbanle.http;

import com.sf.banbanle.BanBanLeApp;
import com.sf.banbanle.sign.PushSignature;
import com.sf.httpclient.core.AjaxParams;
import com.sf.httpclient.newcore.MethodType;
import com.sf.httpclient.newcore.SFRequest;

/**
 * Created by mac on 16/12/4.
 */

abstract public class SFBDPushRequest extends SFRequest {
    public SFBDPushRequest(String url, MethodType methodType) {
        super(url, methodType);
    }

    public SFBDPushRequest(String url, MethodType methodType, AjaxParams params) {
        super(url, methodType, params);
    }

    @Override
    public AjaxParams getAjaxParams() {
        AjaxParams ajaxParams = super.getAjaxParams();
        if(ajaxParams!=null){
            ajaxParams.put("apikey", BanBanLeApp.BAIDU_PUSH_APP_KEY);
            ajaxParams.put("timestamp", System.currentTimeMillis() / 1000 + "");
            PushSignature pushSignature = new PushSignature();
            String sign = pushSignature.digest("POST", mUrl, BanBanLeApp.BAIDU_PUSH_SECRET_KEY, ajaxParams.getUrlParams());
            ajaxParams.put("sign", sign);
        }
        return ajaxParams;
    }
}
