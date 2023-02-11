/**
 * 
 */
package com.bloomlife.android.framework;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View.OnClickListener;
import android.view.Window;

/**
 * @author <a href="mailto:xiai.fei@gmail.com">xiai_fei</a>  
 *
 * @date 2014-7-27  下午5:11:55
 */
public abstract class AbstractActivity extends Activity implements OnClickListener{
	
	protected   FinishRecevier recevier ;
	
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
	}


	
	protected abstract void initUi();
	
	protected abstract void initData(Bundle bundle);
		
	/****
	 *  只能注册一个关闭事件。如果已经注册了的话，会直接返回
	 * @param action
	 */
	protected void setFinishRecevier(String action){
		if(recevier!=null) return ;
		  //关闭广播
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(action);
        recevier =new FinishRecevier();
        this.registerReceiver(recevier, intentFilter);
	}
	
	
	private class FinishRecevier extends BroadcastReceiver{
		
		@Override
		public void onReceive(Context context, Intent intent) {
				finish();
		}
	}

	@Override
	public void finish() {
		super.finish();
		if(recevier!=null){
			unregisterReceiver(recevier); //这个必须在前面，否则的话会死循环
			recevier = null ;
		}
	}
	
}
