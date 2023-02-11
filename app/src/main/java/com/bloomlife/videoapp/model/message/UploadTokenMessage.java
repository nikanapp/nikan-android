/**
 * 
 */
package com.bloomlife.videoapp.model.message;

import com.bloomlife.android.bean.BaseMessage;

/**
 * @author <a href="mailto:xiai.fei@gmail.com">xiai_fei</a>  
 *
 * @date 2014-11-13  下午7:04:39
 */
public class UploadTokenMessage extends BaseMessage {


	public static final int TYPE_UPLOAD_IMAGE = 1;
	private Integer rotate;
	private Integer type;


	public UploadTokenMessage(Integer rotate, Integer type){
		super();
		setMsgCode("3017");
		this.rotate = rotate;
		this.type = type;
	}

	public Integer getRotate() {
		return rotate;
	}

	public void setRotate(Integer rotate) {
		this.rotate = rotate;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}
}
