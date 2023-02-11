package com.bloomlife.videoapp.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by zhengxingtian lan4627@Gmail.com on 2015/8/17.
 */
public class Emotion implements Parcelable {

    private String id;
    private String frameurl;
    private String bgmusic;
    private String frameprefix;
    private String emotionicon;
    private int framecount;
    private String emotionname;
    private String emotionenname;

    public Emotion() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFrameurl() {
        return frameurl;
    }

    public void setFrameurl(String frameurl) {
        this.frameurl = frameurl;
    }

    public String getFrameprefix() {
        return frameprefix;
    }

    public void setFrameprefix(String frameprefix) {
        this.frameprefix = frameprefix;
    }

    public String getEmotionicon() {
        return emotionicon;
    }

    public void setEmotionicon(String emotionicon) {
        this.emotionicon = emotionicon;
    }

    public int getFramecount() {
        return framecount;
    }

    public void setFramecount(int framecount) {
        this.framecount = framecount;
    }

    public String getEmotionname() {
        return emotionname;
    }

    public void setEmotionname(String emotionname) {
        this.emotionname = emotionname;
    }

    public String getEmotionenname() {
        return emotionenname;
    }

    public void setEmotionenname(String emotionenname) {
        this.emotionenname = emotionenname;
    }

    public String getBgmusic() {
        return bgmusic;
    }

    public void setBgmusic(String bgmusic) {
        this.bgmusic = bgmusic;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.frameurl);
        dest.writeString(this.bgmusic);
        dest.writeString(this.frameprefix);
        dest.writeString(this.emotionicon);
        dest.writeInt(this.framecount);
        dest.writeString(this.emotionname);
        dest.writeString(this.emotionenname);
    }

    protected Emotion(Parcel in) {
        this.id = in.readString();
        this.frameurl = in.readString();
        this.bgmusic = in.readString();
        this.frameprefix = in.readString();
        this.emotionicon = in.readString();
        this.framecount = in.readInt();
        this.emotionname = in.readString();
        this.emotionenname = in.readString();
    }

    public static final Creator<Emotion> CREATOR = new Creator<Emotion>() {
        public Emotion createFromParcel(Parcel source) {
            return new Emotion(source);
        }

        public Emotion[] newArray(int size) {
            return new Emotion[size];
        }
    };
}
