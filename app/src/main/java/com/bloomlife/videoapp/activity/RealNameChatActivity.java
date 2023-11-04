/**
 * 
 */
package com.bloomlife.videoapp.activity;

import static com.bloomlife.videoapp.app.MyHXSDKHelper.ATTRIBUTE_SEX;
import static com.bloomlife.videoapp.app.MyHXSDKHelper.ATTRIBUTE_VERSION;
import static com.bloomlife.videoapp.app.MyHXSDKHelper.ATTRIBUTE_VIDEO_ID;
import static com.bloomlife.videoapp.app.MyHXSDKHelper.ATTRIBUTE_CITY;
import static com.bloomlife.videoapp.app.MyHXSDKHelper.ATTRIBUTE_USER_NAME;
import static com.bloomlife.videoapp.app.MyHXSDKHelper.ATTRIBUTE_USER_ICON;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.tsz.afinal.annotation.view.ViewInject;
import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bloomlife.android.bean.CacheBean;
import com.bloomlife.android.common.CacheKeyConstants;
import com.bloomlife.android.common.util.StringUtils;
import com.bloomlife.android.common.util.UiHelper;
import com.bloomlife.android.framework.MyInjectActivity;
import com.bloomlife.android.log.Logger;
import com.bloomlife.android.view.AlterDialog;
import com.bloomlife.android.view.SoftKeyboardLayout;
import com.bloomlife.android.view.SoftKeyboardLayout.OnSoftKeyboardListener;
import com.bloomlife.videoapp.R;
import com.bloomlife.videoapp.activity.fragment.MessageListFragment;
import com.bloomlife.videoapp.adapter.RealNameChatAdapter;
import com.bloomlife.videoapp.adapter.ChatSendPageAdapter;
import com.bloomlife.videoapp.adapter.ChatSendPageAdapter.InstantiateItemListener;
import com.bloomlife.videoapp.app.AppContext;
import com.bloomlife.videoapp.app.Database;
import com.bloomlife.videoapp.app.DbHelper;
import com.bloomlife.videoapp.app.MyHXSDKHelper;
import com.bloomlife.videoapp.common.util.ImageLoaderUtils;
import com.bloomlife.videoapp.common.util.SystemUtils;
import com.bloomlife.videoapp.common.util.Utils;
import com.bloomlife.videoapp.manager.BackgroundManager;
import com.bloomlife.videoapp.model.Account;
import com.bloomlife.videoapp.model.ChatBean;
import com.bloomlife.videoapp.model.ConversationMessage;
import com.bloomlife.videoapp.view.AudioRecorderView;
import com.bloomlife.videoapp.view.AudioRecorderView.OnRecordListener;
import com.bloomlife.videoapp.view.ChatAudioRecorderController;
import com.bloomlife.videoapp.view.ChatAudioRecorderController.ControllerListener;
import com.bloomlife.videoapp.view.ChatSendToolController;
import com.bloomlife.videoapp.view.ChatSendToolController.SendViewListener;
import com.bloomlife.videoapp.view.GlobalProgressBar;
import com.hyphenate.EMCallBack;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMChatManager;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMImageMessageBody;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMMessageBody;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.chat.EMVoiceMessageBody;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;

import androidx.viewpager.widget.ViewPager;
import emojicon.EmojiconGridFragment;
import emojicon.EmojiconsFragment;
import emojicon.emoji.Emojicon;

/**
 * 由于每个视频都是不同会话需求，使用我们数据结构处理的会话详情页面
 * 
 * @author <a href="mailto:xiai.fei@gmail.com">xiai_fei</a>
 * @parameter INTENT_USERNAME String 从消息页面进来时，传对话方的用户名
 * @parameter INTENT_FROM_NOTIFICATION boolean 是从通知栏跳转进来
 * @parameter INTENT_USER_INFO UserInfo 从精选集视频播放页面(StoryPlayActivity)进来需要传用户信息
 * @date 2014-12-17 下午6:40:56
 */
