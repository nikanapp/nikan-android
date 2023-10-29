package com.bloomlife.videoapp.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by zxt lan4627@Gmail.com on 2015/7/28.
 */
public class Recommend extends Status implements Parcelable {

    private String username;
    private String usericon;
    private String gender;
    private String usersign;
    private int fansnum;
    private int follownum;
    private int videonum;
    private long updatetime;

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

    public String getUsersign() {
        return usersign;
    }

    public void setUsersign(String usersign) {
        this.usersign = usersign;
    }

    public int getFansnum() {
        return fansnum;
    }

    public void setFansnum(int fansnum) {
        this.fansnum = fansnum;
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

    public long getUpdatetime() {
        return updatetime;
    }

    public void setUpdatetime(long updatetime) {
        this.updatetime = updatetime;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.username);
        dest.writeString(this.usericon);
        dest.writeString(this.gender);
        dest.writeString(this.usersign);
        dest.writeInt(this.fansnum);
        dest.writeInt(this.follownum);
        dest.writeInt(this.videonum);
        dest.writeLong(this.updatetime);
    }

    public Recommend() {
    }

    protected Recommend(Parcel in) {
        this.username = in.readString();
        this.usericon = in.readString();
        this.gender = in.readString();
        this.usersign = in.readString();
        this.fansnum = in.readInt();
        this.follownum = in.readInt();
        this.videonum = in.readInt();
        this.updatetime = in.readLong();
    }

    public static final Creator<Recommend> CREATOR = new Creator<Recommend>() {
        public Recommend createFromParcel(Parcel source) {
            return new Recommend(source);
        }

        public Recommend[] newArray(int size) {
            return new Recommend[size];
        }
    };
}
