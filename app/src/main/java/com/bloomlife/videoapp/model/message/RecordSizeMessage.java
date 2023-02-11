package com.bloomlife.videoapp.model.message;

import java.util.ArrayList;
import java.util.List;

import android.hardware.Camera.Size;

import com.bloomlife.android.bean.BaseMessage;
import com.bloomlife.videoapp.app.AppContext;

/***
 * 通知到后台，这个机型支持的拍摄分辨率和所选择的拍摄分辨率
 * @author ever
 *
 */
public class RecordSizeMessage extends BaseMessage{
	
	public RecordSizeMessage(){
		setMsgCode("3034");
		this.systemver = AppContext.deviceInfo.systemVersion;
		this.mobiletype = AppContext.deviceInfo.phoneType;
		this.screenwidth = AppContext.deviceInfo.getScreenWidth();
		this.screenheight = AppContext.deviceInfo.getScreenHeight();
	}
	
	public RecordSizeMessage(List<Size> supportSize ,Size chooseSize){
		this();
		this.supportSize  = "";
		for (Size size : supportSize) {
			this.supportSize =this.supportSize+size.width+"|"+size.height+",";
		}
		this.supportSize = this.supportSize.substring(0, this.supportSize.length()-1);
		this.setSize(chooseSize.width+","+chooseSize.height);
	}
	

	private String systemver;
	
	private String mobiletype;
	
	private int screenwidth;
	
	private int screenheight;
	
	private String size ;
	
	private String  supportSize ;
	
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

	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}


}
