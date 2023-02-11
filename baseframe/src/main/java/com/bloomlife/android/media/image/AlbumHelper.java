package com.bloomlife.android.media.image;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore.Audio.Albums;
import android.provider.MediaStore.Images.Media;
import android.provider.MediaStore.Images.Thumbnails;
import android.util.Log;

import com.bloomlife.android.bean.ImageBucket;
import com.bloomlife.android.bean.ImageItem;
import com.bloomlife.android.common.util.StringUtils;
//
///**
// * 相册帮助类
// * 
// * @author Administrator
// * 
// */
//public class AlbumHelper {
//	final String TAG = getClass().getSimpleName();
////	Context context;
////	ContentResolver cr;
//
//	/** 缩略图列表 */
//	HashMap<String, String> thumbnailList = new HashMap<String, String>();
//	/** 相册列表*/
//	List<HashMap<String, String>> albumList = new ArrayList<HashMap<String, String>>();
//	HashMap<String, ImageBucket> bucketList = new HashMap<String, ImageBucket>();
//
//	private static AlbumHelper instance;
//
//	private AlbumHelper() {}
//
//	public static AlbumHelper getHelper() {
//		if (instance == null) {
//			instance = new AlbumHelper();
//		}
//		return instance;
//	}
//
//
//	/**
//	 * 得到缩略图
//	 */
//	private void buildThumbnail(ContentResolver cr) {
//		String[] projection = { Thumbnails._ID, Thumbnails.IMAGE_ID,Thumbnails.DATA };
//		Cursor cur = cr.query(Thumbnails.EXTERNAL_CONTENT_URI, projection,null, null, null);
//		if (cur.moveToFirst()) {
//			int _id;
//			int image_id;
//			String image_path;
//			int _idColumn = cur.getColumnIndex(Thumbnails._ID);
//			int image_idColumn = cur.getColumnIndex(Thumbnails.IMAGE_ID);
//			int dataColumn = cur.getColumnIndex(Thumbnails.DATA);
//
//			do {
//				// Get the field values
//				_id = cur.getInt(_idColumn);
//				image_id = cur.getInt(image_idColumn);
//				image_path = cur.getString(dataColumn);
//
//				// Do something with the values.
//				 Log.i(TAG, _id + " image_id:" + image_id + " path:" + image_path + "---");
//				// HashMap<String, String> hash = new HashMap<String, String>();
//				// hash.put("image_id", image_id + "");
//				// hash.put("path", image_path);
//				// thumbnailList.add(hash);
//				thumbnailList.put("" + image_id, image_path);
//			} while (cur.moveToNext());
//		}
//	}
//
//
//	/**
//	 * 得到原图
//	 */
//	void getAlbum(ContentResolver cr) {
//		String[] projection = { Albums._ID, Albums.ALBUM, Albums.ALBUM_ART,
//				Albums.ALBUM_KEY, Albums.ARTIST, Albums.NUMBER_OF_SONGS };
//		Cursor cursor = cr.query(Albums.EXTERNAL_CONTENT_URI, projection, null,
//				null, null);
//		getAlbumColumnData(cursor);
//
//	}
//
//	/**
//	 * 从本地数据库中得到原图
//	 * 
//	 * @param cur
//	 */
//	private void getAlbumColumnData(Cursor cur) {
//		if (cur.moveToFirst()) {
//			int _id;
//			String album;
//			String albumArt;
//			String albumKey;
//			String artist;
//			int numOfSongs;
//
//			int _idColumn = cur.getColumnIndex(Albums._ID);
//			int albumColumn = cur.getColumnIndex(Albums.ALBUM);
//			int albumArtColumn = cur.getColumnIndex(Albums.ALBUM_ART);
//			int albumKeyColumn = cur.getColumnIndex(Albums.ALBUM_KEY);
//			int artistColumn = cur.getColumnIndex(Albums.ARTIST);
//			int numOfSongsColumn = cur.getColumnIndex(Albums.NUMBER_OF_SONGS);
//
//			do {
//				// Get the field values
//				_id = cur.getInt(_idColumn);
//				album = cur.getString(albumColumn);
//				albumArt = cur.getString(albumArtColumn);
//				albumKey = cur.getString(albumKeyColumn);
//				artist = cur.getString(artistColumn);
//				numOfSongs = cur.getInt(numOfSongsColumn);
//
//				// Do something with the values.
//				Log.i(TAG, _id + " album:" + album + " albumArt:" + albumArt
//						+ "albumKey: " + albumKey + " artist: " + artist
//						+ " numOfSongs: " + numOfSongs + "---");
//				HashMap<String, String> hash = new HashMap<String, String>();
//				hash.put("_id", _id + "");
//				hash.put("album", album);
//				hash.put("albumArt", albumArt);
//				hash.put("albumKey", albumKey);
//				hash.put("artist", artist);
//				hash.put("numOfSongs", numOfSongs + "");
//				albumList.add(hash);
//
//			} while (cur.moveToNext());
//
//		}
//	}
//
//
//	
//	private static final String columns[] =  { 
//		  Media._ID, 
//		  Media.BUCKET_ID,
//		  Media.PICASA_ID, 
//		  Media.DATA, 
//		  Media.DISPLAY_NAME, 
//		  Media.TITLE,
//		  Media.SIZE, 
//		  Media.BUCKET_DISPLAY_NAME 
//		 };
//	
//	/**
//	 * 得到图片集.
//	 * 遍历所有的图片，获取这些图片的相册信息，然后创建对应的相册实体
//	 * 没有办法直接获取相册的信息，只能通过图片的相册信息来归类
//	 */
//	void buildImagesBucketList(ContentResolver cr) {
//		long startTime = System.currentTimeMillis();
//
//		// 构造缩略图索引
//		buildThumbnail(cr);
//		// 得到一个游标
//		Cursor cur = cr.query(Media.EXTERNAL_CONTENT_URI, columns, null, null,null);
//		
//		// 构造相册索引
//		if (cur.moveToFirst()) {
//			// 获取指定列的索引
//			int photoIDIndex = cur.getColumnIndexOrThrow(Media._ID);
//			int photoPathIndex = cur.getColumnIndexOrThrow(Media.DATA);
////			int photoNameIndex = cur.getColumnIndexOrThrow(Media.DISPLAY_NAME);
////			int photoTitleIndex = cur.getColumnIndexOrThrow(Media.TITLE);
////			int photoSizeIndex = cur.getColumnIndexOrThrow(Media.SIZE);
//			int bucketDisplayNameIndex = cur.getColumnIndexOrThrow(Media.BUCKET_DISPLAY_NAME);
//			int bucketIdIndex = cur.getColumnIndexOrThrow(Media.BUCKET_ID);
////			int picasaIdIndex = cur.getColumnIndexOrThrow(Media.PICASA_ID);
//			
//			// 获取相片总数
//			int totalNum = cur.getCount();
//			do {
//				String _id = cur.getString(photoIDIndex);
//				String path = cur.getString(photoPathIndex);
//				String bucketName = cur.getString(bucketDisplayNameIndex);
//				String bucketId = cur.getString(bucketIdIndex);
//
//				ImageBucket bucket = bucketList.get(bucketId);
//				if (bucket == null) {
//					bucket = new ImageBucket(bucketId , bucketName ,thumbnailList.get(_id) );
//					bucketList.put(bucketId, bucket);
//				}
//				bucket.count++;
//				if(StringUtils.isEmpty(bucket.coverPath)) bucket.coverPath =thumbnailList.get(_id);
//				bucket.imageList.add(new ImageItem(_id,path,thumbnailList.get(_id)));
//			} while (cur.moveToNext());
//		}
//		printBucketsInfo();
//		long endTime = System.currentTimeMillis();
//		Log.d(TAG, "use time: " + (endTime - startTime) + " ms");
//	}
//
//	/**
//	 * 	打印相册信息
//	 */
//	private void printBucketsInfo() {
//		Iterator<Entry<String, ImageBucket>> itr = bucketList.entrySet().iterator();
//		while (itr.hasNext()) {
//			Map.Entry<String, ImageBucket> entry = (Map.Entry<String, ImageBucket>) itr .next();
//			ImageBucket bucket = entry.getValue();
//			Log.d(TAG, entry.getKey() + ", " + bucket.bucketName + ", " + bucket.count + " ---------- ");
//			for (int i = 0; i < bucket.imageList.size(); ++i) {
//				ImageItem image = bucket.imageList.get(i);
//				Log.d(TAG, "----- " + image.imageId + ", " + image.imagePath + ", " + image.thumbnailPath);
//			}
//		}
//	}
//	
//
//	/**
//	 * 得到图片集
//	 * 
//	 * @param refresh   是否刷新相册信息，否则的话继续使用上次获取到的相册信息
//	 * @return
//	 */
//	public List<ImageBucket> getImagesBucketList(Activity activity ,boolean refresh) {
//		if (refresh || (!refresh && bucketList.isEmpty())) {
//			bucketList.clear();
//			buildImagesBucketList(activity.getContentResolver());
//		}
////		List<ImageBucket> tmpList = new ArrayList<ImageBucket>();
////		Iterator<Entry<String, ImageBucket>> itr = bucketList.vaentrySet().iterator();
////		while (itr.hasNext()) {
////			Map.Entry<String, ImageBucket> entry = (Map.Entry<String, ImageBucket>) itr.next();
////			tmpList.add(entry.getValue());
////		}
//		return new ArrayList<ImageBucket>(bucketList.values());
//	}
//
//	/**
//	 * 得到原始图像路径
//	 * 
//	 * @param image_id
//	 * @return
//	 */
//	String getOriginalImagePath(ContentResolver cr,String image_id) {
//		String path = null;
//		Log.i(TAG, "---(^o^)----" + image_id);
//		String[] projection = { Media._ID, Media.DATA };
//		Cursor cursor = cr.query(Media.EXTERNAL_CONTENT_URI, projection,
//				Media._ID + "=" + image_id, null, null);
//		if (cursor != null) {
//			cursor.moveToFirst();
//			path = cursor.getString(cursor.getColumnIndex(Media.DATA));
//
//		}
//		return path;
//	}
//
//}

