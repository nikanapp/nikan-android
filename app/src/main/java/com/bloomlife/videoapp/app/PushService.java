package com.bloomlife.videoapp.app;

import static com.igexin.sdk.PushConsts.CMD_ACTION;
import static com.igexin.sdk.PushConsts.GET_CLIENTID;
import static com.igexin.sdk.PushConsts.GET_MSG_DATA;
import static com.igexin.sdk.PushConsts.SETTAG_SUCCESS;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.http.HttpException;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.bloomlife.android.bean.BaseMessage;
import com.bloomlife.android.bean.CacheBean;
import com.bloomlife.android.bean.ProcessResult;
import com.bloomlife.android.network.HttpAccessor;
import com.bloomlife.videoapp.R;
import com.bloomlife.videoapp.manager.MessageManager;
import com.bloomlife.videoapp.model.PayLoad;
import com.bloomlife.videoapp.model.message.PushMessage;
import com.igexin.sdk.PushManager;
import com.igexin.sdk.Tag;

/***
 * 推送服务类。推送是否打开，有三个状态决定。一个是和app后台服务器的打开状态。一个是本地的open标志。还有一个是个推服务器的开关状态
 * 
 * @author <a href="mailto:xiai.fei@gmail.com">xiai_fei</a>
 * 
 * @date 2014-8-26 下午5:55:15
 */
public class PushService extends BroadcastReceiver {

	private static final String TAG = "PushService";

	private static final List<PushInterface> pushlisters = new ArrayList<PushInterface>();

	public static final String PUSH_SP_KEY = "psp";
	public static final String KEY_OPEN = "open";
	
	private static int unreadPushMessage;
	
	public static void initPush(Context context) {
		SharedPreferences sp = context.getSharedPreferences(PUSH_SP_KEY, Context.MODE_PRIVATE);
		boolean isOpen = sp.getBoolean(KEY_OPEN, true);
		if (isOpen) {
			PushManager.getInstance().initialize(context); // 多次执行初始化操作是可以的。
		}
	}

	public static boolean isOpenPush(Context context) {
		SharedPreferences sp = context.getSharedPreferences(PUSH_SP_KEY, Context.MODE_PRIVATE);
		sp.getBoolean(KEY_OPEN, true);
		return PushManager.getInstance().isPushTurnedOn(context);
	}

	public static void closePush(Context context) {
		SharedPreferences sp = context.getSharedPreferences(PUSH_SP_KEY, Context.MODE_PRIVATE);
		sendPushSwithcer(false, null, context);
		sp.edit().putBoolean(KEY_OPEN, false).commit();
		PushManager.getInstance().turnOffPush(context);
	}

	public static void openPush(Context context) {
		context.getSharedPreferences(PUSH_SP_KEY, Context.MODE_PRIVATE).edit().putBoolean(KEY_OPEN, true).commit();
		PushManager.getInstance().turnOnPush(context);
		PushManager.getInstance().initialize(context);
	}

	public static void addPushLister(PushInterface pushInterface) {
		if (!pushlisters.contains(pushInterface))
			pushlisters.add(pushInterface);
	}

	private boolean init = false;

