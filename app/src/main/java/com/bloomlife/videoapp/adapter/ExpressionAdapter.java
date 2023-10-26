/**
 * 
 */
package com.bloomlife.videoapp.adapter;

import java.util.ArrayList;
import java.util.List;

import com.bloomlife.videoapp.model.Dynamicimg;
import com.bloomlife.videoapp.view.ExpressionLayout;
import com.bloomlife.videoapp.view.dialog.ExpressionWindow.ExpressionListener;

import android.content.Context;
import androidx.core.view.PagerAdapter;

import android.view.View;
import android.view.ViewGroup;

/**
 * @author <a href="mailto:lan4627@gmail.com">zhengxingtian</a>
 *
 * @date 2015年4月15日 下午5:51:20
 */
public class ExpressionAdapter extends PagerAdapter {

	private Context mContext;
	private ExpressionListener mListener;
	private List<List<Dynamicimg>> mDynamicimgs;
	
	public ExpressionAdapter(Context context, List<Dynamicimg> imgs) {
		mContext = context;
		mDynamicimgs = new ArrayList<List<Dynamicimg>>();
		List<Dynamicimg> dynamicPage = null;
		for (int i=0; i<imgs.size(); i++){
			if (i % ExpressionLayout.LENGTH == 0){
				dynamicPage = new ArrayList<Dynamicimg>();
				mDynamicimgs.add(dynamicPage);
			}
			if (dynamicPage != null)
				dynamicPage.add(imgs.get(i));
		}
	}
	
	public void setExpressionListener(ExpressionListener l){
		mListener = l;
	}

	@Override
	public int getCount() {
		return mDynamicimgs == null ? 0 :mDynamicimgs.size();
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == arg1;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView((View)object);
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		ExpressionLayout view = new ExpressionLayout(mContext);
		view.setExpressionListener(mListener);
		view.setImages(mDynamicimgs.get(position));
		container.addView(view);
		return view;
	}
	
	

}
