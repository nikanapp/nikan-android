/**
 * 
 */
package com.bloomlife.android.media;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.provider.MediaStore.Images.Media;
import android.util.Log;
import android.widget.ImageView;

import com.bloomlife.android.bean.ImageItem;
import com.bloomlife.android.common.util.FileUtils;
import com.bloomlife.android.common.util.StringUtils;
import com.bloomlife.android.media.image.ImageResizer;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * @author <a href="mailto:xiai.fei@gmail.com">xiai_fei</a>  
 *
 * @date 2014-7-25  上午11:52:50
 */
public class ThumbnailManager {
	
	public static final String TAG = "ThumbnailManager";
	
	
	private final static String[]  mediaColumn={MediaStore.Images.Media._ID,
												MediaStore.Images.Media.DATA,
												MediaStore.Images.Media.TITLE,
												MediaStore.Images.Media.MIME_TYPE};
	
	private final static String[]  thumbColumns = {MediaStore.Images.Thumbnails.DATA,MediaStore.Images.Thumbnails.IMAGE_ID};

	public static Bitmap getLatestImgThumbBitmap(Activity activity){
		
		Cursor cursor = activity.managedQuery(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, mediaColumn, null, null, Media.DATE_ADDED+" DESC ");
		if(cursor==null||cursor.getCount()==0){
			if(cursor!=null)cursor.close();
			return null ;
		}
		
		cursor.moveToFirst();
		String imageId = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media._ID));
//		cursor.close();
//		cursor = null;
		
