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
//     * ??????  
//     */    
//    private RSAPrivateKey privateKey;    
//    
//    /**  
//     * ??????  
//     */    
//    private RSAPublicKey publicKey;    
//        
//    /**  
//     * ????????????????????????????????????  
//     */    
//    private static final char[] HEX_CHAR= {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};    
//        
//    
//    /**  
//     * ????????????  
//     * @return ?????????????????????  
//     */    
//    public RSAPrivateKey getPrivateKey() {    
//        return privateKey;    
//    }    
//    
//    /**  
//     * ????????????  
//     * @return ?????????????????????  
//     */    
//    public RSAPublicKey getPublicKey() {    
//        return publicKey;    
//    }    
//    
//    /**  
//     * ?????????????????????  
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
////        System.out.println("??????:"+ new BASE64Encoder().encode(privateKey.getEncoded()));  
////        System.out.println("??????:"+ new BASE64Encoder().encode(privateKey.getEncoded()));
//    }    
//    
//    /**  
//     * ????????????????????????????????????  
//     * @param in ???????????????  
//     * @throws Exception ??????????????????????????????  
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
//            throw new Exception("???????????????????????????");    
//        } catch (NullPointerException e) {    
//            throw new Exception("?????????????????????");    
//        }    
//    }    
//    
//    
//    /**  
//     * ???????????????????????????  
//     * @param publicKeyStr ?????????????????????  
//     * @throws Exception ??????????????????????????????  
//     */    
//    public void loadPublicKey(String publicKeyStr) throws Exception{    
//        try {    
////            BASE64Decoder base64Decoder= new BASE64Decoder();    
//            byte[] buffer= Base64.decode(publicKeyStr,Base64.DEFAULT);  
//            KeyFactory keyFactory= KeyFactory.getInstance("RSA");    
//            X509EncodedKeySpec keySpec= new X509EncodedKeySpec(buffer);    
//            this.publicKey= (RSAPublicKey) keyFactory.generatePublic(keySpec);    
//        } catch (NoSuchAlgorithmException e) {    
//            throw new Exception("????????????");    
//        } catch (InvalidKeySpecException e) {    
//            throw new Exception("????????????");    
//        } catch (NullPointerException e) {    
//            throw new Exception("??????????????????");    
//        }    
//    }    
//    
//    /**  
//     * ????????????????????????  
//     * @param keyFileName ???????????????  
//     * @return ????????????  
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
//            throw new Exception("????????????????????????");    
//        } catch (NullPointerException e) {    
//            throw new Exception("?????????????????????");    
//        }    
//    }    
//    
//    /**  
//     * ????????????  
//     * @param publicKey ??????  
//     * @param plainTextData ????????????  
//     * @return  
//     * @throws Exception ??????????????????????????????  
//     */    
//    public byte[] encrypt(RSAPublicKey publicKey, byte[] plainTextData) throws Exception{    
//        if(publicKey== null){    
//            throw new Exception("??????????????????, ?????????");    
//        }    
//        Cipher cipher= null;    
//        try {    
//            cipher= Cipher.getInstance("RSA");//, new BouncyCastleProvider());    
//            cipher.init(Cipher.ENCRYPT_MODE, publicKey);    
//            byte[] output= cipher.doFinal(plainTextData);    
//            return output;    
//        } catch (NoSuchAlgorithmException e) {    
//            throw new Exception("??????????????????");    
//        } catch (NoSuchPaddingException e) {    
//            e.printStackTrace();    
//            return null;    
//        }catch (InvalidKeyException e) {    
//            throw new Exception("??????????????????,?????????");    
//        } catch (IllegalBlockSizeException e) {    
//            throw new Exception("??????????????????");    
//        } catch (BadPaddingException e) {    
//            throw new Exception("?????????????????????");    
//        }    
//    }    
//    
//    /**  
//     * ????????????  
//     * @param privateKey ??????  
//     * @param cipherData ????????????  
//     * @return ??????  
//     * @throws Exception ??????????????????????????????  
//     */    
//    public byte[] decrypt(RSAPrivateKey privateKey, byte[] cipherData) throws Exception{    
//        if (privateKey== null){    
//            throw new Exception("??????????????????, ?????????");    
//        }    
//        Cipher cipher= null;    
//        try {    
//            cipher= Cipher.getInstance("RSA");//, new BouncyCastleProvider());    
//            cipher.init(Cipher.DECRYPT_MODE, privateKey);    
//            byte[] output= cipher.doFinal(cipherData);    
//            return output;    
//        } catch (NoSuchAlgorithmException e) {    
//            throw new Exception("??????????????????");    
//        } catch (NoSuchPaddingException e) {    
//            e.printStackTrace();    
//            return null;    
//        }catch (InvalidKeyException e) {    
//            throw new Exception("??????????????????,?????????");    
//        } catch (IllegalBlockSizeException e) {    
//            throw new Exception("??????????????????");    
//        } catch (BadPaddingException e) {    
//            throw new Exception("?????????????????????");    
//        }           
//    }    
//    
//        
//    /**  
//     * ????????????????????????????????????  
//     * @param data ????????????  
//     * @return ??????????????????  
//     */    
//    public static String byteArrayToString(byte[] data){    
//        StringBuilder stringBuilder= new StringBuilder();    
//        for (int i=0; i<data.length; i++){    
//            //???????????????????????? ???????????????????????????????????????????????? ?????????????????????    
//            stringBuilder.isAppend(HEX_CHAR[(data[i] & 0xf0)>>> 4]);
//            //???????????????????????? ????????????????????????????????????????????????    
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
//        //????????????    
//        try {    
//            rsaEncrypt.loadPublicKey(RSAEncrypt.DEFAULT_PUBLIC_KEY);    
//            System.out.println("??????????????????");    
//        } catch (Exception e) {    
//            System.err.println(e.getMessage());    
//            System.err.println("??????????????????");    
//        }    
//    
//        //????????????    
//        try {    
//            rsaEncrypt.loadPrivateKey(RSAEncrypt.DEFAULT_PRIVATE_KEY);    
//            System.out.println("??????????????????");    
//        } catch (Exception e) {    
//            System.err.println(e.getMessage());    
//            System.err.println("??????????????????");    
//        }    
//    
//        //???????????????    
//        String encryptStr= "abc";    
//        System.out.println("???????????????"+rsaEncrypt.getPrivateKey().toString().length());  
//        System.out.println("???????????????"+rsaEncrypt.getPublicKey().toString().length());  
//        try {    
//            //??????    
//            byte[] cipher = rsaEncrypt.encrypt(rsaEncrypt.getPublicKey(), encryptStr.getBytes());    
//              
//             
//            //??????    
//            byte[] plainText = rsaEncrypt.decrypt(rsaEncrypt.getPrivateKey(), cipher);    
//  
//              
//             
//            System.out.println("????????????:"+ cipher.length);    
//            System.out.println(RSAEncrypt.byteArrayToString(cipher));  
//            String base64str = new BASE64Encoder().encode(cipher);
//            System.out.println(new BASE64Encoder().encode(cipher));
//            System.out.println("????????????:"+ plainText.length);    
//            System.out.println(RSAEncrypt.byteArrayToString(plainText));   
//            System.out.println(new String(rsaEncrypt.decrypt(rsaEncrypt.getPrivateKey(),new BASE64Decoder().decodeBuffer(base64str))));
//            System.out.println(new String(plainText));    
//        } catch (Exception e) {    
//            System.err.println(e.getMessage());    
//        }    
//    }  
//}  