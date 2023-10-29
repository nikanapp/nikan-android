/**
 * 
 */
package com.bloomlife.videoapp.manager;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * @author <a href="mailto:lan4627@gmail.com">zxt</a>
 * 私信语音的下载管理
 * @date 2015年5月4日 下午5:54:16
 */
public class ChatAudioFileManager {
	
	private static final int DOWNLOAD_START = 3;
	private static final int DOWNLOAD_COMPLETE = 4;
	private static final int DOWNLOAD_ERROR = 5;

	private static volatile ChatAudioFileManager sManager;
	
	private TaskHandler mHandler = new TaskHandler(this);
	
	// 下载过的任务
	private List<String> mCompletedList = new ArrayList<String>();
	
	// 当前正在下载的任务
	private Map<String, DownloadListener> mTaskMap = new HashMap<>();
	
	private Executor mExecutor;
	
	private ChatAudioFileManager() {
		// TODO Auto-generated constructor stub
		mExecutor = Executors.newFixedThreadPool(3);
	}
	
	public static ChatAudioFileManager getInstance(){
		if (sManager == null){
			synchronized (ChatAudioFileManager.class) {
				if (sManager == null){
					sManager = new ChatAudioFileManager();
				}
			}
		}
		return sManager;
	}
	
	/**
	 * 下载音频文件
	 * @param context
	 * @param url
	 * @param listener
	 */
	public void download(Context context, String url, DownloadListener listener){
		// 已经下载过的直接返回
		if (mCompletedList.contains(url)) return;
		// 正在下载列表中有当前任务
		if (listener != null && mTaskMap.containsKey(url)){
			mTaskMap.put(url, listener);
			listener.start();
			return;
		}
		// 正在下载列表中没有当前任务，开启一个线程读取文件
		if (!mTaskMap.containsKey(url)){
			mTaskMap.put(url, listener);
			mExecutor.execute(new AudioFileLoadTask(context, url, mHandler));
		}
	}
	
	public File getFile(Context context, String url){
		if (url.contains("http")){
			return getCacheFile(context, url);
		} else {
			return new File(url);
		}
	}
	
	static class TaskHandler extends Handler{
		
		private WeakReference<ChatAudioFileManager> mReference;
		
		public TaskHandler(ChatAudioFileManager manager){
			mReference = new WeakReference<>(manager);
		}
		
		@Override
		public void handleMessage(Message msg) {
			ChatAudioFileManager manager = mReference.get();
			DownloadListener listener = null;
			if (manager != null){
				switch (msg.what) {
					case DOWNLOAD_START:
						// 开始下载语音
						listener = manager.mTaskMap.get(msg.obj);
						if (listener != null)
							listener.start();
						break;
					
					case DOWNLOAD_COMPLETE:
						// 下载语音完成
						manager.mCompletedList.add((String) msg.obj);
						listener = manager.mTaskMap.remove(msg.obj);
						if (listener != null)
							listener.complete();
						break;

					case DOWNLOAD_ERROR:
						manager.mTaskMap.remove(msg.obj);
						break;

				default:
					break;
				}
			}
			super.handleMessage(msg);
		}
		
	}
	
	public static File getCacheFile(Context context, String url){
		return new File(context.getExternalFilesDir(Environment.DIRECTORY_ALARMS), url.replace("/", "_").replace(":", "&"));
	}
	
	static class AudioFileLoadTask implements Runnable{
		
		private static final String TAG = AudioFileLoadTask.class.getSimpleName();

		private Context mContext;
		private String mDownloadUrl;
		private Handler mHandler;
		
		public AudioFileLoadTask(Context context, String url, Handler handler){
			mContext = context.getApplicationContext();
			mDownloadUrl = url;
			mHandler = handler;
		}
		
		private String download(String downloadUrl, File saveFile){
			mHandler.sendMessage(getMessage(DOWNLOAD_START, mDownloadUrl));
			long start = System.currentTimeMillis();
			HttpURLConnection huc = null;
			try {
				Log.d(TAG, "start to down audio >>>>>>>>>> ");
				URL url = new URL(downloadUrl);
				huc = (HttpURLConnection) url.openConnection();
				huc.setRequestMethod("GET");
				int hand = huc.getResponseCode();
				if (hand == 200){
					String filePath = saveAudio(saveFile, huc.getInputStream());
					mHandler.sendMessage(getMessage(DOWNLOAD_COMPLETE, mDownloadUrl));
					return filePath;
				} else {
					Log.e(TAG, "下载失败 返回码："+hand);
				}
			} catch (IOException e) {
				e.printStackTrace();
				if (saveFile.exists()){
					saveFile.delete();
				}
				mHandler.sendMessage(getMessage(DOWNLOAD_ERROR, mDownloadUrl));
				Log.e(TAG, "下载失败");
			} finally{
				if (huc != null)
					huc.disconnect();
				Log.d(TAG, "  download audio success , cause time "+(System.currentTimeMillis()-start)+" >>>>>>>>>> ");
			}
			return null;
		}
		
		private String saveAudio(File saveFile, InputStream is) throws IOException{
			BufferedOutputStream bos = null;
			bos = new BufferedOutputStream(new FileOutputStream(saveFile));
			byte[] buffer = new byte[2048];
			int offset = 0;
			while ((offset = is.read(buffer)) != -1){
				bos.write(buffer, 0, offset);
			}
			is.close();
			bos.flush();
			bos.close();
			return saveFile.getAbsolutePath();
		}

		@Override
		public void run() {
			// 检查文件是否在缓存文件夹里
			File file = getCacheFile(mContext, mDownloadUrl);
			if (!file.exists()){
				download(mDownloadUrl, file);
			} else {
				mHandler.sendMessage(getMessage(DOWNLOAD_COMPLETE, mDownloadUrl));
			}
		}
		
		private Message getMessage(int type, String url){
			Message msg = new Message();
			msg.what = type;
			msg.obj = url;
			return msg;
		}

	}
	
	public interface DownloadListener{
		void start();
		void complete();
		void fail();
	}

}
