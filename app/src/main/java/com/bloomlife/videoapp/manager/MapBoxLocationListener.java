/**
 * 
 */
package com.bloomlife.videoapp.manager;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import com.bloomlife.android.bean.CacheBean;
import com.bloomlife.android.common.util.UiHelper;
import com.bloomlife.videoapp.app.Constants;
import com.bloomlife.videoapp.common.CacheKeyConstants;

/**
 * @author <a href="mailto:lan4627@gmail.com">zxt</a>
 *
 * @date 2015年5月19日 下午2:52:32
 */
public class MapBoxLocationListener implements LocationListener {
	
	private static final String TAG = MapBoxLocationListener.class.getSimpleName();

	private LocationManager mLocationManager;
	
	private Context mContext;
	
	private boolean mStart;

	public MapBoxLocationListener(Context context) {
		mContext = context;
		mLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
	}
	
	@Override
	public void start() {
		if (mStart)
			return;
		mStart = true;
		for (final String provider : mLocationManager.getProviders(true)) {
            if (LocationManager.GPS_PROVIDER.equals(provider)
                    || LocationManager.PASSIVE_PROVIDER.equals(provider)
                    || LocationManager.NETWORK_PROVIDER.equals(provider)) {
                mLocationManager.requestLocationUpdates(provider, 0L, 0.0f, mAndroidLocationListener);
            }
        }
	}

	@Override
	public void stop() {
		if (mStart){
			mLocationManager.removeUpdates(mAndroidLocationListener);
			mStart = false;
		}
	}
	
	private double mOldLatitude;
	private double mOldLongitude;

	private android.location.LocationListener mAndroidLocationListener = new android.location.LocationListener(){

		@Override
		public void onLocationChanged(Location location) {
			double newLatitude = location.getLatitude();
			double newLongitude = location.getLongitude();
			if (Math.abs(mOldLatitude - newLatitude) > 0.01 || Math.abs(mOldLongitude - newLongitude) > 0.01){
				mOldLatitude = newLatitude;
				mOldLongitude = newLongitude;
				String lat = String.valueOf(newLatitude);
				String lon = String.valueOf(newLongitude);
				CacheBean.getInstance().putString(mContext, CacheKeyConstants.LOCATION_LAT, lat);
				CacheBean.getInstance().putString(mContext, CacheKeyConstants.LOCATION_LON, lon);
				
				try {
					Geocoder gcd = new Geocoder(mContext, Locale.getDefault());
					List<Address> addresses;
					addresses = gcd.getFromLocation(newLatitude, newLongitude, 1);
					if (addresses.size() > 0) {
					    System.out.println("city >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>"+addresses.get(0).getLocality());
						CacheBean.getInstance().putString(mContext, CacheKeyConstants.LOCATION_CITY, addresses.get(0).getLocality());
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			stop();
			//如果需要在定位成功的时候得到通知，可以使用广播来进行。
			Intent intent = new Intent(Constants.ACTION_LOCATION);
			intent.putExtra(Constants.INTENT_LOCATION, location);
			mContext.sendBroadcast(intent);
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub
			
		}
		
	};
	
}
