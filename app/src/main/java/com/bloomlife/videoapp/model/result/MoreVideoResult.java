/**
 * 
 */
package com.bloomlife.videoapp.model.result;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import net.tsz.afinal.annotation.sqlite.Transient;

import org.apache.commons.httpclient.methods.GetMethod;

import android.graphics.Point;
import android.graphics.RectF;

import com.bloomlife.android.bean.ProcessResult;

/**
 * @author <a href="mailto:xiai.fei@gmail.com">xiai_fei</a>  
 *
 * @date 2014-12-5  下午2:33:29
 */
public class MoreVideoResult extends ProcessResult{

	private List<MoreVideoVo> videos;
	
	private List<String> topics;
	
	private int videonum;
	
	public List<MoreVideoVo> getVideos() {
		return videos;
	}

	public void setVideos(List<MoreVideoVo> videos) {
		this.videos = videos;
	}

	public List<String> getTopics() {
		return topics;
	}

	public void setTopics(List<String> topics) {
		this.topics = topics;
	}

	public class MoreVideoVo implements Serializable{

		private String description;
		private String topic;
		private String videoid;
		private String lat;
		private String lon;
		private String uid;
		
		private String videouri;
		
		private boolean look;
		
		@Transient
		private RectF rectF = new RectF();  //在屏幕中的图标的坐标范围
		
		public String getDescription() {
			return description;
		}
		public void setDescription(String description) {
			this.description = description;
		}
		public String getTopic() {
			return topic;
		}
		public void setTopic(String topic) {
			this.topic = topic;
		}
		public String getVideoid() {
			return videoid;
		}
		public void setVideoid(String videoid) {
			this.videoid = videoid;
		}
		public String getLat() {
			return lat;
		}
		public void setLat(String lat) {
			this.lat = lat;
		}
		public String getLon() {
			return lon;
		}
		public void setLon(String lon) {
			this.lon = lon;
		}
		
		public RectF getRectF() {
			return rectF;
		}

		public void setRectF(RectF rectF) {
			this.rectF = rectF;
		}
		
		public void setRectF(int length ,Point point) {
			this.rectF .set(point.x-length/2, point.y-length, point.x+length/2, point.y);
		}
		public String getVideouri() {
			return videouri;
		}
		public void setVideouri(String videouri) {
			this.videouri = videouri;
		}	
		
		public String getUid() {
			return uid;
		}
		public void setUid(String uid) {
			this.uid = uid;
		}
	}

	public int getVideonum() {
		return videonum;
	}

	public void setVideonum(int videonum) {
		this.videonum = videonum;
	}
	
}
