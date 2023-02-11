/**
 * 
 */
package com.bloomlife.videoapp.model;

import static com.bloomlife.videoapp.common.CacheKeyConstants.LOCATION_LAST_ZOOM;
import android.content.Context;

import com.bloomlife.android.bean.CacheBean;
import com.bloomlife.android.common.util.StringUtils;
import com.bloomlife.videoapp.app.AppContext;


/**
 * 	地图控制的相关参数
 * 
 * @author <a href="mailto:xiai.fei@gmail.com">xiai_fei</a>  
 *
 * @date 2014-12-12  下午4:03:30
 */
public class MapControllOption {
	
	public static final  float Default_Max_level = 15 ;
	public static final  float Default_min_level = 4 ;
	
	public MapControllOption(){
	}

	public float maxZoomLevel ;
	
	public float minZoomLevel;
	
	public  float deviation = 3 ; //缩放的zoombar和百度缩放级别之间的误差。
	
	public int getZoomBarMaxLevle(){
		return (int) maxZoomLevel;
	}
	
	public int getZoomBarMinLevel(){
		return (int) minZoomLevel;
	}
	
	public float getZoomLevel(Context context) {
		SysCode sysCode =AppContext.getSysCode();
		String lastZoomLevel =CacheBean.getInstance().getString(context, LOCATION_LAST_ZOOM); //上一次保存的层级,取用户上一次离开地图时的那个位置
		final float defaultLevel = sysCode.getDefaultlevel(16);
		if(StringUtils.isEmpty(lastZoomLevel))lastZoomLevel = String.valueOf(defaultLevel) ; //如果为空，取默认层级
		float result = Float.parseFloat(lastZoomLevel);
		return result<3.0f?3.0f:result;
	}

}
