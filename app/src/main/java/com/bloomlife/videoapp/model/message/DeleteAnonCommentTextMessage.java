/**
 * 
 */
package com.bloomlife.videoapp.model.message;

import com.bloomlife.android.bean.BaseMessage;

/**
 * @author <a href="mailto:lan4627@gmail.com">zxt</a>
 * 
 * @date 2015年1月30日 下午8:06:22
 */
public class DeleteAnonCommentTextMessage extends BaseMessage {

	private String videoId;
	private long cid;

	public DeleteAnonCommentTextMessage(String videoId, long cid) {
		setMsgCode("3026");
		this.videoId = videoId;
		this.cid = cid;
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
	 * @return the cid
	 */
	public long getCid() {
		return cid;
	}

	/**
	 * @param cid
	 *            the cid to set
	 */
	public void setCid(long cid) {
		this.cid = cid;
	}

}
