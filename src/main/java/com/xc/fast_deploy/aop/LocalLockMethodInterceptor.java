package com.xc.fast_deploy.aop;

import cn.hutool.cache.CacheUtil;
import cn.hutool.cache.impl.TimedCache;
import cn.hutool.crypto.SecureUtil;
import com.alibaba.fastjson.JSONObject;
import com.xc.fast_deploy.dto.ResponseDTO;
import com.xc.fast_deploy.utils.IPUtils;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author litiewang
 * @Date 2022-09-07 14:21
 * @Version 1.0
 */

@Data
@Aspect
@Configuration
public class LocalLockMethodInterceptor {
  
  @Value("${spring.profiles.active}")
  private String springProfilesActive;
  @Value("${spring.application.name}")
  private String springApplicationName;
  
  //@Value("${resubmit.local.timeOut}")
  //private int expireTimeSecond;
  //
  //定义缓存，设置最大缓存数及过期日期
  private TimedCache<String, String> fifoCache = CacheUtil.newTimedCache(180000L);
  
  @Around("@annotation(com.xc.fast_deploy.aop.LocalLock)")
  public Object interceptor(ProceedingJoinPoint joinPoint) {
    
    MethodSignature signature = (MethodSignature) joinPoint.getSignature();
    Method method = signature.getMethod();
    String key = null;
    try {
      key = getLockUniqueKey(signature, joinPoint.getArgs());
      if (fifoCache.get(key) != null) {
        ResponseDTO responseDTO = new ResponseDTO();
        return responseDTO.fail("3分钟内 不允许重复查询，请稍后再试");
      }
      fifoCache.put(key, key);
      return joinPoint.proceed();
    } catch (Throwable throwable) {
      //有异常，则删除
      fifoCache.remove(key);
   //  throw new ServiceException(throwable.getMessage());
    }
    return null;
  }
  
  /**
   * 获取唯一标识key
   *
   * @param methodSignature
   * @param args
   * @return
   */
  private String getLockUniqueKey(MethodSignature methodSignature, Object[] args) {
    //请求uri, 获取类名称，方法名称
    RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
    ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) requestAttributes;
    HttpServletRequest request = servletRequestAttributes.getRequest();
   
    String userMsg = null; //获取登录用户名称
    //1.判断用户是否登录
      userMsg = IPUtils.getIpAddr();
    String hash = "";
    List list = new ArrayList();
    if (args.length > 0) {
      String[] parameterNames = methodSignature.getParameterNames();
      for (int i = 0; i < parameterNames.length; i++) {
        Object obj = args[i];
        list.add(obj);
      }
      hash = JSONObject.toJSONString(list);
      
    }
    //项目名称 + 环境编码 + 获取类名称 + 方法名称 + 唯一key
    String key = "locallock:" + springApplicationName + ":" + springProfilesActive + ":" + userMsg + ":" + request.getRequestURI();
    if (StringUtils.isNotEmpty(key)) {
      key = key + ":" + hash;
    }
    key = SecureUtil.md5(key);
    return key;
  }
  
}


