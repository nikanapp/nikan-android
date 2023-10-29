/**
 * 
 */
package com.bloomlife.videoapp.model.message;

import com.bloomlife.android.bean.BaseMessage;

/**
 * @author <a href="mailto:lan4627@gmail.com">zxt</a>
 *
 * @date 2015年1月4日 下午2:26:36
 */
public class UserLocationMessage extends BaseMessage {
	private String lat;
	private String lon;
	
	public UserLocationMessage(){
		setMsgCode("3022");
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
