/**
 * 
 */
package com.bloomlife.videoapp.model;

/**
 * 	环信聊天文本实体。  因为需要在聊天的时候携带更多的信息，所以发送的聊天文本内容实际上是json。当前这个类是json对应的类实体
 * 
 * @author <a href="mailto:xiai.fei@gmail.com">xiai_fei</a>  
 *
 * @date 2014-12-19  上午11:21:15
 */
public class ChatTextEntity {

	private String previedUrl ;
	
	private String videoId;
	
	private String videoUri ;
	
	private String otherId;
	
	private String content ;

	public String getPreviedUrl() {
		return previedUrl;
	}

	public void setPreviedUrl(String previedUrl) {
		this.previedUrl = previedUrl;
	}

	public String getVideoId() {
		return videoId;
	}

	public void setVideoId(String videoId) {
		this.videoId = videoId;
	}

	public String getOtherId() {
		return otherId;
	}

	public void setOtherId(String otherId) {
		this.otherId = otherId;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getVideoUri() {
		return videoUri;
	}

	public void setVideoUri(String videoUri) {
		this.videoUri = videoUri;
	}

	
}
