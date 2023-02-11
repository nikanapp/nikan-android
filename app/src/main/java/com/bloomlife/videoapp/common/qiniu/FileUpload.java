///**
// * 
// */
//package com.bloomlife.videoapp.common.qiniu;
//
//import java.util.Date;
//import java.util.Map;
//
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import android.content.Context;
//import android.util.Log;
//
//import com.bloomlife.android.bean.CacheBean;
//import com.bloomlife.android.common.util.DateUtils;
//import com.bloomlife.android.common.util.StringUtils;
//import com.bloomlife.android.common.util.UiHelper;
//import com.bloomlife.android.common.util.Utils;
//import com.bloomlife.videoapp.app.AppContext;
//import com.qiniu.android.http.ResponseInfo;
//import com.qiniu.android.storage.UpCompletionHandler;
//import com.qiniu.android.storage.UpProgressHandler;
//import com.qiniu.android.storage.UploadManager;
//import com.qiniu.android.storage.UploadOptions;
//
///**
// * @author <a href="mailto:xiai.fei@gmail.com">xiai_fei</a>
// * 
// * @date 2014-11-13 2:48:42
// */
//@Deprecated
//public class FileUpload {
//
//	private final static String Video_Prefix = "video/";
//
//	private static final String TAG = "FileUpload";
//
//	/**
//	 * 8 上传文件
//	 * 
//	 * @param context
//	 * @param filePath
//	 *            文件路径
//	 * @param token
//	 *            上传凭证
//	 * @param uploadCallback
//	 *            上传回调
//	 */
//	public static void uploadFile(final Context context, final String filePath, String token, final UploadCallback uploadCallback) {
//
//		if (StringUtils.isEmpty(filePath)) {
//			Log.e(TAG, " upload filepath cannot be empty ");
//			return;
//		}
//
//		final UploadManager uploadManager = new UploadManager();
//
//		// filterParam是自定义变量的设置，progress...是进度条，cancel...是分片上传判断是否上传需要终止的
//		final UploadOptions uploadOptions = new UploadOptions(null, null, true, new UpProgressHandler() {
//
//			@Override
//			public void progress(String arg0, double progress) {
//				if (uploadCallback != null) uploadCallback.progress(arg0, progress);
//			}
//		}, null);
//
//		final String fileKey = Video_Prefix + CacheBean.getInstance().getLoginUserId() + "/" + DateUtils.date2str(new Date()) + "/" + Utils.getRandcode(4) + filePath.substring(filePath.lastIndexOf("/"), filePath.length());
//		Log.d(TAG, " upload file key = " + fileKey);
//
//		// 正常流程
//		uploadManager.put(filePath, fileKey, token, new UpCompletionHandler() {
//			@Override
//			public void complete(String key, ResponseInfo info, JSONObject response) {
//				Log.i("qiniu", info.toString());
//
//				// 凭证过期，重新获取
//				if (QiniuConstants.Code_Auth_Failure == info.statusCode) {
//					doAuthorizationFailure(context, filePath, fileKey, uploadOptions, uploadCallback);
//					return;
//				}
//
//				doUploadResult(context, uploadCallback, fileKey, info, response);
//
//			}
//
//			
//		}, uploadOptions);
//	}
//
//	/**
//	 * 	处理上传后返回的结果（证书过期的结果不在这里处理）
//	 * @param context
//	 * @param uploadCallback
//	 * @param fileKey	文件的key
//	 * @param info	七牛sdk的调用结果
//	 * @param response	七牛返回的json数据
//	 */
//	private static void doUploadResult(final Context context, final UploadCallback uploadCallback, final String fileKey, ResponseInfo info, JSONObject response) {
//		if (QiniuConstants.Code_Suc != info.statusCode) {
//			UiHelper.showToast(context, info.error);
//			if (uploadCallback != null) uploadCallback.onFailure(fileKey, info.error);
//			return;
//		}
//		if (uploadCallback != null) {
//			// 获取预览图生成的七牛任务id
//			String persistentsId = null;
//			try {
//				persistentsId = response.getString("persistentId");
//			} catch (JSONException e) {
//				e.printStackTrace();
//			}
//			if (uploadCallback != null)uploadCallback.onSuccess(fileKey, persistentsId);
//		}
//	}
//
//	
//	public interface UploadCallback {
//
//		/**
//		 * 文件上传成功
//		 * 
//		 * @param url
//		 *            -- 文件在七牛上的访问路径
//		 * @param persistents
//		 *            -- 保存七牛的预览图任务id。，
//		 */
//		public void onSuccess(String url, String persistents);
//
//		public void onFailure(String key, String errorMsg);
//
//		/**
//		 * 进度反馈。
//		 * 
//		 * @param arg0
//		 * @param progress
//		 *            进度。
//		 */
//		public void progress(String fileKey, double progress);
//	}
//
//	/**
//	 * 
//	 * 失败后的再次重试上传
//	 * 
//	 * @param context
//	 * @param filePath
//	 * @param fileKey
//	 */
//	private static void doAuthorizationFailure(final Context context, final String filePath, final String fileKey, final UploadOptions uploadOptions, final UploadCallback uploadCallback) {
//		//再获取一次key
//		String uploadToken = null;
//		try {
//			uploadToken = QiniuUtils.getUpLoadToken(context);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		if (StringUtils.isEmpty(uploadToken)) {
//			Log.e(TAG, "  上传失败 ， 无法获取到token ");
//			if (uploadCallback != null) uploadCallback.onFailure(fileKey, " token failure ");
//			return;
//		}
//
//		// 执行上传操作
//		Log.d(TAG, " file path = " + filePath);
//		final UploadManager uploadManager = new UploadManager();
//		uploadManager.put(filePath, fileKey, uploadToken, new UpCompletionHandler() {
//			@Override
//			public void complete(String key, ResponseInfo info, JSONObject response) {
//				Log.i("qiniu", info.toString());
//				doUploadResult(context, uploadCallback, fileKey, info, response);
//			}
//
//		}, uploadOptions);
//	}
//
//}