//		Cursor thumbCursor = activity.managedQuery(MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI, 
//				thumbColumns, Thumbnails.IMAGE_ID +" = ? ",new String[]{thumbId},null);
//		
//		if(thumbCursor==null||thumbCursor.getColumnCount()==0) return null ;
//		thumbCursor.moveToFirst();
		long imageIdLong = Long.parseLong(imageId);
		return  MediaStore.Images.Thumbnails.getThumbnail(activity.getContentResolver(), imageIdLong,Images.Thumbnails.MICRO_KIND, null);  
	}
	
	public static Bitmap getVideoThumbnail(ContentResolver cr, Uri uri) {  
		Bitmap bitmap = null;  
		BitmapFactory.Options options = new BitmapFactory.Options();  
		options.inDither = false;  
		options.inPreferredConfig = Bitmap.Config.ARGB_8888;  
		Cursor cursor = cr.query(uri,new String[] { MediaStore.Video.Media._ID }, null, null, null);   

		if (cursor == null || cursor.getCount() == 0) {  
		return null;  
		}  
		cursor.moveToFirst();  
		String videoId = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media._ID));  //image id in image table.s  

		if (videoId == null) {  
		return null;  
		}  
		cursor.close();  
		long videoIdLong = Long.parseLong(videoId);  
		bitmap = MediaStore.Images.Thumbnails.getThumbnail(cr, videoIdLong,Images.Thumbnails.MICRO_KIND, options);  

		return bitmap;  
		}  
	
	
	 /**
	  * 根据指定的图像路径和大小来获取缩略图
	  * 此方法有两点好处：
	  *     1. 使用较小的内存空间，第一次获取的bitmap实际上为null，只是为了读取宽度和高度，
	  *        第二次读取的bitmap是根据比例压缩过的图像，第三次读取的bitmap是所要的缩略图。
	  *     2. 缩略图对于原图像来讲没有拉伸，这里使用了2.2版本的新工具ThumbnailUtils，使
	  *        用这个工具生成的图像不会被拉伸。
	  * @param imagePath 图像的路径
	  * @param width 指定输出图像的宽度
	  * @param height 指定输出图像的高度
	  * @return 生成的缩略图
	  */
	 private Bitmap getImageThumbnail(String imagePath, int width, int height) {
		  Bitmap bitmap = null;
		  BitmapFactory.Options options = new BitmapFactory.Options();
		  options.inJustDecodeBounds = true;
		  // 获取这个图片的宽和高，注意此处的bitmap为null
		  bitmap = BitmapFactory.decodeFile(imagePath, options);
		  options.inJustDecodeBounds = false; // 设为 false
		  // 计算缩放比
		  int h = options.outHeight;
		  int w = options.outWidth;
		  int beWidth = w / width;
		  int beHeight = h / height;
		  int be = 1;
		  if (beWidth < beHeight) {
		   be = beWidth;
		  } else {
		   be = beHeight;
		  }
		  if (be <= 0) {
		   be = 1;
		  }
		  options.inSampleSize = be;
		  // 重新读入图片，读取缩放后的bitmap，注意这次要把options.inJustDecodeBounds 设为 false
		  bitmap = BitmapFactory.decodeFile(imagePath, options);
		  // 利用ThumbnailUtils来创建缩略图，这里要指定要缩放哪个Bitmap对象
		  bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,
		    ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
		  return bitmap;
		 }
	 
	 public Bitmap getImageThumbnail(Bitmap source, int width, int height){
		 return ThumbnailUtils.extractThumbnail(source, width, height, ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
	 }
	 
	 public static Bitmap getImageThumbnail(ImageItem imageItem , int width , int height){
		 if(StringUtils.isEmpty(imageItem.thumbnailPath)){
				if(StringUtils.isEmpty(imageItem.imagePath)) return null ;
//				// First decode with inJustDecodeBounds=true to check dimensions
//				final BitmapFactory.Options options = new BitmapFactory.Options();
//				options.inJustDecodeBounds = true;
//				options.inPurgeable = true;
//				BitmapFactory.decodeFile(imageItem.imagePath, options);
//				
//				if(options.outWidth<width){
//					return BitmapFactory.decodeFile(imageItem.imagePath);
//				}
				
				//这里不能这么用，会引爆很多内存的。要命的是，调用之后还不会保存缩略图
				Log.i(TAG, "找不到缩略图,使用 ThumbnailUtils 生成");
				
				
				return ImageResizer.getZoomBitmapFromFile(imageItem.imagePath, width,height).bitmap;
				
				//使用这种方法会使得内存一下子暴涨。
//				Bitmap result =  ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(imageItem.imagePath), width, height,ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
//				String savePath = savePicImage(cr, result);
//				if(!StringUtils.isEmpty(savePath)){
//					imageItem.thumbnailPath = savePath;
//				}
//				return result;
			}
			return BitmapFactory.decodeFile(imageItem.thumbnailPath);
	 }

	 
		
//		private static String  savePicImage(ContentResolver cr ,Bitmap bitmap){
//	        String thumbPath =  AlbumHelper.thumbPath;
//			if(StringUtils.isEmpty(thumbPath)) return null;
//			try {
//				
//				File file = new File(thumbPath.substring(0, thumbPath.lastIndexOf("/"))+ "/gm"+System.currentTimeMillis()+".jpg");
//				if(!file.exists()){
//					file.createNewFile();
//				}
//				BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
//				bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);// 将图片压缩的流里面
//				bos.flush();// 刷新此缓冲区的输出流
//				bos.close();// 关闭此输出流并释放与此流有关的所有系统资源
//				
//				MediaStore.Images.Media.insertImage( cr, file.getAbsolutePath(), file.getName(), file.getName());
//				bitmap.recycle();// 回收bitmap空间
//				return file.getAbsolutePath();
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//			return null ;
//		}
	 
	 
	 private static final String Thumb_File_Path = FileUtils.SDCARD+FileUtils.CAM_DIR+"thumb/";
	 
	 private static String getFileNameFormPath(String filePath){
		 return filePath.substring(filePath.lastIndexOf("/")+1, filePath.length());
	 }
	 
	 public static String getAppThumbPath(String sourcePath){
		 return Thumb_File_Path +getFileNameFormPath(sourcePath);
	 }
	 
	 public static String buildThumbImage(Bitmap source , String fileName ){
		//不知道为什么使用系统生成的缩略图会失真，暂时没有想到好办法，只能自己生成
			Matrix matrix = new Matrix();
			matrix.postScale(0.25f, 0.25f);
			
			/*
			 * 这里有其他多种方法可以获取这个bitmap 
			 * 1/、通过Thumbnails.getThumbnail
			 * 2、ThumbnailUtils.extractThumbnail(source, width, height, ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
			 * 
			 */
			Bitmap thumbBitmap = Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
			
			
			int w = thumbBitmap.getWidth();
			int h = thumbBitmap.getHeight();
			
			File path = new File(Thumb_File_Path);
			if (!path.exists()) {
				path.mkdirs();
			}
			String filepath = Thumb_File_Path +getFileNameFormPath(fileName);
			try {
				File file = new File(filepath);
				if (!file.exists()) {
					file.createNewFile();
				}
//				
				BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
				thumbBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);// 将图片压缩的流里面
				bos.flush();// 刷新此缓冲区的输出流
				bos.close();// 关闭此输出流并释放与此流有关的所有系统资源

				Log.d(TAG, " save photo success , photo path is : " + filepath);
			}catch(Exception e){
				e.printStackTrace();
			}
			return filepath; 
	 }
	 
	 
	 private final ExecutorService executorService = Executors.newFixedThreadPool(10);
	 
	 public void clear(){
		 executorService.shutdown();
	 }
	 
	 private ImageLoader imageLoader = ImageLoader.getInstance();
	 
	 public   void displayThumbImage(String thumbPath , ImageView imageView){
		 
	 }
	 
	 
	 
	 
	 
}