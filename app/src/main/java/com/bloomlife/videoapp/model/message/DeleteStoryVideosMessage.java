package com.bloomlife.videoapp.model.message;

import com.bloomlife.android.bean.BaseMessage;

/**
 * Created by zhengxingtian lan4627@Gmail.com on 2015/9/15.
 */
public class DeleteStoryVideosMessage extends BaseMessage {

    private String videoids;

    public DeleteStoryVideosMessage(String videoids){
        setMsgCode("4023");
        this.videoids = videoids;
    }
}
