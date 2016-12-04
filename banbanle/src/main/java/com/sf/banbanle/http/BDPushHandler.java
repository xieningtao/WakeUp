package com.sf.banbanle.http;

import com.basesmartframe.request.SFHttpGsonCacheHandler;
import com.basesmartframe.request.SFHttpGsonHandler;
import com.sf.banbanle.BanBanLeApp;
import com.sf.banbanle.sign.PushSignature;
import com.sf.httpclient.core.AjaxParams;
import com.sf.httpclient.newcore.SFHttpStringCallback;
import com.sf.httpclient.newcore.SFRequest;
import com.sf.httpclient.newcore.cache.SFCacheRequest;

/**
 * Created by mac on 16/12/4.
 */

public class BDPushHandler extends SFHttpGsonHandler {

    public BDPushHandler(SFRequest request, SFHttpStringCallback httpStringCallback) {
        super(request, httpStringCallback);
        setRequestInterceptor(new RequestInterceptor());
    }


}
