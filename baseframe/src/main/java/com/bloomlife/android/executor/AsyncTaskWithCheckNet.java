/**
 * 
 */
package com.bloomlife.android.executor;

import android.app.Activity;

import com.bloomlife.android.network.NetUtils;

/**
 * 	带有检测网络状态的异步任务
 * 
 * @author <a href="mailto:xiai.fei@gmail.com">xiai_fei</a>  
 *
 * @date 2014-6-26  下午6:25:11
 */
public abstract class AsyncTaskWithCheckNet<Params> extends AsyncCheckResultTask<Params>{

	public AsyncTaskWithCheckNet(Activity activity) {
		super(activity);
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		if (!NetUtils.checkNet(act.get())) {
			if(pdialog!=null)pdialog.dismiss();
			this.cancel(true);
		}
	}
	

	
	
	
}

