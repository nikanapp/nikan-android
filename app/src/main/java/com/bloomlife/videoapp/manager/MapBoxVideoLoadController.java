/**
 * 
 */
package com.bloomlife.videoapp.manager;


import com.bloomlife.android.log.Logger;
import com.bloomlife.android.log.LoggerFactory;
import com.bloomlife.videoapp.activity.fragment.BaseMapFragment;
import com.bloomlife.videoapp.app.AppContext;
import com.bloomlife.videoapp.common.BaiduConstants;
import com.bloomlife.videoapp.model.MapControllOption;
import com.bloomlife.videoapp.model.MyLatLng;
import com.mapbox.geojson.Point;
import com.mapbox.maps.MapView;
import com.mapbox.maps.MapboxMap;
import com.mapbox.maps.ScreenCoordinate;

import android.app.Activity;



/**
 * @author <a href="mailto:lan4627@gmail.com">zxt</a>
 *
 * @date 2015年5月20日 上午10:33:25
 */
public class MapBoxVideoLoadController extends BaseMapVideoLoadController{
	
	private Logger mLog = LoggerFactory.getLogger("MapBoxVideoLoadController");
	
	private int mapViewHeight;
	
	private int mapViewWidth;
	
	private Point lastCenterPoint;
	
	/**
	 * @param activity
	 */
	public MapBoxVideoLoadController(BaseMapFragment fragment, Activity activity) {
		super(fragment, activity);
	}
	
	public boolean isLoadVideo(MapboxMap mapboxMap, Point center, double zoom){
		if(lastCenterPoint == null || mapboxMap == null) return true;
		setCurrentZoomLevel(zoom);
		ScreenCoordinate currentPoint = mapboxMap.pixelForCoordinate(center);
		ScreenCoordinate lastScreenPoint = mapboxMap.pixelForCoordinate(lastCenterPoint);
		mLog.d("判断地图移动的距离，lastScreenPoint.x "+lastScreenPoint.getX()+
				" lastScreenPoint.y"+lastScreenPoint.getY()+
				" currentPoint.x "+currentPoint.getX() +
				" currentPoint.y "+currentPoint.getY() );
		if(Math.abs(lastScreenPoint.getX()-currentPoint.getX())>refreshDistance||
				Math.abs(lastScreenPoint.getY()-currentPoint.getY())>refreshDistance){
			mLog.d("isLoadVideo true scale "+getScale()+" zoom "+zoom);
			return true;
		}

		if (Math.abs(lastZoomLevl-zoom) > 1){ //缩放层级大于1
			mLog.d("isLoadVideo true zoom > 1 lastZoomLevl "+lastZoomLevl+" zoom "+zoom);
			return true;
		}
		mLog.d("isLoadVideo false");
		return false;
	}

	public void setMapViewSize(MapView mapView){
		this.mapViewHeight =  mapView.getHeight();
		this.mapViewWidth = mapView.getWidth();
	}

	@Override
	protected int getScale() {
		int index = (int) Math.round(getCurrentZoomLevel());
		if (index < 4){
			mLog.d("getScale error index "+index);
			index = 4;
		}
		if (index > 19){
			mLog.d("getScale error index "+index);
			index = 19;
		}
		return BaiduConstants.SCALE_DESCS[index-3];
	}

	@Override
	protected boolean isMaxLayer() {
		return Math.abs(getCurrentZoomLevel() - AppContext.getSysCode().getMaxlevel(MapControllOption.Default_Max_level)) <= 1;
	}
	
	public void loadVideo(MapboxMap mapboxMap, String topic){
		if(mapboxMap == null){
			mLog.e(" 获取视频数据，但projection wie空，返回");
			return;
		}
		loadHotVideoList(mapboxMap, topic);
		loadMoreVideos(mapboxMap,topic);
	}
	
	public void loadVideo(MyLatLng bottomLeft, MyLatLng topRight, String topic){
		loadHotVideoList(bottomLeft, topRight, topic);
		loadMoreVideos(bottomLeft, topRight, topic);
	}
	
	private void loadHotVideoList(MapboxMap mapboxMap, String topic){
		Point bottomLeft = mapboxMap.coordinateForPixel(new ScreenCoordinate(0, mapViewHeight));
		Point topRight = mapboxMap.coordinateForPixel(new ScreenCoordinate(mapViewWidth, 0));
		loadHotVideoList(new MyLatLng(bottomLeft.latitude(), bottomLeft.longitude()), new MyLatLng(topRight.latitude(), topRight.longitude()),topic);
	}
	
	private void loadMoreVideos(MapboxMap mapboxMap, String topic){
		Point bottomLeft = mapboxMap.coordinateForPixel(new ScreenCoordinate(0, mapViewHeight));
		Point topRight = mapboxMap.coordinateForPixel(new ScreenCoordinate(mapViewWidth, 0));
		loadMoreVideos(new MyLatLng(bottomLeft.latitude(), bottomLeft.longitude()), new MyLatLng(topRight.latitude(), topRight.longitude()),topic);
	}

	@Override
	public void saveLatestParameter(MyLatLng curCenterPoint) {
		super.saveLatestParameter(curCenterPoint);
		if (curCenterPoint == null) return;
		lastCenterPoint = Point.fromLngLat(curCenterPoint.getLon(), curCenterPoint.getLat());
	}
	

}
