/**
 * 
 */
package com.bloomlife.videoapp.manager;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.Context;
import android.content.Intent;

import com.android.volley.toolbox.MessageRequest;
import com.android.volley.toolbox.Volley;
import com.bloomlife.android.bean.CacheBean;
import com.bloomlife.android.common.util.StringUtils;
import com.bloomlife.videoapp.app.Constants;
import com.bloomlife.videoapp.app.Database;
import com.bloomlife.videoapp.common.CacheKeyConstants;
import com.bloomlife.videoapp.common.util.Utils;
import com.bloomlife.videoapp.model.NotificationMessage;
import com.bloomlife.videoapp.model.message.GetMsgNumMessage;
import com.bloomlife.videoapp.model.result.GetMsgNumResult;

/**
 * @author <a href="mailto:xiai.fei@gmail.com">xiai_fei</a>
 * 
 * @date 2015-2-9 下午2:16:47
 */
public class MessageManager {

	private MessageManager() {
	}

	private static MessageManager instance;

	public synchronized static MessageManager getInstance() {
		if (instance == null)
			instance = new MessageManager();
		return instance;
	}

	private int unreadCount = -1;

	private int unreadPrimaryLetterNum;

	private int unreadSysInformNum = -1;

	private CacheBean cacheBean = CacheBean.getInstance();

	public int getPrimaryUnreadNum(Context context) {
		unreadPrimaryLetterNum = Database.unreadChatNum(context.getApplicationContext(), CacheBean.getInstance().getLoginUserId());
		return unreadPrimaryLetterNum;
	}

	public int getTotalUnreadMsgNum() {
		return unreadCount;
	}

	public int getSysInfromNum(Context context) {
		unreadSysInformNum = cacheBean.getInt(context, CacheKeyConstants.KEY_MSG_NUM, 0);
		return unreadSysInformNum;
	}

	public void updateUnreadMsgNum(final Activity activity, final Listener listener) {
		if (!Utils.isLogin(activity)) return;
		if (unreadCount < 0) { // 小于0代表没有从服务器通过过消息数
			unreadCount = 0;
			Volley.addToTagQueue(
					new MessageRequest(
							new GetMsgNumMessage(),
							new MessageRequest.Listener<GetMsgNumResult>() {
								@Override
								public void success(GetMsgNumResult result) {
									cacheBean.putInt(activity, CacheKeyConstants.KEY_MSG_NUM, result.getMsgnum());
									checkNewMsgNum(activity, listener);
								}
							}));
		} else {
			checkNewMsgNum(activity, listener); // 已经从服务器中同步过未读消息数，那么就以后台为准。
		}
	}

	public void setSysInformNum(Context context, int num) {
		unreadSysInformNum = num;
		cacheBean.putInt(context, CacheKeyConstants.KEY_MSG_NUM, num);
	}

	/**
	 * 检查是否有新消息，有则设置未读消息数
	 */
	private void checkNewMsgNum(final Activity activity, Listener listener) {
		unreadPrimaryLetterNum = Database.unreadChatNum(activity.getApplicationContext(), CacheBean.getInstance().getLoginUserId());
		unreadSysInformNum = cacheBean.getInt(activity.getApplicationContext(), CacheKeyConstants.KEY_MSG_NUM, 0);
		unreadCount = unreadPrimaryLetterNum + unreadSysInformNum;
		listener.refreshUnderNum(unreadCount);
	}

	public int addPrimaryUnreadNum() {
		unreadPrimaryLetterNum++;
		unreadCount++;
		return unreadCount;
	}

	private final Set<String> msgIdQuene = new HashSet<String>();

	/**
	 * 增加系统消息未读数。并广播通知相关需要刷新消息数的页面
	 * 
	 * @param context
	 */
	public void addSysInformNum(Context context, String msgId) {
		if (StringUtils.isNotEmpty(msgId)) {
			// 这个缓存队列是处理当用户收到新消息，但是用户并没有进入消息页面获取消息，那么这个数据在数据库中是还没有找到的
			if (msgIdQuene.contains(msgId)) {
				fireSysInformComing(context);
				return;
			} else {
				msgIdQuene.add(msgId);
				NotificationMessage message = Database.findNotifycationById(context, msgId);
				if (message != null) {
					fireSysInformComing(context);
					return; // 已经有这个消息，不用刷新消息数
				}
			}
		}

		unreadSysInformNum++;
		cacheBean.putInt(context, CacheKeyConstants.KEY_MSG_NUM, unreadSysInformNum);
		unreadCount++;

		fireSysInformComing(context);
	}

	/**
	 * @param context
	 */
	private void fireSysInformComing(Context context) {
		// 判断app当前是否在显示
		ActivityManager manager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> runningTasks = manager.getRunningTasks(1);
		RunningTaskInfo cinfo = runningTasks.get(0);

		// 通知对应页面刷新数据
		if (context.getPackageName().equals(cinfo.topActivity.getPackageName())) {
			Intent intent = new Intent();
			intent.setAction(Constants.ACTION_NEW_SYS_INFORM);
			context.sendBroadcast(intent);
		}
	}

	/***
	 * 系统通知未读数减一
	 * 
	 * @param context
	 */
	public void decreaseSysInforNum(Context context) {
		unreadSysInformNum--;
		cacheBean.putInt(context, CacheKeyConstants.KEY_MSG_NUM,
				unreadSysInformNum);
		unreadCount--;
	}
	
	public void cleanPrimaryNum(){
		unreadPrimaryLetterNum = -1;
	}
	
	public void cleanSysinformNum(Context context){
		cacheBean.putInt(context, CacheKeyConstants.KEY_MSG_NUM, 0);
		unreadSysInformNum = -1;
	}

	public void reset() {
		unreadCount = -1;
		unreadPrimaryLetterNum = -1;

		unreadSysInformNum = -1;
		instance = null;
	}

	public interface Listener{
		void refreshUnderNum(int num);
	}
}
