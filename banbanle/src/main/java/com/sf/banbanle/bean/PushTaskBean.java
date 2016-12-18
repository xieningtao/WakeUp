package com.sf.banbanle.bean;

import java.io.Serializable;

/**
 * Created by mac on 16/12/18.
 */

public class PushTaskBean implements Serializable {
    private String title;
    private String content;
    private String id;
    private String videoPath;


    public String getVideoPath() {
        return videoPath;
    }

    public void setVideoPath(String videoPath) {
        this.videoPath = videoPath;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
