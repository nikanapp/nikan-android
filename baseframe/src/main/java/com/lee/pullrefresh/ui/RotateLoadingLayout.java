package com.lee.pullrefresh.ui;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bloomlife.android.R;

/**
 * 这个类封装了下拉刷新的布局
 * 
 * @author Li Hong
 * @since 2013-7-30
 */
public class RotateLoadingLayout extends LoadingLayout {
    /**旋转动画的时间*/
    static final int ROTATION_ANIMATION_DURATION = 1200;
    /**动画插值*/
    static final Interpolator ANIMATION_INTERPOLATOR = new LinearInterpolator();
    /**Header的容器*/
    private RelativeLayout mHeaderContainer;
    
    /**
     * 构造方法
     * 
     * @param context context
     */
    public RotateLoadingLayout(Context context) {
        super(context);
        init(context);
    }

    /**
     * 构造方法
     * 
     * @param context context
     * @param attrs attrs
     */
    public RotateLoadingLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    
    private AnimationDrawable animationDrawable;
    /**
     * 初始化
     * 
     * @param context context
     */
    private void init(Context context) {
        mHeaderContainer = (RelativeLayout) findViewById(R.id.pull_to_refresh_header_content);
        ImageView gif = (ImageView) findViewById(R.id.pull_to_refresh_footer_gif);
//        gif.setGifImage(R.drawable.refrash_anim);
        animationDrawable=(AnimationDrawable) gif.getBackground();
        animationDrawable.start();
    }
    
    @Override
    protected View createLoadingView(Context context, AttributeSet attrs) {
        View container = LayoutInflater.from(context).inflate(R.layout.pull_to_refresh_header2, null);
        return container;
    }

    @Override
    public void setLastUpdatedLabel(CharSequence label) {
        // 如果最后更新的时间的文本是空的话，隐藏前面的标题
    }

    @Override
    public int getContentSize() {
        if (null != mHeaderContainer) {
            return mHeaderContainer.getHeight();
        }
        
        return (int) (getResources().getDisplayMetrics().density * 60);
    }
    
    @Override
    protected void onStateChanged(State curState, State oldState) {
        super.onStateChanged(curState, oldState);
    }

    @Override
    protected void onReset() {
        animationDrawable.stop();
    }

    @Override
    protected void onReleaseToRefresh() {
    	animationDrawable.start();
    }
    
    @Override
    protected void onPullToRefresh() {
    	animationDrawable.start();
    }
    
    @Override
    protected void onRefreshing() {
    	animationDrawable.start();
    }
    
    @Override
    public void onPull(float scale) {
        float angle = scale * 180f; // SUPPRESS CHECKSTYLE
        //mArrowImageView.setRotation(angle);
    }
    
    /**
     * 重置动画
     */
    private void resetRotation() {
        //mArrowImageView.clearAnimation();
        //mArrowImageView.setRotation(0);
    	animationDrawable.stop();
    }
}
