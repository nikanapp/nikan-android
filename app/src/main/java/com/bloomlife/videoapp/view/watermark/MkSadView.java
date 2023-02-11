package com.bloomlife.videoapp.view.watermark;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.text.Html;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.bloomlife.android.common.util.StringUtils;
import com.bloomlife.videoapp.R;
import com.bloomlife.videoapp.common.util.UIHelper;
import com.bloomlife.videoapp.common.util.Utils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by zhengxingtian lan4627@Gmail.com on 2015/8/26.
 * 忧伤水印
 */
public class MkSadView extends BaseMkView {

    public MkSadView(Context context) {
        super(context);
    }

    public MkSadView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MkSadView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public MkSadView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    private static final int DESC_DURATION = 2000;
    private static final int FLASH_DURATION = 1000;
    private static final int FLASH_NUM = 6;

    private FrameLayout mDescLayout;
    private List<String> mWordList;
    private List<TextView> mDescViewList;

    private Handler mHandler = new Handler();
    private Animator mCurrentPlayAnimator;
    private int mPosition;

    private long mTime;
    private long mTotalTime;

    @Override
    protected void initLayout(Context context) {
        inflate(context, R.layout.view_mk_sad, this);

        mDate = (TextView) findViewById(R.id.mk_sad_date);
        mHour = (TextView) findViewById(R.id.mk_sad_hour);
        mLocation = (TextView) findViewById(R.id.mk_sad_local);
        mDescription = (TextView) findViewById(R.id.mk_sad_date_desc);
        mDescLayout = (FrameLayout) findViewById(R.id.mk_sad_desc_layout);

        mDescViewList = new LinkedList<>();
        for (int i=0; i<4; i++){
            mDescViewList.add(createWordView());
        }
    }

    @Override
    public void clearText() {
        mDate.setText(null);
        mHour.setText(null);
        mLocation.setText(null);
        mDescription.setText(null);
        for (TextView tv:mDescViewList){
            tv.setText(null);
        }
    }

    @Override
    public void hideAll() {
        mDate.setVisibility(GONE);
        mHour.setVisibility(GONE);
        mLocation.setVisibility(GONE);
        mDescLayout.setVisibility(GONE);
        mDescription.setVisibility(GONE);
        if (mHandler != null)
            mHandler.removeCallbacks(mFlashTextRunnable);
    }

    @Override
    public void pauseAnimator() {
        if (mHandler != null)
            mHandler.removeCallbacks(mFlashTextRunnable);
        if (mCurrentPlayAnimator != null){
            mCurrentPlayAnimator.cancel();
        }
    }

    @Override
    public void resumeAnimator() {
        if (mHandler != null && mPosition < mWordList.size()){
            mHandler.post(mFlashTextRunnable);
        }
    }

    @Override
    protected void setText(String city, String description) {
        mWordList = new ArrayList<>();
        StringBuilder descBuilder = new StringBuilder();
        for (String word:StringUtils.createWordList(description)){
            String newWord = appendSpace(word);
            mWordList.add(newWord);
//            descBuilder.append(newWord).append(" &nbsp&nbsp&nbsp ");
            descBuilder.append(newWord).append("   ");
        }
        mLocation.setText(city);
        mDescription.setText(Html.fromHtml("<html><body style=\"text-align：justify; \">" + descBuilder.toString() + "</body></html>"));
        mDescription.setText(descBuilder.toString());
    }

    private String appendSpace(String str){
        int length = str.length();
        char[] value = new char[length << 2];
        for (int i=0, j=0; i<length; ++i, j = i << 2) {
            value[j] = str.charAt(i);
            value[1 + j] = ' ';
            value[2 + j] = ' ';
            value[3 + j] = ' ';
        }
        return new String(value);
    }

    private TextView createWordView(){
        TextView tv = new TextView(getContext());
        tv.setTextColor(Color.WHITE);
        tv.setVisibility(INVISIBLE);
        descViewAttachAnimator(tv);
        return tv;
    }

    @Override
    public void setCurrentTime(long time, long totalTime){
        mTime = time;
        mTotalTime = totalTime;
        // 显示时间和地点
        if (!mDate.isShown()){
            mDate.setVisibility(VISIBLE);
            mHour.setVisibility(VISIBLE);
            mLocation.setVisibility(VISIBLE);
        }
        // 开始播放字体动画
        if (!mDescLayout.isShown() && !Utils.isEmpty(mWordList)){
            mDescLayout.setVisibility(VISIBLE);
            mHandler.postDelayed(mFlashTextRunnable, 0);
        }
    }

    // 按照顺序播放每一个单词的动画在屏幕上的随机位置
    private Runnable mFlashTextRunnable = new Runnable() {
        @Override
        public void run() {
            TextView tv = mDescViewList.remove(0);
            tv.setText(mWordList.get(mPosition++));
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.WRAP_CONTENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(randomMarginLeft(tv), randomMarginTop(tv), 0, 0);
            tv.setLayoutParams(params);
            mDescLayout.addView(tv);
            mCurrentPlayAnimator = (Animator) tv.getTag();
            mCurrentPlayAnimator.start();
            if (mPosition == FLASH_NUM || ((mTime + FLASH_DURATION) > (mTotalTime / 2))){
                mCurrentPlayAnimator.addListener(mEndListener);
            } else if(mPosition < mWordList.size()){
                mHandler.postDelayed(mFlashTextRunnable, FLASH_DURATION);
            }
        }
    };

    private Animator.AnimatorListener mEndListener = new Animator.AnimatorListener() {
        @Override
        public void onAnimationStart(Animator animation) {

        }

        @Override
        public void onAnimationEnd(Animator animation) {
            showDescription();
        }

        @Override
        public void onAnimationCancel(Animator animation) {
            showDescription();
        }

        @Override
        public void onAnimationRepeat(Animator animation) {

        }
    };

    private void showDescription(){
        mPosition = 0;
        mDescription.setVisibility(VISIBLE);
    }

    private int randomMarginLeft(TextView tv){
        UIHelper.measure(tv);
        return (int) (Math.random() * (mDescLayout.getWidth() - tv.getMeasuredWidth()));
    }

    private int randomMarginTop(TextView tv){
        UIHelper.measure(tv);
        return (int) (Math.random() * (mDescLayout.getHeight() - tv.getMeasuredHeight()));
    }

    private void descViewAttachAnimator(final TextView tv){
        ValueAnimator animator = ValueAnimator.ofFloat(0.0f, 1.0f);
        animator.setDuration(DESC_DURATION);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                tv.setScaleX(value * 3);
                tv.setScaleY(value * 3);
                tv.setAlpha(1.0f - value);
            }
        });
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                tv.setVisibility(VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                tv.setVisibility(GONE);
                mDescLayout.removeView(tv);
                mDescViewList.add(tv);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        tv.setTag(animator);
    }

}
