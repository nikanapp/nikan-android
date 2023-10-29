/**
 * 
 */
package com.bloomlife.videoapp.view;

import com.bloomlife.videoapp.R;

import android.animation.Animator.AnimatorListener;
import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;

/**
 * @author <a href="mailto:lan4627@gmail.com">zxt</a>
 * 
 * @date 2014年12月5日 下午9:51:09
 */
public class TopicAnimButton extends RelativeLayout implements OnClickListener{

	public TopicAnimButton(Context context) {
		super(context);
		init(context);
	}

	public TopicAnimButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public TopicAnimButton(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context);
	}
	
	public static final int DURATION = 200;
	
	private float mTranslationH;
	private float mTranslationV;
	
	private OnClickListener l;
	
	private View mView0;
	private View mView1;
	private View mView2;
	private View mView3;
	
	private AnimatorSet mDownAnimatorSet;
	private AnimatorSet mUpAnimatorSet;
	
	private boolean isDown;
	
	private void init(Context context){
		inflate(context, R.layout.view_topic_animbutton, this);
		setBackgroundResource(R.drawable.btn_topic_selector);
		mView0 = findViewById(R.id.view_topic_animbutton_view0);
		mView1 = findViewById(R.id.view_topic_animbutton_view1);
		mView2 = findViewById(R.id.view_topic_animbutton_view2);
		mView3 = findViewById(R.id.view_topic_animbutton_view3);
		super.setOnClickListener(this);
		
		mTranslationH = context.getResources().getDisplayMetrics().density * 2.80f;
		mTranslationV = context.getResources().getDisplayMetrics().density * 3.35f;
		
		mDownAnimatorSet = new AnimatorSet();
		mDownAnimatorSet.playTogether(
				ObjectAnimator.ofFloat(mView0, "translationY", 0, mTranslationH),
				ObjectAnimator.ofFloat(mView1, "translationY", 0, -mTranslationH),
				
				ObjectAnimator.ofFloat(mView0, "rotation", 0, 45),
				ObjectAnimator.ofFloat(mView1, "rotation", 0, 45),
				
				ObjectAnimator.ofFloat(mView2, "translationX", 0, mTranslationV),
				ObjectAnimator.ofFloat(mView3, "translationX", 0, -mTranslationV),
				
				ObjectAnimator.ofFloat(mView2, "rotation", 0, 35),
				ObjectAnimator.ofFloat(mView3, "rotation", 0, 35)
				);
		
		mUpAnimatorSet = new AnimatorSet();
		mUpAnimatorSet.playTogether(
				ObjectAnimator.ofFloat(mView0, "translationY", mTranslationH, 0),
				ObjectAnimator.ofFloat(mView1, "translationY", -mTranslationH, 0),
				
				ObjectAnimator.ofFloat(mView0, "rotation", 45, 0),
				ObjectAnimator.ofFloat(mView1, "rotation", 45, 0),
				
				ObjectAnimator.ofFloat(mView2, "translationX", mTranslationV, 0),
				ObjectAnimator.ofFloat(mView3, "translationX", -mTranslationV, 0),
				
				ObjectAnimator.ofFloat(mView2, "rotation", 35, 0),
				ObjectAnimator.ofFloat(mView3, "rotation", 35, 0)
				);
	}

	@Override
	public void setOnClickListener(OnClickListener l) {
		this.l = l;
	}

	@Override
	public void onClick(View v) {
		if (isDown){
			up();
		} else {
			down();
		}
		if (l != null){
			l.onClick(v);
		}
	}
	
	public void down(){
		setClickEnable(false);
		isDown = true;
		mDownAnimatorSet.setDuration(DURATION);
		mDownAnimatorSet.addListener(animatorListener);
		mDownAnimatorSet.start();
	}
	
	public void up(){
		setClickEnable(false);
		isDown = false;
		mUpAnimatorSet.setDuration(DURATION);
		mUpAnimatorSet.addListener(animatorListener);
		mUpAnimatorSet.start();
	}
	
	private void setClickEnable(boolean enable){
		setEnabled(enable);
	}

	private AnimatorListener animatorListener = new AnimatorListener() {
		
		@Override
		public void onAnimationStart(Animator animation) {
			isAnimationFinish = false ;
		}
		
		@Override
		public void onAnimationRepeat(Animator animation) {
			
		}
		
		@Override
		public void onAnimationEnd(Animator animation) {
			isAnimationFinish = true ;
			setClickEnable(true);
		}
		
		@Override
		public void onAnimationCancel(Animator animation) {
		}
	};
	
	private boolean isAnimationFinish = true ;
	
	public boolean isAnimationFinish() {
		return isAnimationFinish;
	}

}
