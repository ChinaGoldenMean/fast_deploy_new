package com.xc.fast_deploy.websocketConfig.myThread;

import lombok.extern.slf4j.Slf4j;
import org.yeauty.pojo.Session;

@Slf4j
public class TestThread extends Thread {
  private static final Integer TIME_OUT = 60 * 1000;
  private Session session;
  
  public TestThread(Session session) {
    this.session = session;
  }
  
  @Override
  public void run() {
    long start = System.currentTimeMillis();
    try {
      while (session.isRegistered()
          && !this.isInterrupted()
          && (System.currentTimeMillis() - start < TIME_OUT)) {
        Thread.sleep(1000);
        log.info("thread is running");
      }
    } catch (InterruptedException e) {
      e.printStackTrace();
    } finally {
      log.info("执行finally");
    }
    
    log.info("线程执行完毕OK!!!");
  }
}
