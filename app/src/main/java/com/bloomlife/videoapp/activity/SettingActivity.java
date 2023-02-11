/**
 *
 */
package com.bloomlife.videoapp.activity;

import java.io.File;

import net.tsz.afinal.annotation.view.ViewInject;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.widget.TextView;

import com.bloomlife.android.bean.CacheBean;
import com.bloomlife.android.common.util.CacheUtils;
import com.bloomlife.android.common.util.UiHelper;
import com.bloomlife.android.framework.BaseActivity;
import com.bloomlife.android.view.AlterDialog;
import com.bloomlife.android.view.SlipButton;
import com.bloomlife.android.view.TitleBar;
import com.bloomlife.android.view.TitleBar.OnTitleBarListener;
import com.bloomlife.videoapp.R;
import com.bloomlife.videoapp.app.AppContext;
import com.bloomlife.videoapp.app.Constants;
import com.bloomlife.videoapp.app.FinishBroadcast;
import com.bloomlife.videoapp.app.MyHXSDKHelper;
import com.bloomlife.videoapp.app.PushService;
import com.bloomlife.videoapp.common.CacheKeyConstants;
import com.bloomlife.videoapp.common.FileUtils;
import com.bloomlife.videoapp.common.util.Utils;
import com.bloomlife.videoapp.dialog.AccountDialog;
import com.bloomlife.videoapp.dialog.DialogUtils;
import com.bloomlife.videoapp.manager.BackgroundManager;
import com.bloomlife.videoapp.manager.VideoFileManager;
import com.bloomlife.videoapp.model.Account;
import com.bloomlife.videoapp.view.SettingView;

/**
 * @author <a href="mailto:lan4627@gmail.com">zhengxingtian</a>
 *         设置页面
 * @date 2014年12月23日 上午11:22:03
 */
public class SettingActivity extends BaseActivity {

    @ViewInject(id = R.id.setting_titlebar)
    private TitleBar mTitleBar;

    @ViewInject(id = R.id.setting_btn_user_info, click = ViewInject.DEFAULT)
    private SettingView mBtnUserInfo;

    @ViewInject(id = R.id.setting_btn_feedback, click = ViewInject.DEFAULT)
    private SettingView mBtnFeedback;

    @ViewInject(id = R.id.setting_btn_about, click = ViewInject.DEFAULT)
    private SettingView mBtnAbout;

    @ViewInject(id = R.id.clearcache, click = ViewInject.DEFAULT)
    private SettingView clearCache;

    @ViewInject(id = R.id.setting_btn_replay, click = ViewInject.DEFAULT)
    private SettingView replay;

    @ViewInject(id = R.id.seeting_push_slip, click = ViewInject.DEFAULT)
    private SlipButton mBtnPush;

    @ViewInject(id = R.id.setting_btn_logout, click = ViewInject.DEFAULT)
    private TextView mBtnLogout;

    private FinishBroadcast mFinishBroadcast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        mTitleBar.setOnTitleBarListener(mTitleBarListener);
        mBtnAbout.setOnLongClickListener(mAboutListener);
        mBtnPush.setCheck(PushService.isOpenPush(this));
        mBtnPush.SetOnChangedListener(pushBtnListener);

        mFinishBroadcast = new FinishBroadcast(this);
        registerReceiver(mFinishBroadcast, new IntentFilter(Constants.ACTION_FINISH));

        if (Utils.isLogin(this, false)){
            mBtnLogout.setText(getString(R.string.activity_setting_logout));
        } else {
            mBtnLogout.setText(getString(R.string.activity_setting_login));
        }
    }

    private OnTitleBarListener mTitleBarListener = new OnTitleBarListener() {

        @Override
        public void onTitleClick() {
            // TODO Auto-generated method stub

        }

        @Override
        public void onRightClick() {
            // TODO Auto-generated method stub

        }

        @Override
        public void onLeftClick() {
            finish();
        }
    };

    private OnLongClickListener mAboutListener = new OnLongClickListener() {

        @Override
        public boolean onLongClick(View v) {
            UiHelper.shortToast(getApplicationContext(), CacheBean.getInstance().getLoginUserId());
            return false;
        }
    };

    private SlipButton.OnChangedListener pushBtnListener = new SlipButton.OnChangedListener() {

        @Override
        public void OnChanged(boolean CheckState) {
            if (CheckState) {
                PushService.openPush(SettingActivity.this);
                MyHXSDKHelper.getInstance().openPush();
            } else {
                PushService.closePush(SettingActivity.this);
                MyHXSDKHelper.getInstance().closePush();
            }
        }
    };

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {

            case R.id.setting_btn_user_info:
                Account account = CacheBean.getInstance().getObject(this, CacheKeyConstants.KEY_MY_ACCOUNT, Account.class);
                if (account == null)
                    DialogUtils.showAccountDialog(this);
                else
                    DialogUtils.showEditDialog(this, account);
                break;

            case R.id.setting_btn_feedback:
                Intent myIntent = new Intent(this, AnonymousChatActivity.class);
                myIntent.putExtra(AnonymousChatActivity.INTENT_USERNAME, getString(R.string.custom_name));
                startActivity(myIntent);
                break;

            case R.id.setting_btn_about:
                startActivity(new Intent(this, AboutActivity.class));
                break;

            case R.id.setting_btn_replay:
                BackgroundManager.setHaveBackground(false);
                Intent intent = new Intent(this, SpalshActivity.class);
                intent.putExtra(SpalshActivity.INTENT_REPLAY, true);
                startActivity(intent);
                break;

            case R.id.clearcache:
                VideoFileManager.getInstance().evictAll();
                File file = CacheUtils.getCacheFileDirectory(this, Constants.CACHE_FOLDER_NAME);
                if (file != null) {
                    boolean result = FileUtils.deleteDir(file);
                    if (result)
                        UiHelper.showToast(this, " " + getString(R.string.activity_setting_clear_tips) + "  ");
                }
                break;

            case R.id.setting_btn_logout:
                if (Utils.isLogin(this, false)){
                    AlterDialog.showDialog(this, getString(R.string.activity_setting_logout_dialog_text), mLogoutClickListener);
                } else {
                    AccountDialog dialog = new AccountDialog();
                    dialog.setListener(mLoginListener);
                    dialog.show(this);
                }
                break;

            default:
                break;
        }
    }

    private View.OnClickListener mLogoutClickListener = new View.OnClickListener(){

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.confirm){
                Utils.logout(SettingActivity.this);
                mBtnLogout.setText(getString(R.string.activity_setting_login));
                BackgroundManager.setHaveBackground(false);
                Intent splashIntent = new Intent(SettingActivity.this, SpalshActivity.class);
                splashIntent.putExtra(SpalshActivity.INTENT_LGOIN, true);
                startActivity(splashIntent);
                ((AppContext)getApplicationContext()).releaseActivty();
            }
        }
    };

    private AccountDialog.Listener mLoginListener = new AccountDialog.Listener() {
        @Override
        public void success(Account account) {
            mBtnLogout.setText(getString(R.string.activity_setting_logout));
        }

        @Override
        public void failure() {

        }
    };

    @Override
    protected void onDestroy() {
        unregisterReceiver(mFinishBroadcast);
        super.onDestroy();
    }


}
