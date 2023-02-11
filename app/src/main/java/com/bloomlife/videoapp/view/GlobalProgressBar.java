/**
 * 
 */
package com.bloomlife.videoapp.view;

import com.bloomlife.android.common.util.UiUtils;
import com.bloomlife.videoapp.R;
import com.bloomlife.videoapp.common.util.UIHelper;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * @author <a href="mailto:lan4627@gmail.com">zhengxingtian</a>
 * 
 * 默认的加载动画
 * @date 2014年12月16日 下午12:36:19
 */
public class GlobalProgressBar extends LinearLayout {

	public GlobalProgressBar(Context context) {
		super(context);
		init(context);
	}

	public GlobalProgressBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public GlobalProgressBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}
	
	public static final int DURATION = 650;
	public static final int START_ANGLE = 0;
	public static final int END_ANGLE = 360;
	
	private ImageView mRotationView;
	private TextView mTextView;
	private AnimationDrawable mAnim;

	@SuppressWarnings("deprecation")
	private void init(Context context){
		if (isInEditMode()) return;
		int padding = UiUtils.dip2px(context, 10);
		
		setGravity(Gravity.CENTER);
		setOrientation(VERTICAL);
		setPadding(padding, padding, padding, padding);
		setBackgroundResource(R.drawable.background_global_load_progress);
		
		// 旋转的图片
		mRotationView = new ImageView(context);
		mAnim = (AnimationDrawable) getResources().getDrawable(R.drawable.animation_global_load_progress);
		mRotationView.setBackgroundDrawable(mAnim);
		addView(mRotationView, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		// 进度文字
		mTextView = new TextView(context);
		mTextView.setTextColor(Color.WHITE);
		mTextView.setTypeface(UIHelper.getBebas(context));
		addView(mTextView, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, Gravity.CENTER));
	}
	
	public void startAnimator(){
		if (!mAnim.isRunning())
			mAnim.start();
	}
	
	public void stopAnimator(){
		if (mAnim.isRunning())
			mAnim.stop();
	}
	
	public void setText(CharSequence text){
		mTextView.setText(text);
	}
	
	public void setTextSize(float textSize){
		mTextView.setTextSize(textSize);
	}
	
	public void setTextColor(int color){
		mTextView.setTextColor(color);
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
//		float textHeight = mTextPaint.descent() - mTextPaint.ascent();
//		float verticalTextOffset = (textHeight / 2) - mTextPaint.descent();
//		for (String s : mSplitText) {
//			float horizontalTextOffset = mTextPaint.measureText(s) / 2;
//			canvas.drawText(s, this.getWidth() / 2 - horizontalTextOffset,
//					this.getHeight() / 2 + verticalTextOffset, mTextPaint);
//		}
		super.onDraw(canvas);
	}
	
	@Override
	public void setVisibility(int visibility) {
		if (visibility == View.VISIBLE){
			startAnimator();
		} else {
			stopAnimator();
		}
		super.setVisibility(visibility);
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		getLayoutParams().width = UiUtils.dip2px(getContext(), 90);
		getLayoutParams().height = UiUtils.dip2px(getContext(), 90);
	}

	
}
