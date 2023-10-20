package com.xc.fast_deploy.myenum;

public enum ModuleMirrorStatusEnum {
  
  AVAILAVLE(1, "可用"),
  FAIL(0, "失败"),
  ONLINE(2, "正在进行中");
  
  private Integer code;
  private String status;
  
  ModuleMirrorStatusEnum(Integer code, String status) {
    this.code = code;
    this.status = status;
  }
  
  public static ModuleMirrorStatusEnum getTypeByCode(Integer moduleMirrorCode) {
    for (ModuleMirrorStatusEnum statusEnum : values()) {
      if (statusEnum.getCode().equals(moduleMirrorCode)) {
        return statusEnum;
      }
    }
    return null;
  }
  
  public Integer getCode() {
    return code;
  }
  
  public void setCode(Integer code) {
    this.code = code;
  }
  
  public String getStatus() {
    return status;
  }
  
  public void setStatus(String status) {
    this.status = status;
  }
}
