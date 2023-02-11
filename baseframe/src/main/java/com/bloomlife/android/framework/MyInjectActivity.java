/**
 * 
 */
package com.bloomlife.android.framework;

import java.lang.reflect.Field;

import net.tsz.afinal.FinalActivity;
import android.app.Activity;
import android.view.View;

/**
 * @author <a href="mailto:xiai.fei@gmail.com">xiai_fei</a>  
 *
 * @date 2014-12-11  下午10:01:05
 */
public class MyInjectActivity extends FinalActivity{

	public static void initInjectedView(Activity activity){
		initInjectedView(activity, activity.getWindow().getDecorView());
	}
	
	public static void initInjectedView(Object injectedSource,View sourceView){
		FinalActivity.initInjectedView(injectedSource, sourceView);
		
		Field[] fields = injectedSource.getClass().getSuperclass().getDeclaredFields();
		if(fields!=null && fields.length>0){
			for(Field field : fields){
				try {
					field.setAccessible(true);
					
					if(field.get(injectedSource)!= null ) continue;
					injectField(injectedSource, sourceView, field);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
}
