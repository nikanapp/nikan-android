package com.bloomlife.videoapp.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by zxt lan4627@Gmail.com on 2015/7/31.
 */
public class Story implements Parcelable {

    private String id;
    private String uid;
    private String location;
    private String description;
    private String videouri;
    private String city;
    private String emotion;
    private String bgmusicid;
    private String bigpreviewurl;
    private String username;
    private String usericon;
    private String gender;
    private String updatetime;
    private int heat;
    private int looknum;
    private int commentnum;
    private long sectime;
    private int fansnum ;
    private int follownum;
    private int videonum;
    private String usersign;
    private int likenum;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getVideouri() {
        return videouri;
    }

    public void setVideouri(String videouri) {
        this.videouri = videouri;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getEmotion() {
        return emotion;
    }

    public void setEmotion(String emotion) {
        this.emotion = emotion;
    }

    public String getBgmusicid() {
        return bgmusicid;
    }

    public void setBgmusicid(String bgmusicid) {
        this.bgmusicid = bgmusicid;
    }

    public String getBigpreviewurl() {
        return bigpreviewurl;
    }

    public void setBigpreviewurl(String bigpreviewurl) {
        this.bigpreviewurl = bigpreviewurl;
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

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getUpdatetime() {
        return updatetime;
    }

    public void setUpdatetime(String updatetime) {
        this.updatetime = updatetime;
    }

    public int getHeat() {
        return heat;
    }

    public void setHeat(int heat) {
        this.heat = heat;
    }

    public int getLooknum() {
        return looknum;
    }

    public void setLooknum(int looknum) {
        this.looknum = looknum;
    }

    public int getCommentnum() {
        return commentnum;
    }

    public void setCommentnum(int commentnum) {
        this.commentnum = commentnum;
    }

    public long getSectime() {
        return sectime;
    }

    public void setSectime(long sectime) {
        this.sectime = sectime;
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

    public String getUsersign() {
        return usersign;
    }

    public void setUsersign(String usersign) {
        this.usersign = usersign;
    }

    public int getLikenum() {
        return likenum;
    }

    public void setLikenum(int likenum) {
        this.likenum = likenum;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.uid);
        dest.writeString(this.location);
        dest.writeString(this.description);
        dest.writeString(this.videouri);
        dest.writeString(this.city);
        dest.writeString(this.emotion);
        dest.writeString(this.bgmusicid);
        dest.writeString(this.bigpreviewurl);
        dest.writeString(this.username);
        dest.writeString(this.usericon);
        dest.writeString(this.gender);
        dest.writeString(this.updatetime);
        dest.writeInt(this.heat);
        dest.writeInt(this.looknum);
        dest.writeInt(this.commentnum);
        dest.writeLong(this.sectime);
        dest.writeInt(this.fansnum);
        dest.writeInt(this.follownum);
        dest.writeInt(this.videonum);
        dest.writeString(this.usersign);
        dest.writeInt(this.likenum);
    }

    public Story() {
    }

    protected Story(Parcel in) {
        this.id = in.readString();
        this.uid = in.readString();
        this.location = in.readString();
        this.description = in.readString();
        this.videouri = in.readString();
        this.city = in.readString();
        this.emotion = in.readString();
        this.bgmusicid = in.readString();
        this.bigpreviewurl = in.readString();
        this.username = in.readString();
        this.usericon = in.readString();
        this.gender = in.readString();
        this.updatetime = in.readString();
        this.heat = in.readInt();
        this.looknum = in.readInt();
        this.commentnum = in.readInt();
        this.sectime = in.readLong();
        this.fansnum = in.readInt();
        this.follownum = in.readInt();
        this.videonum = in.readInt();
        this.usersign = in.readString();
        this.likenum = in.readInt();

    }

    public static final Creator<Story> CREATOR = new Creator<Story>() {
        public Story createFromParcel(Parcel source) {
            return new Story(source);
        }

        public Story[] newArray(int size) {
            return new Story[size];
        }
    };
}
