package com.bloomlife.videoapp.adapter;

import android.app.Activity;
import android.view.View;

import com.bloomlife.videoapp.app.Constants;
import com.bloomlife.videoapp.common.util.UIHelper;
import com.bloomlife.videoapp.dialog.DialogUtils;
import com.bloomlife.videoapp.dialog.UserInfoDialog;
import com.bloomlife.videoapp.model.User;

import java.util.List;

import androidx.fragment.app.FragmentActivity;

/**
 * Created by zhengxingtian lan4627@Gmail.com on 2015/8/6.
 */
public class SearchAdapter extends StatusUserListAdapter<User> {

    public SearchAdapter(FragmentActivity activity, List<User> dataList) {
        super(activity, dataList);
        haveLastLine(true);
    }

    @Override
    protected void initUserView(Holder h) {
        super.initUserView(h);
        h.mUserDescription.setVisibility(View.VISIBLE);
        h.mUserFollowerNum.setVisibility(View.GONE);
    }

    @Override
    protected void setNewViewContent(int position, Holder h, User item) {
        setUserInfo(h, item.getUsername(), item.getUsericon(), item.getGender());
        h.mUserDescription.setText(item.getUsersign());
    }

    @Override
    protected void startUserInfoDialog(User item) {
        UIHelper.hideSoftInput(activity, activity.getWindow().getDecorView());
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