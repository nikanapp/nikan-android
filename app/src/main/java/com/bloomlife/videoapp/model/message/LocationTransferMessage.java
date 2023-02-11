/**
 * 
 */
package com.bloomlife.videoapp.model.message;

import com.bloomlife.android.bean.BaseMessage;

/**
 *   
 *
 * @author <a href="mailto:xiai.fei@gmail.com">xiai_fei</a>  
 *
 * @date 2015-6-19 下午6:41:18
 *
 * @organization bloomlife  
 * @version 1.0
 */
public class LocationTransferMessage extends BaseMessage{

	
	
	public LocationTransferMessage(double lat, double lon) {
		super();
		this.setMsgCode("3039");
		this.lat = lat;
		this.lon = lon;
	}

	private double lat ;
	
	private double lon ;

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
	
	
}
