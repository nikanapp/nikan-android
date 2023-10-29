/**
 * 
 */
package com.bloomlife.videoapp.model.message;

import com.bloomlife.android.bean.BaseMessage;

/**
 * @author <a href="mailto:lan4627@gmail.com">zxt</a>
 *
 * @date 2014年11月24日 下午8:28:37
 */
public class MyVideoInfoMessage extends BaseMessage{
	
	private long createtime;
	
	public MyVideoInfoMessage(){
		setMsgCode("3011");
	}
	
	public long getCreatetime() {
		return createtime;
	}

	public void setCreatetime(long createtime) {
		this.createtime = createtime;
	}

}
