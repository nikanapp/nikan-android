package com.bloomlife.videoapp.model.message;

import com.bloomlife.android.bean.BaseMessage;

/**
 * Created by zhengxingtian lan4627@Gmail.com on 2015/8/10.
 */
public class GetStoryMessage extends BaseMessage {

    private String storyid;

    public GetStoryMessage(String storyid){
        setMsgCode("4011");
        this.storyid = storyid;
    }

    public String getStoryid() {
        return storyid;
    }

    public void setStoryid(String storyid) {
        this.storyid = storyid;
    }
}
