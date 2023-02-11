///**
// * 
// */
//package com.bloomlife.videoapp.common.qiniu;
//
//import org.apache.http.HttpException;
//
//import android.content.Context;
//
//import com.bloomlife.android.network.HttpAccessor;
//import com.bloomlife.videoapp.model.message.UploadTokenMessage;
//import com.bloomlife.videoapp.model.result.UploadTokenResut;
//
///**
// * 	七牛工具类
// * 
// * @author <a href="mailto:xiai.fei@gmail.com">xiai_fei</a>  
// *
// * @date 2014-11-13  下午6:18:06
// */
//@Deprecated
//public class QiniuUtils {
//
//	/***
//	 * 获取上传证书
//	 * @param context
//	 * @return
//	 */
//	public static String getUpLoadToken(Context context){
//		HttpAccessor httpAccessor = new HttpAccessor(context);
//		try {
//			UploadTokenResut result =  httpAccessor.call(new UploadTokenMessage(),UploadTokenResut.class);
//			return result.getUploadtoken();
//		} catch (HttpException e) {
//			e.printStackTrace();
//		}		
//		return "";
//	}
//}
