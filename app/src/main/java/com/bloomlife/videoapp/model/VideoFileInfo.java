package com.bloomlife.videoapp.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 
 * @author <a href="mailto:lan4627@gmail.com">zhengxingtian</a>
 *
 * @date 2015年3月25日 下午2:42:19
 */
public class VideoFileInfo implements Parcelable{

    private long id;
    private long duration;
    private long date;
    private String path;

    public VideoFileInfo(){

    }

    public VideoFileInfo(long date, String path, long id, long duration){
        this.date = date;
        this.path = path;
        this.id = id;
        this.duration = duration;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeLong(duration);
        dest.writeLong(date);
        dest.writeString(path);
    }

    public static final Creator<VideoFileInfo> CREATOR = new Creator<VideoFileInfo>() {
        @Override
        public VideoFileInfo createFromParcel(Parcel source) {
            VideoFileInfo v = new VideoFileInfo();
            v.setId(source.readLong());
            v.setDuration(source.readLong());
            v.setDate(source.readLong());
            v.setPath(source.readString());
            return v;
        }

        @Override
        public VideoFileInfo[] newArray(int size) {
            return new VideoFileInfo[size];
        }
    };

}
