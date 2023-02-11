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
import com.mapbox.mapboxsdk.api.ILatLng;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.views.MapView;
import com.mapbox.mapboxsdk.views.util.Projection;

import android.app.Activity;
import android.graphics.PointF;
import android.util.Log;



/**
 * @author <a href="mailto:lan4627@gmail.com">zhengxingtian</a>
 *
 * @date 2015年5月20日 上午10:33:25
 */
public class MapBoxVideoLoadController extends BaseMapVideoLoadController{
	
	private Logger mLog = LoggerFactory.getLogger("MapBoxVideoLoadController");
	
	private int mapViewHeight;
	
	private int mapViewWidth;
	
	private ILatLng lastCenterPoint;
	
	/**
	 * @param activity
	 */
	public MapBoxVideoLoadController(BaseMapFragment fragment, Activity activity) {
		super(fragment, activity);
	}
	
	public boolean isLoadVideo(Projection mProjection, ILatLng center, float zoom){
		if(lastCenterPoint == null || mProjection == null) return true;
		setCurrentZoomLevel(zoom);
		PointF currentPoint= mProjection.toPixels(center, null);
		PointF lastScreenPoint = mProjection.toPixels(lastCenterPoint, null);
		mLog.d("判断地图移动的距离，lastScreenPoint.x "+lastScreenPoint.x+
				" lastScreenPoint.y"+lastScreenPoint.y+
				" currentPoint.x "+currentPoint.x +
				" currentPoint.y "+currentPoint.y );
		if(Math.abs(lastScreenPoint.x-currentPoint.x)>refreshDistance||
				Math.abs(lastScreenPoint.y-currentPoint.y)>refreshDistance){
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
		int index = Math.round(getCurrentZoomLevel());
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
	
	public void loadVideo(Projection projection, String topic){
		if(projection == null){
			mLog.e(" 获取视频数据，但projection wie空，返回");
			return;
		}
		loadHotVideoList(projection, topic);
		loadMoreVideos(projection,topic);
	}
	
	public void loadVideo(MyLatLng bottomLeft, MyLatLng topRight, String topic){
		loadHotVideoList(bottomLeft, topRight, topic);
		loadMoreVideos(bottomLeft, topRight, topic);
	}
	
	private void loadHotVideoList(Projection projection, String topic){
		ILatLng bottomLeft = projection.fromPixels(0, mapViewHeight);
		ILatLng topRight = projection.fromPixels(mapViewWidth, 0);
		loadHotVideoList(new MyLatLng(bottomLeft.getLatitude(), bottomLeft.getLongitude()), new MyLatLng(topRight.getLatitude(), topRight.getLongitude()),topic);
	}
	
	private void loadMoreVideos(Projection projection, String topic){
		ILatLng bottomLeft = projection.fromPixels(0, mapViewHeight);
		ILatLng topRight = projection.fromPixels(mapViewWidth, 0);
		loadMoreVideos(new MyLatLng(bottomLeft.getLatitude(), bottomLeft.getLongitude()), new MyLatLng(topRight.getLatitude(), topRight.getLongitude()),topic);
	}

	@Override
	public void saveLatestParameter(MyLatLng curCenterPoint) {
		super.saveLatestParameter(curCenterPoint);
		if (curCenterPoint == null) return;
		lastCenterPoint = new LatLng(curCenterPoint.getLat(), curCenterPoint.getLon());
	}
	

}
