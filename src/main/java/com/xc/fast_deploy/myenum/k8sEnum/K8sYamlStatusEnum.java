package com.xc.fast_deploy.myenum.k8sEnum;

public enum K8sYamlStatusEnum {
  
  FILENOTEXIST(0, "文件不存在"),
  YAMLNOTOK(1, "yaml文件格式不正确"),
  YAMLFILEOK(2, "yaml文件格式正确转换");
  
  private Integer code;
  
  private String desribeMsg;
  
  K8sYamlStatusEnum(Integer code, String desribeMsg) {
    this.code = code;
    this.desribeMsg = desribeMsg;
  }
  
  public Integer getCode() {
    return code;
  }
  
  public void setCode(Integer code) {
    this.code = code;
  }
  
  public String getDesribeMsg() {
    return desribeMsg;
  }
  
  public void setDesribeMsg(String desribeMsg) {
    this.desribeMsg = desribeMsg;
  }
}
