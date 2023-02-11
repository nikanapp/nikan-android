/**
 * 
 */
package com.bloomlife.videoapp.model;

import static com.bloomlife.videoapp.app.MyHXSDKHelper.ATTRIBUTE_PRVIEWURI;
import static com.bloomlife.videoapp.app.MyHXSDKHelper.ATTRIBUTE_SEX;
import static com.bloomlife.videoapp.app.MyHXSDKHelper.ATTRIBUTE_USER_ICON;
import static com.bloomlife.videoapp.app.MyHXSDKHelper.ATTRIBUTE_USER_NAME;
import static com.bloomlife.videoapp.app.MyHXSDKHelper.ATTRIBUTE_VIDEO_ID;
import static com.bloomlife.videoapp.app.MyHXSDKHelper.ATTRIBUTE_VIDEO_URI;

import java.util.Date;

import net.tsz.afinal.annotation.sqlite.Id;
import net.tsz.afinal.annotation.sqlite.Table;
import android.content.Context;
import android.text.TextUtils;

import com.bloomlife.android.bean.CacheBean;
import com.bloomlife.videoapp.R;
import com.bloomlife.videoapp.activity.RealNameChatActivity;
import com.bloomlife.videoapp.common.util.UIHelper;
import com.easemob.chat.EMMessage;
import com.easemob.chat.ImageMessageBody;
import com.easemob.chat.TextMessageBody;
import com.easemob.chat.VoiceMessageBody;

/**
 * 	通过列表中的消息实体
 * 
 * @author <a href="mailto:xiai.fei@gmail.com">xiai_fei</a>  
 *
 * @date 2014-12-17  下午5:18:42
 */
@Table(name="tb_message")
public class ConversationMessage {

	@Id
	private Integer id ;
	
	private String videoId;

	private String previewUrl ;

	private String videouri ;

	private String otherId;  //另一方用户的id

	private String userId;  // 当前聊天用户的ID

	public static final int STATUS_UNREAD = 0 ;

	public static final int STATUS_READ = 1 ;
	
	private int status ; 
	
	private String content ;
	
	private Date updateTime ;
	
	private String imagePath;
	
	private String thumbnailUrl;

	private String userName;

	private String userIcon;
	
	private int sex;
	
	public ConversationMessage() {
		super();
	}

	/**
	 * @param videoId
	 * @param previewUrl
	 * @param otherId
	 * @param content
	 */
	public ConversationMessage(String videoId, String previewUrl, String otherId, String content) {
		super();
		this.videoId = videoId;
		this.previewUrl = previewUrl;
		this.otherId = otherId;
		this.content = content;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getPreviewUrl() {
		return previewUrl;
	}

	public void setPreviewUrl(String previewUrl) {
		this.previewUrl = previewUrl;
	}

	public String getOtherId() {
		return otherId;
	}

	public void setOtherId(String otherId) {
		this.otherId = otherId;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
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

	public String getVideouri() {
		return videouri;
	}

	public void setVideouri(String videouri) {
		this.videouri = videouri;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
	
	public void setByEmMessage(Context context,EMMessage emMessage){
		this.updateTime = new Date(emMessage.getMsgTime());
		this.otherId = emMessage.getFrom();
		if(CacheBean.getInstance().getLoginUserId().equals(otherId)){
			this.otherId = emMessage.getTo();
		}
		if(emMessage.getBody() instanceof TextMessageBody){
			this.content = ((TextMessageBody)emMessage.getBody()).getMessage();
		} else if(emMessage.getBody() instanceof ImageMessageBody){
			this.content = context.getString(R.string.view_picture_text);
			this.imagePath = UIHelper.getEMMessageImage((ImageMessageBody)emMessage.getBody());
		} else if(emMessage.getBody() instanceof VoiceMessageBody){
			this.content = context.getString(R.string.view_sound_text);
		}
		this.videouri = emMessage.getStringAttribute(ATTRIBUTE_VIDEO_URI, "");
		this.previewUrl = emMessage.getStringAttribute(ATTRIBUTE_PRVIEWURI, "");
		this.userName = emMessage.getStringAttribute(ATTRIBUTE_USER_NAME, "");
		this.userIcon = emMessage.getStringAttribute(ATTRIBUTE_USER_ICON, "");
		this.sex = emMessage.getIntAttribute(ATTRIBUTE_SEX, 0);
		this.status = STATUS_UNREAD;
		this.userId = CacheBean.getInstance().getLoginUserId();

		// 如果是客服发来的信息，是没有videoId的，所以要给设一个特殊的id
		if (otherId.equals(context.getString(R.string.custom_name)))
			this.videoId = context.getString(R.string.custom_video_id);
		else if (!TextUtils.isEmpty(this.userName))
			this.videoId = RealNameChatActivity.REAL_NAME_CHAT_ID;
		else
			this.videoId = emMessage.getStringAttribute(ATTRIBUTE_VIDEO_ID, "");
	}
	public void setByChatBean(ChatBean chatBean){
		this.updateTime =chatBean.getCreatetime();
		this.content = chatBean.getContent();
		this.imagePath = chatBean.getImagePath();
		this.videoId =chatBean.getVideoId();
		this.videouri = chatBean.getVideoUri();
		this.previewUrl = chatBean.getPreviewUri();
		this.otherId = chatBean.getFromUser();
		this.status = STATUS_UNREAD;
		this.userId = CacheBean.getInstance().getLoginUserId();
	}

	@Override
	public boolean equals(Object o) {
		if(o==null) return false ;
		if(o instanceof ConversationMessage){
			ConversationMessage eq = (ConversationMessage) o ;
//			if(this.id==null){
//				if(eq.getId()!=null) return false ;
//			}else if(this.id != eq.getId()) return false ;
			
			if(this.videoId==null){
				if(eq.getVideoId()!=null)return false ;
			}else if(!this.videoId.equals(eq.getVideoId())) return false ;
			
			if(this.otherId==null){
				if(eq.getOtherId()!=null)return false ;
			}else if(!this.otherId.equals(eq.getOtherId())) return false ;
			
			if(this.videouri==null){
				if(eq.getVideouri()!=null)return false ;
			}else if(!this.videouri.equals(eq.getVideouri())) return false ;
			return true ;
		}else return false ;
	}

	@Override
	public int hashCode() {
		int code = 0 ;
		if(this.id!=null) code +=this.id;
		if(this.videoId!=null) code +=this.videoId.hashCode();
		if(this.videouri!=null) code +=this.videouri.hashCode();
		if(this.previewUrl!=null) code +=this.previewUrl.hashCode();
		if(this.otherId!=null) code +=this.otherId.hashCode();
		if(this.userId!=null) code +=this.userId.hashCode();
		if(this.content!=null) code +=this.content.hashCode();
		if(this.imagePath != null) code += this.imagePath.hashCode();
		 code +=this.status;
		 code +=this.sex;
		return code;
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
