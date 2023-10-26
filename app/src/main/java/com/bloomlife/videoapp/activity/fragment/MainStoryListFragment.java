package com.bloomlife.videoapp.activity.fragment;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import androidx.fragment.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.MessageRequest;
import com.android.volley.toolbox.Volley;
import com.bloomlife.android.bean.CacheBean;
import com.bloomlife.android.common.util.UiUtils;
import com.bloomlife.videoapp.R;
import com.bloomlife.videoapp.activity.MainStoryPlayActivity;
import com.bloomlife.videoapp.activity.BaseStoryPlayActivity;
import com.bloomlife.videoapp.activity.MyVideoActivity;
import com.bloomlife.videoapp.adapter.MainStoryListAdapter;
import com.bloomlife.videoapp.app.Constants;
import com.bloomlife.videoapp.app.DbHelper;
import com.bloomlife.videoapp.app.RequestErrorAlertListener;
import com.bloomlife.videoapp.common.CacheKeyConstants;
import com.bloomlife.videoapp.common.util.Utils;
import com.bloomlife.videoapp.dialog.DialogUtils;
import com.bloomlife.videoapp.manager.BackgroundManager;
import com.bloomlife.videoapp.model.CacheHostStorys;
import com.bloomlife.videoapp.model.DbStoryVideo;
import com.bloomlife.videoapp.model.ResultStoryInfo;
import com.bloomlife.videoapp.model.Story;
import com.bloomlife.videoapp.model.VideoProgress;
import com.bloomlife.videoapp.model.message.GetStoryListMessage;
import com.bloomlife.videoapp.model.result.GetStoryListResult;
import com.bloomlife.videoapp.view.FooterTipsView;
import com.lee.pullrefresh.ui.PullToRefreshBase;
import com.lee.pullrefresh.ui.PullToRefreshListView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;

import net.tsz.afinal.FinalActivity;
import net.tsz.afinal.annotation.view.ViewInject;

import java.util.List;

/**
 * Created by zhengxingtian lan4627@Gmail.com on 2015/7/31.
 */
public class MainStoryListFragment extends Fragment implements View.OnClickListener{

    @ViewInject(id=R.id.fragment_main_story_listview)
    private PullToRefreshListView mPullToRefreshListView;

    @ViewInject(id=R.id.fragment_main_story_touch_layer)
    private View mTouchLayer;

    @ViewInject(id=R.id.fragment_main_story_empty)
    private FooterTipsView mEmptyView;

    @ViewInject(id=R.id.fragment_main_story_upload_fail, click=ViewInject.DEFAULT)
    private TextView mUploadFailView;

    @ViewInject(id=R.id.fragment_main_story_uploading)
    private TextView mUploadingView;

    @ViewInject(id=R.id.fragment_main_story_upload_succ)
    private ImageView mUploadSuccView;

    @ViewInject(id=R.id.fragment_main_story_reload, click=ViewInject.DEFAULT)
    private View mBtnReload;

    private ListView mListView;
    private MainStoryListAdapter mAdapter;

    private String mCursor;

    private View.OnTouchListener mLayoutTouchListener;

    private int mItemClickPosition;
    private boolean mAddFooter;
    private boolean mIsVisibleToUser;
    private View mFooterView;
    private View mItemClickView;
    private UploadProgressReceiver mUploadProgressReceiver;
    private UserLoginReceiver mUserLoginReceiver;

