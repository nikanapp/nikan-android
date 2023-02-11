/**
 * 
 */
package com.bloomlife.videoapp.activity.fragment;

import static com.bloomlife.videoapp.activity.VideoCommentActivity.RESULT_COMMENT_OK;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.tsz.afinal.FinalActivity;
import net.tsz.afinal.annotation.view.ViewInject;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.animation.AlphaAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.MessageRequest;
import com.android.volley.toolbox.Volley;
import com.bloomlife.android.bean.CacheBean;
import com.bloomlife.android.bean.FailureResult;
import com.bloomlife.android.bean.ProcessResult;
import com.bloomlife.android.common.util.DateUtils;
import com.bloomlife.android.common.util.StringUtils;
import com.bloomlife.videoapp.R;
import com.bloomlife.videoapp.activity.AnonymousChatActivity;
import com.bloomlife.videoapp.activity.MyVideoViewPagerActivity;
import com.bloomlife.videoapp.activity.VideoCommentActivity;
import com.bloomlife.videoapp.activity.VideoViewPagerActivity;
import com.bloomlife.videoapp.app.AppContext;
import com.bloomlife.videoapp.app.Constants;
import com.bloomlife.videoapp.common.CacheKeyConstants;
import com.bloomlife.videoapp.common.CommentText;
import com.bloomlife.videoapp.common.AnalyticUtil;
import com.bloomlife.videoapp.common.VideoReportListener;
import com.bloomlife.videoapp.common.util.ShareSDKUtil;
import com.bloomlife.videoapp.common.util.UIHelper;
import com.bloomlife.videoapp.common.util.Utils;
import com.bloomlife.videoapp.model.Comment;
import com.bloomlife.videoapp.model.CommentInfo;
import com.bloomlife.videoapp.model.Commenttags;
import com.bloomlife.videoapp.model.Video;
import com.bloomlife.videoapp.model.message.ClearDynamicImgMessage;
import com.bloomlife.videoapp.model.message.GetVideoMessage;
import com.bloomlife.videoapp.model.message.LookMessage;
import com.bloomlife.videoapp.model.message.ReportMessage;
import com.bloomlife.videoapp.model.message.SendDynamicImgMessage;
import com.bloomlife.videoapp.model.result.GetVideoResult;
import com.bloomlife.videoapp.view.DynamicalLayout;
import com.bloomlife.videoapp.view.DynamicalLayout.OnDynamicImgListener;
import com.bloomlife.videoapp.view.ReportOptionView;
import com.bloomlife.videoapp.view.NetworkConnentErrorView.OnRetryListener;
import com.bloomlife.videoapp.view.ReportOptionView.ReportListener;
import com.bloomlife.videoapp.view.SuperToast;
import com.bloomlife.videoapp.view.VMediaPlayer;
import com.bloomlife.videoapp.view.VideoLoadView;
import com.bloomlife.videoapp.view.dialog.MoreDialog;

/**
 * @author <a href="mailto:lan4627@gmail.com">zhengxingtian</a>
 *
 *
 * @parameter INTENT_VIDEO 传一个需要播放的视频进来。
 * @parameter INTENT_VIDEO_TYPE 播放的类型，NATIVE-播放本地视频，NETWORK-播放网络视频
 * @date 2014年11月24日 下午3:46:05
 */
public class AnonVideoPlayFragment extends VideoPlayerFragment implements OnClickListener{
	
	public static final String TAG = AnonVideoPlayFragment.class.getSimpleName();
	
	public static final String INTENT_VIDEO = "video";
	public static final String INTENT_VIDEO_IS_MY = "video_is_my";
	public static final String INTENT_VIDEO_POSITION = "video_position";
	
	public static final String RESULT_INTENT_POSITION = "result_position";
	public static final String RESULT_INTENT_LIKENUM = "result_likenum";
	public static final String RESULT_INTENT_IS_PLAY = "result_is_play";
	
	public static final int COMMENT_REQUEST_CODE = 9999;
	public static final int CHAT_REQUEST_CODE = 9998;
	
	public static final int NATIVE = 1;
	public static final int NETWORK = 2;

	@ViewInject(id=R.id.video_play_textureview, click=ViewInject.DEFAULT)
	TextureView mTextureView;
	
	@ViewInject(id=R.id.video_play_info)
	ViewGroup mVideoInfoLayout;
	
	@ViewInject(id=R.id.video_play_text_layout)
	ViewGroup mTextLayout;
	
	@ViewInject(id=R.id.video_play_more, click=ViewInject.DEFAULT)
	ImageView mBtnMore;
	
