/**
 * 
 */
package com.bloomlife.videoapp.adapter;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;

import android.content.Context;
import androidx.core.view.PagerAdapter;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

import com.bloomlife.videoapp.model.Comment;
import com.bloomlife.videoapp.model.Commenttags;
import com.bloomlife.videoapp.view.comment.CommentNumberLayout;
import com.jfeinstein.jazzyviewpager.JazzyViewPager;

/**
 * @author <a href="mailto:lan4627@gmail.com">zhengxingtian</a>
 *
 * @date 2014年12月2日 下午5:35:43
 */
public class CommentPagerAdapter extends PagerAdapter {
	
	public static final int LENGTH = 5;
	public static final int UP = 1;
	
	private OnCommentNumberClickListener mListener;
	private SparseArray<CommentNumberLayout> mLayouts;
	private LinkedHashMap<String, Comment> mCommentMap;
	private JazzyViewPager mViewPager;
	private View mEmptyView;
	private Context mContext;
	
	public CommentPagerAdapter(Context context, LinkedHashMap<String, Comment> commentMap, OnCommentNumberClickListener listener, JazzyViewPager viewPager){
		mCommentMap = new LinkedHashMap<String, Comment>();
		mContext = context;
		mViewPager = viewPager;
		mLayouts = new SparseArray<CommentNumberLayout>();
		mListener = listener;
		if (commentMap != null){
			mCommentMap.putAll(commentMap);
			fillCommentLayouts(commentMap);
		}
	}
	
	/**
	 * 生成评论页面
	 * @param commentMap
	 */
	private void fillCommentLayouts(HashMap<String, Comment> commentMap){
		Iterator<Comment> interator = commentMap.values().iterator();
		CommentNumberLayout layout = null;
		for (int i = 0; interator.hasNext(); i++) {
			int index = i % LENGTH;
			if (index == 0){								// 当评论是页面的第一个评论的时候，要去获取新的评论页面。
				layout = getLayout(i);
			}
			Comment comment = interator.next();
			comment.setPosition(i);
			layout.setComment(index, comment);				// 把评论添加到评论页面中
		}
	}
	
	/**
	 * 返回一个评论页的布局页面
	 * @param position
	 * @return
	 */
	private CommentNumberLayout getLayout(int position){
		int page = position / LENGTH;
		CommentNumberLayout pager = mLayouts.get(page);   
		if (pager != null){
			return pager;									// 如果缓存列表里有就直接返回。
		} else {											
			pager = new CommentNumberLayout(mContext);		// 没有就生成一个，然后添加到缓存列表中
			pager.setVisibility(View.INVISIBLE);
			mLayouts.put(page, pager);
			return pager;
		}
	}
	
	public void setComment(LinkedHashMap<String, Comment> comments){
		mCommentMap = comments;
		notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
		return mLayouts.size();
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg1==arg0;
	}
	
	@Override
	public View instantiateItem(ViewGroup container, int position) {
		View layout = mLayouts.get(position);
		layout.setVisibility(View.VISIBLE);
		container.addView(layout);
		mViewPager.setObjectForPosition(layout, position);
		return layout;
	}
	
	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		View layout = mLayouts.get(position);
		container.removeView(layout);
	}
	
	private ViewGroup.LayoutParams getParams(){
		ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		return params;
	}
	/**
	 * 增加一个评论
	 * @param commentName
	 * @return 是否被添加到了一个新的页面
	 */
	public int addComment(Commenttags commenttags){
		Comment comment = mCommentMap.get(commenttags.getContent());
		if (comment != null){
			comment.setCount(String.valueOf(Integer.valueOf(comment.getCount())+UP));
		} else {
			comment = Comment.makeComment(commenttags, String.valueOf(UP), mCommentMap.size());
			mCommentMap.put(commenttags.getContent(), comment);
		}
		notifyDataSetChanged();
		return comment.getPosition()/LENGTH;
	}
	
	/**
	 * 减少一个评论
	 * @param commentName
	 */
	public int minusComment(String commentName){
		Comment comment = mCommentMap.get(commentName);
		int number = Integer.valueOf(comment.getCount());
		if (number == 1){									// 如果评论数只有一个，那么当前评论要从数据集合和评论页面中移除
			resetCommentLayout();
			removeLayout(mCommentMap.remove(commentName).getPosition());
		} else {
			comment.setCount(String.valueOf(number-1)); 
		}
		notifyDataSetChanged();
		return comment.getPosition();
	}
	
	private void removeLayout(int position){
		if (position == mCommentMap.size()){
			mLayouts.remove(position/LENGTH);
		}
	}
	
	@Override
	public int getItemPosition(Object object) {
		// TODO Auto-generated method stub
		return POSITION_NONE;
	}
	
	@Override
	public void notifyDataSetChanged() {
		fillCommentLayouts(mCommentMap);
		if (mEmptyView != null && mCommentMap.isEmpty()){
			mEmptyView.setVisibility(View.VISIBLE);
		} else {
			mEmptyView.setVisibility(View.INVISIBLE);
		}
		super.notifyDataSetChanged();
	}
	
	public void setEmptyView(View view){
		mEmptyView = view;
	}
	
	private void resetCommentLayout(){
		for (int i=0; i<mLayouts.size(); i++){
			mLayouts.valueAt(i).hideViews();
		}
	}
	
	public interface OnCommentNumberClickListener{
		void onClick(Comment comment);
	}

}
