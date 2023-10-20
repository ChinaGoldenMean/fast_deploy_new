package com.xc.fast_deploy.myenum;

public enum DockerfileTypeEnum {
  
  FILE_APPOINT(0, "模块文件中指定dockerfile"),
  FILE_UPLOAD(1, "上传dockerfile文件");
  
  private Integer code;
  private String desc;
  
  DockerfileTypeEnum(Integer code, String desc) {
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
  
  public static DockerfileTypeEnum getEnumByType(Integer dockerfileType) {
    if (dockerfileType != null) {
      for (DockerfileTypeEnum dockerfileTypeEnum : values()) {
        if (dockerfileType.equals(dockerfileTypeEnum.getCode())) {
          return dockerfileTypeEnum;
        }
      }
    }
    return null;
  }
}
