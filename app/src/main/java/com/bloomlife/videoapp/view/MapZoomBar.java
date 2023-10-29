package com.bloomlife.videoapp.view;

import com.bloomlife.videoapp.R;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import androidx.core.view.MotionEventCompat;
import androidx.core.view.ViewCompat;
import androidx.customview.widget.ViewDragHelper;

import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.View.*;
import android.widget.FrameLayout;
import android.widget.ImageView;

/**
 * 地图缩放动作条
 * @author <a href="mailto:lan4627@gmail.com">zxt</a>
 *
 * @date 2014年11月11日 下午3:37:46
 */
public class MapZoomBar extends FrameLayout implements OnClickListener {
	
	public MapZoomBar(Context context) {
		super(context);
		init(context);
	}
	
	public MapZoomBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public MapZoomBar(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context);
	}
	
	public static final String TAG = MapZoomBar.class.getSimpleName();
	public static final int ZOOMBARBACK_DRUATION = 300;
	
	private ViewDragHelper mDragHelper;
	private View mScrollBar;
	private ImageView mZoomBarBack;
	private Paint mLinePaint;
	
	private int offsetLeft;
	private int offsetTop;

	private boolean isTouch;
	
	private void init(Context context){
		mDragHelper = ViewDragHelper.create(this, 1.0f, callback);
		setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		
		mZoomBarBack = new ImageView(context);
		mZoomBarBack.setImageResource(R.drawable.main_zoom_bar);
		mZoomBarBack.setVisibility(View.VISIBLE);
		LayoutParams mZoomBarBackParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
		mZoomBarBackParams.gravity = Gravity.CENTER;
		addView(mZoomBarBack, mZoomBarBackParams);
		
		mScrollBar = new ImageView(context);
		mScrollBar.setBackgroundResource(R.drawable.btn_main_zoom_selector);
		LayoutParams mParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		mParams.topMargin = 0;
		addView(mScrollBar, mParams);
		
		mLinePaint = new Paint();
		mLinePaint.setColor(Color.BLACK);
		
		getViewTreeObserver().addOnGlobalLayoutListener(globalListener);
	}
	
	private ViewDragHelper.Callback callback = new ViewDragHelper.Callback() {

		@Override
		public void onViewPositionChanged(View changedView, int left, int top,
				int dx, int dy) {
			super.onViewPositionChanged(changedView, left, top, dx, dy);
			Log.d(TAG, "onViewPositionChanged left "+left+" top "+top+" dx "+dx+" dy "+dy);
		}

		@Override
		public int clampViewPositionVertical(View child, int top, int dy) {
			Log.d(TAG, "clampViewPositionVertical top "+top+" dy "+dy);
			final int layoutTop = getPaddingTop();
			final int layoutButtom = getHeight() - mScrollBar.getHeight();
			final int newTop = Math.min(Math.max(top, layoutTop), layoutButtom);
			
			mZoom = (mMaxZoom-mMinZoom) * newTop/layoutButtom;
			if (mZoomListener != null)
				mZoomListener.onZoom(mZoom+mMinZoom);
			invalidate();
			return newTop;
		}

		@Override
		public boolean tryCaptureView(View view, int arg1) {
			Log.d(TAG, "Callback "+(mScrollBar == view)); 
			return mScrollBar == view ? true : false;
		}

		@Override
		public void onViewDragStateChanged(int state) {
			switch (state) {
			case ViewDragHelper.STATE_IDLE:
				mScrollBar.setSelected(false);
				break;

			default:
				if (isTouch)
					mScrollBar.setSelected(true);
				break;
			}
			super.onViewDragStateChanged(state);
		}
		
	};
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		isTouch = true;
		mDragHelper.processTouchEvent(event);
		return true;
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		final int action = MotionEventCompat.getActionMasked(ev);
		if (action == MotionEventCompat.ACTION_POINTER_DOWN || action == MotionEventCompat.ACTION_POINTER_UP){
			mDragHelper.cancel();
			return false;
		}
		return mDragHelper.shouldInterceptTouchEvent(ev);
	}
	
	@Override
	public void computeScroll() {
		if (mDragHelper.continueSettling(true)){
			ViewCompat.postInvalidateOnAnimation(this);
		} else {
			offsetLeft = mScrollBar.getLeft();
			offsetTop  = mScrollBar.getTop();
		}
		super.computeScroll();
	}
	

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
		mScrollBar.offsetLeftAndRight(offsetLeft);
		mScrollBar.offsetTopAndBottom(offsetTop);
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		Log.d(TAG, "onDraw "+getRight()+"  "+getTop()+"  "+getBottom()+"  "+getLeft());
		canvas.drawRect((getRight()/2)-1, getTop(), (getRight()/2)+1, getBottom(), mLinePaint);
		super.onDraw(canvas);
	}
	
	private ZoomBarListener mZoomListener;
	private float mMaxZoom = 100;
	private float mMinZoom = 0;
	private float mZoom = 0;
	
	/**
	 * 
	 * @param max 地图缩放动作条的最大刻度。
	 * @throws IllegalStateException 参数不能小于1
	 */
	public void setMaxZoom(int max){
		if (max < 1)
			throw new IllegalStateException("the MaxZoom less than 1");
		mMaxZoom = max;
	}
	
	public void setMinZoom(int min){
		mMinZoom = min;
	}
	
	/**
	 * 
	 * @return 返回当前地图缩放动作条的最大刻度，默认值是100
	 */
	public float getMaxZoom(){
		return mMaxZoom;
	} 
	
	/**
	 * 
	 * @param zoom 设置滑块在缩放工具条中的位置
	 */
	public void setZoom(float zoom){
		if (mZoom == zoom) 
			return;
		mZoom = zoom;
		setSlider(zoom);
	}
	
	private void setSlider(float zoom){
		isTouch = false;
		final float scroll = zoom- mMinZoom > mMaxZoom ? mMaxZoom : zoom- mMinZoom;
		smoothSlideTo(scroll /(mMaxZoom-mMinZoom));
		invalidate();
	}
	
	boolean smoothSlideTo(float slideOffset) {
	    final int topBound = getPaddingTop();
	    final int y = (int) (topBound + slideOffset * (getHeight()-mScrollBar.getHeight()));
	    Log.i(TAG, "topBound "+topBound+" slideOffset "+slideOffset+" height "+getHeight()+" y "+y+" ScrollBarHeight"+mScrollBar.getHeight());
	    mDragHelper.smoothSlideViewTo(mScrollBar, mScrollBar.getLeft(), y);
	    return false;
	}
	
	/**
	 * 
	 * @return 滑块在缩放工具条中的位置
	 */
	public float getZoom(){
		return mZoom;
	}
	
	/**
	 * 监听滑块滑动时候的刻度变化
	 * @param listener
	 */
	public void addZoomBarListener(ZoomBarListener listener){
		mZoomListener = listener;
	}
	
	public interface ZoomBarListener{
		void onZoom(float zoom);
	}
	
	@Override
	public void onClick(View v) {
		
	}	
	
	private OnGlobalLayoutListener globalListener = new OnGlobalLayoutListener() {

		@Override
		public void onGlobalLayout() {
			setSlider(mZoom);
			getViewTreeObserver().removeGlobalOnLayoutListener(globalListener);
		}
	};
}
