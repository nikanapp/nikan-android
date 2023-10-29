/**
 * 
 */
package com.bloomlife.videoapp.activity.fragment;

import static com.bloomlife.videoapp.model.ConversationMessage.STATUS_UNREAD;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.tsz.afinal.FinalActivity;
import net.tsz.afinal.annotation.view.ViewInject;

import com.bloomlife.android.bean.CacheBean;
import com.bloomlife.android.common.util.UiUtils;
import com.bloomlife.android.common.util.Utils;
import com.bloomlife.android.log.Logger;
import com.bloomlife.android.view.AlterDialog;
import com.bloomlife.videoapp.R;
import com.bloomlife.videoapp.activity.AnonymousChatActivity;
import com.bloomlife.videoapp.activity.RealNameChatActivity;
import com.bloomlife.videoapp.activity.MessageListActivity;
import com.bloomlife.videoapp.adapter.ConversationMessageAdapter;
import com.bloomlife.videoapp.app.DbHelper;
import com.bloomlife.videoapp.app.MyHXSDKHelper;
import com.bloomlife.videoapp.app.DbHelper.QueryCallback;
import com.bloomlife.videoapp.common.util.UIHelper;
import com.bloomlife.videoapp.model.ChatBean;
import com.bloomlife.videoapp.model.ConversationMessage;
import com.bloomlife.videoapp.view.FooterTipsView;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMMessage;
import com.easemob.chat.ImageMessageBody;
import com.easemob.chat.TextMessageBody;
import com.easemob.chat.VoiceMessageBody;
import com.easemob.exceptions.EaseMobException;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import androidx.fragment.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

/**
 * @author <a href="mailto:lan4627@gmail.com">zxt</a>
 *
 * @date 2015年2月6日 下午4:50:53
 */
public class MessageListFragment extends Fragment implements View.OnClickListener{
	
	public static final String TAG = MessageListFragment.class.getSimpleName();
	
	public static final String INTENT_NEW_CHAT = "intent_new_chat";
	
	@ViewInject(id=R.id.listview)
	private ListView listView;
	
	@ViewInject(id=R.id.defaultContent,click=ViewInject.DEFAULT)
	private FooterTipsView defaultContent;
	
	private ConversationMessageAdapter adapter;
	
	private Handler handler = new Handler();
	
	private NewMessageBroadcastReceiver msgReceiver ;
	
