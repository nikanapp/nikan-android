///**
// * 
// */
//package com.bloomlife.videoapp.manager;
//
//import static com.bloomlife.videoapp.common.CacheKeyConstants.LOCATION_LAST_VISIABL_AREA;
//import static com.bloomlife.videoapp.common.CacheKeyConstants.LOCATION_LAST_ZOOM;
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
//import com.bloomlife.android.bean.ProcessResult;
//import com.bloomlife.android.executor.RequestAsyncTask;
//import com.bloomlife.videoapp.activity.BaseMapActivity;
//import com.bloomlife.videoapp.app.AppContext;
//import com.bloomlife.videoapp.common.BaiduConstants;
//import com.bloomlife.videoapp.common.CacheKeyConstants;
//import com.bloomlife.videoapp.model.CacheLatLng;
//import com.bloomlife.videoapp.model.MyLatLng;
//import com.bloomlife.videoapp.model.SysCode;
//import com.bloomlife.videoapp.model.message.GetVideoListMessage;
//import com.bloomlife.videoapp.model.message.MoreVideMessage;
//import com.bloomlife.videoapp.model.result.GetVideoListResult;
//import com.bloomlife.videoapp.model.result.MoreVideoResult;
//
///**
// * @author <a href="mailto:xiai.fei@gmail.com">xiai_fei</a>  
// *
// * @date 2014-12-24  下午7:16:03
// */
//public class BaiduMapVideoLoadController extends BaseMapVideoLoadController{
//	
//	private static final String TAG = "MapVideoLoaderController";
//	
//	public BaiduMapVideoLoadController(BaseMapActivity activity){
//		super(activity);
//	}
//	
//	private int mapViewHeight ;
//	
//	private int mapViewWidth ;
//	
//	public LatLng lastCenterPoint ;
//	
//	/***
//	 * 是否可以加载新的视频数据。
//	 * 		需要移动地图超过一定距离或者是缩放超过一个层级才会加载地图
//	 * @return
//	 */
//	public boolean isLoadVideo(Projection mProjection , MapStatus  status){
//		if(lastCenterPoint == null || mProjection == null) return true;
//		Point currentPoint= mProjection.toScreenLocation(status.target);
//		Point lastScreenPoint = mProjection.toScreenLocation(lastCenterPoint);
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
//		return false;
//	}
//	
//	
//	public void loadVideo(Projection projection,String topic){
//		if(projection==null){
//			Log.e(TAG, " 获取视频数据，但projection wie空，返回");
//			return ;
//		}
//		loadHotVideoList(projection, topic);
//		loadMoreVideos(projection,topic);
//	}
//	
//	public void loadVideo(MyLatLng bottomLatLng ,MyLatLng topLatLng, String topic){
//		loadHotVideoList(bottomLatLng, topLatLng, topic);
//		loadMoreVideos(bottomLatLng, topLatLng, topic);
//	}
//
//	/**
//	 * 获取热点视频列表
//	 */
//	private void loadHotVideoList(Projection projection, String topic){
//		LatLng mBottomLeft = projection.fromScreenLocation(new Point(0, mapViewHeight));
//		LatLng mTopRight = projection.fromScreenLocation(new Point(mapViewWidth, 0));
//		loadHotVideoList(new MyLatLng(mBottomLeft.latitude, mBottomLeft.longitude), new MyLatLng(mTopRight.latitude, mTopRight.longitude), topic);
//	}
//	
//	@Override
//	protected int getScale(){
//		int index = Math.round(currentZoomLevel);
//		return BaiduConstants.SCALE_DESCS[index-3];
//	}
//	
//	@Override
//	protected boolean isMaxLayer(){
//		return Math.abs(currentZoomLevel-AppContext.getSysCode().getMaxlevel(19))<0.1;
//	}
//	
//	private void loadMoreVideos(Projection projection, String topic){
//		LatLng mBottomLeft = projection.fromScreenLocation(new Point(0, mapViewHeight));
//		LatLng mTopRight = projection.fromScreenLocation(new Point(mapViewWidth, 0));
//		loadMoreVideos(new MyLatLng(mBottomLeft.latitude, mBottomLeft.longitude), new MyLatLng(mTopRight.latitude, mTopRight.longitude),topic);
//	}
//	
//	
//	public void setMapViewSize(MapView mapView){
//		this.mapViewHeight =  mapView.getHeight();
//		this.mapViewWidth = mapView.getWidth();
//	}
//
//	@Override
//	public void saveLatestParameter(MyLatLng curCenterPoint) {
//		super.saveLatestParameter(curCenterPoint);
//		lastCenterPoint = new LatLng(curCenterPoint.getLat(), curCenterPoint.getLon());
//	}
//}
