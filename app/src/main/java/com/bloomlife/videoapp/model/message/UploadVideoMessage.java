/**
 * 
 */
package com.bloomlife.videoapp.model.message;

import com.alibaba.fastjson.annotation.JSONField;
import com.bloomlife.android.bean.BaseMessage;
import com.bloomlife.android.common.util.StringUtils;
import com.bloomlife.videoapp.app.AppContext;
import com.bloomlife.videoapp.model.Video;

/**
 * @author <a href="mailto:xiai.fei@gmail.com">xiai_fei</a>  
 *
 * @date 2014-11-14  上午10:40:40
 */
public class UploadVideoMessage extends BaseMessage{

	public UploadVideoMessage(){
		super();
		setMsgCode("3004");
		this.systemver = AppContext.deviceInfo.systemVersion;
		this.mobiletype = AppContext.deviceInfo.phoneType;
	}
	
	/**
	 * @param lon
	 * @param lat
	 * @param topic
	 * @param description
	 * @param times
	 */
	public UploadVideoMessage(String lon, String lat, String topic, String description, String city, int times) {
		this();
		this.lon = lon;
		this.lat = lat;
		this.topics = topic;
		this.description = description;
		this.times = times;
		this.city = city;
	}

	private String lon ;
	
	private String lat ;
	
	private String topics;
	
	private String description ;
	
	private int times ;
	
	private String filaPath ;

	private String videokey ; //视频在七牛上的key。
	
	private String previewkey;
	
	private String persistentId;  //用户标识七牛的预览图生成异步任务的id。 
	
	private String city;
	
	private long size;
	
	private String systemver ;
	
	private String mobiletype;
	
	private Integer width ; 
	
	private Integer height ;
	

	public Integer getWidth() {
		return width;
	}

	public void setWidth(Integer width) {
		this.width = width;
	}

	public Integer getHeight() {
		return height;
	}

	public void setHeight(Integer height) {
		this.height = height;
	}

	private Integer rotate ;
	

	@JSONField(serialize=false)
	private Integer id ;
	

	/**
	 *  参见 {@link com.bloomlife.videoapp.model.video} 的status 
	 */
	@JSONField(serialize=false)
	private int status ;

	public String getVideokey() {
		return videokey;
	}

	public void setVideokey(String videokey) {
		this.videokey = videokey;
	}



	public String getPreviewkey() {
		return previewkey;
	}

	public void setPreviewkey(String previewkey) {
		this.previewkey = previewkey;
	}

	public String getLon() {
		return lon;
	}

	public void setLon(String lon) {
		this.lon = lon;
	}

	public String getLat() {
		return lat;
	}

	public void setLat(String lat) {
		this.lat = lat;
	}

	public String getTopics() {
		return topics;
	}

	public void setTopics(String topic) {
		this.topics = topic;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getTimes() {
		return times;
	}

	public void setTimes(int times) {
		this.times = times;
	}

	public String getFilaPath() {
		return filaPath;
	}

	public void setFilaPath(String filaPath) {
		this.filaPath = filaPath;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}
	
	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public static UploadVideoMessage makeByVideo(Video video){
		UploadVideoMessage message = new UploadVideoMessage(
				video.getLon(),
				video.getLat(),
				video.getTopics(),
				video.getDescription(),
				video.getCity(),
				video.getTimes());
		message.setFilaPath(video.getFilaPath());
		message.setPersistentId(video.getPersistentsId());
		message.setSize(video.getSize());
		if(StringUtils.isNotEmpty(video.getWidth())){
			message.setWidth(Integer.parseInt(video.getWidth()));
		}
		if(StringUtils.isNotEmpty(video.getHeight())){
			message.setHeight(Integer.parseInt(video.getHeight()));
		}
		message.setRotate(video.getRotate());
		return message;
	}

	public String getPersistentId() {
		return persistentId;
	}

	public void setPersistentId(String persistentId) {
		this.persistentId = persistentId;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getMobiletype() {
		return mobiletype;
	}

	public void setMobiletype(String mobiletype) {
		this.mobiletype = mobiletype;
	}

	public String getSystemver() {
		return systemver;
	}

	public void setSystemver(String systemver) {
		this.systemver = systemver;
	}

	public Integer getRotate() {
		return rotate;
	}

	public void setRotate(Integer rotate) {
		this.rotate = rotate;
	}
}
