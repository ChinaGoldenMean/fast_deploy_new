package com.xc.fast_deploy.utils;

import com.xc.fast_deploy.configuration.MyThreadFactory;
import com.xc.fast_deploy.vo.RunJobDataVo;
import com.xc.fast_deploy.websocketConfig.myThread.RunJobOutputThread;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.*;

import static com.xc.fast_deploy.utils.HttpUtils.QUEUE_MAX_SIZE;

@Slf4j
public class PoolExcutorUtils2 {
  
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
  public static final List<Future<?>> FUTURE_LIST = new CopyOnWriteArrayList<>();
  public static ThreadPoolExecutor jobPoolExecutor = new ThreadPoolExecutor(QUEUE_MAX_SIZE,
      BLOCK_POOL_SIZE, POOL_EXCUTOR_TIMEOUT_2,
      TimeUnit.SECONDS, new ArrayBlockingQueue<>(BLOCK_POOL_SIZE),
      new MyThreadFactory(EXECUTE_RUN_JOB_POOL),
      new ThreadPoolExecutor.DiscardOldestPolicy());
  
  private static ThreadPoolTaskExecutor threadPoolTaskExecutor = null;
  
  static {
    if (threadPoolTaskExecutor == null) {
      threadPoolTaskExecutor = SpringUtil.getBean("threadPoolTaskExecutor", ThreadPoolTaskExecutor.class);
    }
    //消费队列中的数据的启动runjob的操作
    new Thread(() -> {
      while (true) {
        try {
          RunJobDataVo runJobDataVo = linkedQueue.poll(500L, TimeUnit.MILLISECONDS);
          if (runJobDataVo != null) {
            PoolExcutorUtils2.jobHashMap.put(runJobDataVo.getJobId(),
                System.currentTimeMillis());
            while (jobHashMap.size() > QUEUE_MAX_SIZE) {
              Thread.sleep(2000L);
            }
            ExecutorService threadExecutor = Executors.newSingleThreadExecutor();
            threadPoolMap.put(System.currentTimeMillis(), threadExecutor);
            Future<?> result = threadPoolTaskExecutor.submit(new RunJobOutputThread(runJobDataVo));
            FUTURE_LIST.add(result);
            
          }
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    }).start();
    //获取结果
    new Thread(() -> {
      while (true) {
        //每隔30s检查一次
        if (FUTURE_LIST.size() > 0) {
          for (Future<?> result : FUTURE_LIST) {
            try {
              result.get(20, TimeUnit.MINUTES);
            } catch (InterruptedException e) {
              e.printStackTrace();
            } catch (ExecutionException e) {
              e.printStackTrace();
            } catch (TimeoutException e) {
              e.printStackTrace();
            }
          }
        }
      }
    }).start();
    
  }
  
}
