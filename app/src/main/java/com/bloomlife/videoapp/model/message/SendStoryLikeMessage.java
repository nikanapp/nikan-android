package com.bloomlife.videoapp.model.message;

import com.bloomlife.android.bean.BaseMessage;

/**
 * Created by zhengxingtian lan4627@Gmail.com on 2015/8/11.
 */
public class SendStoryLikeMessage extends BaseMessage {

    private String storyid;

    public SendStoryLikeMessage(String storyid){
        setMsgCode("4013");
        this.storyid = storyid;
    }
}
