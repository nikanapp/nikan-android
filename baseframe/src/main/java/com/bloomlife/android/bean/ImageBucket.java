package com.bloomlife.android.bean;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentResolver;
import android.graphics.Bitmap;

import com.bloomlife.android.common.util.Utils;
import com.bloomlife.android.media.ThumbnailManager;

/**
 * 一个目录的相册对象
 * 
 * @author Administrator
 * 
 */
public class ImageBucket {
	
	public static final String TAG = "ImageBucket";
	
	public ImageBucket(){}
	public ImageBucket(String bucketId , String bucketName){
		this.bucketId = bucketId;
		this.bucketName =bucketName;
		imageList = new ArrayList<ImageItem>();
	}
	
	private String bucketId ;
	/**相册中图片的总数*/
	public int count = 0;
	public String bucketName;
	public List<ImageItem> imageList;
	
	/** 相册的封面照**/
	public Bitmap getCoverPath(ContentResolver cr ,int width , int height){
		if(Utils.isEmptyCollection(imageList))return null;
		return ThumbnailManager.getImageThumbnail(imageList.get(0),width , height);
				
	}

	public String getBucketId(){
		return bucketId;
	}
}
