package com.bloomlife.videoapp.activity;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.toolbox.MessageRequest;
import com.android.volley.toolbox.Volley;
import com.bloomlife.android.bean.FailureResult;
import com.bloomlife.android.common.util.UiUtils;
import com.bloomlife.android.framework.BaseActivity;
import com.bloomlife.videoapp.R;
import com.bloomlife.videoapp.adapter.SearchAdapter;
import com.bloomlife.videoapp.app.Constants;
import com.bloomlife.videoapp.app.RequestErrorAlertListener;
import com.bloomlife.videoapp.common.util.UIHelper;
import com.bloomlife.videoapp.common.util.Utils;
import com.bloomlife.videoapp.manager.BackgroundManager;
import com.bloomlife.videoapp.model.SearchMessage;
import com.bloomlife.videoapp.model.User;
import com.bloomlife.videoapp.model.result.SearchResult;
import com.bloomlife.videoapp.view.FooterTipsView;
import com.bloomlife.videoapp.view.SuperToast;
import com.lee.pullrefresh.ui.PullToRefreshBase;
import com.lee.pullrefresh.ui.PullToRefreshListView;

import net.tsz.afinal.annotation.view.ViewInject;

import java.util.List;

/**
 * Created by zhengxingtian lan4627@Gmail.com on 2015/8/6.
 */
public class SearchActivity extends BaseActivity{

    @ViewInject(id = R.id.activity_search_cancel, click = ViewInject.DEFAULT)
    private View mBtnCancel;

    @ViewInject(id = R.id.activity_search_input)
    private EditText mInput;

    @ViewInject(id = R.id.activity_search_icon)
    private View mSearchIcon;

    @ViewInject(id = R.id.activity_search_list, click = ViewInject.DEFAULT)
    private PullToRefreshListView mPullToRefreshListView;

    @ViewInject(id = R.id.activity_search_layout, click = ViewInject.DEFAULT)
    private ViewGroup mLayout;

    @ViewInject(id = R.id.activity_search_progressbar)
    private View mProgressBar;

    @ViewInject(id = R.id.activity_search_list_empty)
    private FooterTipsView mEmptyView;

    private ListView mUserList;
    private View mFooterView;

    private SearchAdapter mAdapter;

    private String mSearchText;

    private String mCursor;

    private boolean isFirst = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        initListView();
        initInput();
    }

    private void initListView(){
        initFooterView();
        mUserList = mPullToRefreshListView.getRefreshableView();
        mPullToRefreshListView.setPullLoadEnabled(false);
        mPullToRefreshListView.setPullRefreshEnabled(false);
        mPullToRefreshListView.setScrollLoadEnabled(true);
        mPullToRefreshListView.setOnRefreshListener(mOnRefreshListener);

        mEmptyView.setText(getString(R.string.list_empty));
        mAdapter = new SearchAdapter(this, null);
        mUserList.addFooterView(mFooterView);
        mUserList.setAdapter(mAdapter);
        mUserList.removeFooterView(mFooterView);
        mUserList.setDividerHeight(0);
//        mUserList.setEmptyView(mEmptyView);
    }

    private void initInput(){
        mInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    UIHelper.hideSoftInput(SearchActivity.this, mInput);
                    search();
                }
                return false;
            }
        });
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus && isFirst){
            isFirst = false;
            AnimatorSet set = new AnimatorSet();
            set.playTogether(
                    ObjectAnimator.ofFloat(mInput, "TranslationX", -mInput.getMeasuredWidth(), 0),
                    ObjectAnimator.ofFloat(mBtnCancel, "TranslationX", mBtnCancel.getMeasuredWidth(), 0)
            );
            set.setDuration(250);
            set.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    mInput.setVisibility(View.VISIBLE);
                    mBtnCancel.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    mSearchIcon.setVisibility(View.VISIBLE);
                    UIHelper.showSoftInput(SearchActivity.this);
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
            set.start();
        }
    }

    private PullToRefreshBase.OnRefreshListener mOnRefreshListener = new PullToRefreshBase.OnRefreshListener() {

        @Override
        public void onPullDownToRefresh(PullToRefreshBase refreshView) {

        }

        @Override
        public void onPullUpToRefresh(PullToRefreshBase refreshView) {
            loadMoreDataList();
        }
    };

    private void searchUser(){
        if (TextUtils.isEmpty(mSearchText)) return;
        mProgressBar.setVisibility(View.VISIBLE);
        mEmptyView.setVisibility(View.GONE);
        Volley.addToTagQueue(new MessageRequest(new SearchMessage(mSearchText, null), mSearchDataRequestListener));
    }

    private MessageRequest.Listener mSearchDataRequestListener = new RequestErrorAlertListener<SearchResult>() {

        @Override
        public void success(SearchResult result) {
            List<User> users = result.getUsers();
            if (Utils.isEmpty(users)){
                notMoreData();
                mEmptyView.setVisibility(View.VISIBLE);
                return;
            }
            mCursor = result.getPagecursor();
            mEmptyView.setVisibility(View.GONE);
            mAdapter.setDataList(users);
            if (Constants.NO_MORE_PAGE.equals(mCursor)){
                notMoreData();
            }
            mAdapter.notifyDataSetChanged();
        }

        @Override
        public void failure(FailureResult result) {
            super.failure(result);
            if (Utils.isEmpty(mAdapter.getDataList())){
                mEmptyView.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void finish() {
            loadCompleted();
        }
    };

    private void loadMoreDataList(){
        Volley.addToTagQueue(new MessageRequest(new SearchMessage(mSearchText, mCursor), mMoreDataRequestListener));
    }

    private MessageRequest.Listener mMoreDataRequestListener = new RequestErrorAlertListener<SearchResult>() {

        @Override
        public void success(SearchResult result) {
            if (Utils.isEmpty(result.getUsers())){
                notMoreData();
                return;
            }
            mCursor = result.getPagecursor();
            mAdapter.addDataList(result.getUsers());
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
        if (!Utils.isEmpty(mAdapter.getDataList())){
            mUserList.addFooterView(mFooterView, null, false);
        }
    }

    protected void loadCompleted(){
        mPullToRefreshListView.onPullDownRefreshComplete();
        mPullToRefreshListView.onPullUpRefreshComplete();
        mProgressBar.setVisibility(View.GONE);
    }

    private void search(){
        mSearchText = mInput.getText().toString().trim();
        if (TextUtils.isEmpty(mSearchText)){
            SuperToast.show(this, R.string.activity_search_empty_tips);
            return;
        }
        mAdapter.setDataList(null);
        mAdapter.notifyDataSetChanged();
        mCursor = null;
        searchUser();
    }

    private void initFooterView(){
        LinearLayout layout = new LinearLayout(this);
        layout.setPadding(0, UiUtils.dip2px(this, 14), 0, UiUtils.dip2px(this, 9));
        layout.setGravity(Gravity.CENTER);
        layout.addView(new FooterTipsView(this, getString(R.string.more_empty)));
        mFooterView = layout;
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.activity_search_cancel:
                finish();
                break;

            case R.id.activity_search_list:
            case R.id.activity_search_layout:
                UIHelper.hideSoftInput(this, mInput);
                break;

        }
    }

    @Override
    public void finish(){
        super.finish();
        BackgroundManager.getInstance().ReleaseMainBitmap(getApplicationContext());
        overridePendingTransition(0, R.anim.activity_search_alpha_out);
    }

}
