/**
 * 
 */
package com.bloomlife.videoapp.model.message;

import com.bloomlife.android.bean.BaseMessage;

/**
 * @author <a href="mailto:lan4627@gmail.com">zxt</a>
 * 
 * @date 2015年1月30日 下午6:06:26
 */
public class SendCommentTextMessage extends BaseMessage {
	private String videoId;
	private String content;

	public SendCommentTextMessage() {
		setMsgCode("3025");
	}

	/**
	 * @return the videoId
	 */
	public String getVideoId() {
		return videoId;
	}

	/**
	 * @param videoId
	 *            the videoId to set
	 */
	public void setVideoId(String videoId) {
		this.videoId = videoId;
	}

	/**
	 * @return the content
	 */
	public String getContent() {
		return content;
	}

	/**
	 * @param content
	 *            the content to set
	 */
	public void setContent(String content) {
		this.content = content;
	}

}
