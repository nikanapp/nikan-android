package com.bloomlife.videoapp.model.result;

import com.bloomlife.android.bean.ProcessResult;
import com.bloomlife.videoapp.model.StoryVideo;

import java.util.List;

/**
 * Created by zhengxingtian lan4627@Gmail.com on 2015/7/31.
 */
public class GetStoryVideoListResult extends ProcessResult{

    private List<StoryVideo> videos;

    public List<StoryVideo> getVideos() {
        return videos;
    }

    public void setVideos(List<StoryVideo> videos) {
        this.videos = videos;
    }
}
