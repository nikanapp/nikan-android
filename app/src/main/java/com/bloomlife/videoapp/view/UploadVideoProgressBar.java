/**
 * 
 */
package com.bloomlife.videoapp.view;

import com.bloomlife.android.common.util.UiUtils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

/**
 * @author <a href="mailto:lan4627@gmail.com">zxt</a>
 * 视频上传进度条
 * @date 2015年6月5日 下午7:09:57
 */
public class UploadVideoProgressBar extends TextView {

	/**
	 * @param context
	 */
	public UploadVideoProgressBar(Context context) {
		super(context);
		init(context);
	}

	/**
	 * @param context
	 * @param attrs
	 */
	public UploadVideoProgressBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	/**
	 * @param context
	 * @param attrs
	 * @param defStyleAttr
	 */
	public UploadVideoProgressBar(Context context, AttributeSet attrs,
			int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context);
	}
	
	private Paint mCircularProgressPaint;
	private RectF mRectF;
	
	private int mProgressBarWidth;
	private float mSweepAngle;
	
	private void init(Context context){
		setGravity(Gravity.CENTER);
		setTextSize(10);
		mProgressBarWidth = UiUtils.dip2px(context, 4);
		mCircularProgressPaint = new Paint();
		mCircularProgressPaint.setAntiAlias(true);
		mCircularProgressPaint.setStyle(Style.STROKE);
		mCircularProgressPaint.setColor(Color.parseColor("#98EF45"));
		mCircularProgressPaint.setStrokeWidth(mProgressBarWidth);
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		canvas.drawArc(mRectF, -90, mSweepAngle, false, mCircularProgressPaint);
		super.onDraw(canvas);
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		int padding = mProgressBarWidth / 2;
		mRectF = new RectF(padding, padding, getMeasuredWidth()-padding, getMeasuredHeight()-padding);
	}
	
	public void setProgress(float p){
		mSweepAngle = 360 * p;
		setText((int)(p*100) + "%");
		invalidate();
	}

}
