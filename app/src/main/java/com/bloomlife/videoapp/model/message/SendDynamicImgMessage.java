/**
 * 
 */
package com.bloomlife.videoapp.model.message;

import com.bloomlife.android.bean.BaseMessage;

/**
 * @author <a href="mailto:lan4627@gmail.com">zhengxingtian</a>
 *
 * @date 2015年4月16日 下午7:15:41
 */
public class SendDynamicImgMessage extends BaseMessage {

	private String videoid;
	private String imgid;
	private double playtime;
	private double x;
	private double y;
	
	public SendDynamicImgMessage() {
		setMsgCode("3036");
	}

	/**
	 * @return the videoid
	 */
	public String getVideoid() {
		return videoid;
	}

	/**
	 * @param videoid the videoid to set
	 */
	public void setVideoid(String videoid) {
		this.videoid = videoid;
	}

	/**
	 * @return the imgid
	 */
	public String getImgid() {
		return imgid;
	}

	/**
	 * @param imgid the imgid to set
	 */
	public void setImgid(String imgid) {
		this.imgid = imgid;
	}

	/**
	 * @return the playtime
	 */
	public double getPlaytime() {
		return playtime;
	}

	/**
	 * @param playtime the playtime to set
	 */
	public void setPlaytime(double playtime) {
		this.playtime = playtime;
	}

	/**
	 * @return the x
	 */
	public double getX() {
		return x;
	}

	/**
	 * @param x the x to set
	 */
	public void setX(double x) {
		this.x = x;
	}

	/**
	 * @return the y
	 */
	public double getY() {
		return y;
	}

	/**
	 * @param y the y to set
	 */
	public void setY(double y) {
		this.y = y;
	}

	
}
