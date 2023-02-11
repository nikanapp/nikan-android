/**
 * 
 */
package com.bloomlife.videoapp.app;

import static com.bloomlife.videoapp.common.CacheKeyConstants.KEY_HUANXIN_PWD;

import java.util.ArrayList;
import java.util.List;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;

import com.bloomlife.android.bean.CacheBean;
import com.bloomlife.android.common.util.StringUtils;
import com.bloomlife.android.common.util.Utils;
import com.bloomlife.videoapp.R;
import com.bloomlife.videoapp.activity.RealNameChatActivity;
import com.bloomlife.videoapp.activity.AnonymousChatActivity;
import com.bloomlife.videoapp.common.util.UIHelper;
import com.bloomlife.videoapp.model.ChatBean;
import com.bloomlife.videoapp.model.ConversationMessage;
import com.easemob.EMCallBack;
import com.easemob.chat.EMChat;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMChatOptions;
import com.easemob.chat.EMContactManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.easemob.chat.EMMessage.ChatType;
import com.easemob.chat.ImageMessageBody;
import com.easemob.chat.OnNotificationClickListener;
import com.easemob.chat.TextMessageBody;
import com.easemob.exceptions.EaseMobException;

/**
 * @author <a href="mailto:xiai.fei@gmail.com">xiai_fei</a>
 * 
 * @date 2014-12-18 下午3:57:05
 */
public class MyHXSDKHelper {

	private static final String TAG = "MyHXSDKHelper";

	private static List<String> blackList;

	public static final String ATTRIBUTE_VIDEO_ID = "videoId";
	public static final String ATTRIBUTE_PRVIEWURI = "prviewUri";
	public static final String ATTRIBUTE_VIDEO_URI = "videoUri";
	public static final String ATTRIBUTE_SEX = "sex";
	public static final String ATTRIBUTE_VERSION = "version";
	public static final String ATTRIBUTE_CITY = "city";
	public static final String ATTRIBUTE_USER_NAME = "userName";
	public static final String ATTRIBUTE_USER_ICON = "userIcon";
	
	private boolean isInit = false ;

	private static final MyHXSDKHelper instance = new MyHXSDKHelper();
	
	private EMChatOptions options;

	private MyHXSDKHelper() {
	}

	public static MyHXSDKHelper getInstance() {
		return instance;
	}

	private Context appContext;

	/**
	 * 必须要调用，否则其他方法会报错
	 * 
	 * 但环信的sdk只需要在主进程中初始化一次。如果注册多次将收不到信息
	 * 
	 * @param appContext
	 */
	public synchronized void init(final Context appContext) {
		if(isInit) return ;
		Log.d(TAG, " init huanxin ... ");
		this.appContext = appContext;

		// 初始化环信SDK
		EMChat.getInstance().init(appContext);

		// 获取到EMChatOptions对象
		options = EMChatManager.getInstance().getChatOptions();
		// 默认添加好友时，是不需要验证的，改成需要验证
		options.setAcceptInvitationAlways(true);
		// 设置收到消息是否有新消息通知，默认为true
		options.setNotificationEnable(PushService.isOpenPush(appContext));
		// 设置收到消息是否有声音提示，默认为true
		options.setNoticeBySound(false);
		// 设置收到消息是否震动 默认为true
		options.setNoticedByVibrate(true);
		// 设置语音消息播放是否设置为扬声器播放 默认为true
		options.setUseSpeaker(false);

		// options.setShowNotificationInBackgroud(true); //默认为true
		EMChat.getInstance().setDebugMode(true);
		
		EMChatManager.getInstance().getChatOptions().setShowNotificationInBackgroud(false); //禁止走notification，所有的都走广播让我们来处理

		// 设置notification点击listener
		options.setOnNotificationClickListener(new OnNotificationClickListener() {

			@Override
			public Intent onNotificationClick(EMMessage message) {
				Intent intent = new Intent(appContext, RealNameChatActivity.class);
				ChatType chatType = message.getChatType();
				if (chatType == ChatType.Chat) { // 单聊信息
					intent.putExtra(RealNameChatActivity.INTENT_USERNAME, message.getFrom());
				}
				return intent;
			}
		});
		registerHxMessageRecevier();
		
		// 通知sdk，UI 已经初始化完毕，注册了相应的receiver和listener, 可以接受broadcast了
		EMChat.getInstance().setAppInited();
		isInit = true ;
		Log.d(TAG, "  环信初始化成功 ！！！！！！！！！");
	}
	
	public void closePush(){
		options.setNotificationEnable(false);
	}
	
	public void openPush(){
		options.setNotificationEnable(true);
	}

	private void registerHxMessageRecevier() {
		IntentFilter intentFilter = new IntentFilter(EMChatManager.getInstance().getNewMessageBroadcastAction());
		intentFilter.setPriority(2); // 优先级第一点，让在会话页面的先处理
		appContext.registerReceiver(new HxMessageReceview(), intentFilter);
	}
	
