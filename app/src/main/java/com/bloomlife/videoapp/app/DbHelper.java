/**
 * 
 */
package com.bloomlife.videoapp.app;

import java.io.File;
import java.util.Date;
import java.util.List;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.bloomlife.videoapp.common.util.Utils;
import com.bloomlife.videoapp.manager.VideoFileManager;
import com.bloomlife.videoapp.model.ChatBean;
import com.bloomlife.videoapp.model.ConversationMessage;
import com.bloomlife.videoapp.model.DbStoryVideo;
import com.bloomlife.videoapp.model.NotificationMessage;
import com.bloomlife.videoapp.model.Video;
import com.hyphenate.chat.EMMessage;

/**
 * 	视频操作帮助类，实现异步处理
 * 
 * @author <a href="mailto:xiai.fei@gmail.com">xiai_fei</a>  
 *
 * @date 2014-11-14  上午11:18:29
 */
public class DbHelper {

	private static final String TAG = "DbHelper";
	
	public static void saveVideo(final Context context ,final Video video){
		AppContext.EXECUTOR_SERVICE.execute(new Runnable() {
			
			@Override
			public void run() {
				Database.writeVideo(context, video);
			}
		});
	}
	
	public static void saveVideoList(final Context context, final List<Video> videoList){
		AppContext.EXECUTOR_SERVICE.execute(new Runnable() {
			
			@Override
			public void run() {
				for (Video video:videoList){
					Database.writeVideo(context, video);
				}
			}
		});
	}

	public static void syncVideoList(final Context context, final List<Video> saveList, final List<Video> deleteList){
		AppContext.EXECUTOR_SERVICE.execute(new Runnable() {

			@Override
			public void run() {
				if (!Utils.isEmpty(saveList)){
					for (Video v:saveList){
						Database.writeVideo(context, v);
					}
				}

				if (!Utils.isEmpty(deleteList)){
					for (Video v:deleteList){
						Database.deleteVideo(context, v);
						Utils.deleteVideoFile(context, v);
					}
				}
			}
		});
	}

	public static void syncStoryVideoList(final Context context, final List<DbStoryVideo> saveList, final List<DbStoryVideo> deleteList){
		AppContext.EXECUTOR_SERVICE.execute(new Runnable() {

			@Override
			public void run() {
				if (!Utils.isEmpty(saveList)){
					for (DbStoryVideo v:saveList){
						Database.writeStoryVideo(context, v);
					}
				}

				if (!Utils.isEmpty(deleteList)){
					for (DbStoryVideo v:deleteList){
						Database.deleteStoryVideo(context, v);
						Utils.deleteStoryVideoFile(context, v);
					}
				}
			}
		});
	}

	public static void updateStoryVideo(final Context context, final DbStoryVideo video){
		AppContext.EXECUTOR_SERVICE.execute(new Runnable() {

			@Override
			public void run() {
				Database.updateStoryVideo(context, video);
			}
		});
	}

	public static void readStoryVideoList(final Context context, final String userId, final QueryCallback<DbStoryVideo> callback){
		AppContext.EXECUTOR_SERVICE.execute(new Runnable() {

			@Override
			public void run() {
				callback.setResult(Database.readUserStoryVideos(context, userId));
			}
		});
	}

	public static void deleteStoryVideo(final Context context, final DbStoryVideo video){
		AppContext.EXECUTOR_SERVICE.execute(new Runnable() {
			@Override
			public void run() {
				Database.deleteStoryVideo(context, video);
			}
		});
	}

	public static void deleteStoryVideos(final Context context, final List<DbStoryVideo> videos){
		AppContext.EXECUTOR_SERVICE.execute(new Runnable() {
			@Override
			public void run() {
				for (DbStoryVideo video:videos){
					Database.deleteStoryVideo(context, video);
				}
			}
		});
	}

	public static void deleteStoryVideos(final Context context, final String[] ids){
		if (ids == null) return;
		AppContext.EXECUTOR_SERVICE.execute(new Runnable() {
			@Override
			public void run() {
				for (String id:ids){
					Database.deleteStoryVideoById(context, id);
				}
			}
		});
	}

	public static void queryUploadFailStoryVideo(final Context context, final String userId, final QueryCallback<DbStoryVideo> callback){
		AppContext.EXECUTOR_SERVICE.execute(new Runnable() {
			@Override
			public void run() {
				callback.setResult(Database.readUploadFailStoryVideo(context, userId));
			}
		});
	}
	
	public static void updateVideo(final Context context,final  Video video){
		AppContext.EXECUTOR_SERVICE.execute(new Runnable() {
			
			@Override
			public void run() {
				Database.updateVideo(context, video);
			}
		});
	}
	
