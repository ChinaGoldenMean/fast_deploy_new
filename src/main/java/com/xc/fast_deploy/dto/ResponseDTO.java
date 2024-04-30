package com.xc.fast_deploy.dto;

import com.xc.fast_deploy.myenum.ResponseEnum;
import lombok.Data;

import java.io.Serializable;

@Data
public class ResponseDTO implements Serializable {
  
  private static final long serialVersionUID = -1893273044011076834L;
  
  private Object data;
  
  private Integer code;
  
  private String msg;
  
  public ResponseDTO() {
  }
  
  public ResponseDTO(Integer code, String msg) {
    this.code = code;
    this.msg = msg;
  }
  
  public ResponseDTO(Integer code, String msg, Object data) {
    this.data = data;
    this.code = code;
    this.msg = msg;
  }
  
  public ResponseDTO success(Object data) {
    setCode(ResponseEnum.SUCCESS.getStatus());
    setMsg(ResponseEnum.SUCCESS.getMsg());
    this.data =data ;
    return this;
  }
  
  public void success() {
    setCode(ResponseEnum.SUCCESS.getStatus());
    setMsg(ResponseEnum.SUCCESS.getMsg());
    setData(data);
  }
  
  public void fail() {
    setCode(ResponseEnum.FAIL.getStatus());
    setMsg(ResponseEnum.FAIL.getMsg());
    setData(data);
  }
  
  public ResponseDTO fail(Object data) {
    setCode(ResponseEnum.FAIL.getStatus());
    setMsg(data.toString());
    setData(data);
    return this;
  }
  
  public void argsNotOK() {
    setCode(ResponseEnum.FAIL.getStatus());
    setMsg("参数不正确!!");
  }
  
  public static ResponseDTO successReq() {
    return new ResponseDTO(ResponseEnum.SUCCESS.getStatus(), ResponseEnum.SUCCESS.getMsg());
  }
  
  public static ResponseDTO successReq(Object data) {
    return new ResponseDTO(ResponseEnum.SUCCESS.getStatus(), ResponseEnum.SUCCESS.getMsg(), data);
  }
}
