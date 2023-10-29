package com.bloomlife.videoapp.activity;

import android.os.Bundle;
import android.view.View;

import com.android.volley.toolbox.MessageRequest;
import com.android.volley.toolbox.Volley;
import com.bloomlife.videoapp.common.util.Utils;
import com.bloomlife.videoapp.manager.VideoFileManager;
import com.bloomlife.videoapp.model.Emotion;
import com.bloomlife.videoapp.model.ResultStoryInfo;
import com.bloomlife.videoapp.model.Story;
import com.bloomlife.videoapp.model.StoryVideo;
import com.bloomlife.videoapp.model.message.GetStoryMessage;
import com.bloomlife.videoapp.model.result.GetStoryResult;

import java.util.ArrayList;

/**
 * Created by zxt lan4627@Gmail.com on 2015/8/24.
 */
public class MainStoryPlayActivity extends BaseStoryPlayActivity {

    private Story mStory;

    @Override
    protected void initStoryInfo(){
        // 精选集信息
        mStory = getIntent().getParcelableExtra(INTENT_STORY);
        // 观看数加一
        mStory.setLooknum(mStory.getLooknum() + 1);

        setStoryInfoText(mStory.getLooknum(), mStory.getLikenum(), mStory.getCommentnum());

        mStoryInfo = new ResultStoryInfo();
        mStoryInfo.setCommentNum(mStory.getCommentnum());
        mStoryInfo.setLikeNum(mStory.getLikenum());
        mStoryInfo.setLookNum(mStory.getLooknum());
        mStoryInfo.setUid(mStory.getUid());
        mStoryInfo.setStoryId(mStory.getId());

        mBtnMessage.setEnabled(false);
        Volley.addToTagQueue(new MessageRequest(new GetStoryMessage(mStory.getId()), mRequestListener));
    }

    private MessageRequest.Listener mRequestListener = new MessageRequest.Listener<GetStoryResult>(){

        @Override
        public void start() {
            mProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        public void success(GetStoryResult result) {
            mStoryCommentTextList = result.getTextComents();
            mStoryCommentList = result.getTagsComments();
            mSelectComment = result.getCommentagid();

            mCommentText.setText(String.valueOf(Utils.commentCount(result.getTagsComments(), result.getTextComents())));
            mFireButton.setFireButton(result.islike());

            ArrayList<Emotion> emotions = new ArrayList<>(result.getVideos().size());
            for (StoryVideo v:result.getVideos()){
                VideoFileManager.getInstance().loadFile(getApplicationContext(), v.getVideouri());
                emotions.add(getEmotion(v.getEmotionid()));
            }
            setViewPager(result.getVideos(), emotions);
        }

        @Override
        public void finish() {
            mProgressBar.setVisibility(View.GONE);
        }
    };

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(INTENT_STORY, mStory);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mStory = savedInstanceState.getParcelable(INTENT_STORY);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
    }

}
