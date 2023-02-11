/**
 * 
 */
package com.bloomlife.android.business;

import android.content.Context;

import com.bloomlife.android.bean.BaseMessage;
import com.bloomlife.android.bean.ProcessResult;
import com.bloomlife.android.network.HttpAccessor;

/**
 * @author <a href="mailto:xiai.fei@gmail.com">xiai_fei</a>  
 *
 * @date 2014-7-22  下午10:08:11
 */
public class BusinessProcessor {

	private Context context ;
	
	public BusinessProcessor(Context context ){
		this.context = context;
	}

	public ProcessResult doBusinessRequest(BaseMessage baseMessage,Class<? extends ProcessResult> result){
		try {
//			JsonResult jsonResult = baseMessage.getClass().getAnnotation(JsonResult.class);
			HttpAccessor httpAccessor = new HttpAccessor(context);
			return httpAccessor.call(baseMessage,result);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ProcessResult().setCode(ProcessResult.Failure);
	}
}
