package com.bloomlife.videoapp.activity;

import android.text.TextUtils;
import android.view.View;

import com.android.volley.toolbox.MessageRequest;
import com.android.volley.toolbox.Volley;
import com.bloomlife.videoapp.manager.VideoFileManager;
import com.bloomlife.videoapp.model.Emotion;
import com.bloomlife.videoapp.model.ResultStoryInfo;
import com.bloomlife.videoapp.model.UserStory;
import com.bloomlife.videoapp.model.StoryVideo;
import com.bloomlife.videoapp.model.message.GetStoryMessage;
import com.bloomlife.videoapp.model.result.GetStoryResult;

import java.util.ArrayList;

/**
 * Created by zhengxingtian lan4627@Gmail.com on 2015/8/24.
 * 播放用户的精选集视频
 */
public class UserStoryPlayActivity extends BaseStoryPlayActivity {

    public static final String INTENT_USER_STORY = "myStory";
    public static final String INTENT_COMMENT_LIST		= "commentList";
    public static final String INTENT_COMMENT_TEXT_LIST = "commentTextList";
    public static final String INTENT_COMMENT_SELECT 	= "selectList";
    public static final String INTENT_LIKE = "like";

    private UserStory mStory;

    @Override
    protected void initStoryInfo(){
        mStory = getIntent().getParcelableExtra(INTENT_USER_STORY);
        mStoryCommentList = getIntent().getParcelableArrayListExtra(INTENT_COMMENT_LIST);
        mStoryCommentTextList = getIntent().getParcelableArrayListExtra(INTENT_COMMENT_TEXT_LIST);
        mSelectComment = getIntent().getStringExtra(INTENT_COMMENT_SELECT);
        mFireButton.setFireButton(getIntent().getBooleanExtra(INTENT_LIKE, false));

        // 观看数加一
        mStory.setLooknum(mStory.getLooknum() + 1);

        setStoryInfoText(mStory.getLooknum(), mStory.getLikeNum(), mStory.getCommentnum());

        mStoryInfo = new ResultStoryInfo();
        mStoryInfo.setCommentNum(mStory.getCommentnum());
        mStoryInfo.setLikeNum(mStory.getLikeNum());
        mStoryInfo.setLookNum(mStory.getLooknum());
        mStoryInfo.setUid(mStory.getUserId());
        mStoryInfo.setStoryId(mStory.getStoryid());

        ArrayList<Emotion> emotions = new ArrayList<>(mStory.getVideos().size());
        for (StoryVideo v: mStory.getVideos()){
            if (!TextUtils.isEmpty(v.getVideouri()))
                VideoFileManager.getInstance().loadFile(getApplicationContext(), v.getVideouri());
            emotions.add(getEmotion(v.getEmotionid()));
        }
        setViewPager(mStory.getVideos(), emotions);

        Volley.addToTagQueue(new MessageRequest(new GetStoryMessage(mStoryInfo.getStoryId()), mGetStoryListener));
    }

    private MessageRequest.Listener<GetStoryResult> mGetStoryListener = new MessageRequest.Listener<GetStoryResult>() {
        @Override
        public void success(GetStoryResult result) {
            mStoryCommentTextList = result.getTextComents();
            mStoryCommentList = result.getTagsComments();
            mSelectComment = result.getCommentagid();
            mFireButton.setFireButton(result.islike());
        }
    };

    @Override
    public void onClick(View v) {
        super.onClick(v);
    }

}
