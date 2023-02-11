/**
 * 
 */
package com.bloomlife.videoapp.model.message;

import com.bloomlife.android.bean.BaseMessage;

/**
 * @author <a href="mailto:lan4627@gmail.com">zhengxingtian</a>
 *
 * @date 2015年4月17日 上午11:42:02
 */
public class ClearDynamicImgMessage extends BaseMessage {

	private String videoid;
	
	public ClearDynamicImgMessage(String videoid) {
		setMsgCode("3037");
		this.videoid = videoid;
	}

	/**
	 * @return the videoid
	 */
	public String getVideoid() {
		return videoid;
	}

	/**
	 * @param videoid the videoid to set
	 */
	public void setVideoid(String videoid) {
		this.videoid = videoid;
	}

	
}
