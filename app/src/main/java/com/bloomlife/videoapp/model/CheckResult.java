/**
 * 
 */
package com.bloomlife.videoapp.model;

import com.bloomlife.android.bean.ProcessResult;

/**
 * @author <a href="mailto:xiai.fei@gmail.com">xiai_fei</a>  
 *
 * @date 2014-12-19  下午8:44:59
 */
public class CheckResult extends ProcessResult{

	public CheckResult(){}
	
	private int state ;

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}
}
