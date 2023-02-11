package com.bloomlife.android.common;

import java.security.Key;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

import Decoder.BASE64Decoder;
import Decoder.BASE64Encoder;

public class RSAUtil {   
    
        
      public static PublicKey getPublicKey(String key) throws Exception {   
            byte[] keyBytes;   
            keyBytes = (new BASE64Decoder()).decodeBuffer(key);   
    
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);   
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");   
            PublicKey publicKey = keyFactory.generatePublic(keySpec);   
            return publicKey;   
      }   
        
      public static PrivateKey getPrivateKey(String key) throws Exception {   
            byte[] keyBytes;   
            keyBytes = (new BASE64Decoder()).decodeBuffer(key);   
    
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);   
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");   
            PrivateKey privateKey = keyFactory.generatePrivate(keySpec);   
            return privateKey;   
      }   
    
        
      public static String getKeyString(Key key) throws Exception {   
            byte[] keyBytes = key.getEncoded();   
            String s = (new BASE64Encoder()).encode(keyBytes);   
            return s;   
      }   
    
//  	private static final String RSA_PUBLICE ="K27booXr0zZK4BQlI45MIPJJjPPkpCCPELGvoK/wKYUwShIWE6szlZtrmV83C5eBIrT/3lxWTH3+IOA+5mefurVUvXmQIV7fXEHNHLphyM6L9gQsMAGZMCroPjWKvJM59OMS/d5dwwhiRgzVarxXSKpxBYhEYWJTu7nRJ+bZKjumeoqnCSpmntIiV+tRYgkYflOU6j2QlesjO5tzj/TL6n7vHSO/O1qafJkzHcv8Kn2hTy+IH7QXm7z5vtjXOucHkvBm1xWORXdifh+ChyVvP16dSEmCaCAH6KqtA4viX/HwRFEi4mIWaYSIQk74NdcnQOpFcTgEu2nDwtHaBMqahw==";
  	private static final String RSA_PUBLICE ="MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCi3gf5vWkIGfMhl7xl4gUe3aw4"+
"cyGcy+neDDisZ/e5aSoehlL5IzDrQX/kgS2voysdTpDpuMe1EkgbR8TiiaRrE7qo"+
"LUGurjDhvxHGboTvvyokiIXLsh2MqfUkKcmC7cAJRi1K9AmAsOLb28H3m46HkxNQ"+
"gRLhFkj8oqCX6sx+5wIDAQAB";
  	
  	
  	
  	public static final String privateKey = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAKLeB/m9aQgZ8yGX"+
  			"vGXiBR7drDhzIZzL6d4MOKxn97lpKh6GUvkjMOtBf+SBLa+jKx1OkOm4x7USSBtH"+
  			"xOKJpGsTuqgtQa6uMOG/EcZuhO+/KiSIhcuyHYyp9SQpyYLtwAlGLUr0CYCw4tvb"+
  			"wfebjoeTE1CBEuEWSPyioJfqzH7nAgMBAAECgYBhxGELRO2A5Hrz5m88xbIHNlCE"+
  			"znAV/L6WAcYfkIW/smaLTl0Om1ZpUrdgRz9SmONvw1LwSnDvvFq2rZ1909xzSYHd"+
  			"h4cNjcM/ppeKZGfcERrHsSlpPP5/d94vcCxN1/42OrSRuiDmzdvyRuNRRwwdFu1V"+
  			"sOQMcbs9CAnSDb6kKQJBANAYuyye1paoYm+vbDT1KFtxLT/1UEr2pBfConDyUV7C"+
  			"i8Oba9UL84WJ3gGaPx83eDs8JqjNAxHkqM3rlhYJxYsCQQDIW+hzjlvgh3DBJ6n/"+
  			"Pg7safTvSpXlzATyVkZcaEw0ITT2NETjo//s60rQYKGwRy50LTXyXlazhBI3iHBO"+
  			"iC+VAkAlFKMVumAGjI6t1BZ2AVFlqe8kPHRIR1bKHRaRBbzWI9h/zIwUdIK6Quza"+
  			"YD/rMhkshPVmktF/OL2To+0klC0tAkEAjEg9gtsD3Ts3aSYaUoni7Qhq5Dg2p8Sn"+
  			"Gf3qGb00zbMKX+/M5A3pnLpJkYh27POJxPH6uY3v6pToJFW1PGcB2QJBAME3o5Mq"+
  			"Q1rjRJdP55DYEAJKaJW/UKL+yh3tlh26qUK7nhp9GmerYOI3RBxLBPscjuGVts/E"+
  			"Hxvm+Kdk6FWkCkM=";
  	
  	
