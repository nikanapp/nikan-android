/**
 * 
 */
package com.bloomlife.videoapp.common.util;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;

/**
 * @author <a href="mailto:lan4627@gmail.com">zhengxingtian</a>
 *
 * @date 2015年2月4日 下午6:16:30
 */
public class SoundPlay {
	
	private Context mContext;
	private SoundPool mSoundPool;
	private int mSoundID;
	
	public SoundPlay(Context context){
		mContext = context;
		mSoundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
	}
	
	public void play(int rawID){
		mSoundID = mSoundPool.load(mContext, rawID, 0);
		mSoundPool.setOnLoadCompleteListener(new OnLoadCompleteListener() {
			
			@Override
			public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
				mSoundPool.play(mSoundID, 1.0f, 1.0f, 0, 0, 1.0f);
			}
		});

	}

	public void stop(){
		mSoundPool.stop(mSoundID);
	}
	
	public void release(){
		mSoundPool.release();
	}
}
