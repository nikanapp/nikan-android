package com.bloomlife.videoapp.model.result;

import java.util.List;

import com.bloomlife.android.bean.ProcessResult;

public class SynchronicResult extends ProcessResult {
	
	private int userid;
	private int maxlevel;
	private int minlevel;
	private int defaultlevel;
	private List<String> topics;
	
	private String uploadtoken ; 
	
	public int getUserid() {
		return userid;
	}
	public void setUserid(int userid) {
		this.userid = userid;
	}
	public int getMaxlevel() {
		return maxlevel;
	}
	public void setMaxlevel(int maxlevel) {
		this.maxlevel = maxlevel;
	}
	public int getMinlevel() {
		return minlevel;
	}
	public void setMinlevel(int minlevel) {
		this.minlevel = minlevel;
	}
	public int getDefaultlevel() {
		return defaultlevel;
	}
	public void setDefaultlevel(int defaultlevel) {
		this.defaultlevel = defaultlevel;
	}
	public List<String> getTopics() {
		return topics;
	}
	public void setTopics(List<String> topics) {
		this.topics = topics;
	}
	public String getUploadtoken() {
		return uploadtoken;
	}
	public void setUploadtoken(String uploadtoken) {
		this.uploadtoken = uploadtoken;
	}
	
}
