package com.signutil;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

public class Signature {
	
	private static final String CLIENTID = "clientid";
	private static final String FORMAT = "format";
	private static final String METHOD = "method";
	private static final String SESSION = "session";
	private static final String USERID = "userid";
	private static final String LOGINUSERID = "loginuserid";
	private static final String SoftVersion = "softVersion";
	private static final String TIMESTAMP = "timestamp";
	private String app = "3";
	private String security = "bloomlife-video";
//	private String security = "8165yuyan666666";
	private String format = "json";
	private String sign=null;//签名

	/**
	 * 获取签名
	 * @param request
	 * @return
	 */
	public String  getSign(BaseRequest request){
		StringHashMap sysparams = new StringHashMap();
		sysparams.put(METHOD, request.getMethod());
		sysparams.put(FORMAT, format);
		sysparams.put(CLIENTID, app);
		sysparams.put(SESSION, request.getSession());
		sysparams.put(USERID, request.getUserid());
		sysparams.put(LOGINUSERID, request.getLoginuserid());
		sysparams.put(SoftVersion, request.getSoftVersion());
		sysparams.put(TIMESTAMP, request.getTimestamp());
		//增加签名
		RequestParametersHolder holder = new RequestParametersHolder();
		holder.setApplicationParams(request.getApplicationParams());
		holder.setProtocalParams(sysparams);
		try {
			 sign= Signature.signTopRequestNew(holder, security);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return sign;
	}

	/**
	 * 请求签名。
	 * 
	 * @param requestHolder 所有字符型请求参数
	 * @param secret 签名密钥
	 * @return 签名
	 * @throws IOException
	 */
	public static String signTopRequestNew(RequestParametersHolder requestHolder, String secret)
			throws IOException {
		// 第一步：把字典按Key的字母顺序排序
		Map<String, String> sortedParams = new TreeMap<String, String>();
		StringHashMap appParams = requestHolder.getApplicationParams();
		//输出ProtocalParams中的值
//		Iterator it =appParams.keySet().iterator();
//		while(it.hasNext()){
//			Object o =it.next();
//			System.out.println("*********key:"+o+"\t");
//		}
		//如果传过来的是图片或者音频则过滤这两个字段
		appParams.remove("img");
		appParams.remove("audio");
		if(appParams != null && appParams.size() > 0){
			sortedParams.putAll(appParams);
		}
		StringHashMap protocalMustParams = requestHolder.getProtocalParams();
		if(protocalMustParams != null && protocalMustParams.size() > 0){
			sortedParams.putAll(protocalMustParams);
		}

		Set<Entry<String, String>> paramSet = sortedParams.entrySet();

//		for(Entry key :paramSet){
//			System.out.println(""+key.getKey()+"="+key.getValue());
//		}
		
		// 第二步：把所有参数名和参数值串在一起
		StringBuilder query = new StringBuilder();
		query.append(secret);
		for (Entry<String, String> param : paramSet) {
			if (areNotEmpty(param.getKey(), param.getValue())) {
				query.append(param.getKey()).append(param.getValue());
			}
		}
		System.out.println(">>>>>>>>>>>>> query prams "+ query.toString()+"  >>>>>>>>>  ");
		// 第三步：使用MD5/HMAC加密
		byte[] bytes;
		//System.out.println(">>>>>>>>>>>>> encryptMD5(query.toString())  yes  ");
		query.append(secret);
		bytes = encryptMD5(query.toString().trim());
		// 第四步：把二进制转化为大写的十六进制
		return byte2hex(bytes);
	}
	
	private static String byte2hex(byte[] bytes) {
		StringBuilder sign = new StringBuilder();
		for (int i = 0; i < bytes.length; i++) {
			String hex = Integer.toHexString(bytes[i] & 0xFF);
			if (hex.length() == 1) {
				sign.append("0");
			}
			sign.append(hex.toUpperCase());
		}
		return sign.toString();
	}
	
	private static byte[] encryptMD5(String data) throws IOException {
		data = data.trim().replace(" ", "");
		byte[] bytes = null;
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			bytes = md.digest(data.getBytes(Constants.CHARSET_UTF8));
		} catch (GeneralSecurityException gse) {
			String msg = getStringFromException(gse);
			throw new IOException(msg);
		}
		return bytes;
	}
	
	
	private static String getStringFromException(Throwable e) {
		String result = "";
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(bos);
		e.printStackTrace(ps);
		try {
			result = bos.toString(Constants.CHARSET_UTF8);
		} catch (IOException ioe) {
		}
		return result;
	}
	
	/**
	 * 检查指定的字符串列表是否不为空。
	 */
	public static boolean areNotEmpty(String... values) {
		boolean result = true;
		if (values == null || values.length == 0) {
			result = false;
		} else {
			for (String value : values) {
				result &= !isEmpty(value);
			}
		}
		return result;
	}
	
	/**
	 * 检查指定的字符串是否为空。
	 * <ul>
	 * <li>SysUtils.isEmpty(null) = true</li>
	 * <li>SysUtils.isEmpty("") = true</li>
	 * <li>SysUtils.isEmpty("   ") = true</li>
	 * <li>SysUtils.isEmpty("abc") = false</li>
	 * </ul>
	 * 
	 * @param value 待检查的字符串
	 * @return true/false
	 */
	public static boolean isEmpty(String value) {
		int strLen;
		if (value == null || (strLen = value.length()) == 0) {
			return true;
		}
		for (int i = 0; i < strLen; i++) {
			if ((Character.isWhitespace(value.charAt(i)) == false)) {
				return false;
			}
		}
		return true;
	}
	
	
}
