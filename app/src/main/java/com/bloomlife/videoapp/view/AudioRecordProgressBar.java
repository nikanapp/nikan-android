/**
 * 
 */
package com.bloomlife.videoapp.view;

import java.lang.ref.WeakReference;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.bloomlife.android.common.util.UiUtils;
import com.bloomlife.videoapp.R;
import com.bloomlife.videoapp.app.AppContext;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Region;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings.Global;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.ImageView;

/**
 * @author <a href="mailto:lan4627@gmail.com">zxt</a>
 *
 * @date 2015年4月30日 下午2:38:38
 */
public class AudioRecordProgressBar extends ImageView {

	/**
	 * @param context
	 */
	public AudioRecordProgressBar(Context context) {
		super(context);
		init(context);
	}

	/**
	 * @param context
	 * @param attrs
	 */
	public AudioRecordProgressBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	/**
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	public AudioRecordProgressBar(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}
	/**
	 * 高度需要再增加一点，不然水波底部会有一点空白
	 */
	private static int BOTTOM_ADD = 2;
	
	private static int TEXT_PADDING_TOP = 48;
	private static int TEXT_SIZE = 32;
	
	private List<Point> mAmpQueue = new LinkedList<Point>();
	
	private Paint mBezerPaint;
	private Path mBezerPath;
	
	private volatile Paint mTextPaint;
	
	private PorterDuffXfermode mMode;
	
	private Bitmap mBackgroundBtimap;
	private Bitmap mBitmapCover;
	private Paint mBitmapPaint;
	
	private int mDrawTextY;
	private int mMaxWaveHeight;
	private int mMinWaveHeight;
	private int mDruationTextBottom;
	private String mDruationText;
	
	private volatile boolean mStart;
	private volatile int speed;
	private volatile int mProgress;
	
	private void init(Context context){
		mBezerPaint = new Paint();
		mBezerPaint.setAntiAlias(true);
		mBezerPaint.setStyle(Style.FILL_AND_STROKE);
		mBezerPaint.setColor(context.getResources().getColor(R.color.audio_blue));
		mBezerPath = new Path();
		
		mBitmapCover = BitmapFactory.decodeResource(getResources(), R.drawable.speakingtip);
		mBackgroundBtimap = getBackgroundBitmap(mBitmapCover.getWidth(), mBitmapCover.getHeight());
		
		mMode = new PorterDuffXfermode(Mode.DST_ATOP);
		
		mBitmapPaint = new Paint();
		
		mTextPaint = new Paint();
		mTextPaint.setTextSize(UiUtils.sp2px(context, TEXT_SIZE));
		mTextPaint.setAntiAlias(true);
		mTextPaint.setTextAlign(Align.CENTER);
		mTextPaint.setColor(context.getResources().getColor(R.color.audio_blue));
		mDrawTextY = UiUtils.dip2px(context, TEXT_PADDING_TOP);
		mDruationText = "60s";
		// 字体底部到圆形顶部的距离
		mDruationTextBottom = UiUtils.dip2px(getContext(), TEXT_PADDING_TOP);
		
		getViewTreeObserver().addOnGlobalLayoutListener(mGlobalListener);
	}
	
	private Bitmap getBackgroundBitmap(int width, int height){
		Bitmap bitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setColor(0xCDFFFFFF);
		paint.setStyle(Style.FILL_AND_STROKE);
		
		Canvas canvas = new Canvas(bitmap);
		canvas.drawCircle(width/2, height/2, width/2, paint);
		return bitmap;
	}
	
	private OnGlobalLayoutListener mGlobalListener = new OnGlobalLayoutListener(){

		@Override
		public void onGlobalLayout() {
			getLayoutParams().height = mBackgroundBtimap.getHeight();
			getLayoutParams().width = mBackgroundBtimap.getWidth();
			getViewTreeObserver().removeGlobalOnLayoutListener(this);
		}
		
	};
	
	private void resetAnim(){
		mAmpQueue.clear();
		initAllPoint();
	}
	
