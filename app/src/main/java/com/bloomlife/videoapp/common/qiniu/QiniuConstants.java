/**
 * 
 */
package com.bloomlife.videoapp.common.qiniu;

/**
 * @author <a href="mailto:xiai.fei@gmail.com">xiai_fei</a>  
 *
 * @date 2014-11-14  上午9:13:37
 */
public class QiniuConstants {
	
	public static final int  Code_Suc = 200 ; //	操作执行成功。
	public static final int  Code_Part_Suc = 298 ; //	部分操作执行成功。
	public static final int  Code_Illegal_Message = 400 ;//	请求报文格式错误。
	public static final int   Code_Auth_Failure = 401 ;  //	认证授权失败，可能是密钥信息不正确、数字签名错误或授权已超时。
	public static final int  Code_Bucket_Not_Exist = 404 ; //	资源不存在。（包括空间资源不存在、镜像源资源不存在）
	public static final int  Code_Request_Type_Error = 405;//	请求方式错误，非预期的请求方式。
	public static final int  Code_Error_CRC = 406 ;//	上传的数据 CRC32 校验错。
	public static final int  Code_Account_Illegal = 419 ; //	用户账号被冻结。
	//478	镜像回源失败（主要指镜像源服务器出现异常）。
	public static final int  Code_Not_Avaliable = 503 ;// 	服务端不可用。
	public static final int  Code_Timeout = 504 ; //	服务端操作超时。
	
	public static final int  Code_Fail_On_Callback = 579 ; //	 上传成功但是回调失败（包括业务服务器异常、七牛服务器异常以及服务器间网络异常）。
	
	public static final int  Code_Failure = 599 ; //	服务端操作失败。
//	608	资源内容被修改。
	public static final int  Code_Not_Exist = 612;//	指定资源不存在或已被删除。
	//614	目标资源已存在。
	public static final int  Code_No_Space = 630 ;	//已创建的空间数量达到上限，无法创建新空间。
	//631	指定空间不存在。
	//701	在断点续上传过程中，后续上传接收地址不正确，或ctx信息已过期。
}
