package com.bloomlife.videoapp.activity;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.media.MediaPlayer;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;

import com.bloomlife.android.bean.CacheBean;
import com.bloomlife.android.common.util.HorizontalListView;
import com.bloomlife.android.framework.MyInjectActivity;
import com.bloomlife.videoapp.R;
import com.bloomlife.videoapp.adapter.EmotionListAdapter;
import com.bloomlife.videoapp.app.AppContext;
import com.bloomlife.videoapp.app.UploadBackgroundService;
import com.bloomlife.videoapp.common.CacheKeyConstants;
import com.bloomlife.videoapp.common.util.CameraUtil;
import com.bloomlife.videoapp.common.util.UIHelper;
import com.bloomlife.videoapp.common.util.Utils;
import com.bloomlife.videoapp.manager.EmotionMusicFileManager;
import com.bloomlife.videoapp.model.DbStoryVideo;
import com.bloomlife.videoapp.model.Emotion;
import com.bloomlife.videoapp.model.Video;
import com.bloomlife.videoapp.view.EmotionMusicPlayer;
import com.bloomlife.videoapp.view.SuperToast;
import com.bloomlife.videoapp.view.watermark.StoryWatermarkPlayer;

import net.tsz.afinal.annotation.view.ViewInject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhengxingtian lan4627@Gmail.com on 2015/8/17.
 * Story模式下拍摄视频后进入的视频编辑界面
 */
public class StoryVideoEditActivity extends VideoEditActivity implements View.OnClickListener{

    public static final int RESULT_STORY_VIDEO = 1002;

    @ViewInject(id=R.id.video_edit_emotion_list)
    private HorizontalListView mListView;

    @ViewInject(id=R.id.video_edit_watermark)
    private StoryWatermarkPlayer mWatermarkPlayer;

    private EmotionMusicPlayer mMusicPlayer;

    private EmotionMusicFileManager mMusicManager = EmotionMusicFileManager.getInstance();

    private EmotionListAdapter mAdapter;

    private Emotion mEmotion;
    private boolean mRunningLayoutAnim;

    @Override
    protected void setContentView(){
        setContentView(R.layout.activity_story_video_edit);
        MyInjectActivity.initInjectedView(this);
    }

    @Override
    protected void initLayoutView() {
        super.initLayoutView();
        List<Emotion> emotionList = new ArrayList<>(AppContext.getSysCode().getEmotions());
        emotionList.add(0, new Emotion());
        mAdapter = new EmotionListAdapter(this, emotionList);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(mOnItemClickListener);
        mMusicPlayer = new EmotionMusicPlayer(this);
        mMusicPlayer.initMusicPlayer();
        mWatermarkPlayer.setPauseListener(new PauseListener());
        setWatermarkDescription();
    }

