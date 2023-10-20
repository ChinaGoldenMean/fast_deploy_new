package com.xc.fast_deploy.thread;

import com.xc.fast_deploy.utils.SpringUtil;
import com.xc.fast_deploy.websocketConfig.myThread.UpdateCodeThread;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.function.Function;

/**
 * @Author litiewang
 * @Date 2022/7/13 16:41
 * @Version 1.0
 */
public class TaskExecutor<T> {
  
  private final CountDownLatch downLatch;
  private static ThreadPoolTaskExecutor threadPoolTaskExecutor = null;
  
  static {
    synchronized (TaskExecutor.class) {
      if (threadPoolTaskExecutor == null) {
        threadPoolTaskExecutor = SpringUtil.getBean(ThreadPoolTaskExecutor.class);
      }
    }
    
  }
  
  public TaskExecutor(int count) {
    downLatch = new CountDownLatch(count);
  }
  
  public void execute(List<T> list, Function<T, Boolean> function) {
    if (CollectionUtils.isEmpty(list)) {
      return;
    }
    //
    list.stream().forEach(t -> {
      execute(function, t);
    });
  }
  
  public <T, V> List<Future<V>> submit(List<T> paramList, Function<T, V> function) {
    if (CollectionUtils.isEmpty(paramList)) {
      return null;
    }
    //
    List<Future<V>> futures = new CopyOnWriteArrayList<>();
    paramList.stream().forEach(t -> {
      futures.add(submit(function, t));
    });
    return futures;
  }
  
  /**
   * 添加执行任务
   *
   * @param function 需要执行的方法
   * @param t        方法参数
   */
  public void execute(Function<T, Boolean> function, T t) {
    threadPoolTaskExecutor.execute(new TaskThread<>(function, t, downLatch));
  }
  
  public <T, V> Future<V> submit(Function<T, V> function, T t) {
    return threadPoolTaskExecutor.submit(new TaskCallable<>(function, t, downLatch));
  }
  
  /**
   * 等待所有线程执行完成
   */
  public void await() {
    try {
      downLatch.await();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
}
