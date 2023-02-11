/**
 * 
 */
package com.bloomlife.android.common.security;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;

import Decoder.BASE64Decoder;
import Decoder.BASE64Encoder;

/**
 * @author <a href="mailto:xiai.fei@gmail.com">xiai_fei</a>  
 *
 * @date 2014-9-1  下午5:13:40
 */
public class DesUtils {

	/**
	 * 通讯录加密
	 * @param src
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public static String encryptThreeDESECB(String src) throws Exception{  
	    DESedeKeySpec dks = new DESedeKeySpec(KEY.getBytes("UTF-8"));  
	    SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DESede");  
	    SecretKey securekey = keyFactory.generateSecret(dks);  
	      
	    Cipher cipher = Cipher.getInstance("DESede/ECB/PKCS5Padding");  
	    cipher.init(Cipher.ENCRYPT_MODE, securekey);  
	    byte[] b=cipher.doFinal(src.getBytes());  
	      
	    BASE64Encoder encoder = new BASE64Encoder();  
	    return encoder.encode(b).replaceAll("\r", "").replaceAll("\n", "");  
	      
	} 
	
	/**
	 * 通讯录加密
	 * @param src
	 * @return
	 * @throws Exception
	 */
	public static final String KEY = "6b583e62046b16234ce6d38c37088e9e";
	public static String decryptThreeDESECB(String src,String key) throws Exception{  
	    //--通过base64,将字符串转成byte数组  
	    BASE64Decoder decoder = new BASE64Decoder();  
	    byte[] bytesrc = decoder.decodeBuffer(src);  
	    //--解密的key  
	    DESedeKeySpec dks = new DESedeKeySpec(key.getBytes("UTF-8"));  
	    SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DESede");  
	    SecretKey securekey = keyFactory.generateSecret(dks);  
	      
	    //--Chipher对象解密  
	    Cipher cipher = Cipher.getInstance("DESede/ECB/PKCS5Padding");  
	    cipher.init(Cipher.DECRYPT_MODE, securekey);  
	    byte[] retByte = cipher.doFinal(bytesrc);  
	      
	    return new String(retByte);  
	}
}
