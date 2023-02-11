///**
// * 
// */
//package com.bloomlife.videoapp.model;
//
//import com.alibaba.fastjson.annotation.JSONField;
//import com.amap.api.maps.model.LatLng;
//
///**
// * @author <a href="mailto:xiai.fei@gmail.com">xiai_fei</a>  
// *
// * @date 2014-12-23  下午4:32:35
// */
//public class GaodeCacheLatLng {
//
//	public GaodeCacheLatLng(){}
//	
//	/**
//	 * 
//	 * @param center  这是地图当前的中心点位置，和用户的当前位置不是同一个概念。
//	 * @param mBottomLeft
//	 * @param topRight
//	 */
//	public GaodeCacheLatLng(LatLng center , LatLng mBottomLeft ,LatLng topRight ){
//		this.centerLat = center.latitude;
//		this.centerLon = center.longitude;
//		this.lowerleftlat = mBottomLeft.latitude;
//		this.lowerleftlon = mBottomLeft.longitude;
//		this.upperrightlon = topRight.longitude;
//		this.upperrightlat = topRight.latitude;
//		
//	}
//	
//	
//	private double centerLat; 
//	private double centerLon;
//	private double lowerleftlat;
//	private double upperrightlat;
//	private double lowerleftlon;
//	private double upperrightlon;
//	
//	/**
//	 * 设置地图最新的中心点
//	 * @param center
//	 */
//	@JSONField(serialize=false)
//	public void saveCenterPoint(LatLng center){
//		this.centerLat = center.latitude;
//		this.centerLon = center.longitude;
//	}
//	@JSONField(serialize=false)
//	public void saveBottomLeft(LatLng mBottomLeft){
//		this.lowerleftlat = mBottomLeft.latitude;
//		this.lowerleftlon = mBottomLeft.longitude;
//	}
//	@JSONField(serialize=false)
//	public void saveTopRight(LatLng topRight){
//		this.upperrightlon = topRight.longitude;
//		this.upperrightlat = topRight.latitude;
//	}
//	
//	/***
//	 * 获取地图最新的中心点
//	 * @return
//	 */
//	@JSONField(serialize=false)
//	public LatLng getCenterPoint(){
//		if(centerLat==0||centerLon==0) return null;
//		return new LatLng(centerLat, centerLon);
//	}
//	
//	public double getLowerleftlat() {
//		return lowerleftlat;
//	}
//	public void setLowerleftlat(double lowerleftlat) {
//		this.lowerleftlat = lowerleftlat;
//	}
//	public double getUpperrightlat() {
//		return upperrightlat;
//	}
//	public void setUpperrightlat(double upperrightlat) {
//		this.upperrightlat = upperrightlat;
//	}
//	public double getLowerleftlon() {
//		return lowerleftlon;
//	}
//	public void setLowerleftlon(double lowerleftlon) {
//		this.lowerleftlon = lowerleftlon;
//	}
//	public double getUpperrightlon() {
//		return upperrightlon;
//	}
//	public void setUpperrightlon(double upperrightlon) {
//		this.upperrightlon = upperrightlon;
//	} 
//	public LatLng getBottomLeft(){
//		return new LatLng(lowerleftlat, lowerleftlon);
//	}
//	public LatLng getTopRight(){
//		return new LatLng(upperrightlat, upperrightlon);
//	}
//	public double getCenterLat() {
//		return centerLat;
//	}
//	public void setCenterLat(double centerLat) {
//		this.centerLat = centerLat;
//	}
//	public double getCenterLon() {
//		return centerLon;
//	}
//	public void setCenterLon(double centerLon) {
//		this.centerLon = centerLon;
//	}
//	
//	
//}
