/**
 * 
 */
package com.bloomlife.videoapp.model.message;

import com.bloomlife.android.bean.BaseMessage;

/**
 * @author <a href="mailto:lan4627@gmail.com">zxt</a>
 *
 * @date 2015年4月13日 下午2:31:17
 */
public class UpdateUserSexMessage extends BaseMessage {
	
	private int sex;

	public UpdateUserSexMessage(int sex) {
		setMsgCode("3038");
		this.sex = sex;
	}

	public int getSex() {
		return sex;
	}

	public void setSex(int sex) {
		this.sex = sex;
	}
	
}
