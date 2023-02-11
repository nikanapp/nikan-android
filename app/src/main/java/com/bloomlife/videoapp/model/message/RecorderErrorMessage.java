package com.bloomlife.videoapp.model.message;

import com.bloomlife.android.bean.BaseMessage;
import com.bloomlife.videoapp.app.AppContext;

/**
 * 向后台报告拍摄出错的机型
 * @author ever
 *
 */
public class RecorderErrorMessage extends BaseMessage{

	public RecorderErrorMessage(int videoWidth ,int videoHeight , String content) {
		super();
		setMsgCode("3035");
		this.systemver = AppContext.deviceInfo.systemVersion;
		this.mobiletype = AppContext.deviceInfo.phoneType;
		this.screenwidth = AppContext.deviceInfo.getScreenWidth();
		this.screenheight = AppContext.deviceInfo.getScreenHeight();
		this.videoHeight = videoHeight;
		this.videoWidth = videoWidth;
		this.content = content ;
	} 
	
	private String systemver;
	
	private String mobiletype;
	
	private int screenwidth;
	
	private int screenheight;
	
	/**所选择拍摄的分辨率*/
	private int videoWidth ;
	private int videoHeight ;
	
	private String content ;

	public int getVideoWidth() {
		return videoWidth;
	}

	public void setVideoWidth(int videoWidth) {
		this.videoWidth = videoWidth;
	}

	public int getVideoHeight() {
		return videoHeight;
	}

	public void setVideoHeight(int videoHeight) {
		this.videoHeight = videoHeight;
	}

	public String getSystemver() {
		return systemver;
	}

	public void setSystemver(String systemver) {
		this.systemver = systemver;
	}

	public String getMobiletype() {
		return mobiletype;
	}

	public void setMobiletype(String mobiletype) {
		this.mobiletype = mobiletype;
	}

	public int getScreenwidth() {
		return screenwidth;
	}

	public void setScreenwidth(int screenwidth) {
		this.screenwidth = screenwidth;
	}

	public int getScreenheight() {
		return screenheight;
	}

	public void setScreenheight(int screenheight) {
		this.screenheight = screenheight;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}


	
}
