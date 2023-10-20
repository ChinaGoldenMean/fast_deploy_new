package com.xc.fast_deploy.myenum;

public enum CertificateTypeEnum {
  
  GIT_CERTIFICATE_TYPE(3, "git对应的凭证"),
  SVN_CERTIFICATE_TYPE(1, "svn对应的凭证"),
  HARBOR_CERTIFICATE_TYPE(2, "harbor对应的凭证");
  
  private Integer code;
  private String desc;
  
  CertificateTypeEnum(Integer code, String desc) {
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
}
