package com.bloomlife.videoapp.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.widget.ImageView;

public class AudioPlayAnimView extends ImageView {

	public AudioPlayAnimView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public AudioPlayAnimView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public AudioPlayAnimView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	
	private Handler mHandler = new Handler();
	
	private int[] mImageArray;
	
	private Drawable mDrawable;
	
	private void init(Context context){
		
	}
	
	public void setAnimImageArray(int... images){
		mImageArray = images;
	}
	
	public void startAudioPlayAnimation(){
		mDrawable = getDrawable();
		mHandler.postDelayed(mAnimRunnable, 0);
	}
	
	public void stopAudioPlayAnimation(){
		mHandler.removeCallbacks(mAnimRunnable);
		setImageDrawable(mDrawable);
	}
	
	private Runnable mAnimRunnable = new Runnable() {
		
		private int current;
		
		@Override
		public void run() {
			if (mImageArray != null)
				setImageResource(mImageArray[current++%mImageArray.length]);
			mHandler.postDelayed(mAnimRunnable, 500);
		}
	};

}