	@ViewInject(id=R.id.video_play_close, click=ViewInject.DEFAULT)
	ImageView mBtnClose;
	
	@ViewInject(id=R.id.activity_play_story_msg, click=ViewInject.DEFAULT)
	private
	ImageView mBtnMessage;
	
	@ViewInject(id=R.id.activity_video_play_dynamic_tips)
	TextView mDynTips;
	
	@ViewInject(id=R.id.video_play_elememt_layout)
	ViewGroup mElementLayout;
	
	@ViewInject(id=R.id.video_play_text)
	TextView mDescription;

	@ViewInject(id=R.id.video_play_views)
	TextView mViews;
	
	@ViewInject(id=R.id.video_play_heat)
	TextView mHeat;
	
	@ViewInject(id=R.id.video_play_comments_num)
	TextView mCommentsNum;
	
	@ViewInject(id=R.id.video_play_views_des)
	TextView mIgnitesDes;
	
	@ViewInject(id=R.id.video_play_heat_des)
	TextView mWacthDes;
	
	@ViewInject(id=R.id.video_play_comments_num_des)
	TextView mCommentsNumDes;
	
	@ViewInject(id=R.id.video_play_local)
	TextView mLocal;
	
	@ViewInject(id=R.id.video_play_hour)
	TextView mHour;
	
	@ViewInject(id=R.id.video_play_date)
	TextView mDate;
	
	@ViewInject(id=R.id.activity_video_play_fire, click=ViewInject.DEFAULT)
	ImageView mBtnDynamic;
	
	@ViewInject(id=R.id.first_video_tip)
	ViewStub mFirstVideoTip;
	
	@ViewInject(id=R.id.reloadView_stub)
	ViewStub mVideoLoadStub;

	VideoLoadView videoLoadView;
	
	@ViewInject(id=R.id.activity_video_play_expression)
	DynamicalLayout mDynamicalLayout;
	
	@ViewInject(id=R.id.activity_video_play_expression_tips)
	TextView mExpressionTips;
	
	@ViewInject(id=R.id.play_text_divider)
	View mDescriptDivider;
	
	@ViewInject(id=R.id.activity_video_play_msg_tips)
	TextView mMsgTips;
	
	protected volatile VMediaPlayer mMediaPlayer;
	protected View mScrollTips;
	
	protected Video mVideo;  
	protected boolean mIsMy;
	protected int mType;
	protected int mPosition;
	
	protected List<Comment> mVideoCommentList;
	protected List<CommentText> mVideoCommentTextList;
	
	protected String mSelectComment;
	private int mAllowcomment;
	private int mLoopNumber;
	
	private boolean mIsShowMsgTips;
	private boolean mIsShowDynTips;
	private boolean mIsShowDialog;
	private boolean mIsShowShareWindow;
	private boolean mIsVideoError;
	private boolean mIsViewPagerScroll;
	private boolean isPlayDestroyed;
	private boolean isPause;
	
	private boolean mIsVideoLoading;
	
	private volatile boolean isDestroyedView;
	
	private SurfaceTexture surface;
	
	/**加载视频详情是否失败*/
	private volatile boolean loadVideoDetailFailure = false ;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View layout = inflater.inflate(R.layout.fragment_video, container, false);
		FinalActivity.initInjectedView(AnonVideoPlayFragment.this, layout);
		mTextureView.setSurfaceTextureListener(surfaceTextureListener);
		initUiData();
		sendGetVideoTask();

