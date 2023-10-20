package com.xc.fast_deploy.myenum.k8sEnum;

public enum K8sContainerStatusEnum {
  
  RUNNINT("Running"),
  WAITING("Waiting"),
  TERMINATED("Terminated");
  
  private String status;
  
  K8sContainerStatusEnum(String status) {
    this.status = status;
  }
  
  public String getStatus() {
    return status;
  }
  
  public void setStatus(String status) {
    this.status = status;
  }
}
