/**
 * 
 */
package com.bloomlife.videoapp.model;

import java.io.Serializable;

/**
 * @author <a href="mailto:xiai.fei@gmail.com">xiai_fei</a>  
 *
 * @date 2014-11-25  下午12:04:10
 */
public class VideoProgress implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4554440947824855313L;

	public static final int VIDEO = 1;
	public static final int STORY_VIDEO = 2;
	
	/**
	 * @param fileKey
	 * @param id
	 * @param progress
	 */
	public VideoProgress(Integer id, double progress, String serverVideoId, String fileKey, int type) {
		super();
		this.id = id;
		this.progress = progress;
		this.serverVideoId = serverVideoId;
		this.fileKey = fileKey;
		this.type = type;
	}

	private String fileKey ;
	
	private Integer id ;
	
	private double progress;
	
	private String serverVideoId;

	private int type;

	public String getFileKey() {
		return fileKey;
	}

	public void setFileKey(String fileKey) {
		this.fileKey = fileKey;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public double getProgress() {
		return progress;
	}

	public void setProgress(double progress) {
		this.progress = progress;
	}

	public String getServerVideoId() {
		return serverVideoId;
	}

	public void setServerVideoId(String serverVideoId) {
		this.serverVideoId = serverVideoId;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}


	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}
}
