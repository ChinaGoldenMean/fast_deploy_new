package com.xc.fast_deploy.myException;

public class NotDeleteOkException extends RuntimeException {
  
  public NotDeleteOkException() {
  }
  
  public NotDeleteOkException(String message) {
    super(message);
  }
}
