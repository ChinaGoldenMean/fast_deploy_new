package com.xc.fast_deploy.myenum;

public enum ResponseEnum {
  
  FAIL(201, "无数据或处理失败"),
  SUCCESS(200, "请求成功");
  
  private Integer status;
  
  private String msg;
  
  ResponseEnum(Integer status, String msg) {
    this.status = status;
    this.msg = msg;
  }
  
  public Integer getStatus() {
    return status;
  }
  
  public void setStatus(Integer status) {
    this.status = status;
  }
  
  public String getMsg() {
    return msg;
  }
  
  public void setMsg(String msg) {
    this.msg = msg;
  }
}
