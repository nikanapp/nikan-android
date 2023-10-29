package com.bloomlife.videoapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by zxt lan4627@Gmail.com on 2015/8/24.
 */
public class UserStory implements Parcelable {

    private String storyid;
    private String userId;
    private int likeNum;
    private int looknum;
    private int commentnum;
    private List<StoryVideo> videos;

    public int getLikeNum() {
        return likeNum;
    }

    public void setLikeNum(int likeNum) {
        this.likeNum = likeNum;
    }

    public String getStoryid() {
        return storyid;
    }

    public void setStoryid(String storyid) {
        this.storyid = storyid;
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

    public List<StoryVideo> getVideos() {
        return videos;
    }

    public void setVideos(List<StoryVideo> videos) {
        this.videos = videos;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.storyid);
        dest.writeString(this.userId);
        dest.writeInt(this.likeNum);
        dest.writeInt(this.looknum);
        dest.writeInt(this.commentnum);
        dest.writeTypedList(videos);
    }

    public UserStory() {
    }

    protected UserStory(Parcel in) {
        this.storyid = in.readString();
        this.userId = in.readString();
        this.likeNum = in.readInt();
        this.looknum = in.readInt();
        this.commentnum = in.readInt();
        this.videos = in.createTypedArrayList(StoryVideo.CREATOR);
    }

    public static final Creator<UserStory> CREATOR = new Creator<UserStory>() {
        public UserStory createFromParcel(Parcel source) {
            return new UserStory(source);
        }

        public UserStory[] newArray(int size) {
            return new UserStory[size];
        }
    };
}
