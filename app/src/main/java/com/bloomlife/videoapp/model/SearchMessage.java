package com.bloomlife.videoapp.model;

import com.bloomlife.android.bean.BaseMessage;

/**
 * Created by zhengxingtian lan4627@Gmail.com on 2015/8/6.
 */
public class SearchMessage extends BaseMessage {

    private String name;
    private String pagecursor;

    public SearchMessage(String name, String pagecursor){
        setMsgCode("4019");
        this.name = name;
        this.pagecursor = pagecursor;
    }
}
