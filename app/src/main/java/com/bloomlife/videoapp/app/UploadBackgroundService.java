/**
 * 
 */
package com.bloomlife.videoapp.app;

import static com.bloomlife.android.common.util.StringUtils.isEmpty;
import static com.bloomlife.videoapp.app.Constants.ACTION_UPLOAD_PROGRESS;
import static com.bloomlife.videoapp.app.Constants.INTENT_UPLOAD_PROGRESS;
import static com.bloomlife.videoapp.app.Constants.PROGRESS_FAILURE;
import static com.bloomlife.videoapp.app.Constants.PROGRESS_SUCCESSS;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.android.volley.toolbox.MessageRequest;
import com.android.volley.toolbox.UploadFileToQiNiuRequest;
import com.android.volley.toolbox.Volley;
import com.bloomlife.android.bean.CacheBean;
import com.bloomlife.android.bean.FailureResult;
import com.bloomlife.android.bean.ProcessResult;
import com.bloomlife.android.log.Logger;
import com.bloomlife.android.network.HttpAccessor;
import com.bloomlife.videoapp.common.AnalyticUtil;
import com.bloomlife.videoapp.model.DbStoryVideo;
import com.bloomlife.videoapp.model.Video;
import com.bloomlife.videoapp.model.VideoProgress;
import com.bloomlife.videoapp.model.message.UploadStoryVideoMessage;
import com.bloomlife.videoapp.model.message.UploadTokenMessage;
import com.bloomlife.videoapp.model.message.UploadVideoMessage;
import com.bloomlife.videoapp.model.result.UploadStoryVideoResult;
import com.bloomlife.videoapp.model.result.UploadTokenResut;
import com.bloomlife.videoapp.model.result.UploadVideoResult;
import com.cyou.cyanalyticv3.CYAgent;
import com.umeng.analytics.MobclickAgent;

import org.apache.http.HttpException;

/**
 * 
 * 	 	上传视频后台进程，如果传入的视频记录id为空，那么会保存这个视频记录后再上传。
 * 
 * 	上传过程中如果订阅了Constants.ACTION_UPLOAD_PROGRESS的广播，那么会收到上传进度反馈
 * 
 * @param INTENT_UPLOAD_VIDEO -- 视频实体 。 为Video 当上传的是匿名视频时要传
 * @param INTENT_UPLOAD_STORY_VIDEO -- 精选集实体 DbStoryVideo 当上传的是精选集视频时要传
 *
 * @author <a href="mailto:xiai.fei@gmail.com">xiai_fei</a>
 * 
 * @date 2014-11-25 下午3:16:46
 */
public class UploadBackgroundService extends IntentService {
	
	private static final List<Integer> uploadVideoQueue = new ArrayList<>();

	private static final List<Integer> uploadStoryVideoQueue = new ArrayList<>();
	
	/**
	 * 在拍摄视频页面上传视频时传入的参数
	 */
	public static final String INTENT_UPLOAD_VIDEO = "uploadVideo";
	public static final String INTENT_UPLOAD_STORY_VIDEO = "uploadStoryVideo";

	public UploadBackgroundService() {
		super("UploadBackgroundService");
	}

	private static final String TAG = "UploadService";
	
	private boolean isFailure = false ; //intentservice是单线程执行的，所以会比较安全
	
	private String fileKey ;

	@Override
	protected void onHandleIntent(Intent workIntent) {
		Video newVideo = workIntent.getParcelableExtra(INTENT_UPLOAD_VIDEO);
		DbStoryVideo storyVideo = workIntent.getParcelableExtra(INTENT_UPLOAD_STORY_VIDEO);

		Log.d(TAG, " come into upload intent service  ");

		if (newVideo != null){
			uploadVideo(newVideo);
		} else if(storyVideo != null){
			uploadStoryVideo(storyVideo);
		}
		Log.d(TAG, " finish upload  >>>>> ");
	}

