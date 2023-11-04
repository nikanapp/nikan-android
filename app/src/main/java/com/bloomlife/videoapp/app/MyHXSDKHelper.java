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
import androidx.core.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;

import com.bloomlife.android.bean.CacheBean;
import com.bloomlife.android.common.util.StringUtils;
import com.bloomlife.android.common.util.Utils;
import com.bloomlife.videoapp.BuildConfig;
import com.bloomlife.videoapp.R;
import com.bloomlife.videoapp.activity.RealNameChatActivity;
import com.bloomlife.videoapp.activity.AnonymousChatActivity;
import com.bloomlife.videoapp.common.util.UIHelper;
import com.bloomlife.videoapp.model.ChatBean;
import com.bloomlife.videoapp.model.ConversationMessage;
import com.hyphenate.EMCallBack;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMChatManager;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMImageMessageBody;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMOptions;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.exceptions.HyphenateException;
import com.hyphenate.push.EMPushConfig;
import com.hyphenate.push.EMPushHelper;

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


		// 获取到EMChatOptions对象
		EMOptions options = new EMOptions();
		options.setAppKey(BuildConfig.EMCHAT_APP_KEY);
		// 默认添加好友时，是不需要验证的，改成需要验证
		options.setAcceptInvitationAlways(true);
		// 初始化环信SDK
		EMClient.getInstance().init(appContext, options);
		EMClient.getInstance().setDebugMode(BuildConfig.DEBUG);
		registerHxMessageRecevier();
		isInit = true ;
		Log.d(TAG, "  环信初始化成功 ！！！！！！！！！");
	}

	private void registerHxMessageRecevier() {
		EMClient.getInstance().chatManager().addMessageListener(new NotifyEMMessageListener());
	}
	
	class NotifyEMMessageListener implements EMMessageListener {

		@Override
		public void onMessageReceived(List<EMMessage> list) {
			if (PushService.isOpenPush(appContext)) {
				for (EMMessage message:list) {
					// 消息id
					if (PushService.isOpenPush(appContext)){
						MyHXSDKHelper.getInstance().notifyNewMessage(message, appContext);
					}
				}
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
		updateConverstaionMsgByLatestChat(context,message.getFrom(), message);
		EMClient.getInstance().chatManager().getConversation(message.getFrom()).markAllMessagesAsRead();
		
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context).setSmallIcon(context.getApplicationInfo().icon).setWhen(System.currentTimeMillis()).setAutoCancel(true);

		String ticker = null;
		if (message.getBody() instanceof EMTextMessageBody){
			ticker = ((EMTextMessageBody) message.getBody()).getMessage();
			ticker = ticker.replaceAll("\\[.{2,3}\\]", context.getString(R.string.notification_emoj));
		} else if(message.getBody() instanceof EMImageMessageBody){
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
		} catch (HyphenateException e) {
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
			if (latestMsg.getBody() instanceof EMTextMessageBody){
				String content = ((EMTextMessageBody)latestMsg.getBody()).getMessage();
				if(message.getUpdateTime().getTime()==latestMsg.getMsgTime()
						&&StringUtils.isNotEmpty(content)&&content.equals(message.getContent()))return ; //去重
			} else if(latestMsg.getBody() instanceof EMImageMessageBody){
				String imagePath = UIHelper.getEMMessageImage((EMImageMessageBody)latestMsg.getBody());
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
		if(EMClient.getInstance().isLoggedIn()){
			if (!cacheBean.getLoginUserId().equals(EMClient.getInstance().getCurrentUser()) ) {
				EMClient.getInstance().logout(true, new EMCallBack() {
					@Override
					public void onSuccess() {
						Log.i(TAG, "退出环信成功");
					}

					@Override
					public void onError(int i, String s) {
						Log.i(TAG, "退出环信失败 code=" + i + " msg=" + s);
					}
				});
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
		EMClient.getInstance().login(cacheBean.getLoginUserId(), cacheBean.getString(context, KEY_HUANXIN_PWD), new EMCallBack() {

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
		if (EMClient.getInstance().isLoggedIn()){
			EMClient.getInstance().logout(true, new EMCallBack() {
				@Override
				public void onSuccess() {
					Log.i(TAG, "退出环信成功");
				}

				@Override
				public void onError(int i, String s) {
					Log.i(TAG, "退出环信失败 code=" + i + " msg=" + s);
				}
			});
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
						EMClient.getInstance().contactManager().removeUserFromBlackList(toChatUsername);
						MyHXSDKHelper.getBlackList().remove(toChatUsername);
					} else {
						EMClient.getInstance().contactManager().addUserToBlackList( toChatUsername, false);
						MyHXSDKHelper.getBlackList().add(toChatUsername);
					}
				} catch (HyphenateException e) {
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