/**
 * 
 */
package com.bloomlife.android.bean;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * 		提示信息实体类。这个类主要是为了当用户进行操作时，所操作结果的封装。会封装错误代码和错误描述。并且有debug开关，用于显示友好信息和详细后台错误描述
 * 
 * @author <a href="mailto:xiai.fei@gmail.com">xiai_fei</a>  
 *
 * @date 2013-11-13  下午5:30:55
 */
public class ProcessResult {
	
	public static final int SUC = 200;
	public static final int Failure = 202;
	public static final int ILLEAGE_PARAMETER = 500;
	
	public static final int ReUpload = 202;
	public static final int Step_Failure = 10;
	
	public static final int Execption = 500;
	
	/**默认显示的错误信息**/
	public static final String ERROR_MSG= "网络不大给力,换个姿势吧!";
	
	
	
	private Object value ;

	
	@JSONField(name="resultCode")
	private int code ;
	
	@JSONField(name="resultDes")
	private String desc ;
	
	private Integer subCode ;
	
	private String subDes;
	
	public void setResultCode(int code){
		this.code = code ;
	}
	
	public void setResultDes(String desc){
		this.desc =desc;
	}
	
	public Integer getSubCode() {
		return subCode==null?0:subCode;
	}
	public void setSubCode(Integer subCode) {
		this.subCode = subCode;
	}
	public String getSubDes() {
		return subDes;
	}
	public void setSubDes(String subDes) {
		this.subDes = subDes;
	}

	private boolean debug = true;
	
	private Integer pageSize ;
	
	private Integer pageNum ;
	
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
	public ProcessResult(){
		this.code = SUC;
	}
	public ProcessResult(int code , String desc){
		this.code = code;
		this.setDesc(desc);
	}
	
	
	
	
	
	/***
	 * 获取成功的处理结果
	 * @return
	 */
	public static ProcessResult Suc(){
		return new ProcessResult();
	}
	
	public static ProcessResult Suc(Object value){
		ProcessResult pr = new ProcessResult();
		pr.setValue(value);
		return pr;
	}
	
	@JSONField(name="resultCode")
	public int getCode() {
		return code;
	}
	
	public int getResultCode(){
		return code ;
	}
	
	public String getResultDes(){
		return desc;
	}
	@JSONField(name="resultCode")
	public ProcessResult setCode(int code) {
		this.code = code;
		return this ;
	}


	public ProcessResult setDesc(String desc) {
		this.desc = desc;
		return this;
	}

	public boolean isDebug() {
		return debug;
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}
	public Object getValue() {
		return value;
	}
	
	@SuppressWarnings("unchecked")
	public <T> T getValue(Class<T> clz){
		return (T) value;
	}
	
	public void setValue(Object value) {
		this.value = value;
	}
	
	
}
