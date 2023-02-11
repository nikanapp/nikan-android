/**
 * 
 */
package com.bloomlife.videoapp.view;

import java.lang.ref.WeakReference;

import com.bloomlife.android.common.util.UiUtils;
import com.bloomlife.videoapp.R;
import com.bloomlife.videoapp.app.AppContext;
import com.bloomlife.videoapp.common.util.UIHelper;
import com.bloomlife.videoapp.common.util.Utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * @author <a href="mailto:lan4627@gmail.com">zhengxingtian</a>
 * 首次进入时，性别比例视图
 * @date 2015年6月16日 下午4:10:34
 */
public class GenderScaleView extends LinearLayout {

	/**
	 * @param context
	 */
	public GenderScaleView(Context context) {
		super(context);
		init(context);
	}

	/**
	 * @param context
	 * @param attrs
	 */
	public GenderScaleView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	/**
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	public GenderScaleView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}
	
	private int mRedColor;
	private int mBlueColor;
	
	private int mArcWidth;
	private int mPadding;
	
	private Paint mBluePaint;
	private Paint mRedPaint;
	private RectF mBlueRectF;
	private RectF mRedRectF;
	
	private Paint mFirstAndLastPaint;
	private RectF mLastRedRectF;
	private RectF mFirstRedRectF;
	
	private Rect mBounds;
	private Bitmap mBitmap;
	private Matrix mMatrix;
	private int mTextPadding;
	
	private volatile float mProgress;
	private volatile float mTotalProgress;
	private int mMaleScale;
	private int mFemaleScale;
	
	private Paint mMaleScaleTextPaint;
	private Paint mFemaleScaleTextPaint;
	
	private static final int INVALIDATE = 0x99;
	
	private static final int SLEEP = 15;
	private static final int SEC = 1000;
	private static final int CIRCLE = 360;
	
	static class MyHandler extends Handler{
		
		private WeakReference<GenderScaleView> mReference;
		
		public MyHandler(GenderScaleView view){
			mReference = new WeakReference<GenderScaleView>(view);
		}

		@Override
		public void handleMessage(Message msg) {
			GenderScaleView view = mReference.get();
			if (view == null) return;
			switch (msg.what) {
			case INVALIDATE:
				
				break;

			default:
				break;
			}
			super.handleMessage(msg);
		}
		
	}
	
	private void init(Context context){
		inflate(context, R.layout.view_gender_scale, this);
		setGravity(Gravity.CENTER);
		Typeface tf = UIHelper.getBebas(context);
		
		mRedColor = context.getResources().getColor(R.color.view_sexscale_red);
		mBlueColor = context.getResources().getColor(R.color.view_sexscale_blue);
		
		mArcWidth = UiUtils.dip2px(context, 24);
		mPadding = mArcWidth/2;
		
		mBluePaint = new Paint();
		mBluePaint.setAntiAlias(true);
		mBluePaint.setColor(mBlueColor);
		mBluePaint.setStyle(Style.STROKE);
		mBluePaint.setStrokeWidth(mArcWidth);
		
		mRedPaint = new Paint();
		mRedPaint.setAntiAlias(true);
		mRedPaint.setColor(mRedColor);
		mRedPaint.setStyle(Style.STROKE);
		mRedPaint.setStrokeWidth(mArcWidth);
		
		mFirstAndLastPaint = new Paint();
		mFirstAndLastPaint.setAntiAlias(true);
		mFirstAndLastPaint.setColor(mRedColor);
		mFirstAndLastPaint.setStyle(Style.FILL);
		mFirstAndLastPaint.setStrokeWidth(mArcWidth/2);
		
		mBounds = new Rect();
		mMatrix = new Matrix();
		
		mMaleScaleTextPaint = new Paint();
		mMaleScaleTextPaint.setAntiAlias(true);
		mMaleScaleTextPaint.setColor(mBlueColor);
		mMaleScaleTextPaint.setStyle(Style.FILL);
		mMaleScaleTextPaint.setTypeface(tf);
		mMaleScaleTextPaint.setTextSize(UiUtils.sp2px(context, 32));
		
		mFemaleScaleTextPaint = new Paint();
		mFemaleScaleTextPaint.setAntiAlias(true);
		mFemaleScaleTextPaint.setColor(mRedColor);
		mFemaleScaleTextPaint.setStyle(Style.FILL);
		mFemaleScaleTextPaint.setTypeface(tf);
		mFemaleScaleTextPaint.setTextSize(UiUtils.sp2px(context, 32));
		
		mTextPadding = UiUtils.dip2px(context, 35);
	}
	
	@Override
	protected void dispatchDraw(Canvas canvas) {
		canvas.drawArc(mBlueRectF, 0, 360, false, mBluePaint);
		canvas.drawArc(mRedRectF, 269, mProgress+2, false, mRedPaint);
		canvas.drawArc(mLastRedRectF, 90, 180, false, mFirstAndLastPaint);
		
		mMatrix.setRotate(mProgress, mBitmap.getHeight()/2, mBitmap.getWidth()/2);
		canvas.drawBitmap(mBitmap, mMatrix, null);
		
		float scale = mProgress / mTotalProgress;
		
		String maleScaleStr = (int)(mMaleScale * scale) + "%";
		String femaleScaleStr = (int)(mFemaleScale * scale) + "%";
		
		mMaleScaleTextPaint.getTextBounds(maleScaleStr, 0, maleScaleStr.length(), mBounds);
		canvas.drawText(maleScaleStr, getMeasuredWidth()/2 - mBounds.width() /2 - mTextPadding, getMeasuredHeight()/2 + mBounds.height(), mMaleScaleTextPaint);
		
		mFemaleScaleTextPaint.getTextBounds(femaleScaleStr, 0, femaleScaleStr.length(), mBounds);
		canvas.drawText(femaleScaleStr, getMeasuredWidth()/2 + (mTextPadding - mBounds.width() /2), getMeasuredHeight()/2 + mBounds.height(), mFemaleScaleTextPaint);
		super.dispatchDraw(canvas);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		mBlueRectF = new RectF(mPadding, mPadding, getMeasuredWidth()-mPadding, getMeasuredHeight()-mPadding);
		mRedRectF = new RectF(mPadding, mPadding, getMeasuredWidth()-mPadding, getMeasuredHeight()-mPadding);
		mLastRedRectF = new RectF(getMeasuredWidth() / 2 - mPadding, 0, getMeasuredWidth() / 2 + mPadding, mArcWidth);
		mFirstRedRectF = new RectF(getMeasuredWidth() / 2 - mPadding, 0, getMeasuredWidth() / 2 + mPadding, mArcWidth);
		
		mBitmap = Bitmap.createBitmap(getMeasuredWidth(), getMeasuredHeight(), Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(mBitmap);
		canvas.drawArc(mFirstRedRectF, 270, 180, false, mFirstAndLastPaint);
		mMatrix.postRotate(0, mBitmap.getHeight()/2, mBitmap.getWidth()/2);
	}
	
	public void progress(float scale){
		mMaleScale = (int)(scale * 100);
		mFemaleScale = (int)((1- scale) * 100);
		mTotalProgress = (1-scale) * CIRCLE;
		AppContext.EXECUTOR_SERVICE.execute(new AnimatorRun(1-scale));
	}
	
	
	
	class AnimatorRun implements Runnable {
		
		private float addProgress;
		
		AnimatorRun(float scale){
			addProgress = mTotalProgress / (SEC / SLEEP);
		}
		
		@Override
		public void run() {
			mProgress = 0;
			while (mTotalProgress > mProgress) {
				try {
					mProgress += addProgress;
					postInvalidate();
					Thread.sleep(SLEEP);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

}
