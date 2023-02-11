package com.bloomlife.android.common.util;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;

import com.bloomlife.android.R;
import com.bloomlife.android.bean.ZoomBitmap;
import com.bloomlife.android.framework.AppContext;
import com.bloomlife.android.media.image.ImageResizer;

/**
 * 图片操作工具包
 * 
 * @author gary
 */
public class ImageUtils {

	
	
	private static Bitmap getBitmap(String path) {
//		Bitmap bitmap = ImageResizer.decodeSampledBitmapFromFile(path, 640,1136);
//		bitmap = zoomImg(new ZoomBitmap(bitmap, 1), 640,1136,path);
//		saveBitmap(bitmap,path);
//		bitmap.recycle();
		return BitmapFactory.decodeFile(path);
	}
	
	public static final int STEP_SOURCE_TYPE_CAMERA = 1 ;
	public static final int STEP_SOURCE_TYPE_pIC = 0 ;
	public static final int STEP_SOURCE_TYPE_OTHER = 2 ;
//	private static Context m_ctx;
	/***
	 * 获取图片进行step的编辑。如果大于640的会进行压缩并且保存。小于640的进行放大。便于对step进行编辑，缩放等操作。
	 * @param path
	 * @return	一张宽度为640的bitmap
	 */
	public static Bitmap getBimapToCreateStep(String path,int type) {
		System.err.println(" path :" + path + "  and type : ");
		ZoomBitmap zoomBitmap = ImageResizer.getZoomBitmapFromFile(path, AppContext.deviceInfo.getScreenWidth(),AppContext.deviceInfo.getScreenHeight());
		Bitmap bitmap = zoomImg(zoomBitmap,  AppContext.deviceInfo.getScreenWidth(),AppContext.deviceInfo.getScreenHeight(),path,type);
		//其实这里没有保存的必要，经过decode的压缩和听过matrix的压缩之后。图片就已经是640的宽度，并且占用的内存空间和文件大小无关。而且在最终上传的时候，还要最终压缩
		if(zoomBitmap.isSave()){	//当需要压缩的时候才保存
			saveBitmap(bitmap,path);
			bitmap.recycle();
    		return BitmapFactory.decodeFile(path);
		}else {
			return bitmap;
		}
	}
	
	/****
	 * 		
	 * 		如果给定路径的图片的宽或高小于指定的w个h。那么会根据min(w,h)为标准进行压缩。
	 * 
	 * 			否则的话，返回原图。
	 * 
	 * @param path
	 * @param w
	 * @param h
	 * @return
	 */
	public static Bitmap compressWihtSide(String path , int w , int h ) {
		return ImageResizer.decodeSampledBitmapFromFile(path, w,h);
	}
	
	/***
	 * 	压缩图片保存。返回压缩后的文件路径
	 * @param picPath
	 * @return
	 */
	public static  String comprssAndSaveImage(String picPath , int quality){
		String prefix = "cp"+new Random().nextInt()+".";
		String result  = picPath.replace(".", prefix);
		ImageUtils.saveBitmap(BitmapFactory.decodeFile(picPath),result , quality);
		return result ;
	}
	
//	/**封面的长度   480**/
//	private static final int COVER_SIDE_LEN = 650;
	
	/***
	 * 获取截取后的秘籍封面.截取图片中间480*440的区域
	 * @param path
	 * @return
	 */
	public static String getCoverImage(String path){
//		Bitmap bitmap = BitmapFactory.decodeFile(path);
//		if(null==bitmap) return "";
//		int width = bitmap.getWidth();
//		int heigh = bitmap.getHeight();
//		int startW = width/2-COVER_SIDE_LEN/2 ;
//		int startH = heigh/2-COVER_SIDE_LEN/2 ;
//		int cW = COVER_SIDE_LEN ;
//		int cH= COVER_SIDE_LEN;
//		if(width<COVER_SIDE_LEN&&heigh<COVER_SIDE_LEN)return path;
//		
//		if(width<COVER_SIDE_LEN){
//			startW = 0 ;
//			cW =width;
//			
//		}
//		if(heigh<COVER_SIDE_LEN){
//			startH=0;
//			cH = heigh;
//		}
//		Bitmap result = Bitmap.createBitmap(bitmap, startW,startH, cW, cH);
//		path = path.replace(".", "cccc.");
//		saveBitmap(result, path);
//		result.recycle();
//		return path;
		
		int size=(int) (AppContext.deviceInfo.getScreenWidth()*0.9);
		
		ZoomBitmap zoomBitmap = ImageResizer.getZoomBitmapFromFile(path, size,size);
		Bitmap bitmap = zoomImg(zoomBitmap,  AppContext.deviceInfo.getScreenWidth(),AppContext.deviceInfo.getScreenHeight(),path,STEP_SOURCE_TYPE_pIC);
		path = path.replace(".", "cccc.");
		saveBitmap(bitmap,path);
		bitmap.recycle();
		return path;
		
	}
	
