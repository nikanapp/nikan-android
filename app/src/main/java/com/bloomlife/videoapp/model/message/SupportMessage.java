/**
 * 
 */
package com.bloomlife.videoapp.model.message;

import com.bloomlife.android.bean.BaseMessage;

/**
 * @author <a href="mailto:lan4627@gmail.com">zxt</a>
 * 
 * @date 2014年12月9日 下午12:04:26
 */
public class SupportMessage extends BaseMessage {

	private String videoId;
	private String lat;
	private String lon;
	
	public SupportMessage(){
		setMsgCode("3008");
	}
	
	public SupportMessage(String videoid, String lat, String lon){
		setMsgCode("3008");
		this.videoId = videoid;
		this.lat = lat;
		this.lon = lon;
	}

	public String getVideoId() {
		return videoId;
	}

	public void setVideoId(String videoId) {
		this.videoId = videoId;
	}

	public String getLat() {
		return lat;
	}

	public void setLat(String lat) {
		this.lat = lat;
	}

	public String getLon() {
		return lon;
	}

	public void setLon(String lon) {
		this.lon = lon;
	}
}
