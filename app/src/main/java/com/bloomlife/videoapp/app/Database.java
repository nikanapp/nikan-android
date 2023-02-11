package com.bloomlife.videoapp.app;


import static com.bloomlife.videoapp.common.util.DataBaseUtils.*;

import java.util.Collections;
import java.util.List;

import net.tsz.afinal.FinalDb;
import net.tsz.afinal.FinalDb.DbUpdateListener;
import net.tsz.afinal.db.table.TableInfo;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import com.bloomlife.android.bean.CacheBean;
import com.bloomlife.android.common.util.Utils;
import com.bloomlife.android.log.Logger;
import com.bloomlife.videoapp.R;
import com.bloomlife.videoapp.model.ChatBean;
import com.bloomlife.videoapp.model.ConversationMessage;
import com.bloomlife.videoapp.model.DbStoryVideo;
import com.bloomlife.videoapp.model.NotificationMessage;
import com.bloomlife.videoapp.model.NotificationUserInfo;
import com.bloomlife.videoapp.model.Video;
import com.easemob.chat.EMMessage;
/**
 * 数据库类
 *   
 * @notice 这里十分需要注意不同版本的数据库的升级。需要针对不同的版本编写升级脚本。否则的话会导致不同版本之间的不可兼容。
 *
 * @author <a href="mailto:xiai.fei@gmail.com">xiai_fei</a>  
 *
 * @date 2015-3-23 上午10:19:49
 *
 * @organization bloomlife  
 * @version 1.0
 */
public class Database {

	public static final String TAG = "Database";
	public static final boolean DATABASE_DEBUG = true;
	public static final String DATABASE_NAME = "videoapp";
	public static final int DATABASE_VERSION = 19;

	public enum ChatCacheData { EMPTY_OTHER, NEW_VERSION, OLD_VERSION }

	static FinalDb getDb(Context context){
		return FinalDb.create(context, DATABASE_NAME, DATABASE_DEBUG, DATABASE_VERSION, dbUpdateListener);
	}
	
	private static final String VSERSION2_UPGRADE_SQL = " ALTER TABLE tb_chatbean ADD COLUMN sendSatus INT(4) DEFAULT 0 ; " +
			" ALTER TABLE tb_chatbean ADD COLUMN emmessageId VARCHAR(15)  ";
	

	private static final String VSERSION3_UPGRADE_SQL = " ALTER TABLE tb_video ADD COLUMN width INT(4) DEFAULT 0 ; " +
			" ALTER TABLE tb_video ADD COLUMN height INT(4) DEFAULT 0  "+
			" ALTER TABLE tb_video ADD COLUMN rotate INT(4) DEFAULT 0  ";
	
	private static final String VSERSION4_CHATBEAN_UPGRADE_SQL = "ALTER TABLE tb_chatbean ADD COLUMN imagePath VARCHAR(256);";
	
	private static final String VERSION4_MESSAGE_UPGRADE_SQL = "ALTER TABLE tb_message ADD COLUMN imagePath VARCHAR(256);";
	
	private static final String VERSION5_VIDEO_UPGRADE_SQL = "ALTER TABLE tb_video ADD COLUMN sex INT(4) DEFAULT 0;";
	
	private static final String VERSION5_CHATBEAN_UPGRADE_SQL = "ALTER TABLE tb_chatbean ADD COLUMN sex INT(4) DEFAULT 0;";
	
	private static final String VERSION5_MESSAGE_UPGRADE_SQL = "ALTER TABLE tb_message ADD COLUMN sex INT(4) DEFAULT 0;";
	
	private static final String VERSION6_MESSAGE_UPGRADE_SQL = "ALTER TABLE tb_message ADD COLUMN thumbnailUrl VARCHAR(256);";
	
	private static final String VSERSION6_CHATBEAN_UPGRADE_SQL = "ALTER TABLE tb_chatbean ADD COLUMN thumbnailUrl VARCHAR(256);";
	
	private static final String VSERSION7_CHATBEAN_UPGRADE_SQL = "ALTER TABLE tb_chatbean ADD COLUMN viewType INT(4) DEFAULT 0;";
	
	private static final String VSERSION8_CHATBEAN_WAIT_UPGRADE_SQL = "ALTER TABLE tb_chatbean ADD COLUMN waitStatus INT(4) DEFAULT -1 ;";  
	private static final String VSERSION8_CHATBEAN_WIDTH_UPGRADE_SQL = " ALTER TABLE tb_chatbean ADD COLUMN imageWidth INT(4) DEFAULT 0 ;";
	private static final String VSERSION8_CHATBEAN_HEIGHT_UPGRADE_SQL = " ALTER TABLE tb_chatbean ADD COLUMN imageHeight INT(4) DEFAULT 0;";
	
