/**
 * 
 */
package com.bloomlife.videoapp.adapter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bloomlife.android.common.util.UiUtils;
import com.bloomlife.videoapp.R;
import com.bloomlife.videoapp.activity.RealNameChatActivity;
import com.bloomlife.videoapp.app.DbHelper;
import com.bloomlife.videoapp.common.util.EasemobchatDateUtils;
import com.bloomlife.videoapp.common.util.ImageLoaderUtils;
import com.bloomlife.videoapp.common.util.Utils;
import com.bloomlife.videoapp.dialog.DialogUtils;
import com.bloomlife.videoapp.manager.ChatAudioFileManager;
import com.bloomlife.videoapp.model.Account;
import com.bloomlife.videoapp.model.ChatBean;
import com.bloomlife.videoapp.model.Video;
import com.bloomlife.videoapp.view.AudioPlayAnimView;
import com.easemob.chat.EMMessage;
import com.easemob.util.DateUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

/**
 *  满足我们针对不同内容有不同会话的页面的	私信聊天的adapter
 * 
 * @author <a href="mailto:xiai.fei@gmail.com">xiai_fei</a>  
 *
 * @date 2014-12-18  下午12:22:01
 */
public class RealNameChatAdapter extends BaseAdapter{
	
	private static final int SENDING_TIME_OUT = 60*1000;

	protected Activity mActivity;

	private List<ChatBean> mDataList;
	private ChatAdapterCallback mCallback;
	private int mSex;

	private String mMyIcon;
	private String mOtherName;
	private String mOtherIcon;

	private ImageLoader mImageLoader;
	private DisplayImageOptions mOptions;
	
	private ChatAudioFileManager mAudioDownload;
	private SensorManager mSensorManager;
	private AudioManager mAudioManager;
	
	private int mMaxAudioViewWidth;
	
	private boolean mIsLastItem = false;
	private boolean mIsInitItem = true;

	public RealNameChatAdapter(Activity activity, List<ChatBean> dataList, ChatAdapterCallback callback){
		this.mActivity = activity;
		this.mCallback = callback;
		mImageLoader = ImageLoader.getInstance();
		mOptions = ImageLoaderUtils.getMyVideoPreviewImageOption();
		mAudioDownload = ChatAudioFileManager.getInstance();
		mSensorManager = (SensorManager) activity.getSystemService(Context.SENSOR_SERVICE);
		mAudioManager = (AudioManager) activity.getSystemService(Context.AUDIO_SERVICE);
		mMaxAudioViewWidth = UiUtils.dip2px(activity, 120);

		Account account = Utils.getAccount(activity);
		if (account != null)
			mMyIcon = TextUtils.isEmpty(account.getSdcardUserIcon()) ? account.getUserIcon() : account.getSdcardUserIcon();
		setDataList(dataList);
	}

	public void refreshSexInfo(){
		for (int i= mDataList.size()-1; i>0; i--){
			if (mDataList.get(i).getDirect() == EMMessage.Direct.RECEIVE.ordinal()){
				this.mSex = mDataList.get(i).getSex();
				return;
			}
		}
	}

	public void refreshOtherUserInfo(){
		ChatBean chatBean = getLastOtherChat();
//		if (chatBean != null){
//			setCity(chatBean.getCity());
//		}
		if (chatBean != null){
			mOtherName = chatBean.getUserName();
			mOtherIcon = chatBean.getUserIcon();
		}
	}
	
	public ChatBean getFirstChat(){
		if (getCount() == 0) 
			return null;
		for (ChatBean chat: getDataList())
			if (chat.getViewType() == ChatBean.VIEW_TYPE_CHAT)
				return chat;
		return null;
	}

