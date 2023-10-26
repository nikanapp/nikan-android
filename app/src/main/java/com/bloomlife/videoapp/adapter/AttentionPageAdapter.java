package com.bloomlife.videoapp.adapter;


import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import android.os.Bundle;

import com.bloomlife.videoapp.activity.fragment.FansListFragment;
import com.bloomlife.videoapp.activity.fragment.FollowerListFragment;
import com.bloomlife.videoapp.activity.fragment.UserStorysFragment;

/**
 * Created by zhengxingtian lan4627@Gmail.com on 2015/7/29.
 */
public class AttentionPageAdapter extends MyFragmentStatePagerAdapter {

    public static final int STORY = 0;
    public static final int FOLLOWER = 2;
    public static final int FANS = 1;

    private String mUserId;

    private FansListFragment.StatusChangeListener mFansListener;
    private FollowerListFragment.StatusChangeListener mFollowerListener;

    public AttentionPageAdapter(FragmentManager fm, String userId, FansListFragment.StatusChangeListener fansListener, FollowerListFragment.StatusChangeListener followerListener) {
        super(fm);
        mUserId = userId;
        mFansListener = fansListener;
        mFollowerListener = followerListener;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case STORY:
                Fragment usFragment = new UserStorysFragment();
                Bundle usBundle = new Bundle();
                usBundle.putString(UserStorysFragment.INTENT_USER_ID, mUserId);
                usFragment.setArguments(usBundle);
                return usFragment;

            case FANS:
                FansListFragment fansFragment = new FansListFragment();
                Bundle fansBundle = new Bundle();
                fansBundle.putString(UserStorysFragment.INTENT_USER_ID, mUserId);
                fansFragment.setArguments(fansBundle);
                fansFragment.setStatusChangeListener(mFansListener);
                return fansFragment;

            default:
            case FOLLOWER:
                FollowerListFragment folFragment = new FollowerListFragment();
                Bundle folBundle = new Bundle();
                folBundle.putString(UserStorysFragment.INTENT_USER_ID, mUserId);
                folFragment.setArguments(folBundle);
                folFragment.setStatusChangeListener(mFollowerListener);
                return folFragment;

        }
    }

    @Override
    public int getCount() {
        return 3;
    }
}
