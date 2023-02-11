package com.bloomlife.videoapp.view.watermark;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bloomlife.videoapp.R;
import com.bloomlife.videoapp.common.util.UIHelper;

/**
 * Created by zhengxingtian lan4627@Gmail.com on 2015/8/26.
 */
public class MkLonelyView extends BaseMkView {

    public MkLonelyView(Context context) {
        super(context);
    }

    public MkLonelyView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MkLonelyView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public MkLonelyView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    private ImageView mCenterView;
    private FrameLayout mRandomLayout;
    private ViewGroup mInfoLayout;
    private View mRandom1;
    private View mRandom2;
    private View mRandom3;

    @Override
    protected void initLayout(Context context) {
        inflate(context, R.layout.view_mk_lonely, this);
        mDate = (TextView) findViewById(R.id.mk_lonely_date);
        mHour = (TextView) findViewById(R.id.mk_lonely_hour);
        mLocation = (TextView) findViewById(R.id.mk_lonely_local);
        mDescription = (TextView) findViewById(R.id.mk_lonely_desc);
        mCenterView = (ImageView) findViewById(R.id.mk_lonely_center_dot);
        mRandomLayout = (FrameLayout) findViewById(R.id.mk_lonely_random_layout);
        mInfoLayout = (ViewGroup) findViewById(R.id.mk_lonely_info_layout);

        mRandom1 = createRandomView();
        mRandom2 = createRandomView();
        mRandom3 = createRandomView();

        Animator animator1 = createRandomAnimator(mRandom1);
        animator1.setDuration(2000);
        mRandom1.setTag(animator1);

        Animator animator2 = createRandomAnimator(mRandom2);
        animator2.setDuration(1000);
        mRandom2.setTag(animator2);

        Animator animator3 = createRandomAnimator(mRandom3);
        animator3.setDuration(2000);
        mRandom3.setTag(animator3);
    }

    @Override
    protected void setText(String city, String description) {
        mLocation.setText(city);
        String first = "“  ";
        String last = "  ”";
        SpannableStringBuilder strBuilder = new SpannableStringBuilder(first+description+last);

        strBuilder.setSpan(getImageSpan(R.drawable.mk_lonely_desc_first), 0, 1, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        strBuilder.setSpan(
                getImageSpan(R.drawable.mk_lonely_desc_last),
                description.length() + first.length() + last.length() - 1,
                description.length() + first.length() + last.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE
        );
        mDescription.setText(strBuilder);
    }

    private ImageSpan getImageSpan(int resId){
        Drawable drawable = getResources().getDrawable(resId);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        return new ImageSpan(drawable, ImageSpan.ALIGN_BASELINE);
    }

    @Override
    public void hideAll() {
        mInfoLayout.setVisibility(GONE);
        mRandom1.setVisibility(GONE);
        mRandom2.setVisibility(GONE);
        mRandom3.setVisibility(GONE);
        mCenterView.setVisibility(GONE);
    }

    @Override
    public void pauseAnimator() {

    }

    @Override
    public void resumeAnimator() {

    }

    @Override
    public void setCurrentTime(long time, long totalTime) {
        if (!mInfoLayout.isShown()){
            mInfoLayout.setVisibility(View.VISIBLE);
            ObjectAnimator.ofFloat(mInfoLayout, "alpha", 0.0f, 1.0f).setDuration(1000).start();
        }
        if (time > 800 && !mRandom1.isShown()){
            startRandomView(mRandom1);
        } else if (time > 1000 && !mRandom2.isShown()){
            startRandomView(mRandom2);
        } else if (time > 1400 && !mRandom3.isShown()){
            startRandomView(mRandom3);
        }

        if (time > 2000 && !mCenterView.isShown()){
            mCenterView.setVisibility(VISIBLE);
            startCenterAnimator();
        }
    }

    private void startRandomView(View view){
        view.setVisibility(VISIBLE);
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) view.getLayoutParams();
        params.setMargins(randomMarginLeft(view), randomMarginTop(view), 0, 0);
        view.setLayoutParams(params);
        mRandomLayout.addView(view);
        Animator animator = (Animator) view.getTag();
        animator.start();
    }

    private int randomMarginLeft(View view){
        UIHelper.measure(view);
        return (int) (Math.random() * (mRandomLayout.getWidth() - view.getMeasuredWidth()));
    }

    private int randomMarginTop(View view) {
        UIHelper.measure(view);
        return (int) (Math.random() * (mRandomLayout.getHeight() - view.getMeasuredHeight()));
    }


    private View createRandomView() {
        ImageView iv = new ImageView(getContext());
        iv.setBackgroundResource(R.drawable.background_mk_lonely_random);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
        );
        iv.setLayoutParams(params);
        return iv;
    }

    private Animator createRandomAnimator(final View view) {
        AnimatorSet alphaAnim = new AnimatorSet();
        alphaAnim.playSequentially(
                ObjectAnimator.ofFloat(view, "alpha", 0.0f, 0.4f),
                ObjectAnimator.ofFloat(view, "alpha", 0.4f, 0.0f)
        );

        AnimatorSet totalAnim = new AnimatorSet();
        totalAnim.playTogether(
                ObjectAnimator.ofFloat(view, "scaleY", 0, 4.2f),
                ObjectAnimator.ofFloat(view, "scaleX", 0, 4.2f),
                alphaAnim
        );
        totalAnim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                view.setAlpha(0.0f);
                mRandomLayout.removeView(view);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        return totalAnim;
    }

    private void startCenterAnimator(){
        AnimatorSet animatorSet1 = new AnimatorSet();
        animatorSet1.playTogether(
                ObjectAnimator.ofFloat(mCenterView, "scaleY", 0, 6),
                ObjectAnimator.ofFloat(mCenterView, "scaleX", 0, 6),
                ObjectAnimator.ofFloat(mCenterView, "alpha", 0.0f, 0.7f)
        );
        animatorSet1.setDuration(1000);

        AnimatorSet animatorSet2 = new AnimatorSet();
        animatorSet2.playTogether(
                ObjectAnimator.ofFloat(mCenterView, "scaleY", 6, 2),
                ObjectAnimator.ofFloat(mCenterView, "scaleX", 6, 2),
                ObjectAnimator.ofFloat(mCenterView, "alpha", 0.7f, 1.0f)
        );
        animatorSet2.setDuration(2000);


        AnimatorSet animatorSet3 = new AnimatorSet();
        animatorSet3.playTogether(
                ObjectAnimator.ofFloat(mCenterView, "scaleY", 2, 8),
                ObjectAnimator.ofFloat(mCenterView, "scaleX", 2, 8),
                ObjectAnimator.ofFloat(mCenterView, "alpha", 1.0f, 0.0f)
        );
        animatorSet3.setDuration(800);

        AnimatorSet totalAnim = new AnimatorSet();
        totalAnim.playSequentially(animatorSet1, animatorSet2, animatorSet3);
        totalAnim.start();
    }

}