	public ChatBean getLastOtherChat(){
		List<ChatBean> dataList = getDataList();
		if (Utils.isEmpty(dataList))
			return null;
		for (int i=dataList.size()-1; i>=0; i--){
			ChatBean chatBean = dataList.get(i);
			if (chatBean.getDirect() == EMMessage.Direct.RECEIVE.ordinal() && chatBean.getViewType() == ChatBean.VIEW_TYPE_CHAT){
				return dataList.get(i);
			}
		}
		return null;
	}

	public void setOtherUserInfo(String name, String icon){
		mOtherName = name;
		mOtherIcon = icon;
	}
	
	public void setSex(int sex){
		this.mSex = sex;
	}

	public int getSex(){
		return this.mSex;
	}
	
	@Override
	public int getCount() {
		return  mDataList ==null?0: mDataList.size();
	}

	@Override
	public ChatBean getItem(int i) {
		return mDataList.get(i);
	}

	@Override
	public long getItemId(int i) {
		return i;
	}

	@Override
	public int getItemViewType(int position) {
		return getDataList().get(position).getViewType();
	}

	@Override
	public int getViewTypeCount() {
		return 2;
	}

	@Override
	public View getView(int position, View view, ViewGroup viewgroup) {
		switch (getItemViewType(position)){
			// 提示语的视图
			case ChatBean.VIEW_TYPE_TIPS:
				return setTipsView(position, view, viewgroup, getDataList().get(position));

			// 精选集对话的视图
			default:
			case ChatBean.VIEW_TYPE_CHAT:
				return setStoryView(position, view, viewgroup, getDataList().get(position));
		}
	}

	/**
	 * 匿名世界的视图，没有用户名和头像
	 * @param position
	 * @param view
	 * @param viewgroup
	 * @param chat
	 * @return
	 */
	protected View setChatView(int position, View view, ViewGroup viewgroup, ChatBean chat){
		ChatView chatView = null;
		if (view == null){
			view = LayoutInflater.from(mActivity).inflate(R.layout.item_chat, viewgroup, false);
			chatView = createChatView(view);
			view.setTag(chatView);
		} else {
			chatView = (ChatView) view.getTag();
		}
		// 设置对话信息到视图
		setChatViewChatInfo(position, chatView, chat);
		return view;
	}

	/**
	 * 精选集对话的视图，有用户名和头像
	 * @param position
	 * @param view
	 * @param viewgroup
	 * @param chat
	 * @return
	 */
	protected View setStoryView(int position, View view, ViewGroup viewgroup, ChatBean chat){
		ChatView chatView = null;
		if (view == null){
			view = LayoutInflater.from(mActivity).inflate(R.layout.item_user_chat, viewgroup, false);
			chatView = createStoryChatView(view);
			view.setTag(chatView);
		} else {
			chatView = (ChatView) view.getTag();
		}
		// 设置用户名和头像
		chatView.otherUserName.setText(mOtherName);
		loadImage(chatView.other, mOtherIcon);
		loadImage((ImageView) chatView.me, mMyIcon);
		// 设置性别标签
		Drawable genderIcon = mActivity.getResources().getDrawable(
				getSex() == Video.MALE ? R.drawable.icon_user_info_male : R.drawable.icon_user_info_female);
		genderIcon.setBounds(0, 0, genderIcon.getMinimumWidth(), genderIcon.getMinimumHeight());
		chatView.otherUserName.setCompoundDrawables(null, null, genderIcon, null);
		// 设置对话信息到视图
		setChatViewChatInfo(position, chatView, chat);
		return view;
	}

	protected View setTipsView(int position, View view, ViewGroup viewgroup, ChatBean chat) {
		TipsView tipsView = null;
		if (view == null) {
			view = LayoutInflater.from(mActivity).inflate(R.layout.item_chat_picture_tips, viewgroup, false);
			tipsView = new TipsView();
			tipsView.content = (TextView) view.findViewById(R.id.item_picture_tips);
			view.setTag(tipsView);
		} else {
			tipsView = (TipsView) view.getTag();
		}
		String tipsText = getTipsText(chat.getWaitStatus());
		if (TextUtils.isEmpty(tipsText)){
			tipsView.content.setVisibility(View.GONE);
		} else {
			tipsView.content.setVisibility(View.VISIBLE);
			tipsView.content.setText(tipsText);
		}
		return view;
	}

