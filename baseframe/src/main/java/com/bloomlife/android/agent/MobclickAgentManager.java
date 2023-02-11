/**
 * 
 */
package com.bloomlife.android.agent;

import java.util.HashMap;

import android.content.Context;
import android.view.ViewDebug.FlagToString;

import com.bloomlife.android.bean.LoginMessage;
import com.umeng.analytics.MobclickAgent;

/**
 * @author <a href="mailto:xiai.fei@gmail.com">xiai_fei</a>  
 *
 * @date 2014-7-31  下午3:06:55
 */
@Deprecated
public class MobclickAgentManager {

	
	// 友盟数据统计 事件名称
		public final static String THIRDLOGINTYPE = "LoginType";
		public final static String ACTIVITYUSER = "ActivityUser";
		
		
	 /**发送事件到友盟统计*/
	public static  void sendUserEventToServer(Context context , String eventName , LoginMessage loginMessage){
		HashMap<String,String> map = new HashMap<String,String>();
		map.put("channelNum",loginMessage.getChannelnum());
		if (loginMessage.getLoginType().equals("1")) {
			map.put("loginType","SinaWeibo"); 
		}else {
			map.put("loginType","QQ"); 
		}
//		map.put("softVersion", loginMessage.getSoftVersion());
		map.put("softVersionName", loginMessage.getApplicationVersionName());
		MobclickAgent.onEvent(context, ACTIVITYUSER, map);
	}
	
	public static void sendActiveUserEvent(Context context  , LoginMessage loginMessage){
		sendUserEventToServer(context , ACTIVITYUSER , loginMessage);
	}
	
	public static void sendLoginEventToServer(Context context  , LoginMessage loginMessage){
		sendUserEventToServer(context , THIRDLOGINTYPE , loginMessage);
	}
}
