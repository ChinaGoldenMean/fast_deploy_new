package com.xc.fast_deploy.myenum;

public enum UserRoleTypeEnum {
  DEVELOPER_ROLE(4, "开发角色"),
  APPROVER_ROLE(7, "审批角色");
  
  private Integer code;
  private String desc;
  
  UserRoleTypeEnum(Integer code, String desc) {
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
  
  public UserRoleTypeEnum getRoleByCode(Integer code) {
    for (UserRoleTypeEnum userRoleTypeEnum : values()) {
      if (userRoleTypeEnum.getCode().equals(code)) {
        return userRoleTypeEnum;
      }
    }
    return null;
  }
}