	private void uploadVideo(Video newVideo){
		//如果视频没有持久化，则持久化视频
		if (newVideo.getId() == null || newVideo.getId() < 1){
			Database.writeVideo(getApplicationContext(), newVideo);
			int vid = Database.queryLastInsertId(getApplicationContext(), Video.class);
			newVideo.setId(vid);
		}

		//经纬度不能为空
		if(isEmpty(newVideo.getLat())||isEmpty(newVideo.getLon())) return ;

		if(uploadVideoQueue.contains(newVideo.getId())){
			Log.e(TAG, " 视频："+newVideo.getId()+" 已经在上传，等待结果中");
			return ;
		}
		sendUploadBroadcast(getApplicationContext(), newVideo.getId(), 0.0, null, VideoProgress.VIDEO);
		uploadVideoQueue.add(newVideo.getId());
		fileKey = newVideo.getFilaPath();
		try {
			uploadVideo(getApplicationContext(), newVideo);
		} catch (Exception e) {
			e.printStackTrace();
			isFailure = true ;
		}

		if(isFailure) {
			sendUploadBroadcast(getApplicationContext(), newVideo.getId(), Constants.PROGRESS_FAILURE, null, VideoProgress.VIDEO);
			newVideo.setStatus(Video.STATUS_UPLOAD_FAIL);
			Database.updateVideo(getApplicationContext(), newVideo);
		}
		uploadVideoQueue.remove(newVideo.getId());
	}

	private void uploadStoryVideo(DbStoryVideo video){
		//如果视频没有持久化，则持久化视频
		if (video.getId() == null || video.getId() < 1){
			Database.writeStoryVideo(getApplicationContext(), video);
			int vid = Database.queryLastInsertId(getApplicationContext(), DbStoryVideo.class);
			video.setId(vid);
		}

		if(uploadStoryVideoQueue.contains(video.getId())){
			Log.e(TAG, " 视频：" + video.getId() + " 已经在上传，等待结果中");
			return;
		}
		sendUploadBroadcast(getApplicationContext(), video.getId(), 0.0, null, VideoProgress.STORY_VIDEO);
		uploadStoryVideoQueue.add(video.getId());
		fileKey = video.getFilePath();
		try {
			uploadStoryVideo(getApplicationContext(), video);
		} catch (Exception e) {
			e.printStackTrace();
			isFailure = true ;
		}

		if(isFailure) {
			sendUploadBroadcast(getApplicationContext(), video.getId(), Constants.PROGRESS_FAILURE, null, VideoProgress.STORY_VIDEO);
			video.setStatus(Video.STATUS_UPLOAD_FAIL);
			Database.updateStoryVideo(getApplicationContext(), video);
		}
		uploadStoryVideoQueue.remove(video.getId());
	}
	
	
	/**
	 * 发送广播。
	 * @param context
	 * @param serverVideoId
	 * @param id
	 * @param progress  为-1代表失败
	 */
	protected void sendUploadBroadcast(Context context ,Integer id, double progress, String serverVideoId, int type){
		Intent localIntent = new Intent(ACTION_UPLOAD_PROGRESS);
		localIntent.putExtra(INTENT_UPLOAD_PROGRESS, new VideoProgress(id, progress, serverVideoId, fileKey, type));
		// Broadcasts the Intent to receivers in this app.
		sendBroadcast(localIntent);
	}

