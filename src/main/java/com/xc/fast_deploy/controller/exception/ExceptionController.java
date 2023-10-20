package com.xc.fast_deploy.controller.exception;

import com.xc.fast_deploy.dto.ResponseDTO;
import com.xc.fast_deploy.myException.UnauthorcateException;
import com.xc.fast_deploy.myException.UnauthorizedException;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.ShiroException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.servlet.http.HttpServletRequest;

@RestControllerAdvice
@Slf4j
public class ExceptionController {
  
  //捕捉shiro的异常
  @ResponseStatus(HttpStatus.UNAUTHORIZED)
  @ExceptionHandler(ShiroException.class)
  public ResponseDTO handle401(ShiroException e) {
    log.info("异常已经捕捉到!");
    return new ResponseDTO(403, "对不起,您当前没有权限访问该内容");
  }
  
  // 捕捉 未经过验证异常
  @ResponseStatus(HttpStatus.UNAUTHORIZED)
  @ExceptionHandler(UnauthorcateException.class)
  public ResponseDTO handleUnauthentication401() {
    log.error("未登录异常!");
    return new ResponseDTO(401, "未登录,请重新获取token");
  }
  
  // 捕捉UnauthorizedException
  @ResponseStatus(HttpStatus.FORBIDDEN)
  @ExceptionHandler(UnauthorizedException.class)
  public ResponseDTO handleUnauthorization403() {
    log.error("异常已经捕捉到!");
    return new ResponseDTO(403, "对不起,您当前没有权限访问该内容");
  }
  
  // 捕捉 资源未找到异常
  @ResponseStatus(HttpStatus.NOT_FOUND)
  @ExceptionHandler(NoHandlerFoundException.class)
  public ResponseDTO handle404() {
    log.error("资源未找到异常!");
    return new ResponseDTO(404, "未找到请求资源");
  }
  
  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseDTO handleHTTPMessageNotReadabl() {
    log.error("传入参数不可读取!");
    return new ResponseDTO(406, "未能读取请求数据");
  }
  
  @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
  public ResponseDTO handleHTTPNotSupportException() {
    log.error("http请求方式错误!");
    return new ResponseDTO(400, "该次请求无效");
  }
  
  //捕捉其他所有异常
  @ExceptionHandler(Exception.class)
  public ResponseDTO globalException(HttpServletRequest request, Throwable ex) {
    log.error("访问异常", ex);
    return new ResponseDTO(getStatus(request).value(), ex.getMessage());
  }
  
  //获取http_status
  private HttpStatus getStatus(HttpServletRequest request) {
    Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
    if (statusCode == null) {
      return HttpStatus.INTERNAL_SERVER_ERROR;
    }
    return HttpStatus.valueOf(statusCode);
  }
  
}
