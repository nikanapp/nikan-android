package com.bloomlife.videoapp.model.message;

import com.bloomlife.android.bean.BaseMessage;
import com.bloomlife.videoapp.dialog.BaseDialog;

/**
 * Created by zxt lan4627@Gmail.com on 2015/7/28.
 */
public class SendAttentionsMessage extends BaseMessage{

    private String attentions;

    public SendAttentionsMessage(String attentions){
        this.attentions = attentions;
        setMsgCode("4004");
    }
}
