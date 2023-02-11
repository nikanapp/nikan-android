/**
 * 
 */
package com.bloomlife.videoapp.view;

import java.util.Stack;

import com.bloomlife.android.common.util.UiUtils;
import com.bloomlife.videoapp.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * @author <a href="mailto:lan4627@gmail.com">zhengxingtian</a>
 *
 * @date 2014年12月26日 下午3:23:18
 */
public class PageNumberView extends LinearLayout {

	public PageNumberView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public PageNumberView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public PageNumberView(Context context) {
		super(context);
		init(context);
	}
	
	private Stack<View> mDotStack = new Stack<View>();
	private int oldPosition;
	
	private void init(Context context){
		setOrientation(HORIZONTAL);
	}
	
	public void setDotNumber(int num){
		final int stackSize = mDotStack.size();
		if (num > stackSize){
			for (int i = stackSize; i < num; i++) {
				addDot();
			}
		} else {
			for (int i = 0; i < (stackSize - num); i++) {
				removeDot();
			}
		}
		
	}
	
	public void setCurrentDot(int newPosition){
		if (newPosition < mDotStack.size()){
			mDotStack.get(oldPosition).setEnabled(false); 
			mDotStack.get(newPosition).setEnabled(true);
			oldPosition = newPosition;
		}
	}
	
	public void addDot(){
		View view = createDotView();
		addView(view, getParams());
		mDotStack.add(view);
		setVisibility(mDotStack.size() == 1 ? View.INVISIBLE : View.VISIBLE);
	}
	
	public void removeDot(){
		removeView(mDotStack.pop());
		setVisibility(mDotStack.size() == 1 ? View.INVISIBLE : View.VISIBLE);
	}
	
	private View createDotView(){
		ImageView view = new ImageView(getContext());
		view.setBackgroundResource(R.drawable.btn_dotview_selector);
		view.setEnabled(false);
		return view;
	}
	
	private LayoutParams getParams(){
		LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.leftMargin = UiUtils.dip2px(getContext(), 2);
		params.rightMargin = UiUtils.dip2px(getContext(), 2);
		return params;
	}

}
