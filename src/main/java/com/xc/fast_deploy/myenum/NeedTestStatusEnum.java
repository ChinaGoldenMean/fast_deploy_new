package com.xc.fast_deploy.myenum;

public enum NeedTestStatusEnum {
  
  TEST_PASS(1, "测试通过"),
  TEST_NOPASS(0, "测试未通过");
  
  private Integer code;
  private String desc;
  
  NeedTestStatusEnum(Integer code, String desc) {
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
  
  public NeedTestStatusEnum getStatusByCode(Integer code) {
    for (NeedTestStatusEnum needTestStatusEnum : values()) {
      if (needTestStatusEnum.getCode().equals(code)) {
        return needTestStatusEnum;
      }
    }
    return null;
  }
}
