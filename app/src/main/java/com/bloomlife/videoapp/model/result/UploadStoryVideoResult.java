package com.bloomlife.videoapp.model.result;

import com.bloomlife.android.bean.ProcessResult;

/**
 * Created by zhengxingtian lan4627@Gmail.com on 2015/8/10.
 */
public class UploadStoryVideoResult extends ProcessResult {
    private String videoid;
    private String videouri;
    private long createtime;

    public String getVideoid() {
        return videoid;
    }

    public void setVideoid(String videoid) {
        this.videoid = videoid;
    }

    public String getVideouri() {
        return videouri;
    }

    public void setVideouri(String videouri) {
        this.videouri = videouri;
    }

    public long getCreatetime() {
        return createtime;
    }

    public void setCreatetime(long createtime) {
        this.createtime = createtime;
    }
}
