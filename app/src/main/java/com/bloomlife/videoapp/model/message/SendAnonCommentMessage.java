/**
 * 
 */
package com.bloomlife.videoapp.model.message;

import com.bloomlife.android.bean.BaseMessage;

/**
 * @author <a href="mailto:lan4627@gmail.com">zxt</a>
 *
 * @date 2014年12月4日 下午12:18:03
 */
public class SendAnonCommentMessage extends BaseMessage {

	private String videoId;
	private String commenttagId;
	
	public SendAnonCommentMessage(String videoId, String commenttagId){
		setMsgCode("3007");
		this.videoId = videoId;
		this.commenttagId = commenttagId;
	}
	
	public String getVideoId() {
		return videoId;
	}

	public void setVideoId(String videoId) {
		this.videoId = videoId;
	}

	public String getCommenttagId() {
		return commenttagId;
	}

	public void setCommenttagId(String commenttagId) {
		this.commenttagId = commenttagId;
	}

}
