package com.xc.fast_deploy.utils.encyption_utils;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class EncryptUtil {
  
  private static final String ENCRYPT_PASSWORD = "fast_deploy_pass";
  private static final String AES_TYPE = "AES/ECB/PKCS5Padding";
  private static final String CODE_TYPE = "UTF-8";
  
  public static String md5(String src) {
    try {
      MessageDigest md5 = MessageDigest.getInstance("MD5");
      byte[] digest = md5.digest(src.getBytes());
      String md5code = new BigInteger(1, digest).toString(16);// 16进制数字
      // 如果生成数字未满32位，需要前面补0
      for (int i = 0; i < 32 - md5code.length(); i++) {
        md5code = "0" + md5code;
      }
      return md5code;
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
      return "";
    }
  }
  
  /**
   * aes 的相关加密
   *
   * @param content
   * @return
   */
  public static String encrpt(String content) {
    try {
      SecretKeySpec key = new SecretKeySpec(ENCRYPT_PASSWORD.getBytes(), "AES");
      Cipher cipher = Cipher.getInstance(AES_TYPE);
      cipher.init(Cipher.ENCRYPT_MODE, key);
      byte[] encryptedData = cipher.doFinal(content.getBytes(CODE_TYPE));
      return Base64.encodeBase64String(encryptedData);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }
  
  /**
   * 相关的解密
   *
   * @param content
   * @return
   */
  public static String decrypt(String content) {
    try {
      byte[] byteMi = Base64.decodeBase64(content);
      SecretKeySpec key = new SecretKeySpec(
          ENCRYPT_PASSWORD.getBytes(), "AES");
      Cipher cipher = Cipher.getInstance(AES_TYPE);
      //与加密时不同MODE:Cipher.DECRYPT_MODE
      cipher.init(Cipher.DECRYPT_MODE, key);  //CBC类型的可以在第三个参数传递偏移量zeroIv,ECB没有偏移量
      byte[] decryptedData = cipher.doFinal(byteMi);
      return new String(decryptedData, CODE_TYPE);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }
  
  public static void main(String[] args) {
    String context = "8F8UyjE4FCBocbxWj9Fbcg==";
    System.out.println(decrypt(context));
    ;
  }
}