	private MessageListActivity mActivity;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View layout = inflater.inflate(R.layout.fragment_message, container, false);
		FinalActivity.initInjectedView(this, layout);
		initUi();
		initData();
		return layout;
	}
	
	
	private void initUi(){
		listView.setOnItemClickListener(onItemClickListener);
		listView.setOnItemLongClickListener(onItemLongClickListener);
		listView.addFooterView(getFooterView(), null, false);
		
		defaultContent.setText(getString(R.string.list_empty));
	}
	
	private View getFooterView(){
		LinearLayout layout = new LinearLayout(getActivity());
		layout.setPadding(0, UiUtils.dip2px(getActivity(), 35), 0, UiUtils.dip2px(getActivity(), 35));
		layout.setGravity(Gravity.CENTER);
		layout.addView(new FooterTipsView(getActivity(), getString(R.string.more_empty)));
		return layout;
	}

	private void initData() {
		DbHelper.readMessageList(getActivity(), CacheBean.getInstance().getLoginUserId(), new QueryCallback<ConversationMessage>() {

			@Override
			public void setResult(final List<ConversationMessage> result) {
				handler.post(new Runnable() {

					@Override
					public void run() {
						if (Utils.isEmptyCollection(result)) {
							listView.setVisibility(View.INVISIBLE);
							defaultContent.setVisibility(View.VISIBLE);
						}
						mActivity.setMessageDotNum();
						adapter = new ConversationMessageAdapter(getActivity(), result);
						listView.setAdapter(adapter);
						// 确保了数据查询完成之后才初始化消息监听器，否则会有并发问题
						msgReceiver = new NewMessageBroadcastReceiver();
						IntentFilter intentFilter = new IntentFilter(EMChatManager.getInstance().getNewMessageBroadcastAction());
						intentFilter.setPriority(4); //要比详细详情页面的优先级小
						getActivity().registerReceiver(msgReceiver, intentFilter);
					}
				});
			}
		});
	}
	
	private OnItemClickListener onItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View view, int arg2, long arg3) {
			ConversationMessage message = (ConversationMessage) view.getTag(R.id.id_msg);
			if(ConversationMessage.STATUS_UNREAD==message.getStatus()){
				message.setStatus(ConversationMessage.STATUS_READ);
				DbHelper.updateMessage(getActivity().getApplicationContext(), message);
				adapter.notifyDataSetChanged();
			}
			Intent intent = null;
			if (TextUtils.isEmpty(message.getUserName())){
				intent = new Intent(getActivity(), AnonymousChatActivity.class);
				intent.putExtra(AnonymousChatActivity.INTENT_USERNAME, message.getOtherId());
				intent.putExtra(AnonymousChatActivity.INTENT_CHAT_ID, message.getVideoId());
			} else {
				intent = new Intent(getActivity(), RealNameChatActivity.class);
				intent.putExtra(RealNameChatActivity.INTENT_USERNAME, message.getOtherId());
			}
			startActivityForResult(intent, REQUEST_CODE);
			mActivity.overridePendingTransition(R.anim.activity_right_in, R.anim.activity_left_out);
		}
	};
	
	/**
	 * 清除所有未读消息
	 */
	public void clearUnreadMessages(){
		DbHelper.deleteAllChat(getActivity().getApplicationContext());
		DbHelper.deleteAllMessage(getActivity().getApplicationContext());
		List<ConversationMessage> messages = adapter.getDataList();
		if (messages != null && !messages.isEmpty()){
			messages.clear();
			adapter.notifyDataSetChanged();
		}
	}
	
	private ConversationMessage deleteMsg ;
	
	private OnItemLongClickListener onItemLongClickListener = new OnItemLongClickListener() {

		@Override
		public boolean onItemLongClick(AdapterView<?> arg0, View view, int arg2, long arg3) {
			deleteMsg = (ConversationMessage) view.getTag(R.id.id_msg);
			if(deleteMsg!=null){
				AlterDialog.showDialog(getActivity(), getString(R.string.dialog_delete_message), MessageListFragment.this);
			}
			return true;
		}
	};
	

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.confirm:
			if(deleteMsg!=null){
				DbHelper.deleteMessageByOtherId(getActivity().getApplicationContext(), deleteMsg.getOtherId(),deleteMsg.getVideoId()); //不能使用id删除，因为收到的新消息，保存之后没有设置id，
				adapter.getDataList().remove(deleteMsg);
				if(Utils.isEmptyCollection(adapter.getDataList())){
					listView.setVisibility(View.INVISIBLE);
					defaultContent.setVisibility(View.VISIBLE);
				}
				adapter.notifyDataSetChanged();
			}
			break;
		default:
			break;
		}
	}
	
	private class NewMessageBroadcastReceiver extends BroadcastReceiver {
	    @Override
	    public void onReceive(Context context, Intent intent) {
	    	if(defaultContent.getVisibility() == View.VISIBLE){
	    		defaultContent.setVisibility(View.GONE);
	    		listView.setVisibility(View.VISIBLE);
	    	}
	    	
	    	// 记得把广播给终结掉
	    	abortBroadcast();
	        //消息id
	        String msgId = intent.getStringExtra("msgid");
	        //发消息的人的username(userid)
	        String msgFrom = intent.getStringExtra("from");
	        
	        EMChatManager.getInstance().getConversation(msgFrom).resetUnreadMsgCount();
	        
	        //消息类型，文本，图片，语音消息等,这里返回的值为msg.type.ordinal()。
	        //所以消息type实际为是enum类型
	        int msgType = intent.getIntExtra("type", 0);
	        Log.d(TAG, "new message id:" + msgId + " from:" + msgFrom + " type:" + msgType);
	        //更方便的方法是通过msgId直接获取整个message
	        EMMessage emMessage = EMChatManager.getInstance().getMessage(msgId);
	        
	        String videoId = null ;
	        try {
				// 只有匿名世界的私信才有videoId，实名世界的私信是没有传videoId，需要给一个固定的id
				if (TextUtils.isEmpty(emMessage.getStringAttribute(MyHXSDKHelper.ATTRIBUTE_USER_NAME, "")))
					videoId = emMessage.getStringAttribute(MyHXSDKHelper.ATTRIBUTE_VIDEO_ID);
				else
					videoId = RealNameChatActivity.REAL_NAME_CHAT_ID;
			} catch (EaseMobException e) {
				Log.e(TAG, " 收到环信消息，但是视频id为空，报错", e);
				return ;
			}
	        
	        ConversationMessage oldMessage = DbHelper.readMessage(context, emMessage.getFrom(), videoId);
	        if(oldMessage==null && !msgFrom.equals(context.getString(R.string.custom_name))){
	        	oldMessage = new ConversationMessage();
	        	oldMessage.setByEmMessage(context, emMessage);
	        	DbHelper.saveMessage(context, oldMessage);
	        	// 刷新列表
	        	List<ConversationMessage> dataList = adapter.getDataList();
	        	if(Utils.isEmptyCollection(dataList)){
	        		dataList = new ArrayList<>();
	        		adapter.setDataList(dataList);
	        	}
	        	dataList.add(0, oldMessage);
	        }else{
	        	if (emMessage.getBody() instanceof TextMessageBody){
	        		oldMessage.setContent(((TextMessageBody)emMessage.getBody()).getMessage());
	        	} else if(emMessage.getBody() instanceof ImageMessageBody){
	        		oldMessage.setContent(context.getString(R.string.view_picture_text));
	        		oldMessage.setImagePath(UIHelper.getEMMessageImage((ImageMessageBody)emMessage.getBody()));
	        		oldMessage.setThumbnailUrl(UIHelper.getEMMessageThumbnailUrl((ImageMessageBody)emMessage.getBody()));
	        	} else if(emMessage.getBody() instanceof VoiceMessageBody){
	        		oldMessage.setContent(context.getString(R.string.view_sound_text));
	        	}
	        	oldMessage.setStatus(STATUS_UNREAD);
	        	oldMessage.setUpdateTime(new Date());
				oldMessage.setUserName(emMessage.getStringAttribute(MyHXSDKHelper.ATTRIBUTE_USER_NAME, ""));
				oldMessage.setUserIcon(emMessage.getStringAttribute(MyHXSDKHelper.ATTRIBUTE_USER_ICON, ""));
	        	DbHelper.updateMessage(context, oldMessage);
	        	updateShowingMessage(oldMessage);
	        }
	        DbHelper.saveReciveChat(context, new ChatBean(context, emMessage), mSaveChatCallback);
        	adapter.notifyDataSetChanged();
        }
	}
	
	private DbHelper.Callback mSaveChatCallback = new DbHelper.Callback() {
		
		@Override
		public void complete() {
			handler.post(new Runnable() {
				
				@Override
				public void run() {
					mActivity.setMessageDotNum();
				}
			});
		}
	};
	
	// 更新收到的消息在通话列表中的位置和内容
	private void updateShowingMessage(ConversationMessage oldMessage){
		ConversationMessage targetMsg = null ;
		for (ConversationMessage message : adapter.getDataList()) {
			if(message.getId()==oldMessage.getId()) {
				targetMsg = message;
				break;
			}
		}
		if(targetMsg == null){
			Logger.w(TAG, "收到消息后更新列表为空");
			return;
		}
		targetMsg.setContent(oldMessage.getContent());
		targetMsg.setStatus(oldMessage.getStatus());
		targetMsg.setUserName(oldMessage.getUserName());
		targetMsg.setUserIcon(oldMessage.getUserIcon());
		targetMsg.setUpdateTime(new Date());
		adapter.getDataList().remove(targetMsg);
		adapter.getDataList().add(0,targetMsg);
	}

	public static final int REQUEST_CODE = 1112;

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		/*
		 * 这里要判断requestcode，因为在testin的崩溃中，看到这个方法手打一个request==65538的result，但是data为null，不知道这个result是从哪里来的，不其他进程的。
		 * 但是requestcode只能用低16位，也就是说必须大于0和小雨65535，那么这个65538怎么来的啊，坑爹！！！
		 */
		if(Activity.RESULT_OK==resultCode&&requestCode ==REQUEST_CODE &&data!=null){
			ChatBean chatBean = (ChatBean) data.getExtras().get(INTENT_NEW_CHAT);
			ConversationMessage targetMsg = null;
			for (ConversationMessage message : adapter.getDataList()) {
				if(message.getOtherId().equals(chatBean.getFromUser())&&message.getVideoId().equals(chatBean.getVideoId())) {
					targetMsg = message;
					break;
				}
			}
			if(targetMsg==null) return;
			targetMsg.setContent(chatBean.getContent());
			targetMsg.setUpdateTime(chatBean.getCreatetime());
			targetMsg.setUserName(chatBean.getUserName());
			targetMsg.setUserIcon(chatBean.getUserIcon());
			adapter.getDataList().remove(targetMsg);
			adapter.getDataList().add(0,targetMsg);
			adapter.notifyDataSetChanged();
		}
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		if (msgReceiver!=null) getActivity().unregisterReceiver(msgReceiver);
	}

	@Override
	public void onAttach(Activity activity) {
		mActivity = (MessageListActivity) activity;
		super.onAttach(activity);
	}
	
}
