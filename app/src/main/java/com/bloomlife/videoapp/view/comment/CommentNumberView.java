/**
 * 
 */
package com.bloomlife.videoapp.view.comment;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.CheckedTextView;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.bloomlife.videoapp.R;
import com.bloomlife.videoapp.adapter.CommentPagerAdapter.OnCommentNumberClickListener;
import com.bloomlife.videoapp.common.util.UIHelper;
import com.bloomlife.videoapp.model.Comment;

/**
 * @author <a href="mailto:lan4627@gmail.com">zxt</a>
 * 右上角带数字的视频评论标签
 * @date 2014年12月3日 下午5:46:28
 */
public class CommentNumberView extends FrameLayout{
	
	public static final int INIT = Integer.MAX_VALUE;
	
	private TextView mCommentNum;
	private CheckedTextView mCommentText;
	private AnimatorSet mAnimatorSet;
	private int mNumber = INIT;
	private Comment mComment;

	public CommentNumberView(Context context) {
		super(context);
		init(context, null);
	}
	
	public CommentNumberView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs);
	}
	
	public CommentNumberView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context, attrs);
	}

	private void init(Context context, AttributeSet attrs){
		inflate(context, R.layout.view_comment_number, this);
		if (isInEditMode()) return;
		mCommentNum  = (TextView) findViewById(R.id.view_comment_number_num);
		mCommentText = (CheckedTextView) findViewById(R.id.view_comment_number_text);
		mCommentText.setTypeface(UIHelper.getHelveticaLt(context));
		mCommentNum.setTypeface(UIHelper.getImpact(context));
		if (attrs != null){
			TypedArray array= context.obtainStyledAttributes(attrs, R.styleable.comment_view);
			String commentText = array.getString(R.styleable.comment_view_comment_text);
			int commentNum = array.getInt(R.styleable.comment_view_comment_num, 0);
			array.recycle();
			
			mCommentText.setText(commentText);
			mCommentNum.setText(String.valueOf(commentNum));
		}
		
		ObjectAnimator scaleX = ObjectAnimator.ofFloat(mCommentNum, "scaleX", 1.0f , 1.2f);
		scaleX.setRepeatMode(ObjectAnimator.REVERSE);
		scaleX.setRepeatCount(1);
		ObjectAnimator scaleY = ObjectAnimator.ofFloat(mCommentNum, "scaleY", 1.0f , 1.2f);
		scaleY.setRepeatMode(ObjectAnimator.REVERSE);
		scaleY.setRepeatCount(1);
		
		mAnimatorSet = new AnimatorSet();
		mAnimatorSet.playTogether(scaleX, scaleY);
	}
	
	public void setCommentInfo(Comment comment){
		int color = Color.parseColor("#"+comment.getColor());
		setCommentNum(Integer.valueOf(comment.getCount()));
		setCommentText(comment.getContent());
		setCommentBackColor(color);
		if (mCommentText.isChecked())
			setCommentTextColor(Color.BLACK);
		else
			setCommentTextColor(color);
		mComment = comment;
	}
	
	public void clear(){
		setCommentText("");
		setCommentNum(INIT);
	}
	
	public void startNumAnimator(){
		mAnimatorSet.start();
	}
	
	private void setCommentNum(int commentNum){
		mCommentNum.setText(String.valueOf(commentNum));
		mNumber = commentNum;
	}
	
	private void setCommentText(String commentText){
		mCommentText.setText(commentText);
	}
	
	public void setCommentTextColor(int color){
		mCommentText.setTextColor(color);
	}
	
	public void setChecked(boolean checked){
		mCommentText.setChecked(checked);
	}
	
	public void setCommentBackColor(int color){
		UIHelper.setVideoCommentTagSelector(getContext().getResources(), mCommentText, color);
	}

}
