package com.xc.fast_deploy.myenum;

public enum ShiroAccessEnum {
  
  LOGIN_URI("/login"),
  LOGIN_URI_HTML("/loginHtml"),
  MODULE_LOGIN_URI("/pUser/pLogin"),
  DOC_URI("/doc"),
  DOC_URI_2("/doc2"),
  MODULE_DOWNLOAD_URI("/module/downloadFile"),
  UPDATE_MIRROR_URI("/mirror/updateMirror"),
  UPLOAD_MODULE_URI("/module/upload"),
  THREADDUMP_EXPORT_URI("/deploy/threadDump/export");
  
  private String uri;
  
  ShiroAccessEnum(String uri) {
    this.uri = uri;
  }
  
  public String getUri() {
    return uri;
  }
  
  public void setUri(String uri) {
    this.uri = uri;
  }
}
