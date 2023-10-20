package com.xc.fast_deploy.myException;

public class GitOwnException extends RuntimeException {
  public GitOwnException() {
    super();
  }
  
  public GitOwnException(String message) {
    super(message);
  }
}
