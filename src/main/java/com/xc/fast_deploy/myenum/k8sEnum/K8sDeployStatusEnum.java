package com.xc.fast_deploy.myenum.k8sEnum;

public enum K8sDeployStatusEnum {
  
  SUCCESS(1, "发布成功"),
  FAIL(2, "发布失败"),
  NOT_SUPPORT(3, "不支持的类型");
  
  private Integer code;
  private String desMessage;
  
  K8sDeployStatusEnum(Integer code, String desMessage) {
    this.code = code;
    this.desMessage = desMessage;
  }
  
  public Integer getCode() {
    return code;
  }
  
  public void setCode(Integer code) {
    this.code = code;
  }
  
  public String getDesMessage() {
    return desMessage;
  }
  
  public void setDesMessage(String desMessage) {
    this.desMessage = desMessage;
  }
}
