/**
 * 
 */
package com.bloomlife.videoapp.common;

import java.util.Comparator;

import com.bloomlife.videoapp.model.MyLatLng;
import com.bloomlife.videoapp.model.Video;

/**
 * @author <a href="mailto:xiai.fei@gmail.com">xiai_fei</a>  
 *
 * @date 2014-12-5  下午6:29:41
 */
public class LatlngComparator implements Comparator<Video>{

	private MyLatLng centerLatLng;
	
	public LatlngComparator(MyLatLng centerLatLng){
		this.centerLatLng = centerLatLng;
	}
	
	@Override
	public int compare(Video lhs, Video rhs) {
		double llat = Math.abs(centerLatLng.getLat()-Double.parseDouble(lhs.getLat()));
		double llon = Math.abs(centerLatLng.getLon()-Double.parseDouble(lhs.getLon()));
		double ltotal = llat+llon;
		
		double rlat = Math.abs(centerLatLng.getLat()-Double.parseDouble(rhs.getLat()));
		double rlon = Math.abs(centerLatLng.getLon()-Double.parseDouble(rhs.getLon()));
		double rtotal = rlat+rlon;
		
		return ltotal>rtotal?1:-1;
	}

}
