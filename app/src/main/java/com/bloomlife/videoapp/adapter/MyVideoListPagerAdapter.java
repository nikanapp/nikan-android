package com.bloomlife.videoapp.adapter;

import android.app.Fragment;
import android.app.FragmentManager;
import android.util.SparseArray;
import android.view.ViewGroup;

import com.bloomlife.videoapp.activity.fragment.MyStorysFragment;
import com.bloomlife.videoapp.activity.fragment.MyVideosFragment;

/**
 * Created by zhengxingtian lan4627@Gmail.com on 2015/8/4.
 */
public class MyVideoListPagerAdapter extends MyFragmentPagerAdapter {

    public MyVideoListPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new MyStorysFragment();

            default:
            case 1:
                return new MyVideosFragment();
        }
    }

    @Override
    public int getCount() {
        return 2;
    }
}
