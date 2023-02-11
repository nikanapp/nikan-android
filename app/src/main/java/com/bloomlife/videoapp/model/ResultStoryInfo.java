package com.bloomlife.videoapp.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by zhengxingtian lan4627@Gmail.com on 2015/9/9.
 */
public class ResultStoryInfo implements Parcelable {

    private String uid;
    private String storyId;
    private int lookNum;
    private int likeNum;
    private int commentNum;

    public int getCommentNum() {
        return commentNum;
    }

    public void setCommentNum(int commentNum) {
        this.commentNum = commentNum;
    }

    public int getLikeNum() {
        return likeNum;
    }

    public void setLikeNum(int likeNum) {
        this.likeNum = likeNum;
    }

    public int getLookNum() {
        return lookNum;
    }

    public void setLookNum(int lookNum) {
        this.lookNum = lookNum;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
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
        dest.writeString(this.uid);
        dest.writeString(this.storyId);
        dest.writeInt(this.lookNum);
        dest.writeInt(this.likeNum);
        dest.writeInt(this.commentNum);
    }

    public ResultStoryInfo() {
    }

    protected ResultStoryInfo(Parcel in) {
        this.uid = in.readString();
        this.storyId = in.readString();
        this.lookNum = in.readInt();
        this.likeNum = in.readInt();
        this.commentNum = in.readInt();
    }

    public static final Creator<ResultStoryInfo> CREATOR = new Creator<ResultStoryInfo>() {
        public ResultStoryInfo createFromParcel(Parcel source) {
            return new ResultStoryInfo(source);
        }

        public ResultStoryInfo[] newArray(int size) {
            return new ResultStoryInfo[size];
        }
    };
}
