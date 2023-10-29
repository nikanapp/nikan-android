/**
 * 
 */
package com.bloomlife.videoapp.model;

import com.alibaba.fastjson.annotation.JSONField;

import net.tsz.afinal.annotation.sqlite.Id;
import net.tsz.afinal.annotation.sqlite.Table;
import net.tsz.afinal.annotation.sqlite.Transient;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author <a href="mailto:lan4627@gmail.com">zxt</a>
 * 
 * @date 2015年2月6日 下午3:27:35
 */
@Table(name="com_bloomlife_videoapp_model_Message")
public class NotificationMessage implements Parcelable{
	
	@Id
	private int id; //数据库主键
	
	private String msgid;
	private int msgtype;
	private String msg;
	
	public static final int STATUS_READ = 2;
	public static final int STATUS_UNREAD = 1;

	public static final int TYPE_STORY_LIKE = 11;
	public static final int TYPE_STORY_COMMENT_TAG = 12;
	public static final int TYPE_STORY_COMMENT_TEXT = 13;

	private int status;
	private String videoid;
	private String userid;
	private String videouri;
	private String previewurl;
	private String description;
	private String storyId;
	private boolean storyMsg;

	@Transient
	private NotificationUserInfo extra;
	
	@JSONField(name="msgtime")
	private long createtime;


	/**
	 * @return the msgtype
	 */
	public int getMsgtype() {
		return msgtype;
	}

	/**
	 * @param msgtype
	 *            the msgtype to set
	 */
	public void setMsgtype(int msgtype) {
		this.msgtype = msgtype;
	}

	/**
	 * @return the msg
	 */
	public String getMsg() {
		return msg;
	}

	/**
	 * @param msg
	 *            the msg to set
	 */
	public void setMsg(String msg) {
		this.msg = msg;
	}

	/**
	 * @return the status
	 */
	public int getStatus() {
		return status;
	}

	/**
	 * @param status
	 *            the status to set
	 */
	public void setStatus(int status) {
		this.status = status;
	}

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
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return the createTime
	 */
	@JSONField(name="msgtime")
	public long getCreatetime() {
		return createtime;
	}

	/**
	 * @param createTime the createTime to set
	 */
	public void setCreatetime(long createTime) {
		this.createtime = createTime;
	}

	public String getMsgid() {
		return msgid;
	}

	public void setMsgid(String msgid) {
		this.msgid = msgid;
	}

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public NotificationUserInfo getExtra() {
		return extra;
	}

	public void setExtra(NotificationUserInfo extra) {
		this.extra = extra;
	}

	public String getStoryId() {
		return storyId;
	}

	public void setStoryId(String storyId) {
		this.storyId = storyId;
	}

	public boolean isStoryMsg() {
		return storyMsg;
	}

	public void setStoryMsg(boolean storyMsg) {
		this.storyMsg = storyMsg;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(this.id);
		dest.writeString(this.msgid);
		dest.writeInt(this.msgtype);
		dest.writeString(this.msg);
		dest.writeInt(this.status);
		dest.writeString(this.videoid);
		dest.writeString(this.userid);
		dest.writeString(this.videouri);
		dest.writeString(this.previewurl);
		dest.writeString(this.description);
		dest.writeString(this.storyId);
		dest.writeByte(storyMsg ? (byte) 1 : (byte) 0);
		dest.writeParcelable(this.extra, 0);
		dest.writeLong(this.createtime);
	}

	public NotificationMessage() {
	}

	protected NotificationMessage(Parcel in) {
		this.id = in.readInt();
		this.msgid = in.readString();
		this.msgtype = in.readInt();
		this.msg = in.readString();
		this.status = in.readInt();
		this.videoid = in.readString();
		this.userid = in.readString();
		this.videouri = in.readString();
		this.previewurl = in.readString();
		this.description = in.readString();
		this.storyId = in.readString();
		this.storyMsg = in.readByte() != 0;
		this.extra = in.readParcelable(NotificationUserInfo.class.getClassLoader());
		this.createtime = in.readLong();
	}

	public static final Creator<NotificationMessage> CREATOR = new Creator<NotificationMessage>() {
		public NotificationMessage createFromParcel(Parcel source) {
			return new NotificationMessage(source);
		}

		public NotificationMessage[] newArray(int size) {
			return new NotificationMessage[size];
		}
	};

	public static NotificationMessage copy(NotificationMessage m){
		NotificationMessage newMsg = new NotificationMessage();
		newMsg.setStatus(m.getStatus());
		newMsg.setVideouri(m.getVideouri());
		newMsg.setVideoid(m.getVideoid());
		newMsg.setCreatetime(m.getCreatetime());
		newMsg.setDescription(m.getDescription());
		newMsg.setExtra(m.getExtra());
		newMsg.setMsg(m.getMsg());
		newMsg.setMsgid(m.getMsgid());
		newMsg.setMsgtype(m.getMsgtype());
		newMsg.setPreviewurl(m.getPreviewurl());
		newMsg.setStoryId(m.getStoryId());
		newMsg.setStoryMsg(m.isStoryMsg());
		newMsg.setId(m.getId());
		newMsg.setUserid(m.getUserid());
		return newMsg;
	}
}
