package com.xc.fast_deploy.myException;

import org.apache.shiro.authc.AuthenticationException;

public class UnauthorcateException extends AuthenticationException {
  public UnauthorcateException() {
  }
  
  public UnauthorcateException(String message) {
    super(message);
  }
}
