package com.bloomlife.videoapp.activity.fragment;

import androidx.fragment.app.Fragment;
import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Surface;

import com.bloomlife.android.log.Logger;
import com.bloomlife.android.log.LoggerFactory;
import com.bloomlife.videoapp.app.AppContext;
import com.bloomlife.videoapp.manager.VideoFileManager;
import com.bloomlife.videoapp.view.SuperToast;
import com.bloomlife.videoapp.view.VMediaPlayer;

import java.io.IOException;
import java.lang.ref.WeakReference;

/**
 * Created by zhengxingtian lan4627@Gmail.com on 2015/8/10.
 */
public abstract class VideoPlayerFragment extends Fragment {

    public static final String TAG = VideoPlayerFragment.class.getSimpleName();

    public static final int NATIVE = 1;
    public static final int NETWORK = 2;

    private Logger mLog = LoggerFactory.getLogger(TAG);
    private Handler mHandler = new MyHandler(this);
    private volatile boolean mIsPlayTimeRun;

    private VMediaPlayer mMediaPlayer;
    private Surface mSurface;
    private String mVideoUrl;

    protected VMediaPlayer initVideoPlayer(SurfaceTexture surface){
        return initVideoPlayer(new Surface(surface));
    }

    protected VMediaPlayer initVideoPlayer(Surface surface){
        mSurface = surface;
        mMediaPlayer = new VMediaPlayer();
        mMediaPlayer.setOnCompletionListener(mCompletionListener);
        mMediaPlayer.setOnPreparedListener(mPreparedListener);
        mMediaPlayer.setOnErrorListener(mErrorListener);
        mMediaPlayer.setLooping(false);
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mMediaPlayer.setSurface(surface);
            loadPlaySource();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mMediaPlayer;
    }

    private MediaPlayer.OnCompletionListener mCompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
            VideoPlayerFragment.this.onCompletion(mp);
        }
    };


    private MediaPlayer.OnPreparedListener mPreparedListener = new MediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(MediaPlayer mp) {
            setTextureSize(mp);
            VideoPlayerFragment.this.onPrepared(mp);
        }
    };

    private MediaPlayer.OnErrorListener mErrorListener = new MediaPlayer.OnErrorListener() {
        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            Logger.e(TAG, " 播放视频出错  what =" + what + " and extra = " + extra);
            if (VMediaPlayer.MEDIA_ERROR_NO_PERPARE == what){
                VideoFileManager.getInstance().checkErrorVideo(getActivity(), mVideoUrl);
                // 重新下载
                loadPlaySource();
            }
            return VideoPlayerFragment.this.onError(mp, what, extra);
        }
    };

    protected abstract void loadPlaySource();

    protected abstract void onCompletion(MediaPlayer mp);

    protected abstract void onPrepared(MediaPlayer mp);

    protected abstract boolean onError(MediaPlayer mp, int what, int extra);

    protected abstract void downloadVideoStart();

    protected abstract void downloadVideoProgress(int progress);

    protected abstract void downloadVideoFailure();

    protected abstract void downLoadVideoSuccess();

    /**
     * @param mp
     */
    protected abstract void setTextureSize(MediaPlayer mp);

    public VMediaPlayer getMediaPlayer(){
        return mMediaPlayer;
    }

    protected boolean startPlay() {
        if (mMediaPlayer == null || mMediaPlayer.isPlaying())
            return false;
        else
            mMediaPlayer.start();
        // 开始计算当前视频的播放时间
        if (!mIsPlayTimeRun)
            AppContext.EXECUTOR_SERVICE.execute(mPlayTime);
        return true;
    }

    protected boolean pausePlay(){
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()){
            mMediaPlayer.pause();
            return true;
        } else {
            return false;
        }
    }

    protected boolean stopPlay(){
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()){
            mMediaPlayer.stop();
            return true;
        } else {
            return false;
        }
    }

    public boolean isPlaying(){
        if (mMediaPlayer != null && !mMediaPlayer.isRelease()){
            return mMediaPlayer.isPlaying();
        }
        return false;
    }

    /**释放当前的播放资源*/
    public void releaseVideoPlayer(){
        mLog.d("releseMediaplay");
        if (mMediaPlayer == null) return;
        if (mMediaPlayer.isPlaying()){
            mMediaPlayer.stop();
        }
        mMediaPlayer.reset();
        mMediaPlayer.release();
        mMediaPlayer = null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        VideoFileManager.getInstance().removeVideoLoadingListener(mVideoUrl);
    }

    static class MyHandler extends Handler {

        private WeakReference<VideoPlayerFragment> mReference;

        public MyHandler(VideoPlayerFragment fragment){
            mReference = new WeakReference<>(fragment);
        }

        @Override
        public void handleMessage(Message msg) {
            VideoPlayerFragment fragment = mReference.get();
            if (fragment != null){
                if (fragment.isPlaying())
                    fragment.onVideoTime(fragment.mMediaPlayer.getCurrentPosition(), fragment.mMediaPlayer.getDuration());
            }
            super.handleMessage(msg);
        }

    }

    protected void onVideoTime(long time, long totalTime){

    }

    /**
     * 获取视频播放时间
     */
    private Runnable mPlayTime = new Runnable(){

        @Override
        public void run() {
            mIsPlayTimeRun = true;
            Log.v(TAG, "mPlayTime start");
            while (isPlaying()) {
                Message msg = new Message();
                mHandler.sendMessage(msg);
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            mIsPlayTimeRun = false;
            Log.v(TAG, "mPlayTime end");
        }

    };

    protected void playLocalResource(String filePath){
        try {
            mMediaPlayer.setDataSource(filePath);
            mMediaPlayer.prepareAsync();
        } catch (IOException e){
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void startDownload(String videoUrl){
        if (TextUtils.isEmpty(videoUrl))
            return;
        VideoFileManager.getInstance().loadFile(getActivity(), videoUrl, mLoadFileListener);
        mVideoUrl = videoUrl;
    }

    private VideoFileManager.LoadFileListener mLoadFileListener = new VideoFileManager.LoadFileListener() {

        @Override
        public void onLoadFile(String url, String path) {
            mLog.d(" path = " + path + "  ， 找到网络文件: " + url + " 的缓存，直接播放");
            playLocalResource(path);
        }

        @Override
        public void onStart(String url) {
            downloadVideoStart();
        }

        @Override
        public void onProgress(String url, int progress) {
            downloadVideoProgress(progress);
        }

        @Override
        public void onSuccess(String url, String path) {
            mLog.d("视频文件下载完成 "+ url +" 路径 ：" + path);
            playLocalResource(path);
            downLoadVideoSuccess();
        }

        @Override
        public void onFailure(String url) {
            downloadVideoFailure();
        }
    };

}
