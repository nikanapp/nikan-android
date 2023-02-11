/**
 * 
 */
package com.bloomlife.videoapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.alibaba.fastjson.annotation.JSONField;
import com.bloomlife.videoapp.activity.HtmlActivity;
import com.bloomlife.videoapp.activity.MainActivity;
import com.bloomlife.videoapp.activity.MessageListActivity;
import com.bloomlife.videoapp.activity.SpalshActivity;
import com.bloomlife.videoapp.activity.VideoPlayActivity;

/**
 * @author <a href="mailto:xiai.fei@gmail.com">xiai_fei</a>  
 *
 * @date 2014-12-30  下午12:36:06
 */
public class PayLoad implements Parcelable{
	
	public enum TYPE {
		OpenApp(1, SpalshActivity.class),
		OpenMyVideo(2, MessageListActivity.class),
		OpenMap(3, MainActivity.class),
		OpenVideoPage(4, VideoPlayActivity.class),
		OpenHtml(10, HtmlActivity.class);
		
		public final Class<?> clazz;
		public final int type;
		
		TYPE(int type, Class<?> clazz){
			this.type = type;
			this.clazz = clazz;
		}
		
		public static TYPE getType(int typeId){
			for(TYPE type:TYPE.values()){
				if(type.type == typeId){
					return type;
				}
			}
			return OpenApp;
		}
	}

	public static final int  Swith_On= 1;
	
	private String videoid;
	private String text;
	private int type;
	private int sound;
	private int vibration;
	private double lon;
	private double lat;
	
	@JSONField(name="m")
	private String msgId;

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getVideoid() {
		return videoid;
	}

	public void setVideoid(String videoid) {
		this.videoid = videoid;
	}

	public int getSound() {
		return sound;
	}

	public void setSound(int sound) {
		this.sound = sound;
	}

	public int getVibration() {
		return vibration;
	}

	public void setVibration(int vibration) {
		this.vibration = vibration;
	}
	
	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public double getLon() {
		return lon;
	}

	public void setLon(double lon) {
		this.lon = lon;
	}

	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(videoid);
		dest.writeString(text);
		dest.writeInt(type);
		dest.writeInt(sound);
		dest.writeInt(vibration);
		dest.writeDouble(lon);
		dest.writeDouble(lat);
	}
	
	public String getMsgId() {
		return msgId;
	}

	public void setMsgId(String msgId) {
		this.msgId = msgId;
	}

	public static final Parcelable.Creator<PayLoad> CREATOR = new Parcelable.Creator<PayLoad>() {

		@Override
		public PayLoad createFromParcel(Parcel source) {
			PayLoad pay = new PayLoad();
			pay.videoid = source.readString();
			pay.text = source.readString();
			pay.type = source.readInt();
			pay.sound = source.readInt();
			pay.vibration = source.readInt();
			pay.lon = source.readDouble();
			pay.lat = source.readDouble();
			return pay;
		}

		@Override
		public PayLoad[] newArray(int size) {
			return new PayLoad[size];
		}
	};

}