	private static final String VSERSION9_CHATBEAN_VOICE_DURATION_UPGRADE_SQL = "ALTER TABLE tb_chatbean ADD COLUMN voicePath VARCHAR(256);";  
	private static final String VSERSION9_CHATBEAN_VOICE_PATH_UPGRADE_SQL 	  = "ALTER TABLE tb_chatbean ADD COLUMN voiceDuration INT(4) DEFAULT 0;";
	private static final String VSERSION9_CHATBEAN_APP_VERSION_UPGRADE_SQL 	  = "ALTER TABLE tb_chatbean ADD COLUMN appVersion INT(4) DEFAULT 0;";
	
	private static final String VERSION10_VIDEO_UPGRADE_SQL = "ALTER TABLE tb_video ADD COLUMN createtime LONG DEFAULT 0;";
	private static final String VERSION10_CHATBEAN_RESEND_UPGRADE_SQL = "ALTER TABLE tb_chatbean ADD COLUMN reSendStatus INT(4) DEFAULT 0;";
	
	private static final String VERSION11_MESSAGE_UPGRADE_SQL = "ALTER TABLE com_bloomlife_videoapp_model_Message ADD COLUMN createtime LONG DEFAULT ";
	
	private static final String VERSION12_MESSAGE_UPGRADE_SQL = "ALTER TABLE com_bloomlife_videoapp_model_Message CHANGE COLUMN msgid msgid VARCHAR(32)  ";

	private static final String VERSION13_CHATBEAN_UPGRADE_SQL = "ALTER TABLE tb_chatbean ADD COLUMN city VARCHAR(128);";

	private static final String VERSION14_CHATBEAN_USER_NAME_UPGRADE_SQL = "ALTER TABLE tb_chatbean ADD COLUMN userName VARCHAR(128);";
	private static final String VERSION14_CHATBEAN_USER_ICON_UPGRADE_SQL = "ALTER TABLE tb_chatbean ADD COLUMN userIcon VARCHAR(128);";

	private static final String VERSION15_MESSAGE_USER_NAME_UPGRADE_SQL = "ALTER TABLE tb_message ADD COLUMN userName VARCHAR(128);";
	private static final String VERSION15_MESSAGE_USER_ICON_UPGRADE_SQL = "ALTER TABLE tb_message ADD COLUMN userIcon VARCHAR(128);";

	private static final String VERSION16_MESSAGE_USER_ID_UPGRADE_SQL = "ALTER TABLE tb_message ADD COLUMN userId VARCHAR(128) DEFAULT ";
	private static final String VERSION16_NOTIFICATION_USER_ID_UPGRADE_SQL = "ALTER TABLE com_bloomlife_videoapp_model_Message ADD COLUMN userId VARCHAR(128) DEFAULT ";

	private static final String VERSION17_NOTIFICATION_DROP_UPGRADE_SQL = "DROP TABLE com_bloomlife_videoapp_model_Message";
	private static final String VERSION18_CHATBEAN_USER_NAME_UPGRADE_SQL = "ALTER TABLE tb_chatbean ADD COLUMN userId VARCHAR(128);";
	
