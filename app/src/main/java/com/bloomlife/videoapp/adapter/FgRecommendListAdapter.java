package com.bloomlife.videoapp.adapter;

import android.app.Activity;

import com.bloomlife.videoapp.app.Constants;
import com.bloomlife.videoapp.dialog.DialogUtils;
import com.bloomlife.videoapp.model.Recommend;

import java.util.List;

import androidx.fragment.app.FragmentActivity;

/**
 * Created by zxt lan4627@Gmail.com on 2015/8/7.
 */
public class FgRecommendListAdapter extends StatusUserListAdapter<Recommend> {

    public FgRecommendListAdapter(FragmentActivity activity, List<Recommend> dataList) {
        super(activity, dataList);
    }

    @Override
    protected void setNewViewContent(int position, Holder h, Recommend item) {
        setUserInfo(h, item.getUsername(), item.getUsericon(), item.getGender());
        setFansNum(h, item.getFansnum());
    }

    @Override
    protected void startUserInfoDialog(Recommend item) {
        DialogUtils.showUserInfo(activity, item);
    }
}