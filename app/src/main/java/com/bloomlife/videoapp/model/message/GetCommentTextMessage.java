/**
 * 
 */
package com.bloomlife.videoapp.model.message;

import com.bloomlife.android.bean.BaseMessage;

/**
 * @author <a href="mailto:lan4627@gmail.com">zxt</a>
 *
 * @date 2015年2月5日 下午12:16:59
 */
public class GetCommentTextMessage extends BaseMessage {
	
	private String videoId;
	
	public GetCommentTextMessage(){
		setMsgCode("3027");
	}
	
	public GetCommentTextMessage(String videoId, int pageSize, int pageNum){
		setMsgCode("3027");
		this.videoId = videoId;
		setPageSize(pageSize);
		setPageNum(pageNum);
	}

	/**
	 * @return the videoId
	 */
	public String getVideoId() {
		return videoId;
	}

	/**
	 * @param videoId the videoId to set
	 */
	public void setVideoId(String videoId) {
		this.videoId = videoId;
	}
}