/**
 * 专辑帮助类 .。
 * 
 * TODO 系统提供的缩略图不要太过于依赖。因为缩略图有刷新间隔的问题，同时不是所有手机都会有支持这个的。三星蛋疼的一些机型就不支持了。
 * 
 * @author Administrator
 * 
 */
public class AlbumHelper {
	final String TAG = getClass().getSimpleName();

	// 缩略图列表
	HashMap<String, String> thumbnailList = new HashMap<String, String>();
	// 专辑列表
	List<HashMap<String, String>> albumList = new ArrayList<HashMap<String, String>>();
	LinkedHashMap<String, ImageBucket> bucketList = new LinkedHashMap<String, ImageBucket>();

	private static AlbumHelper instance;
	
	public static String thumbPath ;

	private AlbumHelper() {
	}

	public static AlbumHelper getHelper() {
		if (instance == null) {
			instance = new AlbumHelper();
		}
		return instance;
	}


	/**
	 * 得到缩略图
	 * 
	 * 从数据库中得到缩略图.  TODO 这里做存在隐患，因为当用户的缩略图文件夹可能越来越多，用户不清理的话，就会非常庞大，那么这个过程就会很慢。需要处理一下。
	 */
	private void getThumbnail(ContentResolver cr) {
		String[] projection = { Thumbnails._ID, Thumbnails.IMAGE_ID, Thumbnails.DATA };
		Cursor cur = cr.query(Thumbnails.EXTERNAL_CONTENT_URI, projection, null, null, null);
		if (cur.moveToFirst()) {
			int _id;
			int image_id;
			String image_path;
			int _idColumn = cur.getColumnIndex(Thumbnails._ID);
			int image_idColumn = cur.getColumnIndex(Thumbnails.IMAGE_ID);
			int dataColumn = cur.getColumnIndex(Thumbnails.DATA);

			do {
				// Get the field values
				_id = cur.getInt(_idColumn);
				image_id = cur.getInt(image_idColumn);
				image_path = cur.getString(dataColumn);
				thumbnailList.put("" + image_id, image_path);
			} while (cur.moveToNext());
		}
	}


