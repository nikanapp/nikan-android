///**
// * 
// */
//package com.bloomlife.videoapp.manager;
//
//import android.content.Context;
//import android.content.Intent;
//
//import com.baidu.location.BDLocation;
//import com.baidu.location.BDLocationListener;
//import com.baidu.location.LocationClient;
//import com.baidu.location.LocationClientOption;
//import com.baidu.location.LocationClientOption.LocationMode;
//import com.bloomlife.android.bean.CacheBean;
//import com.bloomlife.android.common.util.StringUtils;
//import com.bloomlife.videoapp.app.Constants;
//import com.bloomlife.videoapp.common.CacheKeyConstants;
//
///**
// * @author <a href="mailto:lan4627@gmail.com">zxt</a>
// *
// * @date 2015年5月19日 下午2:31:36
// */
//public class BaiduMapLocationListener implements LocationListener, BDLocationListener {
//
//	private LocationClient mLocationClient;
//	
//	private Context mContext;
//	
//	public BaiduMapLocationListener(Context context) {
//		mLocationClient = new LocationClient(context);
//		mLocationClient.setLocOption(getOption());
//		mLocationClient.registerLocationListener(this);
//		this.mContext = context;
//	}
//
//	@Override
//	public void start() {
//		if (mLocationClient.isStarted()) return;
//		mLocationClient.start();
//		mLocationClient.requestLocation();
//	}
//
//	@Override
//	public void stop() {
//		if (mLocationClient!=null&&mLocationClient.isStarted())
//			mLocationClient.stop();
//	}
//
//	@Override
//	public void onReceiveLocation(BDLocation location) {
//		int locType = location.getLocType();
//		if (locType == BDLocation.TypeGpsLocation || locType==BDLocation.TypeNetWorkLocation){
//			String lat = String.valueOf(location.getLatitude());
//			String lon = String.valueOf(location.getLongitude());
//			CacheBean.getInstance().putString(mContext, CacheKeyConstants.LOCATION_LAT, lat);
//			CacheBean.getInstance().putString(mContext, CacheKeyConstants.LOCATION_LON, lon);
//			
//			String address = location.getCity();
//			if(StringUtils.isEmpty(address)) address = location.getProvince();
//			CacheBean.getInstance().putString(mContext, CacheKeyConstants.LOCATION_CITY, address);
//		}
//		stop();
//		//如果需要在定位成功的时候得到通知，可以使用广播来进行。
//		Intent intent = new Intent(Constants.ACTION_LOCATION);
//		intent.putExtra(Constants.INTENT_LOCATION, location);
//		mContext.sendBroadcast(intent);
//	}
//	
//	private LocationClientOption getOption(){
//		LocationClientOption option = new LocationClientOption();
//		option.setLocationMode(LocationMode.Hight_Accuracy);//设置定位模式
//		option.setCoorType("bd09ll");
//		option.setIsNeedAddress(true);
//		option.setTimeOut(30000);
//		option.setScanSpan(5000);
//		option.setOpenGps(true);
//		return option;
//	}
//
//}
