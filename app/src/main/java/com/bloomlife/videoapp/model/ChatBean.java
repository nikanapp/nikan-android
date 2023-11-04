/**
 * 
 */
package com.bloomlife.videoapp.model;

import java.io.Serializable;
import java.util.Date;

import static  com.bloomlife.videoapp.app.MyHXSDKHelper.*;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.bloomlife.android.bean.CacheBean;
import com.bloomlife.videoapp.R;
import com.bloomlife.videoapp.activity.RealNameChatActivity;
import com.bloomlife.videoapp.app.AppContext;
import com.bloomlife.videoapp.app.MyHXSDKHelper;
import com.bloomlife.videoapp.common.util.UIHelper;
import com.hyphenate.chat.EMImageMessageBody;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.chat.EMVoiceMessageBody;

import net.tsz.afinal.annotation.sqlite.Id;
import net.tsz.afinal.annotation.sqlite.Table;
import net.tsz.afinal.annotation.sqlite.Transient;

/**
 * @author <a href="mailto:xiai.fei@gmail.com">xiai_fei</a>  
 *
 * @date 2014-12-26  下午3:53:35
 */
@Table(name="tb_chatbean")
public class ChatBean implements Serializable{

	public ChatBean(Context context, EMMessage emMessage){
		this.fromUser = emMessage.getFrom();
		String lgoinUserId = CacheBean.getInstance().getLoginUserId();
		if (lgoinUserId.equals(fromUser)){//因为在我们的数据结构同，只保存另一个人的id，
			this.fromUser = emMessage.getTo();
		}
		this.createtime = new Date();
		if (emMessage.getBody() instanceof EMTextMessageBody){
			this.content = ((EMTextMessageBody)emMessage.getBody()).getMessage();
		} else if (emMessage.getBody() instanceof EMImageMessageBody){
			EMImageMessageBody body = (EMImageMessageBody)emMessage.getBody();
			this.content = context.getString(R.string.view_picture_text);
			this.imagePath = UIHelper.getEMMessageImage(body);
			this.thumbnailUrl = UIHelper.getEMMessageThumbnailUrl(body);
			this.imageWidth = body.getWidth();
			this.imageHeight = body.getHeight();
			Log.d("ChatBean", " thumbnailUrl " + this.thumbnailUrl);
		} else if (emMessage.getBody() instanceof EMVoiceMessageBody){
			EMVoiceMessageBody body = (EMVoiceMessageBody)emMessage.getBody();
			this.content = context.getString(R.string.view_sound_text);
			if (TextUtils.isEmpty(body.getRemoteUrl()))
				this.voicePath = body.getLocalUrl();
			else
				this.voicePath = body.getRemoteUrl();
			this.voiceDuration = body.getLength();
			Log.d("ChatBean", " voicePath " + this.voicePath+" voiceDuration "+this.voiceDuration);
		}
		this.videoUri = emMessage.getStringAttribute(ATTRIBUTE_VIDEO_URI, "");
		this.previewUri = emMessage.getStringAttribute(ATTRIBUTE_PRVIEWURI, "");
		this.sex = emMessage.getIntAttribute(ATTRIBUTE_SEX, 0);
		this.appVersion = emMessage.getIntAttribute(ATTRIBUTE_VERSION, 0);
		this.city = emMessage.getStringAttribute(ATTRIBUTE_CITY, "");
		this.userName = emMessage.getStringAttribute(ATTRIBUTE_USER_NAME, "");
		this.userIcon = emMessage.getStringAttribute(ATTRIBUTE_USER_ICON, "");
		this.status = STATUS_UNREAD;

		// 如果是客服发来的信息，是没有videoid的，所以要给设一个特殊的id
		if (fromUser.equals(context.getString(R.string.custom_name)))
			this.videoId = context.getString(R.string.custom_video_id);
		else if (!TextUtils.isEmpty(this.userName))
			this.videoId = RealNameChatActivity.REAL_NAME_CHAT_ID;
		else
			this.videoId = emMessage.getStringAttribute(ATTRIBUTE_VIDEO_ID, "");
		this.userId = CacheBean.getInstance().getLoginUserId();
		setEmmessageId(emMessage.getMsgId());
	}
	
	public static ChatBean makeTipsChatBean(int status, String from, String videoId, String fromCity){
		ChatBean bean = new ChatBean();
		bean.setWaitStatus(status);
		bean.setCreatetime(new Date());
		bean.setViewType(VIEW_TYPE_TIPS);
		bean.setStatus(STATUS_READ);
		bean.setSendSatus(SEND_STATUS_SUC);
		bean.setFromUser(from);
		bean.setVideoId(videoId);
		bean.setSex(AppContext.getSysCode().getSex());
		bean.setUserId(CacheBean.getInstance().getLoginUserId());
		bean.setCity(fromCity);
		return bean;
	}

	public ChatBean(){}
	
	@Id
	private Integer id ;
	
	private String fromUser;
	
	private Date createtime;
	
	private String content;
	
	private String videoId;
	
	private String videoUri;
	
	private String previewUri;

	private String userName;

	private String userIcon;
	
	private int direct = EMMessage.Direct.RECEIVE.ordinal(); //方向，参考环信的EMMessage的对应的类型

	public static final int STATUS_READ = 0 ;
	public static final int STATUS_UNREAD = 1 ;
	private int status ;
	
