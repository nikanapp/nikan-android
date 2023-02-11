package com.bloomlife.videoapp.model.message;

import android.content.Context;

import com.bloomlife.android.bean.BaseMessage;
import com.bloomlife.android.bean.CacheBean;
import com.bloomlife.videoapp.common.CacheKeyConstants;

public class GetVideoMessage extends BaseMessage {
	
	public static final int MY = 1;
	public static final int OTHER = 0;

	private String videoId;
	private String lon;
	private String lat;
	private int ismy;
	
	public GetVideoMessage(){
		setMsgCode("3005");
	}
	
	public GetVideoMessage(String videoid){
		setMsgCode("3005");
		this.videoId = videoid;
	}
	
	public String getVideoId() {
		return videoId;
	}

	public void setVideoId(String videoid) {
		this.videoId = videoid;
	}

	public String getLon() {
		return lon;
	}

	public void setLon(String lon) {
		this.lon = lon;
	}

	public String getLat() {
		return lat;
	}

	public void setLat(String lat) {
		this.lat = lat;
	}
	
	public static GetVideoMessage makeVideoMessage(Context context, String videoId, boolean isMy){
		GetVideoMessage message = new GetVideoMessage();
		String lat = CacheBean.getInstance().getString(context, CacheKeyConstants.LOCATION_LAT);
		String lon = CacheBean.getInstance().getString(context, CacheKeyConstants.LOCATION_LON);
		message.setLat(lat);
		message.setLon(lon);
		message.setVideoId(videoId);
		message.setIsmy(isMy ? GetVideoMessage.MY : GetVideoMessage.OTHER);
		return message;
	}

	/**
	 * @return the ismy
	 */
	public int getIsmy() {
		return ismy;
	}

	/**
	 * @param ismy the ismy to set
	 */
	public void setIsmy(int ismy) {
		this.ismy = ismy;
	}
	
}
