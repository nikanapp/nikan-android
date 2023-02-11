package com.bloomlife.videoapp.activity.fragment;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.android.volley.toolbox.MessageRequest;
import com.android.volley.toolbox.Volley;
import com.bloomlife.android.bean.CacheBean;
import com.bloomlife.android.framework.AbstractAdapter;
import com.bloomlife.videoapp.adapter.FollowerListAdapter;
import com.bloomlife.videoapp.adapter.StatusUserListAdapter;
import com.bloomlife.videoapp.app.Constants;
import com.bloomlife.videoapp.app.RequestErrorAlertListener;
import com.bloomlife.videoapp.dialog.DialogUtils;
import com.bloomlife.videoapp.model.Follower;
import com.bloomlife.videoapp.model.message.FollowerListMessage;
import com.bloomlife.videoapp.model.result.FollowerListResult;

import java.util.List;

/**
 * Created by zhengxingtian lan4627@Gmail.com on 2015/7/28.
 * 关注和被关注的用户列表，在用户详情对话框里会用到
 */
public class FollowerListFragment extends ListFragment {

    public static final String INTENT_USER_ID = "userId";

    private FollowerListAdapter mAdapter;
    private String mCursor;
    private String mUserId;

    @Override
    protected AbstractAdapter getAdapter() {
        mAdapter = new FollowerListAdapter(getActivity(), null);
        mAdapter.setOnStatusChangeListener(mStatusChangeListener);
        return mAdapter;
    }

    @Override
    protected void setListView(ListView listView) {
        mUserId = getArguments().getString(INTENT_USER_ID);
        listView.setOnItemClickListener(mItemClickListener);
        listView.setDividerHeight(0);
    }

    @Override
    protected void loadDataList(){
        Volley.addToTagQueue(new MessageRequest(
                new FollowerListMessage(mUserId, mCursor),
                mGetFollowerListener));
    }

    public void refreshDataList(){
        mCursor = null;
        loadDataList();
    }

    private AdapterView.OnItemClickListener mItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            DialogUtils.showUserInfo(getActivity(), (Follower) parent.getAdapter().getItem(position));
        }
    };

    private MessageRequest.Listener mGetFollowerListener = new RequestErrorAlertListener<FollowerListResult>() {
        @Override
        public void success(FollowerListResult result) {
            if (result.getUsers() == null || result.getUsers().isEmpty()){
                noMoreData();
                return;
            }
            if (mAdapter.getDataList() == null || mCursor == null){
                mAdapter.setDataList(result.getUsers());
            } else {
                mAdapter.addDataList(result.getUsers());
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

    private StatusUserListAdapter.OnStatusChangeListener mStatusChangeListener = new StatusUserListAdapter.OnStatusChangeListener() {

        @Override
        public void onStatusChange(int position, int status) {
            mListener.onFollowerStatusChange(mAdapter.getDataList().get(position).getId(), status);
        }

    };

    private StatusChangeListener mListener;

    public void setStatusChangeListener(StatusChangeListener listener){
        mListener = listener;
    }

    public interface StatusChangeListener {
        void onFollowerStatusChange(String userId, int status);
    }

    public void setUserStatus(String userId, int status){
        // 如果是自己的关注列表，因为关系过于复杂，直接刷新列表。
        if (CacheBean.getInstance().getLoginUserId().equals(mUserId)){
            refreshDataList();
            return;
        }
        // 查看关注列表里是否有我刚才点击关注按钮的用户，有的话同步关注按钮状态
        for (int i=0; i<mAdapter.getDataList().size(); i++){
            Follower f = mAdapter.getDataList().get(i);
            if (f.getId().equals(userId)){
                mAdapter.setStatusList(i, status);
                mAdapter.notifyDataSetChanged();
                return;
            }
        }
    }

}
