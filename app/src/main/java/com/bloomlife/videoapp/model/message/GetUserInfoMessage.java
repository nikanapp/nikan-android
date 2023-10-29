package com.bloomlife.videoapp.model.message;

import com.bloomlife.android.bean.BaseMessage;

/**
 * Created by zxt lan4627@Gmail.com on 2015/8/6.
 */
public class GetUserInfoMessage extends BaseMessage {

    private String userid;

    public GetUserInfoMessage(String userId){
        setMsgCode("4020");
        this.userid = userId;
    }
}
