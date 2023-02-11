/**
 * 
 */
package com.bloomlife.android.executor;

import java.lang.ref.WeakReference;

import android.app.Activity;
import android.app.Dialog;

import com.bloomlife.android.view.CustomProgressDialog;


/**
 * @author <a href="mailto:xiai.fei@gmail.com">xiai_fei</a>  
 *
 * @date 2014-5-23  上午11:19:35
 */
public abstract class AsyncTask<Params, Progress, Result> extends android.os.AsyncTask<Params, Progress, Result>{

	protected WeakReference<Activity> act ;
	
	public AsyncTask(Activity activity){
		this.act = new WeakReference<Activity>(activity);
	}
	
	protected Dialog pdialog;
	
	/**
	 * 提供执行请求时显示请求提示dialog，并且执行完毕之后自动消息。建议有dialog的都用这个。
	 * @param activity
	 * @param showDialog
	 */
	public AsyncTask(Activity activity, boolean showDialog) {
		this(activity);
		if(showDialog){
			pdialog = CustomProgressDialog.createDialog(activity);
			pdialog.show();
		}
	}
	
	
	@Override
	protected Result doInBackground(Params... params) {
		return null;
	}

	@Override
	protected void onPostExecute(Result result) {
		try{
			if(act.get()==null||act.get().isFinishing()) return ;
			if(pdialog!=null)pdialog.dismiss();
			onSafePostExecute(result);
		}finally{onFinally();}
	}
	
	protected void onFinally(){}

	/***
	 * 执行安全的异步线程返回结果。避免当前处理由于activity的关闭等导致异常
	 * @param result
	 */
	protected void onSafePostExecute(Result result) {
	}
}
