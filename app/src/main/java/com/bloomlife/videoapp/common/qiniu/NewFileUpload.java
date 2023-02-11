/**
 * 
 */
package com.bloomlife.videoapp.common.qiniu;

import org.apache.http.HttpException;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.bloomlife.android.common.util.StringUtils;
import com.bloomlife.android.common.util.UiHelper;
import com.bloomlife.android.network.HttpAccessor;
import com.bloomlife.videoapp.model.Video;
import com.bloomlife.videoapp.model.message.UploadTokenMessage;
import com.bloomlife.videoapp.model.result.UploadTokenResut;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UpProgressHandler;
import com.qiniu.android.storage.UploadManager;
import com.qiniu.android.storage.UploadOptions;

/**
 *   新颁布的七牛文件上传工具类。这个类会使用服务器返回的文件key作为上传到七牛中的文件key，不在本地生成。
 *
 * @author <a href="mailto:xiai.fei@gmail.com">xiai_fei</a>  
 *
 * @date 2015-2-12 下午12:15:00
 *
 * @organization bloomlife  
 * @version 1.0
 */
public class NewFileUpload {

	private static final String TAG = NewFileUpload.class.getSimpleName();
	

	/**
	 * 8 上传文件
	 * 
	 * @param context
	 * @param video  上传的video
	 * @param uploadCallback  上传回调
	 * 
	 * @notice 这个方法中最终的上传操作是运行在异步线程中的，需要通过回调获取结果。
	 */
	public static void uploadFile(final Context context, final Video video, final UploadCallback uploadCallback) {

		if (StringUtils.isEmpty(video.getFilaPath())) {
			Log.e(TAG, " upload filepath cannot be empty ");
			return;
		}
		
		
		final UploadTokenResut uploadToken = getUploadToken(context, video.getRotate());
		if (uploadToken==null) uploadCallback.onFailure(null, " fail on get upload token ");

		final UploadManager uploadManager = new UploadManager();

		// filterParam是自定义变量的设置，progress...是进度条，cancel...是分片上传判断是否上传需要终止的
		final UploadOptions uploadOptions = new UploadOptions(null, null, true, new UpProgressHandler() {

			@Override
			public void progress(String arg0, double progress) {
				if (uploadCallback != null) uploadCallback.progress(arg0, progress);
			}
		}, null);

		Log.d(TAG, " upload file key = " + uploadToken.getFilekey());

		// 正常流程
		uploadManager.put(video.getFilaPath(), uploadToken.getFilekey(), uploadToken.getUploadtoken(), new UpCompletionHandler() {
			@Override
			public void complete(String key, ResponseInfo info, JSONObject response) {
				Log.i("qiniu", info.toString());
				doUploadResult(context, uploadCallback, uploadToken.getFilekey(), info, response);
			}
		}, uploadOptions);
	}
	
	private static UploadTokenResut getUploadToken(Context context, Integer rotate){
		HttpAccessor httpAccessor = new HttpAccessor(context);
		try {
			return  httpAccessor.call(new UploadTokenMessage(rotate, 0),UploadTokenResut.class);
		} catch (HttpException e) {
			e.printStackTrace();
		}
		return null ;
	}
	

	/**
	 * 	处理上传后返回的结果（证书过期的结果不在这里处理）
	 * @param context
	 * @param uploadCallback
	 * @param fileKey	文件的key
	 * @param info	七牛sdk的调用结果
	 * @param response	七牛返回的json数据
	 */
	private static void doUploadResult(final Context context, final UploadCallback uploadCallback, final String fileKey, ResponseInfo info, JSONObject response) {
		if (QiniuConstants.Code_Suc != info.statusCode) {
			UiHelper.showToast(context, info.error);
			if (uploadCallback != null) uploadCallback.onFailure(fileKey, info.error);
			return;
		}
		if (uploadCallback != null) {
			// 获取预览图生成的七牛任务id
			String persistentsId = null;
			try {
				persistentsId = response.getString("persistentId");
			} catch (JSONException e) {
				e.printStackTrace();
			}
			if (uploadCallback != null)uploadCallback.onSuccess(fileKey, persistentsId);
		}
	}

	/***
	 * 上传回调。运行在主线程之中
	 */
	public interface UploadCallback {

		/**
		 * 文件上传成功
		 * 
		 * @param url
		 *            -- 文件在七牛上的访问路径
		 * @param persistents
		 *            -- 保存七牛的预览图任务id。，
		 */
		void onSuccess(String url, String persistents);

		void onFailure(String key, String errorMsg);

		/**
		 * 进度反馈。
		 * 
		 * @param arg0
		 * @param progress
		 *            进度。
		 */
		void progress(String fileKey, double progress);
	}

}
