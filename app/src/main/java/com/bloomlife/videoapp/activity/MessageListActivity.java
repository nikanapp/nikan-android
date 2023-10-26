/**
 * 
 */
package com.bloomlife.videoapp.activity;

import java.util.ArrayList;
import java.util.List;

import net.tsz.afinal.FinalActivity;
import net.tsz.afinal.annotation.view.ViewInject;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager.widget.ViewPager;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.bloomlife.android.view.AlterDialog;
import com.bloomlife.videoapp.R;
import com.bloomlife.videoapp.activity.fragment.MessageListFragment;
import com.bloomlife.videoapp.activity.fragment.NotificationListFragment;
import com.bloomlife.videoapp.adapter.MessagePagerAdapter;
import com.bloomlife.videoapp.app.Constants;
import com.bloomlife.videoapp.common.util.UIHelper;
import com.bloomlife.videoapp.manager.BackgroundManager;
import com.bloomlife.videoapp.manager.MessageManager;

/**
 * 	消息列表
 * 
 * @author <a href="mailto:xiai.fei@gmail.com">xiai_fei</a>  
 *
 * @date 2014-12-17  下午5:21:48
 */
public class MessageListActivity extends FragmentActivity implements OnClickListener{

	private static final String TAG = "MessageListActivity";
	
	public static final int MESSAGE = 0;
	public static final int NOTIFICATION = 1;
	
	public static final String INTENT_TYPE = "type";
	public static final String INTENT_FROM_MAIN = "fromMain";
	
	@ViewInject(id=R.id.activity_message_background)
	private ImageView mLayoutBackground;
	
	@ViewInject(id=R.id.back, click=ViewInject.DEFAULT)
	private ImageView back;
	
	@ViewInject(id=R.id.activity_messagelist_menu, click=ViewInject.DEFAULT)
	private View mBtnMenu;
	
	@ViewInject(id=R.id.activity_message_viewpager)
	private ViewPager mViewPager;
	
	@ViewInject(id=R.id.activity_message_tab_message, click=ViewInject.DEFAULT)
	private TextView mTabMessage;
	
	@ViewInject(id=R.id.activity_message_tab_notification, click=ViewInject.DEFAULT)
	private TextView mTabNotification;
	
	@ViewInject(id=R.id.activity_message_tab_message_left_dot)
	private View mTabMessageLeftDot;
	
	@ViewInject(id=R.id.activity_message_tab_notification_left_dot)
	private View mTabNotificationLeftDot;
	
	@ViewInject(id=R.id.message_dot)
	private TextView mMessageDot;
	 
	@ViewInject(id=R.id.notification_dot)
	private TextView mNotificationDot;
	
	private MessagePagerAdapter mAdapter;
	
	private MessageListFragment messageListFragment;
	private NotificationListFragment notificationListFragment;

