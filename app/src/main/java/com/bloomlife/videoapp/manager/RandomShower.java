/**
 * 
 */
package com.bloomlife.videoapp.manager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.animation.AnticipateOvershootInterpolator;
import android.widget.TextView;

import com.bloomlife.android.common.util.StringUtils;
import com.bloomlife.android.common.util.Utils;
import com.bloomlife.videoapp.R;
import com.bloomlife.videoapp.app.AppContext;
import com.bloomlife.videoapp.model.MyLatLng;
import com.bloomlife.videoapp.model.Video;

/**
 * 	固定采用两个view随机显示的控制器
 * 
 * @author <a href="mailto:xiai.fei@gmail.com">xiai_fei</a>
 * 
 * @date 2014-12-9 下午7:25:09
 */
public class RandomShower {
	
	private static final String TAG = "RandomShow";

	ScheduledFuture<?> scheduledFuture;

	private List<Video> showDescList = new ArrayList<Video>();

	protected int displayWidth;
	protected int displayHight;

	private Handler handler;

	protected int playDotHeight;

	private GetScreenPoint mGetScreenPoint;

	private final int screeWidth;

	private View displayView1;

	private View displayView2;
	
	private ScheduledExecutorService executorService =Executors.newScheduledThreadPool(1);
	
	private Context context ;
	
	private float mDensity;
	
	/** 需要被一直显示的点 **/
	private Point mAlwaysDisplayPoint;

	public RandomShower(View displayView1, View displayView2, int playDotHeight, Context context) {
		this.playDotHeight = playDotHeight;
		this.displayView1 = displayView1;
		this.displayView2 = displayView2;
		this.context = context ;

		Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.display_background);
		displayHight = bitmap.getHeight();
		displayWidth = bitmap.getWidth();
		bitmap.recycle();

		this.screeWidth = AppContext.deviceInfo.getScreenWidth();
		
		mDensity = context.getResources().getDisplayMetrics().density;

