/**
 * 
 */
package com.bloomlife.videoapp.common;

/**
 * 
 * 	所有需要存在在cachebean中的key，都写到这里面来，避免重复。
 * 
 * @author <a href="mailto:xiai.fei@gmail.com">xiai_fei</a>  
 *
 * @date 2014-11-14  上午11:06:29
 */
public class CacheKeyConstants extends com.bloomlife.android.common.CacheKeyConstants{

	/***
	 * 文件上传凭证
	 */
	public static final String UPLOAD_TOKEN = "upload_token";
	
	/**
	 * 配置参数的持久化key
	 */
	public static final String CONSTANT_SYSCODE_KEY = "constant_syscode_key";
	
	

	/**
	 * 最新一次地图可以区域的左下角
	 */
	public static final String LOCATION_LAST_TOP_RIGHT = "location_last_top_right";
	/**
	 * 最新一次地图可视区域的右上角
	 */
	public static final String LOCATION_LAST_BOTTOM_LEFT = "location_last_bottom_left";
	
	/**
	 * 最新一次地图可视区域
	 */
	public static final String LOCATION_LAST_VISIABL_AREA = "location_last_visiabl_area";
	
	public static final String LOCATION_LAST_ZOOM = "location_last_zoom";
	
	public static final int VALUE_FAIL = -1;
	public static final int VALUE_SUCC = 1;
	
	public static final int IS_FIRST = -1;
	public static final int NOT_FIRST = 1;

	public static final int IS_SHOW = -1;
	public static final int NOT_SHOW = 1;
	
	
	public static final String KEY_USER_ID = "user_id";
	public static final String KEY_MAX_LEVEL = "max_level";
	public static final String KEY_MIN_LEVEL = "min_level";
	public static final String KEY_DEFAULT_LEVEL = "default_level";
	public static final String KEY_TOPICS = "topics";
	public static final String KEY_VIDEO_UPLOAD_FAIL = "video_upload_fail";
	public static final String KEY_STORY_UPLOAD_FAIL = "story_upload_fail";
	public static final String KEY_FIRST_LOST = "first_lost";
	public static final String KEY_FIRST_COMMENT = "first_comment";
	public static final String KEY_FIRST_VIDEO = "first_video";
	public static final String KEY_FIRST_MESSAGE = "first_message";
	public static final String KEY_FIRST_CAMERA = "first_camera";
	public static final String KEY_FIRST_RANDOM_VIDEO = "first_random_video";
	public static final String KEY_FIRST_MAN_TYPE = "first_man_type";
	public static final String KEY_FIRST_DYNAMIC_WINDOW = "first_dynamic_window";
	public static final String KEY_FIRST_DYNAMIC_CLICK = "first_dynamic_click";

	// 第一次进入精选集页面
	public static final String KEY_FIRST_STORY = "first_story";
	// 第一次进入匿名视频页面
	public static final String KEY_FIRST_GLIMPSE = "first_glimpse";

	public static final String KEY_FIRST_LOGIN = "first_login";

	//	public static final String KEY_FIRST_IN = "first_in";

	public static final String KEY_MY_ACCOUNT = "myAccount";
	public static final String KEY_CACHE_HOST_STORYS = "keyHostStorys";
	public static final String KEY_CACHE_DYMAINIC_MENUS = "keyDymainicMenus";
	public static final String KEY_CACHE_EMOTIONS = "keyEmotions";

	/** 消息模块，未读通知数。不包括未读私信数   ***/
	public static final String KEY_MSG_NUM = "msg_num";
	
	
	/*****************************  环信xiangguan************************************/
	public static final String KEY_HUANXIN_PWD= "key_huanxin_pwd";
	
	/**
	 * 是否将本机的拍摄分辨率上传到后台了。
	 */
	public static final String KEY_VIDEO_SIZE_INFROM= "key_video_size_infrom";
}
