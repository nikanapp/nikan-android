/**
 * 
 */
package com.bloomlife.android.framework;

import net.tsz.afinal.FinalActivity;

import android.app.Activity;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.android.volley.toolbox.Volley;
import com.bloomlife.android.R;
import com.jfeinstein.jazzyviewpager.Util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

/**
 * @author <a href="mailto:xiai.fei@gmail.com">xiai_fei</a>  
 *
 * @date 2014-6-2  下午12:03:28
 */
public class BaseActivity extends FinalActivity {


	private Object mActivityTag;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		((AppContext)getApplication()).addActivity(this);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		mActivityTag = Volley.getTag();
		setStatusBar();
	}

	protected void setStatusBar() {
		// 改变状态栏背景和状态栏字体图片的颜色，MiUI6以上，Flyme4以上，其他的机型要Android6.0及以上
		if (Util.isMiUI()) {
			setMiUIStatusBarStyle(isLightStatusBarStyle());
		} else if (Util.isFlyme()) {
			setFlymeStatusBarStyle(isLightStatusBarStyle());
		} else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			if (isLightStatusBarStyle()) {
				setLightStatusBarStyle();
			} else {
				setDarkStatusBarStyle();
			}
		} else {
			if (isLightStatusBarStyle()) {
				getWindow().setStatusBarColor(Util.getColor(this, R.color.low_translucent_status_bar));
			} else {
				getWindow().setStatusBarColor(Util.getColor(this, R.color.app_black));
			}
		}
	}

	protected void setNavigationBar() {
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
		if (isLightStatusBarStyle()) {
			getWindow().setNavigationBarColor(Util.getColor(this, R.color.navigation_background));
		} else {
			getWindow().setNavigationBarColor(Util.getColor(this, R.color.app_black));
		}
	}

	/**
	 * 默认设置状态栏为暗色的样式，只会在版本大于等于Android M的机型上执行
	 *
	 * @return
	 */
	protected boolean isLightStatusBarStyle() {
		return false;
	}

	/**
	 * 开启全屏
	 */
	protected void fullScreen() {
		int flags = getWindow().getDecorView().getSystemUiVisibility();
		try {
			getWindow().getDecorView().setSystemUiVisibility(
					flags | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 设置状态栏为亮色的样式，只会在版本大于等于Android M的机型上执行
	 *
	 * @return
	 */
	public void setLightStatusBarStyle() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			int flags = getWindow().getDecorView().getSystemUiVisibility();
			getWindow().getDecorView().setSystemUiVisibility(flags | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
			getWindow().setStatusBarColor(getColor(R.color.transparent));
		}
	}

	/**
	 * 设置状态栏为暗色的样式，只会在版本大于等于Android M的机型上执行
	 *
	 * @return
	 */
	public void setDarkStatusBarStyle() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			int flags = getWindow().getDecorView().getSystemUiVisibility();
			getWindow().getDecorView().setSystemUiVisibility(flags ^ View.SYSTEM_UI_FLAG_VISIBLE);
			getWindow().setStatusBarColor(getColor(R.color.black));
		}
	}

	private void setMiUIStatusBarStyle(boolean isLightText) {
		// MIUI在6.0以后使用原生的方案，不再使用原有的改变状态栏方法
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			if (isLightText) {
				setLightStatusBarStyle();
			} else {
				setDarkStatusBarStyle();
			}
		} else {
			Window window = getWindow();
			if (window != null) {
				Class clazz = window.getClass();
				try {
					int tranceFlag = 0;
					int darkModeFlag = 0;
					Class layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");
					Field statusBarColorField = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_TRANSPARENT");
					tranceFlag = statusBarColorField.getInt(layoutParams);
					Field statusBarTextColorField = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE");
					darkModeFlag = statusBarTextColorField.getInt(layoutParams);
					java.lang.reflect.Method extraFlagField = clazz.getMethod("setExtraFlags", int.class, int.class);
					if (isLightText) {
						//清除黑色字体
						extraFlagField.invoke(window, 0, darkModeFlag);
					} else {
						//状态栏透明且黑色字体
						extraFlagField.invoke(window, tranceFlag | darkModeFlag, tranceFlag | darkModeFlag);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void setFlymeStatusBarStyle(boolean isLightText) {
		WindowManager.LayoutParams lp = getWindow().getAttributes();
		try {
			Class<?> instance = Class.forName("android.view.WindowManager$LayoutParams");
			int value = instance.getDeclaredField("MEIZU_FLAG_DARK_STATUS_BAR_ICON").getInt(lp);
			Field field = instance.getDeclaredField("meizuFlags");
			field.setAccessible(true);
			int origin = field.getInt(lp);
			if (isLightText) {
				field.set(lp, (~value) & origin);
			} else {
				field.set(lp, origin | value);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void finish() {
		super.finish();
		Volley.cancelAll(mActivityTag);
	}

	public void onClick(View v) {
		
	}
	
	protected void onResume() {
	    super.onResume();
		Volley.setTag(mActivityTag);
	}

	protected void onPause() {
	    super.onPause();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		return super.dispatchTouchEvent(ev);
	}
}
