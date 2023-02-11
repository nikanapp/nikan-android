/**
 * 
 */
package com.bloomlife.android.network;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.content.Context;
import android.util.Log;

import com.bloomlife.android.cache.FileCache;
import com.bloomlife.android.common.util.StringUtils;
import com.bloomlife.android.common.util.Utils;

/**
 * @author <a href="mailto:xiai.fei@gmail.com">xiai_fei</a>
 * 
 * @date 2014-8-8 下午3:21:11
 */
public class DownLoadUtils {

	private ExecutorService executorService = Executors.newFixedThreadPool(3);

	FileCache fileCache;

	public static final int TYPE_AUDIO = 1;
	public static final int TYPE_IMG = 0;

	private static DownLoadUtils instance;

	private DownLoadUtils(Context context) {
		fileCache = FileCache.getInstance(context);
		executorService = Executors.newFixedThreadPool(3);
	}

	public static synchronized DownLoadUtils getInstance(Context context) {
		if (instance == null)
			instance = new DownLoadUtils(context);
		return instance;
	}

	public void download(List<String> urlList) {
		download(urlList, null);
	}

	public void download(final String url, final DownloadCompeleteListener listener) {
		if (StringUtils.isEmpty(url))
			return;

		final File file = getRenameFile(url);
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				Log.e("", "从sd卡中存在本地缓存文件异常", e);
				return;
			}
		} else {
			if (listener != null) {
				listener.onCompelete(url, file.getAbsolutePath());
			}

		}
		executorService.execute(new Runnable() {

			@Override
			public void run() {
				downFromNet(url, file, listener);
			}
		});
	}

	public void download(List<String> urlList, final DownloadCompeleteListener listener) {
		if (Utils.isEmptyCollection(urlList))
			return;
		for (String url : urlList) {
			download(url, listener);
		}
	}

	public interface DownloadCompeleteListener {
		void onCompelete(String url, String filePath);
	}

	public static final String TAG = "";

	private boolean downFromNet(String url, File f, DownloadCompeleteListener listener) {
		try {
			URL imageUrl = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) imageUrl.openConnection();
			conn.setConnectTimeout(10000);
			conn.setReadTimeout(10000);
			conn.setInstanceFollowRedirects(true);
			InputStream is = conn.getInputStream();
			OutputStream os = new FileOutputStream(f);
			Utils.CopyStream(is, os);
			os.close();
			Log.d(TAG, "  down load audio : " + url + "  success");
			if (listener != null) {
				listener.onCompelete(url, f.getAbsolutePath());
			}
			return true;
		} catch (Throwable ex) {
			Log.e(TAG, "网络加载图片: " + url + " 异常", ex);
			if (ex instanceof OutOfMemoryError)
				;
			return false;
		}
	}

	private File getFile(String url) {
		File f = getRenameFile(url);
		if (!f.exists()) {
			try {
				f.createNewFile();
				return f;
			} catch (IOException e) {
				Log.e("", "从sd卡中存在本地缓存文件异常", e);
				return null;
			}
		}
		return f;
	}

	public String getFilePath(String url) {
		File file = getRenameFile(url);
		if (file == null)
			return null;
		return file.getAbsolutePath();
	}

	private File getRenameFile(String url) {
		String filename = String.valueOf(Math.abs(url.hashCode())) + ".amr";
		return fileCache.getFile(url, filename);
	}

}
