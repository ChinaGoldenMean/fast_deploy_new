package com.xc.fast_deploy.myException;

//校验svnUrl是否存在抛出错误
public class SvnUrlNotExistException extends RuntimeException {
  public SvnUrlNotExistException() {
  }
  
  public SvnUrlNotExistException(String message) {
    super(message);
  }
}
