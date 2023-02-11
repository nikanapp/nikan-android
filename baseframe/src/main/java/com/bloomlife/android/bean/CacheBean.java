/**
 * 
 */
package com.bloomlife.android.bean;

import java.util.HashMap;
import java.util.Map;

import android.accounts.Account;
import android.content.Context;
import android.content.SharedPreferences;

import com.alibaba.fastjson.JSON;
import com.bloomlife.android.common.CacheKeyConstants;
import com.bloomlife.android.common.util.StringUtils;

/**
 * @author <a href="mailto:xiai.fei@gmail.com">xiai_fei</a>
 * 
 * @date 2014-8-11 下午3:15:50
 */
public class CacheBean {

	private final static CacheBean cacheBean = new CacheBean();

	private static final String KEY = "BLOOMLIFE";

	public static final String NO_LOGIN = "-1";

	private CacheBean() {
	}

	private final Map<String, Object> cachaMap = new HashMap<String, Object>();

	public static CacheBean getInstance() {
		return cacheBean;
	}

	public void init(Context context) {
		SharedPreferences sp = context.getSharedPreferences(KEY, Context.MODE_PRIVATE);
		loginUserId = sp.getString("loginUserId", NO_LOGIN);
	}

	private String loginUserId;
	private boolean syncParam;

	public String getLoginUserId() {
		return loginUserId;
	}
	
	public void setLoginUserId(Context context, int loginUserId) {
		if(context==null) return ;
		SharedPreferences sp = context.getSharedPreferences(KEY, Context.MODE_PRIVATE);
		sp.edit().putInt("loginUserId", loginUserId).commit();
		this.loginUserId = String.valueOf(loginUserId);
	}
	
	public void setLoginUserId(Context context, String loginUserId) {
		if(context==null) return ;
		SharedPreferences sp = context.getSharedPreferences(KEY, Context.MODE_PRIVATE);
		sp.edit().putString("loginUserId", loginUserId).commit();
		this.loginUserId = loginUserId;
	}

	public boolean hasLoginUserId() {
		if(StringUtils.isEmpty(loginUserId)) return false ;
		return NO_LOGIN.equals(loginUserId)?false:true;
	}
	
	public void clearLoginUserId(Context context){
		SharedPreferences sp = context.getSharedPreferences(KEY, Context.MODE_PRIVATE);
		sp.edit().putString("loginUserId", NO_LOGIN).commit();
		this.loginUserId = "-1";
	}

	public int getInt(Context context, String key, int defaultValue) {
		if (context==null) return defaultValue;
		if (cachaMap.get(key) != null)
			return (Integer) cachaMap.get(key);
		SharedPreferences sp = context.getSharedPreferences(KEY, Context.MODE_PRIVATE);
		return sp.getInt(key, defaultValue);
	}

	public String getString(Context context, String key) {
		if (cachaMap.get(key) != null) return (String) cachaMap.get(key);
		if (context==null) return null;
		SharedPreferences sp = context.getSharedPreferences(KEY, Context.MODE_PRIVATE);
		return sp.getString(key, "");
	}

	public String getString(Context context, String key, String defaultValue) {
		if (cachaMap.get(key) != null) 	return (String) cachaMap.get(key);
		if (context==null) return null;
		SharedPreferences sp = context.getSharedPreferences(KEY, Context.MODE_PRIVATE);
		return sp.getString(key, defaultValue);
	}

	public void putInt(Context context, String key, int value) {
		SharedPreferences sp = context.getSharedPreferences(KEY, Context.MODE_PRIVATE);
		sp.edit().putInt(key, value).commit();
		cachaMap.put(key, value);
	}

	public void putString(Context context, String key, String value) {
		SharedPreferences sp = context.getSharedPreferences(KEY, Context.MODE_PRIVATE);
		sp.edit().putString(key, value).commit();
		cachaMap.put(key, value);
	}

	public void putObject(Context context, String key, Object value) {
		if (value == null)
			return;
		String jsonStr = JSON.toJSONString(value);
		SharedPreferences sp = context.getSharedPreferences(KEY, Context.MODE_PRIVATE);
		sp.edit().putString(key, jsonStr).commit();
		cachaMap.put(key, value);
	}

	public <T> T getObject(Context context, String key, Class<T> clz) {
		if (cachaMap.get(key) != null) return (T) cachaMap.get(key);
		SharedPreferences sp = context.getSharedPreferences(KEY, Context.MODE_PRIVATE);
		String jsonStr = sp.getString(key, "");
		if (StringUtils.isEmpty(jsonStr)) return null;
		T value = JSON.parseObject(jsonStr, clz);
		cachaMap.put(key, value);
		return value;
	}

	public boolean isSyncParam() {
		return syncParam;
	}

	public void setSyncParam(boolean syncParam) {
		this.syncParam = syncParam;
	}
}
