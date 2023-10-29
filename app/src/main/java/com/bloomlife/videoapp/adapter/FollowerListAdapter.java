package com.bloomlife.videoapp.adapter;

import android.app.Activity;
import android.view.View;

import com.bloomlife.videoapp.app.Constants;
import com.bloomlife.videoapp.dialog.DialogUtils;
import com.bloomlife.videoapp.model.Follower;

import java.util.List;

import androidx.fragment.app.FragmentActivity;

/**
 * Created by zxt lan4627@Gmail.com on 2015/7/29.
 */
public class FollowerListAdapter extends StatusUserListAdapter<Follower> {


    public FollowerListAdapter(FragmentActivity activity, List<Follower> dataList) {
        super(activity, dataList);
    }

    @Override
    protected void setNewViewContent(int position, Holder h, Follower item) {
        setUserInfo(h, item.getUsername(), item.getUsericon(), item.getGender());
        setFansNum(h, item.getFansnum());
    }

    @Override
    protected void startUserInfoDialog(Follower item) {
        DialogUtils.showUserInfo(activity, item);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
    }
}
