package com.bloomlife.videoapp.model.message;

import com.bloomlife.android.bean.BaseMessage;

/**
 * Created by zxt lan4627@Gmail.com on 2015/8/7.
 */
public class GetWeiboFriendsMessage extends BaseMessage {

    private Long time;

    public GetWeiboFriendsMessage(Long time){
        setMsgCode("4021");
        this.time = time;
    }
}
