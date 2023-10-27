package com.bloomlife.videoapp.activity;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.media.AudioManager;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.MessageRequest;
import com.android.volley.toolbox.Volley;
import com.bloomlife.android.bean.CacheBean;
import com.bloomlife.android.bean.ProcessResult;
import com.bloomlife.android.common.CameraTouchFocusListener;
import com.bloomlife.android.common.util.StringUtils;
import com.bloomlife.android.common.util.UiHelper;
import com.bloomlife.android.framework.BaseActivity;
import com.bloomlife.android.framework.BaseHandler;
import com.bloomlife.videoapp.R;
import com.bloomlife.videoapp.app.AppContext;
import com.bloomlife.videoapp.common.CacheKeyConstants;
import com.bloomlife.videoapp.common.util.CameraUtil;
import com.bloomlife.videoapp.common.util.UIHelper;
import com.bloomlife.videoapp.manager.LocationManager;
import com.bloomlife.videoapp.model.Video;
import com.bloomlife.videoapp.model.VideoTmp;
import com.bloomlife.videoapp.model.message.RecordSizeMessage;
import com.bloomlife.videoapp.model.message.RecorderErrorMessage;
import com.bloomlife.videoapp.view.CircularProgressBar;
import com.bloomlife.videoapp.view.SuperToast;

import net.tsz.afinal.annotation.view.ViewInject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
/**
 * 视频拍摄界面
 * @author <a href="mailto:lan4627@gmail.com">zhengxingtian</a>
 *
 * @parameter INTENT_RECORD_TYPE int 视频录制模式，RECORD_TYPE_STORY是精选集视频，RECORD_TYPE_ANONY是匿名视频
 *
 * @date 2014年11月21日 上午10:29:37
 */
public class CameraActivity extends BaseActivity implements OnClickListener, Callback {
	
	public static final String TAG = CameraActivity.class.getSimpleName();

	public static final int RECORD_TYPE_STORY = 1;
	public static final int RECORD_TYPE_GLIMPSE = 2;
	public static final String INTENT_RECORD_TYPE = "recordType";
	
	public static final String BACK_CAMERA = "backCamera";
	public static final String FRONT_CAMERA = "frontCamera";
	
	private static final int MESSAGE_MEDIA 		   = 10;
	private static final int MESSAGE_MEDIA_COMPLETE = 11;
	private static final int MEDIA_RECORDER_START   = 12;
	private static final int MEDIA_RECORDER_STOP	   = 13;

	private static final int MAX_DURATION = 10 * 1000;
	private static final int MIN_DURATION = 3 * 1000;
	private static final int MAX_FREQUENCY = 400;
	private static final int MAX_PROGRESS = 361;

	private static final float SECOND = 1000f;

	private static final int REQUEST_CODE = 1000;
	
	@ViewInject(id=R.id.camera_flash, click=ViewInject.DEFAULT)
	private ImageView mFlash;
	
	@ViewInject(id=R.id.camera_switch, click=ViewInject.DEFAULT)
	private ImageView mSwitch;
	
	@ViewInject(id=R.id.camera_cancel, click=ViewInject.DEFAULT)
	private ImageView mCancel;
	
	@ViewInject(id=R.id.camera_comfirm, click=ViewInject.DEFAULT)
	private ImageView mComfirm;
	
	@ViewInject(id=R.id.camera_time)
	private TextView mTimeText;

	@ViewInject(id=R.id.camera_textureview)
	private SurfaceView mSurfaceView;
	
	@ViewInject(id=R.id.camera_layout)
	private ViewGroup mCameraLayout;
	
	@ViewInject(id=R.id.camera_main_layout)
	private ViewGroup mCameraMainLayout;
	
	@ViewInject(id=R.id.camera_progress_layout, click=ViewInject.DEFAULT)
	private ViewGroup mProgressLayout;
	
	@ViewInject(id=R.id.camera_progressbar, click=ViewInject.DEFAULT)
	private CircularProgressBar mCameraProgressBar;
	
	private volatile Camera mCamera;
	private volatile MediaRecorder mMediaRecorder;
	private volatile boolean mIsStartMediaRecord;
	private View mFirstCamera;

	private int orientation;
	private int cameraType;
	/** 拍摄视频的每秒帧数 **/
	private int mVideoFrame;
	/** 拍摄视频完要进入的编辑模式 **/
	private int mEditType;

	enum FLASH_MODE {AUTO, ON, OFF}

	private int mFlashClick;

	private boolean mIsFront;
	private boolean mIsStart;
	private boolean mIsSurfaceCreated;
	/** 是否播放界面退回来的 */
	private boolean mIsResultVideo;
	
	private AudioManager mAudioManager;
	private MediaHandler mHandler = new MediaHandler(this);
	private ArrayList<VideoTmp> mVideoTmps = new ArrayList<VideoTmp>();
	private SurfaceHolder mSurfaceHolder;
	
