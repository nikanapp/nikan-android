/**
 * 
 */
package com.bloomlife.videoapp.view;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

import com.bloomlife.android.bean.CacheBean;
import com.bloomlife.android.common.util.UiUtils;
import com.bloomlife.videoapp.R;
import com.bloomlife.videoapp.app.AppContext;
import com.bloomlife.videoapp.common.Rotate3dAnimation;
import com.bloomlife.videoapp.common.util.ImageLoaderUtils;
import com.bloomlife.videoapp.common.util.UIHelper;
import com.bloomlife.videoapp.model.Dynamicimg;
import com.bloomlife.videoapp.view.dialog.ExpressionWindow;
import com.bloomlife.videoapp.view.dialog.ExpressionWindow.ExpressionListener;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewStub;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.FrameLayout;

/**
 * @author <a href="mailto:lan4627@gmail.com">zhengxingtian</a>
 * 视频播放页面的弹幕视图，包括弹幕选择窗口
 * @date 2015年4月15日 下午7:17:08
 */
public class DynamicalLayout extends FrameLayout {

	/**
	 * @param context
	 */
	public DynamicalLayout(Context context) {
		super(context);
		init(context);
	}

	/**
	 * @param context
	 * @param attrs
	 */
	public DynamicalLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	/**
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	public DynamicalLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}
	
	private static final String TAG = DynamicalLayout.class.getSimpleName();
	
	private static final int WINDOW_ANIM_DURATION = 200;
	
	private Map<Double, List<Dynamicimg>> mDynamicImgs = new HashMap<Double, List<Dynamicimg>>();
	/**
	 * 正在显示的GifImageView
	 */
	private List<GifImageView> mGifList = new ArrayList<GifImageView>();
	/**
	 * GifImageView的缓存队列
	 */
	private List<GifImageView> mGifCacheList = new LinkedList<GifImageView>();
	
	private int mGifHeight;
	
	private GifImageView mCurrentGif;
	private double mPlayTime;
	private String mGifId;
	private String mUserId;
	private String mGifUrl;

	private ViewStub mEwindowStub;
	private ExpressionWindow mEwindow;
	private FrameLayout mDLayout;
	private ImageLoader mImageLoader;
	private DisplayImageOptions mOptions;
	
	private boolean mIsShow;
	private boolean mIsShiel;
	
	private float mHeightScale;
	private float mWidthScale;
	
	private int mOffsetHeight;
	
	private void init(Context context){
		inflate(context, R.layout.view_dynamical, this);
		mEwindowStub = (ViewStub) findViewById(R.id.expression_window_stub);
		
		mDLayout = (FrameLayout) findViewById(R.id.expression_dynamicimg_layout);
		
		mGifHeight = UiUtils.dip2px(getContext(), 80);
		
		mUserId = CacheBean.getInstance().getLoginUserId();
		mImageLoader = ImageLoader.getInstance();
		mOptions = ImageLoaderUtils.getMyVideoPreviewImageOption();
		
		mOffsetHeight = (AppContext.deviceInfo.getScreenHeight() - context.getResources().getDisplayMetrics().heightPixels) / 2;
	}
	
	public void setDynamicalHeightScale(float scale){
		mHeightScale = scale;
	}
	
	public void setDynamicalWidthScale(float scale){
		mWidthScale = scale;
	}
	
	public void showWindow(){
		if (mEwindow == null){
			mEwindow = (ExpressionWindow) mEwindowStub.inflate();
			mEwindow.setExpressionListener(mExpressionListener);
			UIHelper.measure(mEwindow);
		}
		mEwindow.setAlpha(1);
		mEwindow.setVisibility(View.VISIBLE);
		Animation anim = new Rotate3dAnimation(180, 0, mEwindow.getMeasuredWidth()/2, mEwindow.getMeasuredHeight()/2, 180, false);
		anim.setDuration(WINDOW_ANIM_DURATION);
		mEwindow.startAnimation(anim);
		mIsShow = true;
		if (mDynamicImgListener != null)
			mDynamicImgListener.onShow();
	}
	
