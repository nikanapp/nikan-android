/**
 * 
 */
package com.bloomlife.videoapp.manager;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;

/**
 * @author <a href="mailto:xiai.fei@gmail.com">xiai_fei</a>  
 *
 * @date 2014-12-10  下午6:23:12
 */
public class FireAnimationListener implements AnimationListener{

	private View view ;
	
	public FireAnimationListener(View view){
		this.view = view ;
	}

	@Override
	public void onAnimationStart(Animation animation) {
		if(view!=null)view.setVisibility(View.VISIBLE);
	}
	
	@Override
	public void onAnimationRepeat(Animation animation) { }
	
	@Override
	public void onAnimationEnd(Animation animation) {
		if(view!=null)view.setVisibility(View.INVISIBLE);
	}
}
