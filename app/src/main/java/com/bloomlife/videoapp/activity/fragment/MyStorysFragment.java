package com.bloomlife.videoapp.activity.fragment;

import android.animation.ObjectAnimator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;

import com.android.volley.toolbox.MessageRequest;
import com.android.volley.toolbox.Volley;
import com.bloomlife.android.bean.CacheBean;
import com.bloomlife.android.common.util.UiUtils;
import com.bloomlife.android.view.AlterDialog;
import com.bloomlife.videoapp.R;
import com.bloomlife.videoapp.activity.BaseStoryPlayActivity;
import com.bloomlife.videoapp.activity.MyVideoActivity;
import com.bloomlife.videoapp.activity.UserStoryPlayActivity;
import com.bloomlife.videoapp.adapter.BaseAdapter;
import com.bloomlife.videoapp.adapter.MyStorysAdapter;
import com.bloomlife.videoapp.app.Constants;
import com.bloomlife.videoapp.app.DbHelper;
import com.bloomlife.videoapp.app.RequestErrorAlertListener;
import com.bloomlife.videoapp.common.CommentText;
import com.bloomlife.videoapp.common.util.Utils;
import com.bloomlife.videoapp.model.Comment;
import com.bloomlife.videoapp.model.DbStoryVideo;
import com.bloomlife.videoapp.model.ResultStoryInfo;
import com.bloomlife.videoapp.model.StoryVideo;
import com.bloomlife.videoapp.model.VideoProgress;
import com.bloomlife.videoapp.model.message.GetStoryMessage;
import com.bloomlife.videoapp.model.message.GetUserStoryListMessage;
import com.bloomlife.videoapp.model.result.GetStoryResult;
import com.bloomlife.videoapp.model.result.GetUserStoryListResult;
import com.lee.pullrefresh.ui.PullToRefreshListView;

import net.tsz.afinal.annotation.view.ViewInject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by zxt lan4627@Gmail.com on 2015/8/4.
 */
public class MyStorysFragment extends ListFragment implements View.OnClickListener{

    @ViewInject(id=R.id.fragment_my_story_delete, click=ViewInject.DEFAULT)
    private View mBtnDelete;

    private Handler mHandler = new Handler();
    private UploadProgressReceiver mReceiver;
    private MyStorysAdapter mAdapter;
    private String mCursor;
    private ListView mListView;

    private List<Comment> mStoryCommentList;
    private List<CommentText> mStoryCommentTextList;
    private String mSelectComment;
    private boolean mLike;

