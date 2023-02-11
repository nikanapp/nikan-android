/**
 * 
 */
package com.bloomlife.android.business;

import android.app.Activity;

import com.bloomlife.android.bean.BaseMessage;
import com.bloomlife.android.bean.ProcessResult;
import com.bloomlife.android.executor.RequestAsyncTask;

/**
 * 
 * 	一个空的异步任务实现，通过basemessage来实现不同业务的区分
 * 
 * @author <a href="mailto:xiai.fei@gmail.com">xiai_fei</a>  
 *
 * @date 2014-9-6  下午5:01:06
 */
public class BusinessTask extends RequestAsyncTask<ProcessResult>{

	public BusinessTask(Activity activity, BaseMessage baseMessage) {
		super(activity, baseMessage);
	}
	
	public BusinessTask(Activity activity, BaseMessage baseMessage , boolean isShowFailure) {
		super(activity, isShowFailure ,baseMessage);
	}
	
	@Override
	protected void onCheckPostExecute(ProcessResult result) {
	}

	@Override
	protected void onFailure(ProcessResult result) {}
	
}