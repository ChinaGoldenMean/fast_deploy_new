package com.xc.fast_deploy.utils;

import com.xc.fast_deploy.configuration.MyThreadFactory;
import com.xc.fast_deploy.vo.RunJobDataVo;
import com.xc.fast_deploy.websocketConfig.myThread.RunJobOutputThread;
import com.xc.fast_deploy.websocketConfig.myThread.ShutDownThread;
import lombok.extern.slf4j.Slf4j;

import java.util.Enumeration;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static com.xc.fast_deploy.utils.HttpUtils.QUEUE_MAX_SIZE;

@Slf4j
public class PoolExcutorUtils {
  
  //15分钟的超时时间设置
  public static final long POOL_EXCUTOR_TIMEOUT_2 = 15 * 60;
  
  //5分钟的等待线程池执行完的时间
  public static final Long POOL_EXCUTOR_TIMEOUT = 5 * 60 * 1000L;
  //    private static final Integer EXECUTE_POOL_SIZE = 8;
  private static final Integer BLOCK_POOL_SIZE = 300;
  private static final String EXECUTE_RUN_JOB_POOL = "run-job";
  public static final LinkedBlockingQueue<RunJobDataVo> linkedQueue =
      new LinkedBlockingQueue<>();
  public static final ConcurrentHashMap<Integer, Long> jobHashMap = new ConcurrentHashMap<>();
  public static final ConcurrentHashMap<Long, ExecutorService> threadPoolMap =
      new ConcurrentHashMap<>();
  
  public static ThreadPoolExecutor jobPoolExecutor = new ThreadPoolExecutor(QUEUE_MAX_SIZE,
      BLOCK_POOL_SIZE, POOL_EXCUTOR_TIMEOUT_2,
      TimeUnit.SECONDS, new ArrayBlockingQueue<>(BLOCK_POOL_SIZE),
      new MyThreadFactory(EXECUTE_RUN_JOB_POOL),
      new ThreadPoolExecutor.DiscardOldestPolicy());
  
  static {
    //清除线程池的监听线程启动
    new Thread(() -> {
      while (true) {
        //每隔30s检查一次
        try {
          Thread.sleep(30 * 1000);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
        Enumeration<Integer> keys = jobHashMap.keys();
        while (keys.hasMoreElements()) {
          Integer jobId = keys.nextElement();
          Long addTime = jobHashMap.get(jobId);
          //超出20分钟没有结束的job自动清除
          if (System.currentTimeMillis() - addTime > 20 * 60 * 1000L) {
            log.info("清除 job-id: {}", jobId);
            jobHashMap.remove(jobId);
          }
        }
        
        Enumeration<Long> longEnumeration = threadPoolMap.keys();
        while (longEnumeration.hasMoreElements()) {
          Long startTimes = longEnumeration.nextElement();
          //超出20分钟没有结束的线程池自动清除
          if (System.currentTimeMillis() - startTimes > 20 * 60 * 1000L) {
            ExecutorService executorService = threadPoolMap.get(startTimes);
            if (executorService != null) {
              log.info("清除 thread pool: {}", executorService);
              executorService.shutdown();
              try {
                Thread.sleep(3000);
                executorService.shutdownNow();
              } catch (InterruptedException e) {
                e.printStackTrace();
              }
            }
            threadPoolMap.remove(startTimes);
          }
        }
      }
    }).start();
    
    //消费队列中的数据的启动runjob的操作
    new Thread(() -> {
      while (true) {
        try {
          RunJobDataVo runJobDataVo = linkedQueue.poll(500L, TimeUnit.MILLISECONDS);
          if (runJobDataVo != null) {
            PoolExcutorUtils.jobHashMap.put(runJobDataVo.getJobId(),
                System.currentTimeMillis());
            while (jobHashMap.size() > QUEUE_MAX_SIZE) {
              Thread.sleep(2000L);
            }
            ExecutorService threadExecutor = Executors.newSingleThreadExecutor();
            threadPoolMap.put(System.currentTimeMillis(), threadExecutor);
            threadExecutor.execute(new RunJobOutputThread(runJobDataVo));
          }
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    }).start();
    
  }
  
}
