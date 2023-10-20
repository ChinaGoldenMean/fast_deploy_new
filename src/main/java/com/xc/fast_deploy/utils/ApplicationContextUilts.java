package com.xc.fast_deploy.utils;

import com.xc.fast_deploy.myException.ServiceException;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;

/**
 * 因为规则引擎内部的计算链以及其下的各个计算单元，参数处理器不是直接实例化交给Spring管理。
 * 所以如果内部需要使用到Bean时，请使用这个工具来手动得到Spring的某个Bean。
 *
 * @author zhouch
 * @date 2021/12/13 11:34
 */
@Slf4j
public final class ApplicationContextUilts {
  
  @Setter
  static ApplicationContext context;
  
  public static Object getBean(@NonNull String name) {
    if (context == null) {
      throw new ServiceException("context 未初始化......");
    }
    log.info("从ApplicationContext手动获得Bean: {}", name);
    return context.getBean(name);
  }
  
  public static <T> T getBean(@NonNull String name, @NonNull Class<T> clazz) {
    if (context == null) {
      throw new ServiceException("context未初始化......");
    }
    log.info("从ApplicationContext手动获得Bean: {}, Class: {}", name, clazz.getName());
    return context.getBean(name, clazz);
  }
  
  public static <T> T getBean(@NonNull Class<T> clazz) {
    if (context == null) {
      throw new ServiceException("context未初始化......");
    }
    log.info("从ApplicationContext手动获得Class: {}", clazz.getName());
    return context.getBean(clazz);
  }
}
