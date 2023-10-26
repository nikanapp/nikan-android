package com.bloomlife.videoapp.app;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import com.bloomlife.android.bean.CacheBean;
import com.bloomlife.android.common.util.CacheUtils;
import com.bloomlife.android.framework.CrashHandler;
import com.bloomlife.android.network.HttpProtocolEntry;
import com.bloomlife.videoapp.BuildConfig;
import com.bloomlife.videoapp.common.CacheKeyConstants;
import com.bloomlife.videoapp.common.util.ApplicationUtils;
import com.bloomlife.videoapp.manager.BackgroundManager;
import com.bloomlife.videoapp.manager.LocationManager;
import com.bloomlife.videoapp.manager.VideoFileManager;
import com.bloomlife.videoapp.model.SysCode;
import com.cyou.cyanalyticv3.CYAgent;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.analytics.MobclickAgent;

import java.io.File;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import androidx.multidex.MultiDex;
//import com.bloomlife.videoapp.manager.LocationManager;

/**
 *  全局应用程序类：用于保存和调用全局应用配置及访问网络数据
 * @author <a href="mailto:xiai.fei@gmail.com">xiai_fei</a>  
 *
 * @date 2014-8-27  下午3:53:45
 */
@SuppressLint("NewApi")
public class AppContext extends com.bloomlife.android.framework.AppContext {
	
	public static final String SAVE = "save";
	
	private static SysCode sysCode;
	
	public static  List<String> topics;

	//Mac address & IMEI
	public static String versionCode;

	/** 是否需要正常启动，也就是从闪屏页面开始启动的 , 为了区别点击推送时再顶部通知栏进入app的情况   ***/
	public static  boolean NormalStart =  false ;
	
	private static final String ERROR_FILEPATH = "/nikan/crash";
	
	private static String VIDOE_CACHE_FILE_PATH ;
	
	public int unread_sys_infrom = 0 ; //消息页面未读通知数

	private static AppContext sAppContext;

	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	@Override
	public void onCreate() {
		super.onCreate();
		sAppContext = this;

		String processAppName = ApplicationUtils.getAppName(this,android.os.Process.myPid());
        // 如果使用到百度地图或者类似启动remote service的第三方库，这个if判断不能少
        if (processAppName == null || processAppName.equals("")||!processAppName.equals(getPackageName())) {
            // workaround for baidu location sdk
            // 百度定位sdk，定位服务运行在一个单独的进程，每次定位服务启动的时候，都会调用application::onCreatel
            // 创建新的进程。
            // 则此application::onCreate 是被service 调用的，直接返回
            return;
        }

        // ！！！！！！！！TODO 不允许在这里将请求地址写死，改变请求地址只能在appsettingx.xml中修改！！！！！！！！
		HttpProtocolEntry.URL = BuildConfig.HTTP_APP_API_DOMAIN;
		PushService.initPush(getApplicationContext());

		MyHXSDKHelper.getInstance().init(this);
		if(CacheBean.getInstance().hasLoginUserId()){
			LocationManager.getInstance(this).startLocation();
		}

		/*
		 * 友盟数据统计服务。启用友盟的统计更新机制 。  这个需要放到MainActivity和SpalshActivity中做。不能总是在Application中做太多的业务逻辑操作。
		 */
		MobclickAgent.updateOnlineConfig(this);
		MobclickAgent.openActivityDurationTrack(false);// 因为有activity和fragment，所以禁止默认的页面统计
		MobclickAgent.setDebugMode(BuildConfig.DEBUG);

		// 畅游cysdk服务
		CYAgent.launchApp(this);
		CYAgent.getDeviceUUID(this);
		CYAgent.setDebug(BuildConfig.DEBUG);

		CrashHandler crashHandler = CrashHandler.getInstance();
		crashHandler.init(getApplicationContext(), ERROR_FILEPATH);

		setSysCode(CacheBean.getInstance().getObject(getApplicationContext(), CacheKeyConstants.CONSTANT_SYSCODE_KEY, SysCode.class));

		File file= CacheUtils.getCacheFileDirectory(this, Constants.CACHE_FOLDER_NAME);
		if (file != null)
			VIDOE_CACHE_FILE_PATH = file.getAbsolutePath();
//		AnalyseUtils.uploadAnalyseData(getApplicationContext());
//		Utils.copySQLiteToSdCard(getCacheDir().getParentFile());
	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();
		Log.e("AppContext", "on  low memory , release cache ");
		ImageLoader imageLoader = ImageLoader.getInstance();
		imageLoader.clearMemoryCache();
		VideoFileManager.getInstance().evictAll();
		BackgroundManager.getInstance().recycle();
	}

	@Override
	protected void attachBaseContext(Context base) {
		super.attachBaseContext(base);
		MultiDex.install(this);
	}

	public static final String APP_GAOSHOU_KEY = "APP_GAOSHOU_KEY";
	
	private static final ConcurrentHashMap<String, Object> cMap = new ConcurrentHashMap<String, Object>();
	
	public static final String KEY_CREATE_WORK_BACK = "CREATE_BACK";
	
	
	public void addCache(String key ,Object value ){
		if(value==null)return ;
		cMap.put(key, value);
	}
	
	/**取出来一次之后就销毁**/
	public Object getCache(String key){
		return cMap.remove(key);
	}

	/**
	 * 确保syscode不为空
	 * @return
	 */
	public static  synchronized SysCode getSysCode() {
		return sysCode==null?sysCode=new SysCode():sysCode;
	}

	public static void setSysCode(SysCode sysCode) {
		AppContext.sysCode = sysCode;
	}

	public static String getVIDOE_CACHE_FILE_PATH() {
		return VIDOE_CACHE_FILE_PATH;
	}

	public static AppContext get(){
		return sAppContext;
	}
    
}

