package com.bloomlife.videoapp.model.message;

import com.bloomlife.android.bean.BaseMessage;

/**
 * Created by zxt lan4627@Gmail.com on 2015/8/4.
 */
public class GetUserStoryListMessage extends BaseMessage {

    private String userid;
    private String pagecursor;

    public GetUserStoryListMessage(String userid, String pagecursor){
        setMsgCode("4009");
        this.userid = userid;
        this.pagecursor = pagecursor;
    }

}
