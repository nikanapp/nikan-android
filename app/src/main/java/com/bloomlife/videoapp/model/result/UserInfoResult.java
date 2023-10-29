package com.bloomlife.videoapp.model.result;

import com.bloomlife.android.bean.ProcessResult;

/**
 * Created by zxt lan4627@Gmail.com on 2015/7/27.
 */
public class UserInfoResult extends ProcessResult {

    public static final int SUCC = 0;
    public static final int NAME_ALREADY_USED = 1;
    public static final int FIRSTAUTH = 1;
    public static final int NOT_FIRSTAUTH = 0;

    private String userid;
    private int statecode;
    private String nickname;
    private int firstauth;
    private String gender;
    private String pwd;
    private String errmsg;

    private String usericon ;

    private String usersign;

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public int getStatecode() {
        return statecode;
    }

    public void setStatecode(int statecode) {
        this.statecode = statecode;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public int getFirstauth() {
        return firstauth;
    }

    public void setFirstauth(int firstauth) {
        this.firstauth = firstauth;
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

    public String getUsersign() {
        return usersign;
    }

    public void setUsersign(String usersign) {
        this.usersign = usersign;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public String getErrmsg() {
        return errmsg;
    }

    public void setErrmsg(String errmsg) {
        this.errmsg = errmsg;
    }
}