    private void setWatermarkDescription(){
        String text = mEditDescription.getText().toString();
        mWatermarkPlayer.setInfo(
                CacheBean.getInstance().getString(this, CacheKeyConstants.LOCATION_CITY, ""),
                TextUtils.isEmpty(text) ? getString(R.string.activity_story_video_edit_description) : text,
                System.currentTimeMillis()
        );
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void pausePlay() {
        super.pausePlay();
        if (mEmotion != null){
            mWatermarkPlayer.pause();
            mMusicPlayer.pause();
        }
    }

    @Override
    protected void resumePlay() {
        super.resumePlay();
        if (mEmotion != null){
            mWatermarkPlayer.resume();
            mMusicPlayer.resume();
        }
    }

    @Override
    protected void startPlay() {
        super.startPlay();
        if (mEmotion != null){
            mWatermarkPlayer.start();
            mMusicPlayer.start();
        }
    }

    @Override
    protected void stopPlay() {
        super.stopPlay();
        if (mEmotion != null){
            mWatermarkPlayer.stop();
            mMusicPlayer.stop();
        }
    }

    @Override
    protected void videoPlayCompleted(MediaPlayer mp) {
        // 没有心情，直接播放视频
        if (mEmotion == null){
            super.videoPlayCompleted(mp);
            return;
        }
        // 水印音乐加载完成才能播放视频和水印
        if (mMusicManager.isLoadSuccess(mEmotion.getBgmusic())){
            super.videoPlayCompleted(mp);
            setWatermarkAndMusic(false);
        }
    }

    @Override
    protected void videoPlayTime(long time, long totalTime) {
        super.videoPlayTime(time, totalTime);
        mWatermarkPlayer.time(time, totalTime);
    }

    @Override
    protected void onDestroy() {
        mWatermarkPlayer.release();
        mMusicPlayer.release();
        super.onDestroy();
    }

    @Override
    protected void onSingleTapScreen(MotionEvent e) {
        UIHelper.hideSoftInput(this, mEditDescription);
        if (mRunningLayoutAnim)
            return;
        // 心情列表显示出来的时候要把点击在心情列表上的点击事件过滤掉，避免和点击心情时候的隐藏界面冲突
        if (mLayout.isShown() && e.getY() > mListView.getTop())
            return;
        if (mLayout.isShown()) {
            hideLayout();
        } else {
            showLayout();
        }
    }

    private int mLastClickItem = 0;

    private AdapterView.OnItemClickListener mOnItemClickListener = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (mLastClickItem == position){
                return;
            } else {
                mLastClickItem = position;
            }
            mAdapter.setSelected(position);
            mAdapter.notifyDataSetChanged();
            setWatermarkDescription();
            resetVideoPlayer();
            if (mProgressBar.isShown()){
                mProgressBar.setVisibility(View.GONE);
            }
            // 选中第一个的时候停止播放，恢复到原样
            if (position == 0){
                mEmotion = null;
                if (mWatermarkPlayer.isPlaying()){
                    mWatermarkPlayer.stop();
                }
                mMusicPlayer.switchMusic(null);
                mTimeText.setVisibility(View.VISIBLE);
                setVideoPlayerSource();
            } else {
                mEmotion = (Emotion) mAdapter.getItem(position);
                mTimeText.setVisibility(View.GONE);
                // 播放视频和水印、音乐
                playVideoAndWatermark();
            }
        }
    };

    private void playVideoAndWatermark(){
        // 判断音乐是否已经下载完成
        if (!mMusicManager.isLoadSuccess(mEmotion.getBgmusic())){
            // 没有下载的要启动下载程序，等下载完成在播放视频和水印
            mMusicManager.loadFile(
                    StoryVideoEditActivity.this,
                    mEmotion.getBgmusic(),
                    mLoadMusicFileListener
            );
            if (mWatermarkPlayer.isPlaying()){
                mWatermarkPlayer.stop();
                mMusicPlayer.stop();
            }
        } else {
            // 隐藏界面
            hideLayout();
            // 重新播放视频
            setVideoPlayerSource();
            // 选中其他的心情则切换播放水印和音乐
            setWatermarkAndMusic(true);
        }

    }

    private void setWatermarkAndMusic(boolean isSwitchMusic){
        // 切换音乐
        if (mWatermarkPlayer.isPlaying()){
            // 因为是异步线程在绘制水印，所以不一定能立刻停止，设置一个监听器让播放器停止后放另外的水印
            mWatermarkPlayer.setStopListener(new StopListener(isSwitchMusic));
            mWatermarkPlayer.stop();
            mMusicPlayer.stop();
        } else {
            setWatermarkDescription();
            mWatermarkPlayer.setEmotion(mEmotion);
            mWatermarkPlayer.start();
            if (isSwitchMusic){
                mMusicPlayer.switchMusic(mEmotion);
            } else {
                mMusicPlayer.restart();
            }
        }
    }

    class StopListener implements StoryWatermarkPlayer.OnStopListener {

        private boolean mSwitchMusic;

        public StopListener(boolean switchMusic){
            mSwitchMusic = switchMusic;
        }

        @Override
        public void onStop(StoryWatermarkPlayer player) {
            if (mSwitchMusic){
                mMusicPlayer.switchMusic(mEmotion);
            } else {
                mMusicPlayer.restart();
            }
            setWatermarkDescription();
            player.setStopListener(null);
            player.setEmotion(mEmotion);
            player.start();
        }
    }

    class PauseListener implements StoryWatermarkPlayer.OnPauseListener {

        @Override
        public void onPause(StoryWatermarkPlayer player) {

        }
    }

    private EmotionMusicFileManager.LoadFileListener mLoadMusicFileListener = new EmotionMusicFileManager.LoadFileListener() {
        @Override
        public void onStart(String url) {
            mProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        public void onProgress(String url, int progress) {

        }

        @Override
        public void onSuccess(String url, String path) {
            mProgressBar.setVisibility(View.GONE);
            if (mEmotion != null && url.equals(mEmotion.getBgmusic())){
                playVideoAndWatermark();
            }
        }

        @Override
        public void onFailure(String url) {
            mProgressBar.setVisibility(View.GONE);
            SuperToast.show(StoryVideoEditActivity.this, R.string.network_error_tips);
        }
    };

    @Override
    protected void upLoadVideo() {
        String description = mEditDescription.getText().toString().trim();
        if (TextUtils.isEmpty(description)) {
            mEditDescription.requestFocus();
            UIHelper.showSoftInput(this);
            return;
        }
        if (!Utils.isLogin(this, false)){
            showLoginDialog();
            return;
        }
        mUpload.setEnabled(false);
        mIsUpload = true;

        DbStoryVideo storyVideo = getStoryVideo();

        Intent uploadIntent = new Intent(getApplicationContext(), UploadBackgroundService.class);
        uploadIntent.putExtra(UploadBackgroundService.INTENT_UPLOAD_STORY_VIDEO, storyVideo);
        startService(uploadIntent);

        setResult(RESULT_STORY_VIDEO, new Intent());
        finish();
    }

    private DbStoryVideo getStoryVideo(){
        Video video = getVideo();
        DbStoryVideo storyVideo = new DbStoryVideo();
        CameraUtil.createVideoThumbnail(video.getFilaPath());
        storyVideo.setCreatetime(String.valueOf(System.currentTimeMillis()));
        storyVideo.setSectime(System.currentTimeMillis() / 1000);
        storyVideo.setDesc(video.getDescription());
        storyVideo.setCity(video.getCity());
        storyVideo.setSize(getFileSize(video.getFilaPath()));
        storyVideo.setUserId(CacheBean.getInstance().getLoginUserId());
        storyVideo.setLat(video.getLat());
        storyVideo.setLon(video.getLon());
        storyVideo.setEmotionid(mEmotion == null ? null : mEmotion.getId());
        storyVideo.setRotate(String.valueOf(video.getRotate()));
        storyVideo.setTimes(String.valueOf(video.getTimes()));
        storyVideo.setWidth(video.getWidth());
        storyVideo.setHeight(video.getHeight());
        storyVideo.setFilePath(video.getFilaPath());
        return storyVideo;
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
    }

    private void hideLayout(){
        Animator animator = ObjectAnimator.ofFloat(mLayout, "alpha", 1, 0);
        animator.addListener(mHideLayoutAnimListener);
        animator.start();
    }

    private void showLayout(){
        Animator animator = ObjectAnimator.ofFloat(mLayout, "alpha", 0, 1);
        animator.addListener(mShowLayoutAnimListener);
        animator.start();
    }

    private Animator.AnimatorListener mHideLayoutAnimListener = new Animator.AnimatorListener(){

        @Override
        public void onAnimationStart(Animator animation) {
            mRunningLayoutAnim = true;
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            mRunningLayoutAnim = false;
            mLayout.setVisibility(View.GONE);
        }

        @Override
        public void onAnimationCancel(Animator animation) {

        }

        @Override
        public void onAnimationRepeat(Animator animation) {

        }
    };

    private Animator.AnimatorListener mShowLayoutAnimListener = new Animator.AnimatorListener(){

        @Override
        public void onAnimationStart(Animator animation) {
            mRunningLayoutAnim = true;
            mLayout.setVisibility(View.VISIBLE);
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            mRunningLayoutAnim = false;
        }

        @Override
        public void onAnimationCancel(Animator animation) {

        }

        @Override
        public void onAnimationRepeat(Animator animation) {

        }
    };
}