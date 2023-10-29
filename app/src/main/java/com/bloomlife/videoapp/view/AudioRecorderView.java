/**
 * 
 */
package com.bloomlife.videoapp.view;

import java.io.File;
import java.lang.ref.WeakReference;

import net.tsz.afinal.FinalActivity;
import net.tsz.afinal.annotation.view.ViewInject;

import com.bloomlife.videoapp.R;
import com.bloomlife.videoapp.app.AppContext;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;

/**
 * @author <a href="mailto:lan4627@gmail.com">zxt</a>
 * 声音收集器的视图
 * @date 2015年4月29日 下午7:12:01
 */
public class AudioRecorderView extends RelativeLayout implements OnClickListener{

	/**
	 * @param context
	 */
	public AudioRecorderView(Context context) {
		super(context);
		init(context);
	}

	/**
	 * @param context
	 * @param attrs
	 */
	public AudioRecorderView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	/**
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	public AudioRecorderView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}
	
	private static final String TAG = AudioRecorderView.class.getSimpleName();
	
	public static final int GET_AMP_SLEEP = 120;
	private static final int MAX_DURATION = 60000;
	private static final int MSG_STOP = 1;
	private static final int MSG_AMP = 2;
	private static final int MSG_PROGRESS = 3;
	
	private MyHandler mHandler = new MyHandler(this);
	
	@ViewInject(id = R.id.view_audio_progress)
	private AudioRecordProgressBar mProgressBar;
	
	@ViewInject(id = R.id.view_audio_trash, click = ViewInject.DEFAULT)
	private ImageView mTrash;
	
	private AudioRecorder mAudioRecorder;
	private File mSaveFile;
	
	private volatile boolean mStart;
	private volatile long mStartTime;
	private volatile long mCurrentTime;
	
	private void init(Context context){
		View layout = inflate(context, R.layout.view_audio_recorder, this);
		FinalActivity.initInjectedView(this, layout);
		
		mProgressBar.setVisibility(View.GONE);
		mTrash.setVisibility(View.GONE);
	}
	
	private File getSaveFile(){
		return new File(getContext().getExternalFilesDir(Environment.DIRECTORY_ALARMS), System.currentTimeMillis()+".amr");
	}
	
	public boolean isStart(){
		return mStart;
	}
	
	public void start(){
		mProgressBar.reset();
		mProgressBar.show();
		mTrash.setVisibility(View.GONE);
		mSaveFile = getSaveFile();
		mAudioRecorder = AudioRecorder.newAMRAudioRecorder(mSaveFile);
		mAudioRecorder.startRecord();
		mAudioRecorder.getMaxAmplitude();
		mStart = true;
		AppContext.EXECUTOR_SERVICE.execute(mGetAmp);
	}

	public File stop(){
		mStart = false;
		mProgressBar.hide();
		mTrash.setVisibility(View.GONE);
		try{
			mAudioRecorder.stop();
		} catch (Exception e){
			e.printStackTrace();
		}
		mAudioRecorder.releaseRecord();
		return mSaveFile;
	}
	
	public void delete(){
		if (mSaveFile != null){
			mSaveFile.delete();
		}
	}
	
	public void showTrashIcon(){
		mProgressBar.hide();
		mTrash.setVisibility(View.VISIBLE);
	}
	
	public void showProgress(){
		mProgressBar.show();
		mTrash.setVisibility(View.GONE);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.view_audio_trash:
			if (mSaveFile != null)
				mSaveFile.delete();
			if (mRecordListener != null)
				mRecordListener.onTrash();
			mTrash.setVisibility(View.GONE);
			break;

		default:
			break;
		}
	}
	
	public long getDuration(){
		return mCurrentTime - mStartTime;
	}
	
	static class MyHandler extends Handler{
		
		private WeakReference<AudioRecorderView> mReference;

		public MyHandler(AudioRecorderView view){
			mReference = new WeakReference<AudioRecorderView>(view);
		}
		
		@Override
		public void handleMessage(Message msg) {
			AudioRecorderView view = mReference.get();
			if (view != null){
				switch (msg.what) {
				case MSG_STOP:
					view.send();
					break;

				case MSG_PROGRESS:
					view.mProgressBar.setProgress((int) (MAX_DURATION-view.getDuration()) / 1000);
					break;
					
				case MSG_AMP:
					if (view.mAudioRecorder.isStart())
						view.mProgressBar.addAmp(view.mAudioRecorder.getMaxAmplitude());
					break;
					
				default:
					break;
				}
			} 
			super.handleMessage(msg);
		}
		
	}
	
	private void send(){
		mStart = false;
		if (mRecordListener != null)
			mRecordListener.onSend(stop());
	}
	
	private Runnable mGetAmp = new Runnable() {
		
		@Override
		public void run() {
			mStartTime = System.currentTimeMillis();
			while (mStart) {
				mHandler.sendEmptyMessage(MSG_AMP);
				try {
					Thread.sleep(GET_AMP_SLEEP);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				mCurrentTime = System.currentTimeMillis();
				long sub = MAX_DURATION - getDuration();
				if (sub <= 1000){
					mHandler.sendEmptyMessage(MSG_STOP);
					break;
				} else {
					mHandler.sendEmptyMessage(MSG_PROGRESS);
				}
			}
		}
	};
	
	private OnRecordListener mRecordListener;
	
	public void setOnTrashListener(OnRecordListener l){
		mRecordListener = l;
	}
	
	public interface OnRecordListener{
		void onTrash();
		void onSend(File file);
	}
	
}
