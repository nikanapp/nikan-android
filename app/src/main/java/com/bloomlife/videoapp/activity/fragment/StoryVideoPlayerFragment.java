package com.bloomlife.videoapp.activity.fragment;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.os.Bundle;

import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.FrameLayout;

import com.bloomlife.videoapp.R;
import com.bloomlife.videoapp.activity.BaseStoryPlayActivity;
import com.bloomlife.videoapp.app.AppContext;
import com.bloomlife.videoapp.common.util.Utils;
import com.bloomlife.videoapp.manager.EmotionMusicFileManager;
import com.bloomlife.videoapp.model.Emotion;
import com.bloomlife.videoapp.model.StoryVideo;
import com.bloomlife.videoapp.view.NetworkConnentErrorView;
import com.bloomlife.videoapp.view.SuperToast;
import com.bloomlife.videoapp.view.watermark.MkNoneView;
import com.bloomlife.videoapp.view.watermark.StoryWatermarkPlayer;
import com.bloomlife.videoapp.view.VideoLoadView;

import net.tsz.afinal.FinalActivity;
import net.tsz.afinal.annotation.view.ViewInject;

import androidx.annotation.Nullable;

/**
 * Created by zhengxingtian lan4627@Gmail.com on 2015/8/10.
 */
public class StoryVideoPlayerFragment extends VideoPlayerFragment implements View.OnClickListener{

    public static final String TAG = StoryVideoPlayerFragment.class.getSimpleName();

    public static final String INTENT_VIDEO = "video";
    public static final String INTENT_EMOTION = "emotion";
    public static final String INTENT_VIDEO_POSITION = "video_position";

    @ViewInject(id=R.id.fragment_story_play_textureview, click = ViewInject.DEFAULT)
    private TextureView mTextureView;

    @ViewInject(id=R.id.fragment_story_play_reloadView_stub)
    private ViewStub mVideoLoadStub;

    @ViewInject(id=R.id.fragment_story_play_watermark_stub)
    private ViewStub mWatermarkStub;

    @ViewInject(id=R.id.fragment_story_play_mk_none_stub)
    private ViewStub mMkNoneStub;

    private StoryWatermarkPlayer mWatermarkPlayer;

    private MkNoneView mMkNoneView;

    private VideoLoadView videoLoadView;

    private StoryVideo mVideo;
    private Emotion mEmotion;

    private SurfaceTexture mSurfaceTexture;
    private StoryVideoController mVideoController;

