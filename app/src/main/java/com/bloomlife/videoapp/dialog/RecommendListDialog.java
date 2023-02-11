package com.bloomlife.videoapp.dialog;

import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.toolbox.MessageRequest;
import com.android.volley.toolbox.Volley;
import com.bloomlife.videoapp.R;
import com.bloomlife.videoapp.adapter.DgRecommendListAdapter;
import com.bloomlife.videoapp.model.Recommend;
import com.bloomlife.videoapp.model.message.RecommendMessage;
import com.bloomlife.videoapp.model.message.SendAttentionsMessage;
import com.bloomlife.videoapp.model.result.RecommendResult;
import com.bloomlife.videoapp.view.FooterTipsView;
import com.bloomlife.videoapp.view.SuperToast;

import net.tsz.afinal.annotation.view.ViewInject;

import java.util.ArrayList;

/**
 * Created by zhengxingtian lan4627@Gmail.com on 2015/7/28.
 * 推荐关注列表对话框
 */
public class RecommendListDialog extends BaseDialog {

    @ViewInject(id=R.id.recommend_list_btn_close, click=ViewInject.DEFAULT)
    private ImageView mBtnClose;

    @ViewInject(id=R.id.recommend_list_user_list)
    private ListView mUserList;

    @ViewInject(id=R.id.recommend_list_empty)
    private FooterTipsView mEmptyView;

    @ViewInject(id=R.id.recommend_list_title)
    private TextView mTitle;

    @ViewInject(id=R.id.recommend_list_btn_accept, click=ViewInject.DEFAULT)
    private View mBtnAccept;

    private DgRecommendListAdapter mAdapter;

    @Override
    protected int getLayoutResId() {
        return R.layout.dialog_user_list;
    }

    @Override
    protected void initLayout(View layout) {
        Volley.addToTagQueue(new MessageRequest(new RecommendMessage(), mListener));
    }

    protected void setListView(DgRecommendListAdapter adapter){
        mEmptyView.setText(getString(R.string.list_empty));
        mAdapter = adapter;
        mUserList.setAdapter(adapter);
        mUserList.setVerticalScrollBarEnabled(false);
        mUserList.setEmptyView(mEmptyView);
    }

    private MessageRequest.Listener<RecommendResult> mListener = new MessageRequest.Listener<RecommendResult>(){

        @Override
        public void success(RecommendResult result) {
            setListView(new DgRecommendListAdapter(getActivity(), result.getRecommenders()));
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.recommend_list_btn_close:
                dismiss();
                break;

            case R.id.recommend_list_btn_accept:
                if (mAdapter.isNotSelect()){
                    SuperToast.show(getActivity(), "至少关注一个用户");
                } else {
                    Volley.addToTagQueue(new MessageRequest(new SendAttentionsMessage(mAdapter.getAttentions())));
                    dismiss();
                }
                break;
        }
    }
}
