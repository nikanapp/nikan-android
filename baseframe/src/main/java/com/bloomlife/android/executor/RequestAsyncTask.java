/**
 * 
 */
package com.bloomlife.android.executor;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.bloomlife.android.R;
import com.bloomlife.android.bean.BaseMessage;
import com.bloomlife.android.bean.ProcessResult;
import com.bloomlife.android.business.BusinessProcessor;

/**
 * 
 * 	自动实现了网络请求，返回结果的获取。一般情况下，只需要在onCheckPostExecute中处理返回值就可以了。
 * 
 * 	如果没有连接网络，会自动弹出网络设置对话框。异步任务将会被取消。
 * 
 * @author <a href="mailto:xiai.fei@gmail.com">xiai_fei</a>  
 *
 * @date 2014-7-22  下午10:07:05
 */
public abstract  class RequestAsyncTask <T extends ProcessResult> extends  AsyncTask<Void, Void, T>{
	
	private static final String TAG = "RequestAsyncTask";
	
	private BusinessProcessor businessProcessor;
	
	protected BaseMessage baseMessage;
	
	private Class<T> result ;
	
	private boolean isShowFailure = true;
	
	public RequestAsyncTask(Activity activity, BaseMessage baseMessage) {
		this(activity , baseMessage , false);
	}
	
	public RequestAsyncTask(Activity activity,boolean showFailure, BaseMessage baseMessage) {
		this(activity , baseMessage , false);
		this.isShowFailure = showFailure;
	}
	
	public RequestAsyncTask(Activity activity , BaseMessage baseMessage,boolean dialog) {
		super(activity,dialog);
		businessProcessor = new BusinessProcessor(activity);
		this.baseMessage = baseMessage;
		result =(Class<T>) getGenericType(0);
	}
	
	
	 public Class<?> getGenericType(int index) {
		  Type genType = getClass().getGenericSuperclass();
		  if (!(genType instanceof ParameterizedType)) {
		   return Object.class;
		  }
		  Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
		  if (index >= params.length || index < 0) {
		   throw new RuntimeException("Index outof bounds");
		  }
		  if (!(params[index] instanceof Class)) {
		   return Object.class;
		  }
		  return (Class<?>) params[index];
		}


	@Override
	protected void onSafePostExecute(T result) {
		if (result != null&&ProcessResult.SUC==result.getCode()) {
			onCheckPostExecute(result);
		}else{
			onFailure(result);
		}
		onFinally();
	}
	
	protected void onFailure(ProcessResult result){
		Log.e(TAG, " 业务请求： "+baseMessage.getMsgCode()+" 失败");
		if(!isShowFailure ) return ;
//		act.get().runOnUiThread(new Runnable(){
//			public void run(){  //TODO 有用
//				Toast toast = new Toast(act.get());
//				View view = LayoutInflater.from(act.get()).inflate(R.layout.view_msg_tips,null);
//				toast.setView(view);
//				toast.setDuration(Toast.LENGTH_SHORT);
//				toast.setGravity(Gravity.CENTER, 0, 0);
//				toast.show();
//			}}
//		);
	}
	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public void execute(){
		executeOnExecutor(THREAD_POOL_EXECUTOR);
	}
	
	/***
	 * 		已经对返回值进行了检查判断，执行成功才会掉这个方法的
	 * @param result
	 */
	protected abstract  void onCheckPostExecute(T result) ;


	@SuppressWarnings("unchecked")
	@Override
	protected T doInBackground(Void... params) {
		return (T) businessProcessor.doBusinessRequest(baseMessage,result);
	}

}
