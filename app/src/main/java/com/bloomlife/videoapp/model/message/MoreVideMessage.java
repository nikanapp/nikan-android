/**
 * 
 */
package com.bloomlife.videoapp.model.message;

import com.bloomlife.android.bean.BaseMessage;
import com.bloomlife.android.common.util.StringUtils;
import com.bloomlife.videoapp.model.MyLatLng;

/**
 * 	获取更多视频的网格点和话题报文
 * 
 * @author <a href="mailto:xiai.fei@gmail.com">xiai_fei</a>  
 *
 * @date 2014-12-5  下午2:21:43
 */
public class MoreVideMessage extends BaseMessage{

	public MoreVideMessage(MyLatLng mBottomLeft ,MyLatLng topRight){
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
	private String scale;
	
	private boolean ismaxlayer;
	
	
	public MoreVideMessage(){
		setMsgCode("3018");
		scale = "0";
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

	public String getTopic() {
		return topic;
	}


	public void setTopic(String topic) {
		this.topic = topic;
	}

	public String getScale() {
		return scale;
	}


	public void setScale(String scale) {
		this.scale = scale;
	}
	
	public void setScale(int scale) {
		this.scale = String.valueOf(scale);
	}

	public boolean isIsmaxlayer() {
		return ismaxlayer;
	}

	public void setIsmaxlayer(boolean ismaxlayer) {
		this.ismaxlayer = ismaxlayer;
	}
}
