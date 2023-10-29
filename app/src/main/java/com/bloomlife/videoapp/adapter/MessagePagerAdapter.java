/**
 * 
 */
package com.bloomlife.videoapp.adapter;

import java.util.List;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

/**
 * @author <a href="mailto:lan4627@gmail.com">zxt</a>
 *
 * @date 2015年2月6日 下午5:17:39
 */
public class MessagePagerAdapter extends FragmentPagerAdapter {
	
	private List<Fragment> mFragments;

	public MessagePagerAdapter(FragmentManager fm, List<Fragment> fragments) {
		super(fm);
		mFragments = fragments;
	}

	@Override
	public Fragment getItem(int arg0) {
		return mFragments.get(arg0);
	}

	@Override
	public int getCount() {
		return mFragments == null ? 0 : mFragments.size();
	}

}
