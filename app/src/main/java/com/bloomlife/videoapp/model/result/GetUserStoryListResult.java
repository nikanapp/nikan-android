package com.bloomlife.videoapp.model.result;

import com.bloomlife.android.bean.ProcessResult;
import com.bloomlife.videoapp.model.Story;
import com.bloomlife.videoapp.model.StoryVideo;

import java.util.List;

/**
 * Created by zhengxingtian lan4627@Gmail.com on 2015/8/4.
 */
public class GetUserStoryListResult extends ProcessResult {

    private String heat;
    private String storyid;
    private int looknum;
    private int commentnum;
    private int likenum;
    private List<StoryVideo> videos;
    private String pagecursor;

    public String getPagecursor() {
        return pagecursor;
    }

    public void setPagecursor(String pagecursor) {
        this.pagecursor = pagecursor;
    }

    public String getHeat() {
        return heat;
    }

    public void setHeat(String heat) {
        this.heat = heat;
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

    public int getLikenum() {
        return likenum;
    }

    public void setLikenum(int likenum) {
        this.likenum = likenum;
    }
}
