/**
 * 
 */
package com.bloomlife.videoapp.common.util;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 *   数据库操作公共类
 *
 * @author <a href="mailto:xiai.fei@gmail.com">xiai_fei</a>  
 *
 * @date 2015-6-15 上午9:44:32
 *
 * @organization bloomlife  
 * @version 1.0
 */
public class DataBaseUtils {

	public static  boolean checkTableExit(SQLiteDatabase db,String tableName){
		Cursor cursor = null;
		try {
			String sql = "SELECT COUNT(*) AS c FROM sqlite_master WHERE type ='table' AND name ='"
					+ tableName+ "' ";
			cursor = db.rawQuery(sql, null);
			if (cursor != null && cursor.moveToNext()) {
				int count = cursor.getInt(0);
				if (count > 0) {
					return true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null)
				cursor.close();
			cursor = null;
		}

		return false;
	}
	
}
