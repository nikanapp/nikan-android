//package com.bloomlife.android.common.security;
//
//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.InputStreamReader;
//import java.security.InvalidKeyException;
//import java.security.KeyFactory;
//import java.security.KeyPair;
//import java.security.KeyPairGenerator;
//import java.security.NoSuchAlgorithmException;
//import java.security.SecureRandom;
//import java.security.interfaces.RSAPrivateKey;
//import java.security.interfaces.RSAPublicKey;
//import java.security.spec.InvalidKeySpecException;
//import java.security.spec.PKCS8EncodedKeySpec;
//import java.security.spec.X509EncodedKeySpec;
//
//import javax.crypto.BadPaddingException;
//import javax.crypto.Cipher;
//import javax.crypto.IllegalBlockSizeException;
//import javax.crypto.NoSuchPaddingException;
//
//import android.util.Base64;
//  
//public class RSAEncrypt {  
//    private static final String DEFAULT_PUBLIC_KEY= "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDEZNUtCod4hzBMsH0JNaAz+mcXF87AIQLA6d6/2a5ipeemSgn/8omVEtNHs53ZIXz2fB0a2vh3WODA2T/ZNn1NjVkqtBhRyENViET0+n0H1TWszqf+vCWYOpOP2elta7af7nVXj4ojbzPQd9nqEoqc9RSP6ByOdrhh/rzLgAXkTwIDAQAB";    
//    		/*"MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDEZNUtCod4hzBMsH0JNaAz+mcX"+
//    		"F87AIQLA6d6/2a5ipeemSgn/8omVEtNHs53ZIXz2fB0a2vh3WODA2T/ZNn1NjVkq"+
//    		"tBhRyENViET0+n0H1TWszqf+vCWYOpOP2elta7af7nVXj4ojbzPQd9nqEoqc9RSP"+ 
//    		"6ByOdrhh/rzLgAXkTwIDAQAB";*/
//
//	
//	
//    
//    private static final String DEFAULT_PRIVATE_KEY= "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAMRk1S0Kh3iHMEywfQk1oDP6ZxcXzsAhAsDp3r/ZrmKl56ZKCf/yiZUS00ezndkhfPZ8HRra+HdY4MDZP9k2fU2NWSq0GFHIQ1WIRPT6fQfVNazOp/68JZg6k4/Z6W1rtp/udVePiiNvM9B32eoSipz1FI/oHI52uGH+vMuABeRPAgMBAAECgYEAvQfwx/GigzhcHPSi5QJguKcEIw4fSn57kAOLC04Ss5DMbxpElb50/q+n/RO9kwjQps6/lOG+1eEOM5DoC8KOxytYfvuo+DPA5TlOMe4L4tJ7vuUjIbK+A/Ui2MCYfGVDe2FhY0tlH0mkLCXTFQey5GePVT9Buyjf1/+LpreHJYECQQD+9A08gGh49fbNg5WytJNbCQrj0leQAtVFzWkdnNLBJSukqJ+ySzN8Mv4qRQjD0PV5Z+qRUI72WPAQw4T9rlY3AkEAxTM8nks95hsK7IugbIMBUw7KPMW3cmUYp8zhZ7frOa6TA7eYjAMwCyT3RkeZBnyyk6bx1hpc5clhX8E2ZIvWqQJAJoNfbIBJR9yAB9OUzYYRrHLVPk0MBSkoZn79HggOnjarPRhr51cM0/owAtqi1FHAgzJ7cLAmqHgO67ldYaTwSQJAVHAx/p29LXBa5xqKYKxfvs7QbeFDQapkOkWSJrUr9ZD4xliw6xLaWYtEjkVOEucGGA8d8k1rhcEK/tNelQMDsQJBAKriuVlFUhT/mQ+9QNzDGASOFUy9RrQIaW0Aoa6NfLxGA+ZUPa7nuJyZfHfRpM5cds1bY46bhQoVsKn88jKgEcI=";
//    /*"MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAMRk1S0Kh3iHMEyw"+ 
//    "fQk1oDP6ZxcXzsAhAsDp3r/ZrmKl56ZKCf/yiZUS00ezndkhfPZ8HRra+HdY4MDZ"+ 
//    "P9k2fU2NWSq0GFHIQ1WIRPT6fQfVNazOp/68JZg6k4/Z6W1rtp/udVePiiNvM9B3"+ 
//    "2eoSipz1FI/oHI52uGH+vMuABeRPAgMBAAECgYEAvQfwx/GigzhcHPSi5QJguKcE"+ 
//    "Iw4fSn57kAOLC04Ss5DMbxpElb50/q+n/RO9kwjQps6/lOG+1eEOM5DoC8KOxytY"+ 
//    "fvuo+DPA5TlOMe4L4tJ7vuUjIbK+A/Ui2MCYfGVDe2FhY0tlH0mkLCXTFQey5GeP"+ 
//    "VT9Buyjf1/+LpreHJYECQQD+9A08gGh49fbNg5WytJNbCQrj0leQAtVFzWkdnNLB"+ 
//    "JSukqJ+ySzN8Mv4qRQjD0PV5Z+qRUI72WPAQw4T9rlY3AkEAxTM8nks95hsK7Iug"+ 
//    "bIMBUw7KPMW3cmUYp8zhZ7frOa6TA7eYjAMwCyT3RkeZBnyyk6bx1hpc5clhX8E2"+ 
//    "ZIvWqQJAJoNfbIBJR9yAB9OUzYYRrHLVPk0MBSkoZn79HggOnjarPRhr51cM0/ow"+ 
//    "Atqi1FHAgzJ7cLAmqHgO67ldYaTwSQJAVHAx/p29LXBa5xqKYKxfvs7QbeFDQapk"+  
//    "OkWSJrUr9ZD4xliw6xLaWYtEjkVOEucGGA8d8k1rhcEK/tNelQMDsQJBAKriuVlF"+ 
//    "UhT/mQ+9QNzDGASOFUy9RrQIaW0Aoa6NfLxGA+ZUPa7nuJyZfHfRpM5cds1bY46b"+ 
//    "hQoVsKn88jKgEcI=";*/
//    
//    /**  
//     * 私钥  
//     */    
//    private RSAPrivateKey privateKey;    
//    
//    /**  
//     * 公钥  
//     */    
//    private RSAPublicKey publicKey;    
//        
//    /**  
//     * 字节数据转字符串专用集合  
//     */    
//    private static final char[] HEX_CHAR= {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};    
//        
//    
//    /**  
//     * 获取私钥  
//     * @return 当前的私钥对象  
//     */    
//    public RSAPrivateKey getPrivateKey() {    
//        return privateKey;    
//    }    
//    
//    /**  
//     * 获取公钥  
//     * @return 当前的公钥对象  
//     */    
//    public RSAPublicKey getPublicKey() {    
//        return publicKey;    
//    }    
//    
//    /**  
//     * 随机生成密钥对  
//     */    
//    public void genKeyPair(){    
//        KeyPairGenerator keyPairGen= null;    
//        try {    
//            keyPairGen= KeyPairGenerator.getInstance("RSA");    
//        } catch (NoSuchAlgorithmException e) {    
//            e.printStackTrace();    
//        }    
//        keyPairGen.initialize(1024, new SecureRandom());    
//        KeyPair keyPair= keyPairGen.generateKeyPair();    
//        this.privateKey= (RSAPrivateKey) keyPair.getPrivate();    
//        this.publicKey= (RSAPublicKey) keyPair.getPublic();  
//        
////        System.out.println("私钥:"+ new BASE64Encoder().encode(privateKey.getEncoded()));  
////        System.out.println("私钥:"+ new BASE64Encoder().encode(privateKey.getEncoded()));
//    }    
//    
//    /**  
//     * 从文件中输入流中加载公钥  
//     * @param in 公钥输入流  
//     * @throws Exception 加载公钥时产生的异常  
//     */    
//    public void loadPublicKey(InputStream in) throws Exception{    
//        try {    
//            BufferedReader br= new BufferedReader(new InputStreamReader(in));    
//            String readLine= null;    
//            StringBuilder sb= new StringBuilder();    
//            while((readLine= br.readLine())!=null){    
//                if(readLine.charAt(0)=='-'){    
//                    continue;    
//                }else{    
//                    sb.isAppend(readLine);
//                    sb.isAppend('\r');
//                }    
//            }    
//            loadPublicKey(sb.toString());    
//        } catch (IOException e) {    
//            throw new Exception("公钥数据流读取错误");    
//        } catch (NullPointerException e) {    
//            throw new Exception("公钥输入流为空");    
//        }    
//    }    
//    
//    
//    /**  
//     * 从字符串中加载公钥  
//     * @param publicKeyStr 公钥数据字符串  
//     * @throws Exception 加载公钥时产生的异常  
//     */    
//    public void loadPublicKey(String publicKeyStr) throws Exception{    
//        try {    
////            BASE64Decoder base64Decoder= new BASE64Decoder();    
//            byte[] buffer= Base64.decode(publicKeyStr,Base64.DEFAULT);  
//            KeyFactory keyFactory= KeyFactory.getInstance("RSA");    
//            X509EncodedKeySpec keySpec= new X509EncodedKeySpec(buffer);    
//            this.publicKey= (RSAPublicKey) keyFactory.generatePublic(keySpec);    
//        } catch (NoSuchAlgorithmException e) {    
//            throw new Exception("无此算法");    
//        } catch (InvalidKeySpecException e) {    
//            throw new Exception("公钥非法");    
//        } catch (NullPointerException e) {    
//            throw new Exception("公钥数据为空");    
//        }    
//    }    
//    
//    /**  
//     * 从文件中加载私钥  
//     * @param keyFileName 私钥文件名  
//     * @return 是否成功  
//     * @throws Exception   
//     */    
//    public void loadPrivateKey(InputStream in) throws Exception{    
//        try {    
//            BufferedReader br= new BufferedReader(new InputStreamReader(in));    
//            String readLine= null;    
//            StringBuilder sb= new StringBuilder();    
//            while((readLine= br.readLine())!=null){    
//                if(readLine.charAt(0)=='-'){    
//                    continue;    
//                }else{    
//                    sb.isAppend(readLine);
//                    sb.isAppend('\r');
//                }    
//            }    
//            loadPrivateKey(sb.toString());    
//        } catch (IOException e) {    
//            throw new Exception("私钥数据读取错误");    
//        } catch (NullPointerException e) {    
//            throw new Exception("私钥输入流为空");    
//        }    
//    }    
//    
//    /**  
//     * 加密过程  
//     * @param publicKey 公钥  
//     * @param plainTextData 明文数据  
//     * @return  
//     * @throws Exception 加密过程中的异常信息  
//     */    
//    public byte[] encrypt(RSAPublicKey publicKey, byte[] plainTextData) throws Exception{    
//        if(publicKey== null){    
//            throw new Exception("加密公钥为空, 请设置");    
//        }    
//        Cipher cipher= null;    
//        try {    
//            cipher= Cipher.getInstance("RSA");//, new BouncyCastleProvider());    
//            cipher.init(Cipher.ENCRYPT_MODE, publicKey);    
//            byte[] output= cipher.doFinal(plainTextData);    
//            return output;    
//        } catch (NoSuchAlgorithmException e) {    
//            throw new Exception("无此加密算法");    
//        } catch (NoSuchPaddingException e) {    
//            e.printStackTrace();    
//            return null;    
//        }catch (InvalidKeyException e) {    
//            throw new Exception("加密公钥非法,请检查");    
//        } catch (IllegalBlockSizeException e) {    
//            throw new Exception("明文长度非法");    
//        } catch (BadPaddingException e) {    
//            throw new Exception("明文数据已损坏");    
//        }    
//    }    
//    
//    /**  
//     * 解密过程  
//     * @param privateKey 私钥  
//     * @param cipherData 密文数据  
//     * @return 明文  
//     * @throws Exception 解密过程中的异常信息  
//     */    
//    public byte[] decrypt(RSAPrivateKey privateKey, byte[] cipherData) throws Exception{    
//        if (privateKey== null){    
//            throw new Exception("解密私钥为空, 请设置");    
//        }    
//        Cipher cipher= null;    
//        try {    
//            cipher= Cipher.getInstance("RSA");//, new BouncyCastleProvider());    
//            cipher.init(Cipher.DECRYPT_MODE, privateKey);    
//            byte[] output= cipher.doFinal(cipherData);    
//            return output;    
//        } catch (NoSuchAlgorithmException e) {    
//            throw new Exception("无此解密算法");    
//        } catch (NoSuchPaddingException e) {    
//            e.printStackTrace();    
//            return null;    
//        }catch (InvalidKeyException e) {    
//            throw new Exception("解密私钥非法,请检查");    
//        } catch (IllegalBlockSizeException e) {    
//            throw new Exception("密文长度非法");    
//        } catch (BadPaddingException e) {    
//            throw new Exception("密文数据已损坏");    
//        }           
//    }    
//    
//        
//    /**  
//     * 字节数据转十六进制字符串  
//     * @param data 输入数据  
//     * @return 十六进制内容  
//     */    
//    public static String byteArrayToString(byte[] data){    
//        StringBuilder stringBuilder= new StringBuilder();    
//        for (int i=0; i<data.length; i++){    
//            //取出字节的高四位 作为索引得到相应的十六进制标识符 注意无符号右移    
//            stringBuilder.isAppend(HEX_CHAR[(data[i] & 0xf0)>>> 4]);
//            //取出字节的低四位 作为索引得到相应的十六进制标识符    
//            stringBuilder.isAppend(HEX_CHAR[(data[i] & 0x0f)]);
//            if (i<data.length-1){    
//                stringBuilder.isAppend(' ');
//            }    
//        }    
//        return stringBuilder.toString();    
//    }    
//    
//    
//    public static void main(String[] args){    
//        RSAEncrypt rsaEncrypt= new RSAEncrypt();    
//        //rsaEncrypt.genKeyPair();    
//         
//    
//        //加载公钥    
//        try {    
//            rsaEncrypt.loadPublicKey(RSAEncrypt.DEFAULT_PUBLIC_KEY);    
//            System.out.println("加载公钥成功");    
//        } catch (Exception e) {    
//            System.err.println(e.getMessage());    
//            System.err.println("加载公钥失败");    
//        }    
//    
//        //加载私钥    
//        try {    
//            rsaEncrypt.loadPrivateKey(RSAEncrypt.DEFAULT_PRIVATE_KEY);    
//            System.out.println("加载私钥成功");    
//        } catch (Exception e) {    
//            System.err.println(e.getMessage());    
//            System.err.println("加载私钥失败");    
//        }    
//    
//        //测试字符串    
//        String encryptStr= "abc";    
//        System.out.println("私钥长度："+rsaEncrypt.getPrivateKey().toString().length());  
//        System.out.println("公钥长度："+rsaEncrypt.getPublicKey().toString().length());  
//        try {    
//            //加密    
//            byte[] cipher = rsaEncrypt.encrypt(rsaEncrypt.getPublicKey(), encryptStr.getBytes());    
//              
//             
//            //解密    
//            byte[] plainText = rsaEncrypt.decrypt(rsaEncrypt.getPrivateKey(), cipher);    
//  
//              
//             
//            System.out.println("密文长度:"+ cipher.length);    
//            System.out.println(RSAEncrypt.byteArrayToString(cipher));  
//            String base64str = new BASE64Encoder().encode(cipher);
//            System.out.println(new BASE64Encoder().encode(cipher));
//            System.out.println("明文长度:"+ plainText.length);    
//            System.out.println(RSAEncrypt.byteArrayToString(plainText));   
//            System.out.println(new String(rsaEncrypt.decrypt(rsaEncrypt.getPrivateKey(),new BASE64Decoder().decodeBuffer(base64str))));
//            System.out.println(new String(plainText));    
//        } catch (Exception e) {    
//            System.err.println(e.getMessage());    
//        }    
//    }  
//}  