		return layout;
	}
	
	private boolean isVideoViewPagerActivity(){
		return (getActivity() instanceof VideoViewPagerActivity && ((VideoViewPagerActivity)getActivity()).isFromConversation()) || getActivity() instanceof MyVideoViewPagerActivity;
	}
	
	private boolean isHideMessageBtn(){
		return StringUtils.isEmpty(mVideo.getVideoid()) || CacheBean.getInstance().getLoginUserId().equals(mVideo.getUid());
	}
	
	private boolean isShowMsgBtn(){
		return !isHideMessageBtn() && !isVideoViewPagerActivity();
	}
	
	/**
	 * 
	 */
	protected void initUiData() {
		mVideo = getArguments().getParcelable(INTENT_VIDEO);
		mIsMy  = getArguments().getBoolean(INTENT_VIDEO_IS_MY);
		if(!StringUtils.isEmpty(mVideo.getFilaPath())){
			mType = AnonVideoPlayFragment.NATIVE;
		}else{
			mType = AnonVideoPlayFragment.NETWORK;
		}
		mPosition =  getArguments().getInt(INTENT_VIDEO_POSITION);
		
		mBtnDynamic.setEnabled(false);
		mDynamicalLayout.setOnDynamicImgListener(mDynamicImgListener);
		
		Typeface bebas = UIHelper.getHelveticaTh(getActivity());
		mHour.setTypeface(bebas);
		mDate.setTypeface(bebas);
		mLocal.setTypeface(bebas);
		mDescription.setTypeface(bebas, Typeface.BOLD_ITALIC);
		
		mHeat.setTypeface(bebas);
		mViews.setTypeface(bebas);
		mCommentsNum.setTypeface(bebas);
		
		mWacthDes.setTypeface(bebas);
		mIgnitesDes.setTypeface(bebas);
		mCommentsNumDes.setTypeface(bebas);
		
		if(isShowMsgBtn()){
			mBtnMessage.setVisibility(View.VISIBLE);
		}
		// 显示消息按钮的引导
		if (CacheBean.getInstance().getInt(getActivity().getApplicationContext(), CacheKeyConstants.KEY_FIRST_MESSAGE, CacheKeyConstants.IS_FIRST) == CacheKeyConstants.IS_FIRST
				&& isShowMsgBtn()){
			mIsShowMsgTips = true;
		}
		// 显示表情弹幕的引导
		if (CacheBean.getInstance().getInt(getActivity().getApplicationContext(), CacheKeyConstants.KEY_FIRST_DYNAMIC_WINDOW, CacheKeyConstants.IS_FIRST) == CacheKeyConstants.IS_FIRST){
			mIsShowDynTips = true;
		}
		
		AnimationDrawable anim = (AnimationDrawable) mBtnDynamic.getBackground();
		anim.start();
	}

	/**
	 * 
	 */
	@Override
	protected void loadPlaySource() {
		if (mType == NATIVE){
			playLocalResource(mVideo.getFilaPath());
		} else {
			startDownload(mVideo.getVideouri());
		}
	}
	
	protected void sendGetVideoTask(){
		if(StringUtils.isEmpty(mVideo.getVideoid())) return ;
		GetVideoMessage message = GetVideoMessage.makeVideoMessage(getActivity(), mVideo.getVideoid(), mIsMy);
		Volley.getRequestQueue().add(new MessageRequest(message, mVideoInfoResultListener));
	}
	
	protected boolean impressionClick = false;
	
	protected boolean isAnimationing = false;

	protected boolean isPageOnCurrent(){
		boolean result = VideoViewPagerActivity.getCurrentItemIndex()==mPosition;
		if(!result) Log.d(TAG, " 不是当前选中的项目 , currentIndex=" + VideoViewPagerActivity.getCurrentItemIndex() + " , mPosition=" + mPosition);
		return result;
	}
	
	protected void playLocalResource(String filePath){
		super.playLocalResource(filePath);
		mVideo.setWidthAndHeight(Utils.getVideoSize(filePath));
	}

	@Override
	protected void onCompletion(MediaPlayer mp) {
		Log.d(TAG, " position = " + mPosition + " video onCompletion");
		// 如果是第一次播放结束，判断是否需要显示引导页
//		mp.reset();

		// 如果是第一次播放完成，检查是否要显示引导。
		if (mLoopNumber++ == 0){
			if (CacheBean.getInstance().getInt(getActivity().getApplicationContext(), CacheKeyConstants.KEY_FIRST_VIDEO, CacheKeyConstants.VALUE_FAIL) != CacheKeyConstants.VALUE_SUCC){
				CacheBean.getInstance().putInt(getActivity(), CacheKeyConstants.KEY_FIRST_VIDEO, CacheKeyConstants.VALUE_SUCC);
				mScrollTips = mFirstVideoTip.inflate();
				mScrollTips.setOnClickListener(mTipClickListener);
				mScrollTips.setVisibility(View.VISIBLE);
				mScrollTips.startAnimation(new AlphaAnimation(0.0f, 1.0f));
			}

			if (mIsShowMsgTips){
				setMsgTipsVisiable(true);
			}

			if (mIsShowDynTips){
				mDynTips.setVisibility(View.VISIBLE);
			}
		}
		mp.start();
	}

	@Override
	protected void onPrepared(MediaPlayer mp) {
		Log.d(TAG, " position = " + mPosition + " mediaplay already prepare , isDestroyed = " + isPlayDestroyed + ">>>>  ");
		impressionClick = true;
		setTextureSize(mp);
		int height = mp.getVideoHeight();
		System.err.println(height);
		if (mIsShowDialog){
			mp.seekTo(1);
			showVideoComment();
		} else if (isPlayDestroyed){
			mp.seekTo(1);
		} else {
			startVideoPlayer();
		}
		if (videoLoadView != null)
			hideProgress();
	}

	@Override
	protected boolean onError(MediaPlayer mp, int what, int extra) {
		if (MediaPlayer.MEDIA_ERROR_UNKNOWN == what && -2147483648 == extra){
			mIsVideoError = true;
		}
		return false;
	}

	@Override
	protected void downloadVideoStart() {
		mIsVideoLoading = true;
		mBtnDynamic.setEnabled(false);
		showProgress();
	}

	@Override
	protected void downloadVideoProgress(int progress) {
		videoLoadView.refreshProgress(progress);
	}

	@Override
	protected void downloadVideoFailure() {
		videoLoadView.showReloadStyle();
		mBtnDynamic.setEnabled(false);
		mTextLayout.setVisibility(View.INVISIBLE);
		mIsVideoLoading = false;
	}

	@Override
	protected void downLoadVideoSuccess() {
		videoLoadView.setLoadSuccess(true);
		mBtnDynamic.setEnabled(true);
		mTextLayout.setVisibility(View.VISIBLE);
		mIsVideoLoading = false;
	}

	public void startVideoPlayer(){
		Log.d(TAG, " position = " + mPosition + " mediaplay start to play >>>>  ");
		if (startPlay()){
			if (!mVideo.isLook()){
				mVideo.setLook(true);
				// 发送一个请求表示此视频已经被看过了
				Volley.getRequestQueue().add(new MessageRequest(new LookMessage(mVideo.getVideoid())));
			}

			if(mLoopNumber<1){
				//统计视频播放行为
				Map<String, String> map = new HashMap<String, String>();
				map.put("videoid", mVideo.getVideoid());
				map.put("length", mVideo.getTimes()+"");
				AnalyticUtil.sendAnalytisEvent(getActivity().getApplicationContext(),AnalyticUtil.Event_Play_Video, map);
			}
		}
	}

	public void pauseVideoPlayer(){
		pausePlay();
	}

	public void resumeVideoPlayer(){
		startPlay();
	}

	/**
	 * 弹出评论窗口
	 * @return true 成功弹出, false 成功失败
	 */
	public boolean showVideoComment(){
		if (isPause || mIsShowDialog || mIsViewPagerScroll || mIsVideoError || mIsShowShareWindow )
			return false;
		mIsShowDialog = true;
		mDynamicalLayout.hideWindow();
		mElementLayout.setVisibility(View.INVISIBLE);
		pauseVideoPlayer();
		// 禁止ViewPager左右滑动，防止因为评论框弹出慢，导致切换到其他页面再弹出。
		if (getActivity() instanceof VideoViewPagerActivity){
			((VideoViewPagerActivity)getActivity()).setViewPagerEnabled(false);
		} else if(getActivity() instanceof MyVideoViewPagerActivity){
			((MyVideoViewPagerActivity)getActivity()).setViewPagerEnabled(false);
		}
		if (mScrollTips != null) {
			mScrollTips.setVisibility(View.GONE);
		}
		if (mIsShowDynTips){
			mDynTips.setVisibility(View.GONE);
		}
		if (mIsShowMsgTips){
			setMsgTipsVisiable(false);
		}
		
		Intent intent = new Intent(getActivity(), VideoCommentActivity.class);
		startActivityForResult(setIntentData(intent), COMMENT_REQUEST_CODE);
		getActivity().overridePendingTransition(R.anim.activity_bottom_in, 0);
		return true;
	}
	
	/**
	 * 
	 */
	protected void setMsgTipsVisiable(boolean show) {
		if(show)
			mMsgTips.setVisibility(View.VISIBLE);
		else 
			mMsgTips.setVisibility(View.GONE);
	}
	
	private Intent setIntentData(Intent intent){
		if (mVideoCommentList != null && mVideoCommentTextList != null){
			CommentInfo info = new CommentInfo();
			info.setAllowComment(mAllowcomment);
			info.setSelectComment(mSelectComment);
			info.setCommentList(mVideoCommentList);
			info.setCommentTextList(mVideoCommentTextList);
			info.setVideo(mVideo);
			intent.putExtra(VideoCommentActivity.INTENT_INFO, info);
		}
		return intent;
	}

	public void resume(){
		Log.d(TAG, " position = " + mPosition + " resume");
		if (surface != null)
			mMediaPlayer = initVideoPlayer(surface);
	}
	
	public void isViewPagerScroll(boolean scroll){
		mIsViewPagerScroll = scroll;
		mIsShowShareWindow = false;
	}
	
	protected TextureView.SurfaceTextureListener surfaceTextureListener = new TextureView.SurfaceTextureListener() {
		
		@Override
		public void onSurfaceTextureUpdated(SurfaceTexture surface) {}
		
		@Override
		public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width,
				int height) {
			Log.d(TAG,  " position = "+mPosition+" onSurfaceTextureSizeChanged");
		}
		
		@Override
		public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
			Log.d(TAG,  " position = "+mPosition+" onSurfaceTextureDestroyed");
			if (isPageOnCurrent()){
				releaseVideoPlayer();
				isPlayDestroyed = true;
			}
			return false;
		}
		
		@Override
		public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width,
				int height) {
			Log.d(TAG, " position = "+mPosition+"  onSurfaceTextureAvailable");
			surface = surfaceTexture;
			if(isPageOnCurrent())
				mMediaPlayer = initVideoPlayer(surface);
			else
				Log.d(TAG, " isPageOnCurrent = "+isPageOnCurrent());
		}
	};
	
	/**
	 * @param mp
	 */
	@Override
	protected void setTextureSize(MediaPlayer mp) {
		float width = mp.getVideoWidth();
		float height = mp.getVideoHeight();
		final int screenHeight =AppContext.deviceInfo.getScreenHeight();
		final int screenWidth =AppContext.deviceInfo.getScreenWidth();

		FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) mTextureView.getLayoutParams();
		if(screenHeight-height>screenWidth-width){
			//高度适配
			params.width = (int) (width*screenHeight/height);
			params.height = screenHeight;
			mDynamicalLayout.setDynamicalHeightScale(screenHeight/height);
			mDynamicalLayout.setDynamicalWidthScale(screenHeight/height);
		}else{
			//宽度适配
			params.width = screenWidth ;
			params.height = (int) (height * screenWidth/ width);
			mDynamicalLayout.setDynamicalHeightScale(screenWidth/width);
			mDynamicalLayout.setDynamicalWidthScale(screenWidth/width);
		}
		params.gravity = Gravity.CENTER;
		mTextureView.setLayoutParams(params);
		mTextureView.requestLayout();
	}
	
	private void showProgress(){
		if (mType==NATIVE) return;
		if (videoLoadView == null){
			inflateVideoLoadView();
		}
		videoLoadView.startProgressRefresh();
		mBtnMessage.setEnabled(false);
		mTextureView.setEnabled(false);
		mBtnMore.setEnabled(false);
	}

	private void inflateVideoLoadView(){
		videoLoadView = (VideoLoadView) mVideoLoadStub.inflate();
		videoLoadView.setOnRetryListener(mRetryListener);
		if(!StringUtils.isEmpty(mVideo.getBigpreviewurl())){
			videoLoadView.setBigPreviewImage(mVideo.getBigpreviewurl());
		}
	}
	
	private OnClickListener mTipClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			v.setVisibility(View.GONE);
		}
	};
	
	private void hideProgress(){
		if (videoLoadView == null){
			inflateVideoLoadView();
		}
		videoLoadView.loadSuccess();
		mBtnMessage.setEnabled(true);
		mTextureView.setEnabled(true);
		mBtnMore.setEnabled(true);
	}

	private Dialog mMoreDialog;
	
	@Override
	public void onClick(View v) {
		if (isAnimationing) return;
		switch (v.getId()) {
		case R.id.video_play_textureview:
			mIsShowShareWindow = false;
			Log.d(TAG, "impressionClick : " +impressionClick + " mIsShowDialog: "+mIsShowDialog);
			// 弹幕窗口被打开时，点击屏幕是关闭弹幕窗口
			if (mDynamicalLayout.isShow()){
				mDynamicalLayout.hideWindow();
				break;
			}
			// 弹幕窗口没被打开时，点击屏幕是弹出评论窗口
			if(mMediaPlayer != null && impressionClick && !mIsShowDialog){
				if (mMediaPlayer.isPlaying()){
					showVideoComment();
				} else {
					startVideoPlayer();
				}
			}
			break;
			
		case R.id.activity_play_story_msg:
			if (!Utils.isLogin(getActivity(), true) || Utils.isMy(mVideo.getUid())){
				return;
			}
			Intent intent = new Intent(getActivity(), AnonymousChatActivity.class);
			intent.putExtra(AnonymousChatActivity.INTENT_VIDEO, mVideo);
			startActivityForResult(intent, CHAT_REQUEST_CODE);
			getActivity().overridePendingTransition(R.anim.activity_right_in, 0);
			pauseVideoPlayer();
			if (mIsShowMsgTips){
				mIsShowMsgTips = false;
				setMsgTipsVisiable(false);
				// 设置为不是第一次点击消息按钮
				CacheBean.getInstance().putInt(getActivity().getApplicationContext(), CacheKeyConstants.KEY_FIRST_MESSAGE, CacheKeyConstants.NOT_FIRST);
			}
			break;
			
		case R.id.video_play_close:
			getActivity().finish();
			break;
			
		case R.id.video_play_more:
			if (mVideoCommentList == null){
				new SuperToast(getActivity(), getString(R.string.view_load_video_network_fail));
				break;
			}
			pauseVideoPlayer();
			if (mMoreDialog == null)
				mMoreDialog = MoreDialog.show(getActivity(), mMoreListener, mVideo.getUid());
			else
				mMoreDialog.show();
			if (mDynamicalLayout.isShow())
				mDynamicalLayout.hideWindow();
			break;
			
		case R.id.video_play_comments_num:
			mIsShowShareWindow = false;
			showVideoComment();
			break;
			
		case R.id.activity_video_play_fire:
			if (!Utils.isLogin(getActivity(), true))
				break;
			if (mDynamicalLayout.isShow()){
				mDynamicalLayout.hideWindow();
			} else {
				mDynamicalLayout.showWindow();
			}
			break;

		default:
			break;
		}
	}
	
	private MoreDialog.OnClickListener mMoreListener = new MoreDialog.OnClickListener(){
		
		@Override
		public void onClick(TextView tv, MoreDialog.Type type) {
			switch (type) {
			case SHARE:
				mIsShowDialog = true;
				shareVideo();
				break;

			case REPORT:
				mIsShowDialog = true;
				showReportDialog();
				break;
				
			case CLEAR_DYNAMIC:
				mIsShowDialog = false;
				Volley.getRequestQueue().add(new MessageRequest(new ClearDynamicImgMessage(mVideo.getVideoid())));
				mDynamicalLayout.removeMyDynamic();
				break;
				
			case SHIELD_DYNAMIC:
				mIsShowDialog = false;
				if (mDynamicalLayout.isShiel()){
					mDynamicalLayout.shielDynamic(false);
					tv.setText(R.string.dialog_video_more_off_dynamic);
				} else {
					mDynamicalLayout.shielDynamic(true);
					tv.setText(R.string.dialog_video_more_on_dynamic);
				}
				break;
				
			default:
				break;
			}
		}

		@Override
		public void onDismiss() {
			if (!mIsShowDialog)
				startVideoPlayer();
		}

		@Override
		public void onShow() {
			mIsShowDialog = true;
		}

		@Override
		public void onCancel() {
			mIsShowDialog = false;
		}
		
	};
	
	private void shareVideo(){
		pauseVideoPlayer();
		Bitmap bitmap = Utils.createShareBitmap(mTextLayout, mTextureView);
		if (bitmap != null){
			ShareSDKUtil.shareVideo(getActivity(), mVideo, bitmap, shareDialogListener);
		}
	}
	
	/**
	 * 分享窗口的监听
	 */
	private ShareSDKUtil.ShareWindowListener shareDialogListener = new ShareSDKUtil.ShareWindowListener() {
		

		@Override
		public void onCancel() {
			mIsShowDialog = false;
			mBtnMore.setEnabled(true);
			startVideoPlayer();
		}

		@Override
		public void onClick() {
			mIsShowDialog = false;
			mBtnMore.setEnabled(true);
			mIsShowShareWindow = true;
		}

		@Override
		public void show() {
			
		}
	};
	
	private  void showReportDialog() {
		ReportOptionView.showDialog(getActivity(), mReportBtnListener);
	}
	
	/**
	 * 举报按钮的监听
	 */
	private ReportListener mReportBtnListener = new VideoReportListener() {
		
		@Override
		public void onClick(String content) {
			Volley.getRequestQueue().add(
					new MessageRequest(new ReportMessage(mVideo.getVideoid(), getReportType(getActivity(), content), TYPE_VIDEO), mReportRequestListener));
		}

		@Override
		public void onDismiss(DialogInterface dialog) {
			mIsShowDialog = false;
			startVideoPlayer();
		}
	};

	private MessageRequest.Listener<ProcessResult> mReportRequestListener = new MessageRequest.Listener<ProcessResult>(){

		@Override
		public void success(ProcessResult result) {
			new SuperToast(getActivity(), getString(R.string.fragment_report_succ));
		}
	};
	
	@Override
	public void onResume() {
		isPause = false;
		if (mIsShowDialog){
			pauseVideoPlayer();
		} else {
			startVideoPlayer();
		}
		super.onResume();
	}
	
	@Override
	public void onPause() {
		isPause = true;
		pauseVideoPlayer();
		Intent intent = new Intent();
		intent.putExtra(RESULT_INTENT_POSITION, mPosition);
		intent.putExtra(RESULT_INTENT_LIKENUM, mVideo.getLikenum());
		intent.putExtra(RESULT_INTENT_IS_PLAY, mVideo.isLook());
		getActivity().setResult(Activity.RESULT_OK, intent);
		super.onPause();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	private MessageRequest.Listener<GetVideoResult> mVideoInfoResultListener = new MessageRequest.Listener<GetVideoResult>() {

		@Override
		public void success(GetVideoResult result) {
			// 设置评论列表和找出被选中的那个评论
			mVideoCommentList = result.getList();
			for (Commenttags tag : AppContext.getSysCode().getCommenttags()){
				if (tag.getCommentid().equals(result.getCommentid())){
					mSelectComment = tag.getContent();
					break;
				}
			}
			mVideo.setLike(result.isLike());
			mVideo.setLikenum(result.getLikenum());
			mVideo.setUid(result.getUid());
			mVideo.setCity(result.getCity());
			mVideo.setLooknum(result.getLooknum());
			mVideo.setDescription(result.getDescription());
			mVideo.setUploadTime(result.getCreatetime());
			mVideo.setTimes(result.getTimes());
			mDynamicalLayout.setDynamicImgs(result.getDynamicimgs());
			refreshVideoInfo(mVideo);
			mVideoCommentTextList = result.getCommentTexts();
			mAllowcomment = result.getAllowcomment();
			mCommentsNum.setText(getCommentsNum(result.getCommentnum()));
			if (isShowMsgBtn()){
				mBtnMessage.setVisibility(View.VISIBLE);
			} else {
				mBtnMessage.setVisibility(View.INVISIBLE);
			}
			if (getActivity()!=null)
				getActivity().sendBroadcast(setIntentData(new Intent(Constants.ACTION_COMMENT)));
		}

		@Override
		public void failure(FailureResult result) {
			if (result.getSubCode() == Constants.SERVICE_ERROR_CODE){
				SuperToast.show(getActivity().getApplicationContext(), result.getSubDes());
				getActivity().finish();
				return;
			}
		}

		@Override
		public void finish(){
			if (getActivity()!=null)
				getActivity().sendBroadcast(setIntentData(new Intent(Constants.ACTION_COMMENT)));
		}

	};

	private String getCommentsNum(int num){
		if (num > 9999){
			return "9999+";
		} else {
			return String.valueOf(num);
		}
	}
	
	private void refreshVideoInfo(Video video){
		mBtnDynamic.setEnabled(!mIsVideoLoading);
		mViews.setText(String.valueOf(video.getLooknum()));
		mDate.setText(DateUtils.getVideoCreateTimeStr(video.getUploadTime() * 1000));
		mHour.setText(DateUtils.formatTimesToHour(video.getUploadTime() * 1000));
		mLocal.setText(video.getCity());
		mHeat.setText(String.valueOf(video.getLikenum()));
		mDescription.setText(video.getDescription());
		if (mTextLayout.getAlpha() == 0){
			mTextLayout.animate().alpha(1.0f).setDuration(300);
		}
		mLocal.setVisibility(View.VISIBLE);
		mDescriptDivider.setVisibility(View.VISIBLE);
	}
	
	@Override
	public void onDestroyView() {
		isDestroyedView = true;
		super.onDestroyView();
	}

	public ImageView getmBtnMessage() {
		return mBtnMessage;
	}

	public void setmBtnMessage(ImageView mBtnMessage) {
		this.mBtnMessage = mBtnMessage;
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (COMMENT_REQUEST_CODE == requestCode){
			commentActivityResult(resultCode, data);
			isPlayDestroyed = false;
		} else if(CHAT_REQUEST_CODE == requestCode){
			// 从私信页面返回后让视频重新加载后直接播放，不做暂停
			isPlayDestroyed = false;
		}
		// 评论页返回来后恢复ViewPager左右滑动
		if (getActivity() instanceof VideoViewPagerActivity){
			((VideoViewPagerActivity)getActivity()).setViewPagerEnabled(true);
		} else if(getActivity() instanceof MyVideoViewPagerActivity){
			((MyVideoViewPagerActivity)getActivity()).setViewPagerEnabled(true);
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	private void commentActivityResult(int resultCode, Intent data){
		if (RESULT_COMMENT_OK == resultCode){
			// 在评论页按后退键返回的
			setData(data);
			refreshVideoInfo(mVideo);
			// 开始播放视频
			mIsShowDialog = false;
			startVideoPlayer();
		} else if (VideoCommentActivity.RESULT_COMMENT_ERROR == resultCode){
			// 在评论页出现错误后返回的
			mIsShowDialog = false;
			startVideoPlayer();
		}
		// 如果有消息按钮的引导，则要把引导图重新显示出来
		if (mIsShowMsgTips){
			setMsgTipsVisiable(true);
		}
		// 如果有表情弹幕按钮的引导，则要把引导图重新显示出来
		if (mIsShowDynTips){
			mDynTips.setVisibility(View.VISIBLE);
		}
		mElementLayout.setVisibility(View.VISIBLE);
	}
	
	private void setData(Intent data){
		CommentInfo info = data.getParcelableExtra(VideoCommentActivity.RESULT_INFO);
		Video video = info.getVideo();
		mSelectComment = info.getSelectComment();
		mVideoCommentList = info.getCommentList();
		mVideoCommentTextList = info.getCommentTextList();
		mCommentsNum.setText(getCommentsNum(Utils.commentCount(mVideoCommentList, mVideoCommentTextList)));
		mVideo.setLike(video.isLike());
		mVideo.setLikenum(video.getLikenum());
	}
	
	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
	}

	@Override
	protected void onVideoTime(long time, long totalTime) {
		mDynamicalLayout.showDynamicImg((int)time);
	}

	private OnRetryListener mRetryListener = new OnRetryListener() {
		
		@Override
		public void onRetry() {
			if(isPageOnCurrent()) loadPlaySource();
			if(StringUtils.isEmpty(mDescription.getText().toString())){
				sendGetVideoTask();
				return;
			}
			if(loadVideoDetailFailure){
				sendGetVideoTask();
				return;
			}
		}
	};
	
	/**
	 * 表情弹幕窗口的监听器
	 */
	private OnDynamicImgListener mDynamicImgListener = new OnDynamicImgListener() {
		
		// 放置表情弹幕
		@Override
		public void onDynamicImg(String id, double x, double y, double playTime) {
			SendDynamicImgMessage msg = new SendDynamicImgMessage();
			msg.setImgid(id);
			msg.setVideoid(mVideo.getVideoid());
			msg.setPlaytime(playTime);
			msg.setX(x);
			msg.setY(y);

			//统计弹幕行为
			Map<String, String> map = new HashMap<String, String>();
			map.put("videoid", msg.getVideoid());
			AnalyticUtil.sendAnalytisEvent(getActivity().getApplicationContext(), AnalyticUtil.Event_Dynamic_Image, map);

			Volley.getRequestQueue().add(new MessageRequest(msg));
			
			// 如果表情弹幕的引导图正在显示，则关闭掉。
			if (mExpressionTips.getVisibility() == View.VISIBLE){
				mExpressionTips.animate().alpha(0).setDuration(400);
			}
		}

		// 显示表情弹幕窗口
		@Override
		public void onShow() {
			if (mIsShowDynTips){
				// 关掉表情弹幕引导图
				mIsShowDynTips = false;
				mDynTips.setVisibility(View.INVISIBLE);
				mExpressionTips.setVisibility(View.VISIBLE);
				ObjectAnimator.ofFloat(mExpressionTips, "translationY", mExpressionTips.getY()-mExpressionTips.getHeight(), mExpressionTips.getY()).start();
				// 设置为不是点击表情弹幕按钮
				CacheBean.getInstance().putInt(getActivity().getApplicationContext(), CacheKeyConstants.KEY_FIRST_DYNAMIC_WINDOW, CacheKeyConstants.NOT_FIRST);
			}
			mBtnDynamic.animate().scaleX(0).scaleY(0);
		}

		@Override
		public void onHide() {
			if (!mDynamicalLayout.isSelectDynamicImg())
				mExpressionTips.setVisibility(View.INVISIBLE);
			mBtnDynamic.animate().scaleX(1).scaleY(1);
		}

		// 第一次点击表情弹幕
		@Override
		public void firstClick() {
			mExpressionTips.setVisibility(View.VISIBLE);
			mExpressionTips.setText(R.string.fragment_send_dynamic_tips2);
		}

	};
	
	public Video getVideo(){
		return mVideo;
	}

}