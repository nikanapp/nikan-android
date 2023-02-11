package com.signutil;

import java.util.HashMap;
import java.util.Map;



public class BaseRequest{
	private String method;
	private String format;//默认
	private Long clientid;
	private String ver;//默认
	private String sign;
	private String timestamp;
	private String session;
	private String userid;//如果用户已经登陆，也尽量把userid参数赋值
	private String loginuserid;//登陆用户id
	private String softVersion;//登陆用户id
	private StringHashMap applicationParams = new StringHashMap();
	private StringHashMap protocalParams = new StringHashMap();
	
	public String getMethod() {
		// TODO Auto-generated method stub
		return method;
	}

	public StringHashMap getApplicationParams() {
		// TODO Auto-generated method stub
		return applicationParams;
	}

	public void addAllApplicationParams(StringHashMap params){
		applicationParams.putAll(params);
	}
	
	public void addApplicationParams(String key,Object value){
		applicationParams.put(key, value);
	}
	
	public String getTimestamp() {
		// TODO Auto-generated method stub
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp  = timestamp;
		
	}

	public void addProtocalParams(String key,String value){
		protocalParams.put(key, value);
	}

	public StringHashMap getProtocalParams() {
		return protocalParams;
	}

	public void setMethod(String method) {
		// TODO Auto-generated method stub
		this.method = method;
	}

	public String getFormat() {
		// TODO Auto-generated method stub
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
		
	}

	public Long getClientid() {
		return clientid;
	}

	public void setClientid(Long clientid) {
		this.clientid = clientid;
	}

	public String getVer() {
		// TODO Auto-generated method stub
		return ver;
	}

	public void setVer(String ver) {
		// TODO Auto-generated method stub
		this.ver = ver;
	}

	public String getSign() {
		// TODO Auto-generated method stub
		return sign;
	}

	public void setSign(String sign) {
		// TODO Auto-generated method stub
		this.sign = sign;
	}

	public String getSession() {
		return session;
	}

	public void setSession(String session) {
		this.session = session;
	}

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public String getLoginuserid() {
		return loginuserid;
	}

	public void setLoginuserid(String loginuserid) {
		this.loginuserid = loginuserid;
	}


	public void setApplicationParams(StringHashMap applicationParams) {
		this.applicationParams = applicationParams;
	}

	public void setProtocalParams(StringHashMap protocalParams) {
		this.protocalParams = protocalParams;
	}

	public String getSoftVersion() {
		return softVersion;
	}

	public void setSoftVersion(String softVersion) {
		this.softVersion = softVersion;
	}

}
