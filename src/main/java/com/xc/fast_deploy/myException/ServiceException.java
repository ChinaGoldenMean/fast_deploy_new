package com.xc.fast_deploy.myException;

import lombok.Data;

import java.text.MessageFormat;

@Data
public class ServiceException extends RuntimeException {
  private static final long serialVersionUID = -3135239055465723987L;
  public Object data;
  
  public ServiceException() {
    super();
  }
  
  public ServiceException(String message) {
    super(message);
  }
  
  public ServiceException(String message, Object data) {
    super(message);
    this.data = data;
  }
  
  public ServiceException(Throwable cause) {
    super(cause);
  }
  
  /**
   * 自定义异常不打印堆栈信息
   *
   * @return
   */
  @Override
  public synchronized Throwable fillInStackTrace() {
    return this;
  }
  
  @Override
  public String toString() {
    return MessageFormat.format("{0} = {1}", this.getMessage(), this.data);
  }
}
