/**
 * 
 */
package com.bloomlife.videoapp.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import com.bloomlife.videoapp.R;
import com.lee.pullrefresh.ui.LoadingLayout;

/**
 * @author <a href="mailto:lan4627@gmail.com">zxt</a>
 *
 * @date 2015年2月9日 上午11:50:33
 */
public class MyLoadingLayout extends LoadingLayout {

	private View mLayout;

	/**
	 * @param context
	 */
	public MyLoadingLayout(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	public MyLoadingLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}


	/**
	 * @param context
	 * @param attrs
	 */
	public MyLoadingLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}


	@Override
	public int getContentSize() {
		return getHeight() != 0 ? getHeight() : (int) (getResources().getDisplayMetrics().density * 40);
	}

	@Override
	protected View createLoadingView(Context context, AttributeSet attrs) {
		mLayout = LayoutInflater.from(context).inflate(R.layout.view_pulltorefresh_load, this, false);
		mLayout.setVisibility(View.INVISIBLE);
		return mLayout;
	}

	@Override
	protected void onReset() {
		super.onReset();
		mLayout.setVisibility(View.INVISIBLE);
	}

	@Override
	protected void onPullToRefresh() {
		super.onPullToRefresh();
		mLayout.setVisibility(View.VISIBLE);
	}

	@Override
	protected void onReleaseToRefresh() {
		super.onReleaseToRefresh();
		mLayout.setVisibility(View.VISIBLE);
	}

	@Override
	protected void onRefreshing() {
		super.onRefreshing();
		mLayout.setVisibility(View.VISIBLE);
	}

	@Override
	protected void onNoMoreData() {
		super.onNoMoreData();
		mLayout.setVisibility(View.GONE);
	}
}