	public static void updateVideoList(final Context context,final  List<Video> videoList){
		AppContext.EXECUTOR_SERVICE.execute(new Runnable() {
			
			@Override
			public void run() {
				for (Video video : videoList){
					Database.updateVideo(context, video);
				}
			}
		});
	}
	
	public static void readVideoList(final Context context, final String userId, final DbHelperCallback callback){
		AppContext.EXECUTOR_SERVICE.execute(new Runnable() {
			
			@Override
			public void run() {
				callback.onVideoList(Database.readUserVideos(context, userId));
			}
		});
	}
	
	public static void deleteVideo(final Context context, final Video video){
		AppContext.EXECUTOR_SERVICE.execute(new Runnable() {
			
			@Override
			public void run() {
				Database.deleteVideo(context, video);
			}
		});
	}
	
	public static void deleteVideoById(final Context context, final Integer id){
		AppContext.EXECUTOR_SERVICE.execute(new Runnable() {
			
			@Override
			public void run() {
				Database.deleteVideoById(context, id);
			}
		});
	}
	
	public static void deleteVideoByVideoId(final Context context, final String videoId){
		AppContext.EXECUTOR_SERVICE.execute(new Runnable() {
			
			@Override
			public void run() {
				Database.deleteVideoByVideoId(context, videoId);
			}
		});
	}
	
	public static void readMessageList(final Context context,final String userId, final QueryCallback<ConversationMessage> queryCallback){
		AppContext.EXECUTOR_SERVICE.execute(new Runnable() {
			
			@Override
			public void run() {
				List<ConversationMessage> result = Database.readMessageList(context, userId);
				if (queryCallback != null)
					queryCallback.setResult(result);
			}
		});
	}
	
	public static void deleteMessageById(final Context context, final Integer id){
		AppContext.EXECUTOR_SERVICE.execute(new Runnable() {
			
			@Override
			public void run() {
				Database.deleteMessageById(context, id);
			}
		});
	}
	
	public static void deleteMessageByOtherId(final Context context, final String otherId , final String videoId){
		AppContext.EXECUTOR_SERVICE.execute(new Runnable() {
			
			@Override
			public void run() {
				Database.deleteMessageOtherId(context, otherId,videoId);
				Database.deleteChatOtherId(context, otherId, videoId);
			}
		});
	}
	
	public static void updateMessage(final Context context,final  ConversationMessage message){
		AppContext.EXECUTOR_SERVICE.execute(new Runnable() {
			
			@Override
			public void run() {
//				message.setUpdateTime(new Date());
				Database.updateMessage(context, message);
			}
		});
	}
	
	public static void saveMessage(final Context context ,final ConversationMessage message){
		AppContext.EXECUTOR_SERVICE.execute(new Runnable() {
			
			@Override
			public void run() {
				message.setUpdateTime(new Date());
				Database.writeMessage(context, message);
			}
		});
	}
	
	public static void updateNotification(final Context context, final NotificationMessage message){
		AppContext.EXECUTOR_SERVICE.execute(new Runnable() {
			
			@Override
			public void run() {
				Database.updateNotification(context, message);
			}
		});
	}

	public static void updateNotificationList(final Context context, final List<NotificationMessage> messages){
		AppContext.EXECUTOR_SERVICE.execute(new Runnable() {

			@Override
			public void run() {
				for (NotificationMessage m:messages){
					Database.updateNotification(context, m);
				}
			}
		});
	}
	
	public static void saveNotification(final Context context, final List<NotificationMessage> list){
		if (context == null) return;
		AppContext.EXECUTOR_SERVICE.execute(new Runnable() {
			
			@Override
			public void run() {
				for (NotificationMessage message:list){
					Database.saveNotification(context.getApplicationContext(), message);
				}
			}
		});
	}
	
	public static void readNotification(final Context context, final String userId, final QueryCallback<NotificationMessage> callback){
		AppContext.EXECUTOR_SERVICE.execute(new Runnable() {
			
			@Override
			public void run() {
				callback.setResult(Database.readNotification(context, userId));
			}
		});
	}
	
	public static void deleteNotification(final Context context, final String msgid){
		AppContext.EXECUTOR_SERVICE.execute(new Runnable() {
			
			@Override
			public void run() {
				Database.deleteNotification(context, msgid);
			}
		});
	}
	
	public static void deleteAllNotification(final Context context){
		AppContext.EXECUTOR_SERVICE.execute(new Runnable() {
			
			@Override
			public void run() {
				Database.deleteAllNotification(context);
			}
		});
	}
	
