/**
 * 
 */
package com.bloomlife.android.bean;

import android.content.Context;
import android.content.SharedPreferences;

import com.alibaba.fastjson.JSON;
import com.bloomlife.android.common.util.StringUtils;

/**
 * @author <a href="mailto:xiai.fei@gmail.com">xiai_fei</a>  
 *
 * @date 2014-7-31  下午2:38:41
 */
public class AppParameter {
	
	public static final String KEY = "SP_KEY_APP";
	
	private SysCode sysCode;
	
	private static final String SP_KEY = "sp_key_app";
	
	public static  AppParameter saveToSp(SysCode sysCode , Context context ){
		AppParameter appParameter = new AppParameter() ;
		appParameter.setSysCode(sysCode);
		SharedPreferences sp = context.getSharedPreferences(KEY, Context.MODE_PRIVATE);
		sp.edit().putString(SP_KEY, JSON.toJSONString(appParameter)).commit();
		return appParameter;
	}
	
	public static AppParameter getInstanceFromSp(Context context){
		SharedPreferences sp = context.getSharedPreferences(KEY, Context.MODE_PRIVATE);
		String jsonStr = sp.getString(SP_KEY,null);
		if(StringUtils.isEmpty(jsonStr)) return null;
		return JSON.parseObject(jsonStr, AppParameter.class);
		
	}

	public SysCode getSysCode() {
		return sysCode;
	}

	public void setSysCode(SysCode sysCode) {
		this.sysCode = sysCode;
	}
	
	
}
