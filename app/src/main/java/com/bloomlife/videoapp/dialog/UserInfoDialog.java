package com.bloomlife.videoapp.dialog;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.app.Activity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import androidx.viewpager.widget.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.MessageRequest;
import com.android.volley.toolbox.Volley;
import com.bloomlife.android.bean.CacheBean;
import com.bloomlife.android.bean.ProcessResult;
import com.bloomlife.android.common.util.UiUtils;
import com.bloomlife.android.log.Logger;
import com.bloomlife.videoapp.R;
import com.bloomlife.videoapp.activity.RealNameChatActivity;
import com.bloomlife.videoapp.activity.fragment.FansListFragment;
import com.bloomlife.videoapp.activity.fragment.FollowerListFragment;
import com.bloomlife.videoapp.adapter.AttentionPageAdapter;
import com.bloomlife.videoapp.app.Constants;
import com.bloomlife.videoapp.app.RequestErrorAlertListener;
import com.bloomlife.videoapp.common.util.ImageLoaderUtils;
import com.bloomlife.videoapp.common.util.UIHelper;
import com.bloomlife.videoapp.common.util.Utils;
import com.bloomlife.videoapp.model.User;
import com.bloomlife.videoapp.model.UserInfo;
import com.bloomlife.videoapp.model.message.FollowerMessage;
import com.bloomlife.videoapp.model.message.GetUserInfoMessage;
import com.bloomlife.videoapp.model.result.GetUserInfoResult;
import com.bloomlife.videoapp.view.SliderView;
import com.nostra13.universalimageloader.core.ImageLoader;

import net.tsz.afinal.annotation.view.ViewInject;

import java.util.LinkedList;

/**
 * Created by zhengxingtian lan4627@Gmail.com on 2015/7/27.
 * @parameter INTENT_USER_INFO 传用户的信息进来，使用UserInfo类。
 */
public class UserInfoDialog extends BaseDialog implements FansListFragment.StatusChangeListener, FollowerListFragment.StatusChangeListener {

    private static final String TAG = UserInfoDialog.class.getSimpleName();
    public static final String INTENT_USER_INFO = "userInfo";
    public static final int WINDOW_SCALE_DURATION = 400;

    @ViewInject(id= R.id.user_info_icon)
    private ImageView mUserIcon;

    @ViewInject(id= R.id.user_info_name)
    private TextView mUserName;

    @ViewInject(id=R.id.user_info_description)
    private TextView mUserDescription;

    @ViewInject(id=R.id.user_info_btn_close, click=ViewInject.DEFAULT)
    private ImageView mBtnClose;

    @ViewInject(id=R.id.user_info_status, click=ViewInject.DEFAULT)
    private ImageView mBtnStatus;

    @ViewInject(id=R.id.user_info_message, click=ViewInject.DEFAULT)
    private ImageView mBtnMessage;

    @ViewInject(id=R.id.user_info_stories, click=ViewInject.DEFAULT)
    private TextView mStories;

    @ViewInject(id=R.id.user_info_stories_text, click=ViewInject.DEFAULT)
    private TextView mStoriesText;

    @ViewInject(id=R.id.user_info_followers, click=ViewInject.DEFAULT)
    private TextView mFollowers;

    @ViewInject(id=R.id.user_info_followers_text, click=ViewInject.DEFAULT)
    private TextView mFollowersText;

    @ViewInject(id=R.id.user_info_following, click=ViewInject.DEFAULT)
    private TextView mFollowing;

    @ViewInject(id=R.id.user_info_following_text, click=ViewInject.DEFAULT)
    private TextView mFollowingText;

    @ViewInject(id=R.id.user_info_attention_table)
    private ViewPager mAttentionViewPager;

    @ViewInject(id=R.id.user_info_attention, click=ViewInject.DEFAULT)
    private ViewGroup mAttentionGroup;

    @ViewInject(id=R.id.user_info_detail)
    private ViewGroup mUserInfoDetailGroup;

    @ViewInject(id=R.id.user_info_window)
    private ViewGroup mUserInfoWindow;

    @ViewInject(id=R.id.user_info_title, click=ViewInject.DEFAULT)
    private TextView mTitle;

    @ViewInject(id=R.id.user_info_slider)
    private SliderView mSlider;

    private AttentionPageAdapter mAdapter;

    private boolean mShowAttention;
    private int mViewPagerHeight;
    private int mViewPagerMarginTop;
    private int mReduceUserStatusHeight;

    private int mOldUserStatus;
    private int mNowUserStatus;

    private UserInfo mUserInfo;

    @Override
    protected int getLayoutResId() {
        return R.layout.dialog_user_info;
    }

