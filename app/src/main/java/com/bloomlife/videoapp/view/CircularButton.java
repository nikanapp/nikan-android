/**
 * 
 */
package com.bloomlife.videoapp.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * @author <a href="mailto:lan4627@gmail.com">zhengxingtian</a>
 *
 * @date 2015年4月9日 上午11:07:37
 */
public class CircularButton extends View {
	
	private static final String TAG = CircularButton.class.getSimpleName();

	/**
	 * @param context
	 */
	public CircularButton(Context context) {
		super(context);
		init(context);
	}

	/**
	 * @param context
	 * @param attrs
	 */
	public CircularButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	/**
	 * @param context
	 * @param attrs
	 * @param defStyleAttr
	 */
	public CircularButton(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context);
	}
	
	private Paint mPaint;
	
	private void init(Context context){
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setColor(0xffffb307);
		mPaint.setStyle(Style.FILL);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		canvas.drawCircle(getCurcleX(), getCurcleY(), getCurcleX(), mPaint);
		super.onDraw(canvas);
	}
	
	private float getCurcleX(){
		return (getMeasuredWidth() + getPaddingLeft() + getPaddingRight()) / 2;
	}
	
	private float getCurcleY(){
		return (getMeasuredHeight() + getPaddingTop() + getPaddingBottom()) / 2;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		Log.d(TAG, " onTouchEvent "+event.getAction());
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			animate().scaleX(0.8f).scaleY(0.8f).setDuration(100);
			break;
			
		case MotionEvent.ACTION_UP:
			animate().scaleX(1.0f).scaleY(1.0f).setDuration(100);
			break;

		default:
			break;
		}
		return super.onTouchEvent(event);
	}
	
	

}
