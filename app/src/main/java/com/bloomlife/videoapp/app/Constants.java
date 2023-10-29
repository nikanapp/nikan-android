package com.bloomlife.videoapp.app;

public class Constants {
	public static final String KEY_COMMENT_LIST = "comment_list";
	
	public static final String ACTION_UPLOAD_PROGRESS = "action_upload_progress";
	public static final String ACTION_USER_LOGIN = "action_user_login";
	public static final String INTENT_USER_FIRST_LOGIN = "intent_user_first_login";

	/**新系统消息到达广播**/
	public static final String ACTION_NEW_SYS_INFORM = "action_new_sys_inform";
	public static final String INTENT_UPLOAD_PROGRESS = "action_upload_progress";
	public static final String ACTION_LOCATION = "action_location";

	public static final String INTENT_LOCATION = "intent_location";
	
	public static final String ACTION_DELETE_VIDEO = "action_delete_video";
	public static final String ACTION_FINISH= "action_finish";
	public static final String ACTION_PUSH_VIDEO = "action_push_video";
	public static final String ACTION_COMMENT= "action_comment";
	
	//删除的时再主页上用户发送的视频
	public static final String INTENT_DELETE_SEND = "intent_delete_send";
	//删除的视频id
	public static final String INTENT_DELETE_VIDEOIDS = "intent_delete_videoids";

	public static final String NO_MORE_PAGE = "-1";
	public static final String NEW_PAGE = "";
	
	public static final int COMMENT_OFF = 0;
	public static final int COMMENT_ON = 1;
	
	public static final int SERVICE_ERROR_CODE = 60001;
	
	/**文件缓存夹的名称**/
	public static final String CACHE_FOLDER_NAME = "video";
	
	public static final double PROGRESS_FAILURE = -1 ;
	public static final double PROGRESS_NO_LOCATION = -100 ;
	public static final double PROGRESS_SUCCESSS = 1.0 ;

	public static final String FEMALE = "0";
	public static final String MALE = "1";

	public static final int UNADD = 0;
	public static final int ADDED = 1;
	public static final int MUTUAL = 2;
	/**
	 * 微信广播
	 */
	public static final String WECHAT_ACTION = "wechatAction";
	/**
	 * 微信登录返回数据
	 */
	public static final String WECHAT_LOGIN_DATA = "wechatLoginData";

}
