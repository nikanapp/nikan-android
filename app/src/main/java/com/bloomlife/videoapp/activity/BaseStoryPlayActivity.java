package com.bloomlife.videoapp.activity;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.MessageRequest;
import com.android.volley.toolbox.Volley;
import com.bloomlife.android.bean.ProcessResult;
import com.bloomlife.android.framework.BaseActivity;
import com.bloomlife.android.framework.MyInjectActivity;
import com.bloomlife.videoapp.R;
import com.bloomlife.videoapp.activity.fragment.StoryVideoPlayerFragment;
import com.bloomlife.videoapp.adapter.PlayStoryVideoPagerAdapter;
import com.bloomlife.videoapp.app.AppContext;
import com.bloomlife.videoapp.app.Constants;
import com.bloomlife.videoapp.common.CommentText;
import com.bloomlife.videoapp.common.VideoReportListener;
import com.bloomlife.videoapp.common.util.ShareSDKUtil;
import com.bloomlife.videoapp.common.util.UIHelper;
import com.bloomlife.videoapp.common.util.Utils;
import com.bloomlife.videoapp.dialog.BaseDialog;
import com.bloomlife.videoapp.dialog.DialogUtils;
import com.bloomlife.videoapp.manager.VideoFileManager;
import com.bloomlife.videoapp.model.Comment;
import com.bloomlife.videoapp.model.CommentInfo;
import com.bloomlife.videoapp.model.Emotion;
import com.bloomlife.videoapp.model.ResultStoryInfo;
import com.bloomlife.videoapp.model.StoryVideo;
import com.bloomlife.videoapp.model.Video;
import com.bloomlife.videoapp.model.message.ReportMessage;
import com.bloomlife.videoapp.model.message.SendStoryLikeMessage;
import com.bloomlife.videoapp.view.EmotionMusicPlayer;
import com.bloomlife.videoapp.view.FireButton;
import com.bloomlife.videoapp.view.GlobalProgressBar;
import com.bloomlife.videoapp.view.ReportOptionView;
import com.bloomlife.videoapp.view.SuperToast;
import com.bloomlife.videoapp.view.dialog.MoreDialog;

import net.tsz.afinal.annotation.view.ViewInject;

import java.util.ArrayList;
import java.util.List;

import static com.bloomlife.videoapp.activity.fragment.StoryVideoPlayerFragment.*;

/**
 * Created by zhengxingtian lan4627@Gmail.com on 2015/8/10.
 * @parameter INTENT_STORY <Story> 传一个需要播放的精选集进来。
 * @parameter INTENT_INDEX <int> 指定要从精选集的哪一个视频开始播放，不需要指定可以不传，默认从第0个开始播放
 */
public abstract class BaseStoryPlayActivity extends BaseActivity implements StoryVideoController {

    public static final String INTENT_STORY = "story";
    public static final String INTENT_INDEX = "index";

    public static final int REQUEST_STORY = 2001;

    public static final String RESULT_STORY_INFO = "requestStoryInfo";

    @ViewInject(id = R.id.activity_play_story_viewpager)
    protected ViewPager mViewPager;

    @ViewInject(id = R.id.activity_video_play_info)
    protected ViewGroup mInfoLayout;

    @ViewInject(id = R.id.activity_play_story_close, click = ViewInject.DEFAULT)
    protected ImageView mBtnClose;

    @ViewInject(id=R.id.activity_play_story_msg, click=ViewInject.DEFAULT)
    protected ImageView mBtnMessage;

    @ViewInject(id=R.id.activity_video_play_views)
    protected TextView mViewText;

    @ViewInject(id=R.id.activity_video_play_like, click=ViewInject.DEFAULT)
    protected TextView mLikeText;

    @ViewInject(id=R.id.activity_video_play_like_des, click=ViewInject.DEFAULT)
    protected TextView mLikeTextDes;

    @ViewInject(id=R.id.activity_video_play_comments_num, click=ViewInject.DEFAULT)
    protected TextView mCommentText;

    @ViewInject(id=R.id.activity_video_play_more, click=ViewInject.DEFAULT)
    protected View mBtnMore;

    @ViewInject(id=R.id.activity_video_play_btn_fire)
    protected FireButton mFireButton;

    @ViewInject(id=R.id.activity_video_play_fire)
    protected ImageView mImageFire;

    @ViewInject(id=R.id.activity_video_play_progressbar)
    protected GlobalProgressBar mProgressBar;

    public static final int COMMENT_REQUEST_CODE = 9999;
    public static final int CHAT_REQUEST_CODE = 9998;

