package com.sf.banbanle.bean;

/**
 * Created by mac on 16/12/3.
 */

public class BaiduPushBean {
    private String title;
    private String description;
    private int notification_builder_id;
    private int notification_basic_style;
    private int open_type;
    private String url;
    private String pkg_content;
    private String custom_content;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getNotification_builder_id() {
        return notification_builder_id;
    }

    public void setNotification_builder_id(int notification_builder_id) {
        this.notification_builder_id = notification_builder_id;
    }

    public int getNotification_basic_style() {
        return notification_basic_style;
    }

    public void setNotification_basic_style(int notification_basic_style) {
        this.notification_basic_style = notification_basic_style;
    }

    public int getOpen_type() {
        return open_type;
    }

    public void setOpen_type(int open_type) {
        this.open_type = open_type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPkg_content() {
        return pkg_content;
    }

    public void setPkg_content(String pkg_content) {
        this.pkg_content = pkg_content;
    }

    public String getCustom_content() {
        return custom_content;
    }

    public void setCustom_content(String custom_content) {
        this.custom_content = custom_content;
    }
}