public class RealNameChatActivity extends ChatBaseActivity implements OnClickListener,
		EmojiconGridFragment.OnEmojiconClickedListener,
		EmojiconsFragment.OnEmojiconBackspaceClickedListener {

	private static final String TAG = "ChatActivity";

	public static final String INTENT_USERNAME = "intent_username";
	public static final String INTENT_FROM_NOTIFICATION = "intent_from_notification";

	public static final int REQUEST_CODE_PICTURE = 1;
	public static final String REAL_NAME_CHAT_ID = "realId";

	@ViewInject(id = R.id.listview)
	protected ListView listView;

	@ViewInject(id = R.id.back, click = ViewInject.DEFAULT)
	protected ImageView back;

	@ViewInject(id = R.id.mainlayout, click = ViewInject.DEFAULT)
	protected SoftKeyboardLayout mMainLayout;

	/**文本和语音滑动切换的viewpager**/
	@ViewInject(id = R.id.send_viewpager)
	protected ViewPager mSendViewPager;
	
	@ViewInject(id = R.id.more, click = ViewInject.DEFAULT)
	protected ImageView more;
	
	@ViewInject(id = R.id.activity_chat_background)
	protected ImageView mBackground;

	@ViewInject(id = R.id.title_icon)
	protected TextView mTitle;

	@ViewInject(id = R.id.emojicons)
	protected View emojiFragment;

	@ViewInject(id = R.id.activity_chat_picture_layout, click = ViewInject.DEFAULT)
	protected ViewGroup mPictureLayout;

	@ViewInject(id = R.id.activity_chat_picture_image)
	protected ImageView mPictureImage;

	@ViewInject(id = R.id.activity_chat_picture_progress)
	protected GlobalProgressBar mPictureLoadProgress;

	@ViewInject(id = R.id.activity_chat_audio_recorder_view)
	protected AudioRecorderView mAudioRecorderView;

	private RealNameChatAdapter adapter;
	private GestureDetector mGestureDetector;

	private ImageLoader mImageLoader;

	private boolean isloading;

	private final int pagesize = 20;
	private int page = 0;
	private boolean haveMoreData = true;
	private Account mAccount;

	private Handler handler = new Handler();
	protected String toChatUsername;
	protected ChatBean mChatBean; // 用于记录当前详情是否会信内容的beanl
	protected String mChatId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_chat);
		MyInjectActivity.initInjectedView(this);
		initContentView();
	}

	protected void initContentView(){
		initChatData();
		boolean fromNotification = getIntent().getExtras().getBoolean(INTENT_FROM_NOTIFICATION, false);
		if (EMClient.getInstance().isLoggedIn() && fromNotification) {
			if (mChatBean == null){
				ChatBean cb = new ChatBean();
				cb.setFromUser(toChatUsername);
				updateConversationMsg(cb, mChatId);
			} else {
				updateConversationMsg(mChatBean, mChatId);
			}
		}
		// 监听键盘的打开和关闭
		mMainLayout.setOnSoftKeyboardListener(softKeyBoardListener);
		mAccount = Utils.getAccount(this);
		mGestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener(){
			@Override
			public boolean onSingleTapUp(MotionEvent e) {
				hideInputFragment();
				return super.onSingleTapUp(e);
			}
		});
		initUi();
	}

	protected void initChatData(){
		toChatUsername = getIntent().getExtras().getString(INTENT_USERNAME);
		mChatId = REAL_NAME_CHAT_ID;
	}
	
	/**
	 * 初始化环信相关。加载会话，注册相关的广播等。
	 */
	private void initHx() {
		EMClient.getInstance().chatManager().loadAllConversations();
		EMClient.getInstance().chatManager().addMessageListener(mEMMessageListener);
	}

	private void initUi() {
		listView.setOnScrollListener(onScrollListener);
		emojiFragment.setVisibility(View.GONE);
		mSendViewPager.setAdapter(new ChatSendPageAdapter(this, mViewPagerInitItemListener));
		mAudioRecorderView.setOnTrashListener(mRecordListener);
		mImageLoader = ImageLoader.getInstance();
		Bitmap bg = BackgroundManager.getInstance().getBackgroundBitmap();
		if (bg != null)
			mBackground.setImageBitmap(bg);
		else
			mBackground.setImageResource(R.drawable.background_splashlayout);
	}

	private void scrollListViewToEnd() {
		Log.d(TAG, "scroll to end ");
		if (adapter == null && Utils.isEmpty(adapter.getDataList()))
			return;
		handler.postDelayed(new Runnable() {

			@Override
			public void run() {
				listView.setSelection(adapter.getDataList().size());
			}
		}, 100);
	}

	/**
	 * 	收起键盘和emoji表情
	 */
	private void hideInputFragment() {
		if (mSendView != null) {
			mSendView.showKeyBoardIcon();
		}
		if (mMainLayout.isSoftKeyBoardVisible()) {
			// 软键盘在显示状态则关闭键盘
			hideSoftInput();
		} else
			emojiFragment.setVisibility(View.GONE); // 软键盘在关闭状态则显示表情
	}

	public void showSoftInput() {
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.toggleSoftInput(0, InputMethodManager.RESULT_SHOWN);
	}

	public void hideSoftInput() {
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		if (mSendView != null)
			imm.hideSoftInputFromWindow(mSendView.getEditWindowToken(), 0);
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		mGestureDetector.onTouchEvent(ev);
		return super.dispatchTouchEvent(ev);
	}
	

