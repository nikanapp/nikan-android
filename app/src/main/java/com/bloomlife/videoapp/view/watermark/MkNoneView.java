package com.bloomlife.videoapp.view.watermark;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bloomlife.videoapp.R;

/**
 * Created by zxt lan4627@Gmail.com on 2015/9/8.
 */
public class MkNoneView extends BaseMkView {

    public MkNoneView(Context context) {
        super(context);
    }

    public MkNoneView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MkNoneView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public MkNoneView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void initLayout(Context context) {
        inflate(context, R.layout.view_mk_none, this);
        mDate = (TextView) findViewById(R.id.mk_none_date);
        mHour = (TextView) findViewById(R.id.mk_none_hour);
        mLocation = (TextView) findViewById(R.id.mk_none_local);
        mDescription = (TextView) findViewById(R.id.mk_none_desc);
    }

    @Override
    public void pauseAnimator() {

    }

    @Override
    public void resumeAnimator() {

    }

    @Override
    public void setCurrentTime(long time, long totalTime) {
        if (!mDescription.isShown()){
            showAll();
        }
    }
}
