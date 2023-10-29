package com.bloomlife.videoapp.model.result;

import com.bloomlife.android.bean.ProcessResult;
import com.bloomlife.videoapp.model.User;

/**
 * Created by zxt lan4627@Gmail.com on 2015/8/6.
 */
public class GetUserInfoResult extends ProcessResult {

    private User user;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
