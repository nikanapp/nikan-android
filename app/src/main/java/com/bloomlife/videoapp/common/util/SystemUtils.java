/**
 * 
 */
package com.bloomlife.videoapp.common.util;

import java.lang.reflect.Field;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.location.LocationManager;

/**
 * 获取一些系统状态的工具类
 * @author <a href="mailto:lan4627@gmail.com">zxt</a>
 *
 * @date 2014年11月21日 上午10:52:36
 */
public class SystemUtils {
	
	public static int getStatusBarHeight(Context context) {
	    Class<?> c = null;
	    Object obj = null;
	    Field field = null;
	    int x = 0, statusBarHeight = 0;
	    try {
	        c = Class.forName("com.android.internal.R$dimen");
	        obj = c.newInstance();
	        field = c.getField("status_bar_height");
	        x = Integer.parseInt(field.get(obj).toString());
	        statusBarHeight = context.getResources().getDimensionPixelSize(x);
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return statusBarHeight;
	}

	public static int getNavigationBarHeight(Context context){
		int resId = context.getResources().getIdentifier("navigation_bar_height", "dimen", "android");
		if (resId > 0){
			return context.getResources().getDimensionPixelSize(resId);
		} else {
			return 0;
		}
	}
	
	public static String getSystemInfo(){
		return "机型: "+android.os.Build.MODEL+" 系统版本："+android.os.Build.VERSION.RELEASE;
	}
	
	public static int getVersionCode(Context context){
		try {
			return context.getPackageManager().getPackageInfo(context.getPackageName(), android.content.pm.PackageManager.GET_CONFIGURATIONS).versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return 140;
	}
	
	public static boolean isGpsOn(Context context)
	{
		LocationManager alm =(LocationManager)context.getSystemService( Context.LOCATION_SERVICE );
		return alm.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER ) ;
	}
}
