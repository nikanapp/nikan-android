/**
 * 
 */
package com.bloomlife.videoapp.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View.MeasureSpec;
import android.widget.ListView;

/**
 * @author <a href="mailto:lan4627@gmail.com">zxt</a>
 *
 * @date 2015年1月30日 下午7:12:44
 */
public class ListTable extends ListView {

	/**
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	public ListTable(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param context
	 * @param attrs
	 */
	public ListTable(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param context
	 */
	public ListTable(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,  
	            MeasureSpec.AT_MOST);  
	    super.onMeasure(widthMeasureSpec, expandSpec); 
	}
	
}
