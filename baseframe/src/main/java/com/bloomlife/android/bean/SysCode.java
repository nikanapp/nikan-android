/**
 * 
 */
package com.bloomlife.android.bean;

import android.content.Context;
import android.content.SharedPreferences;

import com.alibaba.fastjson.JSON;
import com.bloomlife.android.common.util.StringUtils;

/**
 * 
 * 		系统参数表。
 * 
 * @author <a href="mailto:xiai.fei@gmail.com">xiai_fei</a>  
 *
 * @date 2013-12-17  上午11:11:00
 */
public class SysCode {

	private Integer coverCmpRatio ; 
	
	private Integer stepCmpRatio ;	//step图片压缩的比例
	
	private Integer stepCmpMin ;	//step的图片进行压缩的最小尺寸
	
	private String version;
	
	private String url;
	
	private String updatehint;		// 升级提示文案
	
	private Integer isfroceupdate;		// 是否强制更新（0 不更新，1 更新）
	
	private String forceupdatehint; // 强制更新文案
	
	private String colorlist;
	
	private String mituanimg;
	
	private String predicticon ;
	
	private String spreadimg;
	
	private int allowcomment;
	
	//For Test
	private int tabindex = 0;
	

	public int getTabindex() {
		return tabindex;
	}

	public void setTabindex(int tabindex) {
		this.tabindex = tabindex;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Integer getCoverCmpRatio() {
		return coverCmpRatio;
	}
	
	public Integer getCoverCmpRatio(int defaultValue) {
		return coverCmpRatio==null?defaultValue:coverCmpRatio;
	}


	public void setCoverCmpRatio(Integer coverCmpRatio) {
		this.coverCmpRatio = coverCmpRatio;
	}

	public Integer getStepCmpRatio() {
		return stepCmpRatio;
	}

	public Integer getStepCmpRatio(int defaulitValue) {
		return stepCmpRatio==null?defaulitValue:stepCmpRatio;
	}

	public void setStepCmpRatio(Integer stepCmpRatio) {
		this.stepCmpRatio = stepCmpRatio;
	}

	public Integer getStepCmpMin() {
		return stepCmpMin;
	}
	
	public Integer getStepCmpMin(int defaultValue) {
		return stepCmpMin==null?defaultValue:stepCmpMin;
	}

	public void setStepCmpMin(Integer stepCmpMin) {
		this.stepCmpMin = stepCmpMin;
	}

	public String getUpdatehint() {
		return updatehint;
	}

	public void setUpdatehint(String updatehint) {
		this.updatehint = updatehint;
	}

	public int getIsfroceupdate() {
		return isfroceupdate==null?0:isfroceupdate;
	}

	public void setIsfroceupdate(int isfroceupdate) {
		this.isfroceupdate = isfroceupdate;
	}

	public String getForceupdatehint() {
		return forceupdatehint;
	}

	public void setForceupdatehint(String forceupdatehint) {
		this.forceupdatehint = forceupdatehint;
	}
	
	public String getColorlist() {
		return colorlist;
	}

	public void setColorlist(String colorlist) {
		this.colorlist = colorlist;
	}
	

	public String getMituanimg() {
		return mituanimg;
	}

	public void setMituanimg(String mituanimg) {
		this.mituanimg = mituanimg;
	}
	
	public int getAllowcomment() {
		return allowcomment;
	}
	
	public void setAllowcomment(int allowcomment) {
		this.allowcomment = allowcomment;
	}
	
	public static final String KEY = "SP_KEY_APP";
	
	
	private static final String SP_KEY = "sp_key_app";
	
	public static  SysCode saveToSp(SysCode sysCode , Context context ){
		SharedPreferences sp = context.getSharedPreferences(KEY, Context.MODE_PRIVATE);
		sp.edit().putString(SP_KEY, JSON.toJSONString(sysCode)).commit();
		return sysCode;
	}
	
	public static SysCode getInstanceFromSp(Context context){
		SharedPreferences sp = context.getSharedPreferences(KEY, Context.MODE_PRIVATE);
		String jsonStr = sp.getString(SP_KEY,null);
		if(StringUtils.isEmpty(jsonStr)) return null;
		return JSON.parseObject(jsonStr, SysCode.class);
		
	}

	public String getPredicticon() {
		return predicticon;
	}

	public void setPredicticon(String predicticon) {
		this.predicticon = predicticon;
	}

	
	public String getSpreadimg() {
		return spreadimg;
	}
	
	public void setSpreadimg(String spreadimg) {
		this.spreadimg = spreadimg;
	}

	
}
