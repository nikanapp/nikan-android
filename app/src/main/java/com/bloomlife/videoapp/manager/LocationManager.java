/**
 * 
 */
package com.bloomlife.videoapp.manager;

import com.baidu.location.LocationClient;
import com.bloomlife.videoapp.BuildConfig;
import com.bloomlife.videoapp.common.util.SystemUtils;

import android.content.Context;

/**
 * @author <a href="mailto:xiai.fei@gmail.com">xiai_fei</a>  
 *
 * @date 2014-12-5  上午11:05:52
 */
public class LocationManager {
	
	private LocationListener mLocationListener;
	
	private static LocationManager locationManager;
	
	/**
	 * 
	 * @param context  因为是全局的，所以需要使用applicationContext
	 * @return
	 */
	public synchronized static LocationManager getInstance(Context context){
		if(locationManager==null)locationManager = new LocationManager(context);
		return locationManager;
	}
	
	private LocationManager(Context context){
		agreePrivacy();
		LocationClient.setKey(BuildConfig.BDIUD_LBS_APP_KEY);
		mLocationListener = new BaiduLocationListener(context);
	}
	
	public void startLocation(){
		mLocationListener.start();
	}

	public void stopLocation(){
		mLocationListener.stop();
	}

	public void agreePrivacy() {
		LocationClient.setAgreePrivacy(true);
	}

}
