///**
// * 
// */
//package com.bloomlife.videoapp.app;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import junit.framework.Assert;
//import android.content.Intent;
//import android.util.Log;
//
//import com.bloomlife.android.common.util.StringUtils;
//import com.bloomlife.videoapp.model.Video;
//
///**
// * 
// * 	支持单个和多个上传的上传service。 这个servie暂时没有用到，是预留的一个操作
// * 
// * @param INTENT_VIDEO_LIST -- 视频id数组， 为ArrayList<Integer>
// * 
// * @param INTENT_UPLOAD_NEW -- 视频实体 。 为Video 。 和上面的参数至少存在一个
// * 
// * @author <a href="mailto:xiai.fei@gmail.com">xiai_fei</a>
// * 
// * @date 2014-11-25 下午3:16:46
// */
//public class UploadBatchService extends UploadBackgroundService{
//	
//	/**
//	 * 选择多个上传时需要传入的参数
//	 */
//	public static final String INTENT_VIDEO_LIST= "intent_video_list";
//	
//	private static final String TAG = "UploadBatchService";
//	
//	private boolean isFailure = false ; //intentservice是单线程执行的，所以会比较安全
//	
//	@Override
//	protected void onHandleIntent(Intent workIntent) {
//		Video newVideo = (Video) workIntent.getParcelableExtra(INTENT_UPLOAD_NEW);
//		
//		Log.d(TAG, " come into upload intent service  ");
//		List<Integer> videoIdList = null ;
//		
//		//输入从拍摄页面发布的视频id
//		if(newVideo!=null){
//			Database.writeVideo(getApplicationContext(), newVideo);
//			videoIdList = new ArrayList<Integer>();
//			videoIdList.add(Database.queryLastInsertId(getApplicationContext(), Video.class));
//		}else{
//			 videoIdList = workIntent.getIntegerArrayListExtra(INTENT_VIDEO_LIST);
//		}
//		
//		String uploadToken = getToken(getApplicationContext());
//		if(StringUtils.isEmpty(uploadToken)){
//			Log.e(TAG, "  上传失败 ， 无法获取到token ");
//			return  ;
//		}
//		
//		//迭代上传
//		for (Integer videoId : videoIdList) {
//			Video video = Database.findVideo(getApplicationContext(), videoId);
//			Assert.assertNotNull("找不到id为："+videoId+ " 的视频记录",video);  //TODO 在正式发版本的时候应该注释他。
//			try {
//				uploadSingleVideo(getApplicationContext(), uploadToken ,video);
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//				isFailure = true ;
//			}
//			if(isFailure) break ;
//		}
//		
//		if(isFailure){
//			sendUploadBroadcast(getApplicationContext(),null , Constants.PROGRESS_FAILURE,null);
//		}
//		Log.d(TAG, " finish upload  >>>>> ");
//	}
//}
