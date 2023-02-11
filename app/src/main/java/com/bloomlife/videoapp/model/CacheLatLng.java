/**
 * 
 */
package com.bloomlife.videoapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * 
 * 	缓存用户上次操作的相关的经纬度信息
 * 
 * @author <a href="mailto:xiai.fei@gmail.com">xiai_fei</a>  
 *
 * @date 2014-12-5  下午10:04:03
 */
public class CacheLatLng implements Parcelable{
	
	public CacheLatLng(){}
	
	/**
	 * 
	 * @param center  这是地图当前的中心点位置，和用户的当前位置不是同一个概念。
	 * @param mBottomLeft
	 * @param topRight
	 */
	public CacheLatLng(MyLatLng center , MyLatLng mBottomLeft ,MyLatLng topRight){
		this.centerLat = center.getLat();
		this.centerLon = center.getLon();
		this.lowerleftlat = mBottomLeft.getLat();
		this.lowerleftlon = mBottomLeft.getLon();
		this.upperrightlon = topRight.getLon();
		this.upperrightlat = topRight.getLat();
		
	}
	
	
	private double centerLat; 
	private double centerLon;
	private double lowerleftlat=-1;
	private double upperrightlat=-1;
	private double lowerleftlon=-1;
	private double upperrightlon=-1;
	
	/**
	 * 设置地图最新的中心点
	 * @param center
	 */
	@JSONField(serialize=false)
	public void saveCenterPoint(MyLatLng center){
		this.centerLat = center.getLat();
		this.centerLon = center.getLon();
	}
	@JSONField(serialize=false)
	public void saveBottomLeft(MyLatLng mBottomLeft){
		this.lowerleftlat = mBottomLeft.getLat();
		this.lowerleftlon = mBottomLeft.getLon();
	}
	@JSONField(serialize=false)
	public void saveTopRight(MyLatLng topRight){
		this.upperrightlon = topRight.getLon();
		this.upperrightlat = topRight.getLat();
	}
	
	/***
	 * 获取地图最新的中心点
	 * @return
	 */
	@JSONField(serialize=false)
	public MyLatLng getCenterPoint(){
		if(centerLat==0||centerLon==0) return null;
		return new MyLatLng(centerLat, centerLon);
	}
	
	/***
	 * 是否有可以加载视频的坐标
	 * @return
	 */
	public boolean hasLoadVideoCoord(){
		return !(lowerleftlat<0||lowerleftlon<0||upperrightlat<0||upperrightlon<0);
	}
	
	public double getLowerleftlat() {
		return lowerleftlat;
	}
	public void setLowerleftlat(double lowerleftlat) {
		this.lowerleftlat = lowerleftlat;
	}
	public double getUpperrightlat() {
		return upperrightlat;
	}
	public void setUpperrightlat(double upperrightlat) {
		this.upperrightlat = upperrightlat;
	}
	public double getLowerleftlon() {
		return lowerleftlon;
	}
	public void setLowerleftlon(double lowerleftlon) {
		this.lowerleftlon = lowerleftlon;
	}
	public double getUpperrightlon() {
		return upperrightlon;
	}
	public void setUpperrightlon(double upperrightlon) {
		this.upperrightlon = upperrightlon;
	} 
	public MyLatLng getBottomLeft(){
		return new MyLatLng(lowerleftlat, lowerleftlon);
	}
	public MyLatLng getTopRight(){
		return new MyLatLng(upperrightlat, upperrightlon);
	}
	public double getCenterLat() {
		return centerLat;
	}
	public void setCenterLat(double centerLat) {
		this.centerLat = centerLat;
	}
	public double getCenterLon() {
		return centerLon;
	}
	public void setCenterLon(double centerLon) {
		this.centerLon = centerLon;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeDouble(centerLat);
		dest.writeDouble(centerLon);
		dest.writeDouble(lowerleftlat);
		dest.writeDouble(upperrightlat);
		dest.writeDouble(lowerleftlon);
		dest.writeDouble(upperrightlon);
	}
	
	public static final Parcelable.Creator<CacheLatLng> CREATOR = new Creator<CacheLatLng>() {
		
		@Override
		public CacheLatLng[] newArray(int size) {
			return new CacheLatLng[size];
		}
		
		@Override
		public CacheLatLng createFromParcel(Parcel source) {
			CacheLatLng cacheLatLng = new CacheLatLng();
			cacheLatLng.setCenterLat(source.readDouble());
			cacheLatLng.setCenterLon(source.readDouble());
			cacheLatLng.setLowerleftlat(source.readDouble());
			cacheLatLng.setUpperrightlat(source.readDouble());
			cacheLatLng.setLowerleftlon(source.readDouble());
			cacheLatLng.setUpperrightlon(source.readDouble());
			return cacheLatLng;
		}
	};
	
}
