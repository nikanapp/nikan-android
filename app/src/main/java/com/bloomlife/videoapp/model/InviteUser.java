package com.bloomlife.videoapp.model;

/**
 * Created by zhengxingtian lan4627@Gmail.com on 2015/8/6.
 */
public class InviteUser extends User{

    public static final int JOINED = 1;
    public static final int NOT_JOINED = 2;

    private int type;
    private long time;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