	/***
	 * 		对输入图片进行放大缩小处理。基本上可以获取一个大小为width和height的图片。
	 * @param bm
	 * @param width
	 * @param height
	 * @return
	 */
	public static Bitmap zoomImg(Bitmap bm, int width ,int height,Context ctx){
		try {
			return zoomImg(new ZoomBitmap(bm, 1),width,height,null,STEP_SOURCE_TYPE_OTHER);
		} catch (OutOfMemoryError  e) {
			e.printStackTrace();
		}
		return BitmapFactory.decodeResource(ctx.getResources(),R.drawable.adv_default);
	}
	// 缩放图片
	private static Bitmap zoomImg(ZoomBitmap zoomBitmap , int width ,int height , String picPath,int type){
		Matrix matrix = new Matrix();
	   // 获得图片的宽高
	   int bitmapWidth = zoomBitmap.bitmap.getWidth();
	   int bitmapHeight =zoomBitmap.bitmap.getHeight();
	   int degree = 0 ;
	   if(STEP_SOURCE_TYPE_CAMERA==type){
		   if(bitmapWidth>bitmapHeight){
//			   if(log.isDebugEnabled())log.debug("发现图片的宽大于高，图片来源为：  "+type+", 选择图片90度");
			   degree = 90;
		   }
	   }else if(STEP_SOURCE_TYPE_pIC==type){
		   if(!StringUtils.isEmpty(picPath)){
			    degree = getDegree(picPath);
		   }
	   }
	   if(degree!=0){
		   matrix.setRotate(degree, (float) bitmapWidth / 2, (float) bitmapHeight / 2);
		   // 得到新的图片
		   zoomBitmap.bitmap = Bitmap.createBitmap(zoomBitmap.bitmap, 0, 0, bitmapWidth, bitmapHeight, matrix, true);
		    bitmapWidth = zoomBitmap.bitmap.getWidth();
		    bitmapHeight =zoomBitmap.bitmap.getHeight();
	   }
	   //采用此种方法会增加主线程的负荷，造成响应时间增加，并且需要重新调整标签位置。但能有效减少OOM问题
/*	   int scale = 1;
	   if(bitmapWidth > width || bitmapHeight > height){
		   if (bitmapWidth - width > bitmapHeight - height) {
				scale = (int)(width / (bitmapWidth * 1.0f));
			} else {
				// 当图片高度大于屏幕高度时，将图片等比例压缩，使它可以完全显示出来
				scale = (int)(height / (bitmapHeight * 1.0f));
			}
	   }else{
			// 当图片的宽高都小于屏幕宽高时，直接让图片居中显示
			scale = (int)(width / (bitmapWidth * 1.0f));
	   }
	   if(log.isDebugEnabled())log.debug(" 缩放图片，缩放比例为："+scale);
	   zoomBitmap.setRatio(ratio);
	   // 取得想要缩放的matrix参数
	   matrix.reset();
	   matrix.postScale(ratio,ratio);
//	   Bitmap newbm = Bitmap.createBitmap(zoomBitmap.bitmap, 0, 0, bitmapWidth, bitmapHeight, matrix, true);
	  //Use BitmapFactory to avoid OOM error
	   BitmapFactory.Options o = new BitmapFactory.Options();
	   o.inJustDecodeBounds = true;
	   ByteArrayOutputStream baos = new ByteArrayOutputStream();
	   zoomBitmap.bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
	   InputStream is = new ByteArrayInputStream(baos.toByteArray());
	   BitmapFactory.Options o2 = new BitmapFactory.Options();
	   o2.inSampleSize = scale;
	   Bitmap newbm = BitmapFactory.decodeStream(is,null,o2);*/
	   //此方式速度较快，但容易造成OOM问题。
	   float ratio = 1.0f;
	   if (bitmapWidth > width || bitmapHeight > height) {
			if (bitmapWidth - width > bitmapHeight - height) {
				ratio = width / (bitmapWidth * 1.0f);
			} else {
				// 当图片高度大于屏幕高度时，将图片等比例压缩，使它可以完全显示出来
				ratio = height / (bitmapHeight * 1.0f);
			}
	   } else {
			// 当图片的宽高都小于屏幕宽高时，直接让图片居中显示
			ratio = width / (bitmapWidth * 1.0f);
	   }
	   
//	   if(log.isDebugEnabled())log.debug(" 缩放图片，缩放比例为："+ratio);
	   zoomBitmap.setRatio(ratio);
	   if(ratio==1.0f) return zoomBitmap.bitmap;
	   
	   // 取得想要缩放的matrix参数
	   matrix.reset();
	   matrix.postScale(ratio,ratio);
	   Bitmap newbm;
//	   try{
		   newbm = Bitmap.createBitmap(zoomBitmap.bitmap, 0, 0, bitmapWidth, bitmapHeight, matrix, true);
//	   }catch(Exception e){
//		   newbm = BitmapFactory.decodeResource(m_ctx.getResources(), R.drawable.default_no_content);
//	   }
	   if (newbm != zoomBitmap.bitmap) {
			// 不是同一张图片，可以释放
		   zoomBitmap.bitmap.recycle();
		}
	   return newbm;
	} 
	
