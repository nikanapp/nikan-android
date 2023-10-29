/**
 * 
 */
package com.bloomlife.videoapp.model.message;

import com.bloomlife.android.bean.BaseMessage;

/**
 * @author <a href="mailto:lan4627@gmail.com">zxt</a>
 * 
 * @date 2015年2月7日 下午3:31:55
 */
public class ChangeNotifiedStatusMessage extends BaseMessage {
	
	private String videoId;
	private String storyid;
	private int operateType;
	private int msgType;
	
	public ChangeNotifiedStatusMessage(String videoId, String storyid, int operateType, int msgType){
		setMsgCode("3010");
		this.videoId = videoId;
		this.storyid = storyid;
		this.operateType = operateType;
		this.msgType = msgType;
	}
	
	public ChangeNotifiedStatusMessage(){
		setMsgCode("3010");
	}

	public int getOperateType() {
		return operateType;
	}

	public void setOperateType(int operateType) {
		this.operateType = operateType;
	}

	public int getMsgType() {
		return msgType;
	}

	public void setMsgType(int msgType) {
		this.msgType = msgType;
	}

	public String getVideoId() {
		return videoId;
	}

	public void setVideoId(String videoId) {
		this.videoId = videoId;
	}

	public String getStoryid() {
		return storyid;
	}

	public void setStoryid(String storyid) {
		this.storyid = storyid;
	}
}
