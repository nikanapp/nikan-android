package com.bloomlife.videoapp.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

/**
 * Created by zhengxingtian lan4627@Gmail.com on 2015/7/31.
 */

public class StoryVideo implements Parcelable {

    private String videouri;
    private String id;
    private String description;
    private String createtime;
    private String bgmusicid;
    private String emotionid;
    private String storyid;
    private String city;
    private String bigpreviewurl;
    private long sectime;

    public String getVideouri() {
        return videouri;
    }

    public void setVideouri(String videouri) {
        this.videouri = videouri;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCreatetime() {
        return createtime;
    }

    public void setCreatetime(String createtime) {
        this.createtime = createtime;
    }

    public String getBgmusicid() {
        return bgmusicid;
    }

    public void setBgmusicid(String bgmusicid) {
        this.bgmusicid = bgmusicid;
    }

    public String getEmotionid() {
        return emotionid;
    }

    public void setEmotionid(String emotionid) {
        this.emotionid = emotionid;
    }


    public String getStoryid() {
        return storyid;
    }

    public void setStoryid(String storyid) {
        this.storyid = storyid;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public long getSectime() {
        return sectime;
    }

    public void setSectime(long sectime) {
        this.sectime = sectime;
    }

    public static StoryVideo make(DbStoryVideo video){
        StoryVideo newVideo = new StoryVideo();
        newVideo.setVideouri(TextUtils.isEmpty(video.getFilePath()) ? video.getVideouri() : video.getFilePath());
        newVideo.setId(video.getVideoid());
        newVideo.setStoryid(video.getStoryid());
        newVideo.setDescription(video.getDesc());
        newVideo.setSectime(video.getSectime());
        newVideo.setBgmusicid(video.getBgmusicid());
        newVideo.setCity(video.getCity());
        newVideo.setCreatetime(video.getCreatetime());
        newVideo.setEmotionid(video.getEmotionid());
        newVideo.setBigpreviewurl(video.getBigpreviewurl());
        return newVideo;
    }

    public String getBigpreviewurl() {
        return bigpreviewurl;
    }

    public void setBigpreviewurl(String bigpreviewurl) {
        this.bigpreviewurl = bigpreviewurl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.videouri);
        dest.writeString(this.id);
        dest.writeString(this.description);
        dest.writeString(this.createtime);
        dest.writeString(this.bgmusicid);
        dest.writeString(this.emotionid);
        dest.writeString(this.storyid);
        dest.writeString(this.city);
        dest.writeString(this.bigpreviewurl);
        dest.writeLong(this.sectime);
    }

    public StoryVideo() {
    }

    protected StoryVideo(Parcel in) {
        this.videouri = in.readString();
        this.id = in.readString();
        this.description = in.readString();
        this.createtime = in.readString();
        this.bgmusicid = in.readString();
        this.emotionid = in.readString();
        this.storyid = in.readString();
        this.city = in.readString();
        this.bigpreviewurl = in.readString();
        this.sectime = in.readLong();
    }

    public static final Creator<StoryVideo> CREATOR = new Creator<StoryVideo>() {
        public StoryVideo createFromParcel(Parcel source) {
            return new StoryVideo(source);
        }

        public StoryVideo[] newArray(int size) {
            return new StoryVideo[size];
        }
    };
}
