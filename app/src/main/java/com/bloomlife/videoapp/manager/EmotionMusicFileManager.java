package com.bloomlife.videoapp.manager;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import com.bloomlife.android.common.util.CacheUtils;
import com.bloomlife.android.log.Logger;
import com.bloomlife.videoapp.app.AppContext;
import com.bloomlife.videoapp.common.util.Utils;
import com.bloomlife.videoapp.model.Emotion;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by zxt lan4627@Gmail.com on 2015/9/17.
 */
public class EmotionMusicFileManager {

    private static final String TAG = EmotionMusicFileManager.class.getSimpleName();

    private static final String CACHE_FOLDER_NAME = "EmotionMusic";

    private static volatile EmotionMusicFileManager sManager;
    private HashMap<String, LoadFileListener> mExecuteListeners;
    private HashMap<String, String> mFilePathCache;
    private Handler mHandler;
    private Executor mExecutor;

    private EmotionMusicFileManager(){
        mHandler = new Handler(Looper.getMainLooper());
        mExecuteListeners = new HashMap<>();
        mFilePathCache = new HashMap<>();
        mExecutor = Executors.newSingleThreadExecutor();
    }

    public static EmotionMusicFileManager getInstance(){
        if (sManager == null){
            synchronized (EmotionMusicFileManager.class){
                if (sManager == null){
                    sManager = new EmotionMusicFileManager();
                }
            }
        }
        return sManager;
    }

    public void syncEmotionMusic(Context context, List<Emotion> emotions){
        if (Utils.isEmpty(emotions)){
            return;
        }
        for (Emotion e:emotions){
            if (e.getBgmusic() != null && e.getBgmusic().contains("http")){
                loadFile(context, e.getBgmusic());
            }
        }
    }

    public void loadFile(Context context, String url){
        loadFile(context, url, null);
    }

    public void loadFile(Context context, String url, LoadFileListener listener){
        // 文件已url在缓存里能找到，说明文件已经下载过了，直接从缓存里取
        if (mFilePathCache.containsKey(url)){
            if (listener != null){
                listener.onSuccess(url, mFilePathCache.get(url));
            }
            return;
        }
        if (!mExecuteListeners.containsKey(url)){
            mExecuteListeners.put(url, listener);
            startLoadFileTask(context.getApplicationContext(), url);
        } else {
            mExecuteListeners.put(url, listener);
        }
        if (listener != null){
            listener.onStart(url);
        }
    }

    public String getFile(String url){
        return mFilePathCache.get(url);
    }

    public boolean isLoadSuccess(String url){
        if (!TextUtils.isEmpty(url) && url.contains("http")){
            return mFilePathCache.containsKey(url);
        } else {
            return true;
        }
    }

    private void startLoadFileTask(Context context, String url){
        AppContext.EXECUTOR_SERVICE.execute(new LoadFileTask(context, url));
    }

    class LoadFileTask implements Runnable {

        private Context mContext;
        private String mUrl;

        public LoadFileTask(Context context, String url){
            mContext = context;
            mUrl = url;
        }

        @Override
        public void run() {
            // 从磁盘缓存里取，没有再去网络下载
            File file = getLocalCache(mContext, mUrl);
            if (file == null) {
                downloadFailure(mUrl);
                return;
            }
            if (file.exists()){
                downloadSuccess(mUrl, file.getAbsolutePath());
                return;
            }
            // 网络下载文件
            mExecutor.execute(new DownloadFileTask(mUrl, file));
        }
    }

    class DownloadFileTask implements Runnable{

        private File mSaveFile;
        private String mUrl;

        public DownloadFileTask(String url, File saveFile){
            mUrl = url;
            mSaveFile = saveFile;
        }

        @Override
        public void run() {
            try {
                downloadVideo(mUrl, mSaveFile);
                downloadSuccess(mUrl, mSaveFile.getAbsolutePath());
            } catch (IOException e) {
                e.printStackTrace();
                downloadFailure(mUrl);
            }
        }

        private void downloadVideo(String videoUrl, File videoFile) throws IOException {
            try {
                if (!videoFile.exists())
                    videoFile.createNewFile();
                URL url = new URL(videoUrl);
                URLConnection connection = url.openConnection();
                connection.connect();
                int fileLength = connection.getContentLength();
                Logger.d(TAG, " fileLength : " + fileLength);
                InputStream input = new BufferedInputStream(url.openStream());
                OutputStream output = new FileOutputStream(videoFile);

                byte data[] = new byte[1024];
                long total = 0;
                int progress = 0;
                int count;
                while ((count = input.read(data)) != -1) {
                    total += count;
                    progress = (int)(total * 100 / fileLength);
                    downloadProgress(videoUrl, progress);
                    output.write(data, 0, count);
                }
                output.flush();
                output.close();
                input.close();
                Logger.d(TAG, " 精选集音乐 ： " + videoUrl + " 下载完成");
            } catch (IOException e){
                Logger.e("Error", "精选集音乐下载出错" + e);
                if (videoFile != null){
                    if (videoFile.delete())
                        Log.e(TAG, " 精选集音乐下载失败，删除失败文件成功");
                    else
                        Logger.e(TAG, " 精选集音乐下载失败，删除失败文件失败");
                }
                throw new IOException(e);
            }
        }
    }

    private void downloadStart(final String url){
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                LoadFileListener listener = mExecuteListeners.get(url);
                if (listener != null)
                    listener.onStart(url);
            }
        });
    }

    private void downloadSuccess(final String url, final String path){
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mFilePathCache.put(url, path);
                LoadFileListener listener = mExecuteListeners.remove(url);
                if (listener != null)
                    listener.onSuccess(url, path);
            }
        });
    }

    private void downloadProgress(final String url, final int progress){
        if (progress < 0) return;
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                LoadFileListener listener = mExecuteListeners.get(url);
                if (listener != null)
                    listener.onProgress(url, progress);
            }
        });
    }

    private void downloadFailure(final String url){
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                LoadFileListener listener = mExecuteListeners.remove(url);
                if (listener != null)
                    listener.onFailure(url);
            }
        });
    }

    public static File getLocalCache(Context context, String videoUri){
        int hashCode = videoUri.hashCode();
        File dirFile = CacheUtils.getCacheFileDirectory(context, CACHE_FOLDER_NAME);
        if (dirFile == null){
            Logger.e(TAG, "不支持外部文件读取");
            return null;
        }
        File file = new File(dirFile, hashCode+".mp3");
        return file;
    }

    public interface LoadFileListener {
        void onStart(String url);
        void onProgress(String url, int progress);
        void onSuccess(String url, String path);
        void onFailure(String url);
    }
}
