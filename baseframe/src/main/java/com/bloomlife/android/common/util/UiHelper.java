/**
 * 
 */
package com.bloomlife.android.common.util;

import android.content.Context;
import android.widget.Toast;

/**
 * @author <a href="mailto:xiai.fei@gmail.com">xiai_fei</a>  
 *
 * @date 2013-11-28  下午1:33:17
 */
public class UiHelper {
	public static void longToast(Context context, CharSequence text) {
		if (context == null)
			return;
		Toast.makeText(context, text, Toast.LENGTH_LONG).show();
	}

	public static void shortToast(Context context, CharSequence text) {
		if (context == null)
			return;
		Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
	}
	
	public static void showToast(Context context, CharSequence text){
		if (context != null && text != null)
			Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
	}
	

}
