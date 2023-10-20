package com.xc.fast_deploy.myException;

public class K8SDeployException extends RuntimeException {
  
  public K8SDeployException() {
  }
  
  public K8SDeployException(String message) {
    super(message);
  }
}
