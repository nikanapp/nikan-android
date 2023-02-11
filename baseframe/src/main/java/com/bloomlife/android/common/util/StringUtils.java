package com.bloomlife.android.common.util;

import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** 
 * @author liux (http://my.oschina.net/liux)
 * @version 1.0
 * @created 2012-3-21
 */
public class StringUtils 
{
	private final static Pattern emailer = Pattern.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");
	//private final static SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	//private final static SimpleDateFormat dateFormater2 = new SimpleDateFormat("yyyy-MM-dd");
	
	private final static ThreadLocal<SimpleDateFormat> dateFormater = new ThreadLocal<SimpleDateFormat>() {
		@Override
		protected SimpleDateFormat initialValue() {
			return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.CHINA);
		}
	};
	
	private final static ThreadLocal<SimpleDateFormat> dateFormater2 = new ThreadLocal<SimpleDateFormat>() {
		@Override
		protected SimpleDateFormat initialValue() {
			return new SimpleDateFormat("yyyy-MM-dd",Locale.CHINA);
		}
	};
	
	public static String formatDate(Date date){
		return dateFormater.get().format(date);
	}
	
	public static boolean isBlank(String str){
		boolean result = false;
		if(str==null || str.trim().length()==0 || str.equalsIgnoreCase("null")){
			result = true;
		}
		return result;
	}
	
	public static boolean isNotBlank(String str){
		boolean result = true;
		if(str==null || str.trim().length()==0 || str.equalsIgnoreCase("null")){
			result = false;
		}
		return result;
	}
	
	
	/**
	 * ���ַ�תλ��������
	 * @param sdate
	 * @return
	 */
	public static Date toDate(String sdate) {
		try {
			return dateFormater.get().parse(sdate);
		} catch (ParseException e) {
			return null;
		}
	}
	
	
	
	/**
	 * @param sdate
	 * @return boolean
	 */
	public static boolean isToday(String sdate){
		boolean b = false;
		Date time = toDate(sdate);
		Date today = new Date();
		if(time != null){
			String nowDate = dateFormater2.get().format(today);
			String timeDate = dateFormater2.get().format(time);
			if(nowDate.equals(timeDate)){
				b = true;
			}
		}
		return b;
	}
	
	/**
	 * @param input
	 * @return boolean
	 */
	public static boolean isEmpty( String input ) 
	{
		if ( input == null || "".equals( input ) )
			return true;
		
		for ( int i = 0; i < input.length(); i++ ) 
		{
			char c = input.charAt( i );
			if ( c != ' ' && c != '\t' && c != '\r' && c != '\n' )
			{
				return false;
			}
		}
		return true;
	}
	
	/**
	 * @param input
	 * @return boolean
	 */
	public static boolean isNotEmpty( String input ) 
	{
		if ( input == null || "".equals( input ) )
			return false;
		
		for ( int i = 0; i < input.length(); i++ ) 
		{
			char c = input.charAt( i );
			if ( c != ' ' && c != '\t' && c != '\r' && c != '\n' )
			{
				return true;
			}
		}
		return false;
	}


	public static boolean isEmail(String email){
		if(email == null || email.trim().length()==0) 
			return false;
	    return emailer.matcher(email).matches();
	}

	public static int toInt(String str, int defValue) {
		try{
			return Integer.parseInt(str);
		}catch(Exception e){}
		return defValue;
	}

	public static int toInt(Object obj) {
		if(obj==null) return 0;
		return toInt(obj.toString(),0);
	}

	public static long toLong(String obj) {
		try{
			return Long.parseLong(obj);
		}catch(Exception e){}
		return 0;
	}

	public static boolean toBool(String b) {
		try{
			return Boolean.parseBoolean(b);
		}catch(Exception e){}
		return false;
	}
	
	public static String convertStreamToString(InputStream is, String encode) {
		StringBuilder sb = new StringBuilder();
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					is, encode), 8 * 1024);
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
		} catch (IOException e) {
			sb.delete(0, sb.length());
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				return null;
			}
		}
		return sb.toString();
	}
	
	/**
     * @汉字判断
     * @param str
     * @return
     */
     public static boolean checkChs(String str) {
      boolean mark = false;
      Pattern pattern = Pattern.compile("[\u4E00-\u9FA5]");
      Matcher matc = pattern.matcher(str);
      StringBuffer stb = new StringBuffer();
      while (matc.find()) {
       mark = true;
       stb.append(matc.group());
      }
      return mark;
     }

	public static boolean checkName(String str){
		Pattern pattern = Pattern.compile("[\u4E00-\u9FA5[a-zA-Z_0-9\u0008]]{" + str.length() + "}");
		return pattern.matcher(str).find();
	}

	public static boolean checkDescription(String str){
		Pattern pattern = Pattern.compile("[\u4E00-\u9FA5[a-zA-Z0-9,.~!:，。、]]{" + str.length() + "}");
		return pattern.matcher(str).find();
	}

	public static List<String> createWordList(String description){
		List<String> wordList = new ArrayList<>();
		Matcher matcher = Pattern.compile("([a-zA-Z_]*)([\\u4E00-\\u9FA5\\s0-9])").matcher(description + " ");
		while (matcher.find()){
			String word = matcher.group(1);
			if (TextUtils.isEmpty(word)){
				word = matcher.group(2);
				if (!TextUtils.isEmpty(word))
					wordList.add(word.trim());
			} else {
				wordList.add(word.trim());
			}
		}
		return wordList;
	}

	public static String appendSpace(String para){
		int length = para.length();
		char[] value = new char[length << 1];
		for (int i=0, j=0; i<length; ++i, j = i << 1) {
				value[j] = para.charAt(i);
				value[1 + j] = ' ';
		}
		return new String(value);
	}
}
