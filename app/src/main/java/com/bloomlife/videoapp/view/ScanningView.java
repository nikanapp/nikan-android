/**
 * 
 */
package com.bloomlife.videoapp.view;

import com.bloomlife.videoapp.R;
import com.bloomlife.videoapp.common.util.UIHelper;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.TextView;

/**
 * @author <a href="mailto:lan4627@gmail.com">zhengxingtian</a>
 *
 * @date 2015年5月28日 下午6:52:10
 */
public class ScanningView extends FrameLayout {

	/**
	 * @param context
	 */
	public ScanningView(Context context) {
		super(context);
		init(context);
	}

	/**
	 * @param context
	 * @param attrs
	 */
	public ScanningView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	/**
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	public ScanningView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}
	
	public static final int DURATION = 2000;
	
	private View mScanningBar;
	private TextView mScanningText;
	private ObjectAnimator mTranslationAnim;
	private int mCount;
	private boolean mStart;
	private boolean mDelayedStop;
	
	private OnStopListener mStopListener;
	
	private void init(Context context){
		inflate(context, R.layout.view_scanning, this);
		setBackgroundResource(R.drawable.background_load_video);
		mScanningBar = findViewById(R.id.view_scanning_bar);
		mScanningBar.setVisibility(View.INVISIBLE);
		
		mScanningText = (TextView) findViewById(R.id.view_scanning_text);
		mScanningText.setTypeface(UIHelper.getBebas(context));
	}
	
	public void addListener(OnStopListener l){
		mStopListener = l;
	}
	
	public boolean isStart(){
		return mStart;
	}

	public void start(){
		if (mStart) return;
		mStart = true;
		mCount = 0;
		setVisibility(View.VISIBLE);
		mScanningBar.setVisibility(View.VISIBLE);
		if (mScanningBar.getWidth() + getMeasuredWidth() == 0){
			UIHelper.measure(this);
			UIHelper.measure(mScanningBar);
		}
		if (mTranslationAnim == null)
			mTranslationAnim = ObjectAnimator.ofFloat(mScanningBar, "translationX", -mScanningBar.getMeasuredWidth(), getMeasuredWidth() + mScanningBar.getMeasuredWidth());
		mTranslationAnim.setRepeatCount(ObjectAnimator.INFINITE);
		mTranslationAnim.setDuration(DURATION);
		mTranslationAnim.addListener(mListener);
		mTranslationAnim.start();
	}
	
	public void stop(){
		if (!mStart) return;
		if (mCount == 0){
			mDelayedStop = true;
		} else {
			stopAnim();
		}
	}
	
	private void stopAnim(){
		mStart = false;
		mTranslationAnim.cancel();
		setVisibility(View.INVISIBLE);
		mScanningBar.setVisibility(View.INVISIBLE);
		if (mStopListener != null)
			mStopListener.onStop();
	}
	
	private AnimatorListener mListener = new AnimatorListener() {
		
		@Override
		public void onAnimationStart(Animator animation) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onAnimationRepeat(Animator animation) {
			if (++mCount == 1 && mDelayedStop){
				mDelayedStop = false;
				stopAnim();
			}
//			Log.e("onAnimationRepeat", "count : "+mCount);
		}
		
		@Override
		public void onAnimationEnd(Animator animation) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onAnimationCancel(Animator animation) {
			// TODO Auto-generated method stub
			
		}
	};
	
	public interface OnStopListener{
		void onStop();
	}

}
