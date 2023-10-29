package com.bloomlife.videoapp.model.result;

import com.bloomlife.android.bean.ProcessResult;
import com.bloomlife.videoapp.model.Recommend;
import com.bloomlife.videoapp.model.User;

import java.util.List;

/**
 * Created by zxt lan4627@Gmail.com on 2015/9/16.
 */
public class GetFriendListResult extends ProcessResult {

    private List<User> recommends;

    public GetFriendListResult(){

    }

    public List<User> getRecommends() {
        return recommends;
    }

    public void setRecommends(List<User> recommends) {
        this.recommends = recommends;
    }
}
