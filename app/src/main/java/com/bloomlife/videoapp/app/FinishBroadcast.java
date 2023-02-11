/**
 * 
 */
package com.bloomlife.videoapp.app;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * @author <a href="mailto:xiai.fei@gmail.com">xiai_fei</a>  
 *
 * @date 2014-12-31  下午7:25:52
 */
public class FinishBroadcast extends BroadcastReceiver{
	
	private Activity activity ;
	
	public FinishBroadcast(Activity activity){
		this.activity = activity;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		activity.finish();
	}

}