//  	private static final String RSA_PUBLICE =
//  			
//  			"MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA258a3rdUGCXq7O80GcrJ" + 
//  					"DcD+Laj4olMF2Y72i/DZFbduj7acYORp77ceFLnCHfTQTzMCN99r+LmuFv8LZgEK" + 
//  					"I2+boLSxXZbsSr04fdkXkFHYTRvpvq2LFnj1wCMbHfrQizier7l4rFevvPjR6+Zh" + 
//  					"4+P4p/j2dxAHgaoXUFtqKrKxCtdI/qNDR34A0fGcdkqaK8fKOyTjgXgGpF6zKE6u" + 
//  					"23xIsYtt7mRsTE11X2peA0/5T0udTF2OO6zb1y/vUP1AnWM5Eph5A1Exw5Bo923i" + 
//  					"dwIDAQAB";

    
      public static void main() throws Exception {   
    	  
//            KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");   
//            //密钥位数   
//            keyPairGen.initialize(1024);   
//            //密钥对   
//            KeyPair keyPair = keyPairGen.generateKeyPair();   
//    
//            // 公钥   
//            PublicKey publicKey = (RSAPublicKey) keyPair.getPublic();   
//    
//            // 私钥   
//            PrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();   
//    
//            String publicKeyString = getKeyString(publicKey);   
//            String publicKeyString = getPublicKey(RSA_PUBLICE);   
    	  
    	  
//            System.out.println("public:/n" + publicKeyString);   
    
//            String privateKeyString = getKeyString(privateKey);   
//            System.out.println("private:/n" + privateKeyString);   
    
            //加解密类   
//            Cipher cipher = Cipher.getInstance("RSA");
    	  Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");   
    
            //明文   
            byte[] plainText = ("Wechat微信和WechatMoments微信朋友圈的appid是一样的；  注意：开发者不能用我们这两个平台的appid,否则分享不了 微信测试的时候，微信测试需要先签名打包出apk,"+
		"sample测试微信，要先签名打包，keystore在sample项目中，密码123456 BypassApproval是绕过审核的标记，设置为true后AppId将被忽略，故不经过"+
		"审核的应用也可以执行分享，但是仅限于分享文字和图片，不能分享其他类型， 默认值为false。此外，微信收藏不支持此字段。").getBytes("UTF-8");
    
            //加密   
            cipher.init(Cipher.ENCRYPT_MODE, getPublicKey(RSA_PUBLICE));   
            byte[] enBytes = cipher.doFinal(plainText);   
    
//           //通过密钥字符串得到密钥   
//            publicKey = getPublicKey(publicKeyString);   
//            privateKey = getPrivateKey(privateKeyString);   
    
            //解密   
            cipher.init(Cipher.DECRYPT_MODE, getPrivateKey(privateKey));   
            byte[]deBytes = cipher.doFinal(enBytes);   
    
//            publicKeyString = getKeyString(publicKey);   
//            System.out.println("public:/n" +publicKeyString);   
//    
//            privateKeyString = getKeyString(privateKey);   
//            System.out.println("private:/n" + privateKeyString);   
    
            String s = new String(deBytes);   
            System.out.println("decnry :  "+s);   
//            System.out.println("encry : "+Base64.encodeToString(enBytes, Base64.DEFAULT));   
      }   
    
}   