/**
 * 
 */
package com.bloomlife.videoapp.view;

import com.bloomlife.android.common.util.UiUtils;
import com.bloomlife.videoapp.R;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.FrameLayout;
import android.widget.ImageView;

/**
 * 点燃按钮的火焰效果
 * @author <a href="mailto:lan4627@gmail.com">zxt</a>
 *
 * @date 2014年12月5日 下午3:23:27
 */
public class FireView extends FrameLayout {

	public FireView(Context context) {
		super(context);
		init(context);
	}
	
	public FireView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}
	
	public FireView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}
	
	private ImageView mImageView;
	private ObjectAnimator mAnim;

	private void init(Context context){
		inflate(context, R.layout.view_fire_color, this);
		mImageView = (ImageView) findViewById(R.id.view_firebutton_image);
		getViewTreeObserver().addOnGlobalLayoutListener(globalLayoutListener);
	}
	
	public void startAnim(){
		int fireButtonHeight = UiUtils.dip2px(getContext(), 70);
		mAnim = ObjectAnimator.ofFloat(mImageView, "translationY", mImageView.getHeight()/2 - fireButtonHeight, -mImageView.getHeight()/2 + fireButtonHeight);
		mAnim.setRepeatCount(ObjectAnimator.INFINITE);
		mAnim.setDuration(2000);
		mAnim.start();
	}
	
	public void cancelAnim(){
		mAnim.cancel();
	}
	
	private OnGlobalLayoutListener globalLayoutListener = new OnGlobalLayoutListener() {
		
		@Override
		public void onGlobalLayout() {
			startAnim();
			getViewTreeObserver().removeGlobalOnLayoutListener(globalLayoutListener);
		}
	};
}