	/**
	 * 得到原图
	 */
	void getAlbum(ContentResolver cr) {
		String[] projection = { Albums._ID, Albums.ALBUM, Albums.ALBUM_ART,
				Albums.ALBUM_KEY, Albums.ARTIST, Albums.NUMBER_OF_SONGS };
		Cursor cursor = cr.query(Albums.EXTERNAL_CONTENT_URI, projection, null,
				null, null);
		getAlbumColumnData(cursor);

	}

	/**
	 * 从本地数据库中得到原图
	 * 
	 * @param cur
	 */
	private void getAlbumColumnData(Cursor cur) {
		if (cur.moveToFirst()) {
			int _id;
			String album;
			String albumArt;
			String albumKey;
			String artist;
			int numOfSongs;

			int _idColumn = cur.getColumnIndex(Albums._ID);
			int albumColumn = cur.getColumnIndex(Albums.ALBUM);
			int albumArtColumn = cur.getColumnIndex(Albums.ALBUM_ART);
			int albumKeyColumn = cur.getColumnIndex(Albums.ALBUM_KEY);
			int artistColumn = cur.getColumnIndex(Albums.ARTIST);
			int numOfSongsColumn = cur.getColumnIndex(Albums.NUMBER_OF_SONGS);

			do {
				// Get the field values
				_id = cur.getInt(_idColumn);
				album = cur.getString(albumColumn);
				albumArt = cur.getString(albumArtColumn);
				albumKey = cur.getString(albumKeyColumn);
				artist = cur.getString(artistColumn);
				numOfSongs = cur.getInt(numOfSongsColumn);

				// Do something with the values.
				Log.i(TAG, _id + " album:" + album + " albumArt:" + albumArt
						+ "albumKey: " + albumKey + " artist: " + artist
						+ " numOfSongs: " + numOfSongs + "---");
				HashMap<String, String> hash = new HashMap<String, String>();
				hash.put("_id", _id + "");
				hash.put("album", album);
				hash.put("albumArt", albumArt);
				hash.put("albumKey", albumKey);
				hash.put("artist", artist);
				hash.put("numOfSongs", numOfSongs + "");
				albumList.add(hash);

			} while (cur.moveToNext());

		}
	}

	
	private final String columns[] = new String[] { Media._ID, Media.BUCKET_ID,
			Media.PICASA_ID, Media.DATA, Media.DISPLAY_NAME, Media.TITLE,
			Media.SIZE, Media.BUCKET_DISPLAY_NAME };


