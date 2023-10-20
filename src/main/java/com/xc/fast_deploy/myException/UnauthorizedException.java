package com.xc.fast_deploy.myException;

import org.apache.shiro.authc.AuthenticationException;

public class UnauthorizedException extends AuthenticationException {
  
  public UnauthorizedException(String message) {
    super(message);
  }
  
  public UnauthorizedException() {
    super("无权限操作");
  }
}
