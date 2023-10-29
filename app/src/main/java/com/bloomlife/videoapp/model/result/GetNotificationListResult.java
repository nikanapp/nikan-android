/**
 * 
 */
package com.bloomlife.videoapp.model.result;

import java.util.List;

import com.bloomlife.android.bean.ProcessResult;
import com.bloomlife.videoapp.model.NotificationMessage;

/**
 * @author <a href="mailto:lan4627@gmail.com">zxt</a>
 * 
 * @date 2015年2月6日 下午3:25:54
 */
public class GetNotificationListResult extends ProcessResult {
	
	private int pagesize;
	private int pagenum;
	private List<NotificationMessage> list;
	private int notreadnum;

	/**
	 * @return the pagesize
	 */
	public int getPagesize() {
		return pagesize;
	}

	/**
	 * @param pagesize
	 *            the pagesize to set
	 */
	public void setPagesize(int pagesize) {
		this.pagesize = pagesize;
	}

	/**
	 * @return the pagenum
	 */
	public int getPagenum() {
		return pagenum;
	}

	/**
	 * @param pagenum
	 *            the pagenum to set
	 */
	public void setPagenum(int pagenum) {
		this.pagenum = pagenum;
	}

	/**
	 * @return the list
	 */
	public List<NotificationMessage> getList() {
		return list;
	}

	/**
	 * @param list
	 *            the list to set
	 */
	public void setList(List<NotificationMessage> list) {
		this.list = list;
	}

	/**
	 * @return the notreadnum
	 */
	public int getNotreadnum() {
		return notreadnum;
	}

	/**
	 * @param notreadnum the notreadnum to set
	 */
	public void setNotreadnum(int notreadnum) {
		this.notreadnum = notreadnum;
	}
}
