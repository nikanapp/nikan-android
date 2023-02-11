/**
 * 
 */
package com.bloomlife.videoapp.view;

import com.bloomlife.videoapp.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

/**
 * @author <a href="mailto:lan4627@gmail.com">zhengxingtian</a>
 *
 * @date 2015年5月11日 下午9:43:11
 */
public class VideoScrollTipsView extends FrameLayout {

	/**
	 * @param context
	 */
	public VideoScrollTipsView(Context context) {
		super(context);
		init(context);
	}

	/**
	 * @param context
	 * @param attrs
	 */
	public VideoScrollTipsView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	/**
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	public VideoScrollTipsView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}
	
	private void init(Context context){
		inflate(context, R.layout.view_video_scroll_tips, this);
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			setVisibility(View.GONE);
			break;

		default:
			break;
		}
		return super.dispatchTouchEvent(ev);
	}


}