	private boolean mSupportAutoFocus;
	private boolean mSupportContinuousVideo;
	private boolean mHaveFrontCamera;
	private boolean mInit;
	private volatile boolean mIsMediaRun;
	private volatile int mTime;
	private volatile float mProgress;
	private boolean mAddPart;
	
	private int mVideoWidth;
	private int mVideoHeight;
	
	private CacheBean cacheBean = CacheBean.getInstance();
	private CameraTouchFocusListener mFoucsListener;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_camera);
		mAudioManager = ((AudioManager)getSystemService(Context.AUDIO_SERVICE));
		mEditType = getIntent().getIntExtra(INTENT_RECORD_TYPE, RECORD_TYPE_STORY);
		mInit = true;
		initCamera();
		initLayout();
		LocationManager.getInstance(this).startLocation();
		
		String firstIn = cacheBean.getString(this, "obtain_sound");
		if(StringUtils.isEmpty(firstIn)){
			tryGetSoundPrivilege();
			cacheBean.putString(this, "obtain_sound", "1");
		}
	}
	
	/**
	 * 为了使一些需要弹出授权的手机提前弹出授权。
	 */
	private void tryGetSoundPrivilege(){
		final MediaRecorder mediaRecorder = new MediaRecorder();
		// 第1步：设置音频来源（MIC表示麦克风）
		mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		//第2步：设置音频输出格式（默认的输出格式）
		mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
		//第3步：设置音频编码方式（默认的编码方式）
		mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
		//创建一个临时的音频输出文件
		//第4步：指定音频输出文件
		String soundFile = getExternalFilesDir(Environment.DIRECTORY_MOVIES) + "/sound"+System.currentTimeMillis()+".amr";
		mediaRecorder.setOutputFile(soundFile);
		//第5步：调用prepare方法
		try {
			mediaRecorder.prepare();
			//第6步：调用start方法开始录音
			mediaRecorder.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		new Handler().postDelayed(new Runnable() {
			
			@Override
			public void run() {
				mediaRecorder.stop();
				mediaRecorder.release();
			}
		}, 500);
	}
	
	private void initCamera(){
		Camera.CameraInfo info = new Camera.CameraInfo();
		for (int i=0; i<Camera.getNumberOfCameras(); i++){
			Camera.getCameraInfo(i, info);
			if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT){
				// 如果有前置摄像头，把按钮和切换手势打开。
				mSwitch.setVisibility(View.VISIBLE);
				mHaveFrontCamera = true;
			}
		}
	}
	
	private String createVideoUrl(){
		return getExternalFilesDir(Environment.DIRECTORY_MOVIES) + "/video"+System.currentTimeMillis()+".mp4";
	}
	
	/** 当前的预览框尺寸 */
	int curPreviewWidth = 0;
	int curPreviewHeight = 0;
	
	@SuppressLint("NewApi")
	private void createCamera(int type){
		cameraType = type;
		mCamera = openCamera(type);
		if (mCamera==null){
			UiHelper.shortToast(this, "请尝试关闭其他相机软件再打开");
			return;
		}
		orientation = CameraUtil.setCameraDisplayOrientation(this, 0, mCamera);
		Camera.Parameters mParameters = mCamera.getParameters();
		
		Size size = getOptimalPreviewSize(mParameters.getSupportedPreviewSizes());
		curPreviewWidth = size.width;
		curPreviewHeight = size.height;
		mParameters.setPreviewSize(curPreviewWidth, curPreviewHeight);
		// 因为CamcorderProfile是以横屏为标准的，而当前屏幕录像时竖屏。所以要把Height和Width要反过来传

		// 如果支持变焦，把变焦设置为0
		if (mParameters.isZoomSupported())
			mParameters.setZoom(0);
		// 设置为自动连续对焦
		List<String> mFocusModes = mParameters.getSupportedFocusModes();
		if (mFocusModes.contains(Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)) {
			mParameters.setFocusMode(Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
			mSupportContinuousVideo = true;
		} else {
			mSupportContinuousVideo = false;
		}

		mSupportAutoFocus = mFocusModes.contains(Parameters.FOCUS_MODE_AUTO);
		mVideoFrame = getVideoFrame(mParameters.getSupportedPreviewFpsRange());
		// 设置为多媒体录制，加快打开的速度 
		mParameters.setRecordingHint(true);
		// 是否支持视频防抖，在小米4上会造成画面抖动
		if (mParameters.isVideoStabilizationSupported()){
//			mParameters.setVideoStabilization(true);
		}
		mCamera.setParameters(mParameters);
		try {
			mCamera.setPreviewDisplay(mSurfaceHolder);
		} catch (IOException e) {
			e.printStackTrace();
			mCamera.release();
		}
		mCamera.startPreview();
		// 因为CamcorderProfile是以横屏为标准的，而当前屏幕录像时竖屏。所以要把Height和Width要反过来传
		setSurfaceViewLayoutParams(curPreviewHeight,curPreviewWidth);
		if (mFoucsListener == null)
			mFoucsListener = new CameraTouchFocusListener(this, mCamera, mFocusCallback);
		mCameraMainLayout.setOnTouchListener(mFoucsListener);
		initFlash();
//		UiHelper.shortToast(this, " preview heith = " + curPreviewHeight+" width "+curPreviewWidth);
	}
	
	private Size getOptimalPreviewSize(List<Size> sizes) {
        if (sizes == null) return null;
        int screenHeith = AppContext.deviceInfo.getScreenHeight();
        int screenWidth = AppContext.deviceInfo.getScreenWidth();
    	Size targetSize = null ;
    	
    	List<Size> filterSizes = new ArrayList<>();
    	for (Size size : sizes) {
			if(size.width<=1280 && size.width>=480){
				filterSizes.add(size);
			}
		}
    	
    	double ratioDiff = 100.0;
    	final double targetRatio = (double)screenHeith/screenWidth;
    	//选择和当前屏幕比例最接近的拍摄分辨率
    	for (Size size : filterSizes) {
    		 //和平面分辨率一样
    		if(size.width==screenHeith&&size.height==screenWidth) {
				targetSize = size ;
				break ;
    		}
    		double tempRatio = Math.abs((double)size.width/size.height-targetRatio);
			if(tempRatio <= ratioDiff){
				ratioDiff = tempRatio ; //更靠近1280
				targetSize = size ;
			}
		}
    	if(targetSize!=null){
        	Log.d(TAG, " 获取到手机的拍摄分辨率为  " + targetSize.height + " * " + targetSize.width);
        	sendRecordVideoToServer(sizes, targetSize);
        }
       return targetSize;
    }
	
	/**
	 * 告诉后台这个设置支持哪些拍摄分辨率
	 * @param sizes
	 * @param size
	 */
	private void sendRecordVideoToServer(List<Size>sizes  , Size size){
		if(StringUtils.isEmpty(cacheBean.getString(this, CacheKeyConstants.KEY_VIDEO_SIZE_INFROM))){
			Volley.addToTagQueue(new MessageRequest(new RecordSizeMessage(sizes, size), new MessageRequest.Listener<ProcessResult>() {
				@Override
				public void success(ProcessResult result) {
					cacheBean.putString(getApplicationContext(), CacheKeyConstants.KEY_VIDEO_SIZE_INFROM, "1");
				}
			}));
		}
	}
	
	private void setSurfaceViewLayoutParams(float width, float height){
		final int screenHeight = AppContext.deviceInfo.getScreenHeight();
		final int screenWidth = AppContext.deviceInfo.getScreenWidth();
		FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) mSurfaceView.getLayoutParams();
		if(screenHeight-height>screenWidth-width){
			//高度适配
			params.width = (int) (width*(screenHeight/height));
			params.height = screenHeight;
		}else{
			//宽度适配
			params.width = screenWidth;
			params.height = (int) (height * (screenWidth/ width));
		}
		
		params.gravity = Gravity.CENTER;
		mSurfaceView.setLayoutParams(params);
		mSurfaceView.requestLayout();
	}
	
	/**
	 * 视频拍摄帧数
	 * @param fpsRanges
	 * @return
	 */
	private int getVideoFrame(List<int[]> fpsRanges){
		int defFps = 15;
		int nimFps = defFps;
		Ranges : for (int[] fpsRange:fpsRanges){
			for (int bfps:fpsRange){
				int fps = bfps / 1000;
				if (fps == defFps){
					break Ranges;
				} else {
					nimFps = fps > nimFps ? fps : nimFps;
				}
			}
		}
		return nimFps == defFps ? defFps : nimFps;
	}
	
	
	private Camera openCamera(int type){
		Camera camera = null;
		try{
			camera = Camera.open(type);
		} catch (Exception e){
			e.printStackTrace();
		}
		return camera;
	}
	
	class SizeComparator implements Comparator<Size>{
		/**
		 * 根据面积来判断Size的大小关系
		 */
		@Override
		public int compare(Size lhs, Size rhs) {
			if (lhs.height * lhs.width > rhs.height * rhs.width){
				return -1;
			} else if (lhs.height * lhs.width == rhs.height * rhs.width){
				return 0;
			} else {
				return 1;
			}
		}
		
	}
	
	private void createMediaRecorder() {
		AppContext.EXECUTOR_SERVICE.execute(startMediaRecorderRunnable);
	}
	
	private Runnable startMediaRecorderRunnable = new Runnable() {
		
		@Override
		public void run() {
			if (mCamera == null) 
				return;
			mIsStartMediaRecord = true;
			if (mMediaRecorder == null){
				mMediaRecorder = new MediaRecorder();
			} else {
				mMediaRecorder.reset();
			}
			mCamera.unlock();
			mMediaRecorder.setCamera(mCamera);
			// 获得预览界面
			mMediaRecorder.setOnErrorListener(errorListener);
			mMediaRecorder.setPreviewDisplay(mSurfaceHolder.getSurface());
			mMediaRecorder.setOrientationHint(getCameraOrientation());
			try {
				mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
				mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
				mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
				mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
				mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
				// 视频大小
				try {
					mMediaRecorder.setVideoSize(curPreviewWidth, curPreviewHeight);
					mVideoWidth  = curPreviewHeight;
					mVideoHeight = curPreviewWidth;
				}catch(Exception e){
					CamcorderProfile profile = getProFile();
					mMediaRecorder.setVideoSize(profile.videoFrameWidth, profile.videoFrameHeight);
					mVideoWidth  = profile.videoFrameHeight;
					mVideoHeight = profile.videoFrameWidth;
				}
				mMediaRecorder.setVideoFrameRate(mVideoFrame);
				// 视频码率
				setVideoEncodingBitRate(mMediaRecorder);
				// 视频文件储存路径
				VideoTmp videoTmp = new VideoTmp(cameraType, createVideoUrl());
				mVideoTmps.add(videoTmp);
				mMediaRecorder.setOutputFile(videoTmp.getVideoTmpPath());
			
				mMediaRecorder.prepare();
				mHandler.sendEmptyMessage(MEDIA_RECORDER_START);
			} catch (Exception e) {
				Log.e(TAG, "启动拍摄失败");
				Volley.addToTagQueue(new MessageRequest(new RecorderErrorMessage(curPreviewHeight, curPreviewWidth, e.getMessage())));
				releaseMediaRecorder();
			} finally{
				mIsStartMediaRecord = false;
			}
		}
	};
	
	private CamcorderProfile getProFile(){
		if (CamcorderProfile.hasProfile(CamcorderProfile.QUALITY_720P)
				&& curPreviewHeight>= 720 &&curPreviewWidth >= 1280){
			return CamcorderProfile.get(CamcorderProfile.QUALITY_720P);
		} else if (CamcorderProfile.hasProfile(CamcorderProfile.QUALITY_480P)
				&& curPreviewHeight >= 480 && curPreviewWidth >= 720){
			return CamcorderProfile.get(CamcorderProfile.QUALITY_480P);
		} else {
			return CamcorderProfile.get(CamcorderProfile.QUALITY_LOW);
		}
	}
	
	/**
	 * 获取保存视频时候要旋转的画面角度
	 * @return
	 */
	private int getCameraOrientation(){
		// 如果是背后的摄像头，返回和相机一样的旋转角度。如果是前置摄像头则要再翻转180度
		if (cameraType == Camera.CameraInfo.CAMERA_FACING_BACK){
			return orientation;
		} else {
			return orientation + 180;
		}
	}
	
	private void setVideoEncodingBitRate(MediaRecorder mediaRecorder){
		int defaultBitrate = AppContext.getSysCode().getBitrate();
		if(defaultBitrate<1000)defaultBitrate = 2000*1000;
//		String info = SystemUtils.getSystemInfo();
//		if (info.contains("MX")){
//			mediaRecorder.setVideoEncodingBitRate(2000*1024);
//		} else if(info.contains("MI")){
//			mediaRecorder.setVideoEncodingBitRate(defaultBitrate);
//		} else {
			mediaRecorder.setVideoEncodingBitRate(defaultBitrate);
//		}
	}
	
	private MediaRecorder.OnErrorListener errorListener = new MediaRecorder.OnErrorListener() {
		
		@Override
		public void onError(MediaRecorder mr, int what, int extra) {
			Log.e(TAG, "MediaRecorder ERROR");
			if (mr != null){
				mr.reset();
			}
		}
	};
	
	private Integer oldStreamVolume;
	
	/**
	 * 打开声音
	 */
	private void enableSound(){
		mHandler.postDelayed(new Runnable() {
			
			@Override
			public void run() {
				setAudioManagerStream(false);
		        if (mAudioManager != null && oldStreamVolume != null) {
		        	mAudioManager.setStreamVolume(AudioManager.STREAM_DTMF, oldStreamVolume, 0); 
		        }
			}
		}, 700);
	}
	
	/**
	 * 关闭声音
	 */
	private void disableSound(){
        oldStreamVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_DTMF);
        mAudioManager.setStreamVolume(AudioManager.STREAM_DTMF, 0, 0);
	}
	
	private void setAudioManagerStream(boolean status){
		mAudioManager.setStreamMute(AudioManager.STREAM_DTMF, status);
		mAudioManager.setStreamMute(AudioManager.STREAM_RING, status);
		mAudioManager.setStreamMute(AudioManager.STREAM_ALARM, status);
		mAudioManager.setStreamMute(AudioManager.STREAM_MUSIC, status);
		mAudioManager.setStreamMute(AudioManager.STREAM_SYSTEM, status);
		mAudioManager.setStreamMute(AudioManager.STREAM_VOICE_CALL, status);
		mAudioManager.setStreamMute(AudioManager.STREAM_NOTIFICATION, status);
	}
	
	private void initLayout(){
		if (CacheBean.getInstance().getInt(this, CacheKeyConstants.KEY_FIRST_CAMERA, -1) < 0) {
			CacheBean.getInstance().putInt(this, CacheKeyConstants.KEY_FIRST_CAMERA, 1);
			mFirstCamera = findViewById(R.id.first_camera_tip);
			mFirstCamera.setVisibility(View.VISIBLE);
		}
		mCameraProgressBar.setLongClickListener(longClickListener);
		mSurfaceHolder = mSurfaceView.getHolder();
		mSurfaceHolder.addCallback(this);
		mComfirm.setEnabled(false);
		mTimeText.setTypeface(UIHelper.getHelveticaTh(this));
	}
	
	private void initFlash(){
		mFlash.setBackgroundResource(R.drawable.btn_normflash_selector);
		mFlash.setEnabled(false);
		Parameters parameters = mCamera.getParameters();
		List<String> list = parameters.getSupportedFlashModes();
		if (list == null || list.isEmpty())
			return;
		for (String str : list) {
			if (str.equals(Parameters.FLASH_MODE_ON)) {
				parameters.setFlashMode(Parameters.FLASH_MODE_AUTO);
				mFlash.setVisibility(View.VISIBLE);
				mFlash.setEnabled(true);
				mFlash.setBackgroundResource(R.drawable.btn_flash_auto_selector);
			}
		}
		mCamera.setParameters(parameters);
	}
	
	@Override
	public void finish() {
		if (!mIsResultVideo){
			deleteAllVideo();
		}
		super.finish();
		overridePendingTransition(0, R.anim.activity_camera_out);
	}
	
	public void showTime(){
		mTimeText.setText(String.format("%.1f", mTime/SECOND));
	}
	
	static class MediaHandler extends BaseHandler<CameraActivity>{
		
		public MediaHandler(CameraActivity activity) {
			super(activity);
		}

		@Override
		protected void handleMessage(Message msg, CameraActivity activity) {
			switch (msg.what) {
			case MESSAGE_MEDIA:
				if (activity.mIsMediaRun){
					activity.mCameraProgressBar.setProgress((Float)msg.obj);
				} else if(activity.mAddPart){
					activity.mAddPart = false;
					activity.mCameraProgressBar.addPart();
				}
				float time = (activity.mTime/SECOND);
				if(time>10.0f)time=10.0f;  //方式由于误差引起的时间超过10s
				activity.showTime();
				if (activity.mTime > MIN_DURATION && !activity.mComfirm.isEnabled()){
					activity.mComfirm.setEnabled(true);
					activity.mComfirm.setBackgroundResource(R.drawable.btn_camera_comfirm_time_selector);
				} 
				if (activity.mTime > MAX_DURATION - 1200){
					// 离视频拍摄最大时间还有1.2秒时关闭进度条按钮，防止暂停后又开始拍摄的视频片段小于1.2秒，小于1.2会导致出错。
					activity.mCameraProgressBar.setUntilTheEnd(true);
				}
				break;
				
			case MEDIA_RECORDER_STOP:
				
				break;
			
			case MESSAGE_MEDIA_COMPLETE:
				activity.mCameraProgressBar.setProgress(360f);
				activity.mCameraProgressBar.setCompleted(true);
				activity.mediaRecorderComplete();
				break;
				
			case MEDIA_RECORDER_START:
				if (activity.mMediaRecorder != null){
					try {
						activity.mMediaRecorder.start();
						activity.enabledUserView();
					} catch (Exception e) {
						e.printStackTrace();
						Volley.addToTagQueue(
								new MessageRequest(new RecorderErrorMessage(activity.curPreviewHeight, activity.curPreviewWidth, e.getMessage())));
					}
				}
				break;

			default:
				break;
			}
		}
		
	}
	
	/**
	 * 计算视频录制的进度条
	 */
	private Runnable mediaRun = new Runnable(){
		
		private int sleepTime = MAX_DURATION / MAX_FREQUENCY;
		private float addProgress = 361f / MAX_FREQUENCY;
		
		@Override
		public void run() {
			while (mProgress < MAX_PROGRESS && mTime < MAX_DURATION && mIsMediaRun) {
				try {
					Thread.sleep(sleepTime);
					mTime += sleepTime;
					Message message = new Message();
					message.what = MESSAGE_MEDIA;
					message.obj = (mProgress += addProgress);
					mHandler.sendMessage(message);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			if (mTime >= MAX_DURATION)
				mHandler.sendEmptyMessage(MESSAGE_MEDIA_COMPLETE);
		}
		
	};
	
	private boolean mIsSwitchCamera;
	private float mX;
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		super.onTouchEvent(event);
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			mIsSwitchCamera = false;
			mX = event.getX();
			return true;
			
		case MotionEvent.ACTION_MOVE:
			float dX = event.getX();
			// 当手指横向滑动超过屏幕的一半且当前机器有前置摄像头、没有正在拍摄时，切换前后摄像。
			if (Math.abs(mX - dX) > (getResources().getDisplayMetrics().widthPixels/3) && mSwitch.getVisibility() == View.VISIBLE && !mIsSwitchCamera){
				mIsSwitchCamera = true;
				switchCamera();
			}
			return true;

		default:
			return false;
		}
	}

	@Override
	public void onClick(View v) {
		if (mIsStartMediaRecord) return;
		switch (v.getId()) {
			
		case R.id.camera_flash:
			switchFlish(FLASH_MODE.values()[++mFlashClick % FLASH_MODE.values().length]);
			break;
			
		case R.id.camera_switch:
			switchCamera();
			break;
			
		case R.id.camera_cancel:
			// 删掉前一个视频片段
			Float rollbackProgress = mCameraProgressBar.removePart();
			if (rollbackProgress != null){
				mProgress = rollbackProgress;
				mTime = (int) ((rollbackProgress / MAX_PROGRESS) * SECOND * 10);
				if (mTime == 0){
					mFlash.setVisibility(View.VISIBLE);
				}
				if (mTime < MIN_DURATION){
					mComfirm.setEnabled(false);
					mComfirm.setBackgroundResource(R.drawable.btn_camera_comfirm);
				} 
				if (mTime == 0){
					mCancel.setBackgroundResource(R.drawable.btn_camera_cancel_selector);
				}
				showTime();
				deleteLastVideo();
				if(mVideoTmps.isEmpty()) mSwitch.setVisibility(View.VISIBLE);
			} else {
				finish();
			}
			break;
			
		case R.id.camera_comfirm:
			mediaRecorderComplete();
			break;
			
		default:
			break;
		}
	}
	
	private CircularProgressBar.LongClickListener longClickListener = new CircularProgressBar.LongClickListener() {
		
		@Override
		public void onLongClick(boolean click) {
			// 切换视频录制的开始和停止
			if (click) {
				startMediaRecorder();
			} else {
				stopMediaRecorder(true);
			}
			if (mFirstCamera != null){
				mFirstCamera.setVisibility(View.GONE);
			}
		}

		@Override
		public void onClick() {
			SuperToast.show(CameraActivity.this, getString(R.string.activity_camera_start_fail));
			if (mFirstCamera != null){
				mFirstCamera.setVisibility(View.GONE);
			}
		}
	};
	
	private void mediaRecorderComplete(){
		mIsMediaRun = false;
		mComfirm.setEnabled(false);
		stopMediaRecorder(true);  //如果这里stop，那么底部ui会闪烁一下
		mCameraProgressBar.setEnabled(false);
		releaseCamera();
		startVideoEditActivity();
	}
	
	private void startVideoEditActivity(){
		Video video = new Video();
		video.setLat(CacheBean.getInstance().getString(this, CacheKeyConstants.LOCATION_LAT));
		video.setLon(CacheBean.getInstance().getString(this, CacheKeyConstants.LOCATION_LON));
		video.setTimes(mTime);    
		video.setRotate(mIsFront ? 270 : 90);
		video.setHeight(String.valueOf(mVideoHeight));
		video.setWidth(String.valueOf(mVideoWidth));

		Intent intent = new Intent(this, mEditType == RECORD_TYPE_GLIMPSE ? VideoEditActivity.class : StoryVideoEditActivity.class);
		intent.putExtra(VideoEditActivity.INTENT_VIDEO, video);
		intent.putParcelableArrayListExtra(VideoEditActivity.INTENT_VIDEO_PATH_LIST, mVideoTmps);
		startActivityForResult(intent, REQUEST_CODE);
	}
	
	private void resetCamera(){
		// 重置状态
		mTime 	  = 0;
		mProgress = 0;
		mIsFront 	= false;
		mIsMediaRun = false;
		mTimeText.setText("0.0");
		mVideoTmps.clear();
		mCameraProgressBar.resetProgress();
		mCameraProgressBar.setEnabled(true);
		mFlash.setBackgroundResource(R.drawable.btn_flash_auto_selector);
		mCancel.setEnabled(true);
		mCancel.setVisibility(View.VISIBLE);
		mCancel.setBackgroundResource(R.drawable.btn_camera_cancel_selector);
		mComfirm.setEnabled(false);
		mComfirm.setBackgroundResource(R.drawable.btn_camera_comfirm);
		mComfirm.setVisibility(View.VISIBLE);
		// 如果有前后摄像头，显示前后摄像头的切换按钮
		if (mHaveFrontCamera)
			mSwitch.setVisibility(View.VISIBLE);
		releaseMediaRecorder();
		releaseCamera();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == VideoEditActivity.RESULT_RESET){
			resetCamera();
		} else if (resultCode == VideoEditActivity.RESULT_VIDEO){
			mIsResultVideo = true;
			setResult(VideoEditActivity.RESULT_VIDEO, data);
			finish();
		} else if (resultCode == StoryVideoEditActivity.RESULT_STORY_VIDEO){
			mIsResultVideo = true;
			finish();
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	private void switchFlish(FLASH_MODE flash){
		Parameters parameters = mCamera.getParameters();
		switch (flash) {
		case ON:
			parameters.setFlashMode(Parameters.FLASH_MODE_ON);//开启闪光灯
			parameters.setFlashMode(Parameters.FLASH_MODE_TORCH);
			mFlash.setBackgroundResource(R.drawable.btn_flash_on_selector);
			break;
			
		case OFF:
			parameters.setFlashMode(Parameters.FLASH_MODE_OFF);
			mFlash.setBackgroundResource(R.drawable.btn_normflash_selector);
			break;
			
		case AUTO:
			parameters.setFlashMode(Parameters.FLASH_MODE_AUTO);
			mFlash.setBackgroundResource(R.drawable.btn_flash_auto_selector);
			break;

		default:
			break;
		}
		mCamera.setParameters(parameters); 
	}
	
	private void startMediaRecorder(){
		mIsStart = true;
		mCancel.setEnabled(false);
//		setCameraAutoFocus();
		setCameraPicFocus();
		createMediaRecorder();
	}
	
	private void stopMediaRecorder(boolean showProgressBar){
		Log.d(TAG, " stopMediaRecorder ");
		if (mMediaRecorder != null){
			mIsStart = false;
			mIsMediaRun = false;
			mAddPart = true;
			mCancel.setEnabled(true);
			mCancel.setBackgroundResource(R.drawable.btn_camera_tran_selector);
			setCameraContinuousVideo();
			try{
				mMediaRecorder.setOnErrorListener(null);
				mMediaRecorder.setPreviewDisplay(null);
				mMediaRecorder.stop();
			} catch(Exception e){
				e.printStackTrace();
			} finally{
				if (mCamera != null)
					mCamera.lock();
				if(showProgressBar)	disenabledUserView();
			}
		}
	}
	
	private void enabledUserView(){
		if (mTime == 0){
			// 录像开始后关闭前后摄像头的切换
			mSwitch.setVisibility(View.INVISIBLE);
		}
//		enableProgressBar();
		showSecondaryProgress();
		startTimeRunnable();
		hideDiscripttionWindow();
//		disableSound();
		mCancel.animate().alpha(0);
		mComfirm.animate().alpha(0).setListener(mBtnHideListener);
	}
	
	private AnimatorListener mBtnHideListener = new AnimatorListener() {
		
		@Override
		public void onAnimationStart(Animator animation) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onAnimationRepeat(Animator animation) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onAnimationEnd(Animator animation) {
			mCancel.setVisibility(View.INVISIBLE);
			mComfirm.setVisibility(View.INVISIBLE);
		}
		
		@Override
		public void onAnimationCancel(Animator animation) {
			// TODO Auto-generated method stub
			
		}
	};
	
	private void disenabledUserView(){
		// 弹出填写视频说明的对话框
		showDiscripttionWindow();
//		enableSound();
		mCancel.setVisibility(View.VISIBLE);
		mComfirm.setVisibility(View.VISIBLE);
		ObjectAnimator.ofFloat(mCancel, "alpha", 0, 1).start();
		ObjectAnimator.ofFloat(mComfirm, "alpha", 0, 1).start();
	}
	
	private void setCameraAutoFocus(){
		if (mSupportAutoFocus && mCamera != null){
			Parameters parameters = mCamera.getParameters();
			parameters.setFocusMode(Parameters.FOCUS_MODE_AUTO);
			mCamera.setParameters(parameters);
		}
	}
	
	private void setCameraPicFocus(){
		try {
			if (mSupportAutoFocus && mCamera != null){
				Parameters parameters = mCamera.getParameters();
				parameters.setFocusMode(Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
				mCamera.setParameters(parameters);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void setCameraContinuousVideo(){
		if (mSupportContinuousVideo && mCamera != null && !mIsMediaRun){
			try {
				Parameters parameters = mCamera.getParameters();
				parameters.setFocusMode(Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
				mCamera.setParameters(parameters);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private void startTimeRunnable(){
		mIsMediaRun = true;
		mHandler.postDelayed(new Runnable() {
			
			@Override
			public void run() {
				AppContext.EXECUTOR_SERVICE.execute(mediaRun);
			}
		}, 300);
	}
	
	/**
	 * 显示视频的确认框
	 */
	private void showDiscripttionWindow(){
		if(mCameraLayout.getVisibility()==View.VISIBLE) return ;
		mCameraLayout.setVisibility(View.VISIBLE);
//		ObjectAnimator.ofFloat(mBottomLayout, "alpha", 0, 1).setDuration(200).start();
	}
	
	/**
	 * 隐藏视频的确认框
	 */
	private void hideDiscripttionWindow(){
//		ObjectAnimator.ofFloat(mBottomLayout, "alpha", 1, 0).setDuration(200).start();
	}
	
	private void showSecondaryProgress(){
		int miniProgress = (int) (((float)MIN_DURATION/MAX_DURATION) * 360);
		mCameraProgressBar.setSecondaryProgress(miniProgress);
		mCameraProgressBar.setSecondaryProgressBackground(true);
	}
	
	
	
	/**
	 * 切换前后摄像头
	 */
	private void switchCamera(){
		releaseMediaRecorder();
		releaseCamera();
		if (mIsFront){
			mIsFront = false;
			createCamera(Camera.CameraInfo.CAMERA_FACING_BACK);
		} else {
			mIsFront = true;
			createCamera(Camera.CameraInfo.CAMERA_FACING_FRONT);
		}
	}
	
	private void releaseMediaRecorder() {
		Log.d(TAG, " release mediarecorder ");
		if (mMediaRecorder != null) {
			mMediaRecorder.setOnErrorListener(null);
			mMediaRecorder.reset();
			mMediaRecorder.release(); 
			mMediaRecorder = null;
		}
		if (mCamera != null){
			mCamera.lock(); 
		}
	}

	private void releaseCamera() {
		if (mCamera != null) {
			mCamera.release();
			mCamera = null;
		}
	}
	
	private boolean deleteLastVideo(){
		return new File(mVideoTmps.remove(mVideoTmps.size()-1).getVideoTmpPath()).delete();
	}
	
	private void deleteAllVideo(){
		for (VideoTmp videoTmp:mVideoTmps){
			new File(videoTmp.getVideoTmpPath()).delete();
		}
	}
	
	private int retryNum;
	
	private CameraTouchFocusListener.OnFocusCallback mFocusCallback = new CameraTouchFocusListener.OnFocusCallback() {
		@Override
		public void onTouchFocusEnd(boolean success, Camera camera) {
			Log.d(TAG, "onAutoFocus success "+success);
			if (success){
				// 手动对焦成功后恢复回自动对焦
				setCameraContinuousVideo();
			} else if (CameraTouchFocusListener.MAX_RETRY_NUM > retryNum && mCamera != null){
				// 手动对焦失败后重新尝试对焦
				retryNum++;
				mFoucsListener.setCameraFocus(mCamera.getParameters(), mFoucsListener.getAreaX(), mFoucsListener.getAreaY());
			} else {
				// 当手动对焦失败后重新尝试对焦的次数超过重新对焦的次数限制时，恢复回自动对焦
				retryNum = 0;
				setCameraContinuousVideo();
			}
		}

		@Override
		public void onTouchFocusStart(Camera camera) {

		}
	};

	@Override
	protected void onResume() {
		Log.d(TAG, "onResume");
		super.onResume();
	}
	
	@Override
	protected void onRestart() {
		Log.d(TAG, "onRestart");
		if (mCamera == null && mIsSurfaceCreated){
			createCamera(cameraType);
		}
		super.onRestart();
	}

	@Override
	protected void onPause() {
		Log.d(TAG, "onPause");
		if (mIsMediaRun){
			stopMediaRecorder(false);
		} else {
			releaseMediaRecorder();
		}
		releaseCamera();
		super.onPause();
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		// 在界面初始化过程中拦截返回键事件，防止某些小米手机上因为初始化未完成时退出Activity导致界面出错。
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0 && mInit){
			return true;
		}
		return super.onKeyUp(keyCode, event);
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		mInit = false;
		super.onWindowFocusChanged(hasFocus);
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		Log.d(TAG, "surfaceCreated");
		mIsSurfaceCreated = true;
		if (mCamera == null){
			createCamera(Camera.CameraInfo.CAMERA_FACING_BACK);
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		Log.d(TAG, "surfaceDestroyed");
		mIsSurfaceCreated = false;
		releaseMediaRecorder();
		releaseCamera();
	}

}