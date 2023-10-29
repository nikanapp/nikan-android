package com.bloomlife.videoapp.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

import com.bloomlife.android.common.util.UiUtils;
import com.bloomlife.videoapp.R;

/**
 * Created by zxt lan4627@Gmail.com on 2015/7/30.
 * 用户信息对话框和好友邀请页面的指示器
 */
public class SliderView extends View {

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public SliderView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    public SliderView(Context context) {
        super(context);
        init(context);
    }

    public SliderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SliderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private Bitmap mSlider;
    private Paint mLinePaint;
    private Rect mLineRect;
    private int mLineHeight;
    private int mLeft;
    private int mNum;
    private int mTabWidth;
    private int mLeftAndRightPadding;
    private int mLinePadding;

    private void init(Context context){
        mSlider = BitmapFactory.decodeResource(context.getResources(), R.drawable.dialog_tabs_slider);
        mLinePaint = new Paint();
        mLinePaint.setAntiAlias(true);
        mLinePaint.setColor(context.getResources().getColor(R.color.dialog_edit_line_yellow));
        mLinePaint.setStyle(Paint.Style.FILL);
        if (isInEditMode()) return;
        mLineHeight = UiUtils.dip2px(getContext(), 1);
        mLinePadding = UiUtils.dip2px(getContext(), 0.5f);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mLineRect = new Rect(0, mSlider.getHeight() - mLineHeight - mLinePadding, w, mSlider.getHeight() - mLinePadding);
        if (mNum > 0)
            mTabWidth = (w - mLeftAndRightPadding * 2) / mNum;
    }

    public void setNumber(int num){
        mNum = num;
    }

    public void setSliderLeftAndRightPadding(int padding){
        mLeftAndRightPadding = padding;
    }


    public void setScrolled(int id, float scale){
        mLeft = mLeftAndRightPadding + (int) (mTabWidth * id + mTabWidth * scale) + (mTabWidth - mSlider.getWidth()) / 2 ;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawRect(mLineRect, mLinePaint);
        canvas.drawBitmap(mSlider, mLeft, 0, null);
        super.onDraw(canvas);
    }
}
