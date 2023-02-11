package com.bloomlife.videoapp.model.message;

import com.bloomlife.android.bean.BaseMessage;

/**
 * Description:
 * User: ZhengXingtian(lan4627@Gmail.com)
 * Date: 2015-08-31
 * Time: 21:49
 * Version: 1.0
 */
public class DeleteRealCommentTextMessage extends BaseMessage {

    private String storyid;
    private String cid;

    public DeleteRealCommentTextMessage(String storyid, String cid){
        setMsgCode("4017");
        this.storyid = storyid;
        this.cid = cid;
    }

}
