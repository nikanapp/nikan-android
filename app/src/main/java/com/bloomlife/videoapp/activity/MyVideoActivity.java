package com.bloomlife.videoapp.activity;

import net.tsz.afinal.annotation.view.ViewInject;

import androidx.fragment.app.Fragment;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import androidx.viewpager.widget.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bloomlife.android.framework.BaseActivity;
import com.bloomlife.videoapp.R;
import com.bloomlife.videoapp.activity.fragment.MyStorysFragment;
import com.bloomlife.videoapp.activity.fragment.MyVideosFragment;
import com.bloomlife.videoapp.adapter.MyVideoListPagerAdapter;
import com.bloomlife.videoapp.app.Constants;
import com.bloomlife.videoapp.app.FinishBroadcast;
import com.bloomlife.videoapp.common.util.ImageLoaderUtils;
import com.bloomlife.videoapp.common.util.Utils;
import com.bloomlife.videoapp.dialog.DialogUtils;
import com.bloomlife.videoapp.manager.BackgroundManager;
import com.bloomlife.videoapp.model.Account;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * 
 * @author <a href="mailto:xiai.fei@gmail.com">xiai_fei</a>  
 *
 * @date 2014-11-27  上午11:23:57
 */
public class MyVideoActivity extends BaseActivity{
	
	private static final String TAG = "MyVideoActivity";

	public static final int DB_CALLBACK = 1;
	
	public static final int PAGE_SIZE = 20;
	
	public static final int RESULT_MY = 1001;

	public static final String INTENT_FIRST_PAGE = "firstPage";

	public static final int PAGE_STORYS = 0x100;
	public static final int PAGE_VIDEOS = 0x200;
	
	@ViewInject(id=R.id.activity_myvideo_title_layout)
	private ViewGroup mTitleBarLayout;
	
	@ViewInject(id=R.id.activity_myvideo_return, click=ViewInject.DEFAULT)
	private ImageView mBtnReturn;
	
	@ViewInject(id=R.id.activity_myvideo_edit, click=ViewInject.DEFAULT)
	private ImageView mBtnEdit;
	
	@ViewInject(id=R.id.activity_myvideo_setting, click=ViewInject.DEFAULT)
	private ImageView mBtnSetting;

	@ViewInject(id=R.id.activity_myvideo_btn_storys, click=ViewInject.DEFAULT)
	private ImageView mBtnStorys;

	@ViewInject(id=R.id.activity_myvideo_btn_storys_line)
	private View mBtnStorysLine;

	@ViewInject(id=R.id.activity_myvideo_btn_videos, click=ViewInject.DEFAULT)
	private ImageView mBtnVideos;

	@ViewInject(id=R.id.activity_myvideo_btn_videos_line, click=ViewInject.DEFAULT)
	private View mBtnVideosLine;

	@ViewInject(id=R.id.activity_myvideo_btn_avatar, click=ViewInject.DEFAULT)
	private ImageView mBtnAvatar;

	@ViewInject(id=R.id.activity_myvideo_layout)
	private ViewPager mViewPager;

	private MyVideoListPagerAdapter mPageAdapter;

	private FinishBroadcast finishBroadcast;

	private boolean mEditSelected;

	private MyVideosFragment mVideosFragment;
	private MyStorysFragment mStorysFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_myvideo);
		initUi();
		initRecevier();
	}

	/**
	 * 
	 */
	private void initRecevier() {
		//注册刷新广播
		finishBroadcast = new FinishBroadcast(this);
		IntentFilter intentFilter = new IntentFilter(Constants.ACTION_FINISH);
		registerReceiver(finishBroadcast, intentFilter);
	}
	
	private void initUi(){
		mTitleBarLayout.bringToFront();
		mPageAdapter = new MyVideoListPagerAdapter(getSupportFragmentManager());
		mViewPager.setAdapter(mPageAdapter);
		mViewPager.setOnPageChangeListener(mOnPageChangeListener);

		if (getIntent().getIntExtra(INTENT_FIRST_PAGE, PAGE_STORYS) == PAGE_STORYS){
			selectStorys();
			mViewPager.setCurrentItem(0);
		} else {
			selectVideos();
			mViewPager.setCurrentItem(1);
		}

		Account account = Utils.getAccount(this);
		String url = TextUtils.isEmpty(account.getSdcardUserIcon()) ? account.getUserIcon() : account.getSdcardUserIcon();
		ImageLoader.getInstance().displayImage(url, mBtnAvatar, ImageLoaderUtils.getDecodingOptions());
	}

	private ViewPager.OnPageChangeListener mOnPageChangeListener = new ViewPager.OnPageChangeListener() {
		@Override
		public void onPageScrolled(int i, float v, int i1) {

		}

		@Override
		public void onPageSelected(int i) {
			if (i == 0){
				selectStorys();
			} else if (i == 1){
				selectVideos();
			}
		}

		@Override
		public void onPageScrollStateChanged(int i) {

		}
	};

	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
			case R.id.activity_myvideo_return:
				finish();
				break;
			case R.id.activity_myvideo_setting:
				startActivity(new Intent(this, SettingActivity.class));
				break;

			case R.id.activity_myvideo_btn_storys:
				selectStorys();
				mViewPager.setCurrentItem(0);
				break;

			case R.id.activity_myvideo_btn_videos:
				selectVideos();
				mViewPager.setCurrentItem(1);
				break;

			case R.id.activity_myvideo_edit:
				setEditBtnStatus(!mEditSelected);
			default:
				break;

			case R.id.activity_myvideo_btn_avatar:
				DialogUtils.showUsarInfo(this, Utils.getAccount(this));
				break;
		}
	}

	private Fragment currentFragment(){
		return mPageAdapter.getFragment(mViewPager.getCurrentItem());
	}

	public void setEditBtnStatus(boolean status){
		mBtnEdit.setSelected(status);
		mEditSelected = status;
		mStorysFragment = (MyStorysFragment) mPageAdapter.getFragment(0);
		mVideosFragment = (MyVideosFragment) mPageAdapter.getFragment(1);

		mVideosFragment.setEditStatus(status);
		mStorysFragment.setEditStatus(status);
	}

	private void selectStorys(){
		mBtnStorys.setSelected(true);
		mBtnStorysLine.setVisibility(View.VISIBLE);
		mBtnVideos.setSelected(false);
		mBtnVideosLine.setVisibility(View.INVISIBLE);
	}

	private void selectVideos(){
		mBtnVideos.setSelected(true);
		mBtnVideosLine.setVisibility(View.VISIBLE);
		mBtnStorys.setSelected(false);
		mBtnStorysLine.setVisibility(View.INVISIBLE);
	}

	@Override
	public void finish() {
		super.finish();
		BackgroundManager.getInstance().ReleaseMainBitmap(getApplicationContext());
		overridePendingTransition(0, R.anim.activity_bottom_out);
	}

	public void showEmptyAnimation(boolean show){
		if (show){
			mBtnEdit.setEnabled(false);
		} else {
			mBtnEdit.setEnabled(true);
		}
	}

	@Override
	protected void onDestroy() {
		if (finishBroadcast!=null) unregisterReceiver(finishBroadcast);
		super.onDestroy();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Fragment fragment = currentFragment();
		if (fragment != null){
			fragment.onActivityResult(requestCode, resultCode, data);
		}
	}
}