/**TODO **********************************************************   发送消息相关    **************************************************************/
	/**
	 * 发送文本消息
	 * 
	 * @param content
	 *            message content
	 */
	private void sendText(String content) {
		if (content.length() > 0) {
			final EMMessage message = EMMessage.createSendMessage(EMMessage.Type.TXT);
			// 如果是群聊，设置chattype,默认是单聊
			EMTextMessageBody txtBody = new EMTextMessageBody(content);
			setMessageBody(message, txtBody);
		}
	}

	private void sendImage(File file) {
		final EMMessage message = EMMessage.createSendMessage(EMMessage.Type.IMAGE);
		// 如果是群聊，设置chattype,默认是单聊
		EMImageMessageBody imageBody = new EMImageMessageBody(file);
		imageBody.setSendOriginalImage(true);
		// 设置消息body
		setMessageBody(message, imageBody);
	}

	private void sendVoice(File voice, int duration) {
		// 时长小于1不发送
		if (duration < 1){
			voice.delete();
			return;
		}
		final EMMessage message = EMMessage .createSendMessage(EMMessage.Type.VOICE);
		EMVoiceMessageBody body = new EMVoiceMessageBody(voice, duration);
		// 设置消息body
		setMessageBody(message, body);
	}

	protected void setMessageBody(EMMessage message, EMMessageBody body) {
		// 设置消息body
		message.addBody(body);
		setAttributes(message);
		// 设置要发给谁,用户username或者群聊groupid
		message.setTo(toChatUsername);
		if (mSendView != null) {
			mSendView.clearInputEditText();
		}
		// 通知adapter有消息变动，adapter会根据加入的这条message显示消息和调用sdk的发送方法
		ChatBean chatBean = saveChatAndUpdateListView(RealNameChatActivity.this, message, true, null);
		sendEchatMessage(message, chatBean);
	}

	protected void setAttributes(EMMessage message){
		message.setAttribute(ATTRIBUTE_VIDEO_ID, CacheBean.getInstance().getLoginUserId());
		message.setAttribute(ATTRIBUTE_VERSION, SystemUtils.getVersionCode(this));
		message.setAttribute(ATTRIBUTE_SEX, AppContext.getSysCode().getSex());
		message.setAttribute(ATTRIBUTE_CITY, CacheBean.getInstance().getString(this, CacheKeyConstants.LOCATION_CITY));
		message.setAttribute(ATTRIBUTE_USER_NAME, mAccount.getUserName());
		message.setAttribute(ATTRIBUTE_USER_ICON, mAccount.getUserIcon());
	}

	protected void resetAttributes(EMMessage message, ChatBean chatBean){
		setAttributes(message);
	}

	/**
	 * 重新发送
	 * @param chatBean
	 */
	public void resendEchatMessage(final ChatBean chatBean) {
		if (chatBean.getSendSatus()!=ChatBean.SEND_STATUS_FAILURE) {
			return;
		}
		chatBean.setSendSatus(ChatBean.SEND_STATUS_SENDING);
		EMMessage message = null;
		// 判断要重新发送的是文本消息还是图片消息
		if (!TextUtils.isEmpty(chatBean.getImagePath())) {
			message = EMMessage.createSendMessage(EMMessage.Type.IMAGE);
			EMImageMessageBody imageBody = new EMImageMessageBody(new File(chatBean.getImagePath().replace("file://", "")));
			imageBody.setSendOriginalImage(true);
			message.addBody(imageBody);
		} else if(!TextUtils.isEmpty(chatBean.getVoicePath())){
			message = EMMessage.createSendMessage(EMMessage.Type.VOICE);
			EMVoiceMessageBody voiceBody = new EMVoiceMessageBody(new File(chatBean.getVoicePath()), chatBean.getVoiceDuration());
			message.addBody(voiceBody);
		} else {
			message = EMMessage.createSendMessage(EMMessage.Type.TXT);
			// 如果是群聊，设置chattype,默认是单聊
			EMTextMessageBody txtBody = new EMTextMessageBody(chatBean.getContent());
			// 设置消息body
			message.addBody(txtBody);
		}
		
		//设置消息属性
		resetAttributes(message, chatBean);

		message.setTo(toChatUsername);
		
		DbHelper.updateChat(getApplicationContext(), chatBean);
		refreahChatListView();

		sendEchatMessage(message, chatBean);
	}
	
	protected void sendEchatMessage(final EMMessage message, final ChatBean chatBean) {
		if (!EMClient.getInstance().isConnected()) {
			doSendFailure(chatBean);
			return;
		}
		// 发送消息
		message.setMessageStatusCallback(new EMCallBack() {
			@Override
			public void onSuccess() {
				onSendChatSuccess(chatBean);
			}

			@Override
			public void onError(int arg0, String arg1) {
				Log.i(TAG, "send chat onError " + arg0 + " message " + arg1);
				doSendFailure(chatBean);
			}
		});
		EMClient.getInstance().chatManager().sendMessage(message);
	}

	protected void onSendChatSuccess(final ChatBean chatBean){
		Log.i(TAG, "send message to " + toChatUsername + " success ");
		chatBean.setSendSatus(ChatBean.SEND_STATUS_SUC);
		chatBean.setReSendStatus(ChatBean.RESEND_STATUS_SEND);
		Database.updateChatBean(getApplicationContext(), mChatBean);
		handler.post(refreshListViewRunnable);
	}
	
	private void doSendFailure(final ChatBean chatBean){
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				chatBean.setSendSatus(ChatBean.SEND_STATUS_FAILURE);
				DbHelper.updateChat(getApplicationContext(), chatBean);
				adapter.notifyDataSetChanged();
				listView.setSelection(adapter.getCount() - 1);
			}
		},500);  //演示是为了能看到菊花的发送效果
	}
	
	private  void  refreahChatListView(){
		adapter.notifyDataSetChanged();
		listView.setSelection(adapter.getCount() - 1);
	}
	
	private Runnable refreshListViewRunnable = new Runnable() {
		
		@Override
		public void run() {
			refreahChatListView();
		}
	};
	
	/**
	 * 
	 * @param context
	 * @param message
	 * @param send
	 * @param sendstatus
	 * @return
	 */
	protected ChatBean saveChatAndUpdateListView(Context context, EMMessage message, boolean send, Integer sendstatus) {
		mChatBean = new ChatBean(context, message);
		if (sendstatus != null)  mChatBean.setSendSatus(sendstatus);
		else if(send) mChatBean.setSendSatus(ChatBean.SEND_STATUS_SENDING);
		if (send){
			mChatBean.setDirect(EMMessage.Direct.SEND.ordinal());
		}
		mChatBean.setStatus(ChatBean.STATUS_READ);
		Database.saveChat(context, mChatBean);
		mChatBean.setId(Database.queryLastInsertId(getApplicationContext(), ChatBean.class));
		adapter.getDataList().add(adapter.getDataList().size(), mChatBean);
		// 如果是对方发来的信息，更新对方的性别和用户信息
		if (mChatBean.getDirect() == EMMessage.Direct.RECEIVE.ordinal()) {
			adapter.setSex(mChatBean.getSex());
			adapter.refreshOtherUserInfo();
		}
		// 通知adapter有新消息，更新ui
		refreahChatListView();
		return mChatBean;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) { // 禁用了back键返回
		// setChatResult();
			finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void setChatResult() {
		if (mChatBean != null) {
			Intent intent = new Intent();
			intent.putExtra(MessageListFragment.INTENT_NEW_CHAT, mChatBean);
			updateConversationMsg(mChatBean, mChatId);
			setResult(RESULT_OK, intent);
			mChatBean = null;
		}
	}

	
	/**
	 * 更新会话信息,标记为已读
	 */
	protected ConversationMessage updateConversationMsg(ChatBean chatBean, String chatId) {
		ConversationMessage oldMessage = DbHelper.readMessage(this, chatBean.getFromUser(), chatId);
		if (oldMessage == null) {
			oldMessage = new ConversationMessage();
			oldMessage.setByChatBean(chatBean);
			oldMessage.setStatus(ConversationMessage.STATUS_READ);
			setConversationMsgUserInfo(chatBean, oldMessage);
			DbHelper.saveMessage(this, oldMessage);
		} else {
			if (StringUtils.isNotEmpty(chatBean.getContent()))
				oldMessage.setContent(chatBean.getContent());
			oldMessage.setStatus(ConversationMessage.STATUS_READ);
			oldMessage.setUpdateTime(new Date());
			setConversationMsgUserInfo(chatBean, oldMessage);
			DbHelper.updateMessage(this, oldMessage);
		}
		return oldMessage;
	}

	private void setConversationMsgUserInfo(ChatBean chatBean, ConversationMessage oldMessage){
		if (!TextUtils.isEmpty(chatBean.getUserName()))
			oldMessage.setUserName(chatBean.getUserName());
		if (!TextUtils.isEmpty(chatBean.getUserIcon()))
			oldMessage.setUserIcon(chatBean.getUserIcon());
	}

	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		if (arg0 == REQUEST_CODE_PICTURE && arg1 == RESULT_OK) {
			String imagePath = arg2
					.getStringExtra(PictureActivity.RESULT_IMAGE_PATH);
			sendImage(new File(imagePath));
		}
		super.onActivityResult(arg0, arg1, arg2);
	}

	@Override
	public void onEmojiconBackspaceClicked(View v) {
		if (mSendView != null)
			EmojiconsFragment.backspace(mSendView.getEditText());
	}

	@Override
	public void onEmojiconClicked(Emojicon emojicon) {
		if (mSendView != null)
			EmojiconsFragment.input(mSendView.getEditText(), emojicon);
	}
	
	

