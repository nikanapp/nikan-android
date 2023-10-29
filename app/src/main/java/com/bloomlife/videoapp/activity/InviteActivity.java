package com.bloomlife.videoapp.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.android.volley.toolbox.MessageRequest;
import com.android.volley.toolbox.Volley;
import com.bloomlife.android.common.util.Utils;
import com.bloomlife.android.framework.BaseActivity;
import com.bloomlife.videoapp.R;
import com.bloomlife.videoapp.activity.fragment.InviteFragment;
import com.bloomlife.videoapp.adapter.InviteAdapter;
import com.bloomlife.videoapp.app.RequestErrorAlertListener;
import com.bloomlife.videoapp.common.util.ShareSDKUtil;
import com.bloomlife.videoapp.manager.BackgroundManager;
import com.bloomlife.videoapp.model.message.GetFriendListMessage;
import com.bloomlife.videoapp.model.message.RecommendMessage;
import com.bloomlife.videoapp.model.result.GetFriendListResult;
import com.bloomlife.videoapp.model.result.RecommendResult;
import com.bloomlife.videoapp.view.FooterTipsView;
import com.lee.pullrefresh.ui.PullToRefreshBase;
import com.lee.pullrefresh.ui.PullToRefreshListView;

import net.tsz.afinal.annotation.view.ViewInject;

/**
 * Created by zxt lan4627@Gmail.com on 2015/8/5.
 */
public class InviteActivity extends BaseActivity{

    @ViewInject(id = R.id.activity_invite_btn_close, click = ViewInject.DEFAULT)
    private View mBtnClose;

    @ViewInject(id = R.id.activity_invite_wechat, click = ViewInject.DEFAULT)
    private View mBtnWechat;

    @ViewInject(id = R.id.activity_invite_weibo, click = ViewInject.DEFAULT)
    private View mBtnWeibo;

    @ViewInject(id = R.id.activity_invite_qq, click = ViewInject.DEFAULT)
    private View mBtnQQ;

    @ViewInject(id = R.id.activity_invite_facebook, click = ViewInject.DEFAULT)
    private View mBtnFacebook;

    @ViewInject(id = R.id.activity_invite_twitter, click = ViewInject.DEFAULT)
    private View mBtnTwitter;

    @ViewInject(id = R.id.activity_invite_contacts, click = ViewInject.DEFAULT)
    private View mBtnContacts;

    @ViewInject(id = R.id.activity_invite_wechat_divier, click = ViewInject.DEFAULT)
    private View mBtnWechatDivider;

    @ViewInject(id = R.id.activity_invite_list_layout, click = ViewInject.DEFAULT)
    private FrameLayout mListLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite);
        initButtons();
        initListView();
    }

    private void initButtons(){
        if (Utils.isZH()){
            mBtnTwitter.setVisibility(View.GONE);
            mBtnFacebook.setVisibility(View.GONE);
        } else {
            mBtnQQ.setVisibility(View.GONE);
            mBtnWeibo.setVisibility(View.GONE);
            mBtnWechat.setVisibility(View.GONE);
            mBtnWechatDivider.setVisibility(View.GONE);
        }
    }

    private void initListView(){
        getSupportFragmentManager().beginTransaction().add(R.id.activity_invite_list_layout, new InviteFragment()).commitAllowingStateLoss();
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.activity_invite_btn_close:
                finish();
                break;

            case R.id.activity_invite_wechat:
                ShareSDKUtil.shareAppToWechat(this);
                break;

            case R.id.activity_invite_weibo:
                ShareSDKUtil.shareAppToWeibo(this);
                break;

            case R.id.activity_invite_qq:
                ShareSDKUtil.shareAppToQQ(this);
                break;

            case R.id.activity_invite_contacts:
                ShareSDKUtil.shareAppToMessage(this);
                break;

            case R.id.activity_invite_facebook:
                ShareSDKUtil.shareAppToFacebook(this);
                break;

            case R.id.activity_invite_twitter:
                ShareSDKUtil.shareAppToTwitter(this);
                break;

        }
    }

    @Override
    public void finish() {
        super.finish();
        BackgroundManager.getInstance().ReleaseMainBitmap(getApplicationContext());
        overridePendingTransition(0, R.anim.activity_bottom_out);
    }
}
