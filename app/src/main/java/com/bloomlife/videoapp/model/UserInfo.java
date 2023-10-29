package com.bloomlife.videoapp.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by zxt lan4627@Gmail.com on 2015/7/31.
 */
public class UserInfo implements Parcelable {

    public static final int NONE = -1;

    private String id;
    private String name;
    private String iconUrl;
    private String gender;
    private String city;
    private String description;
    private String storyId;
    private int fansNum;
    private int storiesNum;
    private int followerNum;
    private int status = NONE;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getFansNum() {
        return fansNum;
    }

    public void setFansNum(int fansNum) {
        this.fansNum = fansNum;
    }

    public int getStoriesNum() {
        return storiesNum;
    }

    public void setStoriesNum(int storiesNum) {
        this.storiesNum = storiesNum;
    }

    public int getFollowerNum() {
        return followerNum;
    }

    public void setFollowerNum(int followerNum) {
        this.followerNum = followerNum;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public String getStoryId() {
        return storyId;
    }

    public void setStoryId(String storyId) {
        this.storyId = storyId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.name);
        dest.writeString(this.iconUrl);
        dest.writeString(this.gender);
        dest.writeString(this.city);
        dest.writeString(this.description);
        dest.writeString(this.storyId);
        dest.writeInt(this.fansNum);
        dest.writeInt(this.storiesNum);
        dest.writeInt(this.followerNum);
        dest.writeInt(this.status);
    }

    public UserInfo() {
    }

    protected UserInfo(Parcel in) {
        this.id = in.readString();
        this.name = in.readString();
        this.iconUrl = in.readString();
        this.gender = in.readString();
        this.city = in.readString();
        this.description = in.readString();
        this.storyId = in.readString();
        this.fansNum = in.readInt();
        this.storiesNum = in.readInt();
        this.followerNum = in.readInt();
        this.status = in.readInt();
    }

    public static final Parcelable.Creator<UserInfo> CREATOR = new Parcelable.Creator<UserInfo>() {
        public UserInfo createFromParcel(Parcel source) {
            return new UserInfo(source);
        }

        public UserInfo[] newArray(int size) {
            return new UserInfo[size];
        }
    };
}
