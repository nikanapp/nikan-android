package com.bloomlife.videoapp.adapter;

import android.app.Activity;
import android.view.View;

import com.bloomlife.videoapp.R;
import com.bloomlife.videoapp.app.Constants;
import com.bloomlife.videoapp.common.util.Utils;
import com.bloomlife.videoapp.dialog.DialogUtils;
import com.bloomlife.videoapp.model.Recommend;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhengxingtian lan4627@Gmail.com on 2015/7/28.
 * 推荐用户列表适配器
 */
public class DgRecommendListAdapter extends UserListAdapter<Recommend> {

    private List<Integer> mStates;
    private boolean mNotSelect;

    public DgRecommendListAdapter(Activity activity, List<Recommend> dataList) {
        super(activity, dataList);
    }

    @Override
    public void setDataList(List<Recommend> dataList) {
        super.setDataList(dataList);
        if (dataList != null){
            mStates = new ArrayList<>(dataList.size());
            for (int i=0; i<dataList.size(); i++)
                mStates.add(Integer.valueOf(i));
        }
    }

    @Override
    protected void setViewContent(int position, Holder h, Recommend item) {
        h.mBtnStatus.setTag(position);
        h.mAvatar.setTag(position);
        setFansNum(h, Integer.valueOf(item.getFansnum()));
        setButtonState(h.mBtnStatus, mStates.contains(position) ? ADDED : UNADD);
        setGenderIcon(h.mUserName, item.getGender().equals(Constants.FEMALE));
        setUserInfo(h, item.getUsername(), item.getUsericon(), item.getGender());
    }

    @Override
    protected void startUserInfoDialog(Recommend item) {
        DialogUtils.showUserInfo(mActivity, item);
    }

    public String getAttentions(){
        StringBuilder builder = new StringBuilder();
        for (int i=0; i<mStates.size(); i++){
            builder.append(getDataList().get(mStates.get(i)).getId()).append(",");
        }
        return builder.substring(0, builder.length()-1);
    }

    public boolean isNotSelect(){
        return mNotSelect;
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (v.getId() == R.id.btn_user_status){
            if (!Utils.isLogin(activity, true)) return;
            int position = (Integer) v.getTag();
            if (mStates.contains(position)){
                setUnAdd(v);
                mStates.remove(Integer.valueOf(position));
                if (mStates.isEmpty()){
                    mNotSelect = true;
                }
            } else {
                setAdded(v);
                if (mStates.isEmpty()){
                    mNotSelect = false;
                }
                mStates.add(Integer.valueOf(position));
            }
            return;
        }
    }

}
