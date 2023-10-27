/**
 * 
 */
package com.bloomlife.videoapp.activity;

import static com.bloomlife.videoapp.common.CacheKeyConstants.CONSTANT_SYSCODE_KEY;
import static com.bloomlife.videoapp.common.CacheKeyConstants.KEY_HUANXIN_PWD;

import androidx.fragment.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources.NotFoundException;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import com.android.volley.toolbox.MessageRequest;
import com.android.volley.toolbox.Volley;
import com.bloomlife.android.bean.CacheBean;
import com.bloomlife.android.bean.FailureResult;
import com.bloomlife.android.bean.ProcessResult;
import com.bloomlife.android.business.BusinessProcessor;
import com.bloomlife.android.common.CacheKeyConstants;
import com.bloomlife.android.common.util.StringUtils;
import com.bloomlife.android.common.util.UiHelper;
import com.bloomlife.android.framework.BaseActivity;
import com.bloomlife.videoapp.R;
import com.bloomlife.videoapp.activity.fragment.WelcomeInitFragment;
import com.bloomlife.videoapp.activity.fragment.WelcomeVideoFragment;
import com.bloomlife.videoapp.app.AppContext;
import com.bloomlife.videoapp.app.Constants;
import com.bloomlife.videoapp.app.MyHXSDKHelper;
import com.bloomlife.videoapp.common.util.ImageLoaderUtils;
import com.bloomlife.videoapp.common.util.Utils;
import com.bloomlife.videoapp.manager.EmotionMusicFileManager;
import com.bloomlife.videoapp.model.Account;
import com.bloomlife.videoapp.model.CacheMenus;
import com.bloomlife.videoapp.model.DymainicMenu;
import com.bloomlife.videoapp.model.Dynamicimg;
import com.bloomlife.videoapp.model.SysCode;
import com.bloomlife.videoapp.model.message.ParamSyncMessage;
import com.bloomlife.videoapp.model.result.ParamSyncResult;
import com.igexin.sdk.PushManager;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.ArrayList;
import java.util.List;

/**
 * 	欢迎界面
 * 
 * @author <a href="mailto:xiai.fei@gmail.com">xiai_fei</a>  
 *
 * @date 2014-12-5  上午10:23:59
 */
public class SpalshActivity extends BaseActivity implements WelcomeInitFragment.Callback{
	
	public static final String INTENT_REPLAY = "intent_replay";
	public static final String INTENT_LGOIN = "intent_login";
	
	public static final String KEY_FIRST_COME = "key_first_come";
	
	public static final String KEY_CHECK_SUCCESS = "key_check_success";
	
	public static final int REQUEST_USER_AGREEMENT = 0x1;
	
	private final CacheBean cacheBean = CacheBean.getInstance();
	
	private Handler handler = new Handler();
	
	private String TAG = "SpalshActivity";
	
	private boolean replay  = false;
	private boolean mFromSettingLogout = false;
	
	/**
	 * 是否点击了进入主页的按钮。
	 */
	public static final String CACHE_KEY = "CLICK_WELCOME_BTN";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//去掉信息栏
		
		setContentView(R.layout.activity_spalsh);
		PushManager.getInstance().initialize(this.getApplicationContext());
		
		try {
			String shoufaFlag = getResources().getString(R.string.shoufa); //是否用首发图。如果没有首发图的版本，为了使得编译通过，随便找了一张最小的图来替代
			if("1".equals(shoufaFlag)){
				findViewById(R.id.activity_spalsh_layout).setBackgroundResource(R.drawable.start_splash_shoufa);
			}
		} catch (NotFoundException e) {
			e.printStackTrace();
		}

