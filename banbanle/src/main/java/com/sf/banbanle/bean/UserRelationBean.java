package com.sf.banbanle.bean;

import com.maxleap.MLObject;

/**
 * Created by mac on 16/12/4.
 */

public class UserRelationBean {
    private  String userName;
    private  String friendName;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getFriendName() {
        return friendName;
    }

    public void setFriendName(String friendName) {
        this.friendName = friendName;
    }

    @Override
    public String toString() {
        return "UserRelationBean{" +
                "userName='" + userName + '\'' +
                ", friendName='" + friendName + '\'' +
                '}';
    }
}
