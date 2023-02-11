package com.bloomlife.videoapp.model.message;

import com.bloomlife.android.bean.BaseMessage;

/**
 * Created by zhengxingtian lan4627@Gmail.com on 2015/8/3.
 */
public class GetStoryListMessage extends BaseMessage {

    private String pagecursor;

    public GetStoryListMessage(String pagecursor){
        setMsgCode("4010");
        this.pagecursor = pagecursor;
    }

}
