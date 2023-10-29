/**
 * 
 */
package com.bloomlife.videoapp.model.result;

import java.util.List;

import com.bloomlife.android.bean.ProcessResult;

/**
 * @author <a href="mailto:lan4627@gmail.com">zxt</a>
 * 
 * @date 2015年2月7日 下午4:47:33
 */
public class RandomVideoResult extends ProcessResult {
	
	private String videoid;
	private int looknum;
	private int likenum;
	private long createtime;
	private List<String> topics;
	private String description;
	private String videouri;
	private String previewurl;
	private double lat;
	private double lon;
	private String uid;

	/**
	 * @return the videoid
	 */
	public String getVideoid() {
		return videoid;
	}

	/**
	 * @param videoid
	 *            the videoid to set
	 */
	public void setVideoid(String videoid) {
		this.videoid = videoid;
	}

	/**
	 * @return the looknum
	 */
	public int getLooknum() {
		return looknum;
	}

	/**
	 * @param looknum
	 *            the looknum to set
	 */
	public void setLooknum(int looknum) {
		this.looknum = looknum;
	}

	/**
	 * @return the likenum
	 */
	public int getLikenum() {
		return likenum;
	}

	/**
	 * @param likenum
	 *            the likenum to set
	 */
	public void setLikenum(int likenum) {
		this.likenum = likenum;
	}

	/**
	 * @return the createtime
	 */
	public long getCreatetime() {
		return createtime;
	}

	/**
	 * @param createtime
	 *            the createtime to set
	 */
	public void setCreatetime(long createtime) {
		this.createtime = createtime;
	}

	/**
	 * @return the topics
	 */
	public List<String> getTopics() {
		return topics;
	}

	/**
	 * @param topics
	 *            the topics to set
	 */
	public void setTopics(List<String> topics) {
		this.topics = topics;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description
	 *            the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the videouri
	 */
	public String getVideouri() {
		return videouri;
	}

	/**
	 * @param videouri
	 *            the videouri to set
	 */
	public void setVideouri(String videouri) {
		this.videouri = videouri;
	}

	/**
	 * @return the previewurl
	 */
	public String getPreviewurl() {
		return previewurl;
	}

	/**
	 * @param previewurl
	 *            the previewurl to set
	 */
	public void setPreviewurl(String previewurl) {
		this.previewurl = previewurl;
	}

	/**
	 * @return the lat
	 */
	public double getLat() {
		return lat;
	}

	/**
	 * @param lat
	 *            the lat to set
	 */
	public void setLat(double lat) {
		this.lat = lat;
	}

	/**
	 * @return the lon
	 */
	public double getLon() {
		return lon;
	}

	/**
	 * @param lon
	 *            the lon to set
	 */
	public void setLon(double lon) {
		this.lon = lon;
	}

	/**
	 * @return the uid
	 */
	public String getUid() {
		return uid;
	}

	/**
	 * @param uid
	 *            the uid to set
	 */
	public void setUid(String uid) {
		this.uid = uid;
	}

}
