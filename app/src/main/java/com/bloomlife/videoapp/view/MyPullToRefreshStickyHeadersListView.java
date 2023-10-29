package com.bloomlife.videoapp.view;

import android.content.Context;
import android.util.AttributeSet;

import com.lee.pullrefresh.ui.LoadingLayout;
import com.lee.pullrefresh.ui.PullToRefreshStickyHeadersListView;

/**
 * Created by zxt lan4627@Gmail.com on 2015/8/7.
 */
public class MyPullToRefreshStickyHeadersListView extends PullToRefreshStickyHeadersListView {

    public MyPullToRefreshStickyHeadersListView(Context context) {
        super(context);
    }

    public MyPullToRefreshStickyHeadersListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyPullToRefreshStickyHeadersListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
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
