package com.xc.fast_deploy.thread;

import cn.hutool.core.date.DateUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.function.Function;

/**
 * @Author litiewang
 * @Date 2022-09-07 10:15
 * @Version 1.0
 * 有返回结果的线程类
 */

@Slf4j
public class TaskCallable<T, V> implements Callable<V> {
  private Function<T, V> function;
  private T t;
  private CountDownLatch countDownLatch;
  
  public TaskCallable(Function<T, V> function, T t, CountDownLatch countDownLatch) {
    this.function = function;
    this.t = t;
    this.countDownLatch = countDownLatch;
  }
  
  @Override
  public V call() {
    V v = null;
    try {
      v = function.apply(t);
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      countDownLatch.countDown();
    }
    return v;
  }
}
