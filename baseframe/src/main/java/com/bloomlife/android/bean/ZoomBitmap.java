/**
 * 
 */
package com.bloomlife.android.bean;

import android.graphics.Bitmap;

/**
 * @author <a href="mailto:xiai.fei@gmail.com">xiai_fei</a>  
 *
 * @date 2013-11-26  上午9:53:28
 */
public class ZoomBitmap {

	/***读取的时候的缩放比例**/
	public final int zoomSize ;
	/**使用matrix缩放的比例**/
	private float ratio = 1.0f;
	
	public  Bitmap bitmap;
	public ZoomBitmap(Bitmap bitmap , int zoomSize){
		this.zoomSize = zoomSize;
		this.bitmap = bitmap;
	}

	/***
	 * 是否需要保存图片
	 * @return
	 */
	public boolean isSave(){
		if(zoomSize>1) return true ;
		return getRatio() < 1;
	}

	public float getRatio() {
		return ratio;
	}

	public void setRatio(float ratio) {
		this.ratio = ratio;
	}
}