    private int mPosition;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mVideoController = (StoryVideoController) activity;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_story_video, container, false);
        FinalActivity.initInjectedView(this, layout);
        mPosition = getArguments().getInt(INTENT_VIDEO_POSITION);
        mVideo = getArguments().getParcelable(INTENT_VIDEO);
        mEmotion = getArguments().getParcelable(INTENT_EMOTION);
        initUI(layout);
        return layout;
    }

    private void initUI(View layout){
        mTextureView.setSurfaceTextureListener(mSurfaceTextureListener);
    }

    private void initWatermark(){
        if (mEmotion != null){
            if (mWatermarkPlayer == null)
                mWatermarkPlayer = (StoryWatermarkPlayer) mWatermarkStub.inflate();
            mWatermarkPlayer.bringToFront();
            mWatermarkPlayer.setEmotion(mEmotion);
            mWatermarkPlayer.setInfo(mVideo.getCity(), mVideo.getDescription(), mVideo.getSectime() * 1000);
        } else {
            if (mMkNoneView == null)
                mMkNoneView = (MkNoneView) mMkNoneStub.inflate();
            mMkNoneView.setInfo(mVideo.getCity(), mVideo.getDescription(), mVideo.getSectime() * 1000);
            mMkNoneView.showAll();
        }
    }

    private TextureView.SurfaceTextureListener mSurfaceTextureListener = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            Log.d(TAG, " position = "+mPosition+"  onSurfaceTextureAvailable");
            mSurfaceTexture = surface;
            if (isPageOnCurrent()) {
                createMediaPlayer();
            } else {
                Log.d(TAG, " isPageOnCurrent = " + isPageOnCurrent());
            }
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            if (isPageOnCurrent()){
                releaseMediaPlayer();
            }
            return false;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {

        }
    };

    private NetworkConnentErrorView.OnRetryListener mRetryListener = new NetworkConnentErrorView.OnRetryListener() {

        @Override
        public void onRetry() {
            if (isPageOnCurrent()) loadPlaySource();
        }
    };

    public String getEmotionId(){
        if (mEmotion != null)
            return mEmotion.getId();
        else
            return "";
    }

    public StoryVideo getVideo(){
        return mVideo;
    }

    public void createMediaPlayer(){
        initWatermark();
        if (mSurfaceTexture != null) {
            initVideoPlayer(mSurfaceTexture);
        }
    }

    public void releaseMediaPlayer(){
        releaseVideoPlayer();
        if (mWatermarkPlayer != null)
            mWatermarkPlayer.release();
    }

    public void startMediaPlayer(){
        if (((BaseStoryPlayActivity)getActivity()).isShowCommentDialog())
            return;
        if (startPlay()){
            if (mWatermarkPlayer != null)
                mWatermarkPlayer.start();
            mVideoController.switchMusic();
            mVideoController.setButtonEnable(true, mPosition);
        }
    }


    public void pauseMediaPlayer(){
        if (pausePlay()){
            if (mWatermarkPlayer != null)
                mWatermarkPlayer.pause();
        }
    }

    public void resumeMediaPlayer(){
        if (startPlay()){
            if (mWatermarkPlayer != null)
                mWatermarkPlayer.resume();
            mVideoController.setButtonEnable(true, mPosition);
        }
    }

    public void restartMediaPlayer(){
        releaseMediaPlayer();
        createMediaPlayer();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (isPlaying()){
            pauseMediaPlayer();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!isPlaying()) {
            resumeMediaPlayer();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    protected void loadPlaySource() {
        if (mVideo.getVideouri().contains("http")){
            startDownload(mVideo.getVideouri());
        } else {
            playLocalResource(mVideo.getVideouri());
        }
    }

    protected boolean isPageOnCurrent(){
        return BaseStoryPlayActivity.getCurrentPage() == mPosition;
    }

    @Override
    protected void onCompletion(MediaPlayer mp) {
        nextVideo();
    }

    @Override
    protected void onPrepared(MediaPlayer mp) {
        startMediaPlayer();
        hideProgress();
    }

    @Override
    protected boolean onError(MediaPlayer mp, int what, int extra) {
        nextVideo();
        return MediaPlayer.MEDIA_ERROR_UNKNOWN == what && -2147483648 == extra;
    }

    @Override
    protected void onVideoTime(long time, long totalTime) {
        if (mWatermarkPlayer != null)
            mWatermarkPlayer.time(time, totalTime);
    }

    protected void playLocalResource(String filePath) {
        super.playLocalResource(filePath);
    }

    protected void downloadVideoStart(){
        mVideoController.pauseMusic();
        showProgress();
    }

    protected void downloadVideoProgress(int progress){
        if (videoLoadView != null){
            // 音乐未下载完成的话，让进度卡在99%
            videoLoadView.refreshProgress(progress);
        }
    }

    protected void downloadVideoFailure(){
        if (videoLoadView != null)
            videoLoadView.showReloadStyle();
    }

    protected void downLoadVideoSuccess() {
        if (videoLoadView != null)
            videoLoadView.setLoadSuccess(true);
    }

    private void showProgress(){
        if (videoLoadView == null){
            inflateVideoLoadView();
        }
        videoLoadView.startProgressRefresh();
        mTextureView.setEnabled(false);
    }

    private void hideProgress(){
        if (videoLoadView == null){
            inflateVideoLoadView();
        }
        videoLoadView.loadSuccess();
        mTextureView.setEnabled(true);
    }

    private void inflateVideoLoadView(){
        videoLoadView = (VideoLoadView) mVideoLoadStub.inflate();
        videoLoadView.setOnRetryListener(mRetryListener);
        if(!TextUtils.isEmpty(mVideo.getBigpreviewurl())){
            videoLoadView.setBigPreviewImage(mVideo.getBigpreviewurl());
        }
    }

    @Override
    protected void setTextureSize(MediaPlayer mp) {
        float width = mp.getVideoWidth();
        float height = mp.getVideoHeight();
        final int screenHeight = AppContext.deviceInfo.getScreenHeight();
        final int screenWidth =AppContext.deviceInfo.getScreenWidth();

        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) mTextureView.getLayoutParams();
        if(screenHeight-height>screenWidth-width){
            //高度适配
            params.width = (int) (width*screenHeight/height);
            params.height = screenHeight;
        }else{
            //宽度适配
            params.width = screenWidth ;
            params.height = (int) (height * screenWidth/ width);
        }
        params.gravity = Gravity.CENTER;
        mTextureView.setLayoutParams(params);
        mTextureView.requestLayout();
    }

    private void nextVideo(){
        mVideoController.playNextStoryVideo();
        if (mWatermarkPlayer != null)
            mWatermarkPlayer.stop();
    }

    @Override
    public void releaseVideoPlayer() {
        super.releaseVideoPlayer();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.fragment_story_play_textureview:
                mVideoController.onClickVideo();
                break;
        }
    }

    public interface StoryVideoController{
        void setButtonEnable(boolean enable, int position);
        void playNextStoryVideo();
        void onClickVideo();
        void switchMusic();
        void pauseMusic();
    }

    public Bitmap getCapture(){
        return Utils.createShareBitmap(mWatermarkPlayer, mTextureView);
    }

}
