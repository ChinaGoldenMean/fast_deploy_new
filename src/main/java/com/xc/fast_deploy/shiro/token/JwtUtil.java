package com.xc.fast_deploy.shiro.token;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.xc.fast_deploy.model.master_model.ModuleUser;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;

import java.util.Date;

/**
 * jwt 工具类
 */
@Slf4j
public class JwtUtil {
  
  public static final String SECRET = "fast_deploy_platform";
  
  public static final String BILLING_SECRET = "k8s_billing_deploy_secret";
  
  public static final String BILLING_USER_ID = "f320cfb2-4f9a-4679-872a-e91ed5ce21e5";
  
  public static final String SELF_USER_ID = "V3s0xfs2-gsef-34ds-3452-e91edcde21e5";
  
  public static final String SELF_USER_NAME = "MY_SELF_USER";
  
  //过期时间 60分钟  2小时
//    private static final long EXPIRE_TIME = 12 * 60 * 60 * 1000;
  
  public static final long EXPIRE_TIME = 2 * 60 * 60 * 1000;
  
  public static final int REDIS_EXPIRE_TIME = 2 * 60 * 60;
//    public static final int REDIS_EXPIRE_TIME = 5 * 60;
  
  private static final long LONGEST_EXPIRE_TIME = 10 * 365 * 24 * 60 * 60 * 1000L;
  
  /**
   * 校验token是否正确
   *
   * @param token
   * @param username
   * @param secret
   * @return
   */
  public static boolean verify(String token, String username, String userId, String secret) {
    //根据密码生成JWT效验器
    try {
      Algorithm algorithm = Algorithm.HMAC256(secret);
      JWTVerifier verifier = JWT
          .require(algorithm)
          .withClaim("username", username)
          .withClaim("userId", userId).build();
      //校验token
      DecodedJWT decodedJWT = verifier.verify(token);
      return true;
    } catch (Exception e) {
      return false;
    }
  }
  
  /**
   * 生成签名
   */
  public static String sign(String username, String userId, String secret) {
    Date date = new Date(System.currentTimeMillis() + EXPIRE_TIME);
    Algorithm algorithm = Algorithm.HMAC256(secret);
    return JWT
        .create()
        .withClaim("username", username)
        .withClaim("userId", userId)
        .withExpiresAt(date)
        .sign(algorithm);
  }
  
  public static String sign(String username, String userId, String secret, Date expireDate) {
    Algorithm algorithm = Algorithm.HMAC256(secret);
    return JWT
        .create()
        .withClaim("username", username)
        .withClaim("userId", userId)
        .withExpiresAt(expireDate)
        .sign(algorithm);
  }
  
  private static String getUserInfo(String token, String userInfo) {
    try {
      if (token != null) {
        DecodedJWT jwt = JWT.decode(token);
        return jwt.getClaim(userInfo).asString();
      }
    } catch (JWTDecodeException e) {
      log.info(e.getMessage());
    }
    return null;
  }
  
  public static String getUserIdFromToken(String token) {
    return getUserInfo(token, "userId");
  }
  
  public static ModuleUser getModuleUserInfo() {
    ModuleUser moduleUser = new ModuleUser();
    Object o = SecurityUtils.getSubject().getPrincipal();
    if (o != null) {
      String token = (String) o;
      moduleUser.setId(getUserIdFromToken(token));
      moduleUser.setUsername(getUserNameFromToken(token));
    }
    return moduleUser;
  }
  
  public static String getUserNameFromToken(String token) {
    return getUserInfo(token, "username");
  }
  
}

