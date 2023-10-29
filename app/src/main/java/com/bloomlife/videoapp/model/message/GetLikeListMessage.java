package com.bloomlife.videoapp.model.message;

import com.bloomlife.android.bean.BaseMessage;

/**
 * Created by zxt lan4627@Gmail.com on 2015/7/31.
 */
public class GetLikeListMessage extends BaseMessage {

    private String storyid;
    private String pagecursor;

    public GetLikeListMessage(String storyid, String pagecursor){
        setMsgCode("4014");
        this.storyid = storyid;
        this.pagecursor = pagecursor;
    }
}
