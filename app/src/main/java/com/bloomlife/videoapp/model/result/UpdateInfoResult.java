/**
 * 
 */
package com.bloomlife.videoapp.model.result;

import java.util.List;

import com.bloomlife.android.bean.ProcessResult;
import com.bloomlife.videoapp.model.Video;

/**
 * @author <a href="mailto:lan4627@gmail.com">zxt</a>
 * 
 * @date 2014年11月24日 下午8:36:07
 */
public class UpdateInfoResult extends ProcessResult {

	private int pagesize;
	private int pagenum;
	private List<Video> videos;

	public int getPagesize() {
		return pagesize;
	}

	public void setPagesize(int pagesize) {
		this.pagesize = pagesize;
	}

	public int getPagenum() {
		return pagenum;
	}

	public void setPagenum(int pagenum) {
		this.pagenum = pagenum;
	}

	public List<Video> getVideos() {
		return videos;
	}

	public void setVideos(List<Video> list) {
		this.videos = list;
	}

}
