/**
 * 
 */
package com.bloomlife.videoapp.manager;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Point;
import android.view.View;
import android.view.animation.OvershootInterpolator;

import com.bloomlife.videoapp.model.MyLatLng;
import com.bloomlife.videoapp.model.Video;
import com.bloomlife.videoapp.view.DescriptionLayoutView;

/**
 *   
 *
 * @author <a href="mailto:xiai.fei@gmail.com">xiai_fei</a>  
 *
 * @date 2015-3-26 上午11:37:58
 *
 * @organization bloomlife  
 * @version 1.0
 */
public class PreviewImageRandomShower extends RandomShower{
	
	private static final String TAG = PreviewImageRandomShower.class.getSimpleName();
	
	private int playDotWidth;
	
	private View mRandomVideoPreview;
	private boolean mIsShowRandomVideoPreview;
	
	/**
	 * @param displayView1
	 * @param displayView2
	 * @param displayView3
	 * @param playDotHeight
	 * @param context
	 */
	public PreviewImageRandomShower(View displayView1, View displayView2, View displayView3,
			int playDotHeight, Context context) {
		super(displayView1, displayView2, playDotHeight, context);
		mRandomVideoPreview = displayView3;
	}

	
	protected void showDislayPosition(View view, Point point, Video video) {
		DescriptionLayoutView display = (DescriptionLayoutView) view ;
		display.clearAnimation();
		display.setContent(video);
		display.setX(point.x - display.getWidth() / 2);
		display.setY(point.y - display.getHeight() - playDotHeight - getMarginTop(8f));
		display.setVisibility(View.VISIBLE);
		makeScaleAnimation(display,display.getX(), display.getY());
		display.setTag(video);
		
	}
	
	public void showRandomVideoPreview(Video video, GetScreenPoint getScreenPoint){
		if (video == null || getScreenPoint == null) return;
		Point point = getScreenPoint.toPoint(new MyLatLng(Double.parseDouble(video.getLat()), Double.parseDouble(video.getLon())));
		setAlwaysDisplay(point);
		showDislayPosition(mRandomVideoPreview, point, video);
		mIsShowRandomVideoPreview = true;
		clearDynamicDisplayView();
	}

	@Override
	public boolean isShow() {
		return super.isShow() || mRandomVideoPreview.getVisibility() == View.VISIBLE;
	}

	public void hideRandomVideoPreview(){
		mIsShowRandomVideoPreview = false;
		setAlwaysDisplay(null);
		clearShow(mRandomVideoPreview);
	}
	
	public boolean isShowRandomVideoPreview(){
		return this.mIsShowRandomVideoPreview;
	}
	
	public int getPlayDotWidth() {
		return playDotWidth;
	}


	public void setPlayDotWidth(int playDotWidth) {
		this.playDotWidth = playDotWidth;
	}

	@Override
	public void stopShow() {
		super.stopShow();
		hideRandomVideoPreview();
	}

}
