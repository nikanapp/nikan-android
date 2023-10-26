/**
 * 
 */
package com.bloomlife.videoapp.activity.fragment;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import net.tsz.afinal.FinalActivity;
import net.tsz.afinal.annotation.view.ViewInject;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.core.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;

import com.android.volley.toolbox.MessageRequest;
import com.android.volley.toolbox.Volley;
import com.bloomlife.android.bean.CacheBean;
import com.bloomlife.android.bean.ProcessResult;
import com.bloomlife.android.common.util.UiUtils;
import com.bloomlife.android.view.AlterDialog;
import com.bloomlife.videoapp.R;
import com.bloomlife.videoapp.activity.MessageListActivity;
import com.bloomlife.videoapp.activity.NotificationStoryPlayActivity;
import com.bloomlife.videoapp.activity.VideoViewPagerActivity;
import com.bloomlife.videoapp.adapter.NotificationListAdapter;
import com.bloomlife.videoapp.app.DbHelper;
import com.bloomlife.videoapp.app.DbHelper.QueryCallback;
import com.bloomlife.videoapp.app.RequestErrorAlertListener;
import com.bloomlife.videoapp.common.util.Utils;
import com.bloomlife.videoapp.manager.MessageManager;
import com.bloomlife.videoapp.model.NotificationMessage;
import com.bloomlife.videoapp.model.Video;
import com.bloomlife.videoapp.model.message.ChangeNotifiedStatusMessage;
import com.bloomlife.videoapp.model.message.GetNotificationListMessage;
import com.bloomlife.videoapp.model.result.GetNotificationListResult;
import com.bloomlife.videoapp.view.FooterTipsView;
import com.lee.pullrefresh.ui.PullToRefreshBase;
import com.lee.pullrefresh.ui.PullToRefreshBase.OnRefreshListener;
import com.lee.pullrefresh.ui.PullToRefreshListView;

import static com.bloomlife.videoapp.activity.VideoViewPagerActivity.*;

/**
 * @author <a href="mailto:lan4627@gmail.com">zhengxingtian</a>
 *
 * @date 2015年2月6日 下午2:49:15
 */
public class NotificationListFragment extends Fragment {

	
	public static final int DB_READ_COMPLETE = 1;
	
	public static final int PAGE_SIZE = 20;
	
	public static final int CHANGE_LOOK = 1;
	public static final int CHANGE_DELETE = 2;
	public static final int CHANGE_CLEAN = 3;
	
	public static final int MSG_TYPE_NEARBY = 4;
	
	private static final String HANDLER_DATA_LIST = "notificationList";
	
	@ViewInject(id=R.id.fragment_notification_messagelist)
	private PullToRefreshListView mPullToRefreshListView;
	
	@ViewInject(id=R.id.fragment_notification_empty)
	private FooterTipsView mEmpty;
	
	private Handler mHandler = new NotificationHandler(this);
	
	private NotificationListAdapter mAdapter;
	
	private MessageListActivity mActivity;
	
	private ListView mListView;
	
	private CacheBean mCacheBean;
	private View mFooterView;
	
	public int mPageNum = 1;
	
