/**
 * 
 */
package com.bloomlife.android.common.security;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

import android.util.Base64;

/**
 * @author <a href="mailto:xiai.fei@gmail.com">xiai_fei</a>
 * 
 * @date 2014-8-28 下午6:46:16
 */
public class RsaUtils {
	
	private static final String RSA_PUBLICE =

	"MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA258a3rdUGCXq7O80GcrJ" + "\r" +
	"DcD+Laj4olMF2Y72i/DZFbduj7acYORp77ceFLnCHfTQTzMCN99r+LmuFv8LZgEK" + "\r" +
	"I2+boLSxXZbsSr04fdkXkFHYTRvpvq2LFnj1wCMbHfrQizier7l4rFevvPjR6+Zh" + "\r" +
	"4+P4p/j2dxAHgaoXUFtqKrKxCtdI/qNDR34A0fGcdkqaK8fKOyTjgXgGpF6zKE6u" + "\r" +
	"23xIsYtt7mRsTE11X2peA0/5T0udTF2OO6zb1y/vUP1AnWM5Eph5A1Exw5Bo923i" + "\r" +
	"dwIDAQAB";

	private static final String ALGORITHM = "RSA";

	/**
	 * 
	 * 得到公钥
	 * 
	 * @param algorithm
	 * 
	 * @param bysKey
	 * 
	 * @return
	 */

	private static PublicKey getPublicKeyFromX509(String algorithm, String bysKey) throws Exception {

		byte[] decodedKey = Base64.decode(bysKey, Base64.DEFAULT);

		X509EncodedKeySpec x509 = new X509EncodedKeySpec(decodedKey);

		KeyFactory keyFactory = KeyFactory.getInstance(algorithm);

		return keyFactory.generatePublic(x509);

	}

	/**
	 * 
	 * 使用公钥加密
	 * 
	 * @param content
	 * 
	 * @param key
	 * 
	 * @return
	 */

	public static String encryptByPublic(String content) {

		try {
			
			PublicKey pubkey = getPublicKeyFromX509(ALGORITHM, RSA_PUBLICE);

			Cipher cipher = Cipher.getInstance("RSA");

			cipher.init(Cipher.ENCRYPT_MODE, pubkey);

			byte plaintext[] = content.getBytes("UTF-8");

			byte[] output = cipher.doFinal(plaintext);

			String s = new String(Base64.encode(output, Base64.DEFAULT));

			return s;

		} catch (Exception e) {

			return null;

		}

	}
	
}
