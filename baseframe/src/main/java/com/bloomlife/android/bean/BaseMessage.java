/**
 * 
 */
package com.bloomlife.android.bean;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import com.alibaba.fastjson.annotation.JSONField;
import com.bloomlife.android.common.MessageException;
import com.bloomlife.android.common.util.MessageUtil;
import com.bloomlife.android.common.util.StringUtils;
import com.bloomlife.android.framework.AppContext;
import com.signutil.SignUtils;
import com.signutil.StringHashMap;

/**
 * @author <a href="mailto:xiai.fei@gmail.com">xiai_fei</a>  
 *
 * @date 2013-11-13 ����2:40:06
 */
public abstract  class BaseMessage {
	
	public static final int ZH = 0;
	public static final int EN = -1;

	private String msgCode;
	
	private String format = "json";
	
	private String clientid = "3";
	
	private String timestamp;
	
	private String session = "123";
	
	private String loginuserid ;

	private String sign;
	
	private String softVersion;
	
	private String devicetype = AppContext.deviceInfo.getScreenResolution();
	
	private String deviceid = AppContext.deviceInfo.macAddress;
	
	private Integer pageSize;
	
	private Integer pageNum;
	
	private Integer regional = -1;
	
	private Integer lang ;
	

	public BaseMessage(){
		this.timestamp = System.currentTimeMillis()+"";
		 softVersion =  AppContext.versionCode;
		 loginuserid = CacheBean.getInstance().getLoginUserId();
		 lang = Locale.CHINESE.getLanguage().equals(Locale.getDefault().getLanguage()) ? ZH : EN;
	}
	
	@JSONField(serialize=false)
	public Map<String,String> files = new HashMap<String,String>();
	
	public String getPostParamsStr(Map<String,String> bodyExtras) throws MessageException {
		Map<String,String> params = getParams();
		if(bodyExtras!=null) params.putAll(bodyExtras);
		StringBuffer sb = new StringBuffer();
		if (params != null && params.size() > 0) {
			for (String key : params.keySet()) {
				try {
					String value = params.get(key);
					if (StringUtils.isBlank(value)) {
						value = "";
					}
					sb.append(key + "=").append(
							URLEncoder.encode(value, "utf-8"));
					sb.append("&");
				} catch (UnsupportedEncodingException e) {
					sb.append("&");
				}
			}
		}
		return sb.substring(0, sb.length() - 1);
	}

	public Map<String,String> getParams() throws MessageException {
		Map<String,String> params = MessageUtil.converToMap(this);
		StringHashMap sign  = MessageUtil.converToMapWithoutParent(this);
		sign.put("pageSize", pageSize==null?"":pageSize);
		sign.put("pageNum", pageNum==null?"":pageNum);
		sign.put("devicetype", devicetype==null?"":devicetype);
		sign.put("deviceid", deviceid==null?"":deviceid);
		sign.put("regional", regional);
		sign.put("lang", lang==null?"":lang);
		params.put("sign", SignUtils.getSign(this, sign));
		return params;
	}
	
	
	public String getPostParamsStr() throws MessageException {
		return getPostParamsStr(null);
	}
	
	
	public String getDevicetype() {
		return devicetype;
	}




	public void setDevicetype(String devicetype) {
		this.devicetype = devicetype;
	}




	public String getMsgCode() {
		return msgCode;
	}




	public void setMsgCode(String msgCode) {
		this.msgCode = msgCode;
	}




	/**
	 * @return the format
	 */
	public String getFormat() {
		return format;
	}

	/**
	 * @param format the format to set
	 */
	public void setFormat(String format) {
		this.format = format;
	}

	/**
	 * @return the clientid
	 */
	public String getClientid() {
		return clientid;
	}

	/**
	 * @param clientid the clientid to set
	 */
	public void setClientid(String clientid) {
		this.clientid = clientid;
	}

	/**
	 * @return the timestamp
	 */
	public String getTimestamp() {
		return timestamp;
	}

	/**
	 * @param timestamp the timestamp to set
	 */
	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	/**
	 * @return the session
	 */
	public String getSession() {
		return session;
	}

	/**
	 * @param session the session to set
	 */
	public void setSession(String session) {
		this.session = session;
	}


	/**
	 * @param loginuserid the loginuserid to set
	 */
	public void setLoginuserid(String loginuserid) {
		this.loginuserid = loginuserid;
	}

	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}
	
	public String getLoginuserid() {
		return loginuserid;
	}

	public Integer getPageSize() {
		return pageSize;
	}




	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}




	public Integer getPageNum() {
		return pageNum;
	}




	public void setPageNum(Integer pageNum) {
		this.pageNum = pageNum;
	}




	public String getSoftVersion() {
		return softVersion;
	}




	public void setSoftVersion(String softVersion) {
		this.softVersion = softVersion;
	}




	public String getDeviceid() {
		return deviceid;
	}




	public void setDeviceid(String deviceid) {
		this.deviceid = deviceid;
	}




	/**
	 * @return the regional
	 */
	public Integer getRegional() {
		return regional;
	}




	/**
	 * @param regional the regional to set
	 */
	public void setRegional(Integer regional) {
		this.regional = regional;
	}




	public Integer getLang() {
		return lang;
	}




	public void setLang(Integer lang) {
		this.lang = lang;
	}

}
