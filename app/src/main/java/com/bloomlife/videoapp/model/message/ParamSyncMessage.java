/**
 * 
 */
package com.bloomlife.videoapp.model.message;

import com.bloomlife.android.bean.BaseMessage;
import com.bloomlife.videoapp.app.AppContext;

/**
 * 	参数同步报文
 * 
 * @author <a href="mailto:xiai.fei@gmail.com">xiai_fei</a>  
 *
 * @date 2014-12-5  上午10:49:18
 */
public class ParamSyncMessage extends BaseMessage{
	
	private String imei;
	
	public ParamSyncMessage(){
		setMsgCode("3001");
		this.imei = AppContext.deviceInfo.iMEIID;
	}
	
	public String getImei() {
		return imei;
	}

	public void setImei(String imei) {
		this.imei = imei;
	}
}
