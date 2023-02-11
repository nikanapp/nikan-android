/**
 * 
 */
package com.bloomlife.videoapp.view;

import android.content.Context;
import android.view.animation.Interpolator;
import android.widget.Scroller;

/**
 * @author <a href="mailto:lan4627@gmail.com">zhengxingtian</a>
 * 
 * @date 2014年12月15日 下午6:51:26
 */
public class ViewPagerScroller extends Scroller {
	
	private int mDuration;

	private ViewPagerScroller(Context context, Interpolator interpolator,
			boolean flywheel) {
		super(context, interpolator, flywheel);
	}

	private ViewPagerScroller(Context context) {
		super(context);
	}

	public ViewPagerScroller(Context context, Interpolator interpolator,
			int duration) {
		super(context, interpolator);
		mDuration = duration;
	}

	@Override
	public void startScroll(int startX, int startY, int dx, int dy) {
		super.startScroll(startX, startY, dx, dy, mDuration);
	}

	@Override
	public void startScroll(int startX, int startY, int dx, int dy, int duration) {
		super.startScroll(startX, startY, dx, dy, mDuration);
	}
}
