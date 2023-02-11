/**
 * 
 */
package com.bloomlife.videoapp.activity;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.ref.WeakReference;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.tsz.afinal.annotation.view.ViewInject;
import android.animation.Animator.AnimatorListener;
import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.bloomlife.android.bean.CacheBean;
import com.bloomlife.android.framework.BaseActivity;
import com.bloomlife.android.view.SoftKeyboardLayout;
import com.bloomlife.videoapp.R;
import com.bloomlife.videoapp.app.AppContext;
import com.bloomlife.videoapp.app.UploadBackgroundService;
import com.bloomlife.videoapp.common.AnalyticUtil;
import com.bloomlife.videoapp.common.CacheKeyConstants;
import com.bloomlife.videoapp.common.util.CameraUtil;
import com.bloomlife.videoapp.common.util.UIHelper;
import com.bloomlife.videoapp.common.util.Utils;
import com.bloomlife.videoapp.dialog.AccountDialog;
import com.bloomlife.videoapp.dialog.BaseDialog;
import com.bloomlife.videoapp.model.Video;
import com.bloomlife.videoapp.model.VideoTmp;
import com.bloomlife.videoapp.view.GlobalProgressBar;
import com.bloomlife.videoapp.view.VMediaPlayer;
import com.coremedia.iso.boxes.Container;
import com.googlecode.mp4parser.authoring.Movie;
import com.googlecode.mp4parser.authoring.Track;
import com.googlecode.mp4parser.authoring.builder.DefaultMp4Builder;
import com.googlecode.mp4parser.authoring.container.mp4.MovieCreator;
import com.googlecode.mp4parser.authoring.tracks.AppendTrack;

/**
 * @author <a href="mailto:lan4627@gmail.com">zhengxingtian</a>
 * 
 * @parameter INTENT_VIDEO Video 传一个需要播放的视频进来。
 * @parameter INTENT_TOPIC List<String> 传一个话题列表进来。
 * @parameter INTENT_TOPIC_SELECT List<String> 传一个被选中的话题的列表进来。
 * @parameter INTENT_VIDEO_PATH_LIST List<String> 传拍摄好的视频片段列表进来，由这边来合并视频。
 * @date 2014年11月28日 下午2:15:52
 */
public class VideoEditActivity extends BaseActivity implements View.OnClickListener, TextureView.SurfaceTextureListener {

	public static final String TAG = VideoEditActivity.class.getSimpleName();

	public static final String INTENT_VIDEO = "video";
	public static final String INTENT_VIDEO_PATH_LIST = "video_path_list";

	public static final int VIDEO_PROGRESS = 1;
	public static final int JOIN_VIDEO_COMPLETE = 2;

	public static final int RESULT_VIDEO = 1001;
	public static final int RESULT_RESET = 1000;
	
	public static final int DESCRIPTION_EDITTEXT_DRUATION = 300;

	@ViewInject(id = R.id.video_edit_delete, click = ViewInject.DEFAULT)
	protected ImageView mDelete;

	@ViewInject(id = R.id.video_edit_upload, click = ViewInject.DEFAULT)
	protected ImageView mUpload;
	
	@ViewInject(id = R.id.editIcon, click = ViewInject.DEFAULT)
	protected ImageView editIcon;

	@ViewInject(id = R.id.video_edit_edit_description, click = ViewInject.DEFAULT)
	protected EditText mEditDescription;

	@ViewInject(id = R.id.video_edit_time)
	protected TextView mTimeText;

	@ViewInject(id = R.id.video_edit_edit_description_layout, click = ViewInject.DEFAULT)
	protected ViewGroup mEditDescriptionLayout;

	@ViewInject(id = R.id.video_edit_layout)
	protected SoftKeyboardLayout mLayout;

	@ViewInject(id = R.id.video_edit_textureview)
	protected TextureView mTextureView;

	@ViewInject(id = R.id.video_edit_progressbar)
	protected GlobalProgressBar mProgressBar;

	protected Handler mHandler = new VideoPlayHandler(this);

	private Video mVideo;
	private List<VideoTmp> mVideoTmps;

