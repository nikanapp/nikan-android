package com.jfeinstein.jazzyviewpager;

import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.text.TextUtils;
import android.util.TypedValue;

import com.igexin.sdk.PushManager;

import java.lang.reflect.Method;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;

public class Util {

	public static int dpToPx(Resources res, int dp) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, res.getDisplayMetrics());
	}

	public static boolean isMiUI() {
		return "Xiaomi".equals(Build.MANUFACTURER);
	}

	public static boolean isFlyme() {
		return "Meizu".equals(Build.MANUFACTURER);
	}

	public static boolean isEmUI() {
		return !TextUtils.isEmpty(getProp("ro.build.version.emui"));
	}

	public static String getProp(String key) {
		String value = "";
		try {
			Class<?> c = Class.forName("android.os.SystemProperties");
			Method get = c.getMethod("get", String.class, String.class);
			value = (String) (get.invoke(c, key, ""));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return value;
	}

	@ColorInt
	public static int getColor(Context context, @ColorRes int resId) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			return context.getResources().getColor(resId, context.getTheme());
		} else {
			return context.getResources().getColor(resId);
		}
	}

}
