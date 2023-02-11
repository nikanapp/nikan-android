package com.bloomlife.videoapp.model.message;

import com.bloomlife.android.bean.BaseMessage;

/**
 * Created by zhengxingtian lan4627@Gmail.com on 2015/7/31.
 */
public class FollowerListMessage extends BaseMessage {

    private String userid;
    private String pagecursor;

    public FollowerListMessage(String userid){
        this(userid, null);
    }

    public FollowerListMessage(String userid, String pagecursor){
        setMsgCode("4005");
        this.userid = userid;
        this.pagecursor = pagecursor;
    }
}
