package com.bloomlife.videoapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.bloomlife.videoapp.app.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import cn.sharesdk.framework.PlatformDb;

/**
 * Created by zhengxingtian lan4627@Gmail.com on 2015/7/24.
 * 承载用户信息的类
 */
public class Account implements Parcelable {

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getSdcardUserIcon() {
        return sdcardUserIcon;
    }

    public void setSdcardUserIcon(String sdcardUserIcon) {
        this.sdcardUserIcon = sdcardUserIcon;
    }

    public enum Type { SINA_WEIBO, WECHAT, FACEBOOK, TWITTER}

    private String id;
    private String userName;
    private String userId;
    private String userIcon;
    private String location;
    private String description;
    private String tokenSecret;
    private String sdcardUserIcon;
    private long expiresTime;
    private String gender;
    private Type type;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUserIcon() {
        return userIcon;
    }

    public void setUserIcon(String userIcon) {
        this.userIcon = userIcon;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getTokenSecret() {
        return tokenSecret;
    }

    public void setTokenSecret(String tokenSecret) {
        this.tokenSecret = tokenSecret;
    }

    public long getExpiresTime() {
        return expiresTime;
    }

    public void setExpiresTime(long expiresTime) {
        this.expiresTime = expiresTime;
    }

    public static Account makeAccount(Type type, Map<String, Object> vlas, PlatformDb db){
        Account account = new Account();
        switch (type){
            case SINA_WEIBO:
                account.setId(vlas.get("id").toString());
                account.setUserName(vlas.get("name").toString());
                account.setDescription(vlas.get("description").toString());
                account.setUserIcon(vlas.get("avatar_large").toString());
                account.setLocation(vlas.get("location").toString());
                account.setGender(vlas.get("gender").toString().equals("m") ? Constants.MALE : Constants.FEMALE);
                break;

            case WECHAT:
                account.setId(vlas.get("openid").toString());
                account.setUserName(vlas.get("nickname").toString());
                account.setUserIcon(vlas.get("headimgurl").toString());
                account.setLocation(vlas.get("province").toString() + " " + vlas.get("city").toString());
                account.setGender(Integer.valueOf(vlas.get("sex").toString()) == 1 ? Constants.MALE : Constants.FEMALE);
                break;

            case TWITTER:
                account.setId(vlas.get("id").toString());
                account.setUserName(vlas.get("name").toString());
                account.setDescription(vlas.get("description").toString());
                account.setLocation(vlas.get("location").toString());
                account.setUserIcon(vlas.get("profile_image_url_https").toString());
                break;

            case FACEBOOK:
                account.setId(vlas.get("id").toString());
                account.setUserName(vlas.get("name").toString());
                try {
                    account.setUserIcon(new JSONObject(vlas.get("picture").toString()).getJSONObject("data").getString("url"));
                    account.setLocation(new JSONObject(vlas.get("currency").toString()).getString("user_currency"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                account.setGender(vlas.get("gender").toString().equals("male") ? Constants.MALE : Constants.FEMALE);
                break;
        }
        account.setTokenSecret(db.getToken());
        account.setExpiresTime(db.getExpiresTime());
        account.setType(type);
        return account;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.userName);
        dest.writeString(this.userId);
        dest.writeString(this.userIcon);
        dest.writeString(this.location);
        dest.writeString(this.description);
        dest.writeString(this.tokenSecret);
        dest.writeString(this.sdcardUserIcon);
        dest.writeLong(this.expiresTime);
        dest.writeString(this.gender);
        dest.writeInt(this.type == null ? -1 : this.type.ordinal());
    }

    public Account() {
    }

    protected Account(Parcel in) {
        this.id = in.readString();
        this.userName = in.readString();
        this.userId = in.readString();
        this.userIcon = in.readString();
        this.location = in.readString();
        this.description = in.readString();
        this.tokenSecret = in.readString();
        this.sdcardUserIcon = in.readString();
        this.expiresTime = in.readLong();
        this.gender = in.readString();
        int tmpType = in.readInt();
        this.type = tmpType == -1 ? null : Type.values()[tmpType];
    }

    public static final Creator<Account> CREATOR = new Creator<Account>() {
        public Account createFromParcel(Parcel source) {
            return new Account(source);
        }

        public Account[] newArray(int size) {
            return new Account[size];
        }
    };
}
