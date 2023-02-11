package com.bloomlife.videoapp.model.message;

import com.bloomlife.android.bean.BaseMessage;

/**
 * Created by zhengxingtian lan4627@Gmail.com on 2015/7/31.
 */
public class GetStoryVideoListMessage extends BaseMessage {

    private String storyid;
    private String pagecursor;

    public GetStoryVideoListMessage(String storyid, String pagecursor){
        setMsgCode("4012");
        this.storyid = storyid;
        this.pagecursor = pagecursor;
    }
}
