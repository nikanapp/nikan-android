/**
 * 
 */
package com.bloomlife.videoapp.view;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.CheckBox;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;

import com.bloomlife.videoapp.R;
import com.bloomlife.videoapp.common.util.Utils;

import androidx.fragment.app.FragmentActivity;

/**
 * @author <a href="mailto:lan4627@gmail.com">zxt</a>
 * 
 * @date 2014年12月5日 下午5:29:28
 */
public class FireButton extends FrameLayout {
	public FireButton(Context context) {
		super(context);
		init(context);
	}

	public FireButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public FireButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}
	
	private CheckBox mCheckBox;
	private Animation mAnimation;
	private FireButtonListener mListener;
	
	private void init(Context context){
		inflate(context, R.layout.view_firebutton, this);
		mCheckBox = (CheckBox) findViewById(R.id.view_firebutton_fireimage);
		if (isInEditMode()) return;
		mCheckBox.setOnCheckedChangeListener(checkedChangeListener);
		mAnimation = AnimationUtils.loadAnimation(context, R.anim.firebutton_scale);
		mAnimation.setAnimationListener(animationListener);
	}
	
	public void setFireButton(boolean checked){
		// 先移除监听器，避免因初始化按钮状态而触发动画和点赞的网络请求
		mCheckBox.setOnCheckedChangeListener(null);
		
		mCheckBox.setChecked(checked);
		mCheckBox.setOnCheckedChangeListener(checkedChangeListener);
	}

	public boolean isChecked(){
		return mCheckBox.isChecked();
	}
	
	private OnCheckedChangeListener checkedChangeListener = new OnCheckedChangeListener() {
		
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			if (!Utils.isLogin((FragmentActivity) getContext(), true)){
				mCheckBox.setChecked(!isChecked);
				return;
			}
			if (isChecked){
				mCheckBox.startAnimation(mAnimation);
			}
			
			if (mListener != null){
				mListener.onClick(buttonView);
			}
		}
	};
	
	private AnimationListener animationListener = new AnimationListener() {
		
		@Override
		public void onAnimationStart(Animation animation) {
			
		}
		
		@Override
		public void onAnimationRepeat(Animation animation) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onAnimationEnd(Animation animation) {
			if (mListener != null)
				mListener.onAnimationEnd();
		}
	};
	
	public void setFireButtonListener(FireButtonListener listener){
		mListener = listener;
	}
	
	public interface FireButtonListener{
		void onAnimationEnd();
		void onClick(View view);
	}

	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		mCheckBox.setEnabled(enabled);
	}
	
}
