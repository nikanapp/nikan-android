package com.bloomlife.videoapp.adapter;

import android.app.Activity;
import android.view.View;

import com.bloomlife.videoapp.app.Constants;
import com.bloomlife.videoapp.dialog.DialogUtils;
import com.bloomlife.videoapp.model.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhengxingtian lan4627@Gmail.com on 2015/7/31.
 */
public class LikeListAdapter extends StatusUserListAdapter<User> {

    private List<Integer> mStates;

    public LikeListAdapter(Activity activity, List<User> dataList) {
        super(activity, dataList);
    }

    @Override
    public void setDataList(List<User> dataList) {
        super.setDataList(dataList);
        if (dataList == null) return;
        mStates = new ArrayList<>(dataList.size());
        for (User f:dataList)
            mStates.add(f.getStatus());
    }

    @Override
    public void addDataList(List<User> dataList) {
        super.addDataList(dataList);
        for (User f:dataList)
            mStates.add(f.getStatus());
    }

    @Override
    protected void setNewViewContent(int position, Holder h, User item) {
        h.mBtnStatus.setTag(position);
        h.mAvatar.setTag(position);
        setFansNum(h, item.getFansnum());
        setButtonState(h.mBtnStatus, mStates.get(position));
        setUserInfo(h, item.getUsername(), item.getUsericon(), item.getGender());
    }

    @Override
    protected void startUserInfoDialog(User item) {
        DialogUtils.showUserInfo(activity, item);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
    }
}
