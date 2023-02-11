/**
 * 
 */
package com.bloomlife.videoapp.view;

import com.bloomlife.videoapp.R;
import com.bloomlife.videoapp.common.util.UIHelper;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * @author <a href="mailto:lan4627@gmail.com">zhengxingtian</a>
 * 消息页面的提示组件，使用在没有更多，暂无消息，暂无通知
 * @date 2015年6月4日 下午4:55:12
 */
public class FooterTipsView extends LinearLayout {

	/**
	 * @param context
	 */
	public FooterTipsView(Context context) {
		super(context);
		init(context, null);
	}
	
	public FooterTipsView(Context context, CharSequence text) {
		super(context);
		init(context, text);
	}

	/**
	 * @param context
	 * @param attrs
	 */
	public FooterTipsView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, null);
	}

	/**
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	public FooterTipsView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context, null);
	}
	
	private TextView mContent;
	
	private void init(Context context, CharSequence contentText){
		inflate(context, R.layout.view_message_list_tips, this);
		setGravity(Gravity.CENTER);
		setOrientation(HORIZONTAL);
		mContent = (TextView) findViewById(R.id.message_list_tips_text);
		mContent.setTypeface(UIHelper.getHelveticaTh(context));
		if (!TextUtils.isEmpty(contentText))
			setText(contentText);
	}
	
	public void setText(CharSequence chars){
		mContent.setText(chars);
	}

}
