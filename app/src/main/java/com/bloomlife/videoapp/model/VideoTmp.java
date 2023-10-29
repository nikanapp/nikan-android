/**
 * 
 */
package com.bloomlife.videoapp.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author <a href="mailto:lan4627@gmail.com">zxt</a>
 * 
 * @date 2015年1月27日 下午5:20:12
 */
public class VideoTmp implements Parcelable{
	
	private int cameraType;
	private String videoTmpPath;
	
	public VideoTmp(){
		
	}
	
	public VideoTmp(int cameraType, String videoTmpPath){
		this.cameraType = cameraType;
		this.videoTmpPath = videoTmpPath;
	}

	public int getCameraType() {
		return cameraType;
	}

	public void setCameraType(int cameraType) {
		this.cameraType = cameraType;
	}

	public String getVideoTmpPath() {
		return videoTmpPath;
	}

	public void setVideoTmpPath(String videoTmpPath) {
		this.videoTmpPath = videoTmpPath;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(cameraType);
		dest.writeString(videoTmpPath);
	}
	
	public static final Parcelable.Creator<VideoTmp> CREATOR = new Parcelable.Creator<VideoTmp>() {

		@Override
		public VideoTmp createFromParcel(Parcel source) {
			return new VideoTmp(source.readInt(), source.readString());
		}

		@Override
		public VideoTmp[] newArray(int size) {
			return new VideoTmp[size];
		}
	};

}
