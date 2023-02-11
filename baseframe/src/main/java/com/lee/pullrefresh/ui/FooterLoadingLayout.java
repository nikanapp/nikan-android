package com.lee.pullrefresh.ui;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.bloomlife.android.R;

/**
 * 这个类封装了下拉刷新的布局
 * 
 * @author Li Hong
 * @since 2013-7-30
 */
public class FooterLoadingLayout extends LoadingLayout {
	
	private static final String TAG = "FooterLoadingLayout";
    
    /**
     * 构造方法
     * 
     * @param context context
     */
    public FooterLoadingLayout(Context context) {
        super(context);
        init(context);
    }

    /**
     * 构造方法
     * 
     * @param context context
     * @param attrs attrs
     */
    public FooterLoadingLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    
    private AnimationDrawable animationDrawable; 
    private ImageView gif;
    /**
     * 初始化
     * 
     * @param context context
     */
    private void init(Context context) {
        gif = (ImageView) findViewById(R.id.pull_to_refresh_footer_gif);
        animationDrawable=(AnimationDrawable) gif.getBackground();
        this.onReset();
    }
    
    @Override
    protected View createLoadingView(Context context, AttributeSet attrs) {
        View container = LayoutInflater.from(context).inflate(R.layout.pull_to_load_footer, this, false);
        return container;
    }

    @Override
    public void setLastUpdatedLabel(CharSequence label) {
    }

    @Override
    public int getContentSize() {
        View view = findViewById(R.id.pull_to_load_footer_content);
        if (null != view) {
            return view.getHeight();
        }
        
        return (int) (getResources().getDisplayMetrics().density * 40);
    }
    
    @Override
    protected void onStateChanged(State curState, State oldState) {
    	gif.setVisibility(View.VISIBLE);
        super.onStateChanged(curState, oldState);
    }
    
    @Override
    protected void onReset() {
    	Log.d(TAG, "onReset");
    	animationDrawable.stop();
    	gif.setVisibility(View.GONE);
    }

    @Override
    protected void onPullToRefresh() {
    	Log.d(TAG, "onPullToRefresh");
    	animationDrawable.start();
    	gif.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onReleaseToRefresh() {
    	Log.d(TAG, "onReleaseToRefresh");
    	animationDrawable.start();
    	gif.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onRefreshing() {
    	Log.d(TAG, "onRefreshing");
    	animationDrawable.start();
    	gif.setVisibility(View.VISIBLE);
    }
    
    @Override
    protected void onNoMoreData() {
    	gif.setVisibility(View.GONE);
    }
}
