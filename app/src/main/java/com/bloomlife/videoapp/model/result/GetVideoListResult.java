package com.bloomlife.videoapp.model.result;

import java.util.List;

import com.bloomlife.android.bean.ProcessResult;
import com.bloomlife.videoapp.model.Video;
/**
 * 热点视频result
 * @author <a href="mailto:xiai.fei@gmail.com">xiai_fei</a>  
 *
 * @date 2014-12-5  下午9:55:21
 */
public class GetVideoListResult extends ProcessResult {
	
	private List<Video> videos;
	private int minindex;
	private Video video;
	
	public List<Video> getVideos() {
		return videos;
	}
	public void setVideos(List<Video> videos) {
		this.videos = videos;
	}

	public int getMinindex() {
		return minindex;
	}

	public void setMinindex(int minindex) {
		this.minindex = minindex;
	}

	public Video getVideo() {
		return video;
	}

	public void setVideo(Video video) {
		this.video = video;
	}
	
}
