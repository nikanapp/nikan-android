/**
 * 
 */
package com.bloomlife.videoapp.view;

import java.io.File;
import java.io.IOException;

import android.media.MediaRecorder;

/**
 * @author <a href="mailto:lan4627@gmail.com">zhengxingtian</a>
 *
 * @date 2015年4月29日 上午11:19:59
 */
public class AudioRecorder extends MediaRecorder {
	
	private boolean mStart;

	public AudioRecorder() {
		
	}
	
	public AudioRecorder(int fromat, int encode, File outPutFile) {
		setAudioSource(AudioSource.MIC);
		setOutputFormat(fromat);
		setAudioEncoder(encode);
		setOutputFile(outPutFile.getAbsolutePath());
	}
	
	public static AudioRecorder newAMRAudioRecorder(File outPutFile){
		return new AudioRecorder(OutputFormat.AMR_NB, AudioEncoder.AMR_NB, outPutFile);
	}
	
	public void startRecord(){
		try {
			prepare();
			start();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			releaseRecord();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			releaseRecord();
		}
	}
	
	
	
	public void releaseRecord(){
		reset();
		release();
	}
	
	public boolean isStart(){
		return mStart;
	}

	@Override
	public void start() throws IllegalStateException {
		super.start();
		mStart = true;
	}

	@Override
	public void stop() throws IllegalStateException {
		super.stop();
		mStart = false;
	}

}