	private SurfaceTexture mSurfaceTexture;
	private VMediaPlayer mMediaPlayer;
	private GestureDetector mGestureDetector;
	private Animation mDescriptionAnimIn;
	private volatile boolean isVideoRun;
	protected boolean mIsUpload;
	protected boolean mIsOpenEditText;
	protected boolean mJoinComplete = true;
	protected boolean mIsShowDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView();
		initLayoutView();
	}

	protected void setContentView(){
		setContentView(R.layout.activity_video_edit);
	}

	protected void initLayoutView(){
		if (mVideo == null){
			mVideo = getIntent().getParcelableExtra(INTENT_VIDEO);
			mVideoTmps = getIntent().getParcelableArrayListExtra(INTENT_VIDEO_PATH_LIST);
		}
		mGestureDetector = new GestureDetector(this, gestureListener);
		mTimeText.setTypeface(UIHelper.getHelveticaTh(this));
		initEditDesciptionText();
		initSurfaceView();
		videoMerge();
		startAnim();
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putParcelable(INTENT_VIDEO, mVideo);
		outState.putParcelableArrayList(INTENT_VIDEO_PATH_LIST, new ArrayList<>(mVideoTmps));
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		mVideo = savedInstanceState.getParcelable(INTENT_VIDEO);
		mVideoTmps = savedInstanceState.getParcelableArrayList(INTENT_VIDEO_PATH_LIST);
		super.onRestoreInstanceState(savedInstanceState);
	}

	private void initSurfaceView() {
		mTextureView.setSurfaceTextureListener(this);
		mTextureView.setOpaque(false);
		
		final int screenHeight =AppContext.deviceInfo.getScreenHeight();
		final int screenWidth =AppContext.deviceInfo.getScreenWidth();
		
		float height = Integer.parseInt(mVideo.getHeight());
		float width  = Integer.parseInt(mVideo.getWidth());
		FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) mTextureView.getLayoutParams();
		if (mVideo.getRotate() == 0){
			//高度适配
			params.width = screenHeight*screenHeight/screenWidth;
			params.height = screenHeight;
		} else if(screenHeight-height>screenWidth-width){
			//高度适配
			params.width = (int) (width*screenHeight/height);
			params.height = screenHeight;
		} else {
			//宽度适配
			params.width = screenWidth ;
			params.height = (int) (height * screenWidth/ width);
		}
		params.gravity = Gravity.CENTER;
		mTextureView.setLayoutParams(params);

		mTextureView.requestLayout();
	}

	private void initEditDesciptionText() {
		mEditDescription.setOnEditorActionListener(editorActionListener);
		mEditDescription.setOnFocusChangeListener(focusChangeListener);
		mEditDescription.addTextChangedListener(mInputLengthListener);
		mDescriptionAnimIn = AnimationUtils.loadAnimation(this, R.anim.video_edit_des_in);
		mDescriptionAnimIn.setAnimationListener(descriptionAnimListener);
	}
	
	private OnEditorActionListener editorActionListener = new OnEditorActionListener() {
		
		@Override
		public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
			if (actionId == EditorInfo.IME_ACTION_SEND){
				upLoadVideo();
				return true;
			} else {
				return false;
			}
		}
	};

	Pattern mZhPattern = Pattern.compile("[\u4E00-\u9FA5]");
	Pattern mEnPattern = Pattern.compile("[^\u4E00-\u9FA5]");

	private TextWatcher mInputLengthListener = new TextWatcher() {
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {

		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {

		}

		@Override
		public void afterTextChanged(Editable s) {
			Matcher zhMatcher = mZhPattern.matcher(s.toString());
			Matcher enMatcher = mEnPattern.matcher(s.toString());
			int zhCount = 0;
			int enCount = 0;
			int end = 0;
			for (int i=0; i<s.length(); i++){
				if (zhMatcher.find()){
					zhCount += 2;
					end++;
				}
				if (enMatcher.find()){
					enCount++;
					end++;
				}
				// 判断字数是否超过长度
				if (enCount + zhCount > 60){
					CharSequence newChars = s.subSequence(0, end);
					mEditDescription.removeTextChangedListener(this);
					mEditDescription.setText(newChars);
					mEditDescription.setSelection(newChars.length());
					mEditDescription.addTextChangedListener(this);
					break;
				}
			}
		}
	};
	
	private OnFocusChangeListener focusChangeListener = new OnFocusChangeListener() {
		
		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			if (hasFocus && !mIsOpenEditText) {
				ObjectAnimator mEditAnim = ObjectAnimator.ofInt(mEditDescription, "width", mEditDescription.getWidth(), getResources().getDisplayMetrics().widthPixels);
				mEditAnim.addListener(mAnimatorListener);
				mEditAnim.setDuration(DESCRIPTION_EDITTEXT_DRUATION);
				mEditAnim.start();
				mIsOpenEditText = true;
			} else {
				UIHelper.hideSoftInput(VideoEditActivity.this, mEditDescription);
			}
		}
	};
	
	private AnimatorListener mAnimatorListener = new AnimatorListener() {
		
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
			mEditDescription.setHint(R.string.activity_camera_video_description);
		}
		
		@Override
		public void onAnimationCancel(Animator animation) {
			// TODO Auto-generated method stub
			
		}
	};
	
	private AnimationListener descriptionAnimListener = new AnimationListener() {
		
		@Override
		public void onAnimationStart(Animation animation) {
			mDelete.setEnabled(false);
			mUpload.setEnabled(false);
		}
		
		@Override
		public void onAnimationRepeat(Animation animation) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onAnimationEnd(Animation animation) {
			if (mJoinComplete){
				mDelete.setEnabled(true);
				mUpload.setEnabled(true);
			}
		}
	};
	
	/**
	 * 拼接视频片段
	 */
	private void videoMerge() {
		// 如果只有一个视频就不用拼接。
		if (mVideoTmps.size() == 1){
			mVideo.setFilePath(mVideoTmps.get(0).getVideoTmpPath());
			return;
		}
		enabledUserView(false);
		mJoinComplete = false;
		mProgressBar.setVisibility(View.VISIBLE);
		// 是否有前置摄像头拍摄的视频片段
		AppContext.EXECUTOR_SERVICE.execute(videoMergeRunnable);
	}
	
	protected void videoMergeComplete(){
		mProgressBar.setVisibility(View.INVISIBLE);
		mJoinComplete = true;
		enabledUserView(true);
		startVideoPlayer();
		// 删除视频片段
		deleteVideo();
	}

	protected void videoPlayTime(long time, long totalTime){
		float second = time / 1000f;
		if (second > 10) second = 10.0f;
		mTimeText.setText(String.format("%.1f", second));
	}
	
	private Runnable videoMergeRunnable = new Runnable() {
		
		@Override
		public void run() {
			mVideo.setFilePath(outputFile(joinVideo(mVideoTmps)));
			mHandler.sendEmptyMessage(JOIN_VIDEO_COMPLETE);
		}
		
		private Movie joinVideo(List<VideoTmp> mVideoTmps){
			List<Movie> movies = new ArrayList<>();
			Movie movie = new Movie();
			try {
				// 读取所有的分段视频
				for (VideoTmp videoTmp:mVideoTmps) {
					Movie mc = MovieCreator.build(videoTmp.getVideoTmpPath());
					if (mc != null){
						movies.add(mc);
					}
				}
				List<Track> videoTracks = new LinkedList<>();
				List<Track> audioTracks = new LinkedList<>();
				for (Movie m : movies) {
					for (Track t : m.getTracks()) {
						if (t.getHandler().equals("soun")) {
							audioTracks.add(t);
						}
						if (t.getHandler().equals("vide")) {
							videoTracks.add(t);
						}
					}
				}
				if (audioTracks.size() > 0) {
					movie.addTrack(new AppendTrack(audioTracks.toArray(new Track[audioTracks.size()])));
				}
				if (videoTracks.size() > 0) {
					movie.addTrack(new AppendTrack(videoTracks.toArray(new Track[videoTracks.size()])));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return movie;
		}
		
		private String outputFile(Movie movie){
			String videoUrl = getNewVideoPath();
			RandomAccessFile file = null;
			FileChannel fc = null;
			try {
				file = new RandomAccessFile(videoUrl, "rw");
				fc = file.getChannel();
				Container container = new DefaultMp4Builder().build(movie);
				container.writeContainer(fc);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					if (file != null)
						file.close();
					if (fc != null)
						fc.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			return videoUrl;
		}
		
	};
	
	private String getNewVideoPath(){
		String newPath = getExternalFilesDir(Environment.DIRECTORY_MOVIES) + "/video" + System.currentTimeMillis()+".mp4";
		mVideo.setFilePath(newPath);
		return newPath;
	}

	private void startAnim() {
		mHandler.postDelayed(new Runnable() {

			@Override
			public void run() {
				mEditDescriptionLayout.setVisibility(View.VISIBLE);
				mEditDescriptionLayout.startAnimation(mDescriptionAnimIn);
				mDelete.setVisibility(View.VISIBLE);
				mDelete.startAnimation(AnimationUtils.loadAnimation(VideoEditActivity.this,
						R.anim.video_edit_delete_in));
				mUpload.setVisibility(View.VISIBLE);
				mUpload.startAnimation(AnimationUtils.loadAnimation(VideoEditActivity.this,
						R.anim.video_edit_upload_in));
			}
		}, 500);
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
	}

	/**
	 * 创建视频播放器
	 */
	private void createVideoPlayer(){
		mMediaPlayer = new VMediaPlayer();
		mMediaPlayer.setOnCompletionListener(completionListener);
		mMediaPlayer.setOnPreparedListener(perPreparedListener);
	}
	
	
	private void setVideoPlayer() {
		if (mSurfaceTexture == null) return;
		try {
			mMediaPlayer.setLooping(false);
			mMediaPlayer.setSurface(new Surface(mSurfaceTexture));
		} catch (Exception e) {
			e.printStackTrace();
		}
		setVideoPlayerSource();
	}

	protected void setVideoPlayerSource(){
		try {
			mMediaPlayer.setDataSource(mVideo.getFilaPath());
			mMediaPlayer.prepare();
		} catch (Exception e){
			e.printStackTrace();
		}
		mMediaPlayer.start();
	}

	private MediaPlayer.OnPreparedListener perPreparedListener = new MediaPlayer.OnPreparedListener() {

		@Override
		public void onPrepared(MediaPlayer mp) {
			if (!isVideoRun){
				AppContext.EXECUTOR_SERVICE.execute(videoRun);
			}
		}
	};

	private MediaPlayer.OnCompletionListener completionListener = new MediaPlayer.OnCompletionListener() {

		@Override
		public void onCompletion(MediaPlayer mp) {
			videoPlayCompleted(mp);
		}
	};

	protected void startVideoPlayer() {
		if (mMediaPlayer == null){
			createVideoPlayer();
		}
		setVideoPlayer();
	}

	protected void resetVideoPlayer(){
		if (mMediaPlayer != null){
			mMediaPlayer.reset();
		}
	}

	protected void releaseVideoPlayer() {
		if (mMediaPlayer != null) {
			isVideoRun = false;
			mMediaPlayer.release();
			mMediaPlayer = null;
		}
	}

	protected void startPlay(){
		if (mMediaPlayer != null && !mMediaPlayer.isPlaying())
			mMediaPlayer.start();
	}

	protected void pausePlay(){
		if (mMediaPlayer != null && !mMediaPlayer.isPause())
			mMediaPlayer.pause();
	}

	protected void resumePlay(){
		startPlay();
	}

	protected void stopPlay(){
		if (mMediaPlayer != null && mMediaPlayer.isPlaying())
			mMediaPlayer.stop();
	}

	protected void videoPlayCompleted(MediaPlayer mp){
		resetVideoPlayer();
		setVideoPlayerSource();
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.video_edit_edit_description:
			mEditDescription.setVisibility(View.VISIBLE);
			break;

		case R.id.video_edit_delete:
			finish();
			break;

		case R.id.video_edit_upload:
			upLoadVideo();
			break;
		case R.id.video_edit_edit_description_layout:
		case R.id.editIcon:
			mEditDescription.requestFocus();
			break;

		default:
			break;
		}
	}

	@Override
	public void finish() {
		if (!mIsUpload) {
			new File(mVideo.getFilaPath()).delete();
			setResult(RESULT_RESET);
		}
		super.finish();
	}

	/**
	 * 上传视频
	 */
	protected void upLoadVideo() {
		String description = mEditDescription.getText().toString().trim();
		if (TextUtils.isEmpty(description)) {
			mEditDescription.requestFocus();
			UIHelper.showSoftInput(this);
			return;
		}
		if (!Utils.isLogin(this, false)){
			showLoginDialog();
			return;
		}
		mUpload.setEnabled(false);
		mIsUpload = true;
		Video video = getVideo();
		//录制了视频的统计事件
		AnalyticUtil.sendAnalytisEvent(getApplicationContext(), AnalyticUtil.Event_Record_Video, null);
		
		startUploadService(video);
		setActivityResult(video);
	}

	protected Video getVideo(){
		CameraUtil.createVideoThumbnail(mVideo.getFilaPath());
		mVideo.setSize(getFileSize(mVideo.getFilaPath()));
		mVideo.setCreatetime(System.currentTimeMillis());
		mVideo.setDescription(mEditDescription.getText().toString());
		mVideo.setUid(CacheBean.getInstance().getLoginUserId());
		mVideo.setCity(CacheBean.getInstance().getString(this, CacheKeyConstants.LOCATION_CITY, ""));
		return mVideo;
	}

	private void startUploadService(Video video){
		Intent intent = new Intent(getApplicationContext(), UploadBackgroundService.class);
		intent.putExtra(UploadBackgroundService.INTENT_UPLOAD_VIDEO, video);
		startService(intent);
	}
	
	private void setActivityResult(Video video){
		Intent intent = new Intent();
		intent.putExtra(INTENT_VIDEO, video);
		setResult(RESULT_VIDEO, intent);
		finish();
	}

	protected long getFileSize(String path) {
		return new File(path).length() / 1024;
	}

	private Runnable videoRun = new Runnable() {

		@Override
		public void run() {
			isVideoRun = true;
			while (isVideoRun) {
				try {
					Thread.sleep(100);
					mHandler.sendEmptyMessage(VIDEO_PROGRESS);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

	};

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		super.onTouchEvent(event);
		mGestureDetector.onTouchEvent(event);
		return true;
	}

	protected void onSingleTapScreen(MotionEvent e){
		if (mMediaPlayer == null) {
			createVideoPlayer();
			setVideoPlayer();
		} else if (mMediaPlayer.isPlaying()) {
			pausePlay();
		} else {
			resumePlay();
		}
		UIHelper.hideSoftInput(VideoEditActivity.this, mEditDescription);
	}

	/**
	 * 监听屏幕滑动的手势和双击事件
	 */
	private GestureDetector.SimpleOnGestureListener gestureListener = new GestureDetector.SimpleOnGestureListener() {

		@Override
		public boolean onSingleTapUp(MotionEvent e) {
			onSingleTapScreen(e);
			return super.onSingleTapUp(e);
		}
	};

	protected void showLoginDialog(){
		stopPlay();
		mIsShowDialog = true;
		AccountDialog dialog = new AccountDialog();
		dialog.setOnDismissListener(mAccountDialogDismissListener);
		dialog.show(this);
	}

	@Override
	public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
		mSurfaceTexture = surface;
		if (mMediaPlayer == null && mVideo.getFilaPath() != null) {
			createVideoPlayer();
			if (!TextUtils.isEmpty(mVideo.getFilaPath())){
				setVideoPlayer();
			}
		}
	}

	@Override
	public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

	}

	@Override
	public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
		mSurfaceTexture = null;
		Log.d(TAG, TAG + " surfaceDestroyed");
		if (mMediaPlayer != null) {
			Log.d(TAG, TAG + " releaseMediaPlayer");
			releaseVideoPlayer();
		}
		return false;
	}

	@Override
	public void onSurfaceTextureUpdated(SurfaceTexture surface) {

	}

	static class VideoPlayHandler extends Handler {

		private WeakReference<VideoEditActivity> mReference;

		public VideoPlayHandler(VideoEditActivity activity) {
			mReference = new WeakReference<>(activity);
		}

		@Override
		public void handleMessage(Message msg) {
			VideoEditActivity activity = mReference.get();
			if (activity == null)
				return;
			switch (msg.what) {
			case VIDEO_PROGRESS:
				if (activity.mMediaPlayer != null) {
					activity.videoPlayTime(
							activity.mMediaPlayer.getCurrentPosition(),
							activity.mMediaPlayer.getDuration()
					);
				}
				break;

			case JOIN_VIDEO_COMPLETE:
				activity.videoMergeComplete();
				break;

			default:
				break;
			}
			super.handleMessage(msg);
		}

	}

	@Override
	protected void onResume() {
		super.onResume();
		if (mIsShowDialog) return;
		startPlay();
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (mIsShowDialog) return;
		stopPlay();
	}
	
	private void enabledUserView(boolean enabled){
		mDelete.setEnabled(enabled);
		mUpload.setEnabled(enabled);
		mEditDescription.setEnabled(enabled);
	}
	
	private void deleteVideo(){
		for (VideoTmp videoTmp:mVideoTmps){
			new File(videoTmp.getVideoTmpPath()).delete();
			new File(videoTmp.getVideoTmpPath().replace(".mp4", ".ts")).delete();
		}
	}

	private BaseDialog.OnDismissListener mAccountDialogDismissListener = new BaseDialog.OnDismissListener() {
		@Override
		public void onDismiss() {
			startPlay();
			mIsShowDialog = false;
		}
	};

}
