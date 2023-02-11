/**
 * 
 */
package com.bloomlife.videoapp.model.result;

import com.bloomlife.android.bean.ProcessResult;
import com.bloomlife.videoapp.model.SysCode;

/**
 * @author <a href="mailto:xiai.fei@gmail.com">xiai_fei</a>  
 *
 * @date 2014-12-5  上午10:32:34
 */
public class ParamSyncResult extends ProcessResult{

	private SysCode syscode;

	public SysCode getSyscode() {
		return syscode;
	}

	public void setSyscode(SysCode syscode) {
		this.syscode = syscode;
	}
}
