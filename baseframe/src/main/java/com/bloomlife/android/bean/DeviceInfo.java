/**
 * 
 */
package com.bloomlife.android.bean;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import com.alibaba.fastjson.JSON;
import com.bloomlife.android.common.util.StringUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Method;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author <a href="mailto:xiai.fei@gmail.com">xiai_fei</a>
 * 
 * @date 2014-7-31 下午12:05:22
 */
public class DeviceInfo {

	public static final String SP_KEY = "context_DeviceInfo";

	// Mac address & IMEI
	public String macAddress;
	public String iMEIID;
	public String versionCode;
	public String phoneType;
	public String systemVersion;
	public String sdkVersion;

	private boolean isSdcard = true;

	private int screenWidth;
	private int screenHeight;

	private String screenResolution;

	private int dpi;

	private String deviceId;  //唯一的设备识别号码
	
	
	public static DeviceInfo obtainInfo(Context ctx, SharedPreferences sp){
		DeviceInfo deviceInfo = DeviceInfo.getInstanceFromSp(sp);
		if(deviceInfo==null||StringUtils.isEmpty(deviceInfo.deviceId))deviceInfo = DeviceInfo.obtainInfoAndSaveToSp(ctx, sp);
		return deviceInfo ;
	}

	/**
	 * Get Device Info:imei * macaddress
	 */
	private static DeviceInfo obtainInfoAndSaveToSp(Context ctx, SharedPreferences sp) {
		WifiManager manager = (WifiManager) ctx.getSystemService(Context.WIFI_SERVICE);
		WifiInfo info = manager.getConnectionInfo();
		DeviceInfo deviceInfo = new DeviceInfo();
		deviceInfo.macAddress = info.getMacAddress();

		deviceInfo.iMEIID = Settings.System.getString(ctx.getContentResolver(), Settings.Secure.ANDROID_ID);
		deviceInfo.phoneType = android.os.Build.MODEL;
		deviceInfo.systemVersion = android.os.Build.VERSION.RELEASE;

		setScreenResolution(deviceInfo, ctx);
		deviceInfo.setScreenResolution(deviceInfo.screenWidth + "_" + deviceInfo.screenHeight);
		
		deviceInfo.deviceId = obtainDeviceId(ctx , deviceInfo.iMEIID);

		sp.edit().putString(SP_KEY, JSON.toJSONString(deviceInfo)).commit();
		return deviceInfo;
	}
	
	private static void setScreenResolution(DeviceInfo deviceInfo,Context ctx){
		Display display = ((WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
		/*  
		 * 当有虚拟按键的时候，会占用了屏幕的一些空间，所以需要获取到实际的屏幕分辨率 
		 */
		if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.JELLY_BEAN_MR1){
	        DisplayMetrics metrix = new DisplayMetrics(); 
	        @SuppressWarnings("rawtypes")
	        Class c;
	        try {
	            c = Class.forName("android.view.Display");
	            @SuppressWarnings("unchecked")
	            Method method = c.getMethod("getRealMetrics",DisplayMetrics.class);
	            method.invoke(display, metrix);
	            deviceInfo.screenWidth = metrix.widthPixels;
	    		deviceInfo.screenHeight = metrix.heightPixels;
	    		deviceInfo.dpi = metrix.densityDpi / 160;
	    		return ;
	        }catch(Exception e){
	            e.printStackTrace();
	        }  
		}
		DisplayMetrics metrix = new DisplayMetrics();
		display.getMetrics(metrix);
		deviceInfo.screenWidth = metrix.widthPixels;
		deviceInfo.screenHeight = metrix.heightPixels;
		deviceInfo.dpi = metrix.densityDpi / 160;
	}
	

	private static DeviceInfo getInstanceFromSp(SharedPreferences sp) {
		String jsonStr = sp.getString(SP_KEY, null);
		if (StringUtils.isEmpty(jsonStr))
			return null;
		return JSON.parseObject(jsonStr, DeviceInfo.class);
	}

	public boolean isSdcard() {
		return isSdcard;
	}

	public void setSdcard(boolean isSdcard) {
		this.isSdcard = isSdcard;
	}

	public int getScreenWidth() {
		return screenWidth;
	}

	public void setScreenWidth(int screenWidth) {
		this.screenWidth = screenWidth;
	}

	public int getScreenHeight() {
		return screenHeight;
	}

	public void setScreenHeight(int screenHeight) {
		this.screenHeight = screenHeight;
	}

	public int getDpi() {
		return dpi;
	}

	public void setDpi(int dpi) {
		this.dpi = dpi;
	}

	public String getScreenResolution() {
		if (StringUtils.isEmpty(screenResolution))
			setScreenResolution(screenWidth + "_" + screenHeight);
		return screenResolution;
	}

	public void setScreenResolution(String screenResolution) {
		this.screenResolution = screenResolution;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}
	
	
	/***
	 * 获取deviceid。当生成deviceid成功之后，保存到本地文件。以后从文件中获取，避免刷新之类的导致deviceid的变化
	 * @param context
	 * @return
	 */
	private static String obtainDeviceId(Context context , String imei) {
		String deviceId = generateDeviceId(imei);
		BufferedReader  fin = null ;
		FileOutputStream fos = null ;
		try{
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			String pkName = context.getPackageName();
			String path = Environment.getExternalStorageDirectory().getPath()+"/" +pkName.substring(pkName.lastIndexOf(".")+1, pkName.length());
			File dir = new File(path);
			if (!dir.exists()){
				if(!dir.mkdirs()) return deviceId;
			}
			File file = new File(dir,"did" +
					".txt");
			if(!file.exists()){
				if(!file.createNewFile()) return deviceId;
				fos = new FileOutputStream(file);
				fos.write(deviceId.getBytes());
				fos.close();
			}else{
				fin = new BufferedReader (new FileReader(file));
				String fileDeviceId = fin.readLine();
				fin.close();
				if(StringUtils.isNotEmpty(fileDeviceId)){
					return fileDeviceId;
				}
			}
		}}catch(Exception e){
			Log.e("", "获取deviceid出错");
		}finally{
			if(fin!=null) try { fin.close(); } catch (IOException e) { }
			if(fos!=null)  try { fos.close(); } catch (IOException e) { }
		}
		return deviceId;
	}

	
	private static String generateDeviceId(String imei){
		String serial = android.os.Build.SERIAL;
		String deviceId = null;
		if(StringUtils.isEmpty(serial)){
			deviceId = imei ;
		}else{
			deviceId = imei+serial;
		}
		String md5Result = md5(deviceId);
		if(StringUtils.isEmpty(md5Result)) return deviceId;
		return md5Result;
	}
	
	private static final char HEX_DIGITS[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',  
        'A', 'B', 'C', 'D', 'E', 'F' };  
	private static String toHexString(byte[] b) {  
	    //String to  byte  
	    StringBuilder sb = new StringBuilder(b.length * 2);    
	    for (int i = 0; i < b.length; i++) {    
	        sb.append(HEX_DIGITS[(b[i] & 0xf0) >>> 4]);    
	        sb.append(HEX_DIGITS[b[i] & 0x0f]);    
	    }    
	    return sb.toString();    
	}  
	private static  String md5(String s) {  
	    try {  
	        MessageDigest digest = java.security.MessageDigest.getInstance("MD5");  
	        digest.update(s.getBytes());  
	        byte messageDigest[] = digest.digest();  
	                                  
	        return toHexString(messageDigest);  
	    } catch (NoSuchAlgorithmException e) {  
	        e.printStackTrace();  
	    }  
	                          
	    return "";  
	}  
}