	protected String getTipsText(int status){
		return "";
	}

	private class TipsView{
		private TextView content;
	}

	protected ChatView createChatView(View layout){
		ChatView chatView = new ChatView();
		findChatChildView(chatView, layout);
		return chatView;
	}

	protected ChatView createStoryChatView(View layout){
		ChatView chatView = new ChatView();
		findStoryChatChildView(chatView, layout);
		return chatView;
	}

	protected void findStoryChatChildView(ChatView chatView, View view){
		// 除用户名的View外，其他组件都和匿名世界的相同，所以都用同一个方法加载
		findChatChildView(chatView, view);
		chatView.otherUserName = (TextView) view.findViewById(R.id.other_name);
	}

	protected void findChatChildView(ChatView chatView, View view){
		chatView.myGroup 		  = (ViewGroup) view.findViewById(R.id.my_layout);
		chatView.mycontent 		  = (TextView) view.findViewById(R.id.mycontent);
		chatView.myImage 		  = (ImageView) view.findViewById(R.id.myimage);
		chatView.otherGroup 	  = (ViewGroup) view.findViewById(R.id.other_layout);
		chatView.othercontent 	  = (TextView) view.findViewById(R.id.othercontent);
		chatView.otherImage 	  = (ImageView) view.findViewById(R.id.otherimage);
		chatView.otherProgressBar = (ProgressBar) view.findViewById(R.id.other_progress);
		chatView.myProgressBar 	  = (ProgressBar) view.findViewById(R.id.my_progress);
		chatView.sendfailure 	  = (ImageView) view.findViewById(R.id.sendfailure);
		chatView.me 			  = view.findViewById(R.id.me);
		chatView.other			= (ImageView) view.findViewById(R.id.other);
		chatView.time 			= (TextView) view.findViewById(R.id.time);
		chatView.myVDuration    = (TextView) view.findViewById(R.id.my_voice_duration);
		chatView.otherVDuration = (TextView) view.findViewById(R.id.other_voice_duration);
		chatView.myVoice		= (AudioPlayAnimView) view.findViewById(R.id.myspeak);
		chatView.otherVoice		= (AudioPlayAnimView) view.findViewById(R.id.otherspeak);

		chatView.otherProgressBar.setVisibility(View.GONE);
		chatView.myImage.setOnClickListener(mImageListener);
		chatView.otherImage.setOnClickListener(mImageListener);
		chatView.myVoice.setAnimImageArray(R.drawable.my_speak1, R.drawable.my_speak2, R.drawable.my_speak3);
		chatView.otherVoice.setAnimImageArray(R.drawable.other_speak1, R.drawable.other_speak2, R.drawable.other_speak3);
	}

	private void setChatViewChatInfo(int position, ChatView chatView, ChatBean chat){
		chatView.myImage.setTag(R.id.item, chat);
		chatView.otherImage.setTag(R.id.item, chat);
		mIsLastItem = (position + 1 == getCount());
		if (chat.getDirect() == EMMessage.Direct.RECEIVE.ordinal()){
			chatView.setOther(chat);
		} else {
			chatView.setMy(chat);
		}
		setTime(position, chatView, chat);
		setSendStatusStyle(chatView, chat.getSendSatus(), chat);
	}
	
