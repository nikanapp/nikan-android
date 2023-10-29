package com.bloomlife.videoapp.model;

import android.content.Context;

import com.bloomlife.videoapp.R;

/**
 * Created by zxt lan4627@Gmail.com on 2015/8/20.
 */
public class DymainicMenu {

    public static final int BEHAVIOR_WEB_URL = 1;
    public static final int BEHAVIOR_USER_ID = 2;
    public static final int BEHAVIOR_STORY_ID = 3;

    private String name;
    private String iconurl;
    private String content;
    private int behavior;
    private int iconResId;
    private boolean newBtn;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIconurl() {
        return iconurl;
    }

    public void setIconurl(String iconurl) {
        this.iconurl = iconurl;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getBehavior() {
        return behavior;
    }

    public void setBehavior(int behavior) {
        this.behavior = behavior;
    }

    public int getIconResId() {
        return iconResId;
    }

    public void setIconResId(int iconResId) {
        this.iconResId = iconResId;
    }

    public boolean isNewBtn() {
        return newBtn;
    }

    public void setNewBtn(boolean newBtn) {
        this.newBtn = newBtn;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof DymainicMenu){
            DymainicMenu dm = (DymainicMenu)o;
            if ((name == null && dm.getName() != null) || (name != null && !name.equals(dm.getName()))){
                return false;
            }
            if ((iconurl == null && dm.getIconurl() != null) || (iconurl != null && !iconurl.equals(dm.getIconurl()))){
                return false;
            }
            if ((content == null && dm.getContent() != null) || (content != null && !content.equals(dm.getContent()))){
                return false;
            }
            if (behavior != dm.getBehavior()){
                return false;
            }
            return iconResId == dm.getIconResId();
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        int hashCode = 0;
        if (name != null){
            hashCode += name.hashCode();
        }
        if (iconurl != null){
            hashCode += iconurl.hashCode();
        }
        if (content != null){
            hashCode += content.hashCode();
        }
        hashCode += behavior;
        hashCode += iconResId;
        return hashCode;
    }

    public static DymainicMenu myPage(Context context){
        DymainicMenu m = new DymainicMenu();
        m.setName(context.getString(R.string.view_menu_my));
        m.setIconurl("");
        m.setIconResId(R.drawable.icon_main_me);
        m.setNewBtn(false);
        return m;
    }

    public static DymainicMenu inviteFriend(Context context){
        DymainicMenu m = new DymainicMenu();
        m.setName(context.getString(R.string.view_menu_invite));
        m.setIconurl("");
        m.setIconResId(R.drawable.icon_main_invite);
        m.setNewBtn(false);
        return m;
    }
}
