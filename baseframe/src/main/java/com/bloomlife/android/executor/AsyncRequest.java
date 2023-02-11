/**
 * 
 */
package com.bloomlife.android.executor;

import java.net.URL;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bloomlife.android.bean.BaseMessage;
import com.bloomlife.android.bean.ProcessResult;
import com.bloomlife.android.common.util.StringUtils;
import com.bloomlife.android.framework.AppContext;
import com.bloomlife.android.network.HttpProtocolEntry;
import com.bloomlife.android.network.NetUtils;

/**
 *   
 *
 * @author <a href="mailto:xiai.fei@gmail.com">xiai_fei</a>  
 *
 * @date 2015-6-19 下午7:13:01
 *
 * @organization bloomlife  
 * @version 1.0
 */
public class AsyncRequest implements Runnable{
	
	private RequestCallBack callBack ;
	
	private BaseMessage messsage ;
	
	private Map<String,String> bodyParams;
	
	private Class<?>clz ;
 	
	public AsyncRequest(BaseMessage message ,RequestCallBack callBack){
		this.messsage = message ;
		this.callBack = callBack;
	}
	public AsyncRequest(BaseMessage message ,Class<?>clz ,RequestCallBack callBack){
		this.messsage = message ;
		this.callBack = callBack;
		this.clz = clz;
	}
	
	public AsyncRequest(BaseMessage message ){
		this.messsage = message ;
	}
	
	public AsyncRequest(Map<String,String> bodyParams){
		this.messsage = new BaseMessage(){};
		this.bodyParams = bodyParams;
	}
	

	@Override
	public void run() {
		try {
			URL url = new URL(HttpProtocolEntry.URL);
			String content = messsage.getPostParamsStr(bodyParams);
			
			String result =NetUtils.getPostInputStream(null, url,messsage.getMsgCode(), content);
			if(callBack == null) return ;
			if(StringUtils.isEmpty(result)) callBack.onSuccess(null);
			if(clz!=null && callBack instanceof RequestWithFailureCallBack){
				((RequestWithClzCallBack)callBack).onFinish(JSON.parseObject(result, clz));
				return ;
			}
			
			Map<String,Object> object= (Map<String, Object>) JSON.parse(result);
			if((Integer)(object.get("resultCode")) == ProcessResult.SUC){
				callBack.onSuccess(object);
			}else if(callBack instanceof RequestWithFailureCallBack){
				((RequestWithFailureCallBack)callBack).onFailure((String) object.get("resultDes"));
			}
		}  catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public interface RequestCallBack {
		void onSuccess(Map<String, Object> map);
	}
	
	/***
	 * 带有失败通知的回调
	 */
	public interface RequestWithFailureCallBack extends RequestCallBack{
		void onFailure(String errMsg);
	}
	/***
	 * 带有失败通知的回调
	 */
	public interface RequestWithClzCallBack extends RequestCallBack{
		void onFinish(Object obj);
	}

	
	public static void doRequest(BaseMessage message ,RequestCallBack callBack){
		AppContext.EXECUTOR_SERVICE.execute(new AsyncRequest(message, callBack));
	}
	public static void doRequest(BaseMessage message){
		AppContext.EXECUTOR_SERVICE.execute(new AsyncRequest(message));
	}
	
	public static void doRequest(BaseMessage message ,Class<?>clz ,RequestWithClzCallBack callBack){
		AppContext.EXECUTOR_SERVICE.execute(new AsyncRequest(message, callBack));
	}

}
