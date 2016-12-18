package com.sf.banbanle.bean;

import com.maxleap.MLObject;

/**
 * Created by mac on 16/12/10.
 */

public class MLObjectParserUtil {

    public static TaskBean parserTaskBean(MLObject object) {
        TaskBean taskBean = new TaskBean();
        if (object == null) {
            return null;
        }
        taskBean.setContent(object.getString("content"));
        taskBean.setState(object.getString("state"));
        taskBean.setTitle(object.getString("title"));
        taskBean.setType(object.getInt("type"));
        taskBean.setUserName(object.getString("creator"));
        taskBean.setId(object.getObjectId());
        taskBean.setUrl(object.getString("url"));
        taskBean.setNickName(object.getString("nickName"));
        taskBean.setStartTime(object.getLong("startTime"));
        taskBean.setEndTime(object.getLong("endTime"));
        return taskBean;
    }
}
