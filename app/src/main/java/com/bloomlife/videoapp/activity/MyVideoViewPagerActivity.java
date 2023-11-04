/**
 * 
 */
package com.bloomlife.videoapp.activity;

import java.util.ArrayList;
import java.util.List;

import net.tsz.afinal.FinalActivity;
import net.tsz.afinal.annotation.view.ViewInject;
import android.os.Bundle;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager.widget.ViewPager;
import android.util.Log;

import com.bloomlife.android.common.util.StringUtils;
import com.bloomlife.android.framework.BaseActivity;
import com.bloomlife.videoapp.R;
import com.bloomlife.videoapp.activity.fragment.MyVideoPlayFragment;
import com.bloomlife.videoapp.activity.fragment.AnonVideoPlayFragment;
import com.bloomlife.videoapp.adapter.MyVideoPagerAdapter;
import com.bloomlife.videoapp.manager.VideoFileManager;
import com.bloomlife.videoapp.model.Video;
import com.bloomlife.videoapp.view.comment.CommentViewPager;

/**
 * @author <a href="mailto:xiai.fei@gmail.com">xiai_fei</a>  
 *
 * @date 2014-12-11  下午9:43:16
 */
public class MyVideoViewPagerActivity extends BaseActivity {

	public static final String TAG = "MyVideoViewPager";
	
	public static final String INTENT_VIDEO_LIST = "video_list";
	public static final String INTENT_VIDEO_POSITION = "video_position";
	
	@ViewInject(id=R.id.test_activity_video_viewpager)
	private CommentViewPager mViewPager;
	
	private MyVideoPagerAdapter mPagerAdapter;
	
	private MyVideoPlayFragment mFragment;
	
	private List<Video> mVideoList;
	private int videoPosition;
	
	private int oldCurrentIndex;
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.test_activity_video);
		FinalActivity.initInjectedView(this);
		
		mVideoList = getIntent().getParcelableArrayListExtra(INTENT_VIDEO_LIST);
		videoPosition = getIntent().getIntExtra(INTENT_VIDEO_POSITION, 0);
		
		Video selectVideo = mVideoList.get(videoPosition);
		List<Video> tempList = new ArrayList<Video>();
		for (Video v : mVideoList) {
			if(StringUtils.isEmpty(v.getVideoid())){
				Log.d(TAG, " 视频没有上传成功，不用播放");
				continue;
			}
			tempList.add(v);
		}
		Log.d(TAG, " 原始的videolist size is "+mVideoList.size()+"  videoPosition="+videoPosition);
		mVideoList.clear();
		mVideoList=tempList;
		videoPosition = mVideoList.indexOf(selectVideo);
		Log.d(TAG, " 最新的videolist size is "+mVideoList.size()+"  videoPosition"+videoPosition);
		
		currentIndex = videoPosition;
		mPagerAdapter = new MyVideoPagerAdapter(getSupportFragmentManager(), mVideoList);
		mViewPager.setAdapter(mPagerAdapter);
		mViewPager.setCurrentItem(videoPosition);
		mViewPager.setOnPageChangeListener(pageChangeListener);
	}
	private static int currentIndex ;
	private ViewPager.OnPageChangeListener pageChangeListener = new ViewPager.OnPageChangeListener() {
		
		@Override
		public void onPageSelected(int arg0) {
			if(arg0!=currentIndex){
				AnonVideoPlayFragment lastFragment = mPagerAdapter.getFragment(currentIndex);
				if(lastFragment!=null){
					lastFragment.releaseVideoPlayer();
				}
			}
			mFragment = (MyVideoPlayFragment) mPagerAdapter.getFragment(arg0);
			mFragment.resume();
			currentIndex = arg0;
		}
		
		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
			// TODO Auto-generated method stub
		}
		
		@Override
		public void onPageScrollStateChanged(int arg0) {
			switch (arg0) {
			case ViewPager.SCROLL_STATE_DRAGGING:
				oldCurrentIndex = currentIndex;
				if (mFragment == null){
					mFragment = (MyVideoPlayFragment) mPagerAdapter.getFragment(videoPosition);
				} else {
					mFragment.pauseVideoPlayer();
				}
				break;
			
			case ViewPager.SCROLL_STATE_IDLE:
				// 如果滑动停止后还是在同一个页面，继续播放视频。
				if (oldCurrentIndex == currentIndex && mFragment != null){
					mFragment.resumeVideoPlayer();
				}
				break;

			default:
				break;
			}
			
		}
	};

	@Override
	public void finish() {
		super.finish();
		overridePendingTransition(0, R.anim.activity_camera_out);
		VideoFileManager.getInstance().cancelAll();
	}

	public static int getCurrentItemIndex(){
		return currentIndex;
	}
	
	public void setViewPagerEnabled(boolean enabled){
		mViewPager.setScroll(enabled);
	}
	
}