	public static final int SEND_STATUS_SENDING = 1 ;
	public static final int SEND_STATUS_FAILURE = 2 ;
	public static final int SEND_STATUS_SUC = 0 ;
	
	public static final int RESEND_STATUS_SEND = 0 ;
	public static final int RESEND_STATUS_RESEND = 3 ;
	
	public static final int VIEW_TYPE_CHAT = 0;
	public static final int VIEW_TYPE_TIPS = 1;
	
	public static final int WAIT_NULL = -1;
	public static final int WAIT_OK = 0;
	public static final int WAIT_MY_MSG = 1;
	public static final int WAIT_OTHER_MSG = 2;

	public static final int TIPS_CITY = 3;
	
	private int sendSatus ;
	
	private String emmessageId ;
	
	private String imagePath;
	
	private String voicePath;
	
	private String thumbnailUrl;
	
	private int sex;
	
	private int viewType;
	
	private int waitStatus;
	
	private int imageWidth;
	
	private int imageHeight;
	
	private int voiceDuration;
	
	private int appVersion;
	
	private int reSendStatus;

	private String city;

	private String userId;

	public String getFromUser() {
		return fromUser;
	}


	public void setFromUser(String fromUser) {
		this.fromUser = fromUser;
	}


	public Date getCreatetime() {
		return createtime;
	}

	public void setCreatetime(Date createtime) {
		this.createtime = createtime;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getVideoId() {
		return videoId;
	}

	public void setVideoId(String videoId) {
		this.videoId = videoId;
	}

	public String getVideoUri() {
		return videoUri;
	}

	public void setVideoUri(String videoUri) {
		this.videoUri = videoUri;
	}

	public String getPreviewUri() {
		return previewUri;
	}

	public void setPreviewUri(String previewUri) {
		this.previewUri = previewUri;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}


	public int getDirect() {
		return direct;
	}


	public void setDirect(int direct) {
		this.direct = direct;
	}


	public Integer getId() {
		return id;
	}


	public void setId(Integer id) {
		this.id = id;
	}


	/**
	 * @return the sendSatus
	 */
	public int getSendSatus() {
		return sendSatus;
	}


	/**
	 * @param sendSatus the sendSatus to set
	 */
	public void setSendSatus(int sendSatus) {
		this.sendSatus = sendSatus;
	}


	public String getEmmessageId() {
		return emmessageId;
	}


	public void setEmmessageId(String emmessageId) {
		this.emmessageId = emmessageId;
	}


	/**
	 * @return the imagePath
	 */
	public String getImagePath() {
		return imagePath;
	}


	/**
	 * @param imagePath the imagePath to set
	 */
	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}


	/**
	 * @return the sex
	 */
	public int getSex() {
		return sex;
	}


	/**
	 * @param sex the sex to set
	 */
	public void setSex(int sex) {
		this.sex = sex;
	}


	public int getViewType() {
		return viewType;
	}


	public void setViewType(int viewType) {
		this.viewType = viewType;
	}

	/**
	 * @return the thumbnailUrl
	 */
	public String getThumbnailUrl() {
		return thumbnailUrl;
	}

	/**
	 * @param thumbnailUrl the thumbnailUrl to set
	 */
	public void setThumbnailUrl(String thumbnailUrl) {
		this.thumbnailUrl = thumbnailUrl;
	}

	/**
	 * @return the waitStatus
	 */
	public int getWaitStatus() {
		return waitStatus;
	}

	/**
	 * @param waitStatus the waitStatus to set
	 */
	public void setWaitStatus(int waitStatus) {
		this.waitStatus = waitStatus;
	}

	/**
	 * @return the imageWidth
	 */
	public int getImageWidth() {
		return imageWidth;
	}

	/**
	 * @param imageWidth the imageWidth to set
	 */
	public void setImageWidth(int imageWidth) {
		this.imageWidth = imageWidth;
	}

	/**
	 * @return the imageHeight
	 */
	public int getImageHeight() {
		return imageHeight;
	}

	/**
	 * @param imageHeight the imageHeight to set
	 */
	public void setImageHeight(int imageHeight) {
		this.imageHeight = imageHeight;
	}

	/**
	 * @return the voicePath
	 */
	public String getVoicePath() {
		return voicePath;
	}

	/**
	 * @param voicePath the voicePath to set
	 */
	public void setVoicePath(String voicePath) {
		this.voicePath = voicePath;
	}

	/**
	 * @return the voiceDuration
	 */
	public int getVoiceDuration() {
		return voiceDuration;
	}

	/**
	 * @param voiceDuration the voiceDuration to set
	 */
	public void setVoiceDuration(int voiceDuration) {
		this.voiceDuration = voiceDuration;
	}

	/**
	 * @return the appVersion
	 */
	public int getAppVersion() {
		return appVersion;
	}

	/**
	 * @param appVersion the appVersion to set
	 */
	public void setAppVersion(int appVersion) {
		this.appVersion = appVersion;
	}

	/**
	 * @return the reSendStatus
	 */
	public int getReSendStatus() {
		return reSendStatus;
	}

	/**
	 * @param reSendStatus the reSendStatus to set
	 */
	public void setReSendStatus(int reSendStatus) {
		this.reSendStatus = reSendStatus;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserIcon() {
		return userIcon;
	}

	public void setUserIcon(String userIcon) {
		this.userIcon = userIcon;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}
}