    @Override
    protected void initLayout(View layout) {
        UserInfo userInfo = getArguments().getParcelable(INTENT_USER_INFO);
        setUserInfo(userInfo);
        setFollower(userInfo);
        // 获取最新用户数据
        getNewUserInfo(userInfo.getId());

        Typeface bebas = UIHelper.getHelveticaTh(getActivity());

        mStories.setTypeface(bebas);
        mStoriesText.setTypeface(bebas);
        mFollowers.setTypeface(bebas);
        mFollowersText.setTypeface(bebas);
        mFollowing.setTypeface(bebas);
        mFollowingText.setTypeface(bebas);

        mViewPagerHeight = getActivity().getResources().getDisplayMetrics().heightPixels
                - UIHelper.getStatusBarHeight(getActivity())
                - UiUtils.dip2px(getActivity(), 144);

        if (CacheBean.getInstance().getLoginUserId().equals(userInfo.getId())){
            // 如果是自己的信息卡片，隐藏状态按钮
            mBtnMessage.setVisibility(View.INVISIBLE);
            mBtnStatus.setVisibility(View.GONE);
            mReduceUserStatusHeight = getResources().getDimensionPixelSize(R.dimen.dialog_btn_user_status_height);

            // 状态按钮被隐藏时要把窗口高度和粉丝、关注列表的高度减去这一部分。
            ViewGroup.LayoutParams windowParams = mUserInfoWindow.getLayoutParams();
            windowParams.height -= mReduceUserStatusHeight;
            mViewPagerHeight -= mReduceUserStatusHeight;
        }

        mAdapter = new AttentionPageAdapter(getPageFragmentManager(), userInfo.getId(), this, this);
        mAttentionViewPager.setAdapter(mAdapter);
        mAttentionViewPager.setOnPageChangeListener(mPageChangeListener);
        mAttentionViewPager.setOffscreenPageLimit(2);
        ViewGroup.LayoutParams params = mAttentionViewPager.getLayoutParams();
        params.height = mViewPagerHeight;
        mAttentionViewPager.setLayoutParams(params);
        mSlider.setNumber(3);
        mSlider.setSliderLeftAndRightPadding(UiUtils.dip2px(getActivity(), 24));
        mViewPagerMarginTop = UiUtils.dip2px(getActivity(), 6);
    }

    private void setUserInfo(UserInfo userInfo){
        if (getActivity() == null)
            return;
        if (!TextUtils.isEmpty(userInfo.getIconUrl()) && !userInfo.getIconUrl().equals(mUserIcon.getTag())){
            mUserIcon.setTag(userInfo.getIconUrl());
            ImageLoader.getInstance().displayImage(
                    userInfo.getIconUrl(),
                    mUserIcon,
                    ImageLoaderUtils.getDecodingOptions(R.drawable.circle_avatar, R.drawable.circle_avatar));
        }
        mUserName.setText(userInfo.getName());
        mTitle.setText(userInfo.getName());
        Drawable genderIcon = getResources().getDrawable(
                Constants.MALE.equals(userInfo.getGender())
                        ? R.drawable.icon_user_info_male
                        : R.drawable.icon_user_info_female);
        genderIcon.setBounds(0, 0, genderIcon.getMinimumWidth(), genderIcon.getMinimumHeight());
        mUserName.setCompoundDrawables(null, null, genderIcon, null);
        if (userInfo.getStatus() != UserInfo.NONE){
            setBtnStatus(userInfo);
        } else {
            mBtnStatus.setEnabled(false);
        }
        mUserInfo = userInfo;
    }

    private void setFollower(UserInfo userInfo){
        mUserDescription.setText(userInfo.getDescription());
        mStories.setText(String.valueOf(userInfo.getStoriesNum()));
        mFollowers.setText(String.valueOf(userInfo.getFansNum()));
        mFollowing.setText(String.valueOf(userInfo.getFollowerNum()));

        mOldUserStatus = userInfo.getStatus();
        mNowUserStatus = userInfo.getStatus();

        mUserInfo = userInfo;
    }

    private void setBtnStatus(UserInfo userInfo){
        if (!userInfo.getId().equals(CacheBean.getInstance().getLoginUserId())){
            mBtnStatus.setImageResource(getBtnStatusRes(userInfo.getStatus()));
            mBtnStatus.setEnabled(true);
        }
    }

    private int getBtnStatusRes(int status){
        switch (status){
            case Constants.UNADD:
                return R.drawable.btn_follow_add;

            default:
            case Constants.ADDED:
                return R.drawable.btn_follow_added;

            case Constants.MUTUAL:
                return R.drawable.btn_follow_mutual;
        }
    }

