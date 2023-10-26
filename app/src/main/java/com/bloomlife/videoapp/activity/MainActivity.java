/**
 * 
 */
package com.bloomlife.videoapp.activity;

import android.animation.ValueAnimator;
import androidx.fragment.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bloomlife.android.common.util.AnalyseUtils;
import com.bloomlife.android.common.util.UiUtils;
import com.bloomlife.android.framework.BaseActivity;
import com.bloomlife.videoapp.R;
import com.bloomlife.videoapp.activity.fragment.MainStoryListFragment;
import com.bloomlife.videoapp.activity.fragment.ManTypeFragment;
import com.bloomlife.videoapp.activity.fragment.MapBoxFragment;
import com.bloomlife.videoapp.app.Constants;
import com.bloomlife.videoapp.common.CacheKeyConstants;
import com.bloomlife.videoapp.common.util.UIHelper;
import com.bloomlife.videoapp.common.util.Utils;
import com.bloomlife.videoapp.manager.BackgroundManager;
import com.bloomlife.videoapp.manager.LocationManager;
import com.bloomlife.videoapp.manager.MessageManager;
import com.bloomlife.videoapp.view.MainMenuWindow;
import com.easemob.chat.EMChatManager;
import com.umeng.analytics.MobclickAgent;
import com.umeng.update.UmengUpdateAgent;

import net.tsz.afinal.annotation.view.ViewInject;

import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.LinkedList;

/**
 * 地图基础实现基类，所有的具体地图实现都需要继承的类
 * 
 * @author <a href="mailto:xiai.fei@gmail.com">xiai_fei</a>
 * 
 * @date 2014-12-23 下午2:43:05
 */
