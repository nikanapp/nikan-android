package com.bloomlife.videoapp.adapter;


import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import android.util.SparseArray;
import android.view.ViewGroup;


/**
 * Created by zxt lan4627@Gmail.com on 2015/8/11.
 */
public abstract class MyFragmentStatePagerAdapter extends FragmentStatePagerAdapter {

    private SparseArray<Fragment> mFragments = new SparseArray<>();

    public MyFragmentStatePagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Fragment fragment = (Fragment) super.instantiateItem(container, position);
        mFragments.append(position, fragment);
        return fragment;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        mFragments.remove(position);
        super.destroyItem(container, position, object);
    }

    public Fragment getFragment(int position){
        return mFragments.get(position);
    }
}
