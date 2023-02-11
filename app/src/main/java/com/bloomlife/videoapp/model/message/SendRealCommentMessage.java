package com.bloomlife.videoapp.model.message;

import com.bloomlife.android.bean.BaseMessage;

/**
 * Created by zhengxingtian lan4627@Gmail.com on 2015/9/15.
 */
public class SendRealCommentMessage extends BaseMessage {

    private String storyid;
    private String commenttagId;

    public SendRealCommentMessage(String storyid, String commenttagId){
        setMsgCode("4015");
        this.storyid = storyid;
        this.commenttagId = commenttagId;
    }
}