	private boolean mHasNoMoreView;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View layout = inflater.inflate(R.layout.fragment_notification, container, false);
		FinalActivity.initInjectedView(this, layout);
		initMessageListView();
		initData();
		return layout;
	}
	
	private void initData(){
		mCacheBean = CacheBean.getInstance();
		if (!Utils.isLogin(getActivity(), false))
			return;
		DbHelper.readNotification(getActivity().getApplicationContext(), CacheBean.getInstance().getLoginUserId(), callback);
	}
	
	private void initMessageListView(){
		mPullToRefreshListView.setOnRefreshListener(mOnRefreshListener);
		mPullToRefreshListView.setPullLoadEnabled(false);
		mPullToRefreshListView.setPullRefreshEnabled(true);
		mPullToRefreshListView.setScrollLoadEnabled(true);
		
		mListView = mPullToRefreshListView.getRefreshableView();
		mListView.setEmptyView(mEmpty);
		mListView.setOnItemClickListener(mOnItemClickListener);
		mListView.setOnItemLongClickListener(mItemLongClickListener);
		mListView.setDivider(getResources().getDrawable(R.color.transparent));
		mListView.setVerticalScrollBarEnabled(false);
		
		mEmpty.setText(getString(R.string.list_empty));
	}
	
	private void initAdapter(List<NotificationMessage> dataList){
		mAdapter = new NotificationListAdapter(getActivity(), dataList);
		mFooterView = getFooterView();
		mListView.addFooterView(mFooterView);
		mListView.setAdapter(mAdapter);
		mListView.removeFooterView(mFooterView);
		sendTask();
	}
	
	private OnItemClickListener mOnItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			NotificationMessage message = (NotificationMessage) parent.getAdapter().getItem(position);
			// 通知设置为已看过
			if (message.getStatus() == NotificationMessage.STATUS_UNREAD){
				message.setStatus(NotificationMessage.STATUS_READ);
				mAdapter.notifyDataSetChanged();
				setBtnDotNumber();
			}
			if (message.isStoryMsg()){
				Intent intent = new Intent(getActivity(), NotificationStoryPlayActivity.class);
				intent.putExtra(NotificationStoryPlayActivity.INTENT_STORY_ID, message.getStoryId());
				intent.putExtra(NotificationStoryPlayActivity.INTENT_USER_ID, message.getExtra().getUserid());
				startActivity(intent);
			} else {
				Intent intent = new Intent(getActivity(), VideoViewPagerActivity.class);
				intent.putParcelableArrayListExtra(INTENT_VIDEO_LIST, getVideoList(message));
				intent.putExtra(INTENT_VIDEO_POSITION, 0);
				startActivity(intent);
				getActivity().overridePendingTransition(R.anim.activity_camera_in, 0);
			}
			// 把通知被查看的消息发送到后台，后台确认后再更新数据库里这条通知的未读状态
			sendChangeMessage(message, CHANGE_LOOK);
		}
	};
	
	private void setBtnDotNumber(){
		MessageManager.getInstance().decreaseSysInforNum(getActivity());
		mActivity.setNotificationDotNum();
	}
	
	private ArrayList<Video> getVideoList(NotificationMessage message){
		ArrayList<Video> videoList = new ArrayList<>();
		Video video = new Video();
		video.setVideoid(message.getVideoid());
		video.setVideouri(message.getVideouri());
		video.setUid(message.getMsgtype() != MSG_TYPE_NEARBY ? mCacheBean.getLoginUserId() : "0");
		videoList.add(video);
		return videoList;
	}
	
	private OnItemLongClickListener mItemLongClickListener = new OnItemLongClickListener() {

		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View view,
				int position, long id) {
			NotificationMessage msg = (NotificationMessage) parent.getAdapter().getItem(position);
			AlterDialog.showDialog(
					getActivity(),
					getActivity().getString(R.string.fragment_notification_delete),
					new DeleteNotificationListener(getActivity().getApplication(), msg));
			return true;
		}
	};
	
	private OnRefreshListener<ListView> mOnRefreshListener = new OnRefreshListener<ListView>() {

		@Override
		public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
			mPageNum = 1;
			sendTask();
		}

		@Override
		public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
			sendTask();
		}
	};
	
	public void sendTask(){
		if (!Utils.isLogin(getActivity())){
			return;
		}
		List<NotificationMessage> notifications= mAdapter.getDataList();
		String lastTime = null;
		if (notifications != null && !notifications.isEmpty())
			lastTime = String.valueOf(notifications.get(notifications.size()-1).getCreatetime());
		if (mPageNum <= 1) lastTime = null;
		Volley.addToTagQueue(
				new MessageRequest(
					new GetNotificationListMessage(mPageNum, PAGE_SIZE, lastTime),
					mMessageReqListener
				)
		);
	}
	
	private void sendChangeMessage(NotificationMessage m, int type){
		ChangeNotifiedStatusMessage message = null;
		if (m.isStoryMsg()){
			message = new ChangeNotifiedStatusMessage(null, m.getStoryId(), type, m.getMsgtype());
		} else {
			message = new ChangeNotifiedStatusMessage(m.getVideoid(), null, type, m.getMsgtype());
		}
		Volley.addToTagQueue(new MessageRequest(message, new ChangeNotificationStatusListener(m, type)));
	}
	
	/**
	 * 清除所有未读通知
	 */
	public void clearUnreadNotifications(){
		List<NotificationMessage> messages = mAdapter.getDataList();
		if (messages != null && !messages.isEmpty()){
			Volley.addToTagQueue(new MessageRequest(
					new ChangeNotifiedStatusMessage(null, null, CHANGE_CLEAN, 0),
					new ChangeNotificationStatusListener(null, CHANGE_CLEAN)));
			MessageManager.getInstance().cleanSysinformNum(getActivity().getApplicationContext());
			messages.clear();
			mAdapter.notifyDataSetChanged();
		}
	}
	
	private QueryCallback<NotificationMessage> callback = new QueryCallback<NotificationMessage>() {
		
		@Override
		public void setResult(List<NotificationMessage> result) {
			Bundle bundle = new Bundle();
			bundle.putParcelableArrayList(HANDLER_DATA_LIST, new ArrayList<>(result));
			android.os.Message message = new android.os.Message();
			message.what = DB_READ_COMPLETE;
			message.setData(bundle);
			mHandler.sendMessage(message);
		}
	};
	
	static class NotificationHandler extends Handler{
		
		private WeakReference<NotificationListFragment> mReference;

		private NotificationHandler(NotificationListFragment fragment){
			mReference = new WeakReference<>(fragment);
		}
		
		@Override
		public void handleMessage(android.os.Message msg) {
			super.handleMessage(msg);
			NotificationListFragment fragment = mReference.get();
			if (fragment == null) return;
			switch (msg.what) {
			case DB_READ_COMPLETE:
				List<NotificationMessage> dataList = msg.getData().getParcelableArrayList(HANDLER_DATA_LIST);
				fragment.initAdapter(dataList);
				break;

			default:
				break;
			}
		}
		
	}
	
	private View getFooterView(){
		LinearLayout layout = new LinearLayout(getActivity());
		layout.setPadding(0, UiUtils.dip2px(getActivity(), 35), 0, UiUtils.dip2px(getActivity(), 35));
		layout.setGravity(Gravity.CENTER);
		layout.addView(new FooterTipsView(getActivity(), getString(R.string.more_empty)));
		return layout;
	}
	
	class DeleteNotificationListener implements View.OnClickListener{
		
		private Context mContext;
		private NotificationMessage mMsg;
		
		public DeleteNotificationListener(Application context, NotificationMessage msg){
			mContext = context;
			mMsg = msg;
		}

		@Override
		public void onClick(View v) {
			DbHelper.deleteNotification(mContext, mMsg.getMsgid());
			sendChangeMessage(mMsg, CHANGE_DELETE);
			mAdapter.getDataList().remove(mMsg);
			mAdapter.notifyDataSetChanged();
			if (mMsg.getStatus()== NotificationMessage.STATUS_UNREAD){
				setBtnDotNumber();
			}
		}
		
	}
	
	private void addNoMoreView(){
		if (!mHasNoMoreView){
			mListView.addFooterView(mFooterView);
			mHasNoMoreView = true;
		}
	}
	
	private void removeMoreView(){
		if (mHasNoMoreView){
			mListView.removeFooterView(mFooterView);
			mHasNoMoreView = false;
		}
	}

	private MessageRequest.Listener mMessageReqListener = new RequestErrorAlertListener<GetNotificationListResult>(){
		@Override
		public void success(GetNotificationListResult result) {
			List<NotificationMessage> messageList = result.getList();
			if (mPageNum == 1){
				// 只保存最新的数据
				filterAndSaveNotification(new ArrayList<>(messageList));
				mAdapter.setDataList(messageList);
			} else {
				mAdapter.addDataList(messageList);
			}
			mAdapter.notifyDataSetChanged();
			if (messageList.size() < PAGE_SIZE){
				mPullToRefreshListView.setHasMoreData(false);
				mPullToRefreshListView.setScrollLoadEnabled(false);
				addNoMoreView();
			} else {
				mPullToRefreshListView.setHasMoreData(true);
				removeMoreView();
			}
			mPageNum++;
			MessageManager.getInstance().setSysInformNum(getActivity(), result.getNotreadnum());
		}

		@Override
		public void finish() {
			mPullToRefreshListView.onPullDownRefreshComplete();
			mPullToRefreshListView.onPullUpRefreshComplete();
		}
	};

	private void filterAndSaveNotification(List<NotificationMessage> messageList){
		// 在数据库已经有的，更新最新数据，剩下新通知保存到数据库里
		List<NotificationMessage> updateMsgList = new ArrayList<>();
		for (int i=0; i<messageList.size(); i++){
			NotificationMessage newMsg = messageList.get(i);
			for (NotificationMessage oldMsg:mAdapter.getDataList()){
				if (oldMsg.getMsgid().equals(newMsg.getMsgid())){
					updateNotificationMessage(oldMsg, newMsg);
					updateMsgList.add(messageList.remove(i--));
					break;
				}
			}
		}
		DbHelper.saveNotification(getActivity(), messageList);
		DbHelper.updateNotificationList(getActivity(), updateMsgList);
	}

	private void updateNotificationMessage(NotificationMessage oldMsg, NotificationMessage newMsg){
		if (oldMsg.getStatus() != newMsg.getStatus()){
			oldMsg.setStatus(newMsg.getStatus());
			DbHelper.updateNotification(getActivity(), oldMsg);
		}
	}

	private class ChangeNotificationStatusListener extends MessageRequest.Listener<ProcessResult>{

		private NotificationMessage mNotificationMessage;
		private int mSendType;

		public ChangeNotificationStatusListener(NotificationMessage notificationMessage, int sendType){
			mNotificationMessage = notificationMessage;
			mSendType = sendType;
		}

	    @Override
	    public void success(ProcessResult result) {
			// 更新数据库里的通知信息
			if (mNotificationMessage == null)
				return;
			if (CHANGE_DELETE == mSendType){
				DbHelper.deleteNotification(getActivity().getApplicationContext(), mNotificationMessage.getMsgid());
				return;
			}
			if (CHANGE_LOOK == mSendType){
				DbHelper.updateNotification(getActivity().getApplicationContext(), mNotificationMessage);
				return;
			}
			if (CHANGE_CLEAN == mSendType){
				DbHelper.deleteAllNotification(getActivity().getApplicationContext());
			}
	    }
	}

	@Override
	public void onAttach(Activity activity) {
		mActivity = (MessageListActivity) activity;
		super.onAttach(activity);
	}

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
		if (isVisibleToUser){
			mActivity.setNotificationDotNum();
		} else {
			if (mAdapter == null || Utils.isEmpty(mAdapter.getDataList()))
				return;
			// 因为精选集的点赞和标签评论是看过一次后就自动变成已读
			List<NotificationMessage> msgList = new ArrayList<>();
			for (NotificationMessage m:mAdapter.getDataList()){
				if (m.getMsgtype() == NotificationMessage.TYPE_STORY_LIKE || m.getMsgtype() == NotificationMessage.TYPE_STORY_COMMENT_TAG){
					m.setStatus(NotificationMessage.STATUS_READ);
					msgList.add(m);
				}
			}
			mAdapter.notifyDataSetChanged();
			DbHelper.updateNotificationList(getActivity(), msgList);
		}
	}

}