/**TODO **********************************************************  语音发送相关    **************************************************************/

	protected ChatSendToolController mSendView;
	protected ChatAudioRecorderController mControllerView;

	private InstantiateItemListener mViewPagerInitItemListener = new InstantiateItemListener() {

		@Override
		public void createItem(View view) {
			if (view instanceof ChatSendToolController) {
				mSendView = (ChatSendToolController) view;
				mSendView.setSendViewListener(mSendViewListener);
			} else if (view instanceof ChatAudioRecorderController) {
				mControllerView = (ChatAudioRecorderController) view;
				mControllerView
						.setControllerListener(mAudioRecorderControllerListener);
			}
		}

		@Override
		public void destroyItem(View view) {
			if (view instanceof ChatSendToolController) {
				mSendView = null;
			} else if (view instanceof ChatAudioRecorderController) {
				mControllerView = null;
			}
		}

	};

	protected void onPicture(){
		startActivityForResult(new Intent(this, PictureActivity.class), REQUEST_CODE_PICTURE);
	}

	protected void onVoice(){
		if (adapter == null) return;
		// 如果这个时候用户在播放语音，则要停止掉。
		if (adapter.isSoundPlaying()){
			adapter.stopPlayingSound();
		}
		// 开始录制语音，同时在屏幕显示录制进度图
		mControllerView.setEnabled(true);
		mAudioRecorderView.start();
	}

	private SendViewListener mSendViewListener = new SendViewListener() {

		@Override
		public void onSend(String text) {
			hideSoftInput(); // 关闭键盘
			if (EMClient.getInstance().isLoggedIn()) {
				sendText(text);
			} else {
				UiHelper.showToast(RealNameChatActivity.this, getString(R.string.activity_chat_network_fail));
				MyHXSDKHelper.login(getApplicationContext());
			}
			emojiFragment.setVisibility(View.GONE); // 关闭表情
		}

		@Override
		public void onKeyboard() {
			showSoftInput(); // 显示软键盘
		}

		@Override
		public void onEmoji() {
			if (mMainLayout.isSoftKeyBoardVisible()) {
				// 软键盘在显示状态则关闭键盘
				hideSoftInput();
				handler.postDelayed(new Runnable() {

					@Override
					public void run() {
						emojiFragment.setVisibility(View.VISIBLE);
						scrollListViewToEnd();
					}
				}, 200);
			} else
				emojiFragment.setVisibility(View.VISIBLE); // 软键盘在关闭状态则显示表情
		}

		@Override
		public void onPicture() {
			RealNameChatActivity.this.onPicture();
		}
	};
	
	private ControllerListener mAudioRecorderControllerListener = new ControllerListener() {

		@Override
		public void stop() {
			// 录制停止后自动发送语音
			if (mAudioRecorderView.isStart()){
				File voiceFile = mAudioRecorderView.stop();
				sendVoice(voiceFile, (int) (mAudioRecorderView.getDuration() / 1000));
			}
		}

		@Override
		public void start() {
			onVoice();
		}

		@Override
		public void trash() {
			// 在屏幕显示删除图标
			mAudioRecorderView.stop();
			mAudioRecorderView.delete();
		}

		@Override
		public void status(STATE state) {
			switch (state) {
			case trash:
				mAudioRecorderView.showTrashIcon();
				break;
			case send:
				mAudioRecorderView.showProgress();
				break;
			default:
				break;
			}
		}
	};
	
	private OnRecordListener mRecordListener = new OnRecordListener(){

		@Override
		public void onTrash() {}

		/**
		 * 当录制时间达到最大限制后，自动发送
		 */
		@Override
		public void onSend(File file) {
			sendVoice(file, 60);
		}
	};
	