public class MainActivity extends BaseActivity implements
		OnClickListener, View.OnTouchListener {

	public static final String INTENT_LATLNG = "intent_latlng";

	public static final int MENU_ANIM_DRAWABLE_SIZE = 11;
	public static final int SWITCH_ANIM_DURATION = 300;
	public static final int REQUEST_CUPTURE = 1001;

	@ViewInject(id = R.id.activity_main_menu)
	private MainMenuWindow mMenuWindow;

	@ViewInject(id = R.id.activity_main_blur_image)
	private ImageView mBlurImage;

	@ViewInject(id = R.id.activity_main_btn_menu, click = ViewInject.DEFAULT)
	private ImageView mBtnMenu;

	@ViewInject(id = R.id.activity_main_btn_massage, click = ViewInject.DEFAULT)
	private ImageView mBtnMessage;

	@ViewInject(id = R.id.new_msg_dot, click = ViewInject.DEFAULT)
	private TextView newMsgNunText;

	@ViewInject(id = R.id.activity_main_btn_camera_red, click = ViewInject.DEFAULT)
	private ImageView mBtnCameraRed;

	@ViewInject(id = R.id.activity_main_btn_camera_purple, click = ViewInject.DEFAULT)
	private ImageView mBtnCameraPurple;

	@ViewInject(id = R.id.activity_main_btn_map)
	private ImageView mBtnMap;

	@ViewInject(id = R.id.activity_main_btn_story)
	private ImageView mBtnStory;

	@ViewInject(id = R.id.activity_main_btn_map_line)
	private View mMapLine;

	@ViewInject(id = R.id.activity_main_btn_story_line)
	private View mStoryLine;

	@ViewInject(id = R.id.activity_main_btn_search, click = ViewInject.DEFAULT)
	private View mBtnSearch;

	@ViewInject(id = R.id.layout_story_camera_tips_stub)
	private ViewStub mStoryTipsStub;

	@ViewInject(id = R.id.layout_glimpse_camera_tips_stub)
	private ViewStub mGlimpseTipsStub;

	private View mStoryTips;
	private View mGlimpseTips;

	private BackgroundManager mBackgroundManager = BackgroundManager.getInstance();

	protected Handler mHandler = new MyHandler(this);

	protected RefreshBackgroundReceiver refreshBackgroundReceiver;
	protected RecycleBackgroundReceiver recycleBackgroundReceiver;

	private MainStoryListFragment mMainStoryListFragment;
	private MapBoxFragment mMapBoxFragment;

	private LinkedList<Drawable> mBtnMenuDrawables;

	private int mCameraType;
	private boolean mSendBroadcast;

	static class MyHandler extends Handler{
		private WeakReference<MainActivity> mReference;

		public MyHandler(MainActivity act){
			mReference = new WeakReference<>(act);
		}

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			MainActivity act = mReference.get();
			if (act != null && msg.what == BackgroundManager.BLUR_TASK_COMPLETE){
				act.bgBitmapCreateComplete((Bitmap) msg.obj);
			}
		}

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		UmengUpdateAgent.update(this);
		init();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()){
			case MotionEvent.ACTION_DOWN:
				mMenuWindow.dismiss();
				break;
		}
		return super.onTouchEvent(event);
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch (event.getAction()){
			case MotionEvent.ACTION_DOWN:
				mMenuWindow.dismiss();
				break;
		}
		return false;
	}

	private void init(){
		mCameraType = CameraActivity.RECORD_TYPE_STORY;
		mMenuWindow.setOnDismissListener(mMenuDissmissListener);

		initMenuBtnAnimation();

		mMainStoryListFragment = new MainStoryListFragment();
		mMapBoxFragment = new MapBoxFragment();
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		transaction.add(R.id.activity_main_layout, mMapBoxFragment);
		transaction.add(R.id.activity_main_layout, mMainStoryListFragment);
		transaction.hide(mMapBoxFragment).commitAllowingStateLoss();

		mBtnMap.setEnabled(true);
		mBtnStory.setEnabled(false);
		mBtnStory.setOnClickListener(mPageSwitchListener);
		mBtnMap.setOnClickListener(mPageSwitchListener);

		refreshBackgroundReceiver = new RefreshBackgroundReceiver();
		recycleBackgroundReceiver = new RecycleBackgroundReceiver();
		registerReceiver(refreshBackgroundReceiver, new IntentFilter(BackgroundManager.ACTION_REFRESH_BACKGROUND));
		registerReceiver(recycleBackgroundReceiver, new IntentFilter(BackgroundManager.ACTION_RECYCLE_BACKGROUND));

		IntentFilter newNotificationFilter = new IntentFilter(Constants.ACTION_NEW_SYS_INFORM);
		newNotificationFilter.setPriority(4);
		registerReceiver(newSysInformReceiver, newNotificationFilter);

		IntentFilter newChatFilter = new IntentFilter(EMChatManager.getInstance().getNewMessageBroadcastAction());
		newChatFilter.setPriority(3);
		registerReceiver(newMsgReceiver, newChatFilter);


		// 初始化引导图
		if (Utils.isFirst(MainActivity.this, CacheKeyConstants.KEY_FIRST_STORY)){
			mStoryTips = mStoryTipsStub.inflate();
			mStoryTips.setOnClickListener(mStoryTipsClickListener);
			TextView text = (TextView) mStoryTips.findViewById(R.id.story_tips_text);
			text.setTypeface(UIHelper.getHelveticaLt(this));
			// 英文版的引导文字长，需要把字体缩小
			if (!Utils.isZH()){
				text.setTextSize(11);
			}
		}
		if (Utils.isFirst(MainActivity.this, CacheKeyConstants.KEY_FIRST_GLIMPSE)){
			mGlimpseTips = mGlimpseTipsStub.inflate();
			mGlimpseTips.setVisibility(View.INVISIBLE);
			mGlimpseTips.setOnClickListener(mGlimpseTipsClickListener);
			TextView text = (TextView) mGlimpseTips.findViewById(R.id.glimpse_tips_text);
			text.setTypeface(UIHelper.getHelveticaLt(this));
			// 英文版的引导文字长，需要把字体缩小
			if (!Utils.isZH()){
				text.setTextSize(11);
			}
		}
	}

	private void initMenuBtnAnimation(){
		mBtnMenuDrawables = new LinkedList<>();
		for (int i=MENU_ANIM_DRAWABLE_SIZE; i>=0; i--){
			String name = "animation_main_menu" + String.format("%02d", i);
			mBtnMenuDrawables.add(getResources().getDrawable(getResources().getIdentifier(name, "drawable", getPackageName())));
		}
	}

	private void startMenuBtnAnimation(){
		AnimationDrawable drawable = new AnimationDrawable();
		drawable.setOneShot(true);
		Collections.reverse(mBtnMenuDrawables);
		for (Drawable d:mBtnMenuDrawables){
			drawable.addFrame(d, 33);
		}
		mBtnMenu.setImageDrawable(drawable);
		drawable.start();
	}

	@Override
	protected void onResume() {
		super.onResume();
		MessageManager.getInstance().updateUnreadMsgNum(this, mRefreshUnreadNMsgNum);
		if (BackgroundManager.isHaveBackground()){
			mBlurImage.setImageBitmap(BackgroundManager.getInstance().getCacheBitmap(this));
		} else {
			recycleBgBitmap();
		}
	}

	@Override
	protected void onRestart() {
		super.onRestart();
	}

	private MessageManager.Listener mRefreshUnreadNMsgNum = new MessageManager.Listener(){

		@Override
		public void refreshUnderNum(int num) {
			if (num > 0) {
				newMsgNunText.setVisibility(View.VISIBLE);
				newMsgNunText.setText("" + num);
			} else
				newMsgNunText.setVisibility(View.INVISIBLE);
			}
	};

	@Override
	protected void onDestroy() {
		unregisterReceiver(refreshBackgroundReceiver);
		unregisterReceiver(recycleBackgroundReceiver);
		unregisterReceiver(newSysInformReceiver);
		unregisterReceiver(newMsgReceiver);
		super.onDestroy();
	}

	private View.OnClickListener mPageSwitchListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
			switch (v.getId()){
				case R.id.activity_main_btn_map:
					// 切换到地图页面
					mBtnMap.setEnabled(false);
					mBtnStory.setEnabled(true);
					mBtnSearch.setVisibility(View.GONE);
					mCameraType = CameraActivity.RECORD_TYPE_GLIMPSE;
					transaction.hide(mMainStoryListFragment);
					transaction.show(mMapBoxFragment);
					transaction.commitAllowingStateLoss();
					// 切换页面时，下方按钮条的动画
					switchAnim(false);
					// 引导图也要切换
					switchTips(false);
					break;

				case R.id.activity_main_btn_story:
					// 切换到精选集列表页面
					mBtnMap.setEnabled(true);
					mBtnStory.setEnabled(false);
					mBtnSearch.setVisibility(View.VISIBLE);
					mCameraType = CameraActivity.RECORD_TYPE_STORY;
					transaction.hide(mMapBoxFragment);
					transaction.show(mMainStoryListFragment);
					transaction.commitAllowingStateLoss();

					switchAnim(true);
					switchTips(true);
					break;
			}
		}
	};

	@Override
	public void onClick(View v) {
		super.onClick(v);
		shortDisenableBtn(v);
		mMenuWindow.dismiss();
		switch (v.getId()){
			case R.id.activity_main_btn_camera_red:
			case R.id.activity_main_btn_camera_purple:
				Intent intent = new Intent(this, CameraActivity.class);
				intent.putExtra(CameraActivity.INTENT_RECORD_TYPE, mCameraType);
				startActivityForResult(intent, RESULT_FIRST_USER);
				overridePendingTransition(R.anim.activity_camera_in, 0);
				break;

			case R.id.activity_main_btn_menu:
				if (mMenuWindow.isShown()){
					mMenuWindow.dismiss();
				} else {
					startMenuBtnAnimation();
					mMenuWindow.show();
				}
				break;

			case R.id.activity_main_btn_massage:
				Intent messageIntent = new Intent(this, MessageListActivity.class);
				messageIntent.putExtra(MessageListActivity.INTENT_FROM_MAIN, true);
				startActivityForResult(messageIntent, REQUEST_CUPTURE);
				overridePendingTransition(R.anim.activity_bottom_in, 0);
				captureScreen();
				break;

			case R.id.activity_main_btn_search:
				startActivityForResult(new Intent(this, SearchActivity.class), REQUEST_CUPTURE);
				overridePendingTransition(R.anim.anim_not, 0);
				captureScreen();
				break;
		}
	}

	/**
	 * 切换引导图
	 */
	private void switchTips(boolean isStory){
		if (isStory){
			if (mStoryTips != null){
				mStoryTips.setVisibility(View.VISIBLE);
			}
			if (mGlimpseTips != null){
				mGlimpseTips.setVisibility(View.GONE);
			}
		} else {
			if (mStoryTips != null){
				mStoryTips.setVisibility(View.GONE);
			}
			if (mGlimpseTips != null){
				mGlimpseTips.setVisibility(View.VISIBLE);
			}
		}
	}

	private View.OnClickListener mStoryTipsClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			mStoryTips.setVisibility(View.GONE);
			mStoryTips = null;
			Utils.notFirst(MainActivity.this, CacheKeyConstants.KEY_FIRST_STORY);
		}
	};

	private View.OnClickListener mGlimpseTipsClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			mGlimpseTips.setVisibility(View.GONE);
			mGlimpseTips = null;
			Utils.notFirst(MainActivity.this, CacheKeyConstants.KEY_FIRST_GLIMPSE);
		}
	};

	private void shortDisenableBtn(final View v){
		v.setEnabled(false);
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				v.setEnabled(true);
			}
		}, 300);
	}

	private void switchAnim(final boolean isStory){
		final int length = mBtnMap.getLeft() - mBtnStory.getLeft() + UiUtils.dip2px(this, 2);
		ValueAnimator animator = ValueAnimator.ofFloat(0, 1);
		animator.setDuration(SWITCH_ANIM_DURATION);
		animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				float value = (float) animation.getAnimatedValue();
				if (isStory) {
					mStoryLine.setAlpha(value);
					mBtnCameraRed.setAlpha(value);

					mMapLine.setTranslationX(length * (1 - value));
					mStoryLine.setTranslationX(length * (1 - value));
				} else {
					mStoryLine.setAlpha(1 - value);
					mBtnCameraRed.setAlpha(1 - value);

					mMapLine.setTranslationX(length * value);
					mStoryLine.setTranslationX(length * value);
				}

			}
		});
		animator.start();
	}

	private MainMenuWindow.OnDismissListener mMenuDissmissListener = new MainMenuWindow.OnDismissListener() {
		@Override
		public void onDismiss() {
			startMenuBtnAnimation();
		}
	};

	private ManTypeFragment.FinishListener mFragmentFinishListener = new ManTypeFragment.FinishListener() {

		@Override
		public void finish() {
			recycleBgBitmap();
		}
	};

	private long startKeyOn = 1;

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) { // 禁用了back键返回
			Toast t = null;
			if (startKeyOn == 1) {
				startKeyOn = System.currentTimeMillis();
			} else if (System.currentTimeMillis() - startKeyOn < 2000) {
				AnalyseUtils.uploadAnalyseData(getApplicationContext());
				
				LocationManager.getInstance(getApplicationContext()).stopLocation();
				
				this.finish();
				MessageManager.getInstance().reset();
				MobclickAgent.onKillProcess(getApplicationContext());
				return true;
			} else
				startKeyOn = System.currentTimeMillis();

			t = Toast.makeText(this, getString(R.string.exit_toast), Toast.LENGTH_SHORT);
			t.show();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void captureScreen(){
		BackgroundManager.setHaveBackground(true);
		mBackgroundManager.makeBlurBitmap(this, UIHelper.makeRootViewBitmap(this, getWindow().getDecorView(), 1f, true), mHandler);
	}

	private void captureScreenAndSendBroadcast(){
		mSendBroadcast = true;
		captureScreen();
	}

	private void bgBitmapCreateComplete(Bitmap bitmap){
		if (mSendBroadcast){
			mSendBroadcast = false;
			sendBroadcast(new Intent(BackgroundManager.ACTION_CREATE_BACKGROUND));
		}
		mBlurImage.setImageBitmap(bitmap);
	}

	private void recycleBgBitmap(){
		mBlurImage.setImageBitmap(null);
		mBackgroundManager.recycle();
		BackgroundManager.setHaveBackground(false);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == VideoEditActivity.RESULT_VIDEO){
			if (mMainStoryListFragment.isVisible()){
				mMainStoryListFragment.onActivityResult(requestCode, resultCode, data);
				return;
			}
		}
		if (resultCode == StoryVideoEditActivity.RESULT_VIDEO){
			if (mMapBoxFragment.isVisible()){
				mMapBoxFragment.onActivityResult(requestCode, resultCode, data);
				return;
			}
		}
		BackgroundManager.setHaveBackground(false);
		super.onActivityResult(requestCode, resultCode, data);
	}

	class RefreshBackgroundReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getBooleanExtra(BackgroundManager.INTENT_IS_REFRESH, false)){
				captureScreen();
			} else {
				captureScreenAndSendBroadcast();
			}
		}
	}

	class RecycleBackgroundReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			recycleBgBitmap();
		}
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
	}

	/**
	 * 消息广播接收者
	 *
	 */
	private BroadcastReceiver newMsgReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			newMsgNunText.setVisibility(View.VISIBLE);
			newMsgNunText.setText("" + MessageManager.getInstance().addPrimaryUnreadNum());
		}

	};
	/**
	 * 系统通知广播接收者
	 *
	 */
	private BroadcastReceiver newSysInformReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			newMsgNunText.setVisibility(View.VISIBLE);
			newMsgNunText.setText("" + MessageManager.getInstance().addPrimaryUnreadNum());
		}

	};
}
