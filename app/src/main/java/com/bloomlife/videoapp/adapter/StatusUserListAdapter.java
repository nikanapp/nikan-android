package com.bloomlife.videoapp.adapter;

import android.app.Activity;
import android.view.View;

import com.bloomlife.android.bean.CacheBean;
import com.bloomlife.videoapp.R;
import com.bloomlife.videoapp.app.Constants;
import com.bloomlife.videoapp.common.util.Utils;
import com.bloomlife.videoapp.model.Status;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhengxingtian lan4627@Gmail.com on 2015/8/5.
 * 有状态按钮的用户列表适配器
 */
public abstract class StatusUserListAdapter<T extends Status> extends UserListAdapter<T> {

    public StatusUserListAdapter(Activity activity, List<T> dataList) {
        super(activity, dataList);
    }

    protected List<Integer> mStates;

    @Override
    public void setDataList(List<T> dataList) {
        super.setDataList(dataList);
        if (dataList == null) return;
        // 将关注状态都存到一个列表中，当某一项的关注状态被改变时就不用去改原来的数据
        // 当要恢复回互相关注时原来的数据好拿来对比，能看到最初时是什么状态。
        mStates = new ArrayList<>(dataList.size());
        for (Status s:dataList)
            mStates.add(s.getStatus());
    }

    @Override
    public void addDataList(List<T> dataList) {
        super.addDataList(dataList);
        for (Status s:dataList)
            mStates.add(s.getStatus());
    }

    @Override
    protected void setViewContent(int position, Holder h, T item) {
        // 设置关注按钮的状态
        setButtonState(h.mBtnStatus, mStates.get(position));
        if (CacheBean.getInstance().getLoginUserId().equals(item.getId())){
            h.mBtnStatus.setVisibility(View.INVISIBLE);
        } else {
            h.mBtnStatus.setVisibility(View.VISIBLE);
            h.mBtnStatus.setTag(R.id.position, position);
        }
        setNewViewContent(position, h, item);
    }

    protected abstract void setNewViewContent(int position, Holder h, T item);

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.btn_user_status:
                // 检查用户是否登陆了
                if (!Utils.isLogin(activity, true)) break;
                int position = (Integer)v.getTag(R.id.position);
                int newState = mStates.get(position);
                int oldState = getDataList().get(position).getStatus();
                if (newState == UNADD){
                    // 用户未被关注
                    if (oldState == Constants.MUTUAL){
                        // 原来是互相关注的，要变回互相关注的按钮
                        setMutual(v);
                        mStates.set(position, MUTUAL);
                    } else {
                        setAdded(v);
                        mStates.set(position, ADDED);
                    }
                } else if (newState == ADDED || newState == MUTUAL){
                    // 取消关注
                    setUnAdd(v);
                    mStates.set(position, UNADD);
                }
                if (mListener != null){
                    mListener.onStatusChange(position, mStates.get(position));
                }
                Status status = (Status) getItem(position);
                sendFollowerMsg(status.getId());
                break;
        }
    }

    public void setStatusList(int position, int status){
        mStates.set(position, status);
    }

    private OnStatusChangeListener mListener;

    public void setOnStatusChangeListener(OnStatusChangeListener listener){
        mListener = listener;
    }

    public interface OnStatusChangeListener {
        void onStatusChange(int position, int status);
    }
}
