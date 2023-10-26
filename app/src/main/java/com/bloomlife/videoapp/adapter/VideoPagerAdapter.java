/**
 * 
 */
package com.bloomlife.videoapp.adapter;

import java.util.List;

import androidx.fragment.app.FragmentManager;
import android.os.Bundle;
import android.util.SparseArray;

import com.bloomlife.videoapp.activity.fragment.AnonVideoPlayFragment;
import com.bloomlife.videoapp.model.Video;

/**
 * @author <a href="mailto:lan4627@gmail.com">zhengxingtian</a>
 *
 * @date 2014年11月24日 下午4:44:11
 */
public class VideoPagerAdapter extends FragmentStatePagerAdapter {

	private List<Video> mDataList;
	
	protected SparseArray<AnonVideoPlayFragment> array = new SparseArray<>();
	
	public VideoPagerAdapter(FragmentManager fm, List<Video> dataList) {
		super(fm);
		mDataList = dataList;
	}

	@Override
	public AnonVideoPlayFragment getItem(int arg0) {
		System.err.println(" create video fragment : " + arg0);
		AnonVideoPlayFragment fragment = new AnonVideoPlayFragment();
		Bundle bundle = new Bundle();
		bundle.putParcelable(AnonVideoPlayFragment.INTENT_VIDEO, mDataList.get(arg0));
		bundle.putBoolean(AnonVideoPlayFragment.INTENT_VIDEO_IS_MY, false);
		bundle.putInt(AnonVideoPlayFragment.INTENT_VIDEO_POSITION, arg0);
		fragment.setArguments(bundle);
		array.append(arg0, fragment);
		return fragment;
	}

	@Override
	public int getCount() {
		return mDataList.size();
	}
	
	public AnonVideoPlayFragment getFragment(int key){
		return array.get(key);
	}

}
