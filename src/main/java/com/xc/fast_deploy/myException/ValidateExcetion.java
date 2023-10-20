package com.xc.fast_deploy.myException;

public class ValidateExcetion extends RuntimeException {
  public ValidateExcetion() {
  }
  
  public ValidateExcetion(String message) {
    super(message);
  }
}
