package com.bloomlife.videoapp.activity.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.toolbox.MessageRequest;
import com.android.volley.toolbox.Volley;
import com.bloomlife.android.bean.FailureResult;
import com.bloomlife.videoapp.R;
import com.bloomlife.videoapp.adapter.InviteSocialAdapter;
import com.bloomlife.videoapp.common.util.Utils;
import com.bloomlife.videoapp.model.InviteUser;
import com.bloomlife.videoapp.model.User;
import com.bloomlife.videoapp.model.message.GetWeiboFriendsMessage;
import com.bloomlife.videoapp.model.result.GetWeiboFriendResult;
import com.lee.pullrefresh.ui.PullToRefreshBase;
import com.lee.pullrefresh.ui.PullToRefreshStickyHeadersListView;

import net.tsz.afinal.FinalActivity;
import net.tsz.afinal.annotation.view.ViewInject;

import java.util.ArrayList;
import java.util.List;

import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

/**
 * Created by zhengxingtian lan4627@Gmail.com on 2015/8/5.
 */
public class InviteSocialFragment extends Fragment{

    @ViewInject(id = R.id.fragment_invite_social_list)
    private PullToRefreshStickyHeadersListView mPullToRefreshView;

    private StickyListHeadersListView mInviteList;

    private InviteSocialAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_invite_social, container, false);
        FinalActivity.initInjectedView(this, layout);
        initListView();
        loadDataList();
        return layout;
    }

    private void initListView(){
        mPullToRefreshView.setOnRefreshListener(mOnRefreshListener);
        mPullToRefreshView.setPullLoadEnabled(false);
        mPullToRefreshView.setPullRefreshEnabled(false);
        mPullToRefreshView.setScrollLoadEnabled(true);

        mInviteList = mPullToRefreshView.getRefreshableView();
        mAdapter = new InviteSocialAdapter(getActivity(), null);
        mInviteList.setVerticalScrollBarEnabled(false);
        mInviteList.setAreHeadersSticky(false);
        mInviteList.setAdapter(mAdapter);
        mInviteList.setDividerHeight(0);
    }

    private void loadDataList(){
        List<InviteUser> list = mAdapter.getDataList();
        Volley.addToTagQueue(new MessageRequest(
                new GetWeiboFriendsMessage(Utils.isEmpty(list) ? null : list.get(list.size()-1).getTime()),
                mRequestListener));
    }

    private MessageRequest.Listener mRequestListener = new MessageRequest.Listener<GetWeiboFriendResult>() {
        @Override
        public void success(GetWeiboFriendResult result) {
            for (InviteUser u:result.getFriends()){
                u.setType(InviteUser.JOINED);
            }
            for (InviteUser u:result.getUnjoinfriends()){
                u.setType(InviteUser.NOT_JOINED);
            }
            List<InviteUser> dataList = new ArrayList<>(result.getFriends().size() + result.getUnjoinfriends().size());
            dataList.addAll(result.getFriends());
            dataList.addAll(result.getUnjoinfriends());
            if (dataList.size() < 40){
                notMoreData();
            }
            if (mAdapter.getDataList() == null){
                mAdapter.setDataList(dataList);
            } else {
                mAdapter.addDataList(dataList);
            }
            mAdapter.notifyDataSetChanged();
        }

        @Override
        public void failure(FailureResult result) {
            notMoreData();
        }

        @Override
        public void finish() {
            loadCompleted();
        }
    };

    private PullToRefreshBase.OnRefreshListener mOnRefreshListener = new PullToRefreshBase.OnRefreshListener() {
        @Override
        public void onPullDownToRefresh(PullToRefreshBase refreshView) {

        }

        @Override
        public void onPullUpToRefresh(PullToRefreshBase refreshView) {
            loadDataList();
        }
    };

    protected void notMoreData(){
        mPullToRefreshView.setHasMoreData(false);
        mPullToRefreshView.setScrollLoadEnabled(false);
    }

    protected void loadCompleted(){
        mPullToRefreshView.onPullDownRefreshComplete();
        mPullToRefreshView.onPullUpRefreshComplete();
    }

}
