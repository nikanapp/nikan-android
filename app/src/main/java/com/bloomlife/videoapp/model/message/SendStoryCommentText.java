package com.bloomlife.videoapp.model.message;

import com.bloomlife.android.bean.BaseMessage;

/**
 * Created by zhengxingtian lan4627@Gmail.com on 2015/8/24.
 */
public class SendStoryCommentText extends BaseMessage{

    private String storyid;
    private String content;

    public SendStoryCommentText(String story, String content){
        setMsgCode("4016");
        this.storyid = story;
        this.content = content;
    }

    public String getStoryid() {
        return storyid;
    }

    public void setStoryid(String storyid) {
        this.storyid = storyid;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
