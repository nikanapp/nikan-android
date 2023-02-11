package com.bloomlife.videoapp.common.util;

public class LocationUtil {
	
	public static double intToDouble(int itude){
		return itude/1000000.0;
	}

	public static int doubleToInt(double itude){
		return (int) (itude * 1000000);
	}
}
