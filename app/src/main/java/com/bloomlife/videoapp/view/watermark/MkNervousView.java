package com.bloomlife.videoapp.view.watermark;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bloomlife.android.common.util.StringUtils;
import com.bloomlife.videoapp.R;
import com.bloomlife.videoapp.common.util.Utils;

import java.util.List;

/**
 * Created by zxt lan4627@Gmail.com on 2015/8/26.
 */
public class MkNervousView extends BaseMkView {
    public MkNervousView(Context context) {
        super(context);
    }

    public MkNervousView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MkNervousView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public MkNervousView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    private static final int FREQUENCY = 3;

    private ViewGroup mInfoGroup;
    private TextView mFlashText;
    private List<String> mFlashWordList;

    private ValueAnimator mFlashAnimator;
    private long mPauseTime;

    @Override
    protected void initLayout(Context context) {
        inflate(context, R.layout.view_mk_nervous, this);
        mDate = (TextView) findViewById(R.id.mk_nervous_date);
        mHour = (TextView) findViewById(R.id.mk_nervous_hour);
        mLocation = (TextView) findViewById(R.id.mk_nervous_local);
        mDescription = (TextView) findViewById(R.id.mk_nervous_desc);
        mInfoGroup = (ViewGroup) findViewById(R.id.mk_nervous_info_group);
        mFlashText = (TextView) findViewById(R.id.mk_nervous_flash_text);
    }

    @Override
    public void hideAll() {
        if (mFlashAnimator != null && mFlashAnimator.isRunning()) {
            mFlashAnimator.cancel();
        }
        mInfoGroup.setVisibility(GONE);
        mFlashText.setVisibility(GONE);
    }

    @Override
    public void pauseAnimator() {
        if (mFlashAnimator != null && mFlashAnimator.isRunning()){
            mPauseTime = mFlashAnimator.getCurrentPlayTime();
            mFlashAnimator.cancel();
        }
    }

    @Override
    public void resumeAnimator() {
        if (mFlashAnimator != null && !mFlashAnimator.isRunning()){
            mFlashAnimator = createFlashTextAnimator();
            mFlashAnimator.setCurrentPlayTime(mPauseTime);
            mFlashAnimator.start();
        }
    }

    @Override
    public void setCurrentTime(long time, long totalTime) {
        if (!mFlashText.isShown() && !Utils.isEmpty(mFlashWordList)){
            mFlashText.setVisibility(VISIBLE);
            mFlashAnimator = createFlashTextAnimator();
            mFlashAnimator.start();
        }
        if (time > 3800 && !mInfoGroup.isShown()){
            mInfoGroup.setVisibility(VISIBLE);
            if (mFlashAnimator.isRunning()){
                mFlashAnimator.cancel();
            }
        }
    }

    private ValueAnimator createFlashTextAnimator(){
        ValueAnimator animator = ValueAnimator.ofInt(0, mFlashWordList.size() - 1);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int position = (int) animation.getAnimatedValue();
                mFlashText.setText(mFlashWordList.get(position));
            }
        });
        animator.setDuration(mFlashWordList.size() * 100);
        animator.addListener(mFlashAnimatorListener);
        animator.setRepeatCount(FREQUENCY);
        animator.setRepeatMode(ValueAnimator.RESTART);
        return animator;
    }

    private Animator.AnimatorListener mFlashAnimatorListener = new Animator.AnimatorListener() {
        @Override
        public void onAnimationStart(Animator animation) {
            mInfoGroup.setVisibility(GONE);
            mFlashText.setAlpha(1.0f);
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            mFlashText.setAlpha(0.0f);
        }

        @Override
        public void onAnimationCancel(Animator animation) {

        }

        @Override
        public void onAnimationRepeat(Animator animation) {

        }
    };

    @Override
    protected void setText(String city, String description) {
        mLocation.setText(city);
        mDescription.setText(description);
        mFlashWordList = StringUtils.createWordList(description);
    }


}
