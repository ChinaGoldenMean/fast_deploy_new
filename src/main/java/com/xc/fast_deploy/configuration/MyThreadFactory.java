package com.xc.fast_deploy.configuration;

import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class MyThreadFactory implements ThreadFactory {
  
  private final ThreadGroup group;
  private final AtomicInteger threadNumber = new AtomicInteger(1);
  private final String namePrefix;
  
  public MyThreadFactory(String name) {
    SecurityManager s = System.getSecurityManager();
    group = (s != null) ? s.getThreadGroup() :
        Thread.currentThread().getThreadGroup();
    namePrefix = name + "-pool-thread-";
  }
  
  public Thread newThread(Runnable r) {
    Thread t = new Thread(group, r,
        namePrefix + threadNumber.getAndIncrement(),
        0);
    if (t.isDaemon())
      t.setDaemon(false);
    if (t.getPriority() != Thread.NORM_PRIORITY)
      t.setPriority(Thread.NORM_PRIORITY);
    return t;
  }
}