    private void clickBtnStatus(){
        switch (mNowUserStatus) {
            case Constants.UNADD:
                // 设置按钮状态
                if (mOldUserStatus == Constants.MUTUAL){
                    mBtnStatus.setImageResource(getBtnStatusRes(Constants.MUTUAL));
                    mNowUserStatus = Constants.MUTUAL;
                } else {
                    mBtnStatus.setImageResource(getBtnStatusRes(Constants.ADDED));
                    mNowUserStatus = Constants.ADDED;
                }
                mUserInfo.setFansNum(mUserInfo.getFansNum() + 1);
                break;

            case Constants.MUTUAL:
            case Constants.ADDED:
                mBtnStatus.setImageResource(getBtnStatusRes(Constants.UNADD));
                mNowUserStatus = Constants.UNADD;
                mUserInfo.setFansNum(mUserInfo.getFansNum() - 1);
                break;
        }
        mFollowers.setText(String.valueOf(mUserInfo.getFansNum()));
        if (mListener != null){
            mListener.onUserStatusChange(mUserInfo.getId(), mNowUserStatus);
        }
        // 发送关注用户请求
        Volley.addToTagQueue(new MessageRequest(new FollowerMessage(mUserInfo.getId()), new MessageRequest.Listener<ProcessResult>() {
            @Override
            public void success(ProcessResult result) {
                Logger.i(TAG, "关注用户成功 "+mUserInfo.getId());
                refreshFansList();
            }
        }));
    }

    private void getNewUserInfo(String userId){
        Volley.addToTagQueue(new MessageRequest(new GetUserInfoMessage(userId), new RequestErrorAlertListener<GetUserInfoResult>() {
            @Override
            public void success(GetUserInfoResult result) {
                User user = result.getUser();
                mUserInfo.setName(user.getUsername());
                mUserInfo.setFansNum(user.getFansnum());
                mUserInfo.setFollowerNum(user.getFollownum());
                mUserInfo.setGender(user.getGender());
                mUserInfo.setIconUrl(user.getUsericon());
                mUserInfo.setDescription(user.getUsersign());
                mUserInfo.setStatus(user.getStatus());
                mUserInfo.setStoriesNum(user.getVideonum());

                setUserInfo(mUserInfo);
                setFollower(mUserInfo);
            }
        }));
    }