    private Handler mHandler = new Handler();

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mLayoutTouchListener = (View.OnTouchListener) activity;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_main_storylist, container, false);
        FinalActivity.initInjectedView(this, v);
        initLayout();
        initReceiver();
        return v;
    }

    private void initLayout(){
        initFooterView();
        mPullToRefreshListView.setPullLoadEnabled(false);
        mPullToRefreshListView.setPullRefreshEnabled(true);
        mPullToRefreshListView.setScrollLoadEnabled(true);
        mPullToRefreshListView.setOnRefreshListener(mOnRefreshListener);
        mPullToRefreshListView.setOnScrollListener(new PauseOnScrollListener(ImageLoader.getInstance(), false, true));

        // 先取缓存的数据
        mAdapter = new MainStoryListAdapter(getActivity(), CacheHostStorys.get(getActivity()));
        mListView = mPullToRefreshListView.getRefreshableView();
        mListView.setVerticalScrollBarEnabled(false);
        mListView.addFooterView(mFooterView);
        mListView.setAdapter(mAdapter);
        mListView.removeFooterView(mFooterView);
        mListView.setDividerHeight(UiUtils.dip2px(getActivity(), 7));
        mListView.setOnItemClickListener(mOnItemClickListener);
        mPullToRefreshListView.doPullRefreshing(true, 500);
//        loadNewDataList();
        mTouchLayer.setOnTouchListener(mLayoutTouchListener);
        mEmptyView.setText(getString(R.string.list_empty));
    }

    private void initFooterView(){
        LinearLayout layout = new LinearLayout(getActivity());
        layout.setPadding(0, UiUtils.dip2px(getActivity(), 7), 0, UiUtils.dip2px(getActivity(), 9));
        layout.setGravity(Gravity.CENTER);
        layout.addView(new FooterTipsView(getActivity(), getString(R.string.more_empty)));
        mFooterView = layout;
    }

    private PullToRefreshBase.OnRefreshListener mOnRefreshListener = new PullToRefreshBase.OnRefreshListener() {
        @Override
        public void onPullDownToRefresh(PullToRefreshBase refreshView) {
            loadNewDataList();
        }

        @Override
        public void onPullUpToRefresh(PullToRefreshBase refreshView) {
            loadMoreDataList();
        }
    };

    private void loadNewDataList(){
        Volley.addToTagQueue(new MessageRequest(new GetStoryListMessage(null), mGetNewStoryListener));
    }

    private MessageRequest.Listener mGetNewStoryListener = new RequestErrorAlertListener<GetStoryListResult>() {

        @Override
        public void success(GetStoryListResult result) {
            if (getActivity() == null)
                return;
            if (mBtnReload.isShown())
                mBtnReload.setVisibility(View.GONE);
            if (Utils.isEmpty(result.getHoststorys())){
                notMoreData();
                return;
            }
            mCursor = result.getPagecursor();
            mAdapter.setDataList(result.getHoststorys());
            if (Constants.NO_MORE_PAGE.equals(mCursor)){
                notMoreData();
            }
            mAdapter.notifyDataSetChanged();
            // 缓存最新的数据
            CacheHostStorys.set(getActivity(), result.getHoststorys());
        }

        @Override
        public void finish() {
            loadCompleted();
        }

        @Override
        public void error(VolleyError error) {
            super.error(error);
            if (Utils.isEmpty(mAdapter.getDataList())){
                mBtnReload.setVisibility(View.VISIBLE);
            }
        }
    };

    private void loadMoreDataList(){
        Volley.addToTagQueue(new MessageRequest(new GetStoryListMessage(mCursor), mGetMoreStoryListener));
    }

    private MessageRequest.Listener mGetMoreStoryListener = new RequestErrorAlertListener<GetStoryListResult>() {
        @Override
        public void success(GetStoryListResult result) {
            if (mBtnReload.isShown())
                mBtnReload.setVisibility(View.GONE);
            if (Utils.isEmpty(result.getHoststorys())){
                notMoreData();
                return;
            }
            mAdapter.addDataList(result.getHoststorys());
            if (Constants.NO_MORE_PAGE.equals(mCursor)){
                notMoreData();
            }
            mAdapter.notifyDataSetChanged();
        }

        @Override
        public void finish() {
            loadCompleted();
        }
    };

    protected void notMoreData(){
        mPullToRefreshListView.setHasMoreData(false);
        mPullToRefreshListView.setScrollLoadEnabled(false);
        if (!Utils.isEmpty(mAdapter.getDataList()) && !mAddFooter){
            mAddFooter = true;
            mListView.addFooterView(mFooterView, null, false);
        }
    }

    protected void loadCompleted(){
        mPullToRefreshListView.onPullDownRefreshComplete();
        mPullToRefreshListView.onPullUpRefreshComplete();
    }

    private AdapterView.OnItemClickListener mOnItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            mItemClickPosition = position;
            mItemClickView = view;
            Story story = (Story) mAdapter.getItem(position);
            Intent intent = new Intent(getActivity(), MainStoryPlayActivity.class);
            intent.putExtra(MainStoryPlayActivity.INTENT_STORY, story);
            startActivityForResult(intent, BaseStoryPlayActivity.REQUEST_STORY);
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        mIsVisibleToUser = true;
        mUploadFailView.setEnabled(true);
        checkedUploadFailStoryVideo();
        if (Utils.isLogin(getActivity())){
            showRecommendList();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mIsVisibleToUser = false;
    }

    @Override
    public void onDestroyView() {
        removeReceiver();
        super.onDestroyView();
    }

    private void initReceiver(){
        mUserLoginReceiver = new UserLoginReceiver();
        mUploadProgressReceiver = new UploadProgressReceiver();
        getActivity().registerReceiver(mUserLoginReceiver, new IntentFilter(Constants.ACTION_USER_LOGIN));
        getActivity().registerReceiver(mUploadProgressReceiver, new IntentFilter(Constants.ACTION_UPLOAD_PROGRESS));
    }

    private void removeReceiver(){
        getActivity().unregisterReceiver(mUserLoginReceiver);
        getActivity().unregisterReceiver(mUploadProgressReceiver);
    }

    private void checkedUploadFailStoryVideo(){
        DbHelper.queryUploadFailStoryVideo(getActivity(), CacheBean.getInstance().getLoginUserId(), mQueryCallback);
    }

    private DbHelper.QueryCallback<DbStoryVideo> mQueryCallback = new DbHelper.QueryCallback<DbStoryVideo>() {
        @Override
        public void setResult(List<DbStoryVideo> result) {
            setUploadView(!Utils.isEmpty(result));
        }

        private void setUploadView(final boolean hasFial){
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (!mUploadingView.isShown() && !mUploadSuccView.isShown()) {
                        mUploadFailView.setVisibility(hasFial ? View.VISIBLE : View.GONE);
                    }
                }
            });
        }
    };

    private void showRecommendList(){
        if (Utils.isShowDialog(getActivity(), CacheKeyConstants.KEY_FIRST_LOGIN)){
            Utils.setShowDialog(getActivity(), CacheKeyConstants.KEY_FIRST_LOGIN, false);
            DialogUtils.showRecommendList(getActivity());
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.fragment_main_story_upload_fail:
                mUploadFailView.setEnabled(false);
                startActivity(new Intent(getActivity(), MyVideoActivity.class));
                getActivity().overridePendingTransition(R.anim.activity_bottom_in, 0);
                BackgroundManager.getInstance().capture(getActivity());
                break;

            case R.id.fragment_main_story_reload:
                mPullToRefreshListView.doPullRefreshing(true, 500);
                break;
        }

    }

    class UploadProgressReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            VideoProgress videoProgress = (VideoProgress) intent.getSerializableExtra(Constants.INTENT_UPLOAD_PROGRESS);
            if (videoProgress.getType() == VideoProgress.STORY_VIDEO){
                setUploadStatus(videoProgress.getProgress());
            }
        }

        private void setUploadStatus(double progress){
            if (progress == Constants.PROGRESS_FAILURE){
                mUploadFailView.setVisibility(View.VISIBLE);
                mUploadingView.setVisibility(View.GONE);
            } else if (progress == Constants.PROGRESS_SUCCESSS){
                mUploadingView.setVisibility(View.GONE);
                loadNewDataList();
                Animator animator = ObjectAnimator.ofFloat(mUploadSuccView, "alpha", 1, 0);
                animator.addListener(mUploadSuccessAnimListener);
                animator.setDuration(2000).start();
            } else if (progress < 100){
                if (mUploadFailView.isShown()){
                    mUploadFailView.setVisibility(View.GONE);
                }
                if (!mUploadingView.isShown()){
                    mUploadingView.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    class UserLoginReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            loadNewDataList();
            if (mIsVisibleToUser){
                showRecommendList();
            }
        }
    }

    private Animator.AnimatorListener mUploadSuccessAnimListener = new Animator.AnimatorListener() {
        @Override
        public void onAnimationStart(Animator animation) {
            mUploadSuccView.setVisibility(View.VISIBLE);
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            mUploadSuccView.setVisibility(View.GONE);
            checkedUploadFailStoryVideo();
        }

        @Override
        public void onAnimationCancel(Animator animation) {

        }

        @Override
        public void onAnimationRepeat(Animator animation) {

        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == BaseStoryPlayActivity.REQUEST_STORY && resultCode == Activity.RESULT_OK){
            ResultStoryInfo storyInfo = data.getParcelableExtra(BaseStoryPlayActivity.RESULT_STORY_INFO);
            Story story = mAdapter.getDataList().get(mItemClickPosition);
            story.setCommentnum(storyInfo.getCommentNum());
            story.setLooknum(storyInfo.getLookNum());
            story.setLikenum(storyInfo.getLikeNum());
            mAdapter.setStoryInfo(mItemClickView, story);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
    }
}
