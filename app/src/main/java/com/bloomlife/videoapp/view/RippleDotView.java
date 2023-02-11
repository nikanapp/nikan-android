/**
 * 
 */
package com.bloomlife.videoapp.view;

import com.bloomlife.android.common.util.UiUtils;
import com.bloomlife.videoapp.app.AppContext;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Interpolator;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;

/**
 * @author <a href="mailto:lan4627@gmail.com">zhengxingtian</a>
 * 视频播放页面Gif选中后下方的黄色小点
 * @date 2015年4月16日 下午4:10:24
 */
public class RippleDotView extends View {

	/**
	 * @param context
	 */
	public RippleDotView(Context context) {
		super(context);
		init(context);
	}

	/**
	 * @param context
	 * @param attrs
	 */
	public RippleDotView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	/**
	 * @param context
	 * @param attrs
	 * @param defStyleAttr
	 */
	public RippleDotView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context);
	}
	
	private static final int DOT_COLOR = 0xFFFFC000;
	private static final int RIPPLE_COLOR = 0x4DFFC000;
	
	private static final int RIPPLE_DRUATION = 1000;
	private static final int DRAW_SLEEP = 50;
	
	private Paint mDotPaint;
	private volatile Paint mRipplePaint;
	
	private volatile DecelerateInterpolator mInterpolator;
	
	private float mDotRadius;
	private float mMaxRippleRadius;
	private volatile float mRippleRadius;
	private volatile boolean mIsAnimStart;
	
	private void init(Context context){
		mDotPaint = new Paint();
		mDotPaint.setAntiAlias(true);
		mDotPaint.setColor(DOT_COLOR);
		mDotPaint.setStyle(Style.FILL);
		
		mRipplePaint = new Paint();
		mRipplePaint.setAntiAlias(true);
		mRipplePaint.setColor(RIPPLE_COLOR);
		mRipplePaint.setStyle(Style.FILL);
		
		
		mDotRadius = UiUtils.dip2px(context, 4);
		mMaxRippleRadius = UiUtils.dip2px(context, 20);
		mRippleRadius = 0;
		
		mInterpolator = new DecelerateInterpolator(1.0f);
		
	}

	@Override
	protected void onDraw(Canvas canvas) {
		final float cx = getCurcleX();
		final float cy = getCurcleY();
		canvas.drawCircle(cx, cy, mDotRadius, mDotPaint);
		canvas.drawCircle(cx, cy, mRippleRadius, mRipplePaint);
		super.onDraw(canvas);
	}

	public void startAnim(){
		mIsAnimStart = true;
		AppContext.EXECUTOR_SERVICE.execute(mAnimRunnable);
	}
	
	public void stopAnim(){
		mIsAnimStart = false;
	}
	
	private float getCurcleX(){
		return (getMeasuredWidth() + getPaddingLeft() + getPaddingRight()) / 2;
	}
	
	private float getCurcleY(){
		return (getMeasuredHeight() + getPaddingTop() + getPaddingBottom()) / 2;
	}
	
	private Runnable mAnimRunnable = new Runnable(){

		private int time;
		
		@Override
		public void run() {
			try {
				while (mIsAnimStart) {
					time = 0;
					// 大圈动画开始
					while (mRippleRadius < mMaxRippleRadius) {
						// 大圈的半径减速增加
						float scale = mInterpolator.getInterpolation((float)time / RIPPLE_DRUATION);
						mRippleRadius = mMaxRippleRadius * scale;
						// 颜色的透明度随着半径的扩大而变大
						mRipplePaint.setColor(0xA0FFC000 - ((int)(scale * 0xA0) * 0x1000000));
						postInvalidate();
						Thread.sleep(DRAW_SLEEP);
						time += DRAW_SLEEP;
					}
					mRippleRadius = 0;
					postInvalidate();
					Thread.sleep(RIPPLE_DRUATION);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
	};
}
