/**
 * 
 */
package com.bloomlife.android.common.util;

import java.io.File;
import java.io.IOException;

import android.content.Context;
import android.os.Environment;

import com.nostra13.universalimageloader.utils.L;

/**
 * 	缓存工具类
 * 
 * @author <a href="mailto:xiai.fei@gmail.com">xiai_fei</a>  
 *
 * @date 2014-12-10  下午4:43:59
 */
public class CacheUtils {

	/***
	 * 获取文件缓存路径
	 * @param context
	 * @param folderName
	 * @return
	 */
	public  static File getCacheFileDirectory(Context context ,String folderName){
		if(StringUtils.isEmpty(folderName)){
			folderName = "cache";
		}
		File dataDir = new File(new File(Environment.getExternalStorageDirectory(), "Android"), "data");
		File appCacheDir = new File(new File(dataDir, context.getPackageName()),folderName);
		if (!appCacheDir.exists()) {
			if (!appCacheDir.mkdirs()) {
				L.w("Unable to create external cache directory");
				return null;
			}
			try {
				new File(appCacheDir, ".nomedia").createNewFile();
			} catch (IOException e) {
				L.i("Can't create \".nomedia\" file in application external cache directory");
			}
		}
		return appCacheDir;
	}
	
	public  static File getCacheRootDirectory(Context context){
		File dataDir = new File(new File(Environment.getExternalStorageDirectory(), "Android"), "data");
		File appCacheDir = new File(dataDir, context.getPackageName());
		if (!appCacheDir.exists()) {
			if (!appCacheDir.mkdirs()) {
				L.w("Unable to create external cache directory");
				return null;
			}
		}
		return appCacheDir;
	}
	
	/***
	 * 获取文件缓存路径
	 * @param context
	 * @param folderName
	 * @return
	 */
	public  static File getExternalCacheFileDirectory(Context context ,String folderName){
		if(StringUtils.isEmpty(folderName)){
			folderName = "cache";
		}
		String packageName =  context.getPackageName();
		String parentFolder = packageName.substring(packageName.lastIndexOf(".")+1, packageName.length());
		File appCacheDir = new File(new File(Environment.getExternalStorageDirectory(),parentFolder), folderName);
		if (!appCacheDir.exists()) {
			if (!appCacheDir.mkdirs()) {
				L.w("Unable to create external cache directory");
				return null;
			}
			try {
				new File(appCacheDir, ".nomedia").createNewFile();
			} catch (IOException e) {
				L.i("Can't create \".nomedia\" file in application external cache directory");
			}
		}
		return appCacheDir;
	}
}
