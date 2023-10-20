package com.xc.fast_deploy.utils;

import org.apache.commons.lang.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class SessionCookieUtils {
  
  public static String getCookie(HttpServletRequest request, String cookieName) {
    Cookie[] cookies = request.getCookies();
    if (cookies != null && cookies.length > 0 && StringUtils.isNotBlank(cookieName)) {
      for (Cookie cookie : cookies) {
        if (cookieName.equals(cookie.getName())) {
          return cookie.getValue();
        }
      }
    }
    return null;
  }
  
  public static void clearCookieAll(HttpServletRequest request) {
    Cookie[] cookies = request.getCookies();
    if (cookies != null && cookies.length > 0) {
      for (Cookie cookie : cookies) {
        cookie.setMaxAge(0);
      }
    }
  }
  
  public static String getZdywSessionValue() {
    HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
    HttpSession session = request.getSession();
    if (session != null) {
      Object o = session.getAttribute(HttpUtils.ZDYW_COOKEY_KEY);
      if (o != null) {
        return (String) o;
      }
    }
    return null;
  }
  
  public static String getJsonParam(HttpServletRequest request) {
    BufferedReader streamReader = null;
    StringBuilder sb = new StringBuilder();
    try {
      streamReader = new BufferedReader(new InputStreamReader(request.getInputStream(), "UTF-8"));
      String line = null;
      while ((line = streamReader.readLine()) != null) {
        sb.append(line);
      }
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      if (streamReader != null) {
        try {
          streamReader.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
    return sb.toString();
  }
  
}
