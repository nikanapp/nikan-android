/**
 * 
 */
package com.bloomlife.android.framework;

import java.util.List;

import net.tsz.afinal.FinalDb;
import net.tsz.afinal.FinalDb.DbUpdateListener;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.bloomlife.android.bean.AnalyseRecord;

/**
 *   数据分析相关的数据库
 *
 * @author <a href="mailto:xiai.fei@gmail.com">xiai_fei</a>  
 *
 * @date 2015-6-15 上午9:42:58
 *
 * @organization bloomlife  
 * @version 1.0
 */
public class AnalyseDatabase {

	public static final String TAG = "AnalyseDatabase";
	public static final boolean DATABASE_DEBUG = true;
	public static final String DATABASE_NAME = "AnalyseDatabase";
	public static final int DATABASE_VERSION = 1;
	
	
	public enum ChatCacheData { EMPTY_OTHER, NEW_VERSION, OLD_VERSION }

	static FinalDb getDb(Context context){
		return FinalDb.create(context, DATABASE_NAME, DATABASE_DEBUG, DATABASE_VERSION, dbUpdateListener);
	}
	
	private static DbUpdateListener dbUpdateListener = new DbUpdateListener() {
		
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			try {
			
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	};
	
	public static void saveAnalyse(Context context ,AnalyseRecord record){
		if(context==null){
			Log.e(TAG, "occur error on save analyse record ,context is null ");
			return ;
		}
		
		FinalDb db = getDb(context);
		db.save(record);
		if(record.getApiName().equals("9000"))Log.e(TAG, " save 9000 !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
	}
	
	public static List<AnalyseRecord> findAnalyses(Context context){
		if(context==null){
			Log.e(TAG, "occur error on save analyse record ,context is null ");
			return null;
		}
		FinalDb db = getDb(context);
		return db.findAllByWhere(AnalyseRecord.class, " status = "+AnalyseRecord.STATUS_NOT_UOLOAD);
	}
	
	/***
	 * 更新所有的分析记录状态为已经上传
	 * @param context
	 */
	public static void updateAnalyseStatus(Context context){
		if(context==null){
			Log.e(TAG, "occur error on save analyse record ,context is null ");
			return ;
		}
		FinalDb db = getDb(context);
		String updateSql = " update tb_analyse set status = "+AnalyseRecord.STATUS_UOLOADED;
		db.executeUpdateSql(updateSql);
	}
	
	
	public static void deleteUpload(Context context){
		if(context==null){
			Log.e(TAG, "occur error on save analyse record ,context is null ");
			return ;
		}
		FinalDb db = getDb(context);
		db.deleteAll(AnalyseRecord.class);
	}
}