	protected void uploadVideo(final Context context, final Video video) throws InterruptedException{
		sendAnalytisEvent(AnalyticUtil.Event_Video_Upload_Start,null);
		//视频还没有上传过
		if(isEmpty(video.getVideoKey())){
			final UploadTokenResut uploadToken = getUploadToken(context, video.getRotate());
			//文件没有上传成功过的。
			Volley.uploadFileToQiNiuRequest(new UploadFileToQiNiuRequest(uploadToken.getUploadtoken(), uploadToken.getFilekey(), video.getFilaPath(), new UploadFileToQiNiuRequest.Listener() {

				@Override
				public void start() {

				}

				@Override
				public void progress(double progress) {
					if (progress >= 0.99) progress = 0.99;
					else if (progress < 0) progress = 0;
					Log.d(TAG, " video : " + video.getId() + " progress : " + progress);
					sendUploadBroadcast(context, video.getId(), progress, null, VideoProgress.VIDEO);
				}

				@Override
				public void error(String msg) {
					isFailure = true;
					synchronized (video) {
						video.notify();
						Log.d(TAG, " onFailure : " + msg + "  video awake ");
					}
				}

				@Override
				public void success(Map<String, Object> attrs) {
					String persistentId = (String) attrs.get("persistentId");
					Log.e(TAG, " 视频文件到七牛服务器成功，文件key ： " + uploadToken.getFilekey() + " ， 预览图persistentid 为： " + persistentId);
					video.setVideoKey(uploadToken.getFilekey());
					video.setPersistentsId(persistentId); //保存七牛的预览图任务id。，
					synchronized (video) {
						video.notify();
						Log.d(TAG, " onSuccess video awake ");
					}
				}
			}));
			synchronized (video) {
				Log.d(TAG, " video wait ");
				video.wait();   // !!!!!!!!!!
			}
		}
		if (!isFailure) {
			publishVideo(context, video, video.getVideoKey());
		}
	}
	
	
	protected void uploadStoryVideo(final Context context, final DbStoryVideo video) throws InterruptedException{
		sendAnalytisEvent(AnalyticUtil.Event_Video_Upload_Start,null);
		//视频还没有上传过
		if(isEmpty(video.getVideoKey())){
			final UploadTokenResut uploadToken = getUploadToken(context, Integer.valueOf(video.getRotate()));
			//文件没有上传成功过的。
			Volley.uploadFileToQiNiuRequest(new UploadFileToQiNiuRequest(uploadToken.getUploadtoken(), uploadToken.getFilekey(), video.getFilePath(), new UploadFileToQiNiuRequest.Listener() {

				@Override
				public void start() {

				}

				@Override
				public void progress(double progress) {
					if (progress >= 0.99) progress = 0.99;
					else if (progress < 0) progress = 0;
					Log.d(TAG, " video : " + video.getId() + " progress : " + progress);
					sendUploadBroadcast(context, video.getId(), progress, null, VideoProgress.STORY_VIDEO);
				}

				@Override
				public void error(String msg) {
					isFailure = true;
					synchronized (video) {
						video.notify();
						Log.d(TAG, " onFailure : " + msg + "  video awake ");
					}
				}

				@Override
				public void success(Map<String, Object> attrs) {
					String persistentId = (String) attrs.get("persistentId");
					Log.e(TAG, " 视频文件到七牛服务器成功，文件key ： " + uploadToken.getFilekey() + " ， 预览图persistentid 为： " + persistentId);
					video.setVideoKey(uploadToken.getFilekey());
					video.setPersistentsId(persistentId); //保存七牛的预览图任务id。，
					synchronized (video) {
						video.notify();
						Log.d(TAG, " onSuccess video awake ");
					}
				}
			}));
			synchronized (video) {
				Log.d(TAG, " video wait ");
				video.wait();   // !!!!!!!!!!
			}
		}
		if (!isFailure) {
			publishStoryVideo(context, video, video.getVideoKey());
		}
	}
	
	
	/**
	 * 向后台发布这个视频。
	 * @param context
	 * @param video
	 * @param videokey  : 七牛返回的key
	 */
	private void publishVideo(final Context context, final Video video , final String videokey) {
		// 上传视频到七牛服务器成功，执行视频创建业务处理
		sendAnalytisEvent(AnalyticUtil.Event_Video_Upload_File_Success, null);
		Log.d(TAG, " upload video to qiniu success , start to public video to server ");
		UploadVideoMessage videoMessage = UploadVideoMessage.makeByVideo(video);
		videoMessage.setVideokey(videokey);
		Volley.add(new MessageRequest(videoMessage, new MessageRequest.Listener<UploadVideoResult>() {
			@Override
			public void success(UploadVideoResult result) {
				Logger.d(TAG, " 发布视频成功");
				video.setVideoid(result.getVideoid());
				video.setUploadTime(result.getCreatetime());
				video.setStatus(Video.STATUS_UPLOAD_SUCCESS);
				video.setVideouri(result.getVideouri());
				Database.updateVideo(context, video);
				sendUploadBroadcast(context, video.getId(), PROGRESS_SUCCESSS, result.getVideoid(), VideoProgress.VIDEO);

				Map<String, String> map = new HashMap<>();
				map.put("videoid", result.getVideoid());
				map.put("length", video.getTimes()+"");
				sendAnalytisEvent(AnalyticUtil.Event_Video_Publish_Success, map);
			}

			@Override
			public void failure(FailureResult result) {
				Logger.e(TAG, " 视频文件上传成功，但 上传视频信息到业务服务器失败 >>>>>>");
				video.setStatus(Video.STATUS_UPLOAD_FAIL);
				Database.updateVideo(context, video);
				sendUploadBroadcast(context, video.getId(), PROGRESS_FAILURE, null, VideoProgress.VIDEO);
			}
		}));
	}

