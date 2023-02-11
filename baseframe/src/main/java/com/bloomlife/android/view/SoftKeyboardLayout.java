/**
 * 
 */
package com.bloomlife.android.view;

/**
 * @author <a href="mailto:xiai.fei@gmail.com">xiai_fei</a>  
 *
 * @date 2014-12-18  下午3:02:42
 */

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.RelativeLayout;
/**
 * 能监听键盘打开和收起的layout，只能在android:windowSoftInputMode="adjustResize"模式下才有效。
 * @author <a href="mailto:lan4627@gmail.com">zhengxingtian</a>
 *
 * @date 2014年9月11日 下午4:57:23
 */
public class SoftKeyboardLayout extends RelativeLayout {

	private OnSoftKeyboardListener onSoftKeyboardListener;
	
	public SoftKeyboardLayout(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public SoftKeyboardLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public SoftKeyboardLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	private boolean isShowing;									// 软键盘是否正在显示中
	private boolean isHidden;									// 软键盘是否正在隐藏中
	
	private boolean softKeyBoardVisible;						// 软键盘的显示状态。
	
	@Override
	protected void onMeasure(final int widthMeasureSpec,
			final int heightMeasureSpec) {
		if (onSoftKeyboardListener != null) {
			final int newSpec = MeasureSpec.getSize(heightMeasureSpec);
			final int oldSpec = getMeasuredHeight();
			Log.d("onMeasure", "oldSpec "+oldSpec+" newSpec "+newSpec);
			if (oldSpec > newSpec) {
				isShowing = true;
				softKeyBoardVisible = true;
				onSoftKeyboardListener.onShown(Math.abs(oldSpec-newSpec));
			} else if (oldSpec < newSpec){
				isHidden = true;
				softKeyBoardVisible = false;
				onSoftKeyboardListener.onHidden();
			}
			
			if (isShowing && oldSpec == newSpec){
				isShowing = false;
				onSoftKeyboardListener.onShownEnd();
				Log.d("onMeasure", "onEnd");
			} else if (isHidden && oldSpec == newSpec){
				isHidden = false;
				onSoftKeyboardListener.onHiddenEnd(); 
			}
		}
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}
	/**
	 * 软键盘的显示状态
	 * @return true 显示，false 隐藏
	 */
	public boolean isSoftKeyBoardVisible() {
		return softKeyBoardVisible;
	}

	public void setOnSoftKeyboardListener(OnSoftKeyboardListener listener) {
		this.onSoftKeyboardListener = listener;
	}
	
	/**
	 * 软键盘状态监听接口
	 * @author <a href="mailto:lan4627@gmail.com">zhengxingtian</a>
	 *
	 * @date 2014年9月15日 下午2:39:54
	 */
	public interface OnSoftKeyboardListener {
		/**
		 * 软键盘开始显示
		 */
		void onShown(int keyboardHeight);
		/**
		 * 软键盘显示完毕
		 */
		void onShownEnd();
		/**
		 * 软键盘开始隐藏
		 */
		void onHidden();
		/**
		 * 软键盘隐藏完毕
		 */
		void onHiddenEnd();
	}

}
