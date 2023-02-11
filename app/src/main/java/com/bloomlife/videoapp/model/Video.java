package com.bloomlife.videoapp.model;

import com.bloomlife.android.common.util.StringUtils;
import com.bloomlife.videoapp.activity.fragment.VideoPlayerFragment;
import com.bloomlife.videoapp.common.util.Utils;
import com.bloomlife.videoapp.model.result.GetVideoResult;
import com.bloomlife.videoapp.model.result.MoreVideoResult.MoreVideoVo;

import net.tsz.afinal.annotation.sqlite.Id;
import net.tsz.afinal.annotation.sqlite.Table;
import net.tsz.afinal.annotation.sqlite.Transient;
import android.graphics.Point;
import android.graphics.RectF;
import android.os.Parcel;
import android.os.Parcelable;
/**
 * 视频实体
 * @author <a href="mailto:xiai.fei@gmail.com">xiai_fei</a>  
 *
 * @date 2014-11-14  上午10:48:03
 */
@Table(name="tb_video")
public class Video implements Parcelable , Comparable<Video> {

	public boolean isLike() {
		return like;
	}

	public void setLike(boolean like) {
		this.like = like;
	}

	@Id
	private Integer id ; //数据库主键
	
	private String lat;
	private String lon;
	private String description;
	private String topics;
	private String videoid;
	private String city;

	private String filaPath ;
	
	private String uid ;
	
	/**已经完成全部上传操作**/
	public static final int STATUS_UPLOAD_SUCCESS = 2 ;
	
	/**还没有执行上传步骤，一般是拍摄完成时网络请求无法进行的情况**/
	public static final int STATIS_NOT_COMPLETE = 0 ;
	
	public static final int STATUS_UPLOAD_FAIL = 3;
	
	public static final int MALE = 1;
	public static final int FEMALE = 0;
	
	private int status = STATIS_NOT_COMPLETE;
	
	private int times ; //视频时长
	
	private long uploadTime;
	
	private long createtime;
	
	private String videoBigrequrl;
	
	private String previewurl;
	
	private String videouri;
	
	private String videoKey ;
	
	private String persistentsId;
	
//	private String videowidth;
//	
//	private String videoHeight;
	
	private String width ;
	
	private String height ;
	
	private Integer rotate ;
	
	private int looknum ;
	
	private int likenum;
	
	private int sex;
	
	private long size;//文件大小，单位kb
	
	private boolean like;
	
	@Transient
	private double progress ;
	
	@Transient
	private boolean look;

	@Transient
	private boolean showAnimation; //是否要显示主页的动画。
	
	@Transient
	private boolean sendVideo = false; //是否用户发送的那个视频
	
	@Transient
	private String bigpreviewurl;
	
	public int getLooknum() {
		return looknum;
	}
	
	@Transient
	private RectF rectF = new RectF();  //在屏幕中的图标的坐标范围

	public void setLooknum(int videoSeeNum) {
		this.looknum = videoSeeNum;
	}
	
	public int getLikenum() {
		return likenum;
	}
	
	public void setLikenum(int likenum) {
		this.likenum = likenum;
	}

	public String getLat() {
		return lat;
	}

	public void setLat(String lat) {
		this.lat = lat;
	}

	public String getLon() {
		return lon;
	}