	private void publishStoryVideo(final Context context, final DbStoryVideo video, final String videokey){
		UploadStoryVideoMessage message = UploadStoryVideoMessage.make(video, videokey);
		Volley.add(new MessageRequest(message, new MessageRequest.Listener<UploadStoryVideoResult>() {
			@Override
			public void success(UploadStoryVideoResult result) {
				Logger.d(TAG, " 发布精选集视频成功");
				video.setVideoid(result.getVideoid());
				video.setCreatetime(String.valueOf(result.getCreatetime() / 1000));
				video.setSectime(result.getCreatetime() / 1000);
				video.setStatus(Video.STATUS_UPLOAD_SUCCESS);
				video.setVideouri(result.getVideouri());
				CacheBean.getInstance().putString(context, CacheBean.getInstance().getLoginUserId(), video.getFilePath().replace(".mp4", ".jpg"));
				Database.updateStoryVideo(context, video);
				sendUploadBroadcast(context, video.getId(), PROGRESS_SUCCESSS, result.getVideoid(), VideoProgress.STORY_VIDEO);
			}

			@Override
			public void failure(FailureResult result) {
				Logger.e(TAG, "精选集视频文件上传成功，但上传视频信息到业务服务器失败 >>>>>>");
				video.setStatus(Video.STATUS_UPLOAD_FAIL);
				Database.updateStoryVideo(context, video);
				sendUploadBroadcast(context, video.getId(), PROGRESS_FAILURE, null, VideoProgress.STORY_VIDEO);
			}
		}));
	}
	
	public static boolean isUploadVideo(Integer id){
		return uploadVideoQueue.contains(id);
	}


	public static boolean isUploadStoryVideo(Integer id){
		return uploadStoryVideoQueue.contains(id);
	}


	private static UploadTokenResut getUploadToken(Context context, Integer rotate){
		HttpAccessor httpAccessor = new HttpAccessor(context);
		try {
			return  httpAccessor.call(new UploadTokenMessage(rotate, 0), UploadTokenResut.class);
		} catch (HttpException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void sendAnalytisEvent(String key,Map<String,String> params){
		Map<String,String> map = new HashMap<String, String>();
		map.put("userid", CacheBean.getInstance().getLoginUserId());
		if(params!=null){
			map.putAll(params);
		}
		CYAgent.onEvent(getApplicationContext(), key,map);
		MobclickAgent.onEvent(getApplicationContext(), key, map);
	}
}