	private void setSendStatusStyle(ChatView chatView ,int sendStatus, ChatBean chatBean){
		switch (sendStatus) {
		case ChatBean.SEND_STATUS_FAILURE:
			setSendFailure(chatView, chatBean);
			break;
		case ChatBean.SEND_STATUS_SENDING:
			/* 如果距离发送事件已经超过发送的超时时间，那么就默认为失败   **/
			if((new Date().getTime()-chatBean.getCreatetime().getTime())>SENDING_TIME_OUT && chatBean.getReSendStatus() != ChatBean.RESEND_STATUS_RESEND){
				chatBean.setSendSatus(ChatBean.SEND_STATUS_FAILURE);
				DbHelper.updateChat(mActivity, chatBean);
				setSendFailure(chatView, chatBean);
				return;
			}
			chatView.myProgressBar.setVisibility(View.VISIBLE);
			chatView.sendfailure.setVisibility(View.INVISIBLE);
			break;
		case ChatBean.SEND_STATUS_SUC:
			chatView.myProgressBar.setVisibility(View.INVISIBLE);
			chatView.sendfailure.setVisibility(View.INVISIBLE);
			break;

		default:
			break;
		}
	}


	/**
	 * @param chatView
	 * @param chatBean
	 */
	private void setSendFailure(ChatView chatView, ChatBean chatBean) {
		chatView.sendfailure.setVisibility(View.VISIBLE);
		chatView.myProgressBar.setVisibility(View.INVISIBLE);
		chatView.sendfailure.setTag(chatBean);
		chatView.sendfailure.setOnClickListener(resendListener);
	}
	