/**TODO **********************************************************   activity生命周期     **************************************************************/

	@Override
	protected void onResume() {
		super.onResume();
		Logger.v(TAG, "toChatUsername = " + toChatUsername);
		if (adapter == null) {
			List<ChatBean> chatList = Database.loadChat(this, toChatUsername, mChatId, 0, pagesize);
			if (EMClient.getInstance().isLoggedIn()) {
				initHx();
			}
			adapter = initChatList(chatList);
		}
		adapter.initMediaPlayer();
	}

	protected RealNameChatAdapter initChatList(List<ChatBean> chatList){
		if (chatList == null)
			chatList = new ArrayList<>();
		RealNameChatAdapter adapter = new RealNameChatAdapter(this, chatList, mPictureCallback);
		Log.d(TAG, "on Resume , set adapter ");
		listView.setAdapter(adapter);
		if (!chatList.isEmpty()) {
			DbHelper.updateUserVideoChatToRead(this, toChatUsername, mChatId);
			listView.setSelection(chatList.size());
			Log.d(TAG, "on Resume , setSection to Listview  ");
		}
		return adapter;
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		adapter.releaseMediaPlayer();
	}

	@Override 
	protected void onStop() {
		super.onStop();
		setChatResult();
	}

	@Override
	public void finish() {
		setChatResult();
		super.finish();
		overridePendingTransition(0, R.anim.activity_right_out);
	}

	

