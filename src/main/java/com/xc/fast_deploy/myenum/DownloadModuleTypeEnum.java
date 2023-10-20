package com.xc.fast_deploy.myenum;

public enum DownloadModuleTypeEnum {
  
  MODULE_MANAGE_SVN_SINGLE(1, "single_module_download"),
  MODULE_MANAGE_SVN_BATCH(2, "batch_module_download");
  
  private Integer code;
  private String msg;
  
  DownloadModuleTypeEnum(Integer code, String msg) {
    this.code = code;
    this.msg = msg;
  }
  
  public Integer getCode() {
    return code;
  }
  
  public void setCode(Integer code) {
    this.code = code;
  }
  
  public String getMsg() {
    return msg;
  }
  
  public void setMsg(String msg) {
    this.msg = msg;
  }
  
  public static DownloadModuleTypeEnum getEnumByCode(Integer code) {
    if (code != null) {
      for (DownloadModuleTypeEnum downloadModuleTypeEnum : values()) {
        if (downloadModuleTypeEnum.getCode().equals(code)) {
          return downloadModuleTypeEnum;
        }
      }
    }
    return null;
  }
}
