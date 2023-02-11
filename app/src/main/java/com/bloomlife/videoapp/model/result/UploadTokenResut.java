/**
 * 
 */
package com.bloomlife.videoapp.model.result;

import com.bloomlife.android.bean.ProcessResult;

/**
 * @author <a href="mailto:xiai.fei@gmail.com">xiai_fei</a>  
 *
 * @date 2014-11-13  下午7:02:07
 */
public class UploadTokenResut extends ProcessResult{
	

	private String uploadtoken;
	
	private String filekey ; //上传到云存储的文件的key

	public String getUploadtoken() {
		return uploadtoken;
	}

	public void setUploadtoken(String uploadtoken) {
		this.uploadtoken = uploadtoken;
	}

	public String getFilekey() {
		return filekey;
	}

	public void setFilekey(String filekey) {
		this.filekey = filekey;
	}
}
