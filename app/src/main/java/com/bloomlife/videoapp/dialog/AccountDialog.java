package com.bloomlife.videoapp.dialog;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcelable;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.ImageView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.MessageRequest;
import com.android.volley.toolbox.Volley;
import com.bloomlife.android.bean.CacheBean;
import com.bloomlife.android.common.util.StringUtils;
import com.bloomlife.android.log.Logger;
import com.bloomlife.videoapp.BuildConfig;
import com.bloomlife.videoapp.R;
import com.bloomlife.videoapp.app.Constants;
import com.bloomlife.videoapp.app.MyHXSDKHelper;
import com.bloomlife.videoapp.common.CacheKeyConstants;
import com.bloomlife.videoapp.common.util.UIHelper;
import com.bloomlife.videoapp.common.util.Utils;
import com.bloomlife.videoapp.model.Account;
import com.bloomlife.videoapp.model.Account.Type;
import com.bloomlife.videoapp.model.Recommend;
import com.bloomlife.videoapp.model.message.UserInfoMessage;
import com.bloomlife.videoapp.model.result.UserInfoResult;
import com.bloomlife.videoapp.view.GlobalProgressBar;
import com.bloomlife.videoapp.view.SuperToast;

import net.tsz.afinal.annotation.view.ViewInject;

import java.util.ArrayList;
import java.util.HashMap;

import cn.sharesdk.facebook.Facebook;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.twitter.Twitter;
import cn.sharesdk.wechat.friends.Wechat;

import static com.bloomlife.videoapp.common.CacheKeyConstants.KEY_HUANXIN_PWD;

/**
 * Created by zhengxingtian lan4627@Gmail.com on 2015/7/23.
 */
public class AccountDialog extends BaseDialog {

    private static final String TAG = AccountDialog.class.getSimpleName();

    @ViewInject(id = R.id.account_btn_left, click = ViewInject.DEFAULT)
    private ImageView mBtnLeft;

    @ViewInject(id = R.id.account_btn_right, click = ViewInject.DEFAULT)
    private ImageView mBtnRight;

    @ViewInject(id = R.id.account_btn_close, click = ViewInject.DEFAULT)
    private ImageView mBtnClose;

    @ViewInject(id = R.id.account_progressbar)
    private GlobalProgressBar mProgressBar;

    private boolean mIsZh;

    @Override
    protected int getLayoutResId() {
        return R.layout.dialog_account;
    }

