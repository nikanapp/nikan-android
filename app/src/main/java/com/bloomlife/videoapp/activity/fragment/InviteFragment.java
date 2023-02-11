package com.bloomlife.videoapp.activity.fragment;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.android.volley.toolbox.MessageRequest;
import com.android.volley.toolbox.Volley;
import com.bloomlife.android.framework.AbstractAdapter;
import com.bloomlife.videoapp.adapter.InviteAdapter;
import com.bloomlife.videoapp.app.RequestErrorAlertListener;
import com.bloomlife.videoapp.dialog.DialogUtils;
import com.bloomlife.videoapp.model.Fans;
import com.bloomlife.videoapp.model.User;
import com.bloomlife.videoapp.model.message.GetFriendListMessage;
import com.bloomlife.videoapp.model.result.GetFriendListResult;

/**
 * Created by zhengxingtian lan4627@Gmail.com on 2015/9/21.
 */
public class InviteFragment extends ListFragment {

    private InviteAdapter mAdapter;

    @Override
    protected AbstractAdapter<?> getAdapter() {
        mAdapter = new InviteAdapter(getActivity(), null);
        return mAdapter;
    }

    @Override
    protected void setListView(ListView listView) {
        listView.setOnItemClickListener(mItemClickListener);
        listView.setDividerHeight(0);
    }

    @Override
    protected void loadDataList() {
        Volley.addToTagQueue(new MessageRequest(new GetFriendListMessage(), mListener));
    }

    private RequestErrorAlertListener<GetFriendListResult> mListener = new RequestErrorAlertListener<GetFriendListResult>() {
        @Override
        public void success(GetFriendListResult result) {
            mAdapter.setDataList(result.getRecommends());
            mAdapter.notifyDataSetChanged();
            noMoreData();
        }

        @Override
        public void finish() {
            loadCompleted();
        }
    };

    private AdapterView.OnItemClickListener mItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            DialogUtils.showUserInfo(getActivity(), (User) parent.getAdapter().getItem(position));
        }
    };
}