    private int mLikeNum;
    private int mLookNum;

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_my_storys;
    }

    @Override
    protected BaseAdapter getAdapter() {
        mAdapter = new MyStorysAdapter(getActivity(), null);
        return mAdapter;
    }

    @Override
    protected void setListView(ListView listView) {
        initLayout();
        mListView = listView;
        mListView.setDivider(new ColorDrawable(Color.TRANSPARENT));
        mListView.setDividerHeight(UiUtils.dip2px(getActivity(), 7));
        mListView.setPadding(0, UiUtils.dip2px(getActivity(), 22), 0, 0);
        mListView.setSelector(new ColorDrawable(Color.TRANSPARENT));
        mAdapter.setListView(listView);
    }

    @Override
    protected void firstLoadData(PullToRefreshListView view) {
        loadDataList();
    }

    private void initLayout(){
        mReceiver = new UploadProgressReceiver();
        getActivity().registerReceiver(mReceiver, new IntentFilter(Constants.ACTION_UPLOAD_PROGRESS));
        DbHelper.readStoryVideoList(getActivity(), CacheBean.getInstance().getLoginUserId(), mCallback);

        getPullToRefreshListView().setOnScrollListener(mListScrollListener);
    }

    private DbHelper.QueryCallback<DbStoryVideo> mCallback = new DbHelper.QueryCallback<DbStoryVideo>() {
        @Override
        public void setResult(final List<DbStoryVideo> result) {
            sort(result);
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    mAdapter.setDataList(result);
                    mAdapter.notifyDataSetChanged();
                }
            });
        }

        private void sort(List<DbStoryVideo> result){
            Collections.sort(result, new Comparator<DbStoryVideo>() {
                @Override
                public int compare(DbStoryVideo lhs, DbStoryVideo rhs) {
                    if (lhs.getStatus() != DbStoryVideo.STATUS_UPLOAD_SUCCESS){
                        return -1;
                    } else {
                        return 1;
                    }
                }
            });
        }
    };

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mReceiver != null)
            getActivity().unregisterReceiver(mReceiver);
    }

    @Override
    protected void loadDataList() {
        GetUserStoryListMessage message = new GetUserStoryListMessage(
                CacheBean.getInstance().getLoginUserId(),
                mCursor
        );
        Volley.addToTagQueue(new MessageRequest(message, mGetStoryListener));
    }

    private MessageRequest.Listener mGetStoryListener = new MessageRequest.Listener<GetUserStoryListResult>() {
        @Override
        public void success(GetUserStoryListResult result) {
            if (Utils.isEmpty(result.getVideos())){
                noMoreData();
                return;
            }
            mCursor = result.getPagecursor();
            mLikeNum = result.getLikenum();
            mLookNum = result.getLooknum();
            mAdapter.setDataList(mAdapter.syncStoryVideoList(result.getVideos(), result.getStoryid()));
            if (Constants.NO_MORE_PAGE.equals(mCursor)){
                noMoreData();
            }
            mAdapter.notifyDataSetChanged();
            Volley.addToTagQueue(new MessageRequest(new GetStoryMessage(result.getStoryid()), mGetStoryInfoListener));
        }

        @Override
        public void finish() {
            loadCompleted();
        }
    };

    private MessageRequest.Listener<GetStoryResult> mGetStoryInfoListener = new RequestErrorAlertListener<GetStoryResult>() {
        @Override
        public void success(GetStoryResult result) {
            mStoryCommentList = result.getTagsComments();
            mStoryCommentTextList = result.getTextComents();
            mSelectComment = result.getCommentagid();
            mLike = result.islike();
            mAdapter.setStoryInfo(mLikeNum, mLookNum, Utils.commentCount(mStoryCommentList, mStoryCommentTextList), mLike);
            mAdapter.setStoryComment(new ArrayList<>(mStoryCommentList), new ArrayList<>(mStoryCommentTextList), mSelectComment);
            mAdapter.notifyDataSetChanged();
        }
    };

    @Override
    protected void noMoreData(){
        super.noMoreData();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.fragment_my_story_delete:
                if (mAdapter.getSelectVideoCount() < 1){
                    break;
                } else {
                    alertDeleteDialog();
                }
                break;
        }
    }

    private void alertDeleteDialog(){
        AlterDialog.showDialog(getActivity(), getString(R.string.activity_my_video_delete_tips), new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mAdapter.deleteStoryVideo();
                ((MyVideoActivity) getActivity()).setEditBtnStatus(false);
            }
        });
    }

    class UploadProgressReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            VideoProgress videoProgress = (VideoProgress) intent.getSerializableExtra(Constants.INTENT_UPLOAD_PROGRESS);
            if (mAdapter != null && videoProgress.getType() == VideoProgress.STORY_VIDEO)
                mAdapter.refreshProgress(videoProgress);
        }
    }

    private AbsListView.OnScrollListener mListScrollListener = new AbsListView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            if (scrollState != AbsListView.OnScrollListener.SCROLL_STATE_IDLE){
                mAdapter.setSelectVideoAnimator(false);
            }
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

        }
    };

    public void setEditStatus(boolean isEdit){
        if (isEdit && Utils.isEmpty(mAdapter.getDataList()))
            return;
        mAdapter.setEditable(isEdit);
        if (isEdit){
            mAdapter.setSelectVideoAnimator(true);
            mBtnDelete.setVisibility(View.VISIBLE);
            ObjectAnimator.ofFloat(mBtnDelete, "Alpha", 0, 1).setDuration(300).start();
        } else {
            mBtnDelete.setVisibility(View.INVISIBLE);
            ObjectAnimator.ofFloat(mBtnDelete, "Alpha", 1, 0).setDuration(300).start();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == UserStoryPlayActivity.REQUEST_STORY){
            ResultStoryInfo storyInfo = data.getParcelableExtra(BaseStoryPlayActivity.RESULT_STORY_INFO);
            if (storyInfo.getLikeNum() != mLikeNum){
                mLike = !mLike;
            }
            mAdapter.setStoryInfo(storyInfo.getLikeNum(), storyInfo.getLookNum(), storyInfo.getCommentNum(), mLike);
            mAdapter.notifyDataSetChanged();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
