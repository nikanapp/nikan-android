package com.bloomlife.videoapp.model;

/**
 * Created by zxt lan4627@Gmail.com on 2015/7/31.
 */
public class User extends Status{

    private String username;
    private String usericon;
    private String gender;
    private String usersign;
    private int fansnum;
    private int follownum;
    private int videonum;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsericon() {
        return usericon;
    }

    public void setUsericon(String usericon) {
        this.usericon = usericon;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public int getFansnum() {
        return fansnum;
    }

    public void setFansnum(int fansnum) {
        this.fansnum = fansnum;
    }

    public String getUsersign() {
        return usersign;
    }

    public void setUsersign(String usersign) {
        this.usersign = usersign;
    }

    public int getFollownum() {
        return follownum;
    }

    public void setFollownum(int follownum) {
        this.follownum = follownum;
    }

    public int getVideonum() {
        return videonum;
    }

    public void setVideonum(int videonum) {
        this.videonum = videonum;
    }
}
