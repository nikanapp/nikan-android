/**
 * 
 */
package com.bloomlife.android.common.util;

import java.lang.reflect.Field;

import android.content.SharedPreferences;
import android.util.Log;

/**
 * @author <a href="mailto:xiai.fei@gmail.com">xiai_fei</a>  
 *
 * @date 2013-12-17  上午11:32:23
 */
public class ObjectUtils {
	
	public static final String TAG = "objectutils"; 

	
	public static void toSharedPreferences(Object o , SharedPreferences sp){
		Class<?> clz = o.getClass();
		try {
			Field[] fields = clz.getDeclaredFields();
			for (Field field : fields) {
				setFieldToSp(o , sp, field);
			}
			
			Field[] parentFiled = clz.getSuperclass().getDeclaredFields();
			for (Field field : parentFiled) {
				 setFieldToSp(o,sp, field);
			}
		} catch (Exception e) {
			Log.e("实体转换","转化实体为sp异常", e);
		}
	}
	
	public static void spToObject(Object o , SharedPreferences sp){
		Class<?> clz = o.getClass();
		try {
			Field[] fields = clz.getDeclaredFields();
			for (Field field : fields) {
				getFieldFormSp(o , sp, field);
			}
			
			Field[] parentFiled = clz.getSuperclass().getDeclaredFields();
			for (Field field : parentFiled) {
				 setFieldToSp(o,sp, field);
			}
		} catch (Exception e) {
			Log.e("实体转换","转化sp为实体异常", e);
		}
	}

	/**
	 * @param o
	 * @param sp
	 * @param field
	 */
	private static void getFieldFormSp(Object o, SharedPreferences sp,
			Field field) throws IllegalAccessException{
		field.setAccessible(true);
		if(!sp.contains(field.getName())) return ;
		String name = field.getName();
		if(Integer.class.isAssignableFrom(field.getType())){
			int value =  sp.getInt(name,-999);
			if(-999==value) return ;
			Log.i(TAG, " get field  >>>>  [ name : "+name + " ] , [ value :  "+ value + "]");
			field.set(o, value);
		}
		if(String.class.isAssignableFrom(field.getType())){
			String value = sp.getString(name,"");
			if("".equals(value)) return ;
			Log.i(TAG, " get field  >>>>  [ name : "+name + " ] , [ value :  "+ value + "]");
			field.set(o, sp.getString(name, null));
		}
		if(Long.class.isAssignableFrom(field.getType())){
			long value =  sp.getLong(name,-999);
			if(-999==value) return ;
			Log.i(TAG, " get field  >>>>  [ name : "+name + " ] , [ value :  "+ value + "]");
			field.set(o, value);
		}
		if(Float.class.isAssignableFrom(field.getType())){
			float value =  sp.getFloat(name,-999);
			if(-999==value) return ;
			Log.i(TAG, " get field  >>>>  [ name : "+name + " ] , [ value :  "+ value + "]");
			field.set(o, value);
		}
	}

	/**
	 * @param message
	 * @param map
	 * @param field
	 * @return
	 * @throws IllegalAccessException
	 */
	private static void setFieldToSp(Object o , SharedPreferences sp, Field field) throws IllegalAccessException {
		if(MessageUtil.FINAL==field.getModifiers()||MessageUtil.PRIVATE_FINAL==field.getModifiers())
			return ;
		field.setAccessible(true);
		Object value = field.get(o);
		if(value==null) return ;
		String name = field.getName();
		Log.i(TAG, " commit field >>>>  [ name : "+name+"] , [ value : "+value+"] ");
		if(Integer.class.isAssignableFrom(field.getType())){
			sp.edit().putInt(name, (Integer) value).commit();
		}
		if(String.class.isAssignableFrom(field.getType())){
			sp.edit().putString(name, (String) value).commit();
		}
		if(Long.class.isAssignableFrom(field.getType())){
			sp.edit().putLong(name, (Long) value).commit();
		}
		if(Float.class.isAssignableFrom(field.getType())){
			sp.edit().putFloat(name, (Float) value).commit();
		}
	}
}
