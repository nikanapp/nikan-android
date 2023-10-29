package com.bloomlife.videoapp.model.message;

import com.bloomlife.android.bean.BaseMessage;

/**
 * Created by zxt lan4627@Gmail.com on 2015/7/31.
 */
public class FollowerUserMessage extends BaseMessage {

    private String followerid;

    public FollowerUserMessage(String id){
        setMsgCode("4007");
        this.followerid = id;
    }
}
