package com.bloomlife.android.network;

import java.util.HashMap;
import java.util.Map;

public abstract class AccessorInfo{
	public String addr;
	public Map<String,Object> params = new HashMap<String,Object>();
	public Map<String,String> files = new HashMap<String,String>();
	
	public AccessorInfo(){
		//clientid=3&format=json&ver=1.0&session=123
		params.put("clientid","3");
		params.put("format","json");
		params.put("ver", "1.0");
		params.put("session","123");
	}
}
