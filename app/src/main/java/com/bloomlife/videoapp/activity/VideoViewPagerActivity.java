/**
 * 
 */
package com.bloomlife.videoapp.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.tsz.afinal.FinalActivity;
import net.tsz.afinal.annotation.view.ViewInject;
import android.os.Bundle;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager.widget.ViewPager.OnPageChangeListener;
import android.util.Log;

import com.bloomlife.videoapp.R;
import com.bloomlife.videoapp.activity.fragment.AnonVideoPlayFragment;
import com.bloomlife.videoapp.adapter.VideoPagerAdapter;
import com.bloomlife.videoapp.common.AnalyticUtil;
import com.bloomlife.videoapp.manager.VideoFileManager;
import com.bloomlife.videoapp.model.Video;
import com.bloomlife.videoapp.view.comment.CommentViewPager;

/**
 * @author <a href="mailto:lan4627@gmail.com">zhengxingtian</a>
 *
 *@parameter INTENT_VIDEO_LIST List<Video> 传一个需要播放的视频列表。
 *@parameter INTENT_VIDEO_POSITION int 由列表中的哪一个位置开始播放。
 * @date 2014年11月24日 下午5:39:54
 */
public class VideoViewPagerActivity extends FragmentActivity {

	public static final String TAG = VideoViewPagerActivity.class.getSimpleName();
	
	public static final String INTENT_VIDEO_LIST = "video_list";
	public static final String INTENT_VIDEO_POSITION = "video_position";
	public static final String INTENT_FROM_LETTER= "intent_from_letter";
	
	@ViewInject(id=R.id.test_activity_video_viewpager)
	private CommentViewPager mViewPager;
	
	private VideoPagerAdapter mPagerAdapter;
	
	private AnonVideoPlayFragment mFragment;
	
	private List<Video> mVideoList;
	private int videoPosition;
	
	private boolean fromConversation;
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.test_activity_video);
		FinalActivity.initInjectedView(this);
		if (mVideoList == null){
			mVideoList = getIntent().getParcelableArrayListExtra(INTENT_VIDEO_LIST);
			fromConversation = getIntent().getBooleanExtra(INTENT_FROM_LETTER, false);
			videoPosition = getIntent().getIntExtra(INTENT_VIDEO_POSITION, 0);
		}
		currentIndex = videoPosition;
		Log.d(TAG, " currentInext = "+currentIndex);
		mPagerAdapter = new VideoPagerAdapter(getSupportFragmentManager(), mVideoList);
		mViewPager.setAdapter(mPagerAdapter);
		mViewPager.setCurrentItem(videoPosition);
		mViewPager.setOnPageChangeListener(pageChangeListener);
		mViewPager.setOffscreenPageLimit(2);
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putParcelableArrayList(INTENT_VIDEO_LIST, new ArrayList<Video>(mVideoList));
		outState.putBoolean(INTENT_FROM_LETTER, fromConversation);
		outState.putInt(INTENT_VIDEO_POSITION, videoPosition);
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		fromConversation = savedInstanceState.getBoolean(INTENT_FROM_LETTER, false);
		mVideoList = savedInstanceState.getParcelableArrayList(INTENT_VIDEO_LIST);
		videoPosition = savedInstanceState.getInt(INTENT_VIDEO_POSITION, 0);
		super.onRestoreInstanceState(savedInstanceState);
	}

	private static int currentIndex ;
	private int oldCurrentIndex;
	
	private OnPageChangeListener pageChangeListener = new OnPageChangeListener() {
		
		@Override
		public void onPageSelected(int arg0) {
			if(arg0!=currentIndex){
				AnonVideoPlayFragment lastFragment = mPagerAdapter.getFragment(currentIndex);
				Video video = lastFragment.getVideo();
				if(video!=null){
					//发送选择了视频观看统计 
					Map<String, String> map = new HashMap<String, String>();
					map.put("videoid", video.getVideoid());
					AnalyticUtil.sendAnalytisEvent(getApplicationContext(), AnalyticUtil.Event_Choise_Video, map);
				}
				
				if(lastFragment!=null){
					lastFragment.releaseVideoPlayer();
				}
			}
			if (mFragment != null){
				mFragment = mPagerAdapter.getFragment(arg0);
				mFragment.resume();
			}
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
					mFragment = mPagerAdapter.getFragment(videoPosition);
				} else {
					mFragment.pauseVideoPlayer();
				}
				break;
				
			case ViewPager.SCROLL_STATE_SETTLING:
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
	
	private void setFragmentScroll(boolean scroll){
		if (mFragment != null){
			mFragment.isViewPagerScroll(scroll);
		}
	}

	@Override
	public void finish() {
		super.finish();
		overridePendingTransition(0, R.anim.activity_camera_out);
		VideoFileManager.getInstance().cancelAll();
	}

	public static int getCurrentItemIndex(){
		return currentIndex;
	}

	public boolean isFromConversation() {
		return fromConversation;
	}

	public void setFromConversation(boolean fromConversation) {
		this.fromConversation = fromConversation;
	}
	
	public void setViewPagerEnabled(boolean enabled){
		mViewPager.setScroll(enabled);
	}
	
}
