/**
 * 
 */
package com.bloomlife.videoapp.model.message;

import com.bloomlife.android.bean.BaseMessage;

/**
 * @author <a href="mailto:lan4627@gmail.com">zhengxingtian</a>
 *
 * @date 2015年2月6日 下午3:18:30
 */
public class GetNotificationListMessage extends BaseMessage {
	
	private String lastmsgtime;
	
	public GetNotificationListMessage (int pageNum, int pageSize, String lastTime){
		setMsgCode("3009");
		setPageNum(pageNum);
		setPageSize(pageSize);
		lastmsgtime = lastTime;
	}

	public String getLastmsgtime() {
		return lastmsgtime;
	}

	public void setLastmsgtime(String lastmsgtime) {
		this.lastmsgtime = lastmsgtime;
	}
	
	
}
