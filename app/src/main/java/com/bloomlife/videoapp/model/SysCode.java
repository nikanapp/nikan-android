/**
 * 
 */
package com.bloomlife.videoapp.model;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * 	app数据结构
 * 
 * @author <a href="mailto:xiai.fei@gmail.com">xiai_fei</a>  
 *
 * @date 2014-12-5  上午10:32:49
 */
public class SysCode {

	private String userid;
	
	private float maxlevel;
	private float minlevel;
	private float defaultlevel;
	
	private int hotdotnum ; //热点数量的数量
	
	private String uploadtoken;
	
	private String pwd ;
	
	private List<String> topics;
	
	private List<Commenttags> commenttags;
	
	private int moveloadratio = 1;
	
	private int bitrate ;
	
	private int customframerate ; //自定义的帧率
	
	private int marknum ;
	
	private int proportionmale;
	
	private List<Dynamicimg> dynamicimgs;
	
	private int sex;
	
	private int photorate = 80;
	
	private String anaylseswitch = "off";

	private String reauth;

	private List<DymainicMenu> dynamicmenus;
	private List<Emotion> emotions;

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public float getMaxlevel() {
		return maxlevel;
	}
	
	public float getMaxlevel(float defaultValue ) {
		return maxlevel==0?defaultValue:maxlevel;
	}

	public void setMaxlevel(int maxlevel) {
		this.maxlevel = maxlevel;
	}

	public float getMinlevel() {
		return minlevel;
	}
	
	public float getMinlevel(float defaultValue) {
		return minlevel==0?defaultValue : minlevel;
	}

	public void setMinlevel(float minlevel) {
		this.minlevel = minlevel;
	}

	public float getDefaultlevel() {
		return defaultlevel;
	}
	public float getDefaultlevel(float defaultValue) {
		return defaultlevel==0?defaultValue:defaultlevel;
	}

	public void setDefaultlevel(float defaultlevel) {
		this.defaultlevel = defaultlevel;
	}

	public String getUploadtoken() {
		return uploadtoken;
	}

	public void setUploadtoken(String uploadtoken) {
		this.uploadtoken = uploadtoken;
	}

	public List<String> getTopics() {
		return topics==null?new ArrayList<String>():topics;
	}

	public void setTopics(List<String> topics) {
		this.topics = topics;
	}

	public List<Commenttags> getCommenttags() {
		return commenttags==null?new ArrayList<Commenttags>():commenttags;
	}

	public void setCommenttags(List<Commenttags> commenttags) {
		this.commenttags = commenttags;
	}

	public String getPwd() {
		return pwd;
	}

	public void setPwd(String pwd) {
		this.pwd = pwd;
	}

	public int getHotdotnum(int defaultnum) {
		return hotdotnum<=0?defaultnum:hotdotnum;
	}

	public void setHotdotnum(int hotdotnum) {
		this.hotdotnum = hotdotnum;
	}

	/**
	 * @return the moveloadratio
	 */
	public int getMoveloadratio() {
		return moveloadratio;
	}

	/**
	 * @param moveloadratio the moveloadratio to set
	 */
	public void setMoveloadratio(int moveloadratio) {
		this.moveloadratio = moveloadratio;
	}

	public int getBitrate() {
		return bitrate;
	}

	public void setBitrate(int bitrate) {
		this.bitrate = bitrate;
	}

	public int getCustomframerate() {
		return customframerate;
	}

	public void setCustomframerate(int customframerate) {
		this.customframerate = customframerate;
	}

	public int getMarknum() {
		return marknum;
	}
	
	public int getMarknum(int defaultValue) {
		if(marknum<1)return defaultValue;
		return marknum;
	}

	public void setMarknum(int marknum) {
		this.marknum = marknum;
	}

	public int getProportionmale() {
		return proportionmale;
	}

	public void setProportionmale(int proportionmale) {
		this.proportionmale = proportionmale;
	}

	public List<Dynamicimg> getDynamicimgs() {
		return dynamicimgs;
	}

	public void setDynamicimgs(List<Dynamicimg> dynamicimgs) {
		this.dynamicimgs = dynamicimgs;
	}

	public int getSex() {
		return sex;
	}

	public void setSex(int sex) {
		this.sex = sex;
	}

	/**
	 * @return the photorate
	 */
	public int getPhotorate() {
		return photorate<=20?80:photorate;
	}

	/**
	 * @param photorate the photorate to set
	 */
	public void setPhotorate(int photorate) {
		this.photorate = photorate;
	}

	public String getAnaylseswitch() {
		return anaylseswitch;
	}

	public void setAnaylseswitch(String anaylseswitch) {
		this.anaylseswitch = anaylseswitch;
	}


	public List<DymainicMenu> getDynamicmenus() {
		return dynamicmenus;
	}

	public void setDynamicmenus(List<DymainicMenu> dynamicmenus) {
		this.dynamicmenus = dynamicmenus;
	}

	public String getReauth() {
		return reauth;
	}

	public void setReauth(String reauth) {
		this.reauth = reauth;
	}

	public List<Emotion> getEmotions() {
		return emotions;
	}

	public void setEmotions(List<Emotion> emotions) {
		this.emotions = emotions;
	}
}