    private static int sCurrentPage;
    private static int sPreviousPage;

    protected List<Comment> mStoryCommentList;
    protected List<CommentText> mStoryCommentTextList;

    protected Dialog mMoreDialog;
    protected String mSelectComment;
    protected int mIndex;
    protected boolean mAlong;
    protected boolean mShowCommentDialog;
    protected ResultStoryInfo mStoryInfo;

    private List<Emotion> mEmotionList;
    private boolean mFirst = true;

    private PlayStoryVideoPagerAdapter mAdapter;
    private EmotionMusicPlayer mMusicPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_story);
        MyInjectActivity.initInjectedView(this);
        setTextTypeface();
        initPlayerView();
    }

    abstract protected void initStoryInfo();

    protected void setTextTypeface(){
        Typeface bebas = UIHelper.getHelveticaTh(this);
        mViewText.setTypeface(bebas);
        mLikeText.setTypeface(bebas);
        mCommentText.setTypeface(bebas);
        mFireButton.setFireButtonListener(mFireBtnListener);
    }

    protected void setStoryInfoText(int lookNum, int likeNum, int commentNum){
        mViewText.setText(String.valueOf(lookNum));
        mLikeText.setText(String.valueOf(likeNum));
        mCommentText.setText(String.valueOf(commentNum));
    }

    protected void initPlayerView(){
        initStoryInfo();
        initViewPager();
        if (Utils.isMy(mStoryInfo.getUid())){
            mBtnMessage.setVisibility(View.INVISIBLE);
        } else {
            mBtnMessage.setVisibility(View.VISIBLE);
        }
        disEnableButtons();
        mMusicPlayer = new EmotionMusicPlayer(this);
        mMusicPlayer.initMusicPlayer();
    }

    protected void initViewPager(){
        // 视频开始播放的位置
        mIndex = getIntent().getIntExtra(INTENT_INDEX, 0);
        sCurrentPage = mIndex;
        mAlong = (mIndex == 0);
        mViewPager.setOnPageChangeListener(mOnPageChangeListener);
    }

    protected void setViewPager(List<StoryVideo> storyVideos, List<Emotion> emotions){
        mEmotionList = emotions;
        mAdapter = new PlayStoryVideoPagerAdapter(getFragmentManager(), storyVideos, emotions);
        mViewPager.setAdapter(mAdapter);
        UIHelper.setViewPagerScrollSpeed(mViewPager, 300);
    }

    /** 音乐相关 **/


    public static int getCurrentPage(){
        return sCurrentPage;
    }

    public StoryVideoPlayerFragment getCurrentFragment(){
        return (StoryVideoPlayerFragment) mAdapter.getFragment(sCurrentPage);
    }

    private ViewPager.OnPageChangeListener mOnPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageScrolled(int i, float v, int i1) {
        }

        @Override
        public void onPageSelected(int i) {
            if (sCurrentPage != i){
                // 切面切换完成后释放上一个页面的播放资源，然后开始播放本页面的视频
                StoryVideoPlayerFragment currentFragment = (StoryVideoPlayerFragment) mAdapter.getFragment(sCurrentPage);
                StoryVideoPlayerFragment newFragment = (StoryVideoPlayerFragment) mAdapter.getFragment(i);
                if (currentFragment != null) {
                    currentFragment.releaseMediaPlayer();
                }
                if (newFragment != null)
                    newFragment.createMediaPlayer();
                sCurrentPage = i;
            }
            if (sCurrentPage == 0) {
                mAlong = true;
                return;
            }
            if (sCurrentPage == mAdapter.getCount() - 1){
                mAlong = false;
                return;
            }
        }

        @Override
        public void onPageScrollStateChanged(int i) {
            switch (i) {
                case ViewPager.SCROLL_STATE_DRAGGING:
                    sPreviousPage = sCurrentPage;
                    // 开始切换页面时暂停播放视频, 和关闭按钮的点击
                    pauseCurrentVideo();
                    disEnableButtons();
                    break;

                case ViewPager.SCROLL_STATE_SETTLING:
                    break;

                case ViewPager.SCROLL_STATE_IDLE:
                    // 如果滑动停止后还是在同一个页面，继续播放视频。。
                    if (sPreviousPage == sCurrentPage){
                        resumeCurrentVideo();
                    }
                    break;

                default:
                    break;
            }
        }

        private boolean isFirstOrLastVideo(int position){
            return position == 0 || position == mAdapter.getCount()-1;
        }
    };

    protected Emotion getEmotion(String id){
        List<Emotion> emotions = AppContext.getSysCode().getEmotions();
        for (Emotion e:emotions){
            if (e.getId().equals(id))
                return e;
        }
        return null;
    }

    public boolean isShowCommentDialog(){
        return mShowCommentDialog;
    }

    protected void showComment(){
        if (mShowCommentDialog)
            return;
        else
            mShowCommentDialog = true;
        Intent intent = new Intent(this, VideoCommentActivity.class);
        startActivityForResult(setIntentData(intent), COMMENT_REQUEST_CODE);
        overridePendingTransition(R.anim.activity_bottom_in, 0);
    }

    private Intent setIntentData(Intent intent){
        if (mStoryCommentList != null && mStoryCommentTextList != null){
            CommentInfo info = new CommentInfo();
            Video video = new Video();
            video.setUid(mStoryInfo.getUid());
            video.setVideoid(mStoryInfo.getStoryId());
            info.setVideo(video);
            info.setStoryId(mStoryInfo.getStoryId());

            info.setCommentList(mStoryCommentList);
            info.setCommentTextList(mStoryCommentTextList);
            info.setSelectComment(mSelectComment);
            info.setAllowComment(Constants.COMMENT_ON);
            intent.putExtra(VideoCommentActivity.INTENT_INFO, info);
        }
        return intent;
    }

    private FireButton.FireButtonListener mFireBtnListener = new FireButton.FireButtonListener() {

        @Override
        public void onAnimationEnd() {

        }

        @Override
        public void onClick(View view) {
            if (mFireButton.isChecked()){
                fireAnimator();
                mStoryInfo.setLikeNum(mStoryInfo.getLikeNum() + 1);
            } else {
                mStoryInfo.setLikeNum(mStoryInfo.getLikeNum() - 1);
            }
            refreshLikeNumText();
            Volley.addToTagQueue(new MessageRequest(new SendStoryLikeMessage(mStoryInfo.getStoryId())));
        }
    };

    private void refreshLikeNumText(){
        mLikeText.setText(getStringNum(mStoryInfo.getLikeNum()));
    }

    private void fireAnimator(){
        ValueAnimator anim = ValueAnimator.ofFloat(0, 1);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                mImageFire.setTranslationY(-mImageFire.getHeight() * 1.5f * value);
                mImageFire.setAlpha(1 - value);
            }
        });
        anim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                mImageFire.setAlpha(1.0f);
                mImageFire.setTranslationY(0.0f);
            }

            @Override
            public void onAnimationEnd(Animator animation) {

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        anim.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMusicPlayer.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mFirst){
            mFirst = false;
            mViewPager.setCurrentItem(sCurrentPage);
        } else {
            mMusicPlayer.resume();
        }
    }

    @Override
    protected void onDestroy() {
        mMusicPlayer.release();
        super.onDestroy();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.activity_play_story_close:
                finish();
                break;

            case R.id.activity_video_play_like_des:
            case R.id.activity_video_play_like:
                DialogUtils.showLikeList(this, mStoryInfo.getStoryId(), new BaseDialog.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        resumeCurrentVideo();
                    }
                });
                pauseCurrentVideo();
                break;

            case R.id.activity_video_play_comments_num:
                showComment();
                break;

            case R.id.activity_video_play_more:
                pauseCurrentVideo();
                if (mMoreDialog == null)
                    mMoreDialog = MoreDialog.show(this, mMoreListener, mStoryInfo.getUid(), MoreDialog.Type.SHARE, MoreDialog.Type.REPORT);
                else
                    mMoreDialog.show();
                break;

            case R.id.activity_play_story_msg:
                // 检查用户是否登陆
                if (Utils.isLogin(this, true) && !Utils.isMy(mStoryInfo.getUid())){
                    showMessage();
                }
                break;
        }
    }

    private void showMessage(){
        Intent intent = new Intent(this, RealNameChatActivity.class);
        intent.putExtra(RealNameChatActivity.INTENT_USERNAME, mStoryInfo.getUid());
        startActivity(intent);
    }

    private MoreDialog.OnClickListener mMoreListener = new MoreDialog.OnClickListener() {
        @Override
        public void onClick(TextView tv, MoreDialog.Type type) {
            switch (type){
                case SHARE:
                    StoryVideoPlayerFragment fragment = getCurrentFragment();
                    if (fragment == null)
                        return;
                    Bitmap bitmap = fragment.getCapture();
                    if (bitmap == null){
                        return;
                    }
                    ShareSDKUtil.shareStoryVideo(BaseStoryPlayActivity.this, fragment.getVideo(), bitmap, mShareDialogListener);
                    break;

                case REPORT:
                    ReportOptionView.showDialog(BaseStoryPlayActivity.this, mReportBtnListener);
                    break;
            }
        }

        @Override
        public void onDismiss() {

        }

        @Override
        public void onShow() {

        }

        @Override
        public void onCancel() {
            resumeCurrentVideo();
        }
    };

    private ShareSDKUtil.ShareWindowListener mShareDialogListener = new ShareSDKUtil.ShareWindowListener() {

        @Override
        public void onCancel() {
            resumeCurrentVideo();
        }

        @Override
        public void onClick() {

        }

        @Override
        public void show() {

        }
    };

    private ReportOptionView.ReportListener mReportBtnListener = new VideoReportListener() {
        @Override
        public void onClick(String content) {
            StoryVideoPlayerFragment fragment = getCurrentFragment();
            if (fragment == null)
                return;
            ReportMessage message = new ReportMessage(fragment.getVideo().getId(), getReportType(BaseStoryPlayActivity.this, content), TYPE_VIDEO);
            Volley.getRequestQueue().add(new MessageRequest(message, new MessageRequest.ProcessReqListener(){
                @Override
                public void success(ProcessResult result) {
                    new SuperToast(BaseStoryPlayActivity.this, getString(R.string.fragment_report_succ));
                }
            }));
        }

        @Override
        public void onDismiss(DialogInterface dialog) {
            resumeCurrentVideo();
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (COMMENT_REQUEST_CODE == requestCode){
            mShowCommentDialog = false;
            if (VideoCommentActivity.RESULT_COMMENT_OK == resultCode){
                CommentInfo info = data.getParcelableExtra(VideoCommentActivity.RESULT_INFO);
                mStoryInfo.setCommentNum(Utils.commentCount(info.getCommentList(), info.getCommentTextList()));

                mSelectComment = info.getSelectComment();
                mStoryCommentList = info.getCommentList();
                mStoryCommentTextList = info.getCommentTextList();
                mCommentText.setText(getStringNum(mStoryInfo.getCommentNum()));
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private String getStringNum(int num){
        if (num > 9999){
            return "9999+";
        } else {
            return String.valueOf(num);
        }
    }

    @Override
    public void finish() {
        Intent intent = new Intent();
        intent.putExtra(RESULT_STORY_INFO, mStoryInfo);
        setResult(RESULT_OK, intent);
        VideoFileManager.getInstance().cancelAll();
        super.finish();
    }

    public void pauseCurrentVideo(){
        mMusicPlayer.pause();
        StoryVideoPlayerFragment fragment = (StoryVideoPlayerFragment) mAdapter.getFragment(sCurrentPage);
        if (fragment != null)
            fragment.pauseMediaPlayer();
    }

    public void resumeCurrentVideo(){
        mMusicPlayer.resume();
        StoryVideoPlayerFragment fragment = (StoryVideoPlayerFragment) mAdapter.getFragment(sCurrentPage);
        if (fragment != null)
            fragment.resumeMediaPlayer();
    }

    public void restartCurrentVideo(){
        mMusicPlayer.restart();
        StoryVideoPlayerFragment fragment = (StoryVideoPlayerFragment) mAdapter.getFragment(sCurrentPage);
        if (fragment != null)
            fragment.restartMediaPlayer();
    }

    @Override
    public void setButtonEnable(boolean enable, int position) {
        if (getCurrentPage() == position){
            mFireButton.setEnabled(enable);
            mBtnMessage.setEnabled(enable);
            mBtnMore.setEnabled(enable);
            mLikeText.setEnabled(enable);
            mLikeTextDes.setEnabled(enable);
            mCommentText.setEnabled(enable);
        }
    }

    protected void disEnableButtons(){
        setButtonEnable(false, getCurrentPage());
    }

    @Override
    public void playNextStoryVideo() {
        if (mAdapter.getCount() == 1){
            restartCurrentVideo();
        }
        if (mAlong){
            mViewPager.setCurrentItem(sCurrentPage + 1);
        } else {
            mViewPager.setCurrentItem(sCurrentPage - 1);
        }
    }

    @Override
    public void onClickVideo() {
        showComment();
    }

    @Override
    public void pauseMusic() {
        mMusicPlayer.pause();
    }

    @Override
    public void switchMusic() {
        // 切换音乐
        mMusicPlayer.switchMusic(mEmotionList.get(sCurrentPage));
    }
}
