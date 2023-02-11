/**
 * 
 */
package com.bloomlife.videoapp.model.message;

import com.bloomlife.android.bean.BaseMessage;

/**
 * @author <a href="mailto:xiai.fei@gmail.com">xiai_fei</a>  
 *
 * @date 2014-8-26  下午4:52:57
 */
public class PushMessage extends BaseMessage{
	
	
	public PushMessage(boolean open  , String deviceToken){
		if(open) status = STATUS_ON;
		else status = STATUS_OFF;
		this.devicetoken =deviceToken;
		this.online = ON_LINE;
		setMsgCode("3015");
	}

	private String platform = "getui";
	private String devicetoken;
	
	
	public static final int STATUS_ON = 0 ;
	/**关闭推送功能**/
	public static final int STATUS_OFF = 1 ;
	
	public static final int ON_LINE = 0;
	public static final int OFF_LINE = 1;
	
	private int status = STATUS_ON;
	
	private int online;

	public String getPlatform() {
		return platform;
	}

	public String getDevicetoken() {
		return devicetoken;
	}

	public void setDevicetoken(String devicetoken) {
		this.devicetoken = devicetoken;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getOnline() {
		return online;
	}

	public void setOnline(int online) {
		this.online = online;
	}
	
}
