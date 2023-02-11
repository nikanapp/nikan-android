package com.bloomlife.videoapp.model.result;

import com.bloomlife.android.bean.ProcessResult;
import com.bloomlife.videoapp.common.CommentText;
import com.bloomlife.videoapp.model.Comment;
import com.bloomlife.videoapp.model.StoryVideo;

import java.util.List;

/**
 * Created by zhengxingtian lan4627@Gmail.com on 2015/8/10.
 */
public class GetStoryResult extends ProcessResult {
    private List<Comment> tagsComments;
    private List<CommentText> textComents;
    private List<StoryVideo> videos;
    private String commentagid;
    private boolean islike;

    public List<Comment> getTagsComments() {
        return tagsComments;
    }

    public void setTagsComments(List<Comment> tagsComments) {
        this.tagsComments = tagsComments;
    }

    public List<CommentText> getTextComents() {
        return textComents;
    }

    public void setTextComents(List<CommentText> textComents) {
        this.textComents = textComents;
    }

    public List<StoryVideo> getVideos() {
        return videos;
    }

    public void setVideos(List<StoryVideo> videos) {
        this.videos = videos;
    }

    public String getCommentagid() {
        return commentagid;
    }

    public void setCommentagid(String commentagid) {
        this.commentagid = commentagid;
    }

    public boolean islike() {
        return islike;
    }

    public void setIslike(boolean islike) {
        this.islike = islike;
    }
}
