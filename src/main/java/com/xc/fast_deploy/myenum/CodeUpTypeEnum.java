package com.xc.fast_deploy.myenum;

public enum CodeUpTypeEnum {
  
  UPLOAD_FILE(0, "上传模板文件"),
  TEXT_PUTIN(1, "文本填入");
  
  private Integer code;
  private String desc;
  
  CodeUpTypeEnum(Integer code, String desc) {
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
  
  public static CodeUpTypeEnum getTypeByCode(Integer svnUpCode) {
    for (CodeUpTypeEnum codeUpTypeEnum : values()) {
      if (codeUpTypeEnum.getCode().equals(svnUpCode)) {
        return codeUpTypeEnum;
      }
    }
    return null;
  }
}
