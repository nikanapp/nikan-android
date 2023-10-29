package com.bloomlife.videoapp.model.message;

import android.os.Build;

import com.bloomlife.android.bean.BaseMessage;
import com.bloomlife.android.common.util.StringUtils;
import com.bloomlife.videoapp.model.DbStoryVideo;
import com.bloomlife.videoapp.model.Emotion;
import com.bloomlife.videoapp.model.Video;

/**
 * Created by zxt lan4627@Gmail.com on 2015/8/10.
 */
public class UploadStoryVideoMessage extends BaseMessage {

    private String lat;
    private String lon;
    private String description;
    private String videokey;
    private String city;
    private String emotionid;
    private String persistentId;
    private String times;
    private String size;
    private String systemver;
    private String mobiletype;
    private String width;
    private String height;
    private String rotate;
    private String filename;

    public UploadStoryVideoMessage(){
        setMsgCode("4008");
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getVideokey() {
        return videokey;
    }

    public void setVideokey(String videokey) {
        this.videokey = videokey;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getEmotionid() {
        return emotionid;
    }

    public void setEmotionid(String emotionid) {
        this.emotionid = emotionid;
    }

    public String getPersistentId() {
        return persistentId;
    }

    public void setPersistentId(String persistentId) {
        this.persistentId = persistentId;
    }

    public String getTimes() {
        return times;
    }

    public void setTimes(String times) {
        this.times = times;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getSystemver() {
        return systemver;
    }

    public void setSystemver(String systemver) {
        this.systemver = systemver;
    }

    public String getMobiletype() {
        return mobiletype;
    }

    public void setMobiletype(String mobiletype) {
        this.mobiletype = mobiletype;
    }

    public String getWidth() {
        return width;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getRotate() {
        return rotate;
    }

    public void setRotate(String rotate) {
        this.rotate = rotate;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public static UploadStoryVideoMessage make(DbStoryVideo video, String videokey){
        UploadStoryVideoMessage message = new UploadStoryVideoMessage();
        message.setEmotionid(video.getEmotionid());
        message.setCity(video.getCity());
        message.setDescription(video.getDesc());
        message.setFilename(video.getFilePath());
        message.setLat(video.getLat());
        message.setLon(video.getLon());
        message.setPersistentId(video.getPersistentsId());
        message.setSize(String.valueOf(video.getSize()));
        if(StringUtils.isNotEmpty(video.getWidth())){
            message.setWidth(video.getWidth());
        }
        if(StringUtils.isNotEmpty(video.getHeight())){
            message.setHeight(video.getHeight());
        }
        message.setRotate(String.valueOf(video.getRotate()));
        message.setVideokey(videokey);
        message.setTimes(String.valueOf(video.getTimes()));
        message.setSystemver(Build.MODEL);
        return message;
    }
}