		handler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				if(scheduledFuture==null) return ;
				randomShowDisplay();
			}

		};
	}

	public void setDisplayViewToShow(List<Video> mVideoList, GetScreenPoint getScreenPoint) {
		Log.d(TAG, " setDisplayViewToShow ");
		if (getScreenPoint == null||Utils.isEmptyCollection(mVideoList)) return;
		this.mGetScreenPoint = getScreenPoint;
		stopShow();

		double lat = 0;
		double lon = 0;
		showDescList.clear();
		showedList.clear();
		for (Video video : mVideoList) {
			if(StringUtils.isEmpty(video.getVideoid())||StringUtils.isEmpty(video.getDescription())) continue ; //有可能是自己发的视频，没有videoid的，所以不要弹窗
			lat = Double.parseDouble(video.getLat());
			lon = Double.parseDouble(video.getLon());
			MyLatLng latLng = new MyLatLng(lat, lon);
			Point point = getScreenPoint.toPoint(latLng);
			if (point == null) return;
			if ((point.x ) <= 0 || (point.x ) >= screeWidth)
				continue; // 至少显示四分之三宽度
			if (point.y - displayHight / 2 - 10 < 0)
				continue; // 至少显示 四分之2的高度
			showDescList.add(video);
		}
//		Collections.sort(mVideoList,new LatlngComparator(cacheLatLng.getCenterPoint())); //排序 , 可以排序，在这里
		if(!showDescList.isEmpty()){
			if(showDescList.size()==1){
				Video video = showDescList.get(0);
				MyLatLng latLng = new MyLatLng(Double.parseDouble(video.getLat()), Double.parseDouble(video.getLon()));
				Point point = getScreenPoint.toPoint(latLng);
				showDislayPosition(displayView1, point, video);
			} else 	scheduledFuture = starctScheduleDisplay();
		}
	}

	private List<Video> showedList = new ArrayList<Video>();

	/**
	 * 随机显示视频描述
	 */
	private void randomShowDisplay() {
		if (showDescList.isEmpty()) {
			showDescList.addAll(showedList);
			showedList.clear();
		}
		if (showDescList.isEmpty())
			return;
		Video video = showDescList.remove(0);

		MyLatLng latLng = new MyLatLng(Double.parseDouble(video.getLat()), Double.parseDouble(video.getLon()));
		Point point = mGetScreenPoint.toPoint(latLng);

		Point secondPoint = null;
		Video secondVideo = null;
		// 判断是否与固定显示弹窗的视频位置冲突
		if (!isShowSecondDisplay(point, mAlwaysDisplayPoint)) {
			clearDynamicDisplayView();
			return;
		}
		for (int i = 0, n = showDescList.size(); i < n; i++) {
			secondVideo = showDescList.get(i);
			secondPoint = latLngToPoint(secondVideo.getLat(), secondVideo.getLon());
			if (!isShowSecondDisplay(point, secondPoint) || !isShowSecondDisplay(point, mAlwaysDisplayPoint)) {
				secondPoint = null ;
				secondVideo = null ;
				continue;
			}
			break;
		}

		showedList.add(video);

		if (secondVideo != null) {
			showDescList.remove(secondVideo);
			showedList.add(secondVideo);

			if (point.y < secondPoint.y) { // 第一个点在下发
				showDislayPosition(displayView1, point,video);
				showDislayPosition(displayView2, secondPoint,secondVideo);
			} else {
				showDislayPosition(displayView1, secondPoint,secondVideo);
				showDislayPosition(displayView2, point,video);
			}
		} else {
			showDislayPosition(displayView1, point,video);
			clearShow(displayView2);
		}
	}

	protected void clearDynamicDisplayView(){
		clearShow(displayView1);
		clearShow(displayView2);
	}


	/**
	 * @param point
	 * @param secondPoint
	 * @return  
	 */
	protected boolean isShowSecondDisplay(Point point, Point secondPoint) {
		if (secondPoint == null) return true;
		return Math.abs(point.x - secondPoint.x) > displayWidth / 2 || Math.abs(point.y - secondPoint.y) > displayHight / 2;
	}
	

	protected void showDislayPosition(View view, Point point , Video video) {
		TextView display = (TextView) view;
		display.clearAnimation();
		display.setText(video.getDescription());
		display.setX(point.x - displayWidth / 2);
		display.setY(point.y - displayHight - playDotHeight - getMarginTop(2.5f));
		display.clearAnimation();
		display.setVisibility(View.VISIBLE);
//		display.setAnimation(makeScaleAnimation(point.x, point.y));
		makeScaleAnimation(display,display.getX(), display.getY());
		display.setTag(video);
	}
	
	protected int getMarginTop(float num){
		return (int) (num * mDensity + 0.5);
	}

	public  void makeScaleAnimation(View view ,float x, float y) {
		AnimatorSet mGridViewIn = new AnimatorSet();
		mGridViewIn.playTogether(
				ObjectAnimator.ofFloat(view, "scaleX", 0.0f, 1.0f),
				ObjectAnimator.ofFloat(view, "scaleY", 0.0f, 1.0f),
				ObjectAnimator.ofFloat(view, "translationY", y+view.getHeight()*0.85f ,y )
				);
		mGridViewIn.setInterpolator(new AnticipateOvershootInterpolator());
		mGridViewIn.setDuration(250);
		mGridViewIn.start();
	}

	private ScheduledFuture<?> starctScheduleDisplay() {
		if(Utils.isEmptyCollection(showDescList)) {
			Log.e(TAG, " starctScheduleDisplay , but showDescList is null ");
		}
		return executorService.scheduleAtFixedRate(new Runnable() {

			@Override
			public void run() {
				handler.sendEmptyMessage(0);
			}

		}, 800, 2300, TimeUnit.MILLISECONDS);
	}

	public void stopShow() {
		if (scheduledFuture != null && !scheduledFuture.isCancelled()){
			scheduledFuture.cancel(true);
			scheduledFuture = null ;
		}
		clearShow(displayView1);
		clearShow(displayView2);
	}

	protected void clearShow(View textView){
		textView.clearAnimation();
		textView.setVisibility(View.INVISIBLE);
	}
	
	public boolean isShow() {
		if (displayView1.getVisibility() == View.VISIBLE || displayView2.getVisibility() == View.VISIBLE)
			return true;
		return scheduledFuture == null ? false : !scheduledFuture.isCancelled();
	}
	
	public interface GetScreenPoint{
		Point toPoint(MyLatLng l);
	}
	
	/**
	 * 设置一个需要被一直显示弹窗的视频
	 * @param point
	 */
	public void setAlwaysDisplay(Point point){
		mAlwaysDisplayPoint = point;
		if (point == null) return; 
		// 清除弹窗列表中位置与固定显示的点接近的视频
		for (int i=0; i<showDescList.size(); i++){
			Video v = showDescList.get(i);
			if (!isShowSecondDisplay(point, latLngToPoint(v.getLat(), v.getLon()))){
				showDescList.remove(i);
				i--;
			}
		}
	}
	
	private Point latLngToPoint(String lat, String lon){
		return mGetScreenPoint.toPoint(new MyLatLng(Double.parseDouble(lat), Double.parseDouble(lon)));
	}
}
