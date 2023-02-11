/**
 * 
 */
package com.bloomlife.android.common.util;

import android.content.Context;

/**
 * @author <a href="mailto:xiai.fei@gmail.com">xiai_fei</a>  
 *
 * @date 2013-11-26  上午10:22:57
 */
public class UiUtils {

	public static int convertDIP2PX(Context context, int dip) { 
	    float scale = context.getResources().getDisplayMetrics().density; 
	    return (int)(dip*scale + 0.5f*(dip>=0?1:-1)); 
	}
	
	
	/** 
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素) 
     */  
    public static int dip2px(Context context, float dpValue) {  
        final float scale = context.getResources().getDisplayMetrics().density;  
        return (int) (dpValue * scale + 0.5f);  
    }  
  
    /** 
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp 
     */  
    public static int px2dip(Context context, float pxValue) {  
        final float scale = context.getResources().getDisplayMetrics().density;  
        return (int) (pxValue / scale + 0.5f);  
    }  

    /**
     * 将px值转换为sp值，保证文字大小不变
     * 
     * @param pxValue
     * @param fontScale（DisplayMetrics类中属性scaledDensity）
     * @return
     */
    public static int px2sp(Context context,  float fontScale) {
    	float pxValue = context.getResources().getDisplayMetrics().scaledDensity;
     return (int) (pxValue / fontScale + 0.5f);
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     * 
     * @param spValue
     * @param fontScale（DisplayMetrics类中属性scaledDensity）
     * @return
     */
    public static int sp2px(Context context, float spValue) {
    	float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
     return (int) (spValue * fontScale + 0.5f);
    }
   }

