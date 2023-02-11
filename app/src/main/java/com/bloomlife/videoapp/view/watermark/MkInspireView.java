package com.bloomlife.videoapp.view.watermark;

import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bloomlife.videoapp.R;

/**
 * Created by zhengxingtian lan4627@Gmail.com on 2015/8/26.
 */
public class MkInspireView extends BaseMkView {

    public MkInspireView(Context context) {
        super(context);
    }

    public MkInspireView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MkInspireView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public MkInspireView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    private ViewGroup mInfoGroup;

    @Override
    protected void initLayout(Context context) {
        inflate(context, R.layout.view_mk_inspire, this);

        mDate = (TextView) findViewById(R.id.mk_inspire_date);
        mHour = (TextView) findViewById(R.id.mk_inspire_hour);
        mLocation = (TextView) findViewById(R.id.mk_inspire_local);
        mDescription = (TextView) findViewById(R.id.mk_inspire_desc);

        mInfoGroup = (ViewGroup) findViewById(R.id.mk_inspire_info_group);
    }

    @Override
    public void hideAll() {
        mInfoGroup.setVisibility(GONE);
        mDescription.setVisibility(GONE);
    }

    @Override
    public void pauseAnimator() {

    }

    @Override
    public void resumeAnimator() {

    }

    @Override
    public void setCurrentTime(long time, long totalTime) {
        if (time > 2000 && !mInfoGroup.isShown()){
            mInfoGroup.setVisibility(VISIBLE);
            mDescription.setVisibility(VISIBLE);
            ValueAnimator animator = ValueAnimator.ofFloat(0.0f, 1.0f);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float value = (float) animation.getAnimatedValue();
                    mInfoGroup.setScaleX(value);
                    mInfoGroup.setScaleY(value);
                    mDescription.setScaleX(value);
                    mDescription.setScaleY(value);
                }
            });
            animator.setDuration(500);
            animator.start();
        }
    }
}
