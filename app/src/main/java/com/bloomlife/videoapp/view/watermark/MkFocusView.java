package com.bloomlife.videoapp.view.watermark;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import com.bloomlife.videoapp.R;

/**
 * Created by zxt lan4627@Gmail.com on 2015/8/26.
 */
public class MkFocusView extends BaseMkView {

    public MkFocusView(Context context) {
        super(context);
    }

    public MkFocusView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MkFocusView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public MkFocusView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void initLayout(Context context) {
        inflate(context, R.layout.view_mk_focus, this);
        mDate = (TextView) findViewById(R.id.mk_focus_date);
        mHour = (TextView) findViewById(R.id.mk_focus_hour);
        mLocation = (TextView) findViewById(R.id.mk_focus_local);
        mDescription = (TextView) findViewById(R.id.mk_focus_desc);
    }

    @Override
    public void hideAll() {
        setVisibility(INVISIBLE);
    }

    @Override
    public void pauseAnimator() {

    }

    @Override
    public void resumeAnimator() {

    }

    @Override
    public void setCurrentTime(long time, long totalTime) {
        if (time > 500 && !isShown()){
            setVisibility(VISIBLE);
            ObjectAnimator.ofFloat(this, "translationX", -getMeasuredWidth(), 0).setDuration(500).start();
        }
    }
}
