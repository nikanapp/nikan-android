/**
 * 
 */
package com.bloomlife.android.bean;

import java.util.Date;

import net.tsz.afinal.annotation.sqlite.Id;
import net.tsz.afinal.annotation.sqlite.Table;
import android.content.Context;

import com.alibaba.fastjson.annotation.JSONField;
import com.bloomlife.android.common.CacheKeyConstants;
import com.bloomlife.android.framework.AppContext;

/**
 *    前端数据统计记录实体
 *
 * @author <a href="mailto:xiai.fei@gmail.com">xiai_fei</a>  
 *
 * @date 2015-6-15 上午11:07:28
 *
 * @organization bloomlife  
 * @version 1.0
 */
@Table(name="tb_analyse")
public class AnalyseRecord {
	
	public static final String HTTP_ERROR = "-1" ;
	
	public AnalyseRecord() {}

	public AnalyseRecord(String apiName) {
		super();
		this.apiName = apiName;
		this.reqTime = new Date();
		this.setNetworkType(AppContext.getNetWorkType());

	}
	
	public AnalyseRecord(String apiName,Context context) {
		this(apiName);
		if(null != context){
			try {
				this.lat = CacheBean.getInstance().getString(context, CacheKeyConstants.LOCATION_LAT);
				this.lon = CacheBean.getInstance().getString(context, CacheKeyConstants.LOCATION_LAT);
				this.city = CacheBean.getInstance().getString(context, CacheKeyConstants.LOCATION_CITY);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public AnalyseRecord(String apiName, String lat, String lon, String city) {
		this(apiName);
		this.lat = lat;
		this.lon = lon;
		this.city = city;
	}

	@Id
	private Integer id ;
	
	@JSONField(name="a")
	private String apiName ;
	
	@JSONField(name="rtl")
	private int reqTimeLength;
	
	@JSONField(name="rc")
	private String resultCode;
	
	@JSONField(name="rt")
	private Date reqTime ;
	
	@JSONField(name="nt")
	private int networkType;
	
	private String lat ;
	
	private String lon;
	
	private String city;
	
	public static final int STATUS_UOLOADED = 1 ;
	public static final int STATUS_NOT_UOLOAD = 0 ;
	
	//用于标记是否需要已经上传
	@JSONField(serialize=false)
	private int status ;
	
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



	public String getResultCode() {
		return resultCode;
	}

	public void setResultCode(String resultCode) {
		this.resultCode = resultCode;
	}

	public Date getReqTime() {
		return reqTime;
	}

	public void setReqTime(Date reqTime) {
		this.reqTime = reqTime;
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

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public int getNetworkType() {
		return networkType;
	}

	public void setNetworkType(int networkType) {
		this.networkType = networkType;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}


	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}


}
