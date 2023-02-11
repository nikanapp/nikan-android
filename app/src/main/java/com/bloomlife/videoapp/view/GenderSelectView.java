/**
 * 
 */
package com.bloomlife.videoapp.view;

import com.bloomlife.videoapp.R;

import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

/**
 *   
 *
 * @author <a href="mailto:xiai.fei@gmail.com">xiai_fei</a>  
 *
 * @date 2015-6-19 下午2:19:54
 *
 * @organization bloomlife  
 * @version 1.0
 */
public class GenderSelectView extends RelativeLayout{

	public GenderSelectView(Context context, AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater.from(context).inflate(R.layout.view_gender_select, this);
		makeSexSelectAnim();
	}
	
	private AnimatorSet mSexSelectleAmin;
	
	private void makeSexSelectAnim(){
		mSexSelectleAmin = new AnimatorSet();
		mSexSelectleAmin.playTogether(
				ObjectAnimator.ofFloat(this, "scaleX", 0.9f, 1.0f),
				ObjectAnimator.ofFloat(this, "scaleY", 0.9f, 1.0f)
		);
		mSexSelectleAmin.setDuration(300);
	}
	
	
	public void startSplashAnim(AnimatorListener animatorListener){
		this.setVisibility(View.VISIBLE);
		mSexSelectleAmin.start();
		mSexSelectleAmin.addListener(animatorListener);
	}

}
