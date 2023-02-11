/**
 * 
 */
package com.bloomlife.android.common.util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import com.bloomlife.android.bean.BaseMessage;
import com.bloomlife.android.common.MessageException;
import com.signutil.StringHashMap;

/**
 * @author <a href="mailto:xiai.fei@gmail.com">xiai_fei</a>  
 *
 * @date 2013-11-13  下午3:18:01
 */
public class MessageUtil {
	
	public static final int  FINAL = Modifier.PUBLIC + Modifier.STATIC + Modifier.FINAL;
	public static final int  PRIVATE_FINAL = Modifier.PRIVATE + Modifier.STATIC + Modifier.FINAL;

	public static Map<String,String> converToMap(BaseMessage message) throws MessageException{
		Class<?> clz = message.getClass();
		Map<String,String> map = new HashMap<String, String>();
		try {
			Field[] fields = clz.getDeclaredFields();
			for (Field field : fields) {
				setFieldToMap(message, map, field);
			}
			
			Field[] parentFiled = clz.getSuperclass().getDeclaredFields();
			for (Field field : parentFiled) {
				 setFieldToMap(message, map, field);
			}
		} catch (Exception e) {
			Log.e("MessageUtil","转化message为map异常", e);
			throw new MessageException("转换出错："+e.getMessage());
		}
		return map;
	}
	
	
	/***
	 * 获取签名的map
	 * @param message
	 * @return
	 * @throws MessageException
	 */
	public static StringHashMap converToMapWithoutParent(BaseMessage message) throws MessageException{
		Class<?> clz = message.getClass();
		Map<String,String> map = new HashMap<String, String>();
		try {
			Field[] fields = clz.getDeclaredFields();
			for (Field field : fields) {
				setFieldToMap(message, map, field);
			}
		} catch (Exception e) {
			Log.e("MessageUtil","转化message为map异常", e);
			throw new MessageException("转换出错："+e.getMessage());
		}
		return new StringHashMap(map);
	}

	/**
	 * @param message
	 * @param map
	 * @param field
	 * @return
	 * @throws IllegalAccessException
	 */
	private static void setFieldToMap(BaseMessage message,
			Map<String, String> map, Field field) throws IllegalAccessException {
		if(FINAL==field.getModifiers()||PRIVATE_FINAL==field.getModifiers())
			return ;
		JSONField jsonField = field.getAnnotation(JSONField.class);
		field.setAccessible(true);
		String name = field.getName();
		if(jsonField!=null){
			if(!jsonField.serialize())return ;
			if(!StringUtils.isEmpty(jsonField.name()))name = jsonField.name();	//使用注解的名称
		}
		
		if(List.class.isAssignableFrom(field.getType())){
			map.put(name,  JSON.toJSONString(field.get(message)));	//处理列表类型的。列表类型需要以json格式发送
		}else{
			String value = ObjectToString(field.get(message));
			if(StringUtils.isEmpty(value)) return ;
			map.put(name,  ObjectToString(field.get(message)));
		}
	}
	
	private static String ObjectToString(Object obj){
		return obj==null?null:obj.toString();
	}
	
	
}
