package com.bloomlife.videoapp.adapter;

import android.app.Activity;

import com.bloomlife.videoapp.dialog.DialogUtils;
import com.bloomlife.videoapp.dialog.UserInfoDialog;
import com.bloomlife.videoapp.model.InviteUser;
import com.bloomlife.videoapp.model.Recommend;
import com.bloomlife.videoapp.model.User;

import java.util.List;

/**
 * Created by zhengxingtian lan4627@Gmail.com on 2015/9/16.
 */
public class InviteAdapter extends StatusUserListAdapter<User> {

    public InviteAdapter(Activity activity, List<User> dataList) {
        super(activity, dataList);
    }

    @Override
    protected void setNewViewContent(int position, Holder h, User item) {
        setUserInfo(h, item.getUsername(), item.getUsericon(), item.getGender());
        setFansNum(h, item.getFansnum());
    }

    @Override
    protected void startUserInfoDialog(User item) {
        DialogUtils.showUserInfo(activity, item, mChangeListener);
    }

    private UserInfoDialog.OnUserStatusChangeListener mChangeListener = new UserInfoDialog.OnUserStatusChangeListener() {
        @Override
        public void onUserStatusChange(String userId, int status) {
            // 更新列表上的用户状态
            for (int i=0; i<getDataList().size(); i++){
                User u = getDataList().get(i);
                if (u.getId().equals(userId)){
                    setStatusList(i, status);
                    notifyDataSetChanged();
                    return;
                }
            }
        }
    };
}
