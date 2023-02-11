package com.bloomlife.videoapp.manager;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.util.LruCache;

import com.bloomlife.android.common.util.CacheUtils;
import com.bloomlife.android.log.Logger;
import com.bloomlife.videoapp.app.Constants;
import com.bloomlife.videoapp.app.Database;
import com.bloomlife.videoapp.model.DbStoryVideo;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by zhengxingtian lan4627@Gmail.com on 2015/8/12.
 * 读取视频文件
 */
public class VideoFileManager {

    private static final String TAG = VideoFileManager.class.getSimpleName();
    private static VideoFileManager sManager;

    private Map<String, LoadFileListener> mExecuteListeners;
    private Map<String, DownLoadVideoTask> mDownLoadingTasks;
    private LinkedBlockingQueue<Runnable> mDownloadFileWorkQueue;
    private LinkedBlockingQueue<Runnable> mGetCacheFileWorkQueue;

    private LruCache<String, String> mFilePathCache = new LruCache<>(1024 * 8);
    private ExecutorService mDownloadFileExecutor;
    private ExecutorService mGetCacheFileExecutor;
    private Handler mHandler;

    private VideoFileManager(){
        mHandler = new Handler(Looper.getMainLooper());
        mExecuteListeners = new HashMap<>();
        mDownLoadingTasks = new ConcurrentHashMap<>();
        mDownloadFileWorkQueue = new LinkedBlockingQueue<>();
        mGetCacheFileWorkQueue = new LinkedBlockingQueue<>();
        mDownloadFileExecutor = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, mDownloadFileWorkQueue);
        mGetCacheFileExecutor = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, mGetCacheFileWorkQueue);
    }

    public static VideoFileManager getInstance(){
        if (sManager == null){
            synchronized (VideoFileManager.class) {
                if (sManager == null){
                    sManager = new VideoFileManager();
                }
            }
        }
        return sManager;
    }

    public void loadFile(Context context, String url){
        loadFile(context, url, null);
    }

    public void loadFile(Context context, String url, LoadFileListener listener){
        // 先读取缓存里看有没有
        String path = mFilePathCache.get(url);
        if (!TextUtils.isEmpty(path)){
            if (listener != null)
                listener.onLoadFile(url, path);
            return;
        }
        // 检查任务是否已经在执行列表中
        if (mExecuteListeners.containsKey(url)){
            // 任务执行列表中，把任务的Listener替换成当前的
            mExecuteListeners.put(url, listener);
            // 如果已经正在下载了，要重新触发onStart方法
            if (listener != null)
                listener.onStart(url);
        } else {
            // 没有在任务执行列表中，启动任务
            mExecuteListeners.put(url, listener);
            mGetCacheFileExecutor.execute(new GetVideoFileTask(context, url));
        }
    }

    class GetVideoFileTask implements Runnable {

        private Context mContext;
        private String mVideoUrl;

        public GetVideoFileTask(Context context, String videoUrl){
            mContext = context.getApplicationContext();
            mVideoUrl = videoUrl;
        }

        @Override
        public void run() {
            // 先查询数据库，看本地有没有视频
            DbStoryVideo video = Database.findStoryVideo(mContext, mVideoUrl);
            if (video != null && !TextUtils.isEmpty(video.getFilePath())){
                loadFileComplete(mVideoUrl, video.getFilePath());
                return;
            }
            // 查询缓存文件夹看视频是否已经下载过了
            File file = getLocalCache(mContext, mVideoUrl);
            if (file == null) return;
            if (file.exists()){
                loadFileComplete(mVideoUrl, file.getAbsolutePath());
            } else {
                // 如果文件不存在，启动一个下载任务
                DownLoadVideoTask task = new DownLoadVideoTask(mVideoUrl, file);
                mDownloadFileExecutor.execute(task);
            }
        }
    }

    class DownLoadVideoTask implements Runnable {

        private String mVideoUrl;
        private File mVideoFile;
        private boolean mCancel;

        public DownLoadVideoTask(String videoUrl, File videoFile){
            mVideoUrl = videoUrl;
            mVideoFile = videoFile;
            downloadStart(mVideoUrl);
        }

        @Override
        public void run() {
            mDownLoadingTasks.put(mVideoUrl, this);
            try {
                if (checkCancel()) return;
                downloadVideo(mVideoUrl, mVideoFile);
                if (checkCancel()) return;
                downloadSuccess(mVideoUrl, mVideoFile.getAbsolutePath());
            } catch (IOException e){
                if (checkCancel()) return;
                downloadFailure(mVideoUrl);
            } finally {
                mDownLoadingTasks.remove(this);
            }
        }

        private void downloadVideo(String videoUrl, File videoFile) throws IOException{
            try {
                if (!videoFile.exists())
                    videoFile.createNewFile();
                URL url = new URL(videoUrl);
                URLConnection connection = url.openConnection();
                connection.connect();
                int fileLength = connection.getContentLength();
                Log.d(TAG, " fileLength : " +fileLength);
                InputStream input = new BufferedInputStream(url.openStream());
                // Save the downloaded file
                OutputStream output = new FileOutputStream(videoFile);

                byte data[] = new byte[1024];  //最大8M
                long total = 0;
                int progress = 0;
                int count;
                while ((count = input.read(data)) != -1) {
                    total += count;
                    // Publish the progress
                    progress = (int)(total * 100 / fileLength);
                    downloadProgress(videoUrl, progress);
                    output.write(data, 0, count);
                }
                // Close connection
                output.flush();
                output.close();
                input.close();
                Log.d(TAG, " 视频 ： " + videoUrl + " 下载完成");
            } catch (IOException e){
                Log.e("Error", "下载视频出错", e);
                if (videoFile != null){
                    if (videoFile.delete()) Log.e(TAG, " 视频下载失败，删除失败文件成功");
                    else Log.e(TAG, " 视频下载失败，删除失败文件失败");
                }
                throw new IOException(e);
            }
        }

        private boolean checkCancel(){
            if (mCancel){
                downloadCancel(mVideoUrl, mVideoFile.exists() ? mVideoFile.getAbsolutePath() : null);
                return true;
            } else {
                return false;
            }
        }

        public boolean isCancel(){
            return this.mCancel;
        }

        public void setCancel(boolean cancel) {
            this.mCancel = cancel;
        }
    }

    private void loadFileComplete(final String url, final String path){
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mFilePathCache.put(url, path);
                LoadFileListener listener = mExecuteListeners.remove(url);
                if (listener != null)
                    listener.onLoadFile(url, path);
            }
        });
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

    private void downloadCancel(final String url, final String path){
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mExecuteListeners.remove(url);
                if (!TextUtils.isEmpty(path))
                    mFilePathCache.put(url, path);
            }
        });
    }

    public void removeVideoLoadingListener(String url){
        if (url == null)
            return;
        if (mExecuteListeners.containsKey(url)){
            mExecuteListeners.put(url, null);
        }
    }

    public void cancelAll(){
        mDownloadFileWorkQueue.clear();
        mGetCacheFileWorkQueue.clear();
        Iterator<String> iterator = mExecuteListeners.keySet().iterator();
        while (iterator.hasNext()){
            String taskUrl = iterator.next();
            if (mDownLoadingTasks.containsKey(taskUrl)){
                mDownLoadingTasks.get(taskUrl).setCancel(true);
            } else {
                iterator.remove();
            }
        }
    }

    public void evictAll(){
        mFilePathCache.evictAll();
        mExecuteListeners.clear();
        mDownLoadingTasks.clear();
        mDownloadFileWorkQueue.clear();
        mGetCacheFileWorkQueue.clear();
    }

    public void clearUrlCache(){
        mFilePathCache.evictAll();
    }

    public boolean checkErrorVideo(Context context, String url){
        File file = getLocalCache(context, url);
        // 视频文件如果小于100k，判断为错误文件
        if (file != null && file.exists() && file.isFile() && file.length() < 1024 * 100){
            file.delete();
            mFilePathCache.remove(url);
            return true;
        } else {
            return false;
        }
    }

    /**
     * 如果不支持外部文件读取，会返回空。
     * @param context
     * @param videoUri
     * @return	返回的file不为空，通过判断文件是否存在决定有没有缓存
     */
    public static File getLocalCache(Context context, String videoUri){
        int hashCode = videoUri.hashCode();
        File dirFile = CacheUtils.getCacheFileDirectory(context, Constants.CACHE_FOLDER_NAME);
        if (dirFile==null){
            Logger.e(TAG, "不支持外部文件读取");
            return null;
        }
        File file = new File(dirFile,hashCode+".mp4");
        return file;
    }

    public interface LoadFileListener {
        void onLoadFile(String url, String path);
        void onStart(String url);
        void onProgress(String url, int progress);
        void onSuccess(String url, String path);
        void onFailure(String url);
    }

}
