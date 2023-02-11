/**
 * 
 */
package com.bloomlife.android.view;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 
 * 
 * @author <a href="mailto:xiai.fei@gmail.com">xiai_fei</a>  
 *
 * @date 2014-7-6  下午1:17:44
 */
public class StyleImageView extends ImageView implements StyleSelector{

	public StyleImageView(Context context, AttributeSet attrs) {
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
		this.setImageResource(resourceList.get(resSeq));
	}
	
}