	/***
	 * 更新用户的视频对话bean状态为已读。
	 * @param context
	 * @param from
	 * @param videoId
	 */
	public static void updateUserVideoChatToRead(final Context context, final String from ,final String videoId){
		AppContext.EXECUTOR_SERVICE.execute(new Runnable() {
			
			@Override
			public void run() {
				Database.setUserVideoChatToRead(context, from , videoId);
			}
		});
	}
	
	public static ConversationMessage readMessage(final Context context ,final String otherId){
		return Database.readMessage(context, otherId);
	}
	public static ConversationMessage readMessage(final Context context ,final String otherId , final String videoId){
		return Database.readMessage(context, otherId , videoId);
	}
	
	public static void deleteAllMessage(final Context context){
		AppContext.EXECUTOR_SERVICE.execute(new Runnable() {
			
			@Override
			public void run() {
				Log.i("chatbean", "deleteAllMessage");
				Database.deleteAllMessage(context);
			}
		});
	}
	
	public interface DbHelperCallback{
		void onVideoList(List<Video> videoList);
	}
	
	public interface QueryCallback<T>{
		void setResult(List<T> result);
	}
	
	public interface Callback{
		void complete();
	}
	
	public static void updateChat(final Context context,final  ChatBean chatBean){
		AppContext.EXECUTOR_SERVICE.execute(new Runnable() {
			
			@Override
			public void run() {
				Log.e("chatbean", "update chat ,statue = "+chatBean.getSendSatus());
				Database.updateChatBean(context, chatBean);
			}
		});
	}
	
	public static void deleteAllChat(final Context context){
		AppContext.EXECUTOR_SERVICE.execute(new Runnable() {
			
			@Override
			public void run() {
				Log.i("chatbean", "deleteAllChat");
				Database.deleteAllChat(context);
			}
		});
	}

	public static void updateAllChatToRead(final Context context, final Callback callback){
		AppContext.EXECUTOR_SERVICE.execute(new Runnable(){

			@Override
			public void run() {
				List<ChatBean> chats = Database.findUnreadChat(context);
				for (ChatBean c:chats){
					c.setStatus(ChatBean.STATUS_READ);
					Database.updateChatBean(context, c);
				}
				if (callback != null)
					callback.complete();
			}
			
		});
	}
	
	public static void saveReciveChat(final Context context, final ChatBean chatBean, final Callback callback){
		AppContext.EXECUTOR_SERVICE.execute(new Runnable() {
			
			@Override
			public void run() {
				if (TextUtils.isEmpty(chatBean.getUserName()))
					saveFacelessMsg();
				else
					saveRealNameMsg();
			}

			private void saveRealNameMsg(){
				Database.saveChat(context, chatBean);
				if (callback != null)
					callback.complete();
			}

			private void saveFacelessMsg(){
				List<ChatBean> chatList = Database.findAllChat(context, chatBean.getFromUser(), chatBean.getVideoId());
				if (chatList == null || chatList.isEmpty()){
					// 如果为空，说明是对方第一次发信息来，加入提示。
					Database.saveChat(context, ChatBean.makeTipsChatBean(ChatBean.WAIT_MY_MSG, chatBean.getFromUser(), chatBean.getVideoId(), chatBean.getCity()));
					chatBean.setWaitStatus(ChatBean.WAIT_MY_MSG);
					Database.saveChat(context, chatBean);
				} else {
					// 找到我最后发的一条消息
					if (!finsMyMsg(context, chatBean, chatList)){
						// 如果找不到我发的信息
						chatBean.setWaitStatus(ChatBean.WAIT_MY_MSG);
						Database.saveChat(context, chatBean);
					}
				}
				if (callback != null)
					callback.complete();
			}
			
			private boolean finsMyMsg(Context context, ChatBean chatBean, List<ChatBean> chatList){
				boolean hasMyMsg = false;
				for (int i=chatList.size()-1; i>0; i--){
					ChatBean cd = chatList.get(i);
					int or = EMMessage.Direct.SEND.ordinal();
					if (cd.getDirect() != or  && cd.getWaitStatus() == ChatBean.WAIT_OK){
						// 如果是不用等待
						chatBean.setWaitStatus(ChatBean.WAIT_OK);
						return false;
					} else if (cd.getDirect() == or && cd.getWaitStatus() == ChatBean.WAIT_OTHER_MSG){
						// 如果是等待对方回复
						chatBean.setWaitStatus(ChatBean.WAIT_OK);
						chatBean.setFromUser(chatBean.getFromUser());
						Database.saveChat(context, chatBean);
						Database.saveChat(context, ChatBean.makeTipsChatBean(ChatBean.WAIT_OK, chatBean.getFromUser(), chatBean.getVideoId(), chatBean.getCity()));
						return true;
					} else if (cd.getDirect() == or){
						hasMyMsg = true;
					}
				}
				return hasMyMsg;
			}
		});
	}
}
