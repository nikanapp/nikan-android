/**
 * 
 */
package com.bloomlife.videoapp.model.result;

import java.util.List;

import com.bloomlife.android.bean.ProcessResult;
import com.bloomlife.videoapp.model.Comment;

/**
 * @author <a href="mailto:lan4627@gmail.com">zxt</a>
 *
 * @date 2014年12月4日 下午12:01:07
 */
public class GetCommentListResult extends ProcessResult {
	
	private int pagesize;
	private int pagenum;
	private List<Comment> list;
	private String commentid;
	
	public int getPagesize() {
		return pagesize;
	}
	public void setPagesize(int pagesize) {
		this.pagesize = pagesize;
	}
	public int getPagenum() {
		return pagenum;
	}
	public void setPagenum(int pagenum) {
		this.pagenum = pagenum;
	}
	public List<Comment> getList() {
		return list;
	}
	public void setList(List<Comment> list) {
		this.list = list;
	}
	public String getCommentid() {
		return commentid;
	}
	public void setCommentid(String commentid) {
		this.commentid = commentid;
	}
	
}
