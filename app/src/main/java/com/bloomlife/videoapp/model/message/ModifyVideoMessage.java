/**
 * 
 */
package com.bloomlife.videoapp.model.message;

import java.util.List;

import com.alibaba.fastjson.annotation.JSONField;
import com.bloomlife.android.bean.BaseMessage;

/**
 * @author <a href="mailto:lan4627@gmail.com">zxt</a>
 *
 * @date 2014年11月24日 上午10:52:47
 */
public class ModifyVideoMessage extends BaseMessage {
	

	public static final int DELETE = 1;
	public static final int LOST = 2;
	
	private int modifyType;
	private String videos;
	
	@JSONField(serialize=false)
	private List<String> deletePathList ;
	
	public ModifyVideoMessage(){
		setMsgCode("3012");
	}
	
	public int getModifyType() {
		return modifyType;
	}

	public void setModifyType(int modifyType) {
		this.modifyType = modifyType;
	}

	public String getVideos() {
		return videos;
	}

	public void setVideos(String videos) {
		this.videos = videos;
	}

	public List<String> getDeletePathList() {
		return deletePathList;
	}

	public void setDeletePathList(List<String> deletePathList) {
		this.deletePathList = deletePathList;
	}

}
