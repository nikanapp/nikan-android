/**
 * 
 */
package com.bloomlife.videoapp.view;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;

import com.bloomlife.android.common.util.UiUtils;
import com.bloomlife.videoapp.R;
import com.vividsolutions.jts.algorithm.Angle;

import android.animation.Animator.AnimatorListener;
import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.*;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;

/**
 * @author <a href="mailto:lan4627@gmail.com">zhengxingtian</a>
 * 拍摄界面的圆形进度按钮
 * @date 2014年11月27日 下午2:10:16
 */
public class CircularProgressBar extends FrameLayout implements OnClickListener{

	public CircularProgressBar(Context context) {
		super(context);
		initLayout(context);
	}
	
	public CircularProgressBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		initLayout(context);
	}
	
	public CircularProgressBar(Context context, AttributeSet attrs,
			int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		initLayout(context);
	}
	
	public static final String TAG = CircularProgressBar.class.getSimpleName();
	
	private CircularView mCircular;
	
	private void initLayout(Context context){
		initCircularWidth(context);
		super.setOnClickListener(this);
	}
	
	/**
	 * 设置各个圆和圆环的初始宽度
	 * @param context
	 */
	private void initCircularWidth(Context context){
		mCircular = new CircularView(context);
		addView(mCircular);
	}
	
	public static final int DRUATION = 200;
	
	public static final int PROGRESS_DRUATION = (int)(DRUATION * 1.5);
	
	private Handler mHandler = new Handler();
	
	public void addPart(){
		mCircular.addColor();
	}
	
	public void setCompleted(boolean c){
		mCircular.setCompleted(c);
	}
	
	public Float removePart(){
		Float angle = mCircular.removeColor();
		return angle;
	}
	
	public void setProgress(float angle){
		mCircular.setProgress(angle);
	}
	
	public void resetProgress(){
//		mLargeUpAnim.start();
		mCircular.resetProgress();
		mStop = true;
		mIsUp = false;
		mUntilTheEnd = false;
		mIsLongClick = false;
	}
	
	public void setSecondaryProgress(float angle){
//		mCircular.setSecondaryProgress(angle);
	}
	
	public void setSecondaryProgressBackground(boolean enable){
//		mCircular.setSecondaryProgressBackground(enable);
	}
	
	private OnClickListener l;

	@Override
	public void setOnClickListener(OnClickListener l) {
		this.l = l;
	}
	
	
	@Override
	public void onClick(View v) {
		mHandler.removeCallbacks(mLargeUpAnimRun);
		// 如果进度条不是为0，则延时到动画结束再执行接口。避免因为启动相机拍摄导致UI动画卡顿。
		if (mCircular.getProgress() == 0){
//			mProgressAnim.start();
		}
	}
	
	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		Log.d(TAG, "onTouchEvent");
		if (!isEnabled()) return true;
		super.onTouchEvent(event);
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			longCLickDown();
			return true;
			
		case MotionEvent.ACTION_CANCEL:
			return true;
			
		case MotionEvent.ACTION_UP:
			longClickUp();
			return true;
			
		case MotionEvent.ACTION_MOVE:
			return true;

		default:
			return false;
		}
		
	}
	
	private Runnable mLargeUpAnimRun = new Runnable() {
		
		@Override
		public void run() {
//			if (mLargeDownAnim.isRunning()) 
//				mLargeDownAnim.cancel();
//			mLargeUpAnim.start();
		}
	};
	
	private boolean mStop = true;
	private boolean mIsUp = false;
	private boolean mUntilTheEnd = false;
	
	private void longCLickDown(){
		if (mStop && !mIsUp) {
			mStop = false;
			mHandler.postDelayed(mLongClickRun, 600);
			mHandler.postDelayed(mMinStopTimeRun, 2300);
		}
	}
	
	private void longClickUp(){
		if (mIsLongClick){
			if (mStop && !mUntilTheEnd && mLongClickListener != null) {
				setClickEvent();
			} else {
				mIsUp = true;
			}
		} else {
			mStop = true;
			mHandler.removeCallbacks(mLongClickRun);
			mHandler.removeCallbacks(mMinStopTimeRun);
			if (mLongClickListener != null){
				mLongClickListener.onClick();
			}
		}
	}
	
	private void setClickEvent(){
		mIsLongClick = false;
		mLongClickListener.onLongClick(false);
	}
	
	private Runnable mLongClickRun = new Runnable() {
		
		@Override
		public void run() {
			if (mLongClickListener != null){
				mIsLongClick = true;
				mLongClickListener.onLongClick(true);
			}
		}
	};
	
	private Runnable mMinStopTimeRun = new Runnable() {
		
		@Override
		public void run() {
			// 设置了继续拍摄到进度结束的话，不做任何操作。
			if (mUntilTheEnd) 
				return;
			// 没有设置继续拍摄到进度结束的话，设置为可以停止拍摄。
			mStop = true;
			// 如果在延时的这段时间内用户松开按圆形进度条的手，则现在停止拍摄。
			if (mIsUp){
				setClickEvent();
				mIsUp = false;
			}
		}
	};
	
	/**
	 * 设置拍摄到进度结束
	 * @param c
	 */
	public void setUntilTheEnd(boolean c){
		mUntilTheEnd = c;
	}
	
	private boolean mIsLongClick = false;
	private LongClickListener mLongClickListener;
	
	public void setLongClickListener(LongClickListener l){
		mLongClickListener = l;
	}
	
	public interface LongClickListener {
		void onLongClick(boolean click);
		void onClick();
	}
	
	static class CircularView extends ImageView{
		
		public static final int DIVIDER_SWEEP = 2;
		
		/** 进度条的默认开始角度 */
		public static final float DEFAULT_START_ANGLE = 270f;
		
		private Deque<ArcAngle> mAngleStack = new ArrayDeque<ArcAngle>();
		
		private float mStartAngle = DEFAULT_START_ANGLE;
		private float mSweepAngle = 0f;
		private float mTotalAngle = 0f;
		private float mLatestAngle = 0f;
		
		private RectF mCircularProgressRectF;
		
		private Paint mCircularProgressPaint;
		
		private int mCircularProgressWidth;
		
		private float mCircularProgressPadding;
		
		private int mProgressColor;
		private int mDividerColor;
		
		private boolean mCompleted;
		

		public CircularView(Context context) {
			super(context);
			setBackgroundResource(R.drawable.btn_camera_selector);
			mCircularProgressWidth = UiUtils.dip2px(context, 4);
			mCircularProgressPadding = UiUtils.dip2px(context, 8) + 1.5f;
			mProgressColor = getResources().getColor(R.color.activity_camera_progressbar_yellow);
			mDividerColor = getResources().getColor(R.color.transparent);
			initPaint();
		}
		
		private void initPaint(){
			mCircularProgressPaint = new Paint();
			mCircularProgressPaint.setAntiAlias(true);
			mCircularProgressPaint.setStyle(Style.STROKE);
			mCircularProgressPaint.setColor(mProgressColor);
			setPaintWidth();
		}
		
		private void setPaintWidth(){
			mCircularProgressPaint.setStrokeWidth(mCircularProgressWidth);
		}
		
		private void drawCircular(){
			int x = getMeasuredWidth();
			int y = getMeasuredHeight();
			mCircularProgressRectF = new RectF(mCircularProgressPadding, mCircularProgressPadding, x - mCircularProgressPadding, y - mCircularProgressPadding);
		}
		
		@Override
		protected void onSizeChanged(int w, int h, int oldw, int oldh) {
			drawCircular();
			invalidate();
			super.onSizeChanged(w, h, oldw, oldh);
		}

		@Override
		protected void onDraw(Canvas canvas) {
			Iterator<ArcAngle> inter = mAngleStack.iterator();
			for (; inter.hasNext();){
				ArcAngle angle = inter.next();
				mCircularProgressPaint.setColor(angle.mColor);
				canvas.drawArc(mCircularProgressRectF, angle.mStartAngle, angle.mSweepAngle, false, mCircularProgressPaint);
			}
			mCircularProgressPaint.setColor(mProgressColor);
			canvas.drawArc(mCircularProgressRectF, mStartAngle, mSweepAngle, false, mCircularProgressPaint);
			if (mCompleted)
				canvas.drawArc(mCircularProgressRectF, 0, 360, false, mCircularProgressPaint);
			super.onDraw(canvas);
		}

		public void incrementProgress(){
			mSweepAngle++;
			mStartAngle--;
			postInvalidate();
		}
		
		public void addColor(){
			mAngleStack.push(new ArcAngle(mStartAngle, mSweepAngle, mProgressColor));
			mAngleStack.push(new ArcAngle(mStartAngle-DIVIDER_SWEEP, DIVIDER_SWEEP, mDividerColor));
			mTotalAngle += mSweepAngle;
			mStartAngle -= mSweepAngle+DIVIDER_SWEEP;
			mLatestAngle = mSweepAngle;
			mSweepAngle = 0;
		}
		
		public Float removeColor(){
			if (!mAngleStack.isEmpty()){
				mAngleStack.pop();
				mAngleStack.pop();
				if (!mAngleStack.isEmpty()){
					ArcAngle oAngle = mAngleStack.pop();
					ArcAngle sAngle = mAngleStack.peek();
					mStartAngle  = sAngle.mStartAngle - sAngle.mSweepAngle - DIVIDER_SWEEP;
					mLatestAngle = sAngle.mSweepAngle;
					mSweepAngle  = 0;
					mTotalAngle  = 0;
					for (ArcAngle a:mAngleStack){
						if (a.mColor == mProgressColor)
							mTotalAngle += sAngle.mSweepAngle;
					}
					mAngleStack.push(oAngle);
				} else {
					mStartAngle = DEFAULT_START_ANGLE;
					mSweepAngle = 0;
					mTotalAngle = 0;
					mLatestAngle = 0;
				}
				postInvalidate();
				return mTotalAngle;
			} 
			return null;
		}
		
		public void setProgress(float angle){
			float newAngle = angle * 360f / (360f + DIVIDER_SWEEP * mAngleStack.size() / 2) - mTotalAngle;
			mStartAngle += mLatestAngle - newAngle;
			mLatestAngle = newAngle;
			mSweepAngle = newAngle;
			postInvalidate();
		}
		
		public float getProgress(){
			return mSweepAngle;
		}
		
		public void setCompleted(boolean c){
			mCompleted = c;
		}
		
		public void resetProgress(){
			mStartAngle = DEFAULT_START_ANGLE;
			mSweepAngle = 0f;
			mTotalAngle = 0f;
			mLatestAngle = 0f;
			mAngleStack.clear();
			mCompleted = false;
			drawCircular();
			postInvalidate();
		}
		
		private class ArcAngle {
			
			float mStartAngle;
			float mSweepAngle;
			int mColor;
			
			public ArcAngle(float startAngle, float sweepAngle, int color){
				mStartAngle = startAngle;
				mSweepAngle = sweepAngle;
				mColor = color;
			}
			
		}
		
	}
	
}
