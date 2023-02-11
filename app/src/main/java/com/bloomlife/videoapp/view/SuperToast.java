/**
 * 
 */
package com.bloomlife.videoapp.view;

import com.bloomlife.videoapp.R;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.jdesktop.application.Resource;

/**
 * @author <a href="mailto:lan4627@gmail.com">zhengxingtian</a>
 *
 * @date 2014年12月15日 下午5:03:24
 */
public class SuperToast extends Toast {

	/**
	 * @param context
	 */
	public SuperToast(Context context, String text) {
		super(context);
		init(context, text);
	}
	
	public SuperToast(Context context, int res) {
		super(context);
		init(context, res);
	}
	
	private void init(Context context, String text){
		View view = LayoutInflater.from(context).inflate(R.layout.view_super_toast, null);
		((TextView)view.findViewById(R.id.view_super_toast_text)).setText(text);
		setView(view);
		setDuration(Toast.LENGTH_SHORT);
		setGravity(Gravity.CENTER, 0, 0);
		show();
	}
	
	private void init(Context context, int res){
		init(context, context.getString(res));
	}
	
	public static SuperToast show(Context context, String text){
		SuperToast toast = new SuperToast(context, text);
		toast.show();
		return toast;
	}

	public static SuperToast show(Context context, int res){
		SuperToast toast = new SuperToast(context, res);
		toast.show();
		return toast;
	}

}
