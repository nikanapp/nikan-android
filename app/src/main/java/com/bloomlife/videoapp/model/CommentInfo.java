package com.bloomlife.videoapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.bloomlife.videoapp.common.CommentText;

import java.util.List;

/**
 * Created by zhengxingtian lan4627@Gmail.com on 2015/9/23.
 */
public class CommentInfo implements Parcelable {

    private List<Comment> commentList;
    private List<CommentText> commentTextList;
    private String selectComment;
    private int allowComment;
    private String storyId;
    private Video video;

    public int getAllowComment() {
        return allowComment;
    }

    public void setAllowComment(int allowComment) {
        this.allowComment = allowComment;
    }

    public List<Comment> getCommentList() {
        return commentList;
    }

    public void setCommentList(List<Comment> commentList) {
        this.commentList = commentList;
    }

    public List<CommentText> getCommentTextList() {
        return commentTextList;
    }

    public void setCommentTextList(List<CommentText> commentTextList) {
        this.commentTextList = commentTextList;
    }

    public String getSelectComment() {
        return selectComment;
    }

    public void setSelectComment(String selectComment) {
        this.selectComment = selectComment;
    }

    public String getStoryId() {
        return storyId;
    }

    public void setStoryId(String storyId) {
        this.storyId = storyId;
    }

    public Video getVideo() {
        return video;
    }

    public void setVideo(Video video) {
        this.video = video;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(commentList);
        dest.writeTypedList(commentTextList);
        dest.writeString(this.selectComment);
        dest.writeInt(this.allowComment);
        dest.writeString(this.storyId);
        dest.writeParcelable(this.video, 0);
    }

    public CommentInfo() {
    }

    protected CommentInfo(Parcel in) {
        this.commentList = in.createTypedArrayList(Comment.CREATOR);
        this.commentTextList = in.createTypedArrayList(CommentText.CREATOR);
        this.selectComment = in.readString();
        this.allowComment = in.readInt();
        this.storyId = in.readString();
        this.video = in.readParcelable(Video.class.getClassLoader());
    }

    public static final Parcelable.Creator<CommentInfo> CREATOR = new Parcelable.Creator<CommentInfo>() {
        public CommentInfo createFromParcel(Parcel source) {
            return new CommentInfo(source);
        }

        public CommentInfo[] newArray(int size) {
            return new CommentInfo[size];
        }
    };
}
