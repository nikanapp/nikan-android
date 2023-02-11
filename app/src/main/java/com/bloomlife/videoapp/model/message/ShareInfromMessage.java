/**
 * 
 */
package com.bloomlife.videoapp.model.message;

import com.bloomlife.android.bean.BaseMessage;

/**
 *   	分享通知到后台的message
 *
 * @author <a href="mailto:xiai.fei@gmail.com">xiai_fei</a>  
 *
 * @date 2015-2-16 上午11:37:57
 *
 * @organization bloomlife  
 * @version 1.0
 */
public class ShareInfromMessage extends BaseMessage{
	
	public ShareInfromMessage(){
		this.setMsgCode("3031");
	}

	private String videoid ;
	
	private int width ;
	
	private int height ;
	
	private Integer rotate ;

	public String getVideoid() {
		return videoid;
	}

	public void setVideoid(String videoid) {
		this.videoid = videoid;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public Integer getRotate() {
		return rotate;
	}

	public void setRotate(Integer rotate) {
		this.rotate = rotate;
	}
	
	
}
