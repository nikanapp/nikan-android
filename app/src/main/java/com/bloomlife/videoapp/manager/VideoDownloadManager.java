/**
 * 
 */
package com.bloomlife.videoapp.manager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.util.Log;

import com.bloomlife.videoapp.activity.fragment.VideoPlayerFragment;

/**
 * @author <a href="mailto:xiai.fei@gmail.com">xiai_fei</a>  
 *
 * @date 2014-12-11  上午9:34:37
 */
//public class VideoDownloadManager {
//
//	private static final String TAG = "VideoDownloadManager";
//
//	private static String curDownloadVideoId ;
//
//	private static Map<String,VideoDownloader> downLoadMap = Collections.synchronizedMap(new HashMap<String,VideoDownloader>());
//
//	private static List<String> downloadIdList = new ArrayList<String>();
//
//	public static void reset(){
//		curDownloadVideoId = null ;
//		clear();
//	}
//
//	public synchronized static void refreshDownload(String finishVideoId){
//		Log.d(TAG, "  刷新下载队列  ");
//		if(downLoadMap.isEmpty()||downloadIdList.isEmpty()){
//			Log.d(TAG, " 队列已经为空，退出  ");
//			curDownloadVideoId = null;
//			return ;
//		}
//		downLoadMap.remove(finishVideoId);
//		curDownloadVideoId = downloadIdList.remove(0);
//		if(downLoadMap.get(curDownloadVideoId)!=null)downLoadMap.get(curDownloadVideoId).execute();
//	}
//
//	public static void executeTask(String videoId, VideoDownloader context){
//		if(getCurDownloadVideoId()==null){
//			Log.d(TAG, " 当前没有视频在下载，启动下载 ");
//			VideoDownloader task = context;
//			setCurDownloadVideoId(videoId);
//			task.execute(); //执行下载
//		}else{
//			if(getCurDownloadVideoId().equals(videoId)){
//				Log.d(TAG, " 视频正在下载中，不需要添加到下载队列 ");
//				return ;
//			}
//			if(containTask(videoId)){
//				VideoPlayerFragment.DownLoadTask task = (VideoPlayerFragment.DownLoadTask) removeTask(videoId);
//				putTask(videoId, task);
//				Log.d(TAG, " 视频已经在队列中，将视频移到头部");
//			}else{
//				putTask(videoId, context);
//				Log.d(TAG, " 将视频放入下载队列头部成功  ");
//			}
//		}
//	}
//
//	private static void putTask(String videoId ,VideoDownloader task){
//		downloadIdList.add(0, videoId);
//		downLoadMap.put(videoId, task);
//	}
//
//	private static boolean containTask(String videoId){
//		return downLoadMap.containsKey(videoId);
//	}
//
//	private static VideoDownloader removeTask(String videoId){
//		downloadIdList.remove(videoId);
//		return downLoadMap.remove(videoId);
//	}
//
//
//	private static String getCurDownloadVideoId() {
//		return curDownloadVideoId;
//	}
//
//	private static void setCurDownloadVideoId(String curDownloadVideoId) {
//		VideoDownloadManager.curDownloadVideoId = curDownloadVideoId;
//	}
//
//	public static void clear(){
//		downLoadMap.clear();
//		downloadIdList.clear();
//	}
//}
