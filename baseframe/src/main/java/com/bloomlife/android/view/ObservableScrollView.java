package com.bloomlife.android.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.ScrollView;

/**
 * Created by zhengxingtian lan4627@Gmail.com on 2015/10/10.
 */
public class ObservableScrollView extends ScrollView {

    public ObservableScrollView(Context context) {
        super(context);
    }

    public ObservableScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ObservableScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ObservableScrollView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    private OnScrollListener mListener;

    @Override
    protected void onScrollChanged(int x, int y, int oldX, int oldY) {
        super.onScrollChanged(x, y, oldX, oldY);
        if (mListener != null)
            mListener.onScrollChanged(x, y, oldX, oldY);
    }

    public void setOnScrollListener(OnScrollListener l){
        mListener = l;
    }

    public interface OnScrollListener{
        void onScrollChanged(int x, int y, int oldX, int oldY);
    }
}