/** TODO ***********************************************************   滚动监听     **************************************************************/
	private OnScrollListener onScrollListener = new OnScrollListener() {

		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			switch (scrollState) {
				case OnScrollListener.SCROLL_STATE_IDLE:
					mImageLoader.resume();
					if (view.getFirstVisiblePosition() < 3 && !isloading
							&& haveMoreData) {
						// sdk初始化加载的聊天记录为20条，到顶时去db里获取更多
						page++;
						List<ChatBean> chatList = Database.loadChat(
								RealNameChatActivity.this, toChatUsername, mChatId, page * pagesize, pagesize);
						if (!Utils.isEmpty(chatList)) {
							adapter.addDataList(0, chatList);
							// 刷新ui
							adapter.notifyDataSetChanged();
							listView.setSelection(chatList.size());
							if (chatList.size() != pagesize)
								haveMoreData = false;
						} else {
							haveMoreData = false;
						}
						isloading = false;
					}
					break;
				case OnScrollListener.SCROLL_STATE_FLING:
				case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
					mImageLoader.pause();
					break;
			}
		}

		@Override
		public void onScroll(AbsListView view, int firstVisibleItem,
				int visibleItemCount, int totalItemCount) {
		}
	};

/** TODO ***********************************************************   监听器与点击事件     **************************************************************/
	@Override
	public void onClick(View v) {
		switch (v.getId()) {

			case R.id.more:
				if (MyHXSDKHelper.getBlackList().contains(toChatUsername)) {
					AlterDialog.showDialog(RealNameChatActivity.this, getString(R.string.activity_chat_remove_shield), this);
				} else
					AlterDialog.showDialog(RealNameChatActivity.this, getString(R.string.activity_chat_shield_user), this);
				break;

			case R.id.back:
				finish();
				break;
			case R.id.confirm:
				MyHXSDKHelper.maskOperation(toChatUsername);
				break;

			case R.id.activity_chat_picture_layout:
				AnimatorSet set = new AnimatorSet();
				set.playTogether(
						ObjectAnimator.ofFloat(mPictureLayout, "scaleX", 1, 0.5f),
						ObjectAnimator.ofFloat(mPictureLayout, "scaleY", 1, 0.5f),
						ObjectAnimator.ofFloat(mPictureLayout, "alpha", 1, 0));
				set.setDuration(300);
				set.addListener(mPictureHideListener);
				set.start();
				break;

			case R.id.mainlayout:
				hideInputFragment();
				break;

		}
	}
	
	private Animator.AnimatorListener mPictureShowListener = new Animator.AnimatorListener() {

		@Override
		public void onAnimationStart(Animator animation) {
			mPictureLayout.setVisibility(View.VISIBLE);
			mPictureLayout.setEnabled(false);
		}

		@Override
		public void onAnimationRepeat(Animator animation) { }

		@Override
		public void onAnimationEnd(Animator animation) {
			mPictureLayout.setEnabled(true);
		}

		@Override
		public void onAnimationCancel(Animator animation) { }
	};

	private ImageLoadingProgressListener mPictureLoadingProgressListener = new ImageLoadingProgressListener() {

		@Override
		public void onProgressUpdate(String imageUri, View view, int current,
				int total) {
			Log.d(TAG, " total " + total + " current " + current);
			if (current == total) {
				mPictureLoadProgress.setVisibility(View.INVISIBLE);
			} else {
				mPictureLoadProgress.setVisibility(View.VISIBLE);
				mPictureLoadProgress.setText((current / total) + "%");
			}
		}
	};

	
	private Animator.AnimatorListener mPictureHideListener = new Animator.AnimatorListener() {

		@Override
		public void onAnimationStart(Animator animation) {
			mPictureLayout.setEnabled(false);
		}

		@Override
		public void onAnimationRepeat(Animator animation) { }

		@Override
		public void onAnimationEnd(Animator animation) {
			mPictureLayout.setVisibility(View.INVISIBLE);
			mPictureLayout.setEnabled(true);
			mPictureImage.setImageBitmap(null);
		}

		@Override
		public void onAnimationCancel(Animator animation) { }
	};

	/**
	 * 监听软键盘的状态
	 */
	private OnSoftKeyboardListener softKeyBoardListener = new OnSoftKeyboardListener() {

		@Override
		public void onShown(int keyboardHeight) {
			if (emojiFragment.getVisibility() == View.VISIBLE) {
				emojiFragment.setVisibility(View.GONE);
			}
			scrollListViewToEnd();
		}

		@Override
		public void onHidden() { }

		@Override
		public void onShownEnd() {

		}

		@Override
		public void onHiddenEnd() {}
	};
	
	private OnItemClickListener onItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			hideInputFragment();
		}
	};
	
	protected RealNameChatAdapter.ChatAdapterCallback mPictureCallback = new RealNameChatAdapter.ChatAdapterCallback() {

		@Override
		public void setPicture(String url) {
			ImageLoader.getInstance().displayImage(url, mPictureImage,
					ImageLoaderUtils.getMyVideoPreviewImageOption(), null,
					mPictureLoadingProgressListener);
			AnimatorSet set = new AnimatorSet();
			set.playTogether(
					ObjectAnimator.ofFloat(mPictureLayout, "scaleX", 0.5f, 1),
					ObjectAnimator.ofFloat(mPictureLayout, "scaleY", 0.5f, 1),
					ObjectAnimator.ofFloat(mPictureLayout, "alpha", 0, 1));
			set.addListener(mPictureShowListener);
			set.setDuration(300);
			set.start();
		}

		@Override
		public void loadingLastImageComplete() {
			listView.setSelection(adapter.getCount() - 1);
		}
	};

	
