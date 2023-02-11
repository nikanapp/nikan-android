package com.bloomlife.videoapp.view;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;
import android.widget.FrameLayout;

import com.bloomlife.videoapp.R;
import com.bloomlife.videoapp.app.AppContext;
import com.bloomlife.videoapp.model.Video;

/**
 * 视频发送完成后的水波动画
 * @author zhengxingtian
 * 
 */
public class WaveView extends FrameLayout {

	public WaveView(Context context) {
		super(context);
		init(context);
	}

	public WaveView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public WaveView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	private View mImage0;
	private View mImage1;
	private View mImage2;

	private Animation mAnim0;
	private Animation mAnim1;
	private Animation mAnim2;

	private Handler mHandler = new Handler();

	private void init(Context context) {
		inflate(context, R.layout.view_wave, this);
		mImage0 = findViewById(R.id.view_wave_imag0);
		mImage1 = findViewById(R.id.view_wave_imag1);
		mImage2 = findViewById(R.id.view_wave_imag2);
		
		mAnim0 = AnimationUtils.loadAnimation(getContext(),
				R.anim.view_wave_anim);
		mAnim1 = AnimationUtils.loadAnimation(getContext(),
				R.anim.view_wave_anim);
		mAnim2 = AnimationUtils.loadAnimation(getContext(),
				R.anim.view_wave_anim);

		mAnim0.setAnimationListener(new WaveAnimationListener(mImage0,0));
		mAnim1.setAnimationListener(new WaveAnimationListener(mImage1,1));
		mAnim2.setAnimationListener(new WaveAnimationListener(mImage2,2));
	}

	public void startAnim() {
		mImage0.startAnimation(mAnim0);
		mHandler.postDelayed(new Runnable() {

			@Override
			public void run() {
				mImage1.startAnimation(mAnim1);
			}
		}, 300);

		mHandler.postDelayed(new Runnable() {

			@Override
			public void run() {
				mImage2.startAnimation(mAnim2);
			}
		}, 600);
	}

	public WavedAnimationListener getWavedAnimationListener() {
		return wavedAnimationListener;
	}

	public void setWavedAnimationListener(WavedAnimationListener wavedAnimationListener) {
		this.wavedAnimationListener = wavedAnimationListener;
	}

	class WaveAnimationListener implements AnimationListener {
		private View mView;
		
		private int index ;

		public WaveAnimationListener(View view,int index ) {
			mView = view;
			this.index = index;
		}

		@Override
		public void onAnimationStart(Animation animation) {
			mView.setVisibility(View.VISIBLE);
		}

		@Override
		public void onAnimationRepeat(Animation animation) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onAnimationEnd(Animation animation) {
			mView.setVisibility(View.INVISIBLE);
			if(index==2){
				if(wavedAnimationListener!=null)wavedAnimationListener.onAnimationEnd();
			}
		}
	}


	public interface WavedAnimationListener {
		void onAnimationEnd();
	}
	
	private WavedAnimationListener wavedAnimationListener;

}
