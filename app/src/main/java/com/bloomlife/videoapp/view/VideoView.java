/**
 * 
 */
package com.bloomlife.videoapp.view;

import java.io.IOException;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaPlayer.OnTimedTextListener;
import android.media.MediaPlayer.OnVideoSizeChangedListener;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Surface;
import android.view.TextureView;
import android.view.TextureView.SurfaceTextureListener;

/**
 * @author <a href="mailto:lan4627@gmail.com">zxt</a>
 * 首次进入时的动画播放视图
 * @date 2014年12月30日 下午12:27:35
 */
public class VideoView extends TextureView implements SurfaceTextureListener {

	public VideoView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public VideoView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public VideoView(Context context) {
		super(context);
		init(context);
	}
	
	private MediaPlayer mMediaPlayer;
	private AssetFileDescriptor mFileDescriptor;
	private String mVideoPath;
	
	private void init(Context context){
		setSurfaceTextureListener(this);
		createMediaPlayer();
	}
	
	private void createMediaPlayer(){
		mMediaPlayer = new MediaPlayer();
		mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
	}
	
	private void initMediaPlayer(SurfaceTexture surface){
		if (mMediaPlayer == null) createMediaPlayer();
		mMediaPlayer.setSurface(new Surface(surface));
		setMediaPlayerDataSource();
	}
	
	private void setMediaPlayerDataSource(){
		try {
			if (!TextUtils.isEmpty(mVideoPath))
				mMediaPlayer.setDataSource(mVideoPath);
			else if (mFileDescriptor != null)
				mMediaPlayer.setDataSource(mFileDescriptor.getFileDescriptor(), mFileDescriptor.getStartOffset(), mFileDescriptor.getLength());
			mMediaPlayer.prepare();
		} catch (IOException e) {
			e.printStackTrace();  
		}
	}
	
	public void setDataSource(AssetFileDescriptor fd){
		mFileDescriptor = fd;
	}
	
	public void setDataSource(String path){
		mVideoPath = path;
	}
	
	public void startMediaPlayer(){
		mMediaPlayer.start();
	}
	
	public void pauseMediaPlayer(){
		mMediaPlayer.pause();
	}
	
	public void stopMediaPlayer(){
		mMediaPlayer.stop();
	}
	
	public void reverseMediaPlayer(){
		mMediaPlayer.reset();
		setMediaPlayerDataSource();
		mMediaPlayer.start();
	}
	
	public void releaseMediaPlayer(){
		if(mMediaPlayer==null) return ;
		mMediaPlayer.stop();
		mMediaPlayer.release();
		mMediaPlayer =null ;
	}
	
	public void setOnCompletionListener(OnCompletionListener listener){
		mMediaPlayer.setOnCompletionListener(listener);
	}
	
	public void setOnPreparedListener(OnPreparedListener listener){
		mMediaPlayer.setOnPreparedListener(listener);
	}
	
	public void setOnVideoSizeChangedListener(OnVideoSizeChangedListener listener){
		mMediaPlayer.setOnVideoSizeChangedListener(listener);
	}
	
	public void setOnTimedTextListener(OnTimedTextListener listener){
		mMediaPlayer.setOnTimedTextListener(listener);
	}
	
	public int getCurrentPosition(){
		if (mMediaPlayer != null){
			return mMediaPlayer.getCurrentPosition();
		} else {
			return 0;
		}
	}
	
	private void releseMediaPlayer(){
		if (mMediaPlayer == null) return;
		if (mMediaPlayer.isPlaying()) mMediaPlayer.stop();
		mMediaPlayer.reset();
		mMediaPlayer.release();
		mMediaPlayer =null;
	}

	@Override
	public void onSurfaceTextureAvailable(SurfaceTexture surface, int width,
			int height) {
		initMediaPlayer(surface);
	}

	@Override
	public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width,
			int height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
		releseMediaPlayer();
		return false;
	}

	@Override
	public void onSurfaceTextureUpdated(SurfaceTexture surface) {
		// TODO Auto-generated method stub
		
	}


}
