package com.bloomlife.videoapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.bloomlife.android.bean.CacheBean;

import net.tsz.afinal.annotation.sqlite.Id;
import net.tsz.afinal.annotation.sqlite.Table;

/**
 * Created by zhengxingtian lan4627@Gmail.com on 2015/8/31.
 */
@Table(name="story_video")
public class DbStoryVideo implements Parcelable {

    /**已经完成全部上传操作**/
    public static final int STATUS_UPLOAD_SUCCESS = 2 ;

    /**还没有执行上传步骤，一般是拍摄完成时网络请求无法进行的情况**/
    public static final int STATUS_NOT_COMPLETE = 0 ;

    public static final int STATUS_UPLOAD_FAIL = 3;

    @Id
    private Integer id; //数据库主键

    private int status;
    private long size;
    private double uploadProgress;
    private String filePath;
    private String userId;
    private String persistentsId;
    private String lat;
    private String lon;
    private String width;
    private String height;
    private String rotate;
    private String times;
    private String videoKey;

    private String videouri;
    private String videoid;
    private String desc;
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

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
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

    public String getVideoid() {
        return videoid;
    }

    public void setVideoid(String videoid) {
        this.videoid = videoid;
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

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLon() {
        return lon;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }

    public String getPersistentsId() {
        return persistentsId;
    }

    public void setPersistentsId(String persistentsId) {
        this.persistentsId = persistentsId;
    }

    public String getRotate() {
        return rotate;
    }

    public void setRotate(String rotate) {
        this.rotate = rotate;
    }

    public String getTimes() {
        return times;
    }

    public void setTimes(String times) {
        this.times = times;
    }

    public String getWidth() {
        return width;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getVideoKey() {
        return videoKey;
    }

    public void setVideoKey(String videoKey) {
        this.videoKey = videoKey;
    }

    public double getUploadProgress() {
        return uploadProgress;
    }

    public void setUploadProgress(double uploadProgress) {
        this.uploadProgress = uploadProgress;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.id);
        dest.writeInt(this.status);
        dest.writeLong(this.size);
        dest.writeDouble(this.uploadProgress);
        dest.writeString(this.filePath);
        dest.writeString(this.userId);
        dest.writeString(this.persistentsId);
        dest.writeString(this.lat);
        dest.writeString(this.lon);
        dest.writeString(this.width);
        dest.writeString(this.height);
        dest.writeString(this.rotate);
        dest.writeString(this.times);
        dest.writeString(this.videoKey);
        dest.writeString(this.videouri);
        dest.writeString(this.videoid);
        dest.writeString(this.desc);
        dest.writeString(this.createtime);
        dest.writeString(this.bgmusicid);
        dest.writeString(this.emotionid);
        dest.writeString(this.storyid);
        dest.writeString(this.city);
        dest.writeString(this.bigpreviewurl);
        dest.writeLong(this.sectime);
    }

    public DbStoryVideo() {
    }

    protected DbStoryVideo(Parcel in) {
        this.id = (Integer) in.readValue(Integer.class.getClassLoader());
        this.status = in.readInt();
        this.size = in.readLong();
        this.uploadProgress = in.readDouble();
        this.filePath = in.readString();
        this.userId = in.readString();
        this.persistentsId = in.readString();
        this.lat = in.readString();
        this.lon = in.readString();
        this.width = in.readString();
        this.height = in.readString();
        this.rotate = in.readString();
        this.times = in.readString();
        this.videoKey = in.readString();
        this.videouri = in.readString();
        this.videoid = in.readString();
        this.desc = in.readString();
        this.createtime = in.readString();
        this.bgmusicid = in.readString();
        this.emotionid = in.readString();
        this.storyid = in.readString();
        this.city = in.readString();
        this.bigpreviewurl = in.readString();
        this.sectime = in.readLong();
    }

    public static final Parcelable.Creator<DbStoryVideo> CREATOR = new Parcelable.Creator<DbStoryVideo>() {
        public DbStoryVideo createFromParcel(Parcel source) {
            return new DbStoryVideo(source);
        }

        public DbStoryVideo[] newArray(int size) {
            return new DbStoryVideo[size];
        }
    };

    public static DbStoryVideo make(StoryVideo video){
        DbStoryVideo storyVideo = new DbStoryVideo();
        storyVideo.setCreatetime(video.getCreatetime());
        storyVideo.setEmotionid(video.getEmotionid());
        storyVideo.setBgmusicid(video.getBgmusicid());
        storyVideo.setCity(video.getCity());
        storyVideo.setDesc(video.getDescription());
        storyVideo.setSectime(video.getSectime());
        storyVideo.setStoryid(video.getStoryid());
        storyVideo.setVideoid(video.getId());
        storyVideo.setVideouri(video.getVideouri());
        storyVideo.setBigpreviewurl(video.getBigpreviewurl());
        storyVideo.setUserId(CacheBean.getInstance().getLoginUserId());
        return storyVideo;
    }

    public String getBigpreviewurl() {
        return bigpreviewurl;
    }

    public void setBigpreviewurl(String bigpreviewurl) {
        this.bigpreviewurl = bigpreviewurl;
    }
}