	private boolean mFirst = true;
	private boolean mFromMain;
	private boolean mRecycleBackground = true;
	private BroadcastReceiver mCreateBackgroundReceiver;
	private BroadcastReceiver mNewNotificationReceiver;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_message);
		FinalActivity.initInjectedView(this);
		initLayout();
		initViewPager();

		IntentFilter filter = new IntentFilter(Constants.ACTION_NEW_SYS_INFORM);
		filter.setPriority(2);
		mNewNotificationReceiver = new NewNotificationReceiver();
		registerReceiver(mNewNotificationReceiver, filter);
	}
	
	private void initLayout(){
		mFromMain = getIntent().getBooleanExtra(INTENT_FROM_MAIN, false);

		Typeface tf = UIHelper.getHelveticaTh(this);
		mTabMessage.setTypeface(tf);
		mTabNotification.setTypeface(tf);
		mTabNotification.getPaint().setFakeBoldText(true);	//加粗
		mTabMessage.getPaint().setFakeBoldText(true);		//加粗
		setNotificationDotNum();
	}
	
	private void initViewPager(){
		List<Fragment> fragments = new ArrayList<Fragment>();
		messageListFragment = new MessageListFragment();
		fragments.add(messageListFragment);
		notificationListFragment = new NotificationListFragment();
		fragments.add(notificationListFragment);
		mAdapter = new MessagePagerAdapter(getSupportFragmentManager(), fragments);
		
		mViewPager.setAdapter(mAdapter);
		mViewPager.setOnPageChangeListener(mOnPageChangeListener);
		
		int type = getIntent().getIntExtra(INTENT_TYPE, MESSAGE);
		mViewPager.setCurrentItem(type);
		if (type == MESSAGE){
			selectMessage();
		} else {
			selectNotification();
		}
	}
	
	public void setNotificationDotNum(){
		int num = MessageManager.getInstance().getSysInfromNum(getApplicationContext());
		if (num <= 0){
			mNotificationDot.setVisibility(View.INVISIBLE);
		} else {
			mNotificationDot.setText(String.valueOf(num));
			mNotificationDot.setVisibility(View.VISIBLE);
		}
	}
	
	public void setMessageDotNum(){
		int num = MessageManager.getInstance().getPrimaryUnreadNum(getApplicationContext());
		if (num <= 0){
			mMessageDot.setVisibility(View.INVISIBLE);
		} else {
			mMessageDot.setText(String.valueOf(num));
			mMessageDot.setVisibility(View.VISIBLE);
		}
	}
	
	private ViewPager.OnPageChangeListener mOnPageChangeListener = new ViewPager.OnPageChangeListener() {
		
		@Override
		public void onPageSelected(int arg0) {
			switch (arg0) {
			case MESSAGE:
				selectMessage();
				break;
				
			case NOTIFICATION:
				selectNotification();
				break;

			default:
				break;
			}
		}
		
		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onPageScrollStateChanged(int arg0) {
			// TODO Auto-generated method stub
			
		}
	};

	@Override
	protected void onResume() {
		super.onResume();
		setNotificationDotNum();
		setMessageDotNum();
		// 不是从主页进来的话，页面下方没有背景，需要设置一个背景
		if (!mFromMain){
			mLayoutBackground.setBackgroundColor(Color.BLACK);
			if (!BackgroundManager.getInstance().hasBackground()){
				// 没有背景图，需要发广播给主页截屏
				BackgroundManager.getInstance().capture(this);
				mCreateBackgroundReceiver = new CreateBackgroundReceiver();
				registerReceiver(mCreateBackgroundReceiver, new IntentFilter(BackgroundManager.ACTION_CREATE_BACKGROUND));
			} else {
				mRecycleBackground = false;
				mLayoutBackground.setImageBitmap(BackgroundManager.getInstance().getBackgroundBitmap());
			}
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back:
			finish();
			break;
			
		case R.id.activity_message_tab_message:
			mViewPager.setCurrentItem(MESSAGE);
			selectMessage();
			break;
			
		case R.id.activity_message_tab_notification:
			mViewPager.setCurrentItem(NOTIFICATION);
			selectNotification();
			break;
			
		case R.id.activity_messagelist_menu:
			if (mViewPager.getCurrentItem() == MESSAGE){
				AlterDialog.showDialog(MessageListActivity.this, getString(R.string.activity_message_clear_messages), mClearMessagesListener);
				break;
			}
			if (mViewPager.getCurrentItem() == NOTIFICATION){
				AlterDialog.showDialog(MessageListActivity.this, getString(R.string.activity_message_clear_notifications), mClearNotificationsListener);
				break;
			}
			break;
			
		default:
			break;
		}
	}
	
	private void selectMessage(){
		mTabMessageLeftDot.setVisibility(View.VISIBLE);
		mTabMessage.setTextColor(getResources().getColor(R.color.activity_message_purple));
		mTabNotificationLeftDot.setVisibility(View.GONE);
		mTabNotification.setTextColor(getResources().getColor(R.color.activity_message_grey));
	}
	
	private void selectNotification(){
		mTabMessageLeftDot.setVisibility(View.GONE);
		mTabMessage.setTextColor(getResources().getColor(R.color.activity_message_grey));
		mTabNotificationLeftDot.setVisibility(View.VISIBLE);
		mTabNotification.setTextColor(getResources().getColor(R.color.activity_message_purple));
	}
	
	private OnClickListener mClearMessagesListener = new OnClickListener(){

		@Override
		public void onClick(View v) {
			mMessageDot.setVisibility(View.INVISIBLE);
			messageListFragment.clearUnreadMessages();
		}
		
	};
	
	private OnClickListener mClearNotificationsListener = new OnClickListener(){

		@Override
		public void onClick(View v) {
			mNotificationDot.setVisibility(View.INVISIBLE);
			notificationListFragment.clearUnreadNotifications();
		}
		
	};

	@Override
	public void finish() {
		if (mNewNotificationReceiver != null) {
			unregisterReceiver(mNewNotificationReceiver);
		}
		if (mCreateBackgroundReceiver != null){
			unregisterReceiver(mCreateBackgroundReceiver);
		}
		if (mRecycleBackground){
			BackgroundManager.getInstance().ReleaseMainBitmap(getApplicationContext());
		}
		super.finish();
		overridePendingTransition(0, R.anim.activity_bottom_out);
	}
	
	
	/**
	 * 通知广播接收者
	 * 
	 */
	class NewNotificationReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			setNotificationDotNum();
			if (notificationListFragment != null){
				notificationListFragment.mPageNum = 1;
				notificationListFragment.sendTask();  //当有消息来的时候，刷新通知列表当前列表。
			}
			abortBroadcast();
		}

	}

	/**
	 * 背景图片创建完成时的广播
	 */
	class CreateBackgroundReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			mLayoutBackground.setImageBitmap(BackgroundManager.getInstance().getBackgroundBitmap());
		}
	}

	@Override
	protected void onDestroy() {
		mLayoutBackground.setImageBitmap(null);
		super.onDestroy();
	}
	
}
