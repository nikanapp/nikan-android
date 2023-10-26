package com.bloomlife.videoapp.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bloomlife.videoapp.R;
import com.bloomlife.videoapp.common.util.ShareSDKUtil;
import com.bloomlife.videoapp.common.util.UIHelper;
import com.bloomlife.videoapp.dialog.DialogUtils;
import com.bloomlife.videoapp.model.InviteUser;

import java.util.List;

import androidx.fragment.app.FragmentActivity;
import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

/**
 * Created by zhengxingtian lan4627@Gmail.com on 2015/8/5.
 */
public class InviteSocialAdapter extends StatusUserListAdapter<InviteUser> implements StickyListHeadersAdapter {

    public InviteSocialAdapter(FragmentActivity activity, List<InviteUser> dataList) {
        super(activity, dataList);

    }

    @Override
    protected void setNewViewContent(int position, Holder h, InviteUser item) {
        if (item.getType() == InviteUser.JOINED){
            h.mBtnInvite.setVisibility(View.GONE);
            h.mBtnStatus.setVisibility(View.VISIBLE);
        } else {
            h.mBtnStatus.setVisibility(View.GONE);
            h.mBtnInvite.setVisibility(View.VISIBLE);
        }
        h.mBtnStatus.setTag(R.id.position, position);
        h.mBtnInvite.setTag(R.id.position, position);
    }

    @Override
    protected void startUserInfoDialog(InviteUser item) {
        DialogUtils.showUserInfo(activity, item);
    }

    @Override
    public long getHeaderId(int position) {
        return getDataList().get(position).getType();
    }

    @Override
    public View getHeaderView(int position, View convertView, ViewGroup parent) {
        TextView title;
        if (convertView == null){
            title = UIHelper.getInviteTitle(activity);
        } else {
            title = (TextView) convertView;
        }
        title.setText(getDataList().get(position).getType() == InviteUser.JOINED
                        ? getString(R.string.fragment_invite_title_joined)
                        : getString(R.string.fragment_invite_title_not_joined));
        return title;
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.btn_user_invite:
                ShareSDKUtil.shareAppToWeibo(activity);
                break;
        }
    }
}
