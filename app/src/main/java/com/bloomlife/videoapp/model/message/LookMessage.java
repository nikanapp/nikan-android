/**
 * 
 */
package com.bloomlife.videoapp.model.message;

import com.bloomlife.android.bean.BaseMessage;

/**
 * @author <a href="mailto:lan4627@gmail.com">zxt</a>
 *
 * @date 2015年2月4日 下午3:25:20
 */
public class LookMessage extends BaseMessage {
	
	private String videoId;
	
	public LookMessage(){
		setMsgCode("3029");
	}
	
	public LookMessage(String videoId){
		setMsgCode("3029");
		this.videoId = videoId;
	}

	public String getVideoId() {
		return videoId;
	}

	public void setVideoId(String videoId) {
		this.videoId = videoId;
	}
}	
