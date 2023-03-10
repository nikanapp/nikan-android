package com.bloomlife.videoapp.model.result;

import com.bloomlife.android.bean.ProcessResult;
import com.bloomlife.videoapp.model.Follower;

import java.util.List;

/**
 * Created by zhengxingtian lan4627@Gmail.com on 2015/7/31.
 */
public class FollowerListResult extends ProcessResult {

    private List<Follower> users;
    private String pagecursor;

    public FollowerListResult(){

    }

    public List<Follower> getUsers() {
        return users;
    }

    public void setUsers(List<Follower> followerList) {
        this.users = followerList;
    }

    public String getPagecursor() {
        return pagecursor;
    }

    public void setPagecursor(String pagecursor) {
        this.pagecursor = pagecursor;
    }
}
