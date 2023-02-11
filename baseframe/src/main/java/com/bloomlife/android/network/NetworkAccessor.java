package com.bloomlife.android.network;

import org.apache.http.HttpException;

import com.bloomlife.android.bean.BaseMessage;
import com.bloomlife.android.bean.ProcessResult;

/**
 * 网络访问接口
 * @author gary
 *
 */
public interface NetworkAccessor {

	<T> T call(BaseMessage message, Class<? extends ProcessResult> clz) throws HttpException;
	
	String call(BaseMessage message) throws HttpException;
}