/**
 * 
 */
package com.bloomlife.android.framework;

import java.lang.ref.WeakReference;

import android.app.Activity;
import android.os.Message;

/**
 * @author <a href="mailto:xiai.fei@gmail.com">xiai_fei</a>  
 *
 * @date 2014-11-27  上午10:47:39
 */
public abstract class BaseHandler<T extends Activity> extends android.os.Handler{
	
	private WeakReference<T> mReference;
	
	public BaseHandler(T  activity){
		mReference = new WeakReference<T>(activity);
	}
	
	@Override
	public void handleMessage(Message msg) {
		T activity = mReference.get();
		if (activity != null){
			handleMessage(msg,activity);
		}
		super.handleMessage(msg);
	}
	
	protected abstract void handleMessage(Message msg , T activty);

}
