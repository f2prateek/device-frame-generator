package com.f2prateek.dfg.core;

import java.io.Serializable;

public class News implements Serializable {

    private static final long serialVersionUID = -6641292855569752036L;

    private String title;
    private String content;
    private String objectId;

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

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }
}
