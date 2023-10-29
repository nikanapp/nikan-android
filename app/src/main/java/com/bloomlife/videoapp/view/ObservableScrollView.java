/**
 * 
 */
package com.bloomlife.videoapp.view;

import com.bloomlife.android.common.util.UiUtils;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.Animator.AnimatorListener;
import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ScrollView;

/**
 * @author <a href="mailto:lan4627@gmail.com">zxt</a>
 *
 * @date 2015年2月3日 上午11:28:05
 */
public class ObservableScrollView extends ScrollView {
	
	public static final String TAG = ObservableScrollView.class.getSimpleName();
	
	private OnScrollListener mListener;
	private OnSingleClickListener mSingleClickListener;
	private View mChildView;
	private int mPullHeight;

	/**
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	public ObservableScrollView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param context
	 * @param attrs
	 */
	public ObservableScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param context
	 */
	public ObservableScrollView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	protected void onFinishInflate() {
		mChildView = getChildAt(0);
		super.onFinishInflate();
	}

	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		super.onScrollChanged(l, t, oldl, oldt);
		if (mListener != null)
			mListener.onScrollChanged(l, t, oldl, oldt);
	}
	
	public void setOnScrollListener(OnScrollListener listener){
		mListener = listener;
	}
	
	public void setOnSingleClickListener(OnSingleClickListener listener){
		mSingleClickListener = listener;
	}
	
	public interface OnScrollListener{
		void onScrollChanged(int l, int t, int oldl, int oldt);
		void onPull();
	}
	
	public interface OnSingleClickListener{
		void onClick();
	}
	
	public void setPullHeight(int height){
		mPullHeight = height;
	}
	
	private Rect mDownRect = new Rect();
	private boolean isPull;
	private boolean isTouchMove;
	private float mLastY;
	private int mSumScrollY;
	
	private boolean SoomthScrollTo(MotionEvent ev){
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			if (getScrollY() == 0){
				mSumScrollY = 0;
				isPull = false;
				mLastY = ev.getY();
				mDownRect = new Rect(mChildView.getLeft(), mChildView.getTop(), mChildView.getRight(), mChildView.getBottom());
			}
			return false;
			
		case MotionEvent.ACTION_MOVE:
			if (getScrollY() == 0){
				float moveY = ev.getY();
				int scrollY = (int) (moveY - mLastY) / 2;
				if (scrollY > 0){
					isTouchMove = true;
					mChildView.layout(mChildView.getLeft(), mChildView.getTop() + scrollY, mChildView.getRight(), mChildView.getBottom() + scrollY);
				}
				mSumScrollY += scrollY;
				mLastY = moveY;
				// 当达到下拉一定高度且下拉事件在本次下拉中未被执行过时，触发下拉事件。
				Log.d(TAG, "mPullHeight "+mChildView.getY());
				if (!isPull && mPullHeight > 0 && mPullHeight <= mSumScrollY && mListener != null){
					isPull = true;
					mListener.onPull();
				}
			}
			return false;
			
		case MotionEvent.ACTION_UP:
			Log.d(TAG, "ACTION_UP");
			if (mDownRect != null && !mDownRect.isEmpty()){
				Log.d(TAG, "ACTION_UP "+mDownRect);
				isTouchMove = false;
				Animation anim = new TranslateAnimation(mChildView.getX(), mDownRect.left, mChildView.getY(), mDownRect.top);
				anim.setDuration((long)(UiUtils.px2dip(getContext(), mPullHeight) * 2.5));
				mChildView.startAnimation(anim);
				mChildView.layout(mDownRect.left, mDownRect.top, mDownRect.right, mDownRect.bottom);
			}
			if (mSingleClickListener != null){
				mSingleClickListener.onClick();
			}
			return false;

		default:
			return false;
		}
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		Log.d(TAG, "onTouchEvent "+ev.getAction());
		return isEnabled() ? super.onTouchEvent(ev) : false;
	}
	
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		Log.d(TAG, "onInterceptTouchEvent "+ev.getAction());
		if (isTouchMove){
			return true;
		}
		boolean even = super.onInterceptTouchEvent(ev);
		Log.d(TAG, "even "+even);
		return even;
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
//		Log.d(TAG, "dispatchTouchEvent "+ev.getAction());
		SoomthScrollTo(ev);
		return super.dispatchTouchEvent(ev);
	}
}
