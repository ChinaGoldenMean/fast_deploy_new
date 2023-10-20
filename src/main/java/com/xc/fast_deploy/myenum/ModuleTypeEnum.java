package com.xc.fast_deploy.myenum;

public enum ModuleTypeEnum {
  
  SVN_SOURCE_CODE(0, "svn源码类型"),
  PROJECT_PACKAGE(1, "程序包类型"),
  GIT_SOURCE_CODE(2, "git源码类型"),
  GIT_AUTO_UP_SOURCE_CODE(5, "git源码脚本更新类型"),
  SVN_AUTO_UP_CODE(3, "svn源码脚本更新类型"),
  YAML_DEPLOY_TYPE(4, "yaml直接发布类型");
  
  private Integer moduleTypeCode;
  private String moduleTypeDes;
  
  ModuleTypeEnum(Integer moduleTypeCode, String moduleTypeDes) {
    this.moduleTypeCode = moduleTypeCode;
    this.moduleTypeDes = moduleTypeDes;
  }
  
  public Integer getModuleTypeCode() {
    return moduleTypeCode;
  }
  
  public void setModuleTypeCode(Integer moduleTypeCode) {
    this.moduleTypeCode = moduleTypeCode;
  }
  
  public String getModuleTypeDes() {
    return moduleTypeDes;
  }
  
  public void setModuleTypeDes(String moduleTypeDes) {
    this.moduleTypeDes = moduleTypeDes;
  }
  
  public static ModuleTypeEnum getTypeByCode(Integer moduleTypeCode) {
    for (ModuleTypeEnum typeEnum : values()) {
      if (typeEnum.getModuleTypeCode().equals(moduleTypeCode)) {
        return typeEnum;
      }
    }
    return null;
  }
}
