/**
 * 
 */
package com.bloomlife.videoapp.model;

/**
 * @author <a href="mailto:lan4627@gmail.com">zhengxingtian</a>
 *
 * @date 2015年5月19日 下午12:47:07
 */
public class MyLatLng {

	private double lat;
	private double lon;

	public MyLatLng(double lat, double lon) {
		this.lat = lat;
		this.lon = lon;
	}

	/**
	 * @return the lat
	 */
	public double getLat() {
		return lat;
	}

	/**
	 * @param lat the lat to set
	 */
	public void setLat(double lat) {
		this.lat = lat;
	}

	/**
	 * @return the lon
	 */
	public double getLon() {
		return lon;
	}

	/**
	 * @param lon the lon to set
	 */
	public void setLon(double lon) {
		this.lon = lon;
	}

}