	/**
	 * 得到图片集.
	 * 遍历所有的图片，获取这些图片的相册信息，然后创建对应的相册实体
	 * 没有办法直接获取相册的信息，只能通过图片的相册信息来归类
	 */
	void buildImagesBucketList(ContentResolver cr) {
		long startTime = System.currentTimeMillis();
		// 构造缩略图索引
		getThumbnail(cr);
		// 构造相册索引
		Cursor cur = cr.query(Media.EXTERNAL_CONTENT_URI, columns, null, null,  Media.DATE_ADDED+" DESC ");
		if (cur.moveToFirst()) {
			// 获取指定列的索引
			int photoIDIndex = cur.getColumnIndexOrThrow(Media._ID);
			int photoPathIndex = cur.getColumnIndexOrThrow(Media.DATA);
			int photoNameIndex = cur.getColumnIndexOrThrow(Media.DISPLAY_NAME);
			int photoTitleIndex = cur.getColumnIndexOrThrow(Media.TITLE);
			int photoSizeIndex = cur.getColumnIndexOrThrow(Media.SIZE);
			int bucketDisplayNameIndex = cur .getColumnIndexOrThrow(Media.BUCKET_DISPLAY_NAME);
			int bucketIdIndex = cur.getColumnIndexOrThrow(Media.BUCKET_ID);
			int picasaIdIndex = cur.getColumnIndexOrThrow(Media.PICASA_ID);
			// 获取图片总数
			int totalNum = cur.getCount();
			do {
				String _id = cur.getString(photoIDIndex);
				String name = cur.getString(photoNameIndex);
				String path = cur.getString(photoPathIndex);
				String title = cur.getString(photoTitleIndex);
				String size = cur.getString(photoSizeIndex);
				String bucketName = cur.getString(bucketDisplayNameIndex);
				String bucketId = cur.getString(bucketIdIndex);
				String picasaId = cur.getString(picasaIdIndex);

				ImageBucket bucket = bucketList.get(bucketId);
				if (bucket == null){
					bucket= new ImageBucket(bucketId , bucketName );
					bucketList.put(bucketId,  bucket);
				}
				
				bucket.count++;
				ImageItem imageItem = new ImageItem();
				imageItem.imageId = _id;
				imageItem.imagePath = path;
				imageItem.thumbnailPath = thumbnailList.get(_id);
				if(thumbPath==null&&!StringUtils.isEmpty(imageItem.thumbnailPath))thumbPath = imageItem.thumbnailPath;
				bucket.imageList.add(imageItem);
			} while (cur.moveToNext());
		}
		printBulidBucketTime();
		long endTime = System.currentTimeMillis();
		Log.d(TAG, "use time: " + (endTime - startTime) + " ms");
	}

