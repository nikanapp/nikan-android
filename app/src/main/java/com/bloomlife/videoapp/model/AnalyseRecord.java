/**
 * 
 */
package com.bloomlife.videoapp.model;

import java.util.Date;

/**
 *   前端数据统计记录实体
 *
 * @author <a href="mailto:xiai.fei@gmail.com">xiai_fei</a>  
 *
 * @date 2015-6-15 上午9:48:49
 *
 * @organization bloomlife  
 * @version 1.0
 */
public class AnalyseRecord {

	private String apiName ;
	
	private int reqTimeLength;
	
	private int resultCode;
	
	private Date reqTime ;
	
	private int lat ;
	
	private int lon;
	
	private String city;
	
	private String networkType;
	
	private String operatorType;

	public String getApiName() {
		return apiName;
	}

	public void setApiName(String apiName) {
		this.apiName = apiName;
	}

	public int getReqTimeLength() {
		return reqTimeLength;
	}

	public void setReqTimeLength(int reqTimeLength) {
		this.reqTimeLength = reqTimeLength;
	}

	public int getResultCode() {
		return resultCode;
	}

	public void setResultCode(int resultCode) {
		this.resultCode = resultCode;
	}

	public Date getReqTime() {
		return reqTime;
	}

	public void setReqTime(Date reqTime) {
		this.reqTime = reqTime;
	}

	public int getLat() {
		return lat;
	}

	public void setLat(int lat) {
		this.lat = lat;
	}

	public int getLon() {
		return lon;
	}

	public void setLon(int lon) {
		this.lon = lon;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getNetworkType() {
		return networkType;
	}

	public void setNetworkType(String networkType) {
		this.networkType = networkType;
	}

	public String getOperatorType() {
		return operatorType;
	}

	public void setOperatorType(String operatorType) {
		this.operatorType = operatorType;
	}
	

	
}
