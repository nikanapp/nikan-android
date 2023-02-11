/**
 * 
 */
package com.bloomlife.videoapp.view.comment;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.jfeinstein.jazzyviewpager.JazzyViewPager;

/**
 * @author <a href="mailto:lan4627@gmail.com">zhengxingtian</a>
 *	视频播放页面中间的评论池左右滑动切换控件
 * @date 2015年1月7日 上午11:17:37
 */
public class CommentViewPager extends ViewPager {
	
	private boolean mEnabled = true;

	/**
	 * @param context
	 * @param attrs
	 */
	public CommentViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param context
	 */
	public CommentViewPager(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean onTouchEvent(MotionEvent arg0) {
		// TODO Auto-generated method stub
		return super.onTouchEvent(arg0);
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		if (mEnabled){
			return super.dispatchTouchEvent(ev);
		} else {
			return false;
		}
	}
	
	public void setScroll(boolean enabled){
		mEnabled = enabled;
	}

}