    @Override
    protected void initLayout(View layout) {
        if (UIHelper.isZH()) {
            mIsZh = true;
//            mBtnLeft.setImageResource(R.drawable.btn_account_wechat_selector);
//            mBtnRight.setImageResource(R.drawable.btn_account_sina_selector);
            mBtnLeft.setImageResource(R.drawable.btn_account_wechat);
            mBtnRight.setImageResource(R.drawable.btn_account_sina);
        } else {
            mIsZh = false;
//            mBtnLeft.setImageResource(R.drawable.btn_account_facebook_selector);
//            mBtnRight.setImageResource(R.drawable.btn_account_twitter_selector);
            mBtnLeft.setImageResource(R.drawable.btn_account_facebook);
            mBtnRight.setImageResource(R.drawable.btn_account_twitter);
        }
        mProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.account_btn_left:
                sign(mIsZh ? Type.WECHAT : Type.FACEBOOK);
                break;

            case R.id.account_btn_right:
                sign(mIsZh ? Type.SINA_WEIBO : Type.TWITTER);
                break;

            case R.id.account_btn_close:
                dismiss();
                break;
        }
    }

    private void sign(Type type) {
        showProgressBar();
        ShareSDK.initSDK(getActivity());
        Platform platform = null;
        switch (type) {
            case SINA_WEIBO:
                platform = ShareSDK.getPlatform(SinaWeibo.NAME);
                platform.setPlatformActionListener(new UserInfoListener(Type.SINA_WEIBO));
                break;

            case WECHAT:
                platform = ShareSDK.getPlatform(Wechat.NAME);
                platform.setPlatformActionListener(new UserInfoListener(Type.WECHAT));
                break;

            case TWITTER:
                platform = ShareSDK.getPlatform(Twitter.NAME);
                platform.setPlatformActionListener(new UserInfoListener(Type.TWITTER));
                break;

            case FACEBOOK:
                platform = ShareSDK.getPlatform(Facebook.NAME);
                platform.setPlatformActionListener(new UserInfoListener(Type.FACEBOOK));
                break;
        }
        if (platform.isValid()) {
            // 清除弹窗登陆框的Cookie，清除记录上一次登陆的账号密码
            platform.removeAccount();
            CookieManager.getInstance().removeAllCookie();
        }
        platform.SSOSetting(BuildConfig.SSO_LOGIN);
        platform.showUser(null);
    }

    public class UserInfoListener implements PlatformActionListener {

        private boolean mFirst = true;
        private Handler mHandler;
        private Type mType;
        private Account mAccount;

        public UserInfoListener(Type type) {
            mHandler = new Handler(Looper.getMainLooper());
            mType = type;
        }

        @Override
        public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
            if (mFirst) {
                mFirst = false;
                Logger.d(TAG, "获取用户信息 成功");
                mAccount = Account.makeAccount(mType, hashMap, platform.getDb());
                Logger.d(TAG, "上传用户信息");
                uploadUserInfo(mAccount);
            }
        }

        @Override
        public void onError(Platform platform, int i, Throwable throwable) {
            postHideProgressBar();
            platform.getDb().removeAccount();
        }

        @Override
        public void onCancel(Platform platform, int i) {
            postHideProgressBar();
        }

        private void postHideProgressBar() {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    hideProgressBar();
                }
            });
        }

        private void uploadUserInfo(Account account) {
            UserInfoMessage uim = new UserInfoMessage();
            uim.setAccesstoken(account.getTokenSecret());
            uim.setOpenid(account.getId());
            uim.setGender(account.getGender());
            uim.setUsername(account.getUserName());
            uim.setUsericon(account.getUserIcon());
            uim.setCity(account.getLocation());
            uim.setUsersign(account.getDescription());
            switch (account.getType()) {
                case SINA_WEIBO:
                    uim.setAuthplatform(UserInfoMessage.SINA_WEIBO);
                    break;
                case WECHAT:
                    uim.setAuthplatform(UserInfoMessage.WECHAT);
                    break;
                case FACEBOOK:
                    uim.setAuthplatform(UserInfoMessage.FACEBOOK);
                    break;
                case TWITTER:
                    uim.setAuthplatform(UserInfoMessage.TWITTER);
                    break;
            }
            Volley.add(new MessageRequest(uim, mUploadUserInfoListener));
        }

        private void showEditDialog(int type, boolean isFirst) {
            Bundle bundle = new Bundle();
            bundle.putParcelable(EditUserInfoDialog.INTENT_ACCOUNT, mAccount);
            bundle.putInt(EditUserInfoDialog.INTENT_TYPE, type);
            bundle.putBoolean(EditUserInfoDialog.INTENT_FIRST_EDIT, isFirst);
            EditUserInfoDialog userInfoDialog = new EditUserInfoDialog();
            userInfoDialog.setListener(mListener);
            userInfoDialog.setArguments(bundle);
            userInfoDialog.show(getActivity());
        }

        private MessageRequest.Listener mUploadUserInfoListener = new MessageRequest.Listener<UserInfoResult>() {

            @Override
            public void success(UserInfoResult result) {
                if (result.getStatecode() == UserInfoResult.SUCC){
                    // 保存登陆状态
                    mAccount.setUserId(result.getUserid());
                    mAccount.setUserIcon(result.getUsericon());
                    mAccount.setGender(result.getGender());
                    mAccount.setUserName(result.getNickname());
                    mAccount.setDescription(result.getUsersign());
                    Utils.login(getActivity(), mAccount, result.getUserid(), result.getPwd());
                    if (result.getFirstauth() == UserInfoResult.FIRSTAUTH) {
                        showEditDialog(EditUserInfoDialog.EDIT, true);
                    } else {
                        if (mListener != null){
                            mListener.success(mAccount);
                        }
                        getActivity().sendBroadcast(new Intent(Constants.ACTION_USER_LOGIN));
                    }
                } else {
                    SuperToast.show(getActivity(), result.getErrmsg());
                    showEditDialog(EditUserInfoDialog.LOGIN, true);
                }
                dismissAllowingStateLoss();
            }

            @Override
            public void error(VolleyError error) {
                super.error(error);
                SuperToast.show(getActivity(), R.string.dialog_login_fail);
                if (mListener != null)
                    mListener.failure();
            }

            @Override
            public void finish() {
                hideProgressBar();
            }
        };

    }

    private void showProgressBar(){
        mProgressBar.setVisibility(View.VISIBLE);
        mBtnLeft.setEnabled(false);
        mBtnRight.setEnabled(false);
    }

    private void hideProgressBar(){
        mProgressBar.setVisibility(View.GONE);
        mBtnLeft.setEnabled(true);
        mBtnRight.setEnabled(true);
    }

    private Listener mListener;

    public void setListener(Listener l) {
        mListener = l;
    }

    public interface Listener {
        void success(Account account);

        void failure();
    }

}
