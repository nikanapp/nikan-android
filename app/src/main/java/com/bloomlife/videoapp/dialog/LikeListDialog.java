package com.bloomlife.videoapp.dialog;

import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.toolbox.MessageRequest;
import com.android.volley.toolbox.Volley;
import com.bloomlife.android.common.util.UiUtils;
import com.bloomlife.videoapp.R;
import com.bloomlife.videoapp.adapter.LikeListAdapter;
import com.bloomlife.videoapp.app.Constants;
import com.bloomlife.videoapp.common.util.Utils;
import com.bloomlife.videoapp.model.message.GetLikeListMessage;
import com.bloomlife.videoapp.model.result.LikeListResult;
import com.bloomlife.videoapp.view.FooterTipsView;
import com.bloomlife.videoapp.view.GlobalProgressBar;
import com.lee.pullrefresh.ui.PullToRefreshBase;
import com.lee.pullrefresh.ui.PullToRefreshListView;

import net.tsz.afinal.annotation.view.ViewInject;

/**
 * Created by zhengxingtian lan4627@Gmail.com on 2015/7/31.
 */
public class LikeListDialog extends BaseDialog {

    public static final String INTENT_STORY_ID = "storyId";

    public static final int PAGE_SIZE = 20;

    @ViewInject(id=R.id.like_list_btn_close, click=ViewInject.DEFAULT)
    private View mBtnClose;

    @ViewInject(id=R.id.like_list_title)
    private TextView mTitle;

    @ViewInject(id=R.id.like_list_empty)
    private FooterTipsView mEmptyView;

    @ViewInject(id=R.id.like_list_user_list)
    private PullToRefreshListView mPullToRefreshListView;

    @ViewInject(id=R.id.like_list_progressbar)
    private GlobalProgressBar mProgressBar;

    private LikeListAdapter mAdapter;
    private ListView mListView;

    private View mFooterView;

    private String mStoryId;
    private String mCursor;

    @Override
    protected int getLayoutResId() {
        return R.layout.dialog_like_list;
    }

    @Override
    protected void initLayout(View layout) {
        mStoryId = getArguments().getString(INTENT_STORY_ID);

        initFooterView();

        mPullToRefreshListView.setOnRefreshListener(mOnRefreshListener);
        mPullToRefreshListView.setPullLoadEnabled(false);
        mPullToRefreshListView.setPullRefreshEnabled(false);
        mPullToRefreshListView.setScrollLoadEnabled(true);

        mEmptyView.setText(getString(R.string.list_empty));

        mAdapter = new LikeListAdapter(getActivity() ,null);
        mListView = mPullToRefreshListView.getRefreshableView();
        mListView.addFooterView(mFooterView);
        mListView.setAdapter(mAdapter);
        mListView.removeFooterView(mFooterView);
//        mPullToRefreshListView.doPullRefreshing(true, 500);
        mProgressBar.setVisibility(View.VISIBLE);
        loadNewDataList();
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
        Volley.addToTagQueue(new MessageRequest(new GetLikeListMessage(mStoryId, null), mRequestListener));
    }

    private void loadMoreDataList(){
        Volley.addToTagQueue(new MessageRequest(new GetLikeListMessage(mStoryId, mCursor), mRequestListener));
    }

    private MessageRequest.Listener mRequestListener = new MessageRequest.Listener<LikeListResult>(){

        @Override
        public void success(LikeListResult result) {
            if (result.getUsers() == null || result.getUsers().isEmpty())
                return;
            mCursor = result.getPagecursor();
            if (mAdapter.getDataList() == null){
                mAdapter.setDataList(result.getUsers());
            } else {
                mAdapter.addDataList(result.getUsers());
            }
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

    private void initFooterView(){
        LinearLayout layout = new LinearLayout(getActivity());
        layout.setPadding(0, UiUtils.dip2px(getActivity(), 9), 0, UiUtils.dip2px(getActivity(), 9));
        layout.setGravity(Gravity.CENTER);
        layout.addView(new FooterTipsView(getActivity(), getString(R.string.more_empty)));
        mFooterView = layout;
    }

    private void noMoreData(){
        mPullToRefreshListView.setHasMoreData(false);
        mPullToRefreshListView.setScrollLoadEnabled(false);
        if (!Utils.isEmpty(mAdapter.getDataList())){
            mListView.addFooterView(mFooterView, null, false);
        }
    }

    private void loadCompleted(){
        mListView.setEmptyView(mEmptyView);
        mPullToRefreshListView.onPullDownRefreshComplete();
        mPullToRefreshListView.onPullUpRefreshComplete();
        if (mProgressBar.isShown()){
            mProgressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.like_list_btn_close){
            dismiss();
        }
    }
}
