package com.bloomlife.videoapp.activity.fragment;

import androidx.fragment.app.Fragment;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.bloomlife.android.common.util.UiUtils;
import com.bloomlife.android.framework.AbstractAdapter;
import com.bloomlife.videoapp.R;
import com.bloomlife.videoapp.common.util.Utils;
import com.bloomlife.videoapp.view.FooterTipsView;
import com.bloomlife.videoapp.view.GlobalProgressBar;
import com.lee.pullrefresh.ui.PullToRefreshBase;
import com.lee.pullrefresh.ui.PullToRefreshListView;

import net.tsz.afinal.FinalActivity;

/**
 * Created by zxt lan4627@Gmail.com on 2015/7/31.
 * 拥有一个到底部自动加载列表的Fragment
 */
public abstract class ListFragment extends Fragment {

    public static final int PAGE_SIZE = 20;

    private PullToRefreshListView mPullToRefreshListView;

    private boolean isFirst;

    private ListView mListView;
    private View mFooterView;
    private FooterTipsView mEmptyView;
    private GlobalProgressBar mProgressBar;
    private AbstractAdapter<?> mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(getLayoutResId(), container, false);
        FinalActivity.initInjectedView(ListFragment.this, v);
        mPullToRefreshListView = (PullToRefreshListView) v.findViewById(R.id.list_listview);
        mEmptyView = (FooterTipsView) v.findViewById(R.id.list_empty);
        mEmptyView.setText(getString(R.string.list_empty));
        mProgressBar = (GlobalProgressBar) v.findViewById(R.id.list_progressbar);
        initView(v);
        firstLoadData(mPullToRefreshListView);
        return v;
    }

    protected int getLayoutResId(){
        return R.layout.fragment_scroll_end_load_list;
    }

    protected void initView(View view){
        initFooterView();
        mPullToRefreshListView.setOnRefreshListener(mOnRefreshListener);
        mPullToRefreshListView.setPullLoadEnabled(false);
        mPullToRefreshListView.setPullRefreshEnabled(false);
        mPullToRefreshListView.setScrollLoadEnabled(true);

        mListView = mPullToRefreshListView.getRefreshableView();
        mListView.setVerticalScrollBarEnabled(false);
//        mListView.setEmptyView(mEmptyView);
        mListView.addFooterView(mFooterView, null, false);
        mAdapter = getAdapter();
        mListView.setAdapter(mAdapter);
        mListView.removeFooterView(mFooterView);
        setListView(mListView);
    }

    protected View getFooterView(){
        return mFooterView;
    }

    protected void firstLoadData(PullToRefreshListView view){
//        view.doPullRefreshing(true, 500);
        mProgressBar.setVisibility(View.VISIBLE);
        loadDataList();
    }

    protected PullToRefreshListView getPullToRefreshListView(){
        return mPullToRefreshListView;
    }

    protected void setEmptyText(CharSequence text){
        mEmptyView.setText(text);
    }

    private void initFooterView(){
        LinearLayout layout = new LinearLayout(getActivity());
        layout.setPadding(0, UiUtils.dip2px(getActivity(), 9), 0, UiUtils.dip2px(getActivity(), 9));
        layout.setGravity(Gravity.CENTER);
        layout.addView(new FooterTipsView(getActivity(), getString(R.string.more_empty)));
        mFooterView = layout;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (!isFirst && isVisibleToUser){
            isFirst = true;
        }
    }

    abstract protected AbstractAdapter<?> getAdapter();

    abstract protected void setListView(ListView listView);

    abstract protected void loadDataList();

    private PullToRefreshBase.OnRefreshListener mOnRefreshListener = new PullToRefreshBase.OnRefreshListener() {
        @Override
        public void onPullDownToRefresh(PullToRefreshBase refreshView) {
            loadDataList();
        }

        @Override
        public void onPullUpToRefresh(PullToRefreshBase refreshView) {
            loadDataList();
        }
    };

    protected void noMoreData(){
        mPullToRefreshListView.setHasMoreData(false);
        mPullToRefreshListView.setScrollLoadEnabled(false);
        if (!Utils.isEmpty(mAdapter.getDataList())){
            mListView.addFooterView(mFooterView, null, false);
        }
    }

    protected void loadCompleted(){
        mListView.setEmptyView(mEmptyView);
        mPullToRefreshListView.onPullDownRefreshComplete();
        mPullToRefreshListView.onPullUpRefreshComplete();
        mProgressBar.setVisibility(View.INVISIBLE);
    }
}
