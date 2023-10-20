package com.xc.fast_deploy.shiro.filter;

import com.alibaba.fastjson.JSONObject;
import com.xc.fast_deploy.dto.ResponseDTO;
import com.xc.fast_deploy.myenum.ShiroAccessEnum;
import com.xc.fast_deploy.rediscache.JedisManage;
import com.xc.fast_deploy.shiro.token.JwtToken;
import com.xc.fast_deploy.shiro.token.JwtUtil;
import com.xc.fast_deploy.utils.HttpUtils;
import com.xc.fast_deploy.utils.SessionCookieUtils;
import com.xc.fast_deploy.utils.SpringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.web.filter.AccessControlFilter;
import org.apache.shiro.web.util.WebUtils;
import org.springframework.http.HttpStatus;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
public class MyShiroFilter extends AccessControlFilter {
  
  @Override
  protected boolean isAccessAllowed(ServletRequest servletRequest,
                                    ServletResponse servletResponse,
                                    Object o) {
    return false;
  }
  
  @Override
  protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
    HttpServletRequest httpServletRequest = (HttpServletRequest) request;
    HttpServletResponse httpServletResponse = (HttpServletResponse) response;
    String requestURI = httpServletRequest.getRequestURI();
    // log.info("shiro filter判断是否放行: {}", requestURI);
    //登录方法直接放行
    ShiroAccessEnum[] values = ShiroAccessEnum.values();
    for (ShiroAccessEnum accessEnum : values) {
      if (accessEnum.getUri().equals(requestURI)) {
        return true;
      }
    }
    
    //在访问index页面的是否判断是否已经登录
    // TODO: 2020/5/13   原有的基于zdyw平台的校验机制
//        if ("/".equals(requestURI) || "/index".equals(requestURI)) {
//            log.info("重定向验证");
//            httpServletResponse.sendRedirect(HttpUtils.REDIRECT_LOGIN + "&q=");
//            return false;
//        }
    
    // TODO: 2020/5/13  新建的权限认证机制,根据客户端的cookie值判断是否经过登录验证
    if ("/".equals(requestURI) || "/index".equals(requestURI)) {
      //客户端的cookie中的ticket判断是否已经登录过的
      String ticketToken = SessionCookieUtils.getCookie(httpServletRequest, "ticket");
      if (StringUtils.isNotBlank(ticketToken)) {
        try {
          //校验ticketToken的有效性
          JwtToken jwtToken = new JwtToken(ticketToken);
          //提交给realm进行登录
          getSubject(request, response).login(jwtToken);
          //如果没有
        } catch (Exception e) {
          log.info("token验证失败");
          httpServletResponse.sendRedirect("/loginHtml");
        }
        return true;
      } else {
        log.info("重定向验证");
        httpServletResponse.sendRedirect("/loginHtml");
        //httpServletResponse.sendRedirect(HttpUtils.REDIRECT_LOGIN + "&q=");
        return false;
      }
//            httpServletResponse.sendRedirect("/loginHtml");
//            return true;
    }
    
    //其他访问接口的情况 需要在请求头上加上X-token进行验证
    if (StringUtils.isNotBlank(httpServletRequest.getHeader("X-token"))
        || StringUtils.isNotBlank(httpServletRequest.getHeader("access-token"))) {
      try {
        if (executeLogin(httpServletRequest, httpServletResponse)) {
          //验证成功直接放行
          return true;
        }
      } catch (Exception e) {
        log.info("token验证失败");
      }
    }
    response401(httpServletRequest, httpServletResponse, null);
    return false;
  }
  
  protected boolean executeLogin(ServletRequest request, ServletResponse response) {
    HttpServletRequest servletRequest = (HttpServletRequest) request;
    String token1 = servletRequest.getHeader("X-token");
    String token2 = servletRequest.getHeader("access-token");
    // log.info("token1:{} token2:{}", token1, token2);
    
    if (StringUtils.isNotBlank(token1)) {
      String userId = JwtUtil.getUserIdFromToken(token1);
      //如果这里从redis里面获取得到的token
      // log.info("execute 获取到的userId为:{}", userId);
      if (StringUtils.isBlank(userId)) {
        return false;
      }
      JwtToken jwtToken = new JwtToken(token1);
      //提交给realm进行登录
      getSubject(request, response).login(jwtToken);
      //如果没有抛出异常则代表登入成功
      return true;
    }
    
    if (StringUtils.isNotBlank(token2)) {
      JwtToken jwtToken = new JwtToken(token2);
      //提交给realm进行登录
      getSubject(request, response).login(jwtToken);
      //如果没有抛出异常则代表登入成功
      return true;
    }
    return false;
  }
  
  private void response401(ServletRequest req, ServletResponse resp, String msg) {
    try {
      HttpServletResponse response = WebUtils.toHttp(resp);
      String contentType = "application/json;charset=utf-8";
      response.setStatus(HttpStatus.UNAUTHORIZED.value());
      response.setContentType(contentType);
      ResponseDTO responseDTO = new ResponseDTO(401, "请重新登录");
      response.getWriter().append(JSONObject.toJSONString(responseDTO));
    } catch (IOException e) {
      log.error(e.getMessage());
    }
  }
}