	private static int getDegree(String bitmapUrl  ) {
		try {
			ExifInterface exifInterface = new ExifInterface(bitmapUrl);
			int orc = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, -1);
//			if(log.isDebugEnabled())log.debug("exifInterface orc is   "+orc);
			int degree = 0;
			if (orc == ExifInterface.ORIENTATION_ROTATE_90) {
				degree = 90;
			} else if (orc == ExifInterface.ORIENTATION_ROTATE_180) {
				degree = 180;
			} 
			return degree ;
		} catch (Exception e) {
//			log.error("获取图片方向异常", e);
		}
		return 0 ; 
	}
	
	//把bitmap转换成String
	public static String bitmapToString(String filePath) {
	        Bitmap bm = getBitmap(filePath);
	        ByteArrayOutputStream baos = new ByteArrayOutputStream();
	        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
	        byte[] b = baos.toByteArray();
	        bm.recycle();
	        return Base64.encode(new String(b));
	    }
	

	/**
	 * 读取网络图片
	 * @param url
	 * @return
	 */
	public static Bitmap getBitmapFromUrl(String url) {
		URL myFileUrl = null;
		Bitmap bitmap = null;
		HttpURLConnection conn= null;
		InputStream is = null;
		try {
			myFileUrl = new URL(url);
			conn = (HttpURLConnection) myFileUrl.openConnection();
			conn.setDoInput(true);
			conn.connect();
			is = conn.getInputStream();
			bitmap = BitmapFactory.decodeStream(is);
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try{
				if(conn!=null){
					conn.disconnect();
					conn=null;
				}
				if(is!=null){
					is.close();
				}
			}catch(Exception e){}
		}
		return bitmap;
	}
	
	/***
	 * 保存图片，默认使用100%质量
	 * @param bitmap
	 * @param filepath
	 */
	public static void saveBitmap(Bitmap bitmap,String filepath ) {
		saveBitmap(bitmap,filepath,100);
	}
	
	/***
	 * 保存压缩图片。（会覆盖原来的图片文件）
	 * @param bitmap
	 * @param filepath
	 * @param quality	压缩后的质量。 0 - 100 的区间。100为质量最好
	 */
	public static void saveBitmap(Bitmap bitmap,String filepath , int quality ) {
//		if(log.isDebugEnabled())log.debug(Utils.format("保存要图片{0},压缩比例为{1}",filepath,quality));
		ByteArrayOutputStream bos = null;
		BufferedOutputStream pos = null;
		try {
			File file = new File(filepath);
			if (!file.exists()) {
//				return;
				file.createNewFile();
			}
			bos = new ByteArrayOutputStream();
			bitmap.compress(Bitmap.CompressFormat.JPEG, quality, bos);
			pos = new BufferedOutputStream(new FileOutputStream(file));
			pos.write(bos.toByteArray());
			pos.flush();
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			try {
				if(!bitmap.isRecycled())bitmap.recycle();
				if (pos != null) {
					pos.close();
				}
				if (bos != null) {
					bos.close();
				}
			} catch (Exception ex) {

			}
		}
	}
	
	public static void zipBitmap(File file) {
		FileInputStream fis = null;
		Bitmap tmpmap = null;
		ByteArrayOutputStream bos = null;
		BufferedOutputStream pos = null;
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inSampleSize = 4;

		try {
			if (!file.exists()) {
				return;
			}
			fis = new FileInputStream(file);
			tmpmap = BitmapFactory.decodeStream(fis, null, options);
			bos = new ByteArrayOutputStream();
			tmpmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
			int optionsrate = 90;
			while (bos.toByteArray().length / 1024 >700) { // 循环判断如果压缩后图片是否大于400kb,大于继续压缩
				bos.reset();// 重置baos即清空baos
				optionsrate -= 5;// 每次都减少10
				tmpmap.compress(Bitmap.CompressFormat.JPEG, optionsrate, bos);// 这里压缩options%，把压缩后的数据存放到baos中
			}
			pos = new BufferedOutputStream(new FileOutputStream(file));
			pos.write(bos.toByteArray());
			pos.flush();
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			try {
				if (pos != null) {
					pos.close();
				}
				if (bos != null) {
					bos.close();
				}
				if (null != tmpmap && !tmpmap.isRecycled()) {
					tmpmap.recycle();
					tmpmap = null;
				}
			} catch (Exception ex) {

			}
		}
	}

	public static void zipBitmap(String imgpath) {
		try {
			File file = new File(imgpath);
			if (!file.exists()) {
				return;
			}
			zipBitmap(file);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
