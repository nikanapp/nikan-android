/**
 * 
 */
package com.bloomlife.android.network;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * 	http  协议相关设置内容
 * 
 * @author <a href="mailto:xiai.fei@gmail.com">xiai_fei</a>  
 *
 * @date 2013-10-19  下午2:20:58
 */
public class HttpProtocolEntry {

	
	/***  默认编码格式   UTF-8***/
	public static final String DEFAULT_CHARSET = "utf-8";
	
	public static final String METHOD_POST = "POST";
	
	public static final String METHOD_GET = "GET";
	
	public static final String CONTENT_TYPE ="application/x-www-form-urlencoded;";
	
	public static  String URL ;
	
	private Map<String,String> requestProperty ;
	
	private int connectTimeout ;
	
	private int readTimeout ;
	
	private String method = METHOD_POST ;
	
	private String url  ;
	
	private String contentType  = CONTENT_TYPE ;

	private String charset  =DEFAULT_CHARSET ;
	
	public HttpProtocolEntry(String method){
		this.method = method; 
		
	}
	
	public HttpProtocolEntry(){
	}
	
	public HttpProtocolEntry( Map<String,String>  property ){
		this.requestProperty = property;
	}
	
	public HttpProtocolEntry(String method ,int ctimeout , int readTimeout){
		this.method = method ;
		this.connectTimeout = ctimeout;
		this.readTimeout = readTimeout;
	}

	public Map<String, String> getRequestProperty() {
		return requestProperty;
	}

	public int getConnectTimeout() {
		return connectTimeout;
	}

	public int getReadTimeout() {
		return readTimeout;
	}

	public String getMethod() {
		return method;
	}

	public void setRequestProperty(Map<String, String> requestProperty) {
		this.requestProperty = requestProperty;
	}

	public void setConnectTimeout(int connectTimeout) {
		this.connectTimeout = connectTimeout;
	}

	public void setReadTimeout(int readTimeout) {
		this.readTimeout = readTimeout;
	}

	public void setMethod(String method) {
		this.method = method;
	}
	
	/***
	 * 设置requestproperty
	 * @param key
	 * @param value
	 */
	public void setProperty(String key ,String value ){
		if(requestProperty==null) requestProperty = new HashMap<String, String>();
		requestProperty.put(key, value);
	}


	/**
	 * @return the contentType
	 */
	public String getContentType() {
		if(contentType.contains("charset=")){
			return contentType;
		}
		return contentType+"charset="+charset;
	}

	/**
	 * @param contentType the contentType to set
	 */
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	/**
	 * @return the charset
	 */
	public String getCharset() {
		return charset;
	}

	/**
	 * @param charset the charset to set
	 */
	public void setCharset(String charset) {
		this.charset = charset;
	}
	
	private static final HttpProtocolEntry entry = new HttpProtocolEntry();
	
	static HttpProtocolEntry getDefaultEnty(){
		return entry;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
}