	/**
	 * 
	 */
	private void printBulidBucketTime() {
		Iterator<Entry<String, ImageBucket>> itr = bucketList.entrySet()
				.iterator();
		while (itr.hasNext()) {
			Map.Entry<String, ImageBucket> entry = itr
					.next();
			ImageBucket bucket = entry.getValue();
			Log.d(TAG, entry.getKey() + ", " + bucket.bucketName + ", "
					+ bucket.count + " ---------- ");
			for (int i = 0; i < bucket.imageList.size(); ++i) {
				ImageItem image = bucket.imageList.get(i);
				Log.d(TAG, "----- " + image.imageId + ", " + image.imagePath
						+ ", " + image.thumbnailPath);
			}
		}
	}

	/**
	 * 得到图片集
	 * 
	 * @param refresh
	 * @return
	 */
	public List<ImageBucket> getImagesBucketList(Context context,boolean refresh) {
		if (refresh || (!refresh && bucketList.isEmpty())) {
			bucketList.clear();
			buildImagesBucketList(context.getContentResolver());
		}
		return new ArrayList<ImageBucket>(bucketList.values());
	}

	/**
	 * 得到原始图像路径
	 * 
	 * @param image_id
	 * @return
	 */
	String getOriginalImagePath(ContentResolver cr,String image_id) {
		String path = null;
		Log.i(TAG, "---(^o^)----" + image_id);
		String[] projection = { Media._ID, Media.DATA };
		Cursor cursor = cr.query(Media.EXTERNAL_CONTENT_URI, projection,
				Media._ID + "=" + image_id, null, null);
		if (cursor != null) {
			cursor.moveToFirst();
			path = cursor.getString(cursor.getColumnIndex(Media.DATA));

		}
		return path;
	}

}
