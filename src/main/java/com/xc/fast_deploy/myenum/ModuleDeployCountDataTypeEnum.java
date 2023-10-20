package com.xc.fast_deploy.myenum;

public enum ModuleDeployCountDataTypeEnum {
  
  DAY_COUNT_DATA(1, "按天统计数量"),
  WEEK_COUNT_DATA(2, "按周统计数量"),
  MONTH_COUNT_DATA(3, "按月统计数量"),
  YEAR_COUNT_DATA(4, "按年统计数量"),
  SELF_COUNT_DATA(5, "自定义类型统计");
  
  private Integer type;
  private String msg;
  
  ModuleDeployCountDataTypeEnum(Integer type, String msg) {
    this.type = type;
    this.msg = msg;
  }
  
  public Integer getType() {
    return type;
  }
  
  public void setType(Integer type) {
    this.type = type;
  }
  
  public String getMsg() {
    return msg;
  }
  
  public void setMsg(String msg) {
    this.msg = msg;
  }
  
  public static ModuleDeployCountDataTypeEnum getEnumByType(Integer type) {
    if (type != null) {
      for (ModuleDeployCountDataTypeEnum dataTypeEnum : values()) {
        if (dataTypeEnum.type.equals(type)) {
          return dataTypeEnum;
        }
      }
    }
    return null;
  }
  
}
