/**
 * 
 */
package com.bloomlife.videoapp.common;

import com.bloomlife.android.bean.CacheBean;
import com.bloomlife.videoapp.model.Account;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

/**
 * @author <a href="mailto:lan4627@gmail.com">zxt</a>
 *
 * @date 2015年1月30日 下午3:32:08
 */
public class CommentText implements Parcelable {
	private long cid;
	private String id;
	private String userid;
	private String content;
	private long createtime;
	private String username;
	private String usericon;
	private String gender;
	private long sectime;


	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public long getCreatetime() {
		return createtime;
	}

	public void setCreatetime(long createtime) {
		this.createtime = createtime;
	}
	
	public static CommentText makeCommentText(String content, Account account, boolean isReal){
		CommentText ct = new CommentText();
		ct.setUserid(CacheBean.getInstance().getLoginUserId());
		ct.setCreatetime(System.currentTimeMillis() / 1000);
		ct.setContent(content);
		ct.setGender(account.getGender());
		if (isReal && !TextUtils.isEmpty(account.getUserName())){
			ct.setUsername(account.getUserName());
			ct.setUsericon(account.getUserIcon());
		}
		return ct;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getUsericon() {
		return usericon;
	}

	public void setUsericon(String usericon) {
		this.usericon = usericon;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}


	public long getSectime() {
		return sectime;
	}

	public void setSectime(long sectime) {
		this.sectime = sectime;
	}

	public long getCid() {
		return cid;
	}

	public void setCid(long cid) {
		this.cid = cid;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}


	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(this.cid);
		dest.writeString(this.id);
		dest.writeString(this.userid);
		dest.writeString(this.content);
		dest.writeLong(this.createtime);
		dest.writeString(this.username);
		dest.writeString(this.usericon);
		dest.writeString(this.gender);
		dest.writeLong(this.sectime);
	}

	public CommentText() {
	}

	protected CommentText(Parcel in) {
		this.cid = in.readLong();
		this.id = in.readString();
		this.userid = in.readString();
		this.content = in.readString();
		this.createtime = in.readLong();
		this.username = in.readString();
		this.usericon = in.readString();
		this.gender = in.readString();
		this.sectime = in.readLong();
	}

	public static final Creator<CommentText> CREATOR = new Creator<CommentText>() {
		public CommentText createFromParcel(Parcel source) {
			return new CommentText(source);
		}

		public CommentText[] newArray(int size) {
			return new CommentText[size];
		}
	};
}
