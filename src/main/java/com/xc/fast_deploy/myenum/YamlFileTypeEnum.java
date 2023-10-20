package com.xc.fast_deploy.myenum;

public enum YamlFileTypeEnum {
  
  YAML_FILE_APPOINT(0, "模块文件中指定yaml文件"),
  YAML_FILE_UPLOAD(1, "上传yaml文件");
  
  private Integer code;
  private String desc;
  
  YamlFileTypeEnum(Integer code, String desc) {
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
  
  public static YamlFileTypeEnum getTypeByCode(Integer yamlFileUpType) {
    for (YamlFileTypeEnum yamlFileTypeEnum : values()) {
      if (yamlFileTypeEnum.getCode().equals(yamlFileUpType)) {
        return yamlFileTypeEnum;
      }
    }
    return null;
  }
}
