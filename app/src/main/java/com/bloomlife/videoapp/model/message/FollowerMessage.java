package com.bloomlife.videoapp.model.message;

import com.bloomlife.android.bean.BaseMessage;

/**
 * Created by zhengxingtian lan4627@Gmail.com on 2015/8/7.
 */
public class FollowerMessage extends BaseMessage {

    private String followerid;

    public FollowerMessage(String followerid){
        setMsgCode("4007");
        this.followerid = followerid;
    }
}
