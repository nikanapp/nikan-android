package com.bloomlife.videoapp.model.result;

import java.util.List;
import java.util.Map;

import com.bloomlife.android.bean.ProcessResult;
import com.bloomlife.videoapp.common.CommentText;
import com.bloomlife.videoapp.model.Comment;
import com.bloomlife.videoapp.model.Dynamicimg;

public class GetVideoResult extends ProcessResult {
	private List<CommentText> commentTexts;
	private int allowcomment;
	private List<Comment> list;
	private String commentid;
	private String videoid;
	private int looknum;
	private int likenum;
	private long createtime;
	private double lat;
	private double lon;
	private String description;
	private String videouri;
	private String previewurl;
	private String city;
	private String uid;
	private boolean like;
	private int commentnum;
	private String waterurl;
	private Map<Double, List<Dynamicimg>> dynamicimgs;

	private int times;
	
	public List<Comment> getList() {
		return list;
	}

	public void setList(List<Comment> list) {
		this.list = list;
	}

	public String getCommentid() {
		return commentid;
	}

	public void setCommentid(String commentid) {
		this.commentid = commentid;
	}

	public String getVideoid() {
		return videoid;
	}

	public void setVideoid(String videoid) {
		this.videoid = videoid;
	}

	public int getLooknum() {
		return looknum;
	}

	public void setLooknum(int looknum) {
		this.looknum = looknum;
	}

	public int getLikenum() {
		return likenum;
	}

	public void setLikenum(int likenum) {
		this.likenum = likenum;
	}



	public long getCreatetime() {
		return createtime;
	}

	public void setCreatetime(long createtime) {
		this.createtime = createtime;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getVideouri() {
		return videouri;
	}

	public void setVideouri(String videouri) {
		this.videouri = videouri;
	}

	public String getPreviewurl() {
		return previewurl;
	}

	public void setPreviewurl(String previewurl) {
		this.previewurl = previewurl;
	}

	public boolean isLike() {
		return like;
	}

	public void setLike(boolean like) {
		this.like = like;
	}

	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public double getLon() {
		return lon;
	}

	public void setLon(double lon) {
		this.lon = lon;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	/**
	 * @return the commentTexts
	 */
	public List<CommentText> getCommentTexts() {
		return commentTexts;
	}

	/**
	 * @param commentTexts the commentTexts to set
	 */
	public void setCommentTexts(List<CommentText> commentTexts) {
		this.commentTexts = commentTexts;
	}

	/**
	 * @return the allowcomment
	 */
	public int getAllowcomment() {
		return allowcomment;
	}

	/**
	 * @param allowcomment the allowcomment to set
	 */
	public void setAllowcomment(int allowcomment) {
		this.allowcomment = allowcomment;
	}

	/**
	 * @return the uid
	 */
	public String getUid() {
		return uid;
	}

	/**
	 * @param uid the uid to set
	 */
	public void setUid(String uid) {
		this.uid = uid;
	}

	/**
	 * @return the commentnum
	 */
	public int getCommentnum() {
		return commentnum;
	}

	/**
	 * @param commentnum the commentnum to set
	 */
	public void setCommentnum(int commentnum) {
		this.commentnum = commentnum;
	}

	/**
	 * @return the waterurl
	 */
	public String getWaterurl() {
		return waterurl;
	}

	/**
	 * @param waterurl the waterurl to set
	 */
	public void setWaterurl(String waterurl) {
		this.waterurl = waterurl;
	}

	/**
	 * @return the dynamicimgs
	 */
	public Map<Double, List<Dynamicimg>> getDynamicimgs() {
		return dynamicimgs;
	}

	/**
	 * @param dynamicimgs the dynamicimgs to set
	 */
	public void setDynamicimgs(Map<Double, List<Dynamicimg>> dynamicimgs) {
		this.dynamicimgs = dynamicimgs;
	}

	public int getTimes() {
		return times;
	}

	public void setTimes(int times) {
		this.times = times;
	}

}
