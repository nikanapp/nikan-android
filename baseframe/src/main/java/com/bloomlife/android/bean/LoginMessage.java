/**
 * 
 */
package com.bloomlife.android.bean;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

/**
 * 	登录消息报文
 * 
 * @author <a href="mailto:xiai.fei@gmail.com">xiai_fei</a>  
 *
 * @date 2013-11-13  下午2:42:41
 */
public class LoginMessage extends BaseMessage{
	
	private final static String METHOD = "3001";

	private Context Mact;
	
	public LoginMessage(Context act) {
		this();
		this.Mact = act;
		// 获取渠道号
		setChannelnum(getApplicationMetaData());
	}
	
	public LoginMessage(){
		setMsgCode(METHOD);
	}

	/**
     * appliction MetaData读取
     */
    private String getApplicationMetaData() {
        ApplicationInfo info;
		// 默认的渠道号
        String umengChannel = "99999";
        try {
            info = this.Mact.getPackageManager().getApplicationInfo(this.Mact.getPackageName(), PackageManager.GET_META_DATA);
            String tempChannel = info.metaData.get("UMENG_CHANNEL").toString();
            if (tempChannel != null && tempChannel.length()>0) {
				umengChannel = tempChannel;
			}
        } catch (Exception e) {
            e.printStackTrace();
        }
		return umengChannel;
    }
    
    public String getApplicationVersionName(){
    	String versionName = "0.0.0";
    	try {
			versionName = this.Mact.getApplicationContext().getPackageManager().getPackageInfo(this.Mact.getApplicationContext().getPackageName(),0).versionName;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return versionName;
    }
	
	private String systemVersion = android.os.Build.VERSION.RELEASE;
	
	private String channel = "2";
	
	private String openId;
	
	private String gender;
	
	private Float longtitude;
	
	private Float latitude;
	
	private String userName;
	
	private String icon;
	
	private String city;
	
	private String deviceVersion ;
	
	private String accessToken;
	
	private String sys = "2";
	
	public static final String LOGIN_TYPE_SINA = "1";
	public static final String LOGIN_TYPE_QQ = "2";
	
	private String loginType;
	
	private String channelnum;

	public String getSystemVersion() {
		return systemVersion;
	}

	public void setSystemVersion(String systemVersion) {
		this.systemVersion = systemVersion;
	}

	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	public String getOpenId() {
		return openId;
	}

	public void setOpenId(String openId) {
		this.openId = openId;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public Float getLongtitude() {
		return longtitude;
	}

	public void setLongtitude(Float longtitude) {
		this.longtitude = longtitude;
	}

	public Float getLatitude() {
		return latitude;
	}

	public void setLatitude(Float latitude) {
		this.latitude = latitude;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}



	public String getDeviceVersion() {
		return deviceVersion;
	}

	public void setDeviceVersion(String deviceVersion) {
		this.deviceVersion = deviceVersion;
	}

	public String getChannelnum() {
		return channelnum;
	}

	public void setChannelnum(String channelnum) {
		this.channelnum = channelnum;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public String getSys() {
		return sys;
	}

	public void setSys(String sys) {
		this.sys = sys;
	}
	
	public static void main(String[] args) {
		
	}

	public String getLoginType() {
		return loginType;
	}

	public void setLoginType(String loginType) {
		this.loginType = loginType;
	}
}
