/**
 * 
 */
package com.bloomlife.videoapp.activity.fragment;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.bloomlife.android.framework.MyInjectActivity;
import com.bloomlife.videoapp.R;
import com.bloomlife.videoapp.activity.MyVideoViewPagerActivity;

/**
 * 	从我的页面进来的播放页面
 * 
 * @author <a href="mailto:xiai.fei@gmail.com">xiai_fei</a>  
 *
 * @date 2014-12-11  上午11:11:56
 */
public class MyVideoPlayFragment extends AnonVideoPlayFragment implements OnClickListener{
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View layout = inflater.inflate(R.layout.fragment_video, container, false);
		MyInjectActivity.initInjectedView(this, layout);
		initUiData();
		mTextureView.setSurfaceTextureListener(surfaceTextureListener);
		
		sendGetVideoTask();
		getmBtnMessage().setVisibility(View.GONE);
		isAnimationing = false;
		return layout;
	}
	
	protected MediaPlayer.OnPreparedListener preparedListener = new MediaPlayer.OnPreparedListener() {
		@Override
		public void onPrepared(MediaPlayer mp) {
			Log.d(TAG, " position = "+mPosition+" mediaplay already prepare >>>>  ");
			setTextureSize(mp);
			impressionClick = true;
			mMediaPlayer.seekTo(1);
			showVideoComment();
			impressionClick = true;
		}
	};
	
	protected MediaPlayer.OnCompletionListener completionListener = new MediaPlayer.OnCompletionListener() {
		
		@Override
		public void onCompletion(MediaPlayer mp) {
			Log.d(TAG, " position = "+mPosition+" video onCompletion");
			// 视频播放完成后禁止ViewPager左右滑动，防止因为评论框弹出慢，导致切换到其他页面再弹出。
			((MyVideoViewPagerActivity)getActivity()).setViewPagerEnabled(false);
			showVideoComment();
		}
	};
	

	protected boolean isPageOnCurrent(){
		return MyVideoViewPagerActivity.getCurrentItemIndex()==mPosition;
	}
	
	@Override
	public void onClick(View v) {
		super.onClick(v);
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	
	/**
	 * 
	 */
	@Override
	protected void setMsgTipsVisiable(boolean show) {
		//我的主页不需要提示
//		mMsgTips.setVisibility(View.GONE);
	}
}
