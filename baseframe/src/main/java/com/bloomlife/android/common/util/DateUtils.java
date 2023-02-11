/**
 * 
 */
package com.bloomlife.android.common.util;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import com.bloomlife.android.R;

import android.content.Context;
import android.text.format.DateFormat;
import android.text.format.Time;

/**
 * @author <a href="mailto:xiai.fei@gmail.com">xiai_fei</a>
 * 
 * @date 2014-8-29 下午2:57:17
 */
public class DateUtils {

	public static String getDualNum(int num) {
		String numText = String.valueOf(num);
		if (numText.length() == 1)
			numText = "0" + numText;
		return numText;
	}

	public static final SimpleDateFormat sf = new SimpleDateFormat("yyyyMMdd",
			Locale.CHINESE);

	public static String todaty2str() {
		return sf.format(new Date());
	}

	public static String date2str(Date date) {
		return sf.format(date);
	}

	// 返回时间字符串
	public static String getTimeString(Context context, long time) {
		String timeStr = "";
		long currentTime = System.currentTimeMillis();
		long intervalTime = currentTime / 1000 - time;
		if (intervalTime < 60) {
			// 一分钟内
			timeStr = context.getString(R.string.date_uitils_moment);
		} else if (intervalTime < 60 * 60) {
			// 一小时内
			timeStr = (int) intervalTime / 60
					+ context.getString(R.string.date_uitils_mins);
		} else if (intervalTime < 60 * 60 * 24) {
			// 一天内
			timeStr = (int) intervalTime / 60 / 60
					+ context.getString(R.string.date_uitils_hours);
		} else if (isSameYear(currentTime, time * 1000)) {
			// 是否同一年
			if (isZh())
				timeStr = (String) DateFormat.format("MM-dd", time * 1000);
			else
				timeStr = intervalTime / (60 * 60 * 24) + "d";
		} else {
			// 不同一年
			timeStr = (String) DateFormat.format("yyyy-MM-dd", time * 1000);
		}
		return timeStr;
	}

	// 判断是否在同一年
	public static Boolean isSameYear(long one, long two) {
		Time t = new Time();
		t.set(one);
		int oneYear = t.year;
		t.set(two);
		int twoYear = t.year;
		return oneYear == twoYear;
	}

	private static boolean isZh() {
		return Locale.getDefault().getLanguage().equals(Locale.CHINESE.getLanguage());
	}

	/**
	 * 活动当前日期的前num天
	 * 
	 * @return 格式化后的字符串日期 yyyy-MM-dd
	 */
	public static String getCurDateBefore(int num) {
		Calendar c = Calendar.getInstance();
		Date date = new Date();
		c.setTime(date);
		int day = c.get(Calendar.DATE);
		c.set(Calendar.DATE, day - num); // 注意
		String dayBefore = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINESE)
				.format(c.getTime());
		return dayBefore;
	}

	/***
	 * 日期转在字符串 格式 ：yyyyMMdd
	 * 
	 * @param date
	 * @return
	 */
	public static String dateToStr(Date date) {
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd", Locale.CHINESE);
		return df.format(date);
	}

	public static String dateToStr(long times) {
		Date date = new Date(times);
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
				Locale.CHINESE);
		String str = df.format(date);
		return str;
	}

	/***
	 * 格式 yyyy年MM月dd日
	 * 
	 * @param times
	 * @return
	 */
	public static String dateToSDatetr(long times) {
		Date date = new Date(times);
		SimpleDateFormat df = new SimpleDateFormat("yyyy年MM月dd日",
				Locale.CHINESE);
		String str = df.format(date);
		return str;
	}

	/***
	 * 格式 yyyy-MM-dd
	 * 
	 * @param times
	 * @return
	 */
	public static String dateToCDatetr(long times) {
		Date date = new Date(times);
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINESE);
		String str = df.format(date);
		return str;
	}

	/***
	 * 格式 HH:mm
	 * 
	 * @param times
	 * @return
	 */
	public static String formatTimesToHour(long times) {
		Date date = new Date(times);
		SimpleDateFormat df = new SimpleDateFormat("HH:mm", Locale.CHINESE);
		String str = df.format(date);
		return str;
	}

	public static String formatTimes(long time) {
		Date date = new Date();
		long sub = date.getTime() / 1000 - time;
		if (sub < 60)
			return "刚刚";
		if (sub < 60 * 60) {
			return (sub / 60) + "分钟前";
		}
		if (sub < 60 * 60 * 24) {
			return (sub / (60 * 60)) + "小时前";
		}
		return dateToSDatetr(time * 1000l);
	}

	public static String format(String str, Object... args) {
		return new MessageFormat(str).format(args);

	}

	public static String getVideoCreateTimeStr(long time){
		if (Locale.getDefault().getLanguage().equals(Locale.CHINESE.getLanguage())) {
			return DateUtils.dateToCDatetr(time);
		} else {
			Calendar cal = Calendar.getInstance();
			cal.setTimeInMillis(time);
			int day = cal.get(Calendar.DAY_OF_MONTH);
			String format = "MMM. d'th', yyyy";
			if (day % 10 == 1){
				format = "MMM. d'st', yyyy";
			} else if (day % 10 == 2){
				format = "MMM. d'nd', yyyy";
			} else if (day % 10 == 3){
				format = "MMM. d'rd', yyyy";
			}
			return new SimpleDateFormat(format, Locale.ENGLISH).format(new Date(time));
		}
	}

	//
	// // 返回时间字符串
	// public static String getTimeString(long time){
	// String timeStr = "fuck";
	// long currentTime = System.currentTimeMillis();
	// long intervalTime = currentTime/1000 - time;
	// if (intervalTime < 60) {
	// // 一分钟内
	// timeStr = "刚刚";
	// }else if (intervalTime < 60*60) {
	// // 一小时内
	// timeStr = ""+(int)intervalTime/60+"分钟前";
	// }else if (intervalTime < 60*60*24) {
	// // 一天内
	// timeStr = ""+(int)intervalTime/60/60+"小时前";
	// }else if (isSameYear(currentTime, time*1000)) {
	// // 是否同一年
	// timeStr = (String) DateFormat.format("MM-dd", time*1000);
	// }else{
	// // 不同一年
	// timeStr = (String) DateFormat.format("yyyy-MM-dd", time*1000);
	// }
	// return timeStr;
	// }
	//
	// // 判断是否在同一年
	// public static Boolean isSameYear(long one,long two){
	// Time t = new Time();
	// t.set(one);
	// int oneYear = t.year;
	// t.set(two);
	// int twoYear = t.year;
	// if (oneYear == twoYear) {
	// return true;
	// }else {
	// return false;
	// }
	// }
}
