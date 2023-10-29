package com.bloomlife.videoapp.model.message;

import com.bloomlife.android.bean.BaseMessage;

/**
 * Created by zxt lan4627@Gmail.com on 2015/7/31.
 */
public class FansListMessage extends BaseMessage {

    private String userid;
    private String pagecursor;

    public FansListMessage(String userid){
        this(userid, null);
    }

    public FansListMessage(String userid, String pagecursor){
        setMsgCode("4006");
        this.userid = userid;
        this.pagecursor = pagecursor;
    }
}
