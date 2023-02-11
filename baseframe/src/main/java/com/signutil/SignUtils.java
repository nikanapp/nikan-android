/**
 * 
 */
package com.signutil;

import java.io.IOException;

import com.bloomlife.android.bean.BaseMessage;

/**
 * @author <a href="mailto:xiai.fei@gmail.com">xiai_fei</a>  
 *
 * @date 2013-12-25  上午10:34:47
 */
public class SignUtils {

	public static String getSign(BaseMessage message, StringHashMap map){
		BaseRequest request= new BaseRequest();
		Signature sign=new Signature();
		request.setMethod(message.getMsgCode());
		request.setSession("123");
		request.setLoginuserid(message.getLoginuserid()+"");
		request.setSoftVersion(message.getSoftVersion());
		request.setTimestamp(message.getTimestamp());
		map.remove("img");
		map.remove("Img");
		map.remove("audio");
		map.remove("Audio");
		request.addAllApplicationParams(map);
		String signStr = sign.getSign(request);
		System.out.println("sign:"+signStr);
		return signStr;
	}
	
	
	public static String getSign(StringHashMap map , String secret){
		RequestParametersHolder holder = new RequestParametersHolder();
		holder.setApplicationParams(map);
		try {
			return Signature.signTopRequestNew(holder,secret);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";
	}
}
