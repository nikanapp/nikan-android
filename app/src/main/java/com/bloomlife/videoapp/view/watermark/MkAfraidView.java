package com.bloomlife.videoapp.view.watermark;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bloomlife.android.common.util.UiUtils;
import com.bloomlife.videoapp.R;

/**
 * Created by zhengxingtian lan4627@Gmail.com on 2015/8/26.
 */
public class MkAfraidView extends BaseMkView {
    public MkAfraidView(Context context) {
        super(context);
    }

    public MkAfraidView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MkAfraidView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public MkAfraidView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    private ViewGroup mTopGroup;
    private ValueAnimator mDescAnimator;
    private String mDescStr;

    @Override
    protected void initLayout(Context context) {
        inflate(context, R.layout.view_mk_afraid, this);
        mDate = (TextView) findViewById(R.id.mk_afraid_date);
        mHour = (TextView) findViewById(R.id.mk_afraid_hour);
        mLocation = (TextView) findViewById(R.id.mk_afraid_local);
        mDescription = (TextView) findViewById(R.id.mk_afraid_desc);
        mTopGroup = (ViewGroup) findViewById(R.id.mk_afraid_top_group);
    }

    @Override
    public void hideAll() {
        if (mDescAnimator != null && mDescAnimator.isRunning()){
            mDescAnimator.cancel();
        }
        mTopGroup.setVisibility(GONE);
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
        if (time > 600 && !mTopGroup.isShown()){
            mTopGroup.setVisibility(VISIBLE);
            ObjectAnimator animator = ObjectAnimator.ofFloat(mTopGroup, "alpha", 0.0f, 1.0f);
            animator.setRepeatCount(4);
            animator.start();
        }
        if (time > 500 && !mDescription.isShown() && !TextUtils.isEmpty(mDescStr)){
            mDescription.setVisibility(VISIBLE);
            mDescAnimator = ValueAnimator.ofInt(0, mDescStr.length() - 1);
            mDescAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener(){

                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    int position = (int) animation.getAnimatedValue();
                    mDescription.setText(mDescStr.substring(0, position + 1));
                }
            });
            mDescAnimator.setDuration(mDescStr.length() * 150);
            mDescAnimator.start();
        }
    }

    @Override
    protected void setText(String city, String description) {
        mLocation.setText(city);
        mDescStr = description;
        if (TextUtils.isEmpty(description))
            return;
        StaticLayout staticLayout = new StaticLayout(
                description,
                mDescription.getPaint(),
                UiUtils.dip2px(getContext(), 320),
                Layout.Alignment.ALIGN_NORMAL,
                1,
                0,
                false
        );
        ViewGroup.LayoutParams params = mDescription.getLayoutParams();
        params.height = staticLayout.getHeight();
        mDescription.setLayoutParams(params);
    }
}
