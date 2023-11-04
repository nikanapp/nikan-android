/**
 * 
 */
package com.bloomlife.videoapp.manager;

import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.alibaba.fastjson.JSONArray;
import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.bloomlife.android.bean.BaseMessage;
import com.bloomlife.android.bean.CacheBean;
import com.bloomlife.android.common.util.GpsCoordinateUtils;
import com.bloomlife.android.common.util.UiHelper;
import com.bloomlife.android.executor.AsyncRequest;
import com.bloomlife.android.executor.AsyncRequest.RequestCallBack;
import com.bloomlife.android.executor.RequestAsyncTask;
import com.bloomlife.videoapp.app.Constants;
import static com.bloomlife.videoapp.common.CacheKeyConstants.*;
import com.bloomlife.videoapp.model.message.LocationTransferMessage;
import com.bloomlife.videoapp.model.result.TransferResult;

/**
 *   
 *
 * @author <a href="mailto:xiai.fei@gmail.com">xiai_fei</a>  
 *
 * @date 2015-6-19 下午5:17:27
 *
 * @organization bloomlife  
 * @version 1.0
 */
public class BaiduLocationListener implements LocationListener{

	public static final String TAG = BaiduLocationListener.class.getSimpleName();
	public static final int GPS_LOCATION_SUCCESS = 61;
	public static final int NETWORK_LOCATION_SUCCESS = 66;
	public static final int OFFLINE_LOCATION_SUCCESS = 161;
	
	private int transferTimes = 0 ;
	
	public LocationClient mLocationClient = null;
	public BDAbstractLocationListener myListener = new MyLocationListener();
	private CacheBean cacheBean = CacheBean.getInstance();
	 
	private Context mContext;

	public BaiduLocationListener(Context context){
		this.mContext = context;
		try {
			mLocationClient = new LocationClient(context);     // 声明LocationClient类
			mLocationClient.registerLocationListener(myListener);    // 注册监听函数
			mLocationClient.setLocOption(getOption());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void start() {
		if (mLocationClient == null) {
			return;
		}
		if (mLocationClient.isStarted()) {
			return;
		}
		mLocationClient.start();
		mLocationClient.requestLocation();
	}

	@Override
	public void stop() {
		if (mLocationClient == null) {
			return;
		}
		if (mLocationClient.isStarted()) {
			mLocationClient.stop();
		}
	}
	
	private LocationClientOption getOption(){
		LocationClientOption option = new LocationClientOption();
		option.setLocationMode(LocationMode.Hight_Accuracy);//设置定位模式
		option.setCoorType("gcj02");//返回的定位结果是百度经纬度,默认值gcj02
		option.setScanSpan(5000);//设置发起定位请求的间隔时间为5000ms
		option.setIsNeedAddress(true);//返回的定位结果包含地址信息
		return option;
	}
	
	private double mOldLatitude;
	private double mOldLongitude;
	
	public class MyLocationListener extends BDAbstractLocationListener {
		@Override
		public void onReceiveLocation(BDLocation location) {
			if (location == null) return;
			if (location.getLocType() != GPS_LOCATION_SUCCESS
					&& location.getLocType() != OFFLINE_LOCATION_SUCCESS
					&& location.getLocType() != NETWORK_LOCATION_SUCCESS) {
				Log.i(TAG, "location error locType=" + location.getLocType());
				return;
			}
			double newLatitude = location.getLatitude();
			double newLongitude = location.getLongitude();
			if (Math.abs(mOldLatitude - newLatitude) > 0.01 || Math.abs(mOldLongitude - newLongitude) > 0.01){
				mOldLatitude = newLatitude;
				mOldLongitude = newLongitude;
				
//				if(transferTimes<5){
//					transferTimes ++;
//
//					AsyncRequest.doRequest(new LocationTransferMessage(mOldLatitude, mOldLongitude), new RequestCallBack() {
//
//						@Override
//						public void onSuccess(Map<String, Object> map) {
//							JSONArray location = (JSONArray) map.get("result");
//							String lat = String.valueOf(location.get(1));
//							String lon = String.valueOf(location.get(0));
//							cacheBean.putString(mContext, LOCATION_LAT, lat);
//							cacheBean.putString(mContext, LOCATION_LON, lon);
//
//							// 如果需要在定位成功的时候得到通知，可以使用广播来进行。
//							Intent intent = new Intent(Constants.ACTION_LOCATION);
//							intent.putExtra(Constants.INTENT_LOCATION, location);
//							mContext.sendBroadcast(intent);
//						}
//					});
//				}
				double[] wgs84Location = GpsCoordinateUtils.calGCJ02toWGS84(mOldLatitude, mOldLongitude);
				cacheBean.putString(mContext, LOCATION_LAT, String.valueOf(wgs84Location[0]));
				cacheBean.putString(mContext, LOCATION_LON, String.valueOf(wgs84Location[1]));
				Log.i(TAG, "Latitude=" + wgs84Location[0] + " Longitude=" + wgs84Location[1]);
				// 如果需要在定位成功的时候得到通知，可以使用广播来进行。
				Intent intent = new Intent(Constants.ACTION_LOCATION);
				intent.putExtra(Constants.INTENT_LOCATION, location);
				mContext.sendBroadcast(intent);
				cacheBean.putString(mContext, LOCATION_CITY, location.getCity());
			}
			
			stop();
			
		}
	}

	
}
