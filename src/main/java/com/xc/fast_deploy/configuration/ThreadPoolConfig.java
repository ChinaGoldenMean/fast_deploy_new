package com.xc.fast_deploy.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import javax.annotation.PostConstruct;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @Author litiewang
 * @Date 2022/7/13 9:12
 * @Version 1.0
 */
@EnableAsync
@Configuration
@Slf4j
public class ThreadPoolConfig {
  
  @Bean
  public ThreadPoolTaskExecutor asyncExecutor() {
    
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    int i = Runtime.getRuntime().availableProcessors();
    //核心线程数目
    executor.setCorePoolSize(i * 2);
    //指定最大线程数
    executor.setMaxPoolSize(i * 2);
    //队列中最大的数目
    executor.setQueueCapacity(i * 2 * 10);
    
    //线程名称前缀
    executor.setThreadNamePrefix("ThreadPoolTaskExecutor-");
    //rejection-policy：当pool已经达到max size的时候，如何处理新任务
    //CALLER_RUNS：不在新线程中执行任务，而是由调用者所在的线程来执行
    //对拒绝task的处理策略
    executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
    //当调度器shutdown被调用时等待当前被调度的任务完成
    executor.setWaitForTasksToCompleteOnShutdown(true);
    //线程空闲后的最大存活时间
    executor.setKeepAliveSeconds(60);
    //加载
    executor.initialize();
    log.info("初始化线程池成功");
    return executor;
  }
  
}
