/**
 * 
 */
package com.bloomlife.videoapp.model.message;

import com.bloomlife.android.bean.BaseMessage;

/**
 *   终端异常调试信息上传报文
 *
 * @author <a href="mailto:xiai.fei@gmail.com">xiai_fei</a>  
 *
 * @date 2015-3-9 下午5:32:36
 *
 * @organization bloomlife  
 * @version 1.0
 */
public class DebugMessage extends BaseMessage{
	
	public DebugMessage(){
		setMsgCode("3033");
	} 
	
	public DebugMessage(String type){
		this();
		this.type = type ;
	}

	private String systemver;
	
	private String mobiletype;
	
	private int screenwidth;
	
	private int screenheight;
	
	private String type ;
	
	private String content ;

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

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
	
	
}
