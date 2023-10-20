package com.xc.fast_deploy.myenum.k8sEnum;

public enum K8sResourceStatusEnum {
  
  IS_NOT_ONLINE(0, "未在线"),
  IS_ONLINE(1, "在线上"),
  ALL_RESOURCE(2, "全部");
  
  private Integer code;
  private String msg;
  
  K8sResourceStatusEnum(Integer code, String msg) {
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
  
  public static K8sResourceStatusEnum getEnumByCode(Integer code) {
    for (K8sResourceStatusEnum k8sResourceStatusEnum : values()) {
      if (k8sResourceStatusEnum.getCode().equals(code)) {
        return k8sResourceStatusEnum;
      }
    }
    return null;
  }
}
