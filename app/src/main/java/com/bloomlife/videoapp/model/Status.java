package com.bloomlife.videoapp.model;

/**
 * Created by zhengxingtian lan4627@Gmail.com on 2015/8/5.
 */
public abstract class Status {
    private int status;
    private String id;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
