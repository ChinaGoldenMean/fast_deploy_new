package com.xc.fast_deploy.myenum;

public enum IsOnlineYamlEnum {
  
  NOT_ONLINE_YAML(0, "否"),
  IS_ONLINE_YAML(1, "是");
  
  private Integer code;
  private String desc;
  
  IsOnlineYamlEnum(Integer code, String desc) {
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
  
  public static IsOnlineYamlEnum getOnlineYamlByCode(Integer code) {
    for (IsOnlineYamlEnum onlineYamlEnum : values()) {
      if (onlineYamlEnum.getCode().equals(code)) {
        return onlineYamlEnum;
      }
    }
    return null;
  }
}