		if(getIntent().getExtras()!=null){
			replay = getIntent().getExtras().getBoolean(INTENT_REPLAY, false);  //从设置页面过来的话，这里会重新播放，就不需要请求后台数据
			mFromSettingLogout = getIntent().getExtras().getBoolean(INTENT_LGOIN, false);  // 是否跳过视频，直接进入登录页
		}
	}
	
	
	@Override
	protected void onResume() {
		super.onResume();
		if (isUserAgreementResult) {
			isUserAgreementResult = false;
			return;
		}
		if (!replay) syncServerParam(false);
		if (mFromSettingLogout){
			// 如果是设置页的退出登录后跳转过来的，则不播放视频，直接进入欢迎页。
			handler.postDelayed(mWelcomeFragmentRunnable, 1000);
		} else if (replay || StringUtils.isEmpty(cacheBean.getString(getApplicationContext(), CACHE_KEY))){
			// 如果是第一次进入应用，需要播放一段开始视频
			handler.postDelayed(videoFragmentRunnable, 1000);
		} else {
			handler.postDelayed(startToMainRunnable, 1000);
		}
	}

	private Runnable videoFragmentRunnable = new Runnable() {

		@Override
		public void run() {

			WelcomeVideoFragment fragment = new WelcomeVideoFragment();
			/*
			 * 当当前activity不占据顶层显示，activity如果进行了onSaveInstance之后，FragmentManager
			 * 是不能调用commit的，否则状态不一致，所以要使用commitAllowingStateLoss，同时在stop的时候删除这个handler
			*/
			getSupportFragmentManager().beginTransaction().replace(R.id.activity_spalsh_layout, fragment).commitAllowingStateLoss();
		}
	};

	private Runnable startToMainRunnable = new Runnable() {

		@Override
		public void run() {
			startToMainActivity();
		}
	};

	private Runnable mWelcomeFragmentRunnable = new Runnable() {
		@Override
		public void run() {
			WelcomeInitFragment fragment = new WelcomeInitFragment();
			Bundle bundle = new Bundle();
			// 因为不用播放欢迎视频，所以关闭重新播放视频按钮
			bundle.putBoolean(WelcomeInitFragment.INTENT_HAS_REVERSE, false);
			fragment.setArguments(bundle);
			getSupportFragmentManager().beginTransaction().replace(R.id.activity_spalsh_layout, fragment).commitAllowingStateLoss();
		}
	};

	/**
	 * 同步后台参数，初始化 .
	 * @param startToMain 。 接收完成参数后，是否跳到主页
	 */
	public void syncServerParam(boolean startToMain){
		Log.d(TAG, "开始同步系统参数");
		if(startToMain){
			//检查在点击进入的一刻，初始化是否已经完成，如果完成则直接跳到主页，不需要今次获取
			if(cacheBean.isSyncParam()){
				Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.activity_spalsh_layout);
				if(fragment instanceof WelcomeInitFragment && Utils.isFirstUseApp(getApplicationContext())){
					((WelcomeInitFragment) fragment).dismissLoadLayout();
				} else {
					startToMainActivity();
				}
				return;
			}
		}
		Volley.addToTagQueue(new MessageRequest(new ParamSyncMessage(), new ParamSyncListener(getApplicationContext(), startToMain)));
	}

	/***
	 *   参数同步异步任务.
	 */
	private class ParamSyncListener extends MessageRequest.Listener<ParamSyncResult>{

		private Context mContext;
		private boolean mStartToMain;

		public ParamSyncListener(Context context, boolean startToMain){
			mContext = context;
			mStartToMain = startToMain;
		}

	    @Override
	    public void success(ParamSyncResult result) {
			Log.d(TAG, "收到系统同步参数结果");
			cacheBean.putObject(mContext, CONSTANT_SYSCODE_KEY, result.getSyscode());
			//add by xjp 20150616
			cacheBean.putObject(mContext, CacheKeyConstants.Analyse_Switch, result.getSyscode().getAnaylseswitch());
			List<DymainicMenu> menuList = CacheMenus.get(mContext);
			// 对比缓存的动态菜单，看是否有新按钮
			for (DymainicMenu m:result.getSyscode().getDynamicmenus()){
				if (menuList.contains(m)){
					m.setNewBtn(false);
				} else {
					m.setNewBtn(true);
				}
			}
			AppContext.setSysCode(result.getSyscode());
			// 下载表情弹幕的gif图片
			if (!Utils.isEmpty(result.getSyscode().getDynamicimgs())) {
				List<Dynamicimg> downloadList = new ArrayList<>(result.getSyscode().getDynamicimgs());
				ImageLoader.getInstance().loadImage(
						downloadList.remove(0).getOriginalurl(),
						ImageLoaderUtils.getMyVideoPreviewImageOption(),
						new MyImageLoadingListener(downloadList));
			}
			// 同步心情背景音乐
			EmotionMusicFileManager.getInstance().syncEmotionMusic(mContext, result.getSyscode().getEmotions());
			// 判断用户是否已经登陆
			Account account = Utils.getAccount(mContext);
			if (account == null || TextUtils.isEmpty(account.getId()) || "1".equals(result.getSyscode().getReauth())){
				cacheBean.setLoginUserId(mContext, CacheBean.NO_LOGIN);
			} else {
				cacheBean.setLoginUserId(mContext, account.getUserId());
			}
			cacheBean.setSyncParam(true);
			MyHXSDKHelper.login(mContext.getApplicationContext());

			if (StringUtils.isEmpty(cacheBean.getString(getApplicationContext(), CACHE_KEY))){
				CacheBean.getInstance().putString(getApplicationContext(), SpalshActivity.CACHE_KEY, "1");
			}

			// 如果没有用户记录，说明第一次进入获取参数成功，跳转到主页
			if (mStartToMain) {
				Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.activity_spalsh_layout);
				if (fragment instanceof WelcomeInitFragment && Utils.isFirstUseApp(getApplicationContext())) {
					Utils.setNoFirstUseApp(getApplicationContext());
					((WelcomeInitFragment) fragment).dismissLoadLayout();
				} else {
					startToMainActivity();
				}
			}
	    }

		@Override
		public void failure(FailureResult result) {
			UiHelper.showToast(mContext, mContext.getResources().getString(R.string.network_error_tips));
			Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.activity_spalsh_layout);
			if(fragment instanceof WelcomeInitFragment){
				((WelcomeInitFragment)fragment).hideInitProgress();
			}
		}
	}

	public void test() {
		cacheBean.putObject(SpalshActivity.this, CONSTANT_SYSCODE_KEY, new SysCode());
		startToMainActivity();
	}

	public void startToMainActivity() {
		Intent broadcastIntent = new Intent();
		broadcastIntent.setAction(Constants.ACTION_FINISH);
		sendBroadcast(broadcastIntent);

		Intent intent = new Intent(SpalshActivity.this, MainActivity.class);
		startActivity(intent);
		overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
		finish();
	}


	@Override
	public void finish() {
		super.finish();
		Log.d(TAG, " 页面finish");
	}


	@Override
	protected void onStop() {
		handler.removeCallbacks(videoFragmentRunnable);
		handler.removeCallbacks(startToMainRunnable);
		super.onStop();
	}

	@Override
	public void welcomeStartToMainActivity() {
		startToMainActivity();
	}

	private boolean isUserAgreementResult;

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_USER_AGREEMENT){
			isUserAgreementResult = true;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	static class MyImageLoadingListener implements ImageLoadingListener{

		private List<Dynamicimg> mDynamicList;

		public MyImageLoadingListener(List<Dynamicimg> dynamicList){
			mDynamicList = dynamicList;
		}

		@Override
		public void onLoadingStarted(String imageUri, View view) {

		}

		@Override
		public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

		}

		@Override
		public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
			if (!Utils.isEmpty(mDynamicList)){
				ImageLoader.getInstance().loadImage(mDynamicList.remove(0).getOriginalurl(), ImageLoaderUtils.getMyVideoPreviewImageOption(), this);
			}
		}

		@Override
		public void onLoadingCancelled(String imageUri, View view) {

		}
	}

}