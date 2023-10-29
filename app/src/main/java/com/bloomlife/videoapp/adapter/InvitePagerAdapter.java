package com.bloomlife.videoapp.adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.bloomlife.videoapp.activity.fragment.ContactsFragment;
import com.bloomlife.videoapp.activity.fragment.InviteSocialFragment;
import com.bloomlife.videoapp.activity.fragment.RecommendFragment;

/**
 * Created by zxt lan4627@Gmail.com on 2015/8/5.
 */
public class InvitePagerAdapter extends FragmentPagerAdapter {

    public InvitePagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new RecommendFragment();

            case 1:
                return new InviteSocialFragment();

            default:
            case 2:
                return new ContactsFragment();
        }
    }

    @Override
    public int getCount() {
        return 3;
    }
}
