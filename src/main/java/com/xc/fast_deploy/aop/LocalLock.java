package com.xc.fast_deploy.aop;

import java.lang.annotation.*;

/**
 * @Author litiewang
 * @Date 2022-09-07 14:21
 * @Version 1.0
 */

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface LocalLock {

}