	public void setLon(String lon) {
		this.lon = lon;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getTopics() {
		return topics;
	}

	public void setTopics(String topic) {
		this.topics = topic;
	}
	
	public String getVideoid() {
		return videoid;
	}

	public void setVideoid(String videoid) {
		this.videoid = videoid;
	}

	public String getFilaPath() {
		return filaPath;
	}

	public void setFilePath(String filaPath) {
		this.filaPath = filaPath;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}


	public int getTimes() {
		return times;
	}

	public void setTimes(int times) {
		this.times = times;
	}


	public String getVideoKey() {
		return videoKey;
	}

	public void setVideoKey(String videoKey) {
		this.videoKey = videoKey;
	}
	
	public long getUploadTime() {
		return uploadTime;
	}

	public String getPersistentsId() {
		return persistentsId;
	}

	public void setPersistentsId(String persistentsId) {
		this.persistentsId = persistentsId;
	}

	public void setUploadTime(long uploadTime) {
		this.uploadTime = uploadTime;
	}

	public String getVideoBigrequrl() {
		return videoBigrequrl;
	}

	public void setVideoBigrequrl(String videoBigrequrl) {
		this.videoBigrequrl = videoBigrequrl;
	}
	
	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}


	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(getId()==null?0:getId());
		dest.writeString(lat);
		dest.writeString(lon);
		dest.writeString(description);
		dest.writeString(topics);
		dest.writeString(videoid);
		dest.writeString(filaPath);
		dest.writeInt(status);
		dest.writeInt(times);
		dest.writeLong(uploadTime);
		dest.writeString(videouri);
		dest.writeString(videoKey);
		dest.writeString(videoBigrequrl);
		dest.writeInt(looknum);
		dest.writeInt(likenum);
		dest.writeString(uid);
		dest.writeString(previewurl);
		dest.writeString(city);
		dest.writeLong(size);
		dest.writeInt(look ? 1 : 0);
		dest.writeInt(like ? 1 : 0);
		dest.writeString(width);
		dest.writeString(height);
		dest.writeInt(rotate == null ? -1 : rotate);
		dest.writeString(bigpreviewurl);
		dest.writeInt(sex);
	}
	
	public static Video createVideo(Parcel source){
		Video video = new Video();
		video.setId(source.readInt());
		video.lat = source.readString();
		video.lon = source.readString();
		video.description = source.readString();
		video.topics = source.readString();
		video.videoid = source.readString();
		video.filaPath = source.readString();
		video.status = source.readInt();
		video.times = source.readInt();
		video.uploadTime = source.readLong();
		video.videouri = source.readString();
		video.videoKey = source.readString();
		video.videoBigrequrl = source.readString();
		video.looknum=source.readInt();
		video.likenum = source.readInt();
		video.uid = source.readString();
		video.previewurl=source.readString();
		video.city = source.readString();
		video.size = source.readLong();
		video.look = source.readInt() == 1 ? true : false;
		video.like = source.readInt() == 1 ? true : false;
		video.width  = source.readString();
		video.height = source.readString();
		video.rotate	  = source.readInt();
		if(video.rotate<0)video.rotate = null ;
		video.bigpreviewurl = source.readString();
		video.sex = source.readInt();
		return video;
	}
	
	public static Video makeByResult(GetVideoResult result){
		Video video = new Video();
		video.videoid = result.getVideoid();
		video.lat = String.valueOf(result.getLat());
		video.lon = String.valueOf(result.getLon());
		video.looknum = result.getLooknum();
		video.likenum = result.getLikenum();
		video.uploadTime = result.getCreatetime();
		video.description = result.getDescription();
		video.videouri = result.getVideouri();
		video.previewurl = result.getPreviewurl();
		video.like = result.isLike();
		video.city = result.getCity();
		return video;
	}
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public static final Parcelable.Creator<Video> CREATOR = new Parcelable.Creator<Video>() {

		@Override
		public Video createFromParcel(Parcel source) {
			return createVideo(source);
		}

		@Override
		public Video[] newArray(int size) {
			return new Video[size];
		}
		
	};

	@Override
	public String toString() {
		return "Video [id=" + id + ", lat=" + lat + ", lon=" + lon + ", description=" + description + ", topic=" + topics + ", videoid=" + videoid + ", filaPath=" + filaPath + ", status=" + status + ", times=" + times
				+ ", uploadTime=" + uploadTime + ", previewurl=" + previewurl + ", videouri=" + videouri + ", videoKey=" + videoKey + ", persistentsId=" + persistentsId + ", city=" + city +"]";
	}

	public double getProgress() {
		return progress;
	}

	public void setProgress(double progress) {
		this.progress = progress;
	}

	@Override
	public int compareTo(Video another) {
		// TODO Auto-generated method stub
		return 0;
	}

	public RectF getRectF() {
		return rectF;
	}

	public void setRectF(RectF rectF) {
		this.rectF = rectF;
	}
	
	public void setRectF(int length ,Point point) {
		this.rectF .set(point.x-length/2, point.y-length, point.x+length/2, point.y);
	}

	public String getVideouri() {
		return videouri;
	}

	public void setVideouri(String videouri) {
		this.videouri = videouri;
	}

	public boolean isShowAnimation() {
		return showAnimation;
	}

	public void setShowAnimation(boolean showAnimation) {
		this.showAnimation = showAnimation;
	}


	public static Video makeByMore(MoreVideoVo videoVo){
		Video result = new Video();
		result.setVideoid(videoVo.getVideoid());
		result.setLat(videoVo.getLat());
		result.setLon(videoVo.getLon());
		result.setDescription(videoVo.getDescription());
		result.setVideouri(videoVo.getVideouri());
		result.setUid(videoVo.getUid());
		return result;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getPreviewurl() {
		return previewurl;
	}

	public void setPreviewurl(String previewurl) {
		this.previewurl = previewurl;
	}

	/**
	 * @return the city
	 */
	public String getCity() {
		return city;
	}

	/**
	 * @param city the city to set
	 */
	public void setCity(String city) {
		this.city = city;
	}

	public boolean isSendVideo() {
		return sendVideo;
	}

	public void setSendVideo(boolean sendVideo) {
		this.sendVideo = sendVideo;
	}

	public boolean isSame(Video video){
		if(video==null) return false ;
		if(this.lat==null) return false ;
		if(!this.lat.equals(video.getLat())) return false ;
		if(this.lon==null) return false ;
		if(!this.lon.equals(video.getLon())) return false ;
		if(this.description==null) return false ;
		return this.description.equals(video.getDescription());
	}

	public boolean isLook() {
		return look;
	}

	public void setLook(boolean look) {
		this.look = look;
	}


	public Integer getRotate() {
		return rotate;
	}

	public void setRotate(Integer rotate) {
		this.rotate = rotate;
	}

	public String getWidth() {
		return width;
	}

	public void setWidth(String width) {
		this.width = width;
	}

	public String getHeight() {
		return height;
	}

	public void setHeight(String height) {
		this.height = height;
	}

	public String getBigpreviewurl() {
		return bigpreviewurl;
	}

	public void setBigpreviewurl(String bigpreviewurl) {
		this.bigpreviewurl = bigpreviewurl;
	}

	public MyLatLng obtainLatLng(){
		if(StringUtils.isNotEmpty(lat)){
			return new  MyLatLng(Double.parseDouble(lat),Double.parseDouble(lon));
		}
		return null;
	}

	public int getSex() {
		return sex;
	}

	public void setSex(int sex) {
		this.sex = sex;
	}

	/**
	 * @return the createtime
	 */
	public long getCreatetime() {
		return createtime;
	}

	/**
	 * @param createtime the createtime to set
	 */
	public void setCreatetime(long createtime) {
		this.createtime = createtime;
	}

	public void setWidthAndHeight(Utils.Size size){
		if(size.width > size.height){
			if (getRotate()==null)
				setRotate(90);
			setHeight(String.valueOf(size.width));
			setWidth(String.valueOf(size.height));
		}else{
			setWidth(String.valueOf(size.width));
			setHeight(String.valueOf(size.height));
		}
	}
}

