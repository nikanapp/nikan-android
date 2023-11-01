/**
 * 
 */
package com.bloomlife.videoapp.common;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;

import com.bloomlife.android.bean.CacheBean;

/**
 *   友盟统计和畅游统计等相关的统计key
 *
 * @author <a href="mailto:xiai.fei@gmail.com">xiai_fei</a>  
 *
 * @date 2015-6-2 下午5:03:22
 *
 * @organization bloomlife  
 * @version 1.0
 */
public class AnalyticUtil {

	
	public final static String Event_Video_Upload_Start = "event_video_upload_start";
	public final static String Event_Video_Upload_File_Success = "event_video_upload_file_success";
	public final static String Event_Video_Publish_Success = "event_video_publish_success";
	
	public final static String Event_Play_Video = "event_play_video";
	
	public final static String Event_Choise_Video = "event_choise_video";
	
	public final static String Event_Record_Video = "event_record_video";
	public final static String Event_Comment = "event_comment";
	
	public final static String Event_Impression_Comment = "event_impression_comment";
	
	/**动态表情发送事件**/
	public final static String Event_Dynamic_Image = "event_dynamic_image";
	
	
	
	/***
	 * 相关的数据统计记录
	 * @param key
	 * @param params
	 */
	public static void sendAnalytisEvent(Context context,String key,Map<String,String> params){

	}
}
