package com.bloomlife.videoapp.model.result;

import com.bloomlife.android.bean.ProcessResult;
import com.bloomlife.videoapp.model.InviteUser;
import com.bloomlife.videoapp.model.User;

import java.util.List;

/**
 * Created by zxt lan4627@Gmail.com on 2015/8/7.
 */
public class GetWeiboFriendResult extends ProcessResult {

    private List<InviteUser> friends;
    private List<InviteUser> unjoinfriends;

    public List<InviteUser> getFriends() {
        return friends;
    }

    public void setFriends(List<InviteUser> friends) {
        this.friends = friends;
    }

    public List<InviteUser> getUnjoinfriends() {
        return unjoinfriends;
    }

    public void setUnjoinfriends(List<InviteUser> unjoinfriends) {
        this.unjoinfriends = unjoinfriends;
    }
}
