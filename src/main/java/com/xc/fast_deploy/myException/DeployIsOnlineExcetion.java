package com.xc.fast_deploy.myException;

public class DeployIsOnlineExcetion extends RuntimeException {
  
  public DeployIsOnlineExcetion() {
  }
  
  public DeployIsOnlineExcetion(String message) {
    super(message);
  }
}
