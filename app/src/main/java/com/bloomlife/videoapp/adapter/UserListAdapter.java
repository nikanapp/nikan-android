package com.bloomlife.videoapp.adapter;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.MessageRequest;
import com.android.volley.toolbox.Volley;
import com.bloomlife.android.framework.AbstractAdapter;
import com.bloomlife.videoapp.R;
import com.bloomlife.videoapp.app.Constants;
import com.bloomlife.videoapp.common.util.ImageLoaderUtils;
import com.bloomlife.videoapp.common.util.UIHelper;
import com.bloomlife.videoapp.common.util.Utils;
import com.bloomlife.videoapp.dialog.DialogUtils;
import com.bloomlife.videoapp.model.Account;
import com.bloomlife.videoapp.model.message.FollowerUserMessage;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

import androidx.fragment.app.FragmentActivity;

/**
 * Created by zxt lan4627@Gmail.com on 2015/7/28.
 * 用户列表
 */
public abstract class UserListAdapter<T> extends AbstractAdapter<T> implements View.OnClickListener {

    class Holder{
        ViewGroup mGroup;
        ImageView mAvatar;
        TextView mUserName;
        TextView mUserFollowerNum;
        ImageView mBtnStatus;
        TextView mBtnInvite;
        TextView mUserDescription;
        View mDivider;
    }

    public static final int UNADD = 0;
    public static final int ADDED = 1;
    public static final int MUTUAL = 2;

    private LayoutInflater mInflater;

    protected FragmentActivity mActivity;
    protected Drawable mFemale;
    protected Drawable mMale;
    protected ImageLoader mImageLoader;
    protected DisplayImageOptions mOptions;
    private boolean mHaveLastLine;

    public UserListAdapter(FragmentActivity activity, List<T> dataList) {
        super(activity, dataList);
        mInflater = LayoutInflater.from(activity);
        mActivity = activity;
        mFemale = activity.getResources().getDrawable(R.drawable.icon_user_info_female);
        mFemale.setBounds(0, 0, mFemale.getMinimumWidth(), mFemale.getMinimumHeight());
        mMale = activity.getResources().getDrawable(R.drawable.icon_user_info_male);
        mMale.setBounds(0, 0, mFemale.getMinimumWidth(), mFemale.getMinimumHeight());
        mImageLoader = ImageLoader.getInstance();
        mOptions = ImageLoaderUtils.getCircleLoadOptions();
        setDataList(dataList);
    }


    @Override
    protected View initItemView(int position, ViewGroup parent) {
        Holder h = new Holder();
        View v = mInflater.inflate(R.layout.item_user, parent, false);
        h.mGroup = (ViewGroup) v.findViewById(R.id.item_user_layout);
        h.mAvatar = (ImageView) v.findViewById(R.id.item_user_avatar);
        h.mUserName = (TextView) v.findViewById(R.id.item_user_name);
        h.mUserFollowerNum = (TextView) v.findViewById(R.id.item_user_follower_num);
        h.mUserDescription = (TextView) v.findViewById(R.id.item_user_description);
        h.mBtnStatus = (ImageView) v.findViewById(R.id.btn_user_status);
        h.mBtnInvite = (TextView) v.findViewById(R.id.btn_user_invite);
        h.mDivider = v.findViewById(R.id.item_user_divider);
        v.findViewById(R.id.btn_user_layout).setOnClickListener(this);
        initUserView(h);
        v.setTag(h);
        return v;
    }

    protected void initUserView(Holder h){
        h.mGroup.setOnClickListener(this);
        h.mBtnStatus.setOnClickListener(this);
        h.mBtnInvite.setOnClickListener(this);
        h.mUserFollowerNum.setTypeface(UIHelper.getHelveticaTh(mActivity));
    }

    public void setGenderIcon(TextView tv, boolean isFemale){
        tv.setCompoundDrawables(null, null, isFemale ? mFemale : mMale, null);
    }

    public void setFansNum(Holder h, int num){
        h.mUserFollowerNum.setText(num + " " + mActivity.getString(R.string.item_user_followers));
    }

    public void setUserInfo(Holder h, String name, String icon, String gender){
        h.mUserName.setText(name);
        setGenderIcon(h.mUserName, Constants.FEMALE.equals(gender));
        if (!TextUtils.isEmpty(icon) && !icon.equals(h.mAvatar.getTag())){
            h.mAvatar.setTag(icon);
            mImageLoader.displayImage(icon, h.mAvatar, mOptions);
        }
    }

    @Override
    protected void setViewContent(int position, View convertView, T item) {
        Holder h = (Holder) convertView.getTag();
        h.mGroup.setTag(R.id.position, position);
        if (!mHaveLastLine && position == getCount()-1){
            h.mDivider.setVisibility(View.INVISIBLE);
        } else {
            h.mDivider.setVisibility(View.VISIBLE);
        }
        setViewContent(position, h, item);
    }

    protected void haveLastLine(boolean lastLine){
        mHaveLastLine = lastLine;
    }

    protected abstract void setViewContent(int position, Holder h, T item);

    protected abstract void startUserInfoDialog(T item);

    protected void setButtonState(View v, int status){
        switch (status){
            case UNADD:
                setUnAdd(v);
                break;

            case ADDED:
                setAdded(v);
                break;

            case MUTUAL:
                setMutual(v);
                break;

        }
    }

    protected void setAdded(View v){
        v.setBackgroundResource(R.drawable.btn_follow_added_selector);
    }

    protected void setUnAdd(View v){
        v.setBackgroundResource(R.drawable.btn_follow_add_selector);
    }

    protected void setMutual(View v){
        v.setBackgroundResource(R.drawable.btn_follow_mutual_selector);
    }

    protected void sendFollowerMsg(String id){
        Volley.addToTagQueue(new MessageRequest(new FollowerUserMessage(id)));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.item_user_layout:
                int position = (Integer)v.getTag(R.id.position);
                startUserInfoDialog(getDataList().get(position));
                break;
        }
    }

    public String getString(int resId){
        return activity.getString(resId);
    }
}
