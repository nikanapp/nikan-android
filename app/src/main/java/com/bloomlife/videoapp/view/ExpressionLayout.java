/**
 * 
 */
package com.bloomlife.videoapp.view;

import java.util.List;

import com.bloomlife.android.bean.CacheBean;
import com.bloomlife.videoapp.R;
import com.bloomlife.videoapp.common.CacheKeyConstants;
import com.bloomlife.videoapp.common.util.ImageLoaderUtils;
import com.bloomlife.videoapp.model.Dynamicimg;
import com.bloomlife.videoapp.view.dialog.ExpressionWindow.ExpressionListener;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.animation.AnimationSet;

/**
 * @author <a href="mailto:lan4627@gmail.com">zxt</a>
 * 表情弹幕viewpager里的布局
 * @date 2015年4月15日 下午5:32:08
 */
public class ExpressionLayout extends RelativeLayout {

	/**
	 * @param context
	 */
	public ExpressionLayout(Context context) {
		super(context);
		init(context);
	}

	/**
	 * @param context
	 * @param attrs
	 */
	public ExpressionLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	/**
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	public ExpressionLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}
	
	public static final String TAG = ExpressionLayout.class.getSimpleName();
	
	public static final int LENGTH = 6;
	
	private Handler mHandler = new Handler();
	private ImageView[] images = new ImageView[6];
	
	private ImageLoader mImageLoader;
	private DisplayImageOptions mOptions;
	
	private void init(Context context){
		inflate(context, R.layout.view_expression_window, this);
		
		ImageView e1 = (ImageView) findViewById(R.id.view_expression_e1);
		ImageView e2 = (ImageView) findViewById(R.id.view_expression_e2);
		ImageView e3 = (ImageView) findViewById(R.id.view_expression_e3);
		ImageView e4 = (ImageView) findViewById(R.id.view_expression_e4);
		ImageView e5 = (ImageView) findViewById(R.id.view_expression_e5);
		ImageView e6 = (ImageView) findViewById(R.id.view_expression_e6);
		
		e1.setOnTouchListener(mGifTouchListener);
		e2.setOnTouchListener(mGifTouchListener);
		e3.setOnTouchListener(mGifTouchListener);
		e4.setOnTouchListener(mGifTouchListener);
		e5.setOnTouchListener(mGifTouchListener);
		e6.setOnTouchListener(mGifTouchListener);
		
		images[0] = e1;
		images[1] = e2;
		images[2] = e3;
		images[3] = e4;
		images[4] = e5;
		images[5] = e6;
		
		mImageLoader = ImageLoader.getInstance();
		mOptions = ImageLoaderUtils.getMyVideoPreviewImageOption();
	}
	
	public void setImages(List<Dynamicimg> dimgs){
		for (int i=0; i<dimgs.size() && i<6; i++){
			images[i].setTag(dimgs.get(i));
			mImageLoader.displayImage(dimgs.get(i).getPreviewurl(), images[i], mOptions);
		}
	}
	
	private View mClickView;
	private AnimatorSet mClickAnim;

	private View.OnTouchListener mGifTouchListener = new View.OnTouchListener() {
		
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			mClickView = v;
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				Log.d(TAG, "ACTION_DOWN ");
				mHandler.postDelayed(mOnTapGif, 200);
				return true;
				
			case MotionEvent.ACTION_CANCEL:
			case MotionEvent.ACTION_UP:
				Log.d(TAG, "ACTION_UP  ACTION_CANCEL");
				mHandler.removeCallbacks(mOnTapGif);
				mHandler.removeCallbacks(mLongClick);
				return true;

			default:
				break;
			}
			return false;
		}
	};

	private Runnable mOnTapGif = new Runnable() {
		@Override
		public void run() {
			mClickAnim = new AnimatorSet();
			mClickAnim.playSequentially(
					ObjectAnimator.ofFloat(mClickView, "y", mClickView.getY(), mClickView.getY() - mClickView.getHeight()/2),
					ObjectAnimator.ofFloat(mClickView, "y", mClickView.getY() - mClickView.getHeight()/2, mClickView.getY())
			);
			mClickAnim.setDuration(200);
			mClickAnim.start();
			if (CacheBean.getInstance().getInt(getContext(), CacheKeyConstants.KEY_FIRST_DYNAMIC_CLICK, CacheKeyConstants.IS_FIRST) == CacheKeyConstants.IS_FIRST){
				CacheBean.getInstance().putInt(getContext(), CacheKeyConstants.KEY_FIRST_DYNAMIC_CLICK, CacheKeyConstants.NOT_FIRST);
				if (mListener != null){
					mListener.firstClick();
				}
			}
			mHandler.postDelayed(mLongClick, 150);
		}
	};
	
	private Runnable mLongClick = new Runnable() {
		
		@Override
		public void run() {
			mClickView.setVisibility(View.INVISIBLE);
			if (mListener != null){
				mListener.onExpression((Dynamicimg)mClickView.getTag());
			}
			mHandler.postDelayed(new Runnable() {
				
				@Override
				public void run() {
					mClickView.setVisibility(View.VISIBLE);
				}
			}, 200);
		}
	};
	
	private ExpressionListener mListener;
	
	public void setExpressionListener(ExpressionListener l) {
		mListener = l;
	}
}
