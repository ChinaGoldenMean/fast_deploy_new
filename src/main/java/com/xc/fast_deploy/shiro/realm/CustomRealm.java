package com.xc.fast_deploy.shiro.realm;

import com.xc.fast_deploy.myException.UnauthorcateException;
import com.xc.fast_deploy.shiro.token.JwtToken;
import com.xc.fast_deploy.shiro.token.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;

@Slf4j
public class CustomRealm extends AuthorizingRealm {
  
  @Override
  public boolean supports(AuthenticationToken token) {
    return token instanceof JwtToken || token instanceof UsernamePasswordToken;
  }
  
  //授权操作
  @Override
  protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
    SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
    return authorizationInfo;
  }
  
  //验证操作
  @Override
  protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken)
      throws AuthenticationException {
    //   log.info("realm的验证操作");
    String token = (String) authenticationToken.getPrincipal();
    if (StringUtils.isBlank(token)) {
      throw new UnauthorcateException("该用户并不存在");
    }
    //判断是否是由自己的登录接口完成的登录校验
//        if (authenticationToken instanceof UsernamePasswordToken) {
//            PModuleUserMapper userMapper = SpringUtil.getBean(PModuleUserMapper.class);
//            PModuleUser moduleUser = userMapper.selectByUserName(token);
//            if (moduleUser == null) {
//                throw new UnauthorcateException("该用户并不存在");
//            }
//            String pwd = new String((char[]) authenticationToken.getCredentials());
//            if (!moduleUser.getPassword().equals(pwd)) {
//                throw new UnauthorcateException("密码错误");
//            }
//            return new SimpleAuthenticationInfo(token, pwd, getName());
//        } else {
    
    String username = JwtUtil.getUserNameFromToken(token);
    String userId = JwtUtil.getUserIdFromToken(token);
    
    if (StringUtils.isBlank(username) || StringUtils.isBlank(userId)) {
      throw new UnauthorcateException("该用户并不存在");
    }
    
    if (JwtUtil.BILLING_USER_ID.equals(userId) &&
        !JwtUtil.verify(token, username, userId, JwtUtil.BILLING_SECRET)) {
      throw new UnauthorcateException("token 已经过期");
    } else if (!JwtUtil.verify(token, username, userId, JwtUtil.SECRET)) {
      throw new UnauthorcateException("token 已经过期");
    }
    return new SimpleAuthenticationInfo(token, token, getName());

//        }
  }
  
}
