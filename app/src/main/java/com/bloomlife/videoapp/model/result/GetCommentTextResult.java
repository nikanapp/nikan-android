/**
 * 
 */
package com.bloomlife.videoapp.model.result;

import java.util.List;

import com.bloomlife.android.bean.ProcessResult;
import com.bloomlife.videoapp.common.CommentText;

/**
 * @author <a href="mailto:lan4627@gmail.com">zxt</a>
 * 
 * @date 2015年2月5日 下午12:19:17
 */
public class GetCommentTextResult extends ProcessResult {
	private List<CommentText> commentTexts;
	private int pagesize;
	private int pagenum;

	/**
	 * @return the commentTexts
	 */
	public List<CommentText> getCommentTexts() {
		return commentTexts;
	}

	/**
	 * @param commentTexts
	 *            the commentTexts to set
	 */
	public void setCommentTexts(List<CommentText> commentTexts) {
		this.commentTexts = commentTexts;
	}

	/**
	 * @return the pagesize
	 */
	public int getPagesize() {
		return pagesize;
	}

	/**
	 * @param pagesize
	 *            the pagesize to set
	 */
	public void setPagesize(int pagesize) {
		this.pagesize = pagesize;
	}

	/**
	 * @return the pagenum
	 */
	public int getPagenum() {
		return pagenum;
	}

	/**
	 * @param pagenum
	 *            the pagenum to set
	 */
	public void setPagenum(int pagenum) {
		this.pagenum = pagenum;
	}

}
