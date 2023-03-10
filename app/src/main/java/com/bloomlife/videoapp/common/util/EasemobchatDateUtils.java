/**
 * 
 */
package com.bloomlife.videoapp.common.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

/**
 * @author <a href="mailto:lan4627@gmail.com">zhengxingtian</a>
 * 
 * @date 2015年6月18日 下午5:27:19
 */
public class EasemobchatDateUtils {

	private static final long INTERVAL_IN_MILLISECONDS = 30000L;

	public static String getTimestampString(Date paramDate) {
		String str = null;
		long l = paramDate.getTime();
		Calendar localCalendar = GregorianCalendar.getInstance();
		Locale cl = Locale.getDefault();
		if (isSameDay(l)) {
			localCalendar.setTime(paramDate);
			int i = localCalendar.get(11);
			if (cl.getLanguage().equals(Locale.CHINESE.getLanguage())){
				if ((i >= 0) && (i <= 6)) {
					str = "凌晨 hh:mm";
				} else if ((i > 6) && (i <= 11)) {
					str = "上午 hh:mm";
				} else if ((i > 11) && (i <= 17)) {
					str = "下午 hh:mm";
				} else {
					str = "晚上 hh:mm";
				}
			} else {
				str = "aa hh:mm";
			}
		} else if (isYesterday(l)) {
			if (cl.getLanguage().equals(Locale.CHINESE.getLanguage()))
				str = "昨天 HH:mm";
			else
				str = "MMM d, HH:mm";
		} else {
			if (cl.getLanguage().equals(Locale.CHINESE.getLanguage()))
				str = "M月d日 HH:mm";
			else
				str = "MMM d, HH:mm";
		}
		return new SimpleDateFormat(str, cl).format(paramDate);
	}
	
	public static boolean isCloseEnough(long paramLong1, long paramLong2) {
		long l = paramLong1 - paramLong2;
		if (l < 0L) {
			l = -l;
		}
		return l < 30000L;
	}

	private static boolean isSameDay(long paramLong) {
		TimeInfo localTimeInfo = getTodayStartAndEndTime();
		return (paramLong > localTimeInfo.getStartTime())
				&& (paramLong < localTimeInfo.getEndTime());
	}

	private static boolean isYesterday(long paramLong) {
		TimeInfo localTimeInfo = getYesterdayStartAndEndTime();
		return (paramLong > localTimeInfo.getStartTime())
				&& (paramLong < localTimeInfo.getEndTime());
	}

	public static Date StringToDate(String paramString1, String paramString2) {
		SimpleDateFormat localSimpleDateFormat = new SimpleDateFormat(
				paramString2);
		Date localDate = null;
		try {
			localDate = localSimpleDateFormat.parse(paramString1);
		} catch (ParseException localParseException) {
			localParseException.printStackTrace();
		}
		return localDate;
	}

	public static String toTime(int paramInt) {
		paramInt /= 1000;
		int i = paramInt / 60;
		int j = 0;
		if (i >= 60) {
			j = i / 60;
			i %= 60;
		}
		int k = paramInt % 60;
		return String.format("%02d:%02d", Integer.valueOf(i),
				Integer.valueOf(k));
	}

	public static String toTimeBySecond(int paramInt) {
		int i = paramInt / 60;
		int j = 0;
		if (i >= 60) {
			j = i / 60;
			i %= 60;
		}
		int k = paramInt % 60;
		return String.format("%02d:%02d", Integer.valueOf(i),
				Integer.valueOf(k));
	}

	public static TimeInfo getYesterdayStartAndEndTime() {
		Calendar localCalendar1 = Calendar.getInstance();
		localCalendar1.add(5, -1);
		localCalendar1.set(11, 0);
		localCalendar1.set(12, 0);
		localCalendar1.set(13, 0);
		localCalendar1.set(14, 0);
		Date localDate1 = localCalendar1.getTime();
		long l1 = localDate1.getTime();
		Calendar localCalendar2 = Calendar.getInstance();
		localCalendar2.add(5, -1);
		localCalendar2.set(11, 23);
		localCalendar2.set(12, 59);
		localCalendar2.set(13, 59);
		localCalendar2.set(14, 999);
		Date localDate2 = localCalendar2.getTime();
		long l2 = localDate2.getTime();
		TimeInfo localTimeInfo = new TimeInfo();
		localTimeInfo.setStartTime(l1);
		localTimeInfo.setEndTime(l2);
		return localTimeInfo;
	}

