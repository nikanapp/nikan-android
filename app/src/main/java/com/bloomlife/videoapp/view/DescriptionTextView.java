package com.bloomlife.videoapp.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.bloomlife.android.common.util.UiUtils;
import com.bloomlife.videoapp.common.util.UIHelper;

import java.util.regex.Pattern;

/**
 * Created by zxt lan4627@Gmail.com on 2015/7/6.
 * 针对地图主页视频描述英文换行问题处理的TextView
 */
public class DescriptionTextView extends View {

    public DescriptionTextView(Context context) {
        super(context);
        init(context);
    }

    public DescriptionTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public DescriptionTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private Paint mTextPaint;
    private String mText;
    private float[] mCharWidths;
    private Rect mRect;
    private int mRowDividerHeight;
    private float mRowWidth;
    private Pattern mPattern;

    private void init(Context context){
        mTextPaint = new Paint();
        mTextPaint.setColor(Color.WHITE);
        mTextPaint.setAntiAlias(true);
        mTextPaint.setTypeface(UIHelper.getHelveticaTh(context));
        mTextPaint.setTextSize(UiUtils.sp2px(context, 12));
        mRect = new Rect();
        mPattern = Pattern.compile("[a-zA-Z]{2}");

        mRowDividerHeight = UiUtils.dip2px(context, 3);
        mRowWidth = mTextPaint.measureText("-");
    }

    public void setText(String text){
        mText = text;
        mCharWidths = new float[text.length()];
        invalidate();
    }

    public void setTextSize(int size){
        mTextPaint.setTextSize(UiUtils.sp2px(getContext(), size));
    }

    public void setTypeface(Typeface tf){
        mTextPaint.setTypeface(tf);
    }

    public void setTextColor(int color){
        mTextPaint.setColor(color);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mText == null || mCharWidths == null) return;
        int width = getMeasuredWidth();
        mTextPaint.getTextWidths(mText, mCharWidths);
        float textWidth = 0;
        float size = 0;
        int start = 0;
        int drawBottom = 0;
        for (int i=0; i < mCharWidths.length; i++){
            size = mCharWidths[i];
            // 累计字的宽度
            textWidth += size;
            // 当字数达到一行时，需要把这一行画到View上
            if (textWidth >= width- mRowWidth){
                // 判断是否是英语单词，是的话在换行处加个‘-’
                if (mPattern.matcher(mText.substring(i - 1, i + 1)).matches()){
                    drawBottom = drawText(canvas, mText.substring(start, --i)+"-", drawBottom);
                } else {
                    drawBottom = drawText(canvas, mText.substring(start, i), drawBottom);
                }
                textWidth = 0;
                start = i;
            }
            // 到最后一个字了，需要把这最后一行也画到View上
            if (i == mCharWidths.length-1){
                drawText(canvas, mText.substring(start, mCharWidths.length), drawBottom);
            }
        }
    }

    private int drawText(Canvas canvas, String text, int drawBottom){
        mTextPaint.getTextBounds(text, 0, text.length(), mRect);
        drawBottom += mRect.height() + mRowDividerHeight;
        canvas.drawText(text, 0, text.length(), 0, drawBottom, mTextPaint);
        return drawBottom;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

    }
}
