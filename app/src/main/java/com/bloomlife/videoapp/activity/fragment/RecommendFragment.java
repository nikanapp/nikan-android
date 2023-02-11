package com.bloomlife.videoapp.activity.fragment;

import android.widget.BaseAdapter;
import android.widget.ListView;

import com.android.volley.toolbox.MessageRequest;
import com.android.volley.toolbox.Volley;
import com.bloomlife.android.framework.AbstractAdapter;
import com.bloomlife.videoapp.adapter.FgRecommendListAdapter;
import com.bloomlife.videoapp.model.message.RecommendMessage;
import com.bloomlife.videoapp.model.result.RecommendResult;

/**
 * Created by zhengxingtian lan4627@Gmail.com on 2015/8/6.
 */
public class RecommendFragment extends ListFragment {

    private FgRecommendListAdapter mAdapter;

    @Override
    protected AbstractAdapter getAdapter() {
        mAdapter = new FgRecommendListAdapter(getActivity(), null);
        return mAdapter;
    }

    @Override
    protected void setListView(ListView listView) {
        listView.setDividerHeight(0);
    }

    @Override
    protected void loadDataList() {
        Volley.addToTagQueue(new MessageRequest(new RecommendMessage(), mRequestListener));
    }

    private MessageRequest.Listener mRequestListener = new MessageRequest.Listener<RecommendResult>() {
        @Override
        public void success(RecommendResult result) {
            mAdapter.setDataList(result.getRecommenders());
            mAdapter.notifyDataSetChanged();
            noMoreData();
        }

        @Override
        public void finish() {
            loadCompleted();
        }
    };
}
