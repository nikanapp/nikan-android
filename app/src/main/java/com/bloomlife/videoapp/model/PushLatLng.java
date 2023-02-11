/**
 * 
 */
package com.bloomlife.videoapp.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author <a href="mailto:lan4627@gmail.com">zhengxingtian</a>
 *
 * @date 2014年12月31日 下午7:21:20
 */
public class PushLatLng implements Parcelable{
	
	private String videoId;
	private double lat;
	private double lon;
	
	public PushLatLng(){
		
	}

	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public double getLon() {
		return lon;
	}

	public void setLon(double lon) {
		this.lon = lon;
	}
	
	public String getVideoId() {
		return videoId;
	}

	public void setVideoId(String videoId) {
		this.videoId = videoId;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(videoId);
		dest.writeDouble(lat);
		dest.writeDouble(lon);
	}
	
	public static final Parcelable.Creator<PushLatLng> CREATOR = new Creator<PushLatLng>() {
		
		@Override
		public PushLatLng[] newArray(int size) {
			return new PushLatLng[size];
		}
		
		@Override
		public PushLatLng createFromParcel(Parcel source) {
			PushLatLng latLng = new PushLatLng();
			latLng.setVideoId(source.readString());
			latLng.setLat(source.readDouble());
			latLng.setLon(source.readDouble());
			return latLng;
		}
	};
}
