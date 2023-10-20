package com.xc.fast_deploy.myenum;

public enum ComplieTypeEnum {
  
  COMMAND_COMPILIE(0, "命令行编译"),
  FILE_COMPILIE(1, "指定编译脚本");
  
  private Integer code;
  private String desc;
  
  ComplieTypeEnum(Integer code, String desc) {
    this.code = code;
    this.desc = desc;
  }
  
  public Integer getCode() {
    return code;
  }
  
  public void setCode(Integer code) {
    this.code = code;
  }
  
  public String getDesc() {
    return desc;
  }
  
  public void setDesc(String desc) {
    this.desc = desc;
  }
  
  public static ComplieTypeEnum getEnumByType(Integer code) {
    for (ComplieTypeEnum complieTypeEnum : values()) {
      if (complieTypeEnum.getCode() == code) {
        return complieTypeEnum;
      }
    }
    return null;
  }
}
