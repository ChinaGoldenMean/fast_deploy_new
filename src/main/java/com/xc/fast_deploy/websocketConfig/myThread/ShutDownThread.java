package com.xc.fast_deploy.websocketConfig.myThread;

import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static com.xc.fast_deploy.utils.PoolExcutorUtils.POOL_EXCUTOR_TIMEOUT;

public class ShutDownThread extends Thread {
  
  private ThreadPoolExecutor poolExecutor;
  
  public ShutDownThread(ThreadPoolExecutor poolExecutor) {
    this.poolExecutor = poolExecutor;
  }
  
  @Override
  public void run() {
    long start = System.currentTimeMillis();
    while (true) {
      try {
        Thread.sleep(2000);
        if (poolExecutor != null) {
          if (poolExecutor.getActiveCount() == 0 ||
              System.currentTimeMillis() - start >= POOL_EXCUTOR_TIMEOUT) {
            poolExecutor.shutdown();
            poolExecutor.awaitTermination(5, TimeUnit.SECONDS);
            if (!poolExecutor.isTerminated()) {
              poolExecutor.shutdownNow();
            }
            return;
          }
        } else {
          break;
        }
        
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
    super.run();
  }
}
