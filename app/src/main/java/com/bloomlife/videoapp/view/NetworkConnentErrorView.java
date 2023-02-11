/**
 * 
 */
package com.bloomlife.videoapp.view;

import com.bloomlife.videoapp.R;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.view.View;
import android.view.View.OnClickListener;

/**
 * @author <a href="mailto:lan4627@gmail.com">zhengxingtian</a>
 *
 * @date 2015年6月11日 下午6:42:46
 */
public class NetworkConnentErrorView extends RelativeLayout implements OnClickListener {

	/**
	 * @param context
	 */
	public NetworkConnentErrorView(Context context) {
		super(context);
		init(context);
	}

	/**
	 * @param context
	 * @param attrs
	 */
	public NetworkConnentErrorView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	/**
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	public NetworkConnentErrorView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}
	
	private void init(Context context){
		inflate(context, R.layout.view_network_connent_error, this);
		setBackgroundResource(R.drawable.background_load_video_error);
		findViewById(R.id.view_mapreload_retry).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.view_mapreload_retry:
			if (mOnRetryListener != null){
				mOnRetryListener.onRetry();
			}
			break;

		default:
			break;
		}
	}
	
	private OnRetryListener mOnRetryListener;
	
	public void setOnRetryListener(OnRetryListener l){
		mOnRetryListener = l;
	}
	
	public interface OnRetryListener{
		void onRetry();
	}

}
