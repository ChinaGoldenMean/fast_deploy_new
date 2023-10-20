package com.xc.fast_deploy.myenum;

public enum ApproveStatusEnum {
  
  APPROVE_NO_SUBMIT(0, "需求未提交"),
  APPROVE_SUBMIT(1, "未审批"),
  APPROVE_PASS(2, "审批通过"),
  APPROVE_NO_PASS(3, "审批不通过"),
  APPROVE_COMPLETE(4, "需求完成");
  
  private Integer code;
  private String desc;
  
  ApproveStatusEnum(Integer code, String desc) {
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
  
  public static ApproveStatusEnum getStatusByCode(Integer code) {
    for (ApproveStatusEnum approveStatusEnum : values()) {
      if (approveStatusEnum.getCode().equals(code)) {
        return approveStatusEnum;
      }
    }
    return null;
  }
}
