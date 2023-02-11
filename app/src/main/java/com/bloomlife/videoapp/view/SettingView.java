/**
 * 
 */
package com.bloomlife.videoapp.view;

import com.bloomlife.videoapp.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * @author <a href="mailto:xiai.fei@gmail.com">xiai_fei</a>  
 *
 * @date 2014-12-31  下午6:46:37
 */
public class SettingView extends RelativeLayout{
	
	private TextView textView ;

	/**
	 * @param context
	 * @param attrs
	 */
	public SettingView(Context context, AttributeSet attrs) {
		super(context, attrs);
		inflate(context, R.layout.view_setting_view, this);
		textView = (TextView) findViewById(R.id.settingText);
		if (attrs != null){
			TypedArray array= context.obtainStyledAttributes(attrs, R.styleable.setting_view);
			String settingText = array.getString(R.styleable.setting_view_setting_text);
			textView.setText(settingText);
			array.recycle();
		}
	}

}
