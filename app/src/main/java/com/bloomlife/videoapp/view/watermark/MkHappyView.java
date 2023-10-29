package com.bloomlife.videoapp.view.watermark;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Color;
import android.text.Layout;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.TextView;

import com.bloomlife.android.common.util.UiUtils;
import com.bloomlife.videoapp.R;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by zxt lan4627@Gmail.com on 2015/8/26.
 */
public class MkHappyView extends BaseMkView {

    public MkHappyView(Context context) {
        super(context);
    }

    public MkHappyView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MkHappyView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public MkHappyView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    private ViewGroup mDateGroup;
    private ViewGroup mDescGroup;
    private List<TextView> mDescViewList;
    private List<Animator> mDescAnimatorList;
    private TextView mFirstLineFirstText;
    private TextView mFirstLineLastText;

    @Override
    protected void initLayout(Context context) {
        inflate(context, R.layout.view_mk_happy, this);

        mDateGroup = (ViewGroup) findViewById(R.id.mk_happy_date_group);
        mDate = (TextView) findViewById(R.id.mk_happy_date);
        mHour = (TextView) findViewById(R.id.mk_happy_hour);
        mLocation = (TextView) findViewById(R.id.mk_happy_local);
        mDescGroup = (ViewGroup) findViewById(R.id.mk_happy_desc_group);

        mFirstLineFirstText = (TextView) findViewById(R.id.mk_happy_desc_first_line_first);
        mFirstLineLastText = (TextView) findViewById(R.id.mk_happy_desc_first_line_last);

        mDescViewList = new ArrayList<>();
        mDescAnimatorList = new ArrayList<>();
    }

    @Override
    public void clearText() {
        mDate.setText(null);
        mHour.setText(null);
        mLocation.setText(null);
        for (TextView tv:mDescViewList){
            tv.setText(null);
        }
    }

    @Override
    public void hideAll() {
        mDateGroup.setVisibility(GONE);
        for (TextView tv:mDescViewList){
            tv.setVisibility(GONE);
        }
    }

    @Override
    public void pauseAnimator() {

    }

    @Override
    public void resumeAnimator() {

    }

    @Override
    protected void setText(String city, String description) {
        mLocation.setText(city);
        String first = getFirstStr(description);
        if (!TextUtils.isEmpty(first)){
            SpannableStringBuilder strBuilder = new SpannableStringBuilder(description);
            strBuilder.setSpan(new AbsoluteSizeSpan(32, true), 0, first.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            strBuilder.setSpan(new AbsoluteSizeSpan(24, true), first.length(), description.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);

            TextPaint textPaint = new TextPaint();
            textPaint.setTextSize(32);
            StaticLayout staticLayout = new StaticLayout(
                    strBuilder,
                    textPaint,
                    UiUtils.dip2px(getContext(), 90),
                    Layout.Alignment.ALIGN_NORMAL,
                    1,
                    0,
                    false
            );

            // 添加到描述文本列表中
            mDescViewList.clear();
            mDescViewList.add(mFirstLineFirstText);
            mDescViewList.add(mFirstLineLastText);

            // 添加到描述文本列表动画中
            mDescAnimatorList.clear();
            mDescAnimatorList.add(createTextAnimator(mFirstLineFirstText));
            mDescAnimatorList.add(createTextAnimator(mFirstLineLastText));

            // 开头的第一句是固定分开前后两段，第一段是一个大字体的单词，第二段是小字体的文字
            mFirstLineFirstText.setText(first);
            mFirstLineLastText.setText(description.substring(first.length(), staticLayout.getLineEnd(0)));
            mFirstLineLastText.setVisibility(GONE);
            mFirstLineFirstText.setVisibility(GONE);
            // 后面的文字是每一行一个TextView
            for (int i=1; i<staticLayout.getLineCount(); i++){
                int start = staticLayout.getLineStart(i);
                int end = staticLayout.getLineEnd(i);
                TextView tv  = new TextView(getContext());
                tv.setTextSize(16);
                tv.setTextColor(Color.WHITE);
                tv.setText(description.substring(start, end));
                tv.setVisibility(GONE);
                mDescGroup.addView(tv);
                mDescViewList.add(tv);
                mDescAnimatorList.add(createTextAnimator(tv));
            }
        }
    }

    /**
     * 获取第一个单词或者汉字
     * @param str
     * @return
     */
    private String getFirstStr(String str){
        String firstStr = null;
        Matcher matcherEn = Pattern.compile("^([a-zA-Z_&&[^\\s]]*)([\\u4E00-\\u9FA5\\s0-9])").matcher(str);
        if (matcherEn.find()){
            firstStr = matcherEn.group(1);
            if (TextUtils.isEmpty(firstStr)){
                firstStr = matcherEn.group(2);
            }
        }
        return firstStr;
    }

    /**
     * 创建一个从0-100缩放，有回弹效果的动画
     * @param v
     * @return
     */
    private Animator createViewAnimator(final View v){
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(
                ObjectAnimator.ofFloat(v, "scaleY", 0, 1),
                ObjectAnimator.ofFloat(v, "scaleX", 0, 1)
        );
        animatorSet.setDuration(200);
        animatorSet.setInterpolator(new OvershootInterpolator());
        return animatorSet;
    }

    private Animator createTextAnimator(final View view){
        Animator animator = createViewAnimator(view);
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                view.setVisibility(VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        return animator;
    }

    private Animator createInfoAnimator(){
        mDateGroup.setAlpha(0.0f);
        Animator animator = createViewAnimator(mDateGroup);
        animator.setDuration(800);
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                mDateGroup.setAlpha(1.0f);
            }

            @Override
            public void onAnimationEnd(Animator animation) {

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        return animator;
    }

    @Override
    public void setCurrentTime(long time, long totalTime) {
        if (time > 1500 && !mDateGroup.isShown()){
            mDateGroup.setVisibility(VISIBLE);

            // 运行描述文本的动画
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.playSequentially(mDescAnimatorList);
            animatorSet.start();

            // 运行时间地点的动画
            createInfoAnimator().start();
        }
    }

}
