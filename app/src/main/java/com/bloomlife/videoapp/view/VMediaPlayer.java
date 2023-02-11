/**
 * 
 */
package com.bloomlife.videoapp.view;

import android.media.MediaPlayer;

/**
 * @author <a href="mailto:lan4627@gmail.com">zhengxingtian</a>
 *
 * @date 2015年1月9日 下午2:39:34
 */
public class VMediaPlayer extends MediaPlayer {
	
	public static final int MEDIA_ERROR_NO_PERPARE = -38;
	
	private boolean mIsPause;
	private boolean mIsRelease;

	@Override
	public boolean isPlaying() {
		return super.isPlaying();
	}

	@Override
	public void start() {
		try {
			super.start();
			mIsPause = false;
		} catch(IllegalStateException e){
			e.printStackTrace();
		}
	}

	@Override
	public void stop() {
		try {
			super.stop();
			mIsPause = false;
		} catch(IllegalStateException e){
			e.printStackTrace();
		}
	}

	@Override
	public void pause() {
		try {
			super.pause();
			mIsPause = true;
		} catch(IllegalStateException e){
			e.printStackTrace();
		}
	}

	@Override
	public void release() {
		mIsRelease = true;
		mIsPause = false;
		super.release();
	}

	@Override
	public void reset() {
		mIsPause = false;
		super.reset();
	}
	
	public boolean isPause(){
		return mIsPause;
	}

	public boolean isRelease(){
		return mIsRelease;
	}

}
