/**
 * 
 */
package com.bloomlife.videoapp.activity.fragment;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import net.tsz.afinal.FinalActivity;
import net.tsz.afinal.annotation.view.ViewInject;

import android.app.FragmentTransaction;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaPlayer.OnVideoSizeChangedListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.bloomlife.videoapp.R;
import com.bloomlife.videoapp.common.DefaultAnimationListener;
import com.bloomlife.videoapp.view.VideoView;

/**
 * @author <a href="mailto:lan4627@gmail.com">zhengxingtian</a>
 *
 * @date 2014年12月30日 下午12:17:03
 */
public class WelcomeVideoFragment extends AbstractWelcomeFragment implements OnClickListener{

	@ViewInject(id=R.id.activity_welcome_videoview)
	private VideoView mVideoView;
	
	@ViewInject(id=R.id.activity_welcome_btn_skip, click=ViewInject.DEFAULT)
	private TextView mBtnSkip;
	
	@ViewInject(id=R.id.activity_welcome_text1)
	private View mVideoText1;
	
	@ViewInject(id=R.id.activity_welcome_text2)
	private View mVideoText2;
	
	public static final String TAG = WelcomeVideoFragment.class.getSimpleName();
	public static final int SCENE_1 = 10; //第一个场景
	public static final int SCENE_2 = 11;	//第二个场景
	
	private Handler mHandler = new WelcomeFragmentHandler(this);
	
	private ScheduledExecutorService scheduledExecutorService;
	
	private boolean clickEnable = false  ;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		View layout = inflater.inflate(R.layout.activity_welcome, null);
		FinalActivity.initInjectedView(this, layout);
		initVideoView();
		scheduledExecutorService =Executors.newScheduledThreadPool(1);
		return layout;
	}
	
	private void initVideoView(){
		try {
			mVideoView.setOnVideoSizeChangedListener(mVideoSizeChangedListener);
			mVideoView.setOnCompletionListener(mCompletionListener);
			mVideoView.setOnPreparedListener(mPreparedListener);
			mVideoView.setDataSource(getActivity().getAssets().openFd("first_open_video.mp4"));
			mVideoView.setRotation(0);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void setVideoViewScale(int width, int height){
		Log.d(TAG, "width "+width+" height "+height);
		mVideoView.setScaleX(getResources().getDisplayMetrics().widthPixels / (float)width);
		mVideoView.setScaleY(getResources().getDisplayMetrics().heightPixels / (float)height);
	}
	
	private OnCompletionListener mCompletionListener = new OnCompletionListener() {
		
		@Override
		public void onCompletion(MediaPlayer mp) {
			
		}
	};
	
	private OnVideoSizeChangedListener mVideoSizeChangedListener = new OnVideoSizeChangedListener() {
		
		@Override
		public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
			
		}
	};
	
	private ScheduledFuture<?> scene1Futrue ;
	private ScheduledFuture<?> scene2Futrue ;
	private OnPreparedListener mPreparedListener = new OnPreparedListener() {
		
		@Override
		public void onPrepared(MediaPlayer mp) {
			setVideoViewScale(mVideoView.getWidth(), mVideoView.getHeight());
			mVideoView.startMediaPlayer();
			scene1Futrue=scheduledExecutorService.schedule(new Runnable() {
				
				@Override
				public void run() {
					mHandler.sendEmptyMessage(SCENE_1);
				}
			}, 24, TimeUnit.SECONDS);
			scene2Futrue=scheduledExecutorService.schedule(new Runnable() {
				
				@Override
				public void run() {
					mHandler.sendEmptyMessage(SCENE_2);
				}
			}, 29, TimeUnit.SECONDS);
		}
	};
	
	private AnimationListener mVideoTextAnimListener = new DefaultAnimationListener() {
		
		@Override
		public void onAnimationEnd(Animation animation) {
			mVideoText2.setVisibility(View.VISIBLE);
			mVideoText2.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.view_welcome_in));
		}
	};
	

	@Override
	public void onClick(View v) {
		if(!clickEnable) return ;
		switch (v.getId()) {
		case R.id.activity_welcome_btn_skip:
			showInitFragment();
			break;
		default:
			break;
		}
	}
	public void showInitFragment(){
		if(getFragmentManager()==null){
			Log.e(TAG, " fragment already close , fragment manager is null");
			return ;
		}
		FragmentTransaction  fragmentTransaction = getFragmentManager().beginTransaction();
		fragmentTransaction.setCustomAnimations(R.anim.animation_alpha_in, R.anim.animation_alpha_out);
		fragmentTransaction.replace(R.id.activity_spalsh_layout, new WelcomeInitFragment());
		fragmentTransaction.commit();
	}
	
	@Override
	public void onDestroyView() {
		super.onDestroyView();
	}
	
	static class WelcomeFragmentHandler extends Handler{
		
		private WeakReference<WelcomeVideoFragment> mReference;
		
		public WelcomeFragmentHandler(WelcomeVideoFragment fragment){
			mReference = new WeakReference<>(fragment);
		}
		
		@Override
		public void handleMessage(Message msg) {
			WelcomeVideoFragment fragment = mReference.get();
			if (fragment == null || fragment.mVideoView == null) return;
			switch (msg.what) {
			case SCENE_1:
				
				break ;
			case SCENE_2:
				fragment.showInitFragment();
				break ;
			default:
				break;
			}
			super.handleMessage(msg);
		}
		
	}

	@Override
	public void onPause() {
		mVideoView.releaseMediaPlayer();
		if(scene1Futrue!=null)scene1Futrue.cancel(true);
		if(scene2Futrue!=null)scene2Futrue.cancel(true);
		scheduledExecutorService.shutdownNow();
		super.onPause();
	}

	@Override
	public void onResume() {
		super.onResume();
		new Handler().postDelayed(new Runnable() {
			
			@Override
			public void run() {
				clickEnable = true ;
			}
		}, 1000);
	}

	@Override
	public void onStop() {
		super.onStop();
	}

	@Override
	public void onDetach() {
		super.onDetach();
	}
	
	
}
