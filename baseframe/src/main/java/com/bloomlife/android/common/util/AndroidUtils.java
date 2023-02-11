/**
 * 
 */
package com.bloomlife.android.common.util;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.Context;

/**
 * 和android系统相关的工具类
 * 
 * @author <a href="mailto:xiai.fei@gmail.com">xiai_fei</a>
 * 
 * @date 2014-9-1 下午12:15:38
 */
public class AndroidUtils {

	/**
	 * 判断app是否在运行
	 * @param context
	 * @return
	 */
	public static boolean isAppRunnable(Context context) {
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> list = am.getRunningTasks(100);
		String MY_PKG_NAME =context.getPackageName();
		for (RunningTaskInfo info : list) {
			if (info.topActivity.getPackageName().equals(MY_PKG_NAME) || info.baseActivity.getPackageName().equals(MY_PKG_NAME)) {
				return true ;
			}
		}
		return false ;
	}

}
