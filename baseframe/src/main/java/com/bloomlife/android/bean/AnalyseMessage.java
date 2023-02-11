/**
 * 
 */
package com.bloomlife.android.bean;

import com.bloomlife.android.framework.AppContext;


/**
 *   
 *
 * @author <a href="mailto:xiai.fei@gmail.com">xiai_fei</a>  
 *
 * @date 2015-6-15 下午2:58:07
 *
 * @organization bloomlife  
 * @version 1.0
 */
public class AnalyseMessage extends BaseMessage{
	
	public static final String MsgCode = "9000";
	
	public AnalyseMessage(){
		this.setMsgCode(MsgCode);
		this.setSystemver(AppContext.deviceInfo.systemVersion);
		this.setMobiletype(AppContext.deviceInfo.phoneType);
	}
	
	
	/**
	 * @param records
	 */
	public AnalyseMessage(String records) {
		this();
		this.setRecords(records);
	}

//
//	public List<AnalyseRecord> getRecords() {
//		return records;
//	}
//
//	public void setRecords(List<AnalyseRecord> records) {
//		this.records = records;
//	}

	private String systemver;
	
	private String mobiletype ;
	
	public String getRecords() {
		return records;
	}



	public void setRecords(String records) {
		this.records = records;
	}

	private String records;

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
	
	
	
	
}
