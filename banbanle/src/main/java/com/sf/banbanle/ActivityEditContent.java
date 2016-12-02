package com.sf.banbanle;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;

import com.basesmartframe.baseui.BaseActivity;
import com.basesmartframe.request.SFHttpGsonHandler;
import com.maxleap.im.DataHandler;
import com.maxleap.im.MLParrot;
import com.maxleap.im.ParrotException;
import com.maxleap.im.SimpleDataHandler;
import com.maxleap.im.entity.FriendInfo;
import com.maxleap.im.entity.Message;
import com.maxleap.im.entity.MessageBuilder;
import com.nostra13.universalimageloader.utils.L;
import com.sf.banbanle.http.HttpUrl;
import com.sf.httpclient.core.AjaxParams;
import com.sf.httpclient.newcore.MethodType;
import com.sf.httpclient.newcore.SFHttpStringCallback;
import com.sf.httpclient.newcore.SFRequest;
import com.sf.utils.baseutil.Md5Utils;
import com.sf.utils.baseutil.SFToast;
import com.sflib.CustomView.baseview.EditTextClearDroidView;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by NetEase on 2016/12/1 0001.
 */

public class ActivityEditContent extends BaseActivity {
    private EditTextClearDroidView mContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_content);
        mContent = (EditTextClearDroidView) findViewById(R.id.edit_content);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case 0:
                getFriendInfo("584017657e2c790007e57b19");
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void doSendInfo(String clientId) {
        if (TextUtils.isEmpty(mContent.getEditText().getText())) {
            SFToast.showToast(R.string.edit_content_tip);
            return;
        }
        String content = mContent.getEditText().getText().toString();
        sendInfo(clientId, content);
    }

    private void addFriend(String clientId) {
        MLParrot.getInstance().addFriend(clientId, new DataHandler<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                L.i(TAG, "addFriend,onSuccess");

            }

            @Override
            public void onError(ParrotException e) {
                L.i(TAG, "addFriend,onError: " + e);
            }
        });
    }

    private void getFriendInfo(final String clientId) {
        MLParrot.getInstance().getFriendInfo(clientId, new DataHandler<FriendInfo>() {
            @Override
            public void onSuccess(FriendInfo friendInfo) {
                L.i(TAG, "getFriendInfo friendInfo: " + friendInfo.toString());
            }

            @Override
            public void onError(ParrotException e) {
                L.i(TAG, "getFriendInfo exception : " + e);
                addFriend(clientId);
            }
        });
    }

    private void sendInfo(String clientId, String message) {
        Message msg = MessageBuilder.newBuilder()
                .to(clientId, true)   // 目标对象的 ClientId
                .text(message);
        MLParrot.getInstance().sendMessage(msg, new SimpleDataHandler<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                L.e(TAG, "sendInfo onSuccess");
                finish();
            }

        });
    }

    private String constructMd5(String url, AjaxParams params) {
        StringBuilder builder = new StringBuilder();
        builder.append("POST").append(url);
        if (params != null) {
            Map<String, String> contentMap = params.getUrlParams();
            Iterator<Map.Entry<String, String>> entrySet = contentMap.entrySet().iterator();
            while (entrySet.hasNext()) {
                Map.Entry<String, String> entry = entrySet.next();
                builder.append(entry.getKey()).append("=").append(entry.getValue());
            }
        }
        builder.append(BanBanLeApp.BAIDU_PUSH_SECRET_KEY);
        try {
            String encodeContent = URLEncoder.encode(builder.toString(), "utf-8");
            return Md5Utils.getMD5(encodeContent);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";
    }

    private void sendSingleDevice() {
        AjaxParams ajaxParams = new AjaxParams();
        ajaxParams.put("apikey", BanBanLeApp.BAIDU_PUSH_APP_KEY);
        ajaxParams.put("timestamp", System.currentTimeMillis() + "");
        ajaxParams.put("device_type", "android");
        ajaxParams.put("sign", constructMd5(HttpUrl.PUSH_SINGLE_DEVICE, ajaxParams));
        SFRequest _request = new SFRequest(HttpUrl.PUSH_SINGLE_DEVICE, MethodType.POST) {
            @Override
            public Class getClassType() {
                return Student.class;
            }

        };
        SFHttpGsonHandler sfHttpGsonHandler = new SFHttpGsonHandler(_request, new SFHttpStringCallback<Student>() {

            @Override
            public void onSuccess(SFRequest sfRequest, Student student) {

            }

            @Override
            public void onFailed(SFRequest sfRequest, Exception e) {

            }
        });
        sfHttpGsonHandler.start();
    }

    class Student {

    }
}
