package com.bloomlife.android.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 一个图片对象
 * 
 * @author Administrator
 * 
 */
public class ImageItem implements Parcelable {
	
	private static final long serialVersionUID = 811407947675927917L;
	public ImageItem(){}
	
	public ImageItem(String imageId ,String imagePath , String thumbnailPath){
		this.imageId = imageId ;
		this.imagePath = imagePath;
		this.thumbnailPath = thumbnailPath;
	}
	
	public String imageId;
	public String thumbnailPath;
	/***app 自己生成的缩略图路径，和android系统自己生成的不一样,一般是在系统缩略图无法满足的时候生成的**/
	public String appThumbnailPath ;
	public String imagePath;
	
	/***被选中时的序号，如果为0表示没有被选中**/
//	public int selectNum ;
	
	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((imageId == null) ? 0 : imageId.hashCode());
		result = prime * result
				+ ((thumbnailPath == null) ? 0 : thumbnailPath.hashCode());
		result = prime * result
				+ ((imagePath == null) ? 0 : imagePath.hashCode());
//		result = prime * result + ((selectNum == 0.0) ? 0 : (int)selectNum);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		ImageItem item = (ImageItem) obj;
		if (imageId == null) {
			if (item.imageId != null) return false;
		} else if (!imageId.equals(item.imageId)) return false;
		
		if (thumbnailPath == null) {
			if (item.thumbnailPath != null) return false;
		} else if (!thumbnailPath.equals(item.thumbnailPath)) return false;
		
		if (imagePath == null) {
			if (item.imagePath != null) return false;
		} else if (!imagePath.equals(item.imagePath)) return false;
		return true;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(this.imageId);
		dest.writeString(this.imagePath);
		dest.writeString(this.thumbnailPath);
	}
	
	public static final Parcelable.Creator<ImageItem> CREATOR = new Creator<ImageItem>() {

		@Override
		public ImageItem createFromParcel(Parcel source) {
			ImageItem imageItem = new ImageItem();
			imageItem.imageId = source.readString();
			imageItem.imagePath = source.readString();
			imageItem.thumbnailPath = source.readString();
			return imageItem;
		}

		@Override
		public ImageItem[] newArray(int size) {
			return new ImageItem[size];
		}
		
	};
	
}
