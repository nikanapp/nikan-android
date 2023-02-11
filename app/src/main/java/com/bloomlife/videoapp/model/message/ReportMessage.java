/**
 * 
 */
package com.bloomlife.videoapp.model.message;

import com.bloomlife.android.bean.BaseMessage;

/**
 * @author <a href="mailto:xiai.fei@gmail.com">xiai_fei</a>  
 *
 * @date 2014-12-11  下午5:09:42
 */
public class ReportMessage extends BaseMessage{

	/**
	 * @param reporType
	 * @param reportId
	 * @param reportVideoType
	 */
	public ReportMessage( String reportId, int reporType, int contentType) {
		super();
		setMsgCode("3013");
		this.reportId = reportId;
		this.reporType = reporType;
		this.contentType = contentType;
	}
	
	public ReportMessage(){
		super();
		setMsgCode("3013");
	}

	private int reporType;
	
	private String reportId;
	
	private int contentType;
	
	public int getReporType() {
		return reporType;
	}

	public void setReporType(int reporType) {
		this.reporType = reporType;
	}

	public String getReportId() {
		return reportId;
	}

	public void setReportId(String reportId) {
		this.reportId = reportId;
	}

	public int getContentType() {
		return contentType;
	}

	public void setContentType(int contentType) {
		this.contentType = contentType;
	}
	
}
