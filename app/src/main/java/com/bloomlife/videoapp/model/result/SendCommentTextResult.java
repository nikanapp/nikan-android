/**
 * 
 */
package com.bloomlife.videoapp.model.result;

import com.bloomlife.android.bean.ProcessResult;

/**
 * @author <a href="mailto:lan4627@gmail.com">zxt</a>
 * 
 * @date 2015年2月13日 上午10:47:58
 */
public class SendCommentTextResult extends ProcessResult {
	private long cid;

	/**
	 * @return the cid
	 */
	public long getCid() {
		return cid;
	}

	/**
	 * @param cid
	 *            the cid to set
	 */
	public void setCid(long cid) {
		this.cid = cid;
	}

}