    private ViewPager.OnPageChangeListener mPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int i, float v, int i1) {
            mSlider.setScrolled(i, v);
        }

        @Override
        public void onPageSelected(int i) {

        }

        @Override
        public void onPageScrollStateChanged(int i) {

        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.user_info_status:
                if (!Utils.isLogin(getActivity(), true))
                    break;
                clickBtnStatus();
                break;

            case R.id.user_info_btn_close:
                dismiss();
                break;

            case R.id.user_info_title:
            case R.id.user_info_attention:
                if (mShowAttention) {
                    hideAttentionList();
                } else {
                    showAttentionList();
                }
                break;

            case R.id.user_info_stories:
            case R.id.user_info_stories_text:
                onClickTabs(0);
                break;

            case R.id.user_info_followers:
            case R.id.user_info_followers_text:
                onClickTabs(1);
                break;

            case R.id.user_info_following:
            case R.id.user_info_following_text:
                onClickTabs(2);
                break;

            case R.id.user_info_message:
                if (!Utils.isLogin(getActivity(), true) || Utils.isMy(mUserInfo.getId()))
                    break;
                Intent intent = new Intent(getActivity(), RealNameChatActivity.class);
                intent.putExtra(RealNameChatActivity.INTENT_USERNAME, mUserInfo.getId());
                startActivity(intent);
                dismiss();
                break;
        }
    }

    private void onClickTabs(int position){
        mAttentionViewPager.setCurrentItem(position);
        if (!mShowAttention) {
            showAttentionList();
        }
    }

    private void showAttentionList(){
        final int windowHeight = mUserInfoWindow.getMeasuredHeight();
        final int titleHeight = UiUtils.dip2px(getActivity(), 50);
        ValueAnimator animator = ValueAnimator.ofFloat(1, 0);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Float value = (Float) animation.getAnimatedValue();
                // 头像往下移动的距离
                float avatarTranslY = -(1 - value) * mUserInfoDetailGroup.getMeasuredHeight();
                // 切换指示条要往下移动的距离
                float tabsTranslY = Math.min(Math.max(avatarTranslY, avatarTranslY + titleHeight), 0);
                // 对话框窗口相比原来需要变大的值, 比例 * （列表高度 + 用户头像和昵称的布局高度 + 状态按钮被隐藏时的高度）
                int windowHeightAdd = (int) ((1 - value) * (mViewPagerHeight - mUserInfoDetailGroup.getMeasuredHeight() + mReduceUserStatusHeight));
                onViewAnimator(value, avatarTranslY, tabsTranslY);
                onWindowHeight(windowHeight + windowHeightAdd);

            }
        });
        animator.addListener(mShowAttentionListener);
        animator.setDuration(WINDOW_SCALE_DURATION);
        animator.start();
    }

    private Animator.AnimatorListener mShowAttentionListener = new Animator.AnimatorListener() {
        @Override
        public void onAnimationStart(Animator animation) {
            mAttentionViewPager.setVisibility(View.VISIBLE);
            mTitle.setVisibility(View.VISIBLE);
            mSlider.setVisibility(View.VISIBLE);
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            mShowAttention = true;
        }

        @Override
        public void onAnimationCancel(Animator animation) {

        }

        @Override
        public void onAnimationRepeat(Animator animation) {

        }
    };

    private void hideAttentionList(){
        final int windowHeight = mUserInfoWindow.getMeasuredHeight();
        final int titleHeight = UiUtils.dip2px(getActivity(), 50);
        ValueAnimator animator = ValueAnimator.ofFloat(0, 1);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Float value = (Float) animation.getAnimatedValue();
                // 头像往下移动的距离
                float avatarTranslY = -(1 - value) * mUserInfoDetailGroup.getMeasuredHeight();
                // 切换指示条要往下移动的距离
                float tabsTranslY = Math.min(Math.max(avatarTranslY, avatarTranslY + titleHeight), 0);
                // 对话框窗口相比原来需要变小的值， 比例 * （列表高度 + 用户头像和昵称的布局高度 + 状态按钮被隐藏时的高度）
                int windowHeightAdd = (int) (value * (mViewPagerHeight - mUserInfoDetailGroup.getMeasuredHeight() + mReduceUserStatusHeight));
                onViewAnimator(value, avatarTranslY, tabsTranslY);
                onWindowHeight(windowHeight - windowHeightAdd);
            }
        });
        animator.setDuration(WINDOW_SCALE_DURATION);
        animator.addListener(mHideAttentionListener);
        animator.start();
    }

    private Animator.AnimatorListener mHideAttentionListener = new Animator.AnimatorListener() {
        @Override
        public void onAnimationStart(Animator animation) {

        }

        @Override
        public void onAnimationEnd(Animator animation) {
            mAttentionViewPager.setVisibility(View.INVISIBLE);
            mTitle.setVisibility(View.INVISIBLE);
            mSlider.setVisibility(View.INVISIBLE);
            mShowAttention = false;
        }

        @Override
        public void onAnimationCancel(Animator animation) {

        }

        @Override
        public void onAnimationRepeat(Animator animation) {

        }
    };

    private void onViewAnimator(float value, float avatarTranslY, float tabsTranslY){
        mUserInfoDetailGroup.setAlpha(value);
        mUserInfoDetailGroup.setScaleY(value);
        mUserInfoDetailGroup.setScaleX(value);
        mUserInfoDetailGroup.setTranslationY(avatarTranslY / 2);
        mAttentionViewPager.setAlpha(1 - value);
        mAttentionViewPager.setTranslationY(tabsTranslY + mViewPagerMarginTop);
        mAttentionGroup.setTranslationY(tabsTranslY);
        mBtnStatus.setAlpha(value);
        mSlider.setAlpha(1 - value);
        mTitle.setAlpha(1 - value);
    }

    private void onWindowHeight(int windowHeight){
        ViewGroup.LayoutParams params = mUserInfoWindow.getLayoutParams();
        params.height = windowHeight;
        mUserInfoWindow.setLayoutParams(params);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public FragmentManager getPageFragmentManager(){
        return getChildFragmentManager();
    }

    private static LinkedList<UserInfoDialog> mUserInfoDialogTask = new LinkedList<>();

    @Override
    public void show(FragmentActivity activity) {
        super.show(activity);
        if (mUserInfoDialogTask.size() > 0){
            UserInfoDialog dialog = mUserInfoDialogTask.removeLast();
            dialog.dismiss();
        }
        mUserInfoDialogTask.addLast(this);
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        mUserInfoDialogTask.remove(this);
    }

    @Override
    public void onFansStatusChange(String userId, int status) {
        Fragment fragment = mAdapter.getFragment(AttentionPageAdapter.FOLLOWER);
        if (fragment != null){
            ((FollowerListFragment)fragment).setUserStatus(userId, status);
        }
    }

    @Override
    public void onFollowerStatusChange(String userId, int status) {
        Fragment fragment = mAdapter.getFragment(AttentionPageAdapter.FANS);
        if (fragment != null){
            ((FansListFragment)fragment).setUserStatus(userId, status);
        }
    }

    private void refreshFansList(){
        Fragment fragment = mAdapter.getFragment(AttentionPageAdapter.FANS);
        if (fragment != null){
            ((FansListFragment)fragment).refreshDataList();
        }
    }

    private OnUserStatusChangeListener mListener;

    public void setOnUserStatusChangeListener(OnUserStatusChangeListener listener){
        mListener = listener;
    }

    public interface OnUserStatusChangeListener{
        void onUserStatusChange(String userId, int status);
    }
}
