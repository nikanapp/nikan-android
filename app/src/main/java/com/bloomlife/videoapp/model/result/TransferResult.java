/**
 * 
 */
package com.bloomlife.videoapp.model.result;

import com.bloomlife.android.bean.ProcessResult;

/**
 *   
 *
 * @author <a href="mailto:xiai.fei@gmail.com">xiai_fei</a>  
 *
 * @date 2015-6-19 下午6:53:00
 *
 * @organization bloomlife  
 * @version 1.0
 */
public class TransferResult extends ProcessResult{

	private double[] result;

	public double[] getResult() {
		return result;
	}

	public void setResult(double[] result) {
		this.result = result;
	}
}
