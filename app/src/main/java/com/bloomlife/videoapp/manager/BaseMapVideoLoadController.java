/**
 * 
 */
package com.bloomlife.videoapp.manager;

import static com.bloomlife.videoapp.common.CacheKeyConstants.LOCATION_LAST_VISIABL_AREA;
import static com.bloomlife.videoapp.common.CacheKeyConstants.LOCATION_LAST_ZOOM;

import java.util.ArrayList;

import android.app.Activity;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.MessageRequest;
import com.android.volley.toolbox.Volley;
import com.bloomlife.android.bean.CacheBean;
import com.bloomlife.android.log.Logger;
import com.bloomlife.android.log.LoggerFactory;
import com.bloomlife.videoapp.activity.fragment.BaseMapFragment;
import com.bloomlife.videoapp.app.AppContext;
import com.bloomlife.videoapp.common.CacheKeyConstants;
import com.bloomlife.videoapp.model.CacheLatLng;
import com.bloomlife.videoapp.model.MyLatLng;
import com.bloomlife.videoapp.model.SysCode;
import com.bloomlife.videoapp.model.message.GetVideoListMessage;
import com.bloomlife.videoapp.model.message.MoreVideMessage;
import com.bloomlife.videoapp.model.result.GetVideoListResult;
import com.bloomlife.videoapp.model.result.MoreVideoResult;
import com.bloomlife.videoapp.model.result.MoreVideoResult.MoreVideoVo;

/**
 * @author <a href="mailto:lan4627@gmail.com">zxt</a>
 *
 * @date 2015年5月20日 上午10:36:30
 */
public abstract class BaseMapVideoLoadController {
	
	private static final String TAG = BaseMapVideoLoadController.class.getSimpleName();

	private Logger mLog = LoggerFactory.getLogger(TAG);

	private BaseMapFragment mFragment;

	private Activity mActivity;
	
	private CacheLatLng cacheLatLng;
	
	public int refreshDistance ; //刷新的移动的像素的范围
	
	protected double lastZoomLevl;  //上一次缩放的层级

	private double currentZoomLevel;

	private MessageRequest mHotVideoRequest;
	private MessageRequest mMoreVideoRequest;
	
	/**
	 * 
	 */
	public BaseMapVideoLoadController(BaseMapFragment fragment, Activity activity) {
		mFragment = fragment;
		mActivity = activity;
		SysCode sysCode = CacheBean.getInstance().getObject(activity, CacheKeyConstants.CONSTANT_SYSCODE_KEY, SysCode.class);
		refreshDistance = (int) (AppContext.deviceInfo.getScreenWidth() * sysCode.getMoveloadratio() / 100f);
	}
	
	protected abstract int getScale();
	
	protected abstract boolean isMaxLayer();
	
	public CacheLatLng getCacheLatLng() {
		return cacheLatLng;
	}


	public void setCacheLatLng(CacheLatLng cacheLatLng) {
		this.cacheLatLng = cacheLatLng;
	}

	/**
	 * 保存最新的一次加载视频数据时的参数
	 * @param curCenterPoint
	 */
	public void saveLatestParameter(MyLatLng curCenterPoint){
		lastZoomLevl  = currentZoomLevel;
	}
	
	/**
	 * 获取热点视频列表
	 */
	protected void loadHotVideoList(MyLatLng bottomLeft ,MyLatLng topRight, String topic){
		if (getCacheLatLng() == null)
			setCacheLatLng(new CacheLatLng());
		getCacheLatLng().saveBottomLeft(bottomLeft);
		getCacheLatLng().saveTopRight(topRight);
		
		saveLatestParameter(getCacheLatLng().getCenterPoint());
		
		CacheBean.getInstance().putObject(mActivity, LOCATION_LAST_VISIABL_AREA, getCacheLatLng());
		CacheBean.getInstance().putString(mActivity, LOCATION_LAST_ZOOM, lastZoomLevl + "");

		mLog.v("左下坐标 纬度 " + bottomLeft.getLat() + " 经度 " + bottomLeft.getLon() + " 右上坐标 纬度 " + topRight.getLat() + " 经度 " + topRight.getLon());

		GetVideoListMessage message = new GetVideoListMessage(bottomLeft, topRight);
		message.setTopic(topic);
		message.setScale(getScale());
		message.setIsmaxlayer(isMaxLayer());
		if (mHotVideoRequest != null)
			mHotVideoRequest.cancel();
		mHotVideoRequest = new MessageRequest(message, new GetVideoListener(mFragment));
		Volley.addToTagQueue(mHotVideoRequest);
	}
	
	/**
	 * @param bottomLeft	可视区域左下角
	 * @param topRight	可视区域右上角
	 */
	protected void loadMoreVideos(MyLatLng bottomLeft ,MyLatLng topRight , String topic){
		MoreVideMessage message = new MoreVideMessage(bottomLeft, topRight);
		message.setTopic(topic);
		message.setScale(getScale());
		message.setIsmaxlayer(isMaxLayer());
		if (mMoreVideoRequest != null)
			mMoreVideoRequest.cancel();
		mMoreVideoRequest = new MessageRequest(message, new GetMoreVideoListener(mFragment));
		Volley.addToTagQueue(mMoreVideoRequest);
	}

	public void setCurrentZoomLevel(double currentZoomLevel) {
		this.currentZoomLevel = currentZoomLevel;
	}

	public double getCurrentZoomLevel(){
		return this.currentZoomLevel;
	}

	private static class GetVideoListener extends MessageRequest.Listener<GetVideoListResult>{

		private static final String TAG = GetVideoListener.class.getSimpleName();

		private BaseMapFragment mFragment;

		public GetVideoListener(BaseMapFragment fragment){
			this.mFragment = fragment;
		}

		@Override
		public void start() {
			Logger.d(TAG, "GetVideoListTask start");
			mFragment.showProgress(true);
		}

		@Override
		public void finish() {
			Logger.d(TAG, "GetVideoListTask end");
		}

		@Override
		public void error(VolleyError error) {
			mFragment.showLoadFailTips();
		}

		@Override
		public void success(GetVideoListResult result) {
			if (result.getVideos() == null){
				Logger.e(TAG, "GetVideoListResult null");
				return;
			}
			mFragment.doLoadVideoSuccess(result);
		}

	}

	private static class GetMoreVideoListener extends MessageRequest.Listener<MoreVideoResult>{

		private static final String TAG = GetMoreVideoListener.class.getSimpleName();

		private BaseMapFragment mFragment;

		public GetMoreVideoListener(BaseMapFragment fragment){
			mFragment = fragment;
		}

		@Override
		public void start() {
			Logger.d(TAG, "MoreVideoTask start");
			mFragment.showProgress(true);
		}

		@Override
		public void success(MoreVideoResult result) {
			if (result.getVideos() == null)
				result.setVideos(new ArrayList<MoreVideoVo>());
			mFragment.doLoadMoreVideoSuccess(result);
		}

		@Override
		public void finish() {
			mFragment.showProgress(false);
			Logger.d(TAG, "MoreVideoTask end");
		}
	}

}
