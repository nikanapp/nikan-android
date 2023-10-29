/**
 * 
 */
package com.bloomlife.videoapp.view;

import java.util.List;

import com.bloomlife.videoapp.R;
import com.bloomlife.videoapp.common.util.TopicHandler;
import com.bloomlife.videoapp.common.util.TopicHandler.TopicView;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.Editable;
import android.text.Spannable;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;
import android.widget.TextView.BufferType;

/**
 * @author <a href="mailto:lan4627@gmail.com">zxt</a>
 *
 * @date 2014年12月24日 下午12:07:14
 */
public class TopicTextView extends TextView implements TopicView{

	public TopicTextView(Context context) {
		super(context);
		init(context, null);
	}
	

	public TopicTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs);
	}

	public TopicTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context, attrs);
	}
	
	private TopicHandler mTopicHandler;
	private int mColorRes;
	
	private void init(Context context, AttributeSet attrs){
		mColorRes = getTextColors().getDefaultColor();
	}
	
	@Override
    public void setText(CharSequence text, BufferType type) {
        super.setText(text, BufferType.SPANNABLE);
    }
	
	public Spannable getText(){
		return (Spannable)super.getText();
	}
	
	@Override
	protected void onTextChanged(CharSequence text, int start,
			int lengthBefore, int lengthAfter) {
//		super.onTextChanged(text, start, lengthBefore, lengthAfter);
		if (mTopicHandler == null){
			mTopicHandler = new TopicHandler(getContext());
			mTopicHandler.setTopicColorRes(mColorRes == 0 ? R.color.display_topic : mColorRes);
		}
		mTopicHandler.setTopicStyle(getText());
	}
	
	public void setColorRes(int res){
		mColorRes = res;
	}
	
	public int getColorRes(){
		return mColorRes;
	}
	
	@Override
	public List<String> getTopicList() {
		return mTopicHandler.getTopicList(getText());
	}

}
