/**
 * 
 */
package com.bloomlife.videoapp.model.result;

import com.bloomlife.android.bean.ProcessResult;

/**
 * @author <a href="mailto:xiai.fei@gmail.com">xiai_fei</a>  
 *
 * @date 2014-11-14  上午11:31:17
 */
public class UploadVideoResult extends ProcessResult{

	private String videoid ;
	
	private String videouri;
	
	private long createtime;

	public String getVideoid() {
		return videoid;
	}

	public void setVideoid(String videoid) {
		this.videoid = videoid;
	}

	
	public String getVideouri() {
		return videouri;
	}

	public void setVideouri(String videouri) {
		this.videouri = videouri;
	}

	public long getCreatetime() {
		return createtime;
	}

	public void setCreatetime(long createtime) {
		this.createtime = createtime;
	}

}
