/**
 * 
 */
package com.bloomlife.android.executor;

import android.app.Activity;

import com.bloomlife.android.R;
import com.bloomlife.android.bean.ProcessResult;

/**
 * @author <a href="mailto:xiai.fei@gmail.com">xiai_fei</a>  
 *
 * @date 2014-5-23  上午11:33:41
 */
public abstract class AsyncCheckResultTask<Params> extends AsyncTask<Params, Void, ProcessResult>{

	public AsyncCheckResultTask(Activity activity) {
		super(activity);
	}
	
	public AsyncCheckResultTask(Activity activity,boolean showDialog) {
		super(activity,showDialog);
	}
	

	@Override
	protected void onSafePostExecute(ProcessResult result) {
		if (result != null&&ProcessResult.SUC==result.getCode()) {
			onCheckPostExecute(result);
		}else{
		}
	}
	

	protected abstract  void onCheckPostExecute(ProcessResult result) ;
}
