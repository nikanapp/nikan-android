package com.bloomlife.videoapp.model.message;

import com.bloomlife.android.bean.BaseMessage;
import com.bloomlife.videoapp.model.MyLatLng;
/***
 * 根据经纬度范围获取热点视频列表报文。
 * @author <a href="mailto:xiai.fei@gmail.com">xiai_fei</a>  
 *
 * @date 2014-12-5  下午2:09:21
 */
public class GetVideoListMessage extends BaseMessage {
	
	public GetVideoListMessage(MyLatLng mBottomLeft ,MyLatLng topRight){
		this();
		setLowerleftlat(mBottomLeft.getLat());
		setLowerleftlon(mBottomLeft.getLon());
		setUpperrightlat(topRight.getLat());
		setUpperrightlon(topRight.getLon());
	}
	
	private double lowerleftlat;
	private double upperrightlat;
	private double lowerleftlon;
	private double upperrightlon;
	private String topic;
	private String minindex;
	
	private int scale;
	
	private int maxindex;
	private int batch;
	
	private boolean ismaxlayer;
	
	public GetVideoListMessage(){
		setMsgCode("3002");
		minindex = "0";
	}
	


	public String getTopic() {
		return topic;
	}


	public void setTopic(String topic) {
		this.topic = topic;
	}


	public String getMinindex() {
		return minindex;
	}


	public void setMinindex(String minindex) {
		this.minindex = minindex;
	}


	public int getMaxindex() {
		return maxindex;
	}

	public void setMaxindex(int maxindex) {
		this.maxindex = maxindex;
	}

	public int getBatch() {
		return batch;
	}

	public void setBatch(int batch) {
		this.batch = batch;
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



	public int getScale() {
		return scale;
	}



	public void setScale(int scale) {
		this.scale = scale;
	}



	public boolean isIsmaxlayer() {
		return ismaxlayer;
	}



	public void setIsmaxlayer(boolean ismaxlayer) {
		this.ismaxlayer = ismaxlayer;
	}

	
	
	
}