/************************************************************   环信相关回调     **************************************************************/

	private EMMessageListener mEMMessageListener = new EMMessageListener() {

		@Override
		public void onMessageReceived(List<EMMessage> list) {
			for (EMMessage message: list) {
				String username = message.getFrom();
				EMClient.getInstance().chatManager().getConversation(username).markAllMessagesAsRead();
				receiveChat(message, username);
				saveChatAndUpdateListView(getApplicationContext(), message, false, null);
			}
		}

		@Override
		public void onMessageDelivered(List<EMMessage> messages) {
			for (EMMessage message:messages) {
				// 把message设为已读
				EMMessage msg = EMClient.getInstance().chatManager().getMessage(message.getMsgId());
				if (msg != null && !msg.isDelivered()) {
					msg.setDelivered(true);
					adapter.notifyDataSetChanged();
				}
			}
		}

		@Override
		public void onMessageRead(List<EMMessage> messages) {
			for (EMMessage message:messages) {
				EMMessage msg = EMClient.getInstance()
						.chatManager().getMessage(message.getMsgId());
				if (msg != null && !msg.isAcked()) {
					msg.setAcked(true);
					adapter.notifyDataSetChanged();
				}
			}
		}
	};

	protected void receiveChat(EMMessage message, String username){
		// 如果是群聊消息，获取到group id
		if (message.getChatType() == EMMessage.ChatType.GroupChat) {
			username = message.getTo();
		}
		if (!username.equals(toChatUsername) || !mChatId.equals(message.getStringAttribute(
				ATTRIBUTE_VIDEO_ID, ""))) {
			// 消息不是发给当前会话，return
			notifyNewMessage(message);
		}
	}

	@Override
	protected void onDestroy() {
		mBackground.setImageBitmap(null);
		EMClient.getInstance().chatManager().removeMessageListener(mEMMessageListener);
		super.onDestroy();
	}
}