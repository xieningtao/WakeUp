package com.sf.banbanle.http;

import android.content.Intent;

import com.sf.banbanle.ActivityEditContent;
import com.sf.banbanle.bean.BaiduSingleDevicePushBean;
import com.sf.banbanle.bean.TaskBean;
import com.sf.banbanle.bean.UserInfoBean;
import com.sf.banbanle.task.ActivityTaskDetail;
import com.sf.httpclient.core.AjaxParams;
import com.sf.httpclient.newcore.MethodType;
import com.sf.httpclient.newcore.SFHttpStringCallback;
import com.sf.httpclient.newcore.SFRequest;
import com.sf.loglib.L;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by mac on 16/12/10.
 */

public class BDPushUtil {
    private static final String TAG = BDPushUtil.class.getName();

    public static void pushToBatchDevice(List<String> channelIds, JSONObject notification) {
        AjaxParams ajaxParams = new AjaxParams();
        ajaxParams.put("device_type", "3");
        ajaxParams.put("msg_type", "1");

//        BaiduPushInfo pushInfo = GlobalInfo.getInstance().mPushInfo.getValue();
//        if (pushInfo != null) {
//            ajaxParams.put("channel_id", "4500265865066869730");
//            BaiduPushBean pushBean = new BaiduPushBean();
//            pushBean.setTitle("hello");
//            pushBean.setDescription("description");
//            Gson gson = new Gson();
//            ajaxParams.put("msg", gson.toJson(pushBean));
//        }

        JSONArray jsonArray = new JSONArray();
        for (String channelId : channelIds) {
            jsonArray.put(Long.valueOf(channelId));
        }
        ajaxParams.put("channel_ids", jsonArray.toString());
        ajaxParams.put("msg", notification.toString());
        SFBDPushRequest _request = new SFBDPushRequest(HttpUrl.PUSH_BATCH_DEVICE, MethodType.POST, ajaxParams) {
            @Override
            public Class getClassType() {
                return BaiduSingleDevicePushBean.class;
            }

        };
        BDPushHandler pushHandler = new BDPushHandler(_request, new SFHttpStringCallback<BaiduSingleDevicePushBean>() {

            @Override
            public void onSuccess(SFRequest sfRequest, BaiduSingleDevicePushBean pushBean) {
                L.info(TAG, "pushToSingleDevice,onsuccess: " + pushBean);
            }

            @Override
            public void onFailed(SFRequest sfRequest, Exception e) {
                L.error(TAG, "pushToSingleDevice,onfailed: " + e);
            }
        });
        pushHandler.start();
    }
}
