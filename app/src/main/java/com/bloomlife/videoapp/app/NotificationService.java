/**
 * 
 */
package com.bloomlife.videoapp.app;

import java.util.List;

import com.bloomlife.android.common.util.AndroidUtils;
import com.bloomlife.videoapp.activity.MainActivity;
import com.bloomlife.videoapp.activity.MessageListActivity;
import com.bloomlife.videoapp.activity.SpalshActivity;
import com.bloomlife.videoapp.model.PayLoad;
import com.bloomlife.videoapp.model.PushLatLng;
import com.bloomlife.videoapp.model.PayLoad.TYPE;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * @author <a href="mailto:lan4627@gmail.com">zxt</a>
 *
 * @date 2015年2月6日 上午11:25:00
 */
public class NotificationService extends BroadcastReceiver {

	public static final String PUSH_LAT_LNG = "push_lat_lng";
	public static final String PUSH_PAYLOAD = "push_pay_load";
	
	public static final String ACTION = NotificationService.class.getName();
	
	@Override
	public void onReceive(Context context, Intent intent) {
		PayLoad load = intent.getParcelableExtra(PUSH_PAYLOAD);
		Intent activityIntent = getStartActivityIntent(context, load);
		if (activityIntent != null){
			activityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(activityIntent);
		}
		PushService.setUnreadPushMessage(PushService.getUnreadPushMessage()-1);
	}
	
	/**
	 * @param context
	 * @param load
	 * @param cinfo
	 * @return
	 */
	private Intent getStartActivityIntent(Context context, PayLoad load) {
		Intent updateIntent = null;
		if(!AndroidUtils.isAppRunnable(context)){
			//app没有运行
			updateIntent = new Intent(context, PayLoad.TYPE.OpenMap.clazz);
		}else{
			updateIntent = getRuntimeIntent(context, load);
		}
		return updateIntent;
	}
	
	private Intent getRuntimeIntent(Context context, PayLoad load){
		TYPE type = TYPE.getType(load.getType());
		// 判断app当前是否在显示
		ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> runningTasks = manager.getRunningTasks(1);
		RunningTaskInfo cinfo = runningTasks.get(0);
		String topName = cinfo.topActivity.getClassName();
		Intent intent = null;
		// 发送广播打新消息标记
		if (!context.getPackageName().equals(cinfo.topActivity.getPackageName())) {
			//当前APP没有显示
			intent = getActivityIntent(context, type.clazz, load);
		} else if(type.clazz.getName().equals(topName)){
			if (PayLoad.TYPE.OpenMap.clazz.getName().equals(topName)){
				// 如果当前要推送的页面是地图页面
				return sendVideoAndLatLng(context, load);
			} else {
				// 如果当前要推送的页面正在显示，则不跳转。
				return null;
			} 
		} else {
			intent = getActivityIntent(context, type.clazz, load);
		}
		
		return intent;
	}
	
	private Intent getActivityIntent(Context context, Class<?> clazz, PayLoad load){
		if (clazz.equals(SpalshActivity.class)){
			return new Intent(context, SpalshActivity.class);
		} else if (clazz.equals(PayLoad.TYPE.OpenMap.clazz)){
			Intent intent = new Intent(context, PayLoad.TYPE.OpenMap.clazz);
			intent.putExtra(MainActivity.INTENT_LATLNG, createPushLatLng(load));
			return intent;
		} else if (clazz.equals(MessageListActivity.class)){
			Intent intent = new Intent(context, MessageListActivity.class);
			intent.putExtra(MessageListActivity.INTENT_TYPE, MessageListActivity.NOTIFICATION);
			return intent;
		} else {
			return new Intent();
		}
	}
	
	private PushLatLng createPushLatLng(PayLoad load){
		PushLatLng latLng = new PushLatLng();
		latLng.setVideoId(load.getVideoid());
		latLng.setLat(load.getLat());
		latLng.setLon(load.getLon());
		return latLng;
	}
	
	/**
	 * 发送广播到地图页面
	 * @param context
	 * @param load
	 * @return
	 */
	private Intent sendVideoAndLatLng(Context context, PayLoad load){
		Intent intent = new Intent(Constants.ACTION_PUSH_VIDEO);
		intent.putExtra(PUSH_LAT_LNG, createPushLatLng(load));
		context.sendBroadcast(intent);
		return null;
	}

}
