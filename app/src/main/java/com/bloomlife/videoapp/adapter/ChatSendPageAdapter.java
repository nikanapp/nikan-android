/**
 * 
 */
package com.bloomlife.videoapp.adapter;

import com.bloomlife.videoapp.view.ChatSendToolController;
import com.bloomlife.videoapp.view.ChatAudioRecorderController;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.viewpager.widget.PagerAdapter;

/**
 * @author <a href="mailto:lan4627@gmail.com">zxt</a>
 *
 * @date 2015年4月29日 下午4:08:00
 */
public class ChatSendPageAdapter extends PagerAdapter {

	private Context mContext;
	private InstantiateItemListener mListener;

	public ChatSendPageAdapter(Context context, InstantiateItemListener l) {
		mContext = context;
		mListener = l;
	}

	@Override
	public int getCount() {
		return 2;
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == arg1;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		if (mListener != null){
			mListener.destroyItem((View) object);
		}
		container.removeView((View) object);
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		View view = null;
		if (position == 0){
			view = new ChatSendToolController(mContext);
		} else {
			view = new ChatAudioRecorderController(mContext);
		}
		if (mListener != null){
			mListener.createItem(view);
		}
		container.addView(view);
		return view;
	}
	
	public interface InstantiateItemListener{
		void createItem(View view);
		void destroyItem(View view);
	}
	
}
