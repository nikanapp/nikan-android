package com.bloomlife.videoapp.view.watermark;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.bloomlife.videoapp.R;

/**
 * Created by zhengxingtian lan4627@Gmail.com on 2015/8/26.
 */
public class MkQuietView extends BaseMkView {

    public MkQuietView(Context context) {
        super(context);
    }

    public MkQuietView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MkQuietView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public MkQuietView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void initLayout(Context context) {
        inflate(context, R.layout.view_mk_quiet, this);

        mDate = (TextView) findViewById(R.id.mk_quiet_play_date);
        mHour = (TextView) findViewById(R.id.mk_quiet_play_hour);
        mLocation = (TextView) findViewById(R.id.mk_quiet_play_local);
        mDescription = (TextView) findViewById(R.id.mk_quiet_play_desc);
    }

    @Override
    public void pauseAnimator() {

    }

    @Override
    public void resumeAnimator() {

    }

    @Override
    public void setCurrentTime(long time, long totalTime){
        if (!mDate.isShown()){
            mDate.setVisibility(View.VISIBLE);
            mHour.setVisibility(View.VISIBLE);
        }
        if (time >= 1750 && !mDescription.isShown()){
            mDescription.setVisibility(View.VISIBLE);
        }
        if (time >= 2200 && !mLocation.isShown()){
            mLocation.setScaleX(0);
            mLocation.setScaleY(0);
            mLocation.setVisibility(View.VISIBLE);
            mLocation.animate().scaleX(1).scaleY(1).start();
        }
    }
}
