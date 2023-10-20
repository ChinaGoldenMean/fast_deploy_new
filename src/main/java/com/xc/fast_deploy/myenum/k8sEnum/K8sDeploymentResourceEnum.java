package com.xc.fast_deploy.myenum.k8sEnum;

public enum K8sDeploymentResourceEnum {
  
  DEPLOYMENT_LIMIT_ARGS(1, "deployment的资源限制参数"),
  DEPLOYMENT_LABEL_ARGS(2, "deployment的标签参数"),
  DEPLOYMENT_LABELS_NAME(3, "deployment的标签名"),
  DEPLOYMENT_VOLUME_MOUNTS(4, "deployment的卷挂载");
  
  private Integer code;
  private String msg;
  
  K8sDeploymentResourceEnum(Integer code, String msg) {
    this.code = code;
    this.msg = msg;
  }
  
  public Integer getCode() {
    return code;
  }
  
  public void setCode(Integer code) {
    this.code = code;
  }
  
  public String getMsg() {
    return msg;
  }
  
  public void setMsg(String msg) {
    this.msg = msg;
  }
  
  public static K8sDeploymentResourceEnum getByCode(Integer code) {
    if (code != null) {
      for (K8sDeploymentResourceEnum deploymentResourceEnum : values()) {
        if (deploymentResourceEnum.getCode().equals(code)) {
          return deploymentResourceEnum;
        }
      }
    }
    return null;
  }
}
