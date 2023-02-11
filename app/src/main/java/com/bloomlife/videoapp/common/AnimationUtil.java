/**
 * 
 */
package com.bloomlife.videoapp.common;

import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;

/**
 * @author <a href="mailto:xiai.fei@gmail.com">xiai_fei</a>  
 *
 * @date 2014-12-5  下午7:56:33
 */
public class AnimationUtil {

	public static Animation makeScaleAnimation(){
		Animation animation = new ScaleAnimation(0, 1f, 0, 1f);
		animation.setDuration(500);
		return animation;
	}
}
