package com.bloomlife.videoapp.dialog;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcelable;
import android.util.Log;
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
import com.bloomlife.videoapp.model.result.PlatformDb;
import com.bloomlife.videoapp.model.result.UserInfoResult;
import com.bloomlife.videoapp.view.GlobalProgressBar;
import com.bloomlife.videoapp.view.SuperToast;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WbAuthListener;
import com.sina.weibo.sdk.common.UiError;
import com.sina.weibo.sdk.openapi.IWBAPI;
import com.sina.weibo.sdk.openapi.WBAPIFactory;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import net.tsz.afinal.annotation.view.ViewInject;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;

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

    private IWBAPI mWBAPI;
    private IWXAPI mIWXAPI;
    private WXEntryBroadcastReceiver mWXEntryBroadcastReceiver;
    private UserInfoListener mWechatListener;
    private boolean mIsZh;

    @Override
    protected int getLayoutResId() {
        return R.layout.dialog_account;
    }

    @Override
    protected void initLayout(View layout) {
        if (UIHelper.isZH()) {
            mIsZh = true;
            mBtnLeft.setImageResource(R.drawable.btn_account_wechat);
            mBtnRight.setImageResource(R.drawable.btn_account_sina);
        } else {
            mIsZh = false;
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
        if (getActivity() == null) {
            return;
        }
        showProgressBar();
        switch (type) {
            case SINA_WEIBO:
                UserInfoListener sinaListener = new UserInfoListener(Type.SINA_WEIBO);
                AuthInfo authInfo = new AuthInfo(getActivity(), Constants.SINA_APP_KY, Constants.SINA_REDIRECT_URL, Constants.SINA_COPE);
                mWBAPI = WBAPIFactory.createWBAPI(getActivity());
                mWBAPI.registerApp(getActivity(), authInfo);
                mWBAPI.authorize(getActivity(), new WbAuthListener() {
                    @Override
                    public void onComplete(Oauth2AccessToken oauth2AccessToken) {
                        PlatformDb platformDb = new PlatformDb();
                        platformDb.setUserName(oauth2AccessToken.getScreenName());
                        platformDb.setUserId(oauth2AccessToken.getUid());
                        platformDb.setExpiresTime(oauth2AccessToken.getExpiresTime());
                        platformDb.setToken(oauth2AccessToken.getAccessToken());
                        sinaListener.onComplete(platformDb);
                    }

                    @Override
                    public void onError(UiError uiError) {
                        sinaListener.onError();
                    }

                    @Override
                    public void onCancel() {
                        sinaListener.onCancel();
                    }
                });
                break;

            case WECHAT:
                mWXEntryBroadcastReceiver = new WXEntryBroadcastReceiver();
                getActivity().registerReceiver(mWXEntryBroadcastReceiver, new IntentFilter(Constants.WECHAT_ACTION));
                mWechatListener = new UserInfoListener(Type.WECHAT);
                mIWXAPI = WXAPIFactory.createWXAPI(getActivity(), Constants.WECHAT_APP_ID, true);
                mIWXAPI.registerApp(Constants.WECHAT_APP_ID);
                SendAuth.Req req = new SendAuth.Req();
                req.scope = "snsapi_userinfo";
                mIWXAPI.sendReq(req);
                break;

            case TWITTER:
            case FACEBOOK:
                break;
        }
    }

    public class UserInfoListener {

        private boolean mFirst = true;
        private Handler mHandler;
        private Type mType;
        private Account mAccount;

        public UserInfoListener(Type type) {
            mHandler = new Handler(Looper.getMainLooper());
            mType = type;
        }

        public void onComplete(PlatformDb platform) {
            if (mFirst) {
                mFirst = false;
                Logger.d(TAG, "获取用户信息 成功");
                mAccount = Account.makeAccount(mType, platform);
                Logger.d(TAG, "上传用户信息");
                uploadUserInfo(mAccount);
            }
        }

        public void onError() {
            postHideProgressBar();
        }

        public void onCancel() {
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

    @Override
    public void onDestroyView() {
        if (mWXEntryBroadcastReceiver != null && getActivity() != null) {
            getActivity().unregisterReceiver(mWXEntryBroadcastReceiver);
        }
        super.onDestroyView();
    }

    class WXEntryBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle data = intent.getBundleExtra(Constants.WECHAT_LOGIN_DATA);
            if (data != null && mWechatListener != null) {
                SendAuth.Resp resp = new SendAuth.Resp();
                resp.fromBundle(data);
                switch (resp.errCode) {
                    case BaseResp.ErrCode.ERR_OK:
                        PlatformDb platformDb = new PlatformDb();
                        platformDb.setUserName("微信用户");
                        platformDb.setUserId(resp.openId);
                        mIWXAPI.sendReq(resp.code)
                        platformDb.setExpiresTime();
                        platformDb.setToken(oauth2AccessToken.getAccessToken());
                        mWechatListener.onComplete(platformDb);
                        break;
                    case BaseResp.ErrCode.ERR_USER_CANCEL:
                        mWechatListener.onCancel();
                        break;
                    case BaseResp.ErrCode.ERR_AUTH_DENIED:
                    default:
                        mWechatListener.onError();
                        break;
                }
            }
        }
    }

    //通过得到的CODE获取access_token
    private void getAccess_token(String code){
        AjaxParams params=new AjaxParams();
        params.put("appid", ContentsInfo.APP_ID);
        params.put("secret", ContentsInfo.APP_SECRET);
        params.put("code", code);
        params.put("grant_type", "authorization_code");
        String url = "https://api.weixin.qq.com/sns/oauth2/access_token";

        ApiClientUtil.getInstance().Post(url, params, new AjaxCallBack<Object>() {
            @Override
            public void onSuccess(Object t) {
                super.onSuccess(t);
                try {
                    JSONObject resObj=new JSONObject(t.toString());
                    //得到openid和access_token，调用接口登录
                    access_token=resObj.getString("access_token");
                    openid=resObj.getString("openid");
                    refreshToken=resObj.getString("refresh_token");
                    expires_in=resObj.getLong("expires_in");
                    getUserInfo();
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });
    }

    //通过access_token调用接口
    private void getUserInfo(){
        if(isAccessTokenIsInvalid() && System.currentTimeMillis() < expires_in){
            String url="https://api.weixin.qq.com/sns/userinfo?access_token=" + access_token + "&openid=" + openid;
            HttpClient client = new DefaultHttpClient();
            HttpGet get = new HttpGet(URI.create(uri));
            try {
                HttpResponse response = client.execute(get);
                if (response.getStatusLine().getStatusCode() == 200) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
                    StringBuilder builder = new StringBuilder();
                    for (String temp = reader.readLine(); temp != null; temp = reader.readLine()) {
                        builder.append(temp);
                    }
                    JSONObject object = new JSONObject(builder.toString().trim());
                    String nikeName = object.getString("nickname");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
