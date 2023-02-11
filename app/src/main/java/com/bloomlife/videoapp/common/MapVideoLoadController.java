///**
// * 
// */
//package com.bloomlife.videoapp.common;
//
//import static com.bloomlife.videoapp.common.CacheKeyConstants.LOCATION_LAST_VISIABL_AREA;
//import static com.bloomlife.videoapp.common.CacheKeyConstants.LOCATION_LAST_ZOOM;
//
//import java.util.concurrent.ConcurrentLinkedQueue;
//import java.util.concurrent.CopyOnWriteArrayList;
//
//import android.app.Activity;
//import android.graphics.Point;
//import android.util.Log;
//
//import com.baidu.mapapi.map.MapStatus;
//import com.baidu.mapapi.map.MapView;
//import com.baidu.mapapi.map.Projection;
//import com.baidu.mapapi.model.LatLng;
//import com.bloomlife.android.bean.BaseMessage;
//import com.bloomlife.android.bean.CacheBean;
//import com.bloomlife.android.common.util.Utils;
//import com.bloomlife.android.executor.RequestAsyncTask;
//import com.bloomlife.videoapp.activity.MainActivity;
//import com.bloomlife.videoapp.app.AppContext;
//import com.bloomlife.videoapp.model.CacheLatLng;
//import com.bloomlife.videoapp.model.Video;
//import com.bloomlife.videoapp.model.message.GetVideoListMessage;
//import com.bloomlife.videoapp.model.message.MoreVideMessage;
//import com.bloomlife.videoapp.model.result.GetVideoListResult;
//import com.bloomlife.videoapp.model.result.MoreVideoResult;
//import com.bloomlife.videoapp.model.result.MoreVideoResult.MoreVideoVo;
//
///**
// * 
// * 	地图视频数据加载控制器。控制是否进行地图数据加载
// * 
// * @author <a href="mailto:xiai.fei@gmail.com">xiai_fei</a>  
// *
// * @date 2014-12-12  下午4:26:01
// */
//@Deprecated
//public class MapVideoLoadController {
//	
//	private static final String TAG = "MapVideoLoaderController";
//	
//	public MapVideoLoadController(MainActivity activity , CopyOnWriteArrayList<Video> mVideoList,ConcurrentLinkedQueue<MoreVideoVo> mMoreList){
//		refreshDistance = (int) (AppContext.deviceInfo.getScreenWidth()*0.33);
//		this.mVideoList = mVideoList;
//		this.activity = activity;
//		this.mMoreList = mMoreList;
//	}
//	
//	private MainActivity activity ;
//	
//	private CopyOnWriteArrayList<Video> mVideoList ; 
//	
//	private ConcurrentLinkedQueue<MoreVideoVo> mMoreList;
//	
//	private CacheLatLng cacheLatLng ;
//	
//	private int mapViewHeight ;
//	
//	private int mapViewWidth ;
//	
//	public float lastZoomLevl;  //上一次缩放的层级
//	
//	public float currentZoomLevel;
//	
//	public LatLng lastCenterPoint ;
//	
//	public int refreshDistance ; //刷新的移动的像素的范围
//	
//	/**
//	 * 保存最新的一次加载视频数据时的参数
//	 * @param curCenterPoint
//	 */
//	public void saveLatestParameter(LatLng curCenterPoint ){
//		lastZoomLevl  = currentZoomLevel;
//		lastCenterPoint = curCenterPoint;
//	}
//
//
//	
//	/***
//	 * 是否可以加载新的视频数据。
//	 * 		需要移动地图超过一定距离或者是缩放超过一个层级才会加载地图
//	 * @return
//	 */
//	public boolean isLoadVideo(Projection mProjection , MapStatus  status){
//		if(lastCenterPoint==null) return true ;
//		Point currentPoint= mProjection.toScreenLocation(status.target);
//		Point lastScreenPoint = mProjection.toScreenLocation(lastCenterPoint);
//		
//		Log.d(TAG, "判断地图移动的距离，lastScreenPoint.x "+lastScreenPoint.x+
//				" lastScreenPoint.y"+lastScreenPoint.y+
//				" currentPoint.x "+currentPoint.x +
//				" currentPoint.y "+currentPoint.y );
//		if(Math.abs(lastScreenPoint.x-currentPoint.x)>refreshDistance||
//				Math.abs(lastScreenPoint.y-currentPoint.y)>refreshDistance){
//			return true ;
//		}
//
//		if (Math.abs(lastZoomLevl-status.zoom) > 1){ //缩放层级大于1
//			return true ;
//		}
//		
//		return false;
//	}
//	
//	
//	public void loadVideo(Projection projection,String topic){
//		if(projection==null){
//			Log.e(TAG, " 获取视频数据，但projection wie空，返回");
//		}
//		loadHotVideoList(projection, topic);
//		loadMoreVideos(projection,topic);
//	}
//	
//	public void loadVideo(LatLng mBottomLeft ,LatLng topRight, String topic){
//		loadHotVideoList(mBottomLeft, topRight, topic);
//		loadMoreVideos(mBottomLeft, topRight, topic);
//	}
//
//	/**
//	 * 获取热点视频列表
//	 */
//	private void loadHotVideoList(Projection projection, String topic){
//		LatLng mBottomLeft = projection.fromScreenLocation(new Point(0, mapViewHeight));
//		LatLng  mTopRight = projection.fromScreenLocation(new Point(mapViewWidth,0 ));
//		loadHotVideoList(mBottomLeft, mTopRight, topic);
//	}
//	
//	private int mMaxindex;
//	
//	/**
//	 * 获取热点视频列表
//	 */
//	private void loadHotVideoList(LatLng mBottomLeft ,LatLng topRight, String topic){
//		getCacheLatLng().saveBottomLeft(mBottomLeft);
//		getCacheLatLng().saveTopRight(topRight);
//		
//		saveLatestParameter( getCacheLatLng().getCenterPoint());
//		
//		CacheBean.getInstance().putObject(activity, LOCATION_LAST_VISIABL_AREA, getCacheLatLng());
//		CacheBean.getInstance().putString(activity, LOCATION_LAST_ZOOM, lastZoomLevl+"");
//		
//		Log.d(TAG, "左下坐标 纬度 "+mBottomLeft.latitude+" 经度 "+mBottomLeft.longitude+" 右上坐标 纬度 "+topRight.latitude+" 经度 "+topRight.longitude);
//
//		GetVideoListMessage message = new GetVideoListMessage(mBottomLeft,topRight);
//		message.setMaxindex(mMaxindex);
//		message.setTopic(topic);
//		new GetVideoListTask(activity, message).execute();
//	}
//	
//	private class GetVideoListTask extends RequestAsyncTask<GetVideoListResult> {
//
//		public GetVideoListTask(Activity activity, BaseMessage baseMessage) {
//			super(activity, baseMessage);
//		}
//
//		@Override
//		protected void onCheckPostExecute(GetVideoListResult result) {
//			mVideoList.clear();
//			if(!Utils.isEmptyCollection(result.getVideos()))mVideoList.addAll(result.getVideos());
////			mMaxindex = result.get
//			((MainActivity)act.get()).doLoadVideoSuccess(result.getVideos());
//		}
//
//		@Override
//		protected void onFinally() {
//			super.onFinally();
//		}
//
//	}
//	
//	private void loadMoreVideos(Projection projection,String topic){
//		LatLng mBottomLeft = projection.fromScreenLocation(new Point(0, mapViewHeight));
//		LatLng mTopRight = projection.fromScreenLocation(new Point(mapViewWidth, 0));
//		
//		loadMoreVideos(mBottomLeft,mTopRight,topic);
//	}
//	
//	/**
//	 * @param mBottomLeft	可视区域左下角
//	 * @param topRight	可视区域右上角
//	 */
//	private void loadMoreVideos(LatLng mBottomLeft ,LatLng topRight , String topic){
//		
//		MoreVideMessage message = new MoreVideMessage(mBottomLeft,topRight);
//		message.setTopic(topic);
//		new MoreVideoTask(activity, message).execute();
//	}
//
//	public CacheLatLng getCacheLatLng() {
//		return cacheLatLng;
//	}
//
//
//
//	public void setCacheLatLng(CacheLatLng cacheLatLng) {
//		this.cacheLatLng = cacheLatLng;
//	}
//
//	private class MoreVideoTask extends RequestAsyncTask<MoreVideoResult>{
//
//		public MoreVideoTask(Activity activity, BaseMessage baseMessage) {
//			super(activity, baseMessage);
//		}
//
//		@Override
//		protected void onCheckPostExecute(MoreVideoResult result) {
//			mMoreList.clear();
//			if(!Utils.isEmptyCollection(result.getVideos()))mMoreList.addAll(result.getVideos());
//			((MainActivity)act.get()).doLoadMoreVideoSuccess(result);
//		}
//		
//	}
//	
//	public void setMapViewSize(MapView mapView){
//		this.mapViewHeight =  mapView.getHeight();
//		this.mapViewWidth = mapView.getWidth();
//	}
//}