	public void hideWindow(){
		if (mEwindow == null) return;
		Animation anim = new AlphaAnimation(1, 0);
		anim.setAnimationListener(new AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				mEwindow.setVisibility(View.GONE);
				mEwindow.setAlpha(0);
			}
		});
		anim.setDuration(WINDOW_ANIM_DURATION);
		mEwindow.startAnimation(anim);
		mIsShow = false;
		if (mDynamicImgListener != null)
			mDynamicImgListener.onHide();
	}
	
	public boolean isShow(){
		return mIsShow;
	}
	
	public boolean isShiel(){
		return mIsShiel;
	}
	
	public void shielDynamic(boolean shiel){
		mIsShiel = shiel;
		mDLayout.setVisibility(shiel ? View.INVISIBLE : View.VISIBLE);
	}
	
	public void setDynamicImgs(Map<Double, List<Dynamicimg>> dynamicImgs){
		if (dynamicImgs != null)
			mDynamicImgs.putAll(dynamicImgs);
	}
	
	/**
	 * 移除所有我发的表情弹幕
	 */
	public void removeMyDynamic(){
		// 移除在表情集合里的表情弹幕
		for (List<Dynamicimg> imgs:mDynamicImgs.values()){
			for (int i=0; i<imgs.size(); i++){
				if (mUserId.equals(imgs.get(i).getUserid())){
					imgs.remove(i);
				}
			}
		}
		// 移除正在屏幕上显示的表情弹幕
		for (int i=0; i<mGifList.size(); i++){
			if (mUserId.equals(mGifList.get(i).getTag(R.id.gif_user_id))){
				GifImageView gif = mGifList.remove(i);
				mDLayout.removeView(gif);
				mGifCacheList.add(gif);
				i--;
			}
		}
	}
	
	public void showDynamicImg(int time){
		double playTime = new BigDecimal((time / 1000.0)).setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
		if (playTime == mPlayTime) 
			return;
		// 视频开始时，移除所有表情
		if (playTime <= 0.1){
			mDLayout.removeAllViews();
			// 把剩下的所有GifImageView都移入缓存队列
			while (!mGifList.isEmpty()) {
				mGifCacheList.add(mGifList.remove(0));
			}
		}
		mPlayTime = playTime;
		if (mIsShiel) return;
		addDynamicImg(mDynamicImgs.get(mPlayTime));
		removeGifImageView();
	}
	
	private void addDynamicImg(List<Dynamicimg> imgs){
		if (imgs == null) return;
		for (Dynamicimg img:imgs){
			GifImageView view = getGifView(img.getOriginalurl());
			view.setX(convertMyXPx((float)img.getX()));
			view.setY(convertMyYPx((float)img.getY()));
			mDLayout.addView(view);
			mGifList.add(view);
		}
	}
	
	/**
	 * 判断GIF是否播放完一次，播放完一次的移除掉
	 */
	private void removeGifImageView(){
		for (int i=0; i<mGifList.size(); i++){
			GifImageView gif = mGifList.get(i);
			GifDrawable drawable = (GifDrawable)gif.getDrawable();
			if (drawable == null) continue;
			// 如果gif动画播放完第一遍就移除掉
			long time = System.currentTimeMillis() - (long) gif.getTag();
			if (Math.abs(time - drawable.getDuration()) < 50 || time - drawable.getDuration() > 50){
				mDLayout.removeView(gif);
				// 移除后加入到缓存中
				mGifCacheList.add(mGifList.remove(i--));
			}
		}
	}
	
	private float convertMyXPx(float x){
		return mWidthScale * x;
	}
	
	private float convertMyYPx(float y){
		return mHeightScale * y - mOffsetHeight;
	}
	
	private float convertVideoXPx(float x){
		return x / mWidthScale;
	}
	
	private float convertVideoYPx(float y){
		return (y + mOffsetHeight) / mHeightScale;
	}
	
	private ExpressionListener mExpressionListener = new ExpressionListener(){

		@Override
		public void onExpression(Dynamicimg dimg) {
			mSelectDyn = true;
			hideWindow();
			mGifId = dimg.getImgid();
			mGifUrl = dimg.getOriginalurl();
			mCurrentGif = getGifView(dimg.getOriginalurl());
			mCurrentGif.measure(mCurrentGif.getLayoutParams().width, mCurrentGif.getLayoutParams().height);
			mCurrentGif.setX(mX - mCurrentGif.getMeasuredWidth() / 2);
			// 不能使用gif.getMeasuredHeight()，因为LayoutParams强制了高度为mGifHeight，使用getMeasuredHeight获取的高度不准确
			mCurrentGif.setY(mY - mGifHeight);
			// 添加到触摸layout，让表情随着手指移动。如果添加到表情layout会因为每次视频重新开始移除所有表情的影响，移着突然消失。
			addView(mCurrentGif);
		}

		@Override
		public void firstClick() {
			if (mDynamicImgListener != null)
				mDynamicImgListener.firstClick();
		}
	};
	
	private GifImageView getGifView(String url){
		GifImageView gif = createGifView();
		gif.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, mGifHeight));
		GifDrawable drawable = null;
		try {
			if (!TextUtils.isEmpty(url)){
				drawable = getGifDrawable(url);
			}
		} catch (IOException e) {
			e.printStackTrace();
			// 如果GIF图片读取出错则重新下载
			mImageLoader.loadImage(url, mOptions, new DownLoadListener(gif));
		}
		if (drawable != null)
			gif.setImageDrawable(drawable);
		return gif;
	}
	
	private GifDrawable getGifDrawable(String url) throws IOException{
		@SuppressWarnings("deprecation")
		File file = mImageLoader.getDiskCache().get(url);
		if (file != null)
			return new GifDrawable(file);
		else
			return null;
	}
	
	private GifImageView createGifView(){
		GifImageView gif = null;
		// 如果缓存为空则创建新对象
		if (mGifCacheList.isEmpty()){
			gif = new GifImageView(getContext());
		} else {
			gif = mGifCacheList.remove(0);
		}
		gif.setTag(System.currentTimeMillis());
		return gif;
	}
	
	private float mX;
	private float mY;
	private boolean mSelectDyn;

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		if ((mEwindow == null || mEwindow.getVisibility() != View.VISIBLE) && mCurrentGif == null) return super.dispatchTouchEvent(ev);
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			mX = ev.getX();
			mY = ev.getY();
			break;
			
		case MotionEvent.ACTION_MOVE:
			float moveX = ev.getX();
			float moveY = ev.getY();
			if (mCurrentGif != null){
				getParent().requestDisallowInterceptTouchEvent(true);
				mCurrentGif.setX(moveX - mCurrentGif.getMeasuredWidth() / 2);
				mCurrentGif.setY(moveY - mGifHeight);
				mX = moveX;
				mY = moveY;
			}
			break;
			
		case MotionEvent.ACTION_UP:
			// 松手的时候添加表情
			if (mCurrentGif != null){
				// 移除触摸layout的表情
				removeView(mCurrentGif);
				// 添加到表情layout
				mDLayout.addView(mCurrentGif);
				mGifList.add(mCurrentGif);
				// 重新开始动画
				((GifDrawable)mCurrentGif.getDrawable()).reset();
				mCurrentGif.setTag(System.currentTimeMillis());
				// 添加到动画队列里，同时发往后台
				addDynamicImg(mX - mCurrentGif.getMeasuredWidth() / 2, mY - mGifHeight);
				mCurrentGif = null;
				getParent().requestDisallowInterceptTouchEvent(false);
				mSelectDyn = false;
			}
			break;

		default:
			break;
		}
		return super.dispatchTouchEvent(ev);
	}
	
	/**
	 * 是否选中表情弹幕
	 * @return
	 */
	public boolean isSelectDynamicImg(){
		return mSelectDyn;
	}
	
	private void addDynamicImg(float x, float y){
		if (mIsShiel) return;
		Dynamicimg dimg = new Dynamicimg();
		dimg.setUserid(CacheBean.getInstance().getLoginUserId());
		dimg.setImgid(mGifId);
		dimg.setX(convertVideoXPx(x));
		dimg.setY(convertVideoYPx(y));
		dimg.setOriginalurl(mGifUrl);
		dimg.setPlaytime(mPlayTime);
		// 把弹幕表情添加进视频的表情弹幕集合里
		List<Dynamicimg> imgs = mDynamicImgs.get(mPlayTime);
		if (imgs == null){
			imgs = new ArrayList<Dynamicimg>();
		} 
		imgs.add(dimg);
		mDynamicImgs.put(mPlayTime, imgs);
		// 把弹幕表情信息发送到服务器
		if (mDynamicImgListener != null)
			mDynamicImgListener.onDynamicImg(mGifId, convertVideoXPx(x), convertVideoYPx(y), mPlayTime);
	}
	
	private OnDynamicImgListener mDynamicImgListener;
	
	public void setOnDynamicImgListener(OnDynamicImgListener l){
		mDynamicImgListener = l;
	}
	
	public interface OnDynamicImgListener{
		void onDynamicImg(String id, double x, double y, double playTime);
		void onShow();
		void onHide();
		void firstClick();
	}
	
	class DownLoadListener implements ImageLoadingListener {
		
		GifImageView mGifView;
		
		private DownLoadListener(GifImageView gif){
			mGifView = gif;
		}

		@Override
		public void onLoadingStarted(String imageUri, View view) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onLoadingFailed(String imageUri, View view,
				FailReason failReason) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onLoadingComplete(String imageUri, View view,
				Bitmap loadedImage) {
			GifDrawable drawable = null;
			try {
				drawable = getGifDrawable(imageUri);
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (drawable != null)
				mGifView.setImageDrawable(drawable);
		}

		@Override
		public void onLoadingCancelled(String imageUri, View view) {
			// TODO Auto-generated method stub
			
		}
		
	}
	
}
