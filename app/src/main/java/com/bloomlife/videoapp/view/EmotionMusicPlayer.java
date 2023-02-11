package com.bloomlife.videoapp.view;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.text.TextUtils;

import com.bloomlife.android.log.Logger;
import com.bloomlife.android.log.LoggerFactory;
import com.bloomlife.videoapp.manager.EmotionMusicFileManager;
import com.bloomlife.videoapp.model.Emotion;

/**
 * Created by zhengxingtian lan4627@Gmail.com on 2015/9/22.
 */
public class EmotionMusicPlayer {

    private Logger mLog = LoggerFactory.getLogger("EmotionMusicPlayer");

    private VMediaPlayer mMusicPlayer;
    private Emotion mCurrentEmotion;
    private Emotion mDownloadEmotion;
    private Context mContext;

    public EmotionMusicPlayer(Context context){
        mContext = context.getApplicationContext();
    }

    /**
     * 初始化音乐播放器
     */
    public void initMusicPlayer(){
        mMusicPlayer = new VMediaPlayer();
        mMusicPlayer.setVolume(1.0f, 1.0f);
        mMusicPlayer.setOnPreparedListener(mMusicPlayerPrepared);
        mMusicPlayer.setOnCompletionListener(mMusicPlayerCompletion);
    }

    private MediaPlayer.OnPreparedListener mMusicPlayerPrepared = new MediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(MediaPlayer mp) {
            mp.start();
        }
    };

    private MediaPlayer.OnCompletionListener mMusicPlayerCompletion = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
            mp.start();
        }
    };

    /**
     * 设置音乐播放资源
     */
    public void setEmotion(Emotion emotion){
        if (emotion == null || TextUtils.isEmpty(emotion.getBgmusic())){
            return;
        } else {
            mCurrentEmotion = emotion;
        }
        try {
            if (emotion.getBgmusic().contains("http")){
                mMusicPlayer.setDataSource(EmotionMusicFileManager.getInstance().getFile(emotion.getBgmusic()));
            } else {
                int id = mContext.getResources().getIdentifier(
                        emotion.getBgmusic().replace(".mp3", ""),
                        "raw",
                        mContext.getPackageName()
                );
                AssetFileDescriptor fd = mContext.getResources().openRawResourceFd(id);
                mMusicPlayer.setDataSource(fd.getFileDescriptor(), fd.getStartOffset(), fd.getLength());
            }
            mMusicPlayer.prepareAsync();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 停止播放音乐
     */
    public void stop(){
        try {
            if (mMusicPlayer.isPlaying())
                mMusicPlayer.stop();
        } catch (IllegalStateException e){
            e.printStackTrace();
            mLog.e("停止音乐出错");
        }
    }

    public void pause(){
        try {
            if (mMusicPlayer.isPlaying())
                mMusicPlayer.pause();
        } catch (IllegalStateException e){
            e.printStackTrace();
            mLog.e("暂停音乐出错");
        }
    }

    public void start(){
        try {
            if (!mMusicPlayer.isPlaying())
                mMusicPlayer.start();
        } catch (IllegalStateException e){
            e.printStackTrace();
            mLog.e("开始音乐出错");
        }
    }

    public void resume(){
        try {
            if (mMusicPlayer.isPause())
                mMusicPlayer.start();
        } catch (IllegalStateException e){
            e.printStackTrace();
            mLog.e("恢复音乐出错");
        }
    }

    /**
     * 重置音乐播放器
     */
    public void reset(){
        try {
            mMusicPlayer.reset();
        } catch (IllegalStateException e){
            e.printStackTrace();
            mLog.e("重置音乐播放器出错");
        }
    }

    /**
     * 释放音乐播放器资源
     */
    public void release(){
        stop();
        reset();
        mMusicPlayer.release();
    }

    /**
     * 从头重新开始播发音乐
     */
    public void restart(){
        stop();
        reset();
        setEmotion(mCurrentEmotion);
    }

    public void switchMusic(Emotion emotion){
        if (emotion != null && !EmotionMusicFileManager.getInstance().isLoadSuccess(emotion.getBgmusic())){
            EmotionMusicFileManager.getInstance().loadFile(mContext, emotion.getBgmusic(), new MusicLoadListener());
            mDownloadEmotion = emotion;
            return;
        } else {
            mDownloadEmotion = null;
        }
        if (mCurrentEmotion != null && emotion != null && mCurrentEmotion.getBgmusic().equals(emotion.getBgmusic())){
            if (mMusicPlayer.isPause())
                mMusicPlayer.start();
            return;
        }
        stop();
        reset();
        if (emotion != null){
            setEmotion(emotion);
        } else {
            mCurrentEmotion = null;
        }
    }

    /**
     * 获取当前音乐播放进度
     * @return
     */
    public int getCurrentPosition(){
        int current = 0;
        try {
            current = mMusicPlayer.getCurrentPosition();
        } catch (IllegalStateException e){
            e.printStackTrace();
            mLog.e("获取当前音乐播放进度出错");
        }
        return current;
    }

    class MusicLoadListener implements EmotionMusicFileManager.LoadFileListener{

        MusicLoadListener(){

        }

        @Override
        public void onStart(String url) {

        }

        @Override
        public void onProgress(String url, int progress) {

        }

        @Override
        public void onSuccess(String url, String path) {
            if (mDownloadEmotion != null && mDownloadEmotion.getBgmusic().equals(url)){
                switchMusic(mDownloadEmotion);
            }
        }

        @Override
        public void onFailure(String url) {

        }
    }

}
