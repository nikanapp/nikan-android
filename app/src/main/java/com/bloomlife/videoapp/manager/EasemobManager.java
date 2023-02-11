/**
 * 
 */
package com.bloomlife.videoapp.manager;

import android.util.Log;

import com.easemob.chat.EMChatManager;
import com.easemob.exceptions.EaseMobException;

/**
 * 
 * 	环信相关操作
 * 
 * @author <a href="mailto:xiai.fei@gmail.com">xiai_fei</a>  
 *
 * @date 2014-12-17  下午12:08:41
 */
public class EasemobManager {
	
	private static final String TAG = "EasemobManager";

	public boolean reigster(String username ,String pwd){
		 try {
			EMChatManager.getInstance().createAccountOnServer(username, pwd);
		} catch (EaseMobException e) {
			Log.e(TAG, " 环信用户注册失败",e);
			return false ;
		}
		 return true ;
	}
}
