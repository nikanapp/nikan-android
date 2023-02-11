package com.bloomlife.android.framework;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.StrictMode;
import android.util.Log;

import com.android.volley.toolbox.Volley;
import com.bloomlife.android.bean.CacheBean;
import com.bloomlife.android.bean.DeviceInfo;
import com.bloomlife.android.log.LoggerManager;
import com.bloomlife.android.network.NetUtils;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 全局应用程序类：用于保存和调用全局应用配置及访问网络数据
 * @author liux (http://my.oschina.net/liux)
 * @version 1.0
 * @created 2012-3-21
 */
@SuppressLint("NewApi")
public class AppContext extends Application {

	//Mac address & IMEI
	public static DeviceInfo deviceInfo;
	public static String versionCode;

	private static int networkType = -9999;

	protected final List< WeakReference<Activity> > activityList = new ArrayList<WeakReference<Activity>>();


	/** 是否需要正常启动，也就是从闪屏页面开始启动的 , 为了区别点击推送时再顶部通知栏进入app的情况   ***/
	public static  boolean NormalStart =  false ;
	public static final ExecutorService  EXECUTOR_SERVICE = Executors.newFixedThreadPool(8);

	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	@SuppressWarnings("unused")
	@Override
	public void onCreate() {
		super.onCreate();

		if (Config.DEVELOPER_MODE && (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD)) {
			StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectAll().penaltyDialog().build());
			StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectAll().penaltyDeath().build());
		}

		initApp(getApplicationContext());
		CacheBean.getInstance().init(getApplicationContext());
		
		 try {
			 versionCode = String.valueOf(this.getPackageManager().getPackageInfo(getPackageName(), 0).versionCode);
//
//				// 添加渠道标签
//				ApplicationInfo info = getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
//				Object umengChannel = info.metaData.get("UMENG_CHANNEL");
//				channleNum =  umengChannel == null ? "9999" : umengChannel.toString();
//
			} catch (NameNotFoundException e) {
				e.printStackTrace();
			}
		initImageLoader(getApplicationContext());
		// 初始化日志
		File path = getExternalCacheDir();
		if (path != null){
			LoggerManager.DefaultSettingBuilder builder = new LoggerManager.DefaultSettingBuilder();
			builder.setDebug(false);
			LoggerManager.getInstance().init(path.getParent(), builder.create());
		}
		// 初始化网络请求
		Volley.init(this);

	}
	// 配置
	public static class Config {
		public static final boolean DEVELOPER_MODE = false;
	}
	
	public static void initImageLoader(Context context) {
		// This configuration tuning is custom. You can tune every option, you may tune some of them,
		// or you can create default configuration by
		//  ImageLoaderConfiguration.createDefault(this);
		// method.
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
				.threadPriority(Thread.NORM_PRIORITY - 2)
				.denyCacheImageMultipleSizesInMemory()
				.diskCacheFileNameGenerator(new Md5FileNameGenerator())
				.tasksProcessingOrder(QueueProcessingType.LIFO)
				.writeDebugLogs() // Remove for release app
				.build();
		// Initialize ImageLoader with configuration.
		ImageLoader.getInstance().init(config);

	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();
		Log.e("AppContext", "on  low memory , release cache ");
		ImageLoader imageLoader = ImageLoader.getInstance();
		imageLoader.clearMemoryCache();
	}



	protected  void initApp(Context ctx){
		SharedPreferences sp = ctx.getSharedPreferences(APP_GAOSHOU_KEY, Context.MODE_PRIVATE);
		
		deviceInfo = DeviceInfo.obtainInfo(ctx, sp);
		//检测SDCard
		if(android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)){
			deviceInfo.setSdcard(true);
		}else{
			deviceInfo.setSdcard(false);
		}
		
		networkType = NetUtils.getNetState(ctx);
		IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        ctx.registerReceiver(new NetworkReceiver(), filter);
	}
	
	
	
	/**
	 * 更新用户头像
	 *  @param ctx
	 *  @param userIconUrl 头像地址
	 */
	public static void updateUserIcon(Context ctx,String userIconUrl){ }
	
	public static final String APP_GAOSHOU_KEY = "APP_GAOSHOU_KEY";
	
	private static final ConcurrentHashMap<String, Object> cMap = new ConcurrentHashMap<String, Object>();
	
	public static final String KEY_CREATE_WORK_BACK = "CREATE_BACK";
	
	/***
	 * 读了一次就会清除
	 * @param key
	 * @return
	 */
	public static  Object getParameter(String key){
		return cMap .remove(key);
	}
	
	public static  void setParameter(String key,Object value){
		cMap.put(key, value);
	}
	
	public void addCache(String key ,Object value ){
		if(value==null)return ;
		cMap.put(key, value);
	}
	
	/**取出来一次之后就不销毁**/
	public Object getCache(String key){
		return cMap.remove(key);
	}
	
	
	/****
	 *  ******************************************     setter and getter         *********************************************************
	 */

	public static int getNetWorkType(){
		return networkType;
	}

	public void addActivity(Activity activity){
		activityList.add(new WeakReference<>(activity));
	}
	
	public void releaseActivty(){
		for (WeakReference<Activity> weakReference: activityList) {
			if(weakReference.get()!=null){
				weakReference.get().finish();
			}
		}
	}

	/** *************************************************       监听器              ********************************************************************/
	public class NetworkReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            networkType = NetUtils.getNetState(context);
        }
    }

}

