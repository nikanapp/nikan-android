package com.bloomlife.videoapp.model.message;

import com.bloomlife.android.bean.BaseMessage;
/**
 * 
 * @author xiai_fei
 *
 */
public class CheckMsg extends BaseMessage{
	public CheckMsg(String code){
		this.setCode(code) ;
		setMsgCode("3023");
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	private String code ; 
}