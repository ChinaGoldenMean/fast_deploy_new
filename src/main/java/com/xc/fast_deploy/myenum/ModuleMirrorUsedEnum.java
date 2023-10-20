package com.xc.fast_deploy.myenum;

public enum ModuleMirrorUsedEnum {
  
  ISUSED(1, "被发布或升级过"),
  ISNOTUSED(0, "未被使用过");
  
  private Integer code;
  private String status;
  
  ModuleMirrorUsedEnum(Integer code, String status) {
    this.code = code;
    this.status = status;
  }
  
  public static ModuleMirrorUsedEnum getTypeByCode(Integer moduleMirrorCode) {
    for (ModuleMirrorUsedEnum usedEnum : values()) {
      if (usedEnum.getCode().equals(moduleMirrorCode)) {
        return usedEnum;
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