	private OnClickListener resendListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			ChatBean chatBean = (ChatBean) v.getTag();
			chatBean.setReSendStatus(ChatBean.RESEND_STATUS_RESEND);
			((RealNameChatActivity) mActivity).resendEchatMessage(chatBean);
		}
	};


	protected void loadImage(ImageView iv, String url){
		mImageLoader.displayImage(url, iv, mOptions);
	}

	/**
	 * @param position
	 * @param chatView
	 * @param chatBean
	 */
	protected void setTime(int position, ChatView chatView, ChatBean chatBean) {
		// 两条消息时间离得如果稍长，或者是第一个时，显示时间
		if (position == 0 || !DateUtils.isCloseEnough(chatBean.getCreatetime().getTime(), getDataList().get(position - 1).getCreatetime().getTime())) {
			chatView.time.setText(EasemobchatDateUtils.getTimestampString(new Date(chatBean.getCreatetime().getTime())));
			chatView.time.setVisibility(View.VISIBLE);
		} else {
			chatView.time.setVisibility(View.GONE);
		}
	}
	
	
	public synchronized List<ChatBean> getDataList() {
		if(mDataList ==null) mDataList = new ArrayList<>();
		return mDataList;
	}

	public void setDataList(List<ChatBean> dataList) {
		this.mDataList = dataList;
		refreshOtherUserInfo();
	}

	public void addDataList(int location, List<ChatBean> dataList){
		if (this.mDataList == null)
			return;
		this.mDataList.addAll(location, dataList);
		refreshOtherUserInfo();
	}

	protected ChatBean getCityTipsChatBean(){
		ChatBean ct = new ChatBean();
		ct.setViewType(ChatBean.VIEW_TYPE_TIPS);
		ct.setWaitStatus(ChatBean.TIPS_CITY);
		ct.setCreatetime(new Date(System.currentTimeMillis()));
		return ct;
	}
	
	@Override
	public void notifyDataSetChanged() {
		super.notifyDataSetChanged();
	}
	
	private OnClickListener mImageListener = new OnClickListener(){

		@Override
		public void onClick(View v) {
			if (mCallback != null){
				mCallback.setPicture(((ChatBean) v.getTag(R.id.item)).getImagePath());
			}
		}
		
	};
	
	class ChatView {
		View me;
		TextView othercontent, mycontent;
		TextView time, myVDuration, otherVDuration, otherUserName;
		ProgressBar myProgressBar, otherProgressBar;
		ImageView sendfailure, myImage, otherImage, other;
		AudioPlayAnimView myVoice, otherVoice;
		ViewGroup myGroup, otherGroup;
		
		void setOther(ChatBean chat){
			String content = chat.getContent();
			String imagePath = chat.getThumbnailUrl();
			String voicePath = chat.getVoicePath();
			// 重置所有状态
			mycontent.setVisibility(View.GONE);
			me.setVisibility(View.GONE);
			myGroup.setVisibility(View.GONE);
			myVoice.setVisibility(View.GONE);
			myVDuration.setVisibility(View.GONE);

			othercontent.setVisibility(View.GONE);
			otherImage.setVisibility(View.GONE);
			otherVoice.setVisibility(View.GONE);
			otherVDuration.setVisibility(View.GONE);
			otherProgressBar.setVisibility(View.GONE);

			otherGroup.setVisibility(View.VISIBLE);
			otherGroup.setOnClickListener(null);
			otherGroup.setTag(R.id.item_voice, null);
			otherGroup.getLayoutParams().width = ViewGroup.LayoutParams.WRAP_CONTENT;
			other.setVisibility(View.VISIBLE);
			mImageLoader.cancelDisplayTask(otherImage);
			mImageLoader.cancelDisplayTask(myImage);
			if (otherUserName != null){
				otherUserName.setVisibility(View.VISIBLE);
				other.setOnClickListener(mOtherUserIconClickListener);
				other.setTag(chat);
			} else {
				setOtherIcon(chat);
			}
			// 语音消息
			if(!TextUtils.isEmpty(voicePath)){
				otherVoice.setVisibility(View.VISIBLE);
				otherVDuration.setVisibility(View.VISIBLE);
				otherVDuration.setText(chat.getVoiceDuration()+"s");
				mAudioDownload.download(mActivity, voicePath, new AudioLoadListener(otherGroup, otherProgressBar, otherVDuration));
				otherGroup.setTag(R.id.item_voice, voicePath);
				otherGroup.setTag(R.id.item_voice_anim, otherVoice);
				otherGroup.setOnClickListener(mAudioPlayClickListener);
				setAudioLayoutWidth(otherGroup.getLayoutParams(), chat.getVoiceDuration());
				return;
			} 
			// 图片消息
			if(!TextUtils.isEmpty(imagePath)){
				otherImage.setVisibility(View.VISIBLE);
				otherImage.setImageBitmap(null);
				// 先让图片的大小显示出来
				mImageLoader.displayImage(imagePath, otherImage, mOptions, new PictureLoadingListener(this.otherProgressBar));
				return;
			} 
			// 文本消息
			if(!TextUtils.isEmpty(content)) {
				othercontent.setText(content);	//对方
				othercontent.setVisibility(View.VISIBLE);
			}

		}

		void setOtherIcon(ChatBean chat){
			other.setImageResource(mSex == Video.MALE ? R.drawable.icon_type_male : R.drawable.icon_type_female);
		}
		
		void setMy(ChatBean chat){
			String content = chat.getContent();
			String imagePath = chat.getThumbnailUrl();
			String voicePath = chat.getVoicePath();
			// 重置所有状态
			othercontent.setVisibility(View.GONE);
			otherGroup.setVisibility(View.GONE);
			other.setVisibility(View.GONE);
			otherVoice.setVisibility(View.GONE);
			otherVDuration.setVisibility(View.GONE);
			mycontent.setVisibility(View.GONE);
			myImage.setVisibility(View.GONE);
			myVoice.setVisibility(View.GONE);
			myVDuration.setVisibility(View.GONE);
			myGroup.setOnClickListener(null);
			myGroup.setVisibility(View.VISIBLE);
			myGroup.getLayoutParams().width = ViewGroup.LayoutParams.WRAP_CONTENT;
			me.setVisibility(View.VISIBLE);
			mImageLoader.cancelDisplayTask(otherImage);
			mImageLoader.cancelDisplayTask(myImage);
			setMeIcon(chat);
			if (otherUserName != null){
				otherUserName.setVisibility(View.INVISIBLE);
			}
			// 语音消息
			if (!TextUtils.isEmpty(voicePath)){
				myVoice.setVisibility(View.VISIBLE);
				if (chat.getSendSatus() == ChatBean.SEND_STATUS_SUC){
					myVDuration.setVisibility(View.VISIBLE);
					myVDuration.setText(chat.getVoiceDuration()+"s");
					myGroup.setTag(R.id.item_voice, voicePath);
					myGroup.setTag(R.id.item_voice_anim, myVoice);
					myGroup.setOnClickListener(mAudioPlayClickListener);
					setAudioLayoutWidth(myGroup.getLayoutParams(), chat.getVoiceDuration());
				}
				return;
			} 
			// 图片消息
			if (!TextUtils.isEmpty(imagePath)){
				myImage.setVisibility(View.VISIBLE);
				myImage.setImageBitmap(null);
				mImageLoader.displayImage(imagePath, myImage, mOptions, mMyPictureLoadingListener);
				return;
			} 
			// 文本消息
			if (!TextUtils.isEmpty(content)){
				mycontent.setVisibility(View.VISIBLE);
				mycontent.setText(content); 	//我
			}
		}

		void setMeIcon(ChatBean chat){

		}

		private OnClickListener mOtherUserIconClickListener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				DialogUtils.showUserInfo(mActivity, (ChatBean)v.getTag());
			}
		};
		
	}
	
	private void setAudioLayoutWidth(LayoutParams params, int duration){
		// 根据语音长度来设置的语音播放视图长度
		if (duration < 2)
			params.width = LayoutParams.WRAP_CONTENT;
		else if(duration < 12)
			params.width = mMaxAudioViewWidth / 2 + mMaxAudioViewWidth / 24 * duration;
		else
			params.width = mMaxAudioViewWidth;
	}

	/**
	 * 我的图片读取监听器
	 */
	private ImageLoadingListener mMyPictureLoadingListener = new PictureLoadingListener();
	
	class PictureLoadingListener implements ImageLoadingListener{
		
		private ProgressBar mProgressBar;
		
		public PictureLoadingListener(){
			
		}
		
		public PictureLoadingListener(ProgressBar progressbar){
			mProgressBar = progressbar;
		}

		@Override
		public void onLoadingStarted(String imageUri, View view) {
			view.setEnabled(false);
			if (mProgressBar != null)
				mProgressBar.setVisibility(View.VISIBLE);
		}

		@Override
		public void onLoadingFailed(String imageUri, View view,
				FailReason failReason) {
			if (mProgressBar != null)
				mProgressBar.setVisibility(View.GONE);
		}

		@Override
		public void onLoadingComplete(String imageUri, View view,
				Bitmap loadedImage) {
			view.setEnabled(true);
			if (mProgressBar != null)
				mProgressBar.setVisibility(View.GONE);
			if (mCallback != null && mIsLastItem && mIsInitItem){
				mIsInitItem = false;
				mCallback.loadingLastImageComplete();
			}
		}

		@Override
		public void onLoadingCancelled(String imageUri, View view) {
			if (mProgressBar != null)
				mProgressBar.setVisibility(View.GONE);
		}
		
	}
	
	static class AudioLoadListener implements ChatAudioFileManager.DownloadListener{
		
		private View mLayout;
		private View mProgressBar;
		private View mTextView;

		AudioLoadListener(View layout, View progressBar, View textView){
			mLayout = layout;
			mProgressBar = progressBar;
			mTextView = textView;
		}
		
		@Override
		public void start() {
			mLayout.setEnabled(false);
			mProgressBar.setVisibility(View.VISIBLE);
			mTextView.setVisibility(View.GONE);
		}

		@Override
		public void complete() {
			mLayout.setEnabled(true);
			mProgressBar.setVisibility(View.GONE);
			if (mLayout.getTag(R.id.item_voice) != null)
				mTextView.setVisibility(View.VISIBLE);
		}

		@Override
		public void fail() {
			mProgressBar.setVisibility(View.GONE);
		}

	}
	
	private View.OnClickListener mAudioPlayClickListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			String soundPath = (String)v.getTag(R.id.item_voice);
			// 如果此时正在播发其他音频，停止播放。
			if(mMPlayer.isPlaying()){
				stopPlayingSound();
				// 如果刚才播发的与被点击的是同一个语音，则不播放本次被点击的语音
				if (soundPath.equals(mPlaySoundPath)){
					return;
				}
			}
			mPlaySoundPath = soundPath;
			playSound(soundPath, (AudioPlayAnimView)v.getTag(R.id.item_voice_anim));
		}
	};
	
	private MediaPlayer mMPlayer;
	private AudioPlayAnimView mApaView;
	private String mPlaySoundPath;
	private boolean mPrepare;
	private Sensor mSensor;
	
	public void playSound(String path, final AudioPlayAnimView animView){
		if (mPrepare) return;
		mPrepare = true;
		File file = mAudioDownload.getFile(mActivity, path);
		if (file == null) return;
		mApaView = animView;
		try {
			mMPlayer.reset();
			mMPlayer.setOnCompletionListener(completionListener);
			mMPlayer.setOnPreparedListener(mPlayPreparedListener);
			mMPlayer.setOnErrorListener(mPlayErrorListener);
			mMPlayer.setDataSource(file.getAbsolutePath());
			mMPlayer.prepareAsync();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	public void stopPlayingSound(){
		mAudioManager.setStreamMute(AudioManager.STREAM_MUSIC, false);

		mApaView.stopAudioPlayAnimation();
		mMPlayer.stop();
		mMPlayer.reset();
	}
	
	private OnCompletionListener completionListener = new OnCompletionListener() {
		
		@Override
		public void onCompletion(MediaPlayer mp) {
			// 播放结束时停止动画
			mApaView.stopAudioPlayAnimation();
		}
	};
	
	private OnPreparedListener mPlayPreparedListener = new OnPreparedListener() {
		
		@Override
		public void onPrepared(MediaPlayer mp) {
			mAudioManager.setStreamMute(AudioManager.STREAM_MUSIC, true);
			mApaView.startAudioPlayAnimation();
			mMPlayer.start();
			mPrepare = false;
		}
	};
	
	private OnErrorListener mPlayErrorListener = new OnErrorListener() {
		
		@Override
		public boolean onError(MediaPlayer mp, int what, int extra) {
			releaseMediaPlayer();
			initMediaPlayer();
			return false;
		}
	};
	
	private SensorEventListener mSensorEventListener = new SensorEventListener(){

		@Override
		public void onSensorChanged(SensorEvent event) {
			Log.e("SensorEventListener", "values "+event.values[0]+" range "+mSensor.getMaximumRange());
			if (isSoundPlaying()) return;
			if (event.values[0] == mSensor.getMaximumRange()){
				mAudioManager.setMode(AudioManager.MODE_NORMAL);
				mAudioManager.setSpeakerphoneOn(true);
			} else {
				mAudioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
				mAudioManager.setSpeakerphoneOn(false);
			}
		}

		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
			// TODO Auto-generated method stub
			
		}
		
	};
	
	public boolean isSoundPlaying(){
		return mMPlayer.isPlaying();
	}
	
	public void initMediaPlayer(){
		mMPlayer = new MediaPlayer();
		mMPlayer.setAudioStreamType(AudioManager.STREAM_VOICE_CALL);
		// 注册距离传感器监听
		mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
		mSensorManager.registerListener(mSensorEventListener, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
	}
	
	public void releaseMediaPlayer(){
		mMPlayer.reset();
		mMPlayer.release();
		// 取消距离传感器监听
		mSensorManager.unregisterListener(mSensorEventListener);
	}
	
	public interface ChatAdapterCallback{
		void setPicture(String url);
		void loadingLastImageComplete();
	}

}
