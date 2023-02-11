/**
 * 
 */
package com.bloomlife.videoapp.common.util;

import java.util.Iterator;
import java.util.List;

import android.app.ActivityManager;
import android.content.Context;
import android.util.Log;

/**
 * @author <a href="mailto:xiai.fei@gmail.com">xiai_fei</a>  
 *
 * @date 2014-12-26  下午4:20:40
 */
public class ApplicationUtils {
	
	 public static String getAppName(Context context  ,int pID) {
			String processName = null;
			ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
			List<?> l = am.getRunningAppProcesses();
			Iterator<?> i = l.iterator();
//			PackageManager pm = appContext.getPackageManager();
			while (i.hasNext()) {
				ActivityManager.RunningAppProcessInfo info = (ActivityManager.RunningAppProcessInfo) (i.next());
				try {
					if (info.pid == pID) {
//						CharSequence c = pm.getApplicationLabel(pm.getApplicationInfo(info.processName,PackageManager.GET_META_DATA));
						processName = info.processName;
						return processName;
					}
				} catch (Exception e) {
					Log.e("", "",e);
				}
			}
			return processName;
		}
}
