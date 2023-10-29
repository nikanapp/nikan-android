/**
 * 
 */
package com.bloomlife.videoapp.model.message;

import com.bloomlife.android.bean.BaseMessage;

/**
 * @author <a href="mailto:lan4627@gmail.com">zxt</a>
 *
 * @date 2014年12月4日 上午11:57:58
 */
public class GetCommentListMessage extends BaseMessage {

	private int videoId;
	private int pagesize;
	private int pagenum;
	
	public GetCommentListMessage(){
		setMsgCode("3006");
	}
	
	public int getVideoId() {
		return videoId;
	}
	public void setVideoId(int videoId) {
		this.videoId = videoId;
	}

	public int getPagesize() {
		return pagesize;
	}
	public void setPagesize(int pagesize) {
		this.pagesize = pagesize;
	}
	public int getPagenum() {
		return pagenum;
	}
	public void setPagenum(int pagenum) {
		this.pagenum = pagenum;
	}
}
