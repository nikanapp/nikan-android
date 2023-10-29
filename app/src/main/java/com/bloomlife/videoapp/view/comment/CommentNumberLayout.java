/**
 * 
 */
package com.bloomlife.videoapp.view.comment;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.RelativeLayout;

import com.bloomlife.videoapp.R;
import com.bloomlife.videoapp.model.Comment;
/**
 * @author <a href="mailto:lan4627@gmail.com">zxt</a>
 *
 * @date 2014年12月3日 下午7:58:03
 */
public class CommentNumberLayout extends RelativeLayout {

	public CommentNumberLayout(Context context) {
		super(context);
		init(context);
	}
	
	public CommentNumberLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}
	
	public CommentNumberLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}
	
	private List<CommentNumberView> mViews;

	private void init(Context context){
		inflate(context, R.layout.viewpager_comment, this);
		setGravity(Gravity.CENTER | Gravity.TOP);
		CommentNumberView view1 = (CommentNumberView)findViewById(R.id.commentnumber_view1);
		CommentNumberView view2 = (CommentNumberView)findViewById(R.id.commentnumber_view2);
		CommentNumberView view3 = (CommentNumberView)findViewById(R.id.commentnumber_view3);
		CommentNumberView view4 = (CommentNumberView)findViewById(R.id.commentnumber_view4);
		CommentNumberView view5 = (CommentNumberView)findViewById(R.id.commentnumber_view5);
		
		mViews = new ArrayList<CommentNumberView>();
		mViews.add(view1);
		mViews.add(view2);
		mViews.add(view3);
		mViews.add(view4);
		mViews.add(view5);
	}

	public void setComment(int i, Comment comment){
		mViews.get(i).setVisibility(View.VISIBLE);
		mViews.get(i).setCommentInfo(comment);
	}
	
	public void setCommnets(List<Comment> comments){
		for (int i=0; i<comments.size(); i++){
			setComment(i, comments.get(i));
		}
	}
	
	public void hideViews(){
		for (CommentNumberView view:mViews){
			view.setVisibility(View.INVISIBLE);
		}
	}
	
	public void hideView(int location){
		mViews.get(location).setVisibility(View.INVISIBLE);
	}
	
}
