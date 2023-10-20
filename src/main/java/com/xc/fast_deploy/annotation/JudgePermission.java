package com.xc.fast_deploy.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface JudgePermission {
  
  int envIdIndex() default -1;
  
  String permissionName() default "";
  
}
