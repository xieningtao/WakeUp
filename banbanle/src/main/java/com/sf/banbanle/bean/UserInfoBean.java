package com.sf.banbanle.bean;

import com.maxleap.MLObject;

import java.io.Serializable;

/**
 * Created by mac on 16/12/4.
 */

public class UserInfoBean implements Serializable{

    private String userName;
    private String nickName;
    private String url;
    private String userId;
    private String channelId;


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
