package com.bloomlife.videoapp.model;

/**
 * Created by zxt lan4627@Gmail.com on 2015/9/25.
 */
public class VideoShareInfo {

    private String videoId;
    private int width;
    private int height;
    private Integer rotate;

    public VideoShareInfo(){

    }

    public VideoShareInfo(String videoId, int width, int height, int rotate){
        this.videoId = videoId;
        this.width = width;
        this.height = height;
        this.rotate = rotate;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public Integer getRotate() {
        return rotate;
    }

    public void setRotate(Integer rotate) {
        this.rotate = rotate;
    }
}
