package com.xc.fast_deploy.thread;

import cn.hutool.core.date.DateUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CountDownLatch;
import java.util.function.Function;

/**
 * 批量保存时任务执行
 *
 * @Author litiewang
 * @Date 2022/7/13 11:02
 * @Version 1.0
 */
@Slf4j
public class TaskThread<T> implements Runnable {
  private Function<T, Boolean> function;
  private T t;
  
  private final CountDownLatch countDownLatch;
  
  public TaskThread(Function<T, Boolean> function, T t, CountDownLatch countDownLatch) {
    this.function = function;
    this.t = t;
    this.countDownLatch = countDownLatch;
  }
  
  @Override
  public void run() {
    try {
      log.info("Spring自带的线程池" + Thread.currentThread().getName() + "-" + DateUtil.now());
      function.apply(t);
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      countDownLatch.countDown();
    }
  }
}