	private class HxMessageReceview extends BroadcastReceiver {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			// 消息id
			String msgId = intent.getStringExtra("msgid");
			EMMessage message = EMChatManager.getInstance().getMessage(msgId);
			DbHelper.saveReciveChat(context.getApplicationContext(), new ChatBean(context, message), null);
			EMChatManager.getInstance().getConversation(intent.getStringExtra("from")).resetUnreadMsgCount();
			if (PushService.isOpenPush(context)){
				MyHXSDKHelper.getInstance().notifyNewMessage(message, context);
			}
		}

	}
	

	private static final int notifiId = 11;

	/**
	 * 当应用在前台时，如果当前消息不是属于当前会话，在状态栏提示一下 如果不需要，注释掉即可
	 * 
	 * @param message
	 */
	public  void notifyNewMessage(EMMessage message, Context context) {
		// 如果是设置了不提醒只显示数目的群组(这个是app里保存这个数据的，demo里不做判断)
		// 以及设置了setShowNotificationInbackgroup:false(设为false后，后台时sdk也发送广播)
//		if (!EasyUtils.isAppRunningForeground(context)) {
//			return;
//		}

		updateConverstaionMsgByLatestChat(context,message.getFrom(), message);
		EMChatManager.getInstance().getConversation(message.getFrom()).resetUnreadMsgCount();
		
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context).setSmallIcon(context.getApplicationInfo().icon).setWhen(System.currentTimeMillis()).setAutoCancel(true);

		String ticker = null;
		if (message.getBody() instanceof TextMessageBody){
			ticker = ((TextMessageBody) message.getBody()).getMessage();
			ticker = ticker.replaceAll("\\[.{2,3}\\]", context.getString(R.string.notification_emoj));
		} else if(message.getBody() instanceof ImageMessageBody){
			ticker = context.getString(R.string.notification_npicture);
		}
		// 设置状态栏提示
		mBuilder.setTicker(ticker);
		// TODO
		// 这里要做好，就要像微信一样，记录有多少个人，发了多少条消息。我们现在直接进入聊天页面是有问题的。当有三个人给我发消息时是分不清，以为只有一个
		// 尼玛，12点了，还在写，这部分重写了啊，天啊！！！！！
		mBuilder.setContentTitle(context.getString(R.string.notification_new_message));
		mBuilder.setContentText(ticker);

		// 必须设置pendingintent，否则在2.3的机器上会有bug
		Class clazz = TextUtils.isEmpty(message.getStringAttribute(ATTRIBUTE_USER_NAME, ""))
				? AnonymousChatActivity.class
				: RealNameChatActivity.class;
		Intent intent = new Intent(context, clazz);
		intent.putExtra(RealNameChatActivity.INTENT_USERNAME, message.getFrom());
		try {
			if (TextUtils.isEmpty(message.getStringAttribute(ATTRIBUTE_USER_NAME, "")))
				intent.putExtra(AnonymousChatActivity.INTENT_CHAT_ID, message.getStringAttribute(ATTRIBUTE_VIDEO_ID));
			intent.putExtra(RealNameChatActivity.INTENT_FROM_NOTIFICATION, true);
		} catch (EaseMobException e) {
			e.printStackTrace();
		}
		PendingIntent pendingIntent = PendingIntent.getActivity(context, notifiId, intent, Intent.FLAG_ACTIVITY_NEW_TASK);
		mBuilder.setContentIntent(pendingIntent);

		Notification notification = mBuilder.build();
		NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.notify(notifiId, notification);
	}

	/***
	 * 这里还没有对异常中断进行处理。比如处理到一半，宕机了了，会丢失数据的。
	 */
	private static void loadHXData(final Context context ) {

		AppContext.EXECUTOR_SERVICE.execute(new Runnable() {
			
			@Override
			public void run() {
				
				List<String> blackList= null ;
				try {
					setBlackList(EMContactManager.getInstance().getBlackListUsernames());
					blackList = EMContactManager.getInstance().getBlackListUsernamesFromServer();
				} catch (Exception e) {
					e.printStackTrace();
				}
				if(!Utils.isEmptyCollection(blackList))
					EMContactManager.getInstance().saveBlackList(blackList);  //环信其实会自动保存，但是为了保障起见，还是保存吧！！！
			
				EMChatManager.getInstance().loadAllConversations();
				
				//获取环信的离线未读消息。  ！！！ 将环信的数据结构转换成我们的消息的数据结构。
				List<String> unreadConversation = EMChatManager.getInstance().getConversationsUnread();
				if(Utils.isEmptyCollection(unreadConversation)) return ;
				for (String username : unreadConversation) {
					EMConversation conversation = EMChatManager.getInstance().getConversation(username);
					int unreadMsgCount = conversation.getUnreadMsgCount();
					Log.d(TAG, "  user : "+username+" has "+unreadMsgCount+" offline message  to process ");
					EMMessage latestMsg = conversation.getLastMessage();
					updateConverstaionMsgByLatestChat(context ,username, latestMsg);
					Log.d(TAG, "  update user : "+username+" conversation message success ");
					List<EMMessage> emMessagesList = conversation.loadMoreMsgFromDB(latestMsg.getMsgId(), unreadMsgCount);
					for (EMMessage emMessage : emMessagesList) {
						DbHelper.saveReciveChat(context.getApplicationContext(), new ChatBean(context, emMessage), null);
					}
					Log.d(TAG, " save  user : "+username+" offline chat bean success ");
					conversation.resetUnreadMsgCount();
				}
				//TODO 如果有未读消息，那么要在notification中提示用户啊。
			}
		});
		
		
		
	}

	/**
	 * 根据最新的聊天内容更新会话消息
	 * @param username
	 * @param latestMsg
	 */
	private static void updateConverstaionMsgByLatestChat(Context appContext, String username, EMMessage latestMsg) {
		if (username.equals(appContext.getString(R.string.custom_name))) return;
		String videoId = null;
		// 判断是否来自实名世界，因为实名世界是没有videoId的，所以要给一个特殊的id
		if (!TextUtils.isEmpty(latestMsg.getStringAttribute(ATTRIBUTE_USER_NAME, "")))
			videoId = RealNameChatActivity.REAL_NAME_CHAT_ID;
		else
			videoId = latestMsg.getStringAttribute(ATTRIBUTE_VIDEO_ID, "");
		ConversationMessage message = Database.readMessage(appContext, username, videoId);
		if(message == null){
			message = new ConversationMessage();
			message.setByEmMessage(appContext, latestMsg);
			Database.writeMessage(appContext, message);
		}else{
			if (latestMsg.getBody() instanceof TextMessageBody){
				String content = ((TextMessageBody)latestMsg.getBody()).getMessage();
				if(message.getUpdateTime().getTime()==latestMsg.getMsgTime()
						&&StringUtils.isNotEmpty(content)&&content.equals(message.getContent()))return ; //去重
			} else if(latestMsg.getBody() instanceof ImageMessageBody){
				String imagePath = UIHelper.getEMMessageImage((ImageMessageBody)latestMsg.getBody());
				if(message.getUpdateTime().getTime()==latestMsg.getMsgTime()
						&&StringUtils.isNotEmpty(imagePath)&&imagePath.equals(message.getImagePath()))return ; //去重
			}
			message.setByEmMessage(appContext, latestMsg);
			Database.updateMessage(appContext, message);
		}
	}

	/**
	 * 环信登陆
	 */
	public static  void login(final Context context) {
		CacheBean cacheBean = CacheBean.getInstance();

		if(EMChat.getInstance().isLoggedIn()){
			if (!cacheBean.getLoginUserId().equals(EMChatManager.getInstance().getCurrentUser()) ) {
				EMChatManager.getInstance().logout();
			}else{
				Log.i(TAG, " 已经登陆过环信，不需要再次登陆了");
				loadHXData(context);
				return ;
			}
		}


		final String pwd =  cacheBean.getString(context, KEY_HUANXIN_PWD);
		if(StringUtils.isEmpty(pwd)){
			Log.e(TAG, "环信登陆错误，密码为null");
			return ;
		}
		
		// 调用sdk登陆方法登陆聊天服务器
		EMChatManager.getInstance().login(cacheBean.getLoginUserId(), cacheBean.getString(context, KEY_HUANXIN_PWD), new EMCallBack() {

			@Override
			public void onSuccess() {
				Log.e(TAG, "Login  onSuccess " );
				loadHXData(context);
			}

			@Override
			public void onProgress(int progress, String status) { }

			@Override
			public void onError(int code, String message) {
				Log.e(TAG, "Login  failure " + message);
			}
		});
	}

	public static void logout(){
		if (EMChat.getInstance().isLoggedIn()){
			EMChatManager.getInstance().logout();
		}
	}
	
	
	/**
	 *   屏蔽用户操作
	 */
	public static  void maskOperation(final String toChatUsername) {
		// 第二个参数如果为true，则把用户加入到黑名单后双方发消息时对方都收不到；false,则
		// 我能给黑名单的中用户发消息，但是对方发给我时我是收不到的
		AppContext.EXECUTOR_SERVICE.execute(new Runnable() {

			@Override
			public void run() {
				try {
					if (MyHXSDKHelper.getBlackList().contains(toChatUsername)) {
						EMContactManager.getInstance().deleteUserFromBlackList( toChatUsername);
						MyHXSDKHelper.getBlackList().remove(toChatUsername);
					} else {
						EMContactManager.getInstance().addUserToBlackList( toChatUsername, false);
						MyHXSDKHelper.getBlackList().add(toChatUsername);
					}
				} catch (EaseMobException e) {
					Log.e(TAG, "屏蔽用户出错", e);
				}
			}
		});
	}

	public synchronized static List<String> getBlackList() {
		if(blackList==null)blackList = new ArrayList<String>();
		return blackList;
	}

	public static void setBlackList(List<String> blackList) {
		MyHXSDKHelper.blackList = blackList;
	}

}