	private static DbUpdateListener dbUpdateListener = new DbUpdateListener() {
		
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			try {
				if(oldVersion==1){
					if(checkTableExit(db, TableInfo.get(ChatBean.class).getTableName()))
					db.execSQL(VSERSION2_UPGRADE_SQL);
				}
				if(oldVersion<3){
					if(checkTableExit(db, TableInfo.get(Video.class).getTableName()))
					db.execSQL(VSERSION3_UPGRADE_SQL);
				}
				if (oldVersion < 4){
					if (checkTableExit(db, TableInfo.get(ChatBean.class).getTableName()))
						db.execSQL(VSERSION4_CHATBEAN_UPGRADE_SQL);
					if (checkTableExit(db, TableInfo.get(ConversationMessage.class).getTableName()))
						db.execSQL(VERSION4_MESSAGE_UPGRADE_SQL);
				}
				if (oldVersion < 5){
					if (checkTableExit(db,TableInfo.get(Video.class).getTableName()))
						db.execSQL(VERSION5_VIDEO_UPGRADE_SQL);
					if (checkTableExit(db,TableInfo.get(ChatBean.class).getTableName()))
						db.execSQL(VERSION5_CHATBEAN_UPGRADE_SQL);
					if (checkTableExit(db,TableInfo.get(ConversationMessage.class).getTableName()))
						db.execSQL(VERSION5_MESSAGE_UPGRADE_SQL);
				}
				if (oldVersion < 6){
					if (checkTableExit(db, TableInfo.get(ChatBean.class).getTableName()))
						db.execSQL(VSERSION6_CHATBEAN_UPGRADE_SQL);
					if (checkTableExit(db, TableInfo.get(ConversationMessage.class).getTableName()))
						db.execSQL(VERSION6_MESSAGE_UPGRADE_SQL);
				}
				if (oldVersion < 7){
					if (checkTableExit(db, TableInfo.get(ChatBean.class).getTableName()))
						db.execSQL(VSERSION7_CHATBEAN_UPGRADE_SQL);
				}
				if (oldVersion < 8){
					if (checkTableExit(db, TableInfo.get(ChatBean.class).getTableName())){
						db.execSQL(VSERSION8_CHATBEAN_WAIT_UPGRADE_SQL);
						db.execSQL(VSERSION8_CHATBEAN_WIDTH_UPGRADE_SQL);
						db.execSQL(VSERSION8_CHATBEAN_HEIGHT_UPGRADE_SQL);
					}
				}
				if (oldVersion < 9){
					if (checkTableExit(db, TableInfo.get(ChatBean.class).getTableName())){
						db.execSQL(VSERSION9_CHATBEAN_VOICE_PATH_UPGRADE_SQL);
						db.execSQL(VSERSION9_CHATBEAN_APP_VERSION_UPGRADE_SQL);
						db.execSQL(VSERSION9_CHATBEAN_VOICE_DURATION_UPGRADE_SQL);
					}
				}
				if (oldVersion < 10){
					if (checkTableExit(db, TableInfo.get(ChatBean.class).getTableName())){
						db.execSQL(VERSION10_CHATBEAN_RESEND_UPGRADE_SQL);
					}
					if (checkTableExit(db, TableInfo.get(Video.class).getTableName())){
						db.execSQL(VERSION10_VIDEO_UPGRADE_SQL);
					}
				}
				
				if (oldVersion < 12){
					if (checkTableExit(db, TableInfo.get(NotificationMessage.class).getTableName())){
						db.execSQL(VERSION11_MESSAGE_UPGRADE_SQL+System.currentTimeMillis()+" ;");
					}
				}
				if (oldVersion < 13){
					if (checkTableExit(db, TableInfo.get(NotificationMessage.class).getTableName())){
						db.execSQL(VERSION12_MESSAGE_UPGRADE_SQL+System.currentTimeMillis()+" ;");
					}
				}
				if (oldVersion < 14){
					if (checkTableExit(db, TableInfo.get(ChatBean.class).getTableName())){
						db.execSQL(VERSION13_CHATBEAN_UPGRADE_SQL);
					}
				}
				if (oldVersion < 15) {
					if (checkTableExit(db, TableInfo.get(ChatBean.class).getTableName())){
						db.execSQL(VERSION14_CHATBEAN_USER_NAME_UPGRADE_SQL);
						db.execSQL(VERSION14_CHATBEAN_USER_ICON_UPGRADE_SQL);
					}
				}
				if (oldVersion < 16) {
					if (checkTableExit(db, TableInfo.get(ConversationMessage.class).getTableName())){
						db.execSQL(VERSION15_MESSAGE_USER_NAME_UPGRADE_SQL);
						db.execSQL(VERSION15_MESSAGE_USER_ICON_UPGRADE_SQL);
					}
				}
				if (oldVersion < 17){
					if (checkTableExit(db, TableInfo.get(ConversationMessage.class).getTableName())){
						db.execSQL(VERSION16_MESSAGE_USER_ID_UPGRADE_SQL + "'" + CacheBean.getInstance().getLoginUserId() + "'");
					}
					if (checkTableExit(db, TableInfo.get(NotificationMessage.class).getTableName())){
						db.execSQL(VERSION16_NOTIFICATION_USER_ID_UPGRADE_SQL+ "'" + CacheBean.getInstance().getLoginUserId() + "'");
					}
				}

				if (oldVersion < 18){
					if (checkTableExit(db, TableInfo.get(NotificationMessage.class).getTableName())){
						db.execSQL(VERSION17_NOTIFICATION_DROP_UPGRADE_SQL);
					}
				}

				if (oldVersion < 19){
					if (checkTableExit(db, TableInfo.get(ChatBean.class).getTableName())){
						db.execSQL(VERSION18_CHATBEAN_USER_NAME_UPGRADE_SQL);
					}
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	};
	
	

	static void writeVideo(final Context context, Video video){
		FinalDb db = getDb(context);
		save(db, video);
		Log.i(TAG, "保存视频实体成功");
	}
	
	static void updateVideo(final Context context, Video video){
		FinalDb db = getDb(context);
		db.update(video);
		Log.i(TAG, "更新视频实体 "+video+"成功");
	}
	
	static void deleteVideo(final Context context, Video video){
		FinalDb db = getDb(context);
		db.delete(video);
		Log.i(TAG, "删除视频实体成功");
	}
	
	static void deleteVideoById(final Context context, int id){
		FinalDb db = getDb(context);
		db.deleteById(Video.class, id);
		Log.i(TAG, "删除视频实体成功");
	}
	
	static void deleteVideoByVideoId(final Context context, String videoId){
		if(context==null) return ;
		FinalDb db = getDb(context);
		db.deleteByWhere(Video.class, "videoid=\"" + videoId + "\"");
		Log.i(TAG, "删除视频实体成功");
	}
	
	public static void deleteVideo(final Context context,String desc){
		FinalDb db = getDb(context);
		db.deleteByWhere(Video.class, " description = " + "\"" + desc + "\"");
		Log.i(TAG, "删除视频实体成功");
	}
	
	static void save(FinalDb db, Object entity){
		try{
			db.save(entity);
		} catch (SQLiteException e){
			Log.e(TAG, "执行数据库保存操作失败");
			e.printStackTrace();
		}
	}
	
	static <T>List<T> read(final Context context, Class<T> clazz){
		FinalDb db = getDb(context);
		return db.findAll(clazz, "uploadTime DESC");
	}
	
	public static List<Video> readVideoList(final Context context, Class<Video> clazz){
		FinalDb db = getDb(context);
		return db.findAll(clazz);
	}

	static List<Video> readUserVideos(final Context context, final String userId){
		return getDb(context).findAllByWhere(Video.class, "uid='" + userId + "'", "uploadTime DESC");
	}
	
	static Video findVideo(Context context ,Integer videoId){
		FinalDb db = getDb(context);
		return db.findById(videoId, Video.class);
	}
	
	public static int queryLastInsertId(Context context ,Class<?> clz){
		FinalDb db = getDb(context);
		return db.getLastSaveId(clz);
	}
	
	
	/** *********************************       私信相关的方法     *********************************************** */
	public static List<ConversationMessage> readMessageList(final Context context, final String userId){
		FinalDb db = getDb(context);
		return db.findAllByWhere(ConversationMessage.class, " userId = '" + userId + "'", " updatetime desc ");
//		return db.findAll(ConversationMessage.class, " updatetime desc ");
	}
	
	static void deleteMessageById(final Context context, int id){
		FinalDb db = getDb(context);
		db.deleteById(ConversationMessage.class, id);
		Log.i(TAG, "删除消息实体成功");
	}
	
	static void deleteMessageOtherId(final Context context, final String otherId ,String videoId){
		FinalDb db = getDb(context);
		db.deleteByWhere(ConversationMessage.class, " otherId = '" + otherId + "'" + " and videoId = '" + videoId + "'");
		Log.i(TAG, "删除消息实体成功");
	}
	
	static void deleteAllMessage(final Context context){
		FinalDb db = getDb(context);
		db.deleteAll(ConversationMessage.class);
		Log.i(TAG, "删除所有消息实体成功");
	}
	
	static void deleteChatOtherId(final Context context,  final String from ,String videoId){
		FinalDb db = getDb(context);
		db.deleteByWhere(ChatBean.class, " fromUser = '" + from + "' and  videoId = '" + videoId + "' ");
		Log.i(TAG, "删除消息实体成功");
	}
	
	static void deleteAllChat(final Context context){
		FinalDb db = getDb(context);
		db.deleteAll(ChatBean.class);
		Log.i(TAG, "删除所有消息成功");
	}
	
	static void updateMessage(final Context context, ConversationMessage message){
		FinalDb db = getDb(context);
		db.update(message, " otherId='" + message.getOtherId() + "' and  videoId = '" + message.getVideoId() + "'");
		Log.i(TAG, "更新消息实体 "+message+"成功");
	}
	
	public static void writeMessage(final Context context, ConversationMessage message){
		FinalDb db = getDb(context);
		save(db, message);
		Log.i(TAG, "保存消息实体成功");
	}
	
	static ConversationMessage readMessage(final Context context ,String otherId){
		FinalDb db = getDb(context);
		List<ConversationMessage> result = db.findAllByWhere(ConversationMessage.class, " otherId = '" + otherId+"'");
		return Utils.isEmptyCollection(result)?null:result.get(0);
	}
	

	static ConversationMessage readMessage(final Context context ,String otherId , String videoId){
		FinalDb db = getDb(context);
		List<ConversationMessage> result = db.findAllByWhere(ConversationMessage.class, " otherId = '" + otherId+"'"+" and videoId = '" + videoId+"'");
		return Utils.isEmptyCollection(result)?null:result.get(0);
	}
	
	public static void saveChat(final Context context , ChatBean chatBean){
		FinalDb db = getDb(context);
		save(db, chatBean);
		Log.i(TAG, "保存聊天内容实体成功");
	}
	
	public static List<ChatBean> findAllChat(final Context context, final String from, final String videoId){
		return getDb(context).findAllByWhere(ChatBean.class, " fromUser = '"+from+"' and  videoId = '"+videoId+"' " );
	}
	
	public static List<ChatBean> findTipType(final Context context, final String from, final String videoId, final int type){
		return getDb(context).findAllByWhere(ChatBean.class, " fromUser = '"+from+"' and  videoId = '"+videoId+"' and waitStatus="+type+" and viewType="+ChatBean.VIEW_TYPE_TIPS);
	}
	
	public static List<ChatBean> findUnreadChat(final Context context){
		return getDb(context).findAllByWhere(ChatBean.class, " status = "+ChatBean.STATUS_UNREAD);
	}
	
	public static List<ChatBean> loadChat(final Context context ,String from , String videoId ,int start , int offset){
		FinalDb db = getDb(context);
		String userId = CacheBean.getInstance().getLoginUserId();
		List<ChatBean> result =  db.findWhere(ChatBean.class,
				" fromUser = '" + from + "' and  videoId = '" + videoId + "' and userId='"+ userId +"'",
				" id desc ", start, offset);
		if(!Utils.isEmptyCollection(result))Collections.reverse(result);
		return result;
	}
	
	public static ChatCacheData checkChatBeanVersion(final Context context, int version, String from , String videoId){
		FinalDb db = getDb(context);
		List<ChatBean> chats = db.findAllByWhere(
				ChatBean.class, " appVersion >= " + version + " AND direct = " + EMMessage.Direct.RECEIVE.ordinal()
						+ " AND fromUser = '" + from + "' AND  videoId = '" + videoId + "' ");
		if (!chats.isEmpty())
			return ChatCacheData.NEW_VERSION;
		if (db.findAllByWhere(ChatBean.class, " appVersion >= "+version +" AND direct = "+EMMessage.Direct.RECEIVE.ordinal()
				+" AND fromUser = '"+from+"' AND  videoId = '"+videoId+"' ").isEmpty())
			return ChatCacheData.EMPTY_OTHER;
		return ChatCacheData.OLD_VERSION;
	}
	
	public static int unreadMessageConversation(Context context){
		FinalDb db = getDb(context);
		try {
			return db.count(ConversationMessage.class, " status="+ConversationMessage.STATUS_UNREAD);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0 ;
	}
	
	public static int unreadChatNum(Context context, String userId){
		FinalDb db = getDb(context);
		try {
			return db.count(ChatBean.class, " status="+ChatBean.STATUS_UNREAD+" and videoId<>'"+context.getString(R.string.custom_video_id)+"' and userId='"+userId+"'");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	public static void setUserVideoChatToRead(Context context , String from , String videoId ){
		FinalDb db = getDb(context);
		try {
			String sql = " update "+db.getTable(ChatBean.class)+" set status="+ChatBean.STATUS_READ+" where fromUser = '"+from+"' and  videoId='"+videoId+"'";
			db.executeUpdateSql(sql);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void saveNotification(final Context context, final NotificationMessage message){
		FinalDb db = getDb(context);
		Logger.d(TAG, "保存通知" + message.getMsgid());
		db.save(message);
		if (message.getExtra() != null){
			Logger.d(TAG, "保存通知用户信息" + message.getMsgid());
			message.getExtra().setMsgid(message.getMsgid());
			saveNotificationUserInfo(context, message.getExtra());
		}
	}

	private static void saveNotificationUserInfo(final Context context, final NotificationUserInfo info){
		getDb(context).save(info);
	}

	private static void deleteNotificationUserInfo(final Context context, final String messageId){
		getDb(context).deleteByWhere(NotificationUserInfo.class, "msgid='" + messageId + "'");
	}

	private static void deleteAllNotificationUserInfo(final Context context){
		getDb(context).deleteAll(NotificationUserInfo.class);
	}

	private static NotificationUserInfo findNotificationUserInfoById(final Context context, final String messageId){
		List<NotificationUserInfo> infoList = getDb(context).findAllByWhere(NotificationUserInfo.class, "msgid='" + messageId + "'");
		if (!Utils.isCollectionEmpty(infoList)){
			return infoList.get(0);
		} else {
			return null;
		}
	}
	
	/***
	 * 通过系统通知的消息id获取系统通知消息
	 * @param context
	 * @param messageId
	 * @return
	 */
	public static NotificationMessage findNotifycationById(final Context context, final String messageId){
		FinalDb db = getDb(context);
		List<NotificationMessage> messages = db.findAllByWhere(NotificationMessage.class, "msgid='"+messageId+"'");
		if (messages != null && !messages.isEmpty()){
			NotificationMessage m = messages.get(0);
			m.setExtra(findNotificationUserInfoById(context, messageId));
			return m;
		} else {
			return null;
		}
		
	}
	
	public static List<NotificationMessage> readNotification(final Context context, final String userId){
		List<NotificationMessage> messages = getDb(context).findAllByWhere(NotificationMessage.class, " userid = '" + userId + "'");
		// 设置每一个通知的用户信息
		for (NotificationMessage m:messages){
			m.setExtra(findNotificationUserInfoById(context, m.getMsgid()));
		}
		return messages;
	}
	
	public static void deleteNotification(final Context context, final String msgid){
		getDb(context).deleteByWhere(NotificationMessage.class, "msgid='" + msgid + "'");
		deleteNotificationUserInfo(context, msgid);
	}
	
	public static void updateNotification(final Context context, final NotificationMessage message){
		getDb(context).update(message, "msgid='" + message.getMsgid() + "'");
	}
	
	public static void deleteAllNotification(final Context context){
		getDb(context).deleteAll(NotificationMessage.class);
		deleteAllNotificationUserInfo(context);
	}
	
	
	private static final Byte[] chatLock = new Byte[0];

	public static void updateChatBean(Context context ,ChatBean bean){
		synchronized (chatLock) {
			Log.e("chatbean", "1 update chat ,statue = "+bean.getSendSatus());
			FinalDb db = getDb(context);
			try{
				db.update(bean);
			} catch (Exception e){Log.e(TAG, " occur error on update chat bean ");}
		}
	}

	static void writeStoryVideo(final Context context, DbStoryVideo video){
		FinalDb db = getDb(context);
		save(db, video);
		Log.i(TAG, "保存精选集视频实体成功");
	}

	static void updateStoryVideo(final Context context, DbStoryVideo video){
		FinalDb db = getDb(context);
		db.update(video);
		Log.i(TAG, "更新选集视频实体 "+video+"成功");
	}

	public static DbStoryVideo findStoryVideo(final Context context, String url){
		List<DbStoryVideo> videos = getDb(context).findAllByWhere(DbStoryVideo.class, "videouri='" + url + "'");
		if (!Utils.isCollectionEmpty(videos)){
			return videos.get(0);
		} else {
			return null;
		}
	}

	static void deleteStoryVideo(final Context context, DbStoryVideo video){
		getDb(context).delete(video);
		Log.i(TAG, "删除选集视频实体 " + video + "成功");
	}

	static void deleteStoryVideoById(Context context, String id){
		getDb(context).deleteByWhere(DbStoryVideo.class, " videoid='" + id + "'");
		Log.i(TAG, "删除选集视频实体 " + id + "成功");
	}

	static List<DbStoryVideo> readUserStoryVideos(final Context context, final String userId){
		return getDb(context).findAllByWhere(DbStoryVideo.class, "userId='" + userId + "'", "createtime DESC");
	}

	static List<DbStoryVideo> readUploadFailStoryVideo(final Context context, final String userId){
		return getDb(context).findAllByWhere(DbStoryVideo.class, "userId='" + userId + "' AND status<>"+DbStoryVideo.STATUS_UPLOAD_SUCCESS);
	}
}
