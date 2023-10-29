/**
 * 
 */
package com.bloomlife.videoapp.view;

import android.content.Context;
import android.util.AttributeSet;

import com.lee.pullrefresh.ui.LoadingLayout;
import com.lee.pullrefresh.ui.PullToRefreshListView;

/**
 * @author <a href="mailto:lan4627@gmail.com">zxt</a>
 *
 * @date 2015年2月9日 上午11:46:39
 */
public class MyPullToRefreshListView extends PullToRefreshListView {

	/**
	 * @param context
	 */
	public MyPullToRefreshListView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	public MyPullToRefreshListView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param context
	 * @param attrs
	 */
	public MyPullToRefreshListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected LoadingLayout createFooterLoadingLayout(Context context,
			AttributeSet attrs) {
		return new MyLoadingLayout(context);
	}

	@Override
	protected LoadingLayout createHeaderLoadingLayout(Context context,
			AttributeSet attrs) {
		return new MyLoadingLayout(context);
	}

	@Override
	protected LoadingLayout createMoreFooterLoadingLayout(Context context) {
		return new MyLoadingLayout(context);
	}
	
}
