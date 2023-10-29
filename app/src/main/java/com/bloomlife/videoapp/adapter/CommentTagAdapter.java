/**
 * 
 */
package com.bloomlife.videoapp.adapter;

import java.util.List;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;

import com.bloomlife.android.framework.AbstractAdapter;
import com.bloomlife.videoapp.model.Comment;
import com.bloomlife.videoapp.view.comment.CommentNumberView;

import androidx.fragment.app.FragmentActivity;

/**
 * @author <a href="mailto:lan4627@gmail.com">zxt</a>
 *
 * @date 2015年1月29日 下午7:10:46
 */
public class CommentTagAdapter extends AbstractAdapter<Comment> {

	private Activity mActivity;
	private Comment mSelectComment;
	private boolean mAnimEnabled = false;
	
	public CommentTagAdapter(FragmentActivity activity, List<Comment> dataList) {
		super(activity, dataList);
		mActivity = activity;
	}

	@Override
	protected View initItemView(int position, ViewGroup parent) {
		return new CommentNumberView(mActivity);
	}

	@Override
	protected void setViewContent(int position, View convertView, Comment item) {
		CommentNumberView view = (CommentNumberView) convertView;
		if (mSelectComment != null && item.getContent().equals(mSelectComment.getContent())){
			view.setChecked(true);
			if (mAnimEnabled)
				view.startNumAnimator();
		} else {
			view.setChecked(false);
		}
		view.setCommentInfo(item);
	}
	
	/**
	 * 设置标签
	 */
	public void setComment(Comment comment){
		removeLastComment();
		if (mSelectComment != null && mSelectComment.getContent().equals(comment.getContent())){
			mSelectComment = null;
		} else {
			for (Comment c:getDataList()){
				if (c.getContent().equals(comment.getContent())){
					mSelectComment = c;
					c.setCount(String.valueOf(Integer.valueOf(c.getCount()) + 1));
				}
			}
		}
		notifyDataSetChanged();
	}
	
	/**
	 * 添加标签
	 * @param comment
	 */
	public void addComment(Comment comment){
		// 移除之前选中的评论标签。
		removeLastComment();
		if (mSelectComment == null || !mSelectComment.getContent().equals(comment.getContent())){
			// 如果不相同，先找看列表里是否有相同的标签了，有的话数值加上1，没有就把标签加到列表中。
			boolean isCantains = false;
			for (Comment c:getDataList()){
				if (c.getContent().equals(comment.getContent())){
					isCantains = true;
					mSelectComment = c;
					c.setCount(String.valueOf(Integer.valueOf(c.getCount()) + 1));
				}
			}
			if (!isCantains){
				mSelectComment = comment;
				getDataList().add(comment);
			}
		} 
		notifyDataSetChanged();
	}
	
	/**
	 * 移除最后一次选中的标签
	 */
	public void removeLastComment(){
		if (mSelectComment == null) return;
		// 数值等于1的移除掉，大于1的减掉1。
		if (Integer.valueOf(mSelectComment.getCount()) == 1){
			getDataList().remove(mSelectComment);
			mSelectComment = null;
		} else {
			mSelectComment.setCount(String.valueOf(Integer.valueOf(mSelectComment.getCount()) - 1));
		}
	}
	
	public void removeSelectComment(){
		mSelectComment = null;
	}
	
	public void setSelectComment(String content){
		for (Comment c:getDataList()){
			if (c.getContent() != null && c.getContent().equals(content)){
				mSelectComment = c;
			}
		}
	}
	
	public Comment getSelectComment(){
		return this.mSelectComment;
	}
	
	public void enabledNumAnimator(boolean enabled){
		mAnimEnabled = enabled;
	}
}
