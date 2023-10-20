package com.xc.fast_deploy.configuration;

/**
 * @Author litiewang
 * @Date 2022-09-08 15:49
 * @Version 1.0
 */

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class ApplicationContextProvider implements ApplicationContextAware {
  private static ApplicationContext context;
  
  public ApplicationContext getApplicationContext() {
    return context;
  }
  
  @Override
  public void setApplicationContext(ApplicationContext ctx) {
    context = ctx;
  }
}