	public static TimeInfo getTodayStartAndEndTime() {
		Calendar localCalendar1 = Calendar.getInstance();
		localCalendar1.set(11, 0);
		localCalendar1.set(12, 0);
		localCalendar1.set(13, 0);
		localCalendar1.set(14, 0);
		Date localDate1 = localCalendar1.getTime();
		long l1 = localDate1.getTime();
		SimpleDateFormat localSimpleDateFormat = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss S");
		Calendar localCalendar2 = Calendar.getInstance();
		localCalendar2.set(11, 23);
		localCalendar2.set(12, 59);
		localCalendar2.set(13, 59);
		localCalendar2.set(14, 999);
		Date localDate2 = localCalendar2.getTime();
		long l2 = localDate2.getTime();
		TimeInfo localTimeInfo = new TimeInfo();
		localTimeInfo.setStartTime(l1);
		localTimeInfo.setEndTime(l2);
		return localTimeInfo;
	}

	public static TimeInfo getBeforeYesterdayStartAndEndTime() {
		Calendar localCalendar1 = Calendar.getInstance();
		localCalendar1.add(5, -2);
		localCalendar1.set(11, 0);
		localCalendar1.set(12, 0);
		localCalendar1.set(13, 0);
		localCalendar1.set(14, 0);
		Date localDate1 = localCalendar1.getTime();
		long l1 = localDate1.getTime();
		Calendar localCalendar2 = Calendar.getInstance();
		localCalendar2.add(5, -2);
		localCalendar2.set(11, 23);
		localCalendar2.set(12, 59);
		localCalendar2.set(13, 59);
		localCalendar2.set(14, 999);
		Date localDate2 = localCalendar2.getTime();
		long l2 = localDate2.getTime();
		TimeInfo localTimeInfo = new TimeInfo();
		localTimeInfo.setStartTime(l1);
		localTimeInfo.setEndTime(l2);
		return localTimeInfo;
	}

	public static TimeInfo getCurrentMonthStartAndEndTime() {
		Calendar localCalendar1 = Calendar.getInstance();
		localCalendar1.set(5, 1);
		localCalendar1.set(11, 0);
		localCalendar1.set(12, 0);
		localCalendar1.set(13, 0);
		localCalendar1.set(14, 0);
		Date localDate1 = localCalendar1.getTime();
		long l1 = localDate1.getTime();
		Calendar localCalendar2 = Calendar.getInstance();
		Date localDate2 = localCalendar2.getTime();
		long l2 = localDate2.getTime();
		TimeInfo localTimeInfo = new TimeInfo();
		localTimeInfo.setStartTime(l1);
		localTimeInfo.setEndTime(l2);
		return localTimeInfo;
	}

	public static TimeInfo getLastMonthStartAndEndTime() {
		Calendar localCalendar1 = Calendar.getInstance();
		localCalendar1.add(2, -1);
		localCalendar1.set(5, 1);
		localCalendar1.set(11, 0);
		localCalendar1.set(12, 0);
		localCalendar1.set(13, 0);
		localCalendar1.set(14, 0);
		Date localDate1 = localCalendar1.getTime();
		long l1 = localDate1.getTime();
		Calendar localCalendar2 = Calendar.getInstance();
		localCalendar2.add(2, -1);
		localCalendar2.set(5, 1);
		localCalendar2.set(11, 23);
		localCalendar2.set(12, 59);
		localCalendar2.set(13, 59);
		localCalendar2.set(14, 999);
		localCalendar2.roll(5, -1);
		Date localDate2 = localCalendar2.getTime();
		long l2 = localDate2.getTime();
		TimeInfo localTimeInfo = new TimeInfo();
		localTimeInfo.setStartTime(l1);
		localTimeInfo.setEndTime(l2);
		return localTimeInfo;
	}

	public static String getTimestampStr() {
		return Long.toString(System.currentTimeMillis());
	}

	private static class TimeInfo {
		
		private long startTime;
		private long endTime;
		
		public TimeInfo(){
			
		}

		public long getStartTime() {
			return this.startTime;
		}

		public void setStartTime(long paramLong) {
			this.startTime = paramLong;
		}

		public long getEndTime() {
			return this.endTime;
		}

		public void setEndTime(long paramLong) {
			this.endTime = paramLong;
		}
	}

}
