package com.bloomlife.videoapp.model;

import android.content.Context;

import com.bloomlife.videoapp.R;

/**
 * Created by zhengxingtian lan4627@Gmail.com on 2015/8/4.
 */
public class Menu {
    private String iconUrl;
    private String name;
    private String pageUrl;
    private int iconResId;
    private boolean newBtn;

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isNewBtn() {
        return newBtn;
    }

    public void setNewBtn(boolean newBtn) {
        this.newBtn = newBtn;
    }

    public String getPageUrl() {
        return pageUrl;
    }

    public void setPageUrl(String pageUrl) {
        this.pageUrl = pageUrl;
    }

    public int getIconResId() {
        return iconResId;
    }

    public void setIconResId(int iconResId) {
        this.iconResId = iconResId;
    }
}
