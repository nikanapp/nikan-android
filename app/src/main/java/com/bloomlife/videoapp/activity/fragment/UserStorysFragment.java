package com.bloomlife.videoapp.activity.fragment;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.android.volley.toolbox.MessageRequest;
import com.android.volley.toolbox.Volley;
import com.bloomlife.android.common.util.UiUtils;
import com.bloomlife.android.framework.AbstractAdapter;
import com.bloomlife.videoapp.activity.BaseStoryPlayActivity;
import com.bloomlife.videoapp.activity.UserStoryPlayActivity;
import com.bloomlife.videoapp.adapter.UserStorysAdapter;
import com.bloomlife.videoapp.app.Constants;
import com.bloomlife.videoapp.common.util.Utils;
import com.bloomlife.videoapp.model.ResultStoryInfo;
import com.bloomlife.videoapp.model.UserStory;
import com.bloomlife.videoapp.model.message.GetUserStoryListMessage;
import com.bloomlife.videoapp.model.result.GetUserStoryListResult;

/**
 * Created by zxt lan4627@Gmail.com on 2015/7/31.
 */
public class UserStorysFragment extends ListFragment {

    public static final String INTENT_USER_ID = "userId";

    private UserStorysAdapter mAdapter;

    private String mUserId;
    private String mStoryId;
    private String mCursor;

    @Override
    protected AbstractAdapter getAdapter() {
        mAdapter = new UserStorysAdapter(getActivity(), null);
        return mAdapter;
    }

    @Override
    protected void setListView(ListView listView) {
        mUserId = getArguments().getString(INTENT_USER_ID);
        listView.setDividerHeight(UiUtils.dip2px(getActivity(), 5));
        listView.setOnItemClickListener(mStoryVideoClickListener);
        getFooterView().setPadding(0, UiUtils.dip2px(getActivity(), 5), 0, UiUtils.dip2px(getActivity(), 9));
    }

    private AdapterView.OnItemClickListener mStoryVideoClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            UserStory story = new UserStory();
            story.setVideos(mAdapter.getDataList());
            story.setCommentnum(mAdapter.getComment());
            story.setLikeNum(mAdapter.getLike());
            story.setLooknum(mAdapter.getLook());
            story.setUserId(mUserId);
            story.setStoryid(mStoryId);
            Intent intent = new Intent(getActivity(), UserStoryPlayActivity.class);
            intent.putExtra(UserStoryPlayActivity.INTENT_USER_STORY, story);
            intent.putExtra(UserStoryPlayActivity.INTENT_INDEX, position);
            startActivityForResult(intent, BaseStoryPlayActivity.REQUEST_STORY);
        }
    };

    @Override
    protected void loadDataList() {
        Volley.addToTagQueue(new MessageRequest(new GetUserStoryListMessage(mUserId, mCursor), mGetStoryListener));
    }


    private MessageRequest.Listener mGetStoryListener = new MessageRequest.Listener<GetUserStoryListResult>() {
        @Override
        public void success(GetUserStoryListResult result) {
            if (Utils.isEmpty(result.getVideos())){
                noMoreData();
                return;
            }
            mStoryId = result.getStoryid();
            mAdapter.setStoryInfo(result.getLikenum(), result.getLooknum(), result.getCommentnum());
            if (mAdapter.getDataList() == null || mCursor == null){
                mAdapter.setDataList(result.getVideos());
            } else {
                mAdapter.addDataList(result.getVideos());
            }
            mCursor = result.getPagecursor();
            if (Constants.NO_MORE_PAGE.equals(mCursor)){
                noMoreData();
            }
            mAdapter.notifyDataSetChanged();
        }

        @Override
        public void finish() {
            loadCompleted();
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == BaseStoryPlayActivity.REQUEST_STORY && resultCode == Activity.RESULT_OK){
            ResultStoryInfo storyInfo = data.getParcelableExtra(BaseStoryPlayActivity.RESULT_STORY_INFO);
            mAdapter.setStoryInfo(storyInfo.getLikeNum(), storyInfo.getLookNum(), storyInfo.getCommentNum());
            mAdapter.notifyDataSetChanged();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