	public void reset(){
		mDruationText = "60s";
		mProgress = 60;
	}
	
	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		mMinWaveHeight = getMeasuredHeight() / 3 * 2;
		mMaxWaveHeight = mMinWaveHeight - mDruationTextBottom;
		initAllPoint();
	}
	 
	private void initAllPoint(){

		speed = getMeasuredWidth() / 16;
		for (int i=0; i<6; i++){
			mAmpQueue.add(new Point(i * getMeasuredWidth() / 2, mMinWaveHeight));
		}
		mBezerPath.reset();
		mBezerPath.lineTo(0, mMinWaveHeight);
		mBezerPath.lineTo(getMeasuredWidth(), mMinWaveHeight);
		mBezerPath.lineTo(getMeasuredWidth(), getMeasuredHeight() + BOTTOM_ADD);
		mBezerPath.lineTo(0, getMeasuredHeight() + BOTTOM_ADD);
		mBezerPath.close();
		invalidate();
	}
	
	public void addAmp(int amp){
		if (mStart){
			float point =  (1 - (amp / 32767f)) * mMaxWaveHeight + mDruationTextBottom;
			mAmpQueue.add(new Point(mAmpQueue.get(mAmpQueue.size()-1).x + getMeasuredWidth() / 2, point));
		}
	}
	
	public void setProgress(int sec){
		mProgress = sec;
	}
	
	private void bezier(){
		mBezerPath.reset();
//		Log.v("bezier", "bezier start x "+mAmpQueue.get(0).x+" y "+mAmpQueue.get(0).y);
		mBezerPath.moveTo(mAmpQueue.get(0).x, mAmpQueue.get(0).y);
		for (int i=1; i<mAmpQueue.size()-2; i+=3){
//			Log.v("bezier", "bezier x1 "+mAmpQueue.get(i).x+" y1 "+mAmpQueue.get(i).y+" x2 "+mAmpQueue.get(i+1).x+" y2 "+ mAmpQueue.get(i+1).y);
			mBezerPath.cubicTo(mAmpQueue.get(i).x, mAmpQueue.get(i).y, mAmpQueue.get(i+1).x, mAmpQueue.get(i+1).y, mAmpQueue.get(i+2).x, mAmpQueue.get(i+2).y);
		}
		Log.v("bezier", "bezier end " + mAmpQueue.size());
		mBezerPath.lineTo(mAmpQueue.get(mAmpQueue.size()-1).x, getMeasuredHeight()+BOTTOM_ADD);
		mBezerPath.lineTo(mAmpQueue.get(0).x, getMeasuredHeight()+BOTTOM_ADD);
		mBezerPath.close();
	}
	
	private void text(){
		mDruationText = mProgress+"s";
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		canvas.drawBitmap(mBitmapCover, 0, 0, mBitmapPaint);
		canvas.saveLayer(0, 0, getRight(), getBottom(), null,
				Canvas.ALL_SAVE_FLAG);
		mBitmapPaint.setXfermode(mMode);
		canvas.drawPath(mBezerPath, mBezerPaint);
		canvas.drawBitmap(mBackgroundBtimap, 0, 0, mBitmapPaint);
		canvas.drawText(mDruationText, getMeasuredWidth() / 2f, mDrawTextY, mTextPaint);
		mBitmapPaint.setXfermode(null);
		canvas.restore();
		super.onDraw(canvas);
	}
	
	private void drawWave(){
		if (mAmpQueue.get(0).x < -(speed * 8)){  
			mAmpQueue.remove(0);
			if (mAmpQueue.size() < 6){
				mAmpQueue.add(new Point(mAmpQueue.get(mAmpQueue.size()-1).x + getMeasuredWidth() / 2, mMinWaveHeight));
			}
		}
		for (Point p:mAmpQueue){
			p.x -= speed;
		}
		bezier();
		text();
		postInvalidate();
	}
	
	private MyHandler mHandler = new MyHandler(this);
	
	static class MyHandler extends Handler{
		
		private WeakReference<AudioRecordProgressBar> mReference;
		
		public MyHandler(AudioRecordProgressBar view){
			mReference = new WeakReference<AudioRecordProgressBar>(view);
		}

		@Override
		public void handleMessage(Message msg) {
			if (mReference.get() != null){
				mReference.get().drawWave();
			}
			super.handleMessage(msg);
		}
		
	}
	
	
	
	private Runnable mAnimRunnable = new Runnable() {
		
		@Override
		public void run() {
			while (mStart) {
				mHandler.sendEmptyMessage(1);
//				drawWave();
				try {
					Thread.sleep(AudioRecorderView.GET_AMP_SLEEP / 8 - 1);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	};
	
	class Point{
		float x;
		float y;
		
		public Point(){
			
		}
		
		public Point(float x, float y){
			this.x = x;
			this.y = y;
		}
	}
	
	public void show(){
		resetAnim();
		mStart = true;
		setVisibility(View.VISIBLE);
		AppContext.EXECUTOR_SERVICE.execute(mAnimRunnable);
	}
	
	public void hide(){
		mStart = false;
		setVisibility(View.INVISIBLE);
	}

}
