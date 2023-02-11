/**
 * 
 */
package com.bloomlife.videoapp.common.util;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.ThumbnailUtils;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Surface;

/**
 * @author <a href="mailto:lan4627@gmail.com">zhengxingtian</a>
 *
 * @date 2014年11月27日 下午6:58:55
 */
public class CameraUtil {
	
	public static final String TAG = CameraUtil.class.getSimpleName();
	
	/**
	 * 生成视频的预览图保存到SD卡
	 * @return 预览图保存的路径
	 */
	public static String createVideoThumbnail(String videoUrl){
		Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(videoUrl, MediaStore.Video.Thumbnails.MINI_KIND);
		String thumbnailUrl = videoUrl.replace(".mp4", ".jpg");
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(thumbnailUrl);
			if(bitmap.compress(Bitmap.CompressFormat.JPEG, 85, fos)){
				return thumbnailUrl;
			} else {
				return null;
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		} finally{
			try {
				if (fos != null) 
					fos.close();
				if (bitmap != null){
					bitmap.recycle();
					bitmap = null;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 设置摄像头的方向
	 * @param activity
	 * @param cameraId
	 * @param camera
	 */
	public static int setCameraDisplayOrientation(Activity activity,
	         int cameraId, Camera camera) {
	    Camera.CameraInfo info = new Camera.CameraInfo();
	    Camera.getCameraInfo(cameraId, info);
	    int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
	    int degrees = 0;
	    switch (rotation) {
	        case Surface.ROTATION_0: degrees = 0; break;
	        case Surface.ROTATION_90: degrees = 90; break;
	        case Surface.ROTATION_180: degrees = 180; break;
	        case Surface.ROTATION_270: degrees = 270; break;
	    }

	    int result;
	    if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
	        result = (info.orientation + degrees) % 360;
	        result = (360 - result) % 360;  // compensate the mirror
	    } else {  // back-facing
	        result = (info.orientation - degrees + 360) % 360;
	    }
	    camera.setDisplayOrientation(result);
	    return result;
	}
	
	public static void checkSupportCamcorderProfile(){
		if (CamcorderProfile.hasProfile(CamcorderProfile.QUALITY_1080P))
			printCamcorderProfileLog(CamcorderProfile.get(CamcorderProfile.QUALITY_1080P), "QUALITY_1080P");
		if (CamcorderProfile.hasProfile(CamcorderProfile.QUALITY_720P))
			printCamcorderProfileLog(CamcorderProfile.get(CamcorderProfile.QUALITY_720P), "QUALITY_720P");
		if (CamcorderProfile.hasProfile(CamcorderProfile.QUALITY_480P))
			printCamcorderProfileLog(CamcorderProfile.get(CamcorderProfile.QUALITY_480P), "QUALITY_480P");
		if (CamcorderProfile.hasProfile(CamcorderProfile.QUALITY_CIF))
			printCamcorderProfileLog(CamcorderProfile.get(CamcorderProfile.QUALITY_CIF), "QUALITY_CIF");
		if (CamcorderProfile.hasProfile(CamcorderProfile.QUALITY_HIGH))
			printCamcorderProfileLog(CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH), "QUALITY_HIGH");
		if (CamcorderProfile.hasProfile(CamcorderProfile.QUALITY_LOW))
			printCamcorderProfileLog(CamcorderProfile.get(CamcorderProfile.QUALITY_LOW), "QUALITY_LOW");
		if (CamcorderProfile.hasProfile(CamcorderProfile.QUALITY_QCIF))
			printCamcorderProfileLog(CamcorderProfile.get(CamcorderProfile.QUALITY_QCIF), "QUALITY_QCIF");
		if (CamcorderProfile.hasProfile(CamcorderProfile.QUALITY_QVGA))
			printCamcorderProfileLog(CamcorderProfile.get(CamcorderProfile.QUALITY_QVGA), "QUALITY_QVGA");
	}
	
	private static void printCamcorderProfileLog(CamcorderProfile profile, String name){
		Log.e(TAG, "Support "+name+" FrameWidth "+profile.videoFrameWidth+" FrameHeight "+profile.videoFrameHeight);
	}
}
