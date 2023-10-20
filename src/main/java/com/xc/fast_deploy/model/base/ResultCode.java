package com.xc.fast_deploy.model.base;

/**
 * 枚举了一些常用API操作码
 */
public enum ResultCode implements IErrorCode {
  
  SUCCESS(200, "操作成功"),
  FAILED(500, "操作失败"),
  REQUEST_NOT_SUPPORT(400, "请求无效"),
  UNAUTHORIZED(401, "暂未登录或token已经过期"
  ),
  VALIDATE_FAILED(402, "参数检验失败"),
  FORBIDDEN(403, "没有相关权限"),
  NOT_FOUND_FAILED(404, "未找到资源"),
  REQUEST_PARA_ERROR(405, "缺少接口要求必填参数"),
  NOT_REQUESTED_DATA(406, "未能读取请求数据");
  public final Integer code;
  public final String message;
  
  ResultCode(Integer code, String message) {
    this.code = code;
    this.message = message;
  }
  
  @Override
  public Integer getCode() {
    return code;
  }
  
  @Override
  public String getMessage() {
    return message;
  }
  
}
