package com.bloomlife.videoapp.adapter;

import android.app.Activity;
import android.view.View;

import com.bloomlife.videoapp.app.Constants;
import com.bloomlife.videoapp.dialog.DialogUtils;
import com.bloomlife.videoapp.model.Fans;

import java.util.ArrayList;
import java.util.List;

import androidx.fragment.app.FragmentActivity;

/**
 * Created by zxt lan4627@Gmail.com on 2015/7/31.
 */
public class FansListAdapter extends StatusUserListAdapter<Fans> {

    public FansListAdapter(FragmentActivity activity, List<Fans> dataList) {
        super(activity, dataList);
    }

    @Override
    protected void setNewViewContent(int position, Holder h, Fans item) {
        setUserInfo(h, item.getUsername(), item.getUsericon(), item.getGender());
        setFansNum(h, item.getFansnum());
    }

    @Override
    protected void startUserInfoDialog(Fans item) {
        DialogUtils.showUserInfo(activity, item);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
    }
}
