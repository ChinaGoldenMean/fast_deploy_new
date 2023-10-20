package com.xc.fast_deploy.shiro.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.web.servlet.ShiroHttpServletRequest;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.apache.shiro.web.util.WebUtils;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.Serializable;

@Slf4j
public class SessionConfig extends DefaultWebSessionManager {
  
  /**
   * 重写接收前台传过来的token
   *
   * @param request
   * @param response
   * @return
   */
  @Override
  protected Serializable getSessionId(ServletRequest request, ServletResponse response) {
    String token = WebUtils.getHttpRequest(request).getHeader("token");
    log.info("获取token信息: " + token);
    if (StringUtils.isNotBlank(token)) {
      request.setAttribute(ShiroHttpServletRequest.REFERENCED_SESSION_ID_SOURCE,
          "Stateless request");
      request.setAttribute(ShiroHttpServletRequest.REFERENCED_SESSION_ID, token);
      request.setAttribute(ShiroHttpServletRequest.REFERENCED_SESSION_ID_IS_VALID, Boolean.TRUE);
      return token;
    } else {
      return super.getSessionId(request, response);
    }
  }
}
