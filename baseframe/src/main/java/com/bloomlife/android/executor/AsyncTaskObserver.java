/**
 * 
 */
package com.bloomlife.android.executor;

/**
 * @author <a href="mailto:xiai.fei@gmail.com">xiai_fei</a>  
 *
 * @date 2014-5-27  上午11:59:36
 */
public interface AsyncTaskObserver {

	enum TYPE { NOTHING}
	
	/***
	 *    会在ui线程中调用。
	 *    
	 * @param obj
	 * @param msg
	 * @return  false： task将终止执行
	 */
	boolean notify(Object obj, Object msg, TYPE type);
}
