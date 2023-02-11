package com.bloomlife.videoapp.view.watermark;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bloomlife.android.common.util.DateUtils;
import com.bloomlife.videoapp.common.util.UIHelper;

/**
 * Created by zhengxingtian lan4627@Gmail.com on 2015/8/26.
 */
public abstract class BaseMkView extends RelativeLayout {
    public BaseMkView(Context context) {
        super(context);
        init(context);
    }

    public BaseMkView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public BaseMkView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public BaseMkView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    protected TextView mDescription;
    protected TextView mLocation;
    protected TextView mHour;
    protected TextView mDate;

    private boolean mPause;

    protected void init(Context context){
        initLayout(context);

        Typeface bebas = UIHelper.getHelveticaTh(context);
        mHour.setTypeface(bebas);
        mDate.setTypeface(bebas);
        // 隐藏所有视图
        hideAll();
        // 清除所有文字
        clearText();
    }

    protected abstract void initLayout(Context context);

    public void hideAll(){
        mDate.setVisibility(View.GONE);
        mHour.setVisibility(View.GONE);
        mLocation.setVisibility(View.GONE);
        mDescription.setVisibility(View.GONE);
    }

    public void showAll(){
        mDate.setVisibility(View.VISIBLE);
        mHour.setVisibility(View.VISIBLE);
        mLocation.setVisibility(View.VISIBLE);
        mDescription.setVisibility(View.VISIBLE);
    }

    public boolean isPause(){
        return mPause;
    }

    public void pause(){
        mPause = true;
        pauseAnimator();
    }

    public void resume(){
        mPause = false;
        resumeAnimator();
    }


    abstract protected void pauseAnimator();

    abstract protected void resumeAnimator();

    public void clearText(){
        mDate.setText("");
        mHour.setText("");
        mLocation.setText("");
        mDescription.setText("");
    }

    public void setInfo(String city, String description, long date){
        if (TextUtils.isEmpty(city) || TextUtils.isEmpty(description))
            return;
        setDate(date);
        setText(city, description);
    }

    protected void setDate(long date){
        mDate.setText(DateUtils.getVideoCreateTimeStr(date));
        mHour.setText(DateUtils.formatTimesToHour(date));
    }

    protected void setText(String city, String description){
        mDescription.setText(description);
        mLocation.setText(city);
    }

    public abstract void setCurrentTime(long time, long totalTime);

}
