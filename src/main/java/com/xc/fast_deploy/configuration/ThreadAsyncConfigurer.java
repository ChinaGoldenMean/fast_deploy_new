//package com.xc.fast_deploy.configuration;
//
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.scheduling.annotation.AsyncConfigurer;
//import org.springframework.scheduling.annotation.EnableAsync;
//import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
//
//import java.util.concurrent.Executor;
//import java.util.concurrent.ThreadPoolExecutor;
//
//@Configuration
//@EnableAsync
//@Slf4j
//public class ThreadAsyncConfigurer implements AsyncConfigurer {
//
//    @Bean(name = "asyncExecutor")
//    public ThreadPoolTaskExecutor asyncExecutor() {
//        ThreadPoolTaskExecutor threadPool = new ThreadPoolTaskExecutor();
//        //设置核心线程数
//        threadPool.setCorePoolSize(20);
//        //设置最大线程数
//        threadPool.setMaxPoolSize(100);
//        //线程池所使用的缓冲队列
//        threadPool.setQueueCapacity(10);
//        //等待任务在关机时完成--表明等待所有线程执行完
//        threadPool.setWaitForTasksToCompleteOnShutdown(true);
//        //等待时间 （默认为0，此时立即停止），并没等待xx秒后强制停止
//        threadPool.setAwaitTerminationSeconds(60);
//        //线程名称前缀
//        threadPool.setThreadNamePrefix("MyAsync-");
//
//        threadPool.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
//        // 初始化线程
//        threadPool.initialize();
//        return threadPool;
//    }
//
//    @Override
//    public Executor getAsyncExecutor() {
//        return asyncExecutor();
//    }
//
//    @Override
//    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
//        log.info("异步任务执行出现异常");
//        return null;
//    }
//
//}
