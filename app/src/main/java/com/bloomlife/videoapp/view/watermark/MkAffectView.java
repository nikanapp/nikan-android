package com.bloomlife.videoapp.view.watermark;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.widget.TextView;

import com.bloomlife.videoapp.R;

/**
 * Created by zxt lan4627@Gmail.com on 2015/8/26.
 */
public class MkAffectView extends BaseMkView {

    public MkAffectView(Context context) {
        super(context);
    }

    public MkAffectView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MkAffectView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public MkAffectView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    private Animator mDateAnimator;
    private Animator mDescAnimator;

    @Override
    protected void initLayout(Context context) {
        inflate(context, R.layout.view_mk_affect, this);

        mDate = (TextView) findViewById(R.id.mk_affect_date);
        mHour = (TextView) findViewById(R.id.mk_affect_hour);
        mLocation = (TextView) findViewById(R.id.mk_affect_local);
        mDescription = (TextView) findViewById(R.id.mk_affect_desc);
    }

    @Override
    public void pauseAnimator() {

    }

    @Override
    public void resumeAnimator() {

    }

    @Override
    public void setCurrentTime(long time, long totalTime){
        if (time > 0 && !mDate.isShown()){
            mDate.setVisibility(View.VISIBLE);
            mHour.setVisibility(View.VISIBLE);
            ObjectAnimator.ofFloat(mDate, "alpha", 0.0f, 1.0f).start();
            ObjectAnimator.ofFloat(mHour, "alpha", 0.0f, 1.0f).start();
        }
        if (time > 1000 && !mDescription.isShown()){
            mLocation.setVisibility(View.VISIBLE);
            mDescription.setVisibility(View.VISIBLE);
            ObjectAnimator.ofFloat(mLocation, "alpha", 0.0f, 1.0f).setDuration(1000).start();
            ObjectAnimator.ofFloat(mDescription, "alpha", 0.0f, 1.0f).setDuration(1000).start();
        }
    }

}
