package com.bloomlife.videoapp.activity;

import android.os.Handler;
import android.text.TextUtils;
import android.view.View;

import com.android.volley.toolbox.MessageRequest;
import com.android.volley.toolbox.Volley;
import com.bloomlife.videoapp.common.util.Utils;
import com.bloomlife.videoapp.manager.VideoFileManager;
import com.bloomlife.videoapp.model.Emotion;
import com.bloomlife.videoapp.model.ResultStoryInfo;
import com.bloomlife.videoapp.model.StoryVideo;
import com.bloomlife.videoapp.model.message.GetStoryMessage;
import com.bloomlife.videoapp.model.result.GetStoryResult;

import java.util.ArrayList;

/**
 * Created by zhengxingtian lan4627@Gmail.com on 2015/9/11.
 */
public class NotificationStoryPlayActivity extends BaseStoryPlayActivity {

    public static final String INTENT_USER_ID = "userId";
    public static final String INTENT_STORY_ID = "storyId";

    private Handler mHandler = new Handler();

    @Override
    protected void initPlayerView() {
        mStoryInfo = new ResultStoryInfo();
        mStoryInfo.setUid(getIntent().getStringExtra(INTENT_USER_ID));
        mStoryInfo.setStoryId(getIntent().getStringExtra(INTENT_STORY_ID));

        initStoryInfo();
        initViewPager();
        disEnableButtons();

        if (Utils.isMy(mStoryInfo.getUid())){
            mBtnMessage.setVisibility(View.INVISIBLE);
        } else {
            mBtnMessage.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void initStoryInfo() {
        mProgressBar.setVisibility(View.VISIBLE);
        Volley.addToTagQueue(new MessageRequest(new GetStoryMessage(mStoryInfo.getStoryId()), mGetStoryListener));
    }

    private MessageRequest.Listener mGetStoryListener = new MessageRequest.Listener<GetStoryResult>() {

        @Override
        public void success(GetStoryResult result) {
            if (Utils.isEmpty(result.getVideos())){
                return;
            }

//            mStoryInfo.setLikeNum(result.getLikenum());
//            mStoryInfo.setLookNum(result.getLooknum());
//            mStoryInfo.setCommentNum(result.getCommentnum());
//            setStoryInfoText(result.getLooknum(), result.getLikenum(), result.getCommentnum());

            mStoryCommentTextList = result.getTextComents();
            mStoryCommentList = result.getTagsComments();
            mSelectComment = result.getCommentagid();

            mCommentText.setText(String.valueOf(Utils.commentCount(result.getTagsComments(), result.getTextComents())));
            mFireButton.setFireButton(result.islike());

            ArrayList<Emotion> emotions = new ArrayList<>(result.getVideos().size());
            for (StoryVideo v: result.getVideos()){
                if (!TextUtils.isEmpty(v.getVideouri()))
                    VideoFileManager.getInstance().loadFile(getApplicationContext(), v.getVideouri());
                emotions.add(getEmotion(v.getEmotionid()));
            }
            setViewPager(result.getVideos(), emotions);

            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    showComment();
                }
            }, 400);
        }

        @Override
        public void finish() {
            mProgressBar.setVisibility(View.GONE);
        }
    };

    @Override
    public void onClick(View v) {
        super.onClick(v);
    }
}
