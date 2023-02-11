/**
 * 
 */
package com.bloomlife.videoapp.view;

import com.bloomlife.videoapp.R;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Drawable.Callback;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.view.View;
import android.view.View.OnClickListener;

/**
 * @author <a href="mailto:lan4627@gmail.com">zhengxingtian</a>
 * 随机视频的动画按钮
 * @date 2015年1月13日 下午5:40:03
 */
public class RandomVideoButton extends RelativeLayout implements OnClickListener {

	public RandomVideoButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public RandomVideoButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public RandomVideoButton(Context context) {
		super(context);
		init();
	}
	
	private Handler mHandler = new Handler();
	private ImageView mAnimImage;
	private ImageView mAnimClick;
	private AnimationDrawable mRotateAnim;
	private OnClickListener l;
	private boolean mStart;

	private void init(){
		super.setOnClickListener(this);
		inflate(getContext(), R.layout.view_map_zoom, this);
		mAnimImage = (ImageView) findViewById(R.id.view_btn_random_animation);
		mRotateAnim = (AnimationDrawable) mAnimImage.getBackground();
		mAnimClick = (ImageView) findViewById(R.id.view_btn_random_click);
		mAnimClick.setAlpha(0f);
	}
	
	public void startRotationAnim(){
		if (mStart) return;
		ObjectAnimator.ofFloat(mAnimClick, "Alpha", 0, 1, 0).setDuration(300).start();
		mRotateAnim.start();
		mStart = true;
	}
	
	public void stopRotationAnim(){
		if (!mStart) return;
		mRotateAnim.stop();
		mRotateAnim.selectDrawable(0);
		mStart = false;
	}
	
	public void oneshotAnim(){
		if (mStart) return;
		mRotateAnim.start();
		setEnabled(false);
		mStart = true;
		mHandler.postDelayed(new Runnable() {
			
			@Override
			public void run() {
				stopRotationAnim();
			}
		}, 600);
	}

	@Override
	public void setOnClickListener(OnClickListener l) {
		this.l = l;
	}

	@Override
	public void onClick(View v) {
		if (l != null){
			l.onClick(v);
		}
	}
	
}
