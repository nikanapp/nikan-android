/**
 * 
 */
package com.bloomlife.android.view;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * 
 * 	提供一个方法，当关联了对应的样式文件之后，提供一个方法，通过参数决定样式
 * 
 * @author <a href="mailto:xiai.fei@gmail.com">xiai_fei</a>  
 *
 * @date 2014-7-6  下午1:08:49
 */
public class StyleTextView extends TextView implements StyleSelector{

	/**
	 * @param context
	 * @param attrs
	 */
	public StyleTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	
	private List<Integer> resourceList = new ArrayList<Integer>();
	
	@Override
	public void setResources(Integer... args) {
		if(args==null) return ;
		resourceList.clear();
		for (Integer integer : args) {
			resourceList.add(integer);
		}
	}

	@Override
	public void changeStyle(int resSeq) {
		if(resourceList.size()<=resSeq) return ;
		this.setBackgroundResource(resourceList.get(resSeq));
	}
	
	
	
}
