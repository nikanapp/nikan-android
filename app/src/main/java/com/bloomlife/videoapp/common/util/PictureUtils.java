/**
 * 
 */
package com.bloomlife.videoapp.common.util;

import com.bloomlife.videoapp.common.imagefilter.GaussianBlurFilter;
import com.bloomlife.videoapp.common.imagefilter.SoftGlowFilter;

import android.graphics.Bitmap;

/**
 * @author <a href="mailto:lan4627@gmail.com">zxt</a>
 *
 * @date 2015年5月7日 下午3:45:53
 */
public class PictureUtils {

	/**
	 * 
	 */
	public PictureUtils() {
		// TODO Auto-generated constructor stub
	}
	
	
	public static Bitmap filter(Bitmap bitmap){
		return new SoftGlowFilter(bitmap, 1, 0.0f, 0.0f).imageProcess().getDstBitmap();
//		return new SoftGlowFilter(new GaussianBlurFilter(bitmap).imageProcess().getDstBitmap()).imageProcess().getDstBitmap();
	}
	

}
