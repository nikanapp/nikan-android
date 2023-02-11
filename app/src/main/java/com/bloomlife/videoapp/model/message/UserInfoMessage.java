package com.bloomlife.videoapp.model.message;

import com.bloomlife.android.bean.BaseMessage;

/**
 * Created by zhengxingtian lan4627@Gmail.com on 2015/7/27.
 * 上传用户信息
 */
public class UserInfoMessage extends BaseMessage{

    public static final String SINA_WEIBO = "1";
    public static final String WECHAT = "2";
    public static final String FACEBOOK = "3";
    public static final String TWITTER = "4";

    private String authplatform;
    private String openid;
    private String accesstoken;
    private String gender;
    private String username;
    private String usericon;
    private String city;
    private String usersign;

    public UserInfoMessage(){
        setMsgCode("4001");
    }

    public String getAuthplatform() {
        return authplatform;
    }

    public void setAuthplatform(String authplatform) {
        this.authplatform = authplatform;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public String getAccesstoken() {
        return accesstoken;
    }

    public void setAccesstoken(String accesstoken) {
        this.accesstoken = accesstoken;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

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

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getUsersign() {
        return usersign;
    }

    public void setUsersign(String usersign) {
        this.usersign = usersign;
    }
}
