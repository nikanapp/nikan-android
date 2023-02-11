package com.bloomlife.videoapp.adapter;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;

import com.bloomlife.videoapp.activity.fragment.StoryVideoPlayerFragment;
import com.bloomlife.videoapp.model.Emotion;
import com.bloomlife.videoapp.model.StoryVideo;

import java.util.List;

/**
 * Created by zhengxingtian lan4627@Gmail.com on 2015/8/10.
 */
public class PlayStoryVideoPagerAdapter extends MyFragmentStatePagerAdapter {

    private List<StoryVideo> mStoryVideos;
    private List<Emotion> mEmotions;

    public PlayStoryVideoPagerAdapter(FragmentManager fm, List<StoryVideo> storyVideos, List<Emotion> emotions) {
        super(fm);
        mStoryVideos = storyVideos;
        mEmotions = emotions;
    }

    @Override
    public Fragment getItem(int position) {
        StoryVideoPlayerFragment fragment = new StoryVideoPlayerFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(StoryVideoPlayerFragment.INTENT_VIDEO, mStoryVideos.get(position));
        bundle.putParcelable(StoryVideoPlayerFragment.INTENT_EMOTION, mEmotions.get(position));
        bundle.putInt(StoryVideoPlayerFragment.INTENT_VIDEO_POSITION, position);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public int getCount() {
        return mStoryVideos == null ? 0 : mStoryVideos.size();
    }
}