	private Random random = new Random();

	
	/***
	 * 执行透传消息处理
	 * @param context
	 * @param payloadStr	透传的字符串消息
	 */
	private void firePayload(Context context, String payloadStr) {
		for (PushInterface pushInterface : pushlisters) {
			pushInterface.onReceiveMessage(payloadStr, null, null, false);
		}
		final NotificationManager updateNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		PayLoad load = null;
		if (payloadStr.contains("{") && payloadStr.contains("}")) {
			load = JSON.parseObject(payloadStr, PayLoad.class);
			if (load != null)
				payloadStr = load.getText();
		}
		
		

		//设置点击通知之后的行为
		Intent intent = new Intent(NotificationService.ACTION);
		intent.putExtra(NotificationService.PUSH_PAYLOAD, load);
		PendingIntent updatePendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

		// 设置通知栏显示内容
		Notification.Builder builder = new Notification.Builder(context);
		builder.setContentTitle(context.getString(R.string.app_name));
		builder.setContentText(payloadStr);
		builder.setSmallIcon(R.drawable.ic_launcher);
		builder.setContentIntent(updatePendingIntent);
		builder.setTicker(payloadStr);
		builder.setLights(0xff00ff00, 500, 500);

		if (load != null) {
			if (PayLoad.Swith_On == load.getSound()) {
				builder.setSound(Uri.parse("android.resource://"+context.getPackageName()+"/"+R.raw.notification));
			}
			if (PayLoad.Swith_On==load.getVibration()) {
				builder.setDefaults(Notification.DEFAULT_VIBRATE);
			}
		}

		Notification updateNotification = builder.build();
		updateNotification.flags |= Notification.FLAG_AUTO_CANCEL;
		updateNotification.flags |= Notification.FLAG_SHOW_LIGHTS;

		// 发出通知
		updateNotificationManager.notify(random.nextInt(5), updateNotification);
		MessageManager.getInstance().addSysInformNum(context,load==null?null:load.getMsgId());
		unreadPushMessage++;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		Bundle bundle = intent.getExtras();
		switch (bundle.getInt(CMD_ACTION)) {
		case GET_MSG_DATA:
			// 获取透传数据
			byte[] payload = bundle.getByteArray("payload");
			if (payload != null) {
				String data = new String(payload);
				Log.d(TAG, "收到透传数据:" + data);
				firePayload(context, data);
			}
			break;
		case GET_CLIENTID:
			// 获取ClientID(CID)
			/* 第三方应用需要将ClientID上传到第三方服务器，并且将当前用户帐号和ClientID进行关联，以便以后通过用户帐号查找ClientID进行消息推送
			有些情况下ClientID可能会发生变化，为保证获取最新的ClientID，请应用程序在每次获取ClientID广播后，都能进行一次关联绑定 */
			final String cid = bundle.getString("clientid");
//			Log.d(TAG, " 获取推送的uid成功， 为： " +cid);
			if (CacheBean.getInstance().hasLoginUserId() && !init) {
				init = true;
				sendPushSwithcer(true, cid, context);
				setPushTag(context);
			}
			break;
		case SETTAG_SUCCESS:
			break;
		}
	}


	/***
	 * 设置推送的tag。版本和渠道都设置为tag
	 */
	private void setPushTag(Context context) {
		try {
			List<Tag> tagList = new ArrayList<Tag>();

			// 添加版本标签
			Tag t1 = new Tag();
			t1.setName(AppContext.versionCode + "");
			tagList.add(t1);

			// 添加渠道标签
			ApplicationInfo info = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
			Object umengChannel = info.metaData.get("UMENG_CHANNEL");
			umengChannel = umengChannel == null ? "9999" : umengChannel;

			Tag t2 = new Tag();
			t2.setName(umengChannel.toString());
			tagList.add(t2);

			// 发送到个推后台设置
			Tag[] tags = new Tag[2];
			int result = PushManager.getInstance().setTag(context, tagList.toArray(tags));
			if(result==0)	Log.i(TAG, "set push tag success  ");
			else Log.e(TAG, "set push tag failure , result :  " + result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/***
	 * 发送推送开关事件
	 */
	private static void sendPushSwithcer(boolean open, String deviceToken, Context context) {
		PushMessage pushMessage = new PushMessage(open, deviceToken);
		new PushSwitchTask(context, pushMessage).execute();
	}

	private static class PushSwitchTask extends AsyncTask<Void,Void,ProcessResult> {
		private Context context;
		private BaseMessage baseMessage;
		public PushSwitchTask(Context context, BaseMessage baseMessage) {
			this.context= context ;
			this.baseMessage = baseMessage;
		}

		@Override
		protected ProcessResult doInBackground(Void... params) {
			HttpAccessor accessor = new HttpAccessor(context);
			try {
				ProcessResult result = accessor.call(baseMessage,ProcessResult.class);
				if(result != null && result.getCode()==ProcessResult.SUC) Log.i("", " 推送服务设置成功");
				else Log.e(TAG, "推送服务设置失败");
			} catch (HttpException e) {
				e.printStackTrace();
			}
			return null;
		}

	}

	public static int getUnreadPushMessage() {
		return unreadPushMessage;
	}

	public static void setUnreadPushMessage(int unreadPushMessage) {
		PushService.unreadPushMessage = unreadPushMessage;
	}
	
}
