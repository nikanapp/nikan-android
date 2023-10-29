package com.bloomlife.videoapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import net.tsz.afinal.annotation.sqlite.Id;

/**
 * Created by zxt lan4627@Gmail.com on 2015/9/6.
 */
public class NotificationUserInfo implements Parcelable {

    @Id
    private int id;

    private String msgid;
    private String userid;
    private String username;
    private String usericon;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsericon() {
        return usericon;
    }

    public void setUsericon(String usericon) {
        this.usericon = usericon;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getMsgid() {
        return msgid;
    }

    public void setMsgid(String msgid) {
        this.msgid = msgid;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.msgid);
        dest.writeString(this.userid);
        dest.writeString(this.username);
        dest.writeString(this.usericon);
    }

    public NotificationUserInfo() {
    }

    protected NotificationUserInfo(Parcel in) {
        this.id = in.readInt();
        this.msgid = in.readString();
        this.userid = in.readString();
        this.username = in.readString();
        this.usericon = in.readString();
    }

    public static final Parcelable.Creator<NotificationUserInfo> CREATOR = new Parcelable.Creator<NotificationUserInfo>() {
        public NotificationUserInfo createFromParcel(Parcel source) {
            return new NotificationUserInfo(source);
        }

        public NotificationUserInfo[] newArray(int size) {
            return new NotificationUserInfo[size];
        }
    };
}
