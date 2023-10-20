package com.xc.fast_deploy.quartz_job;

import com.xc.fast_deploy.quartz_job.job.*;
import org.quartz.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QuartzConfiguration {
  
  /**
   * 定时任务执行 清除在harbor仓库中已经删除的镜像
   *
   * @return
   */
  @Bean
  public JobDetail myJobDetail() {
    return JobBuilder.newJob(MyJob.class).withIdentity("myJob").storeDurably().build();
  }
  
  /**
   * 比对线上yaml与数据库保存的yaml是否一致
   *
   * @return
   */
  @Bean
  public JobDetail compareYamlDetail() {
    return JobBuilder.newJob(CompareYaml.class).withIdentity("CompareYaml").storeDurably().build();
  }
  
  /**
   * 发布功能定时启停
   *
   * @return
   */
  @Bean
  public JobDetail CronOpOnOffDetail() {
    return JobBuilder.newJob(CronOpOnOff.class).withIdentity("CronOpOnOff").storeDurably().build();
  }
  
  /**
   * 定时清理生产发布清单及将需求更改为已完成状态
   *
   * @return
   */
  @Bean
  public JobDetail ClearDeployListDetail() {
    return JobBuilder.newJob(ClearDeployList.class).withIdentity("ClearDeployList").storeDurably().build();
  }
  
  @Bean
  public JobDetail JoinDeployListDetail() {
    return JobBuilder.newJob(JoinDeployList.class).withIdentity("JoinDeployList").storeDurably().build();
  }
  
  /**
   * 定时任务配置
   *
   * @return
   */
  @Bean
  public Trigger myJobTrigger() {
    CronScheduleBuilder cronSchedule = CronScheduleBuilder.cronSchedule("0 0 3,15 * * ? *");
//        CronScheduleBuilder cronSchedule = CronScheduleBuilder.cronSchedule("0/30 * * * * ? ");
    return TriggerBuilder
        .newTrigger()
        .forJob(myJobDetail())
        .withSchedule(cronSchedule)
        .withIdentity("myjobtrigger")
        .build();
    
  }
  
  @Bean
  public Trigger compareYamlTrigger() {
    CronScheduleBuilder cronSchedule = CronScheduleBuilder.cronSchedule("0 0 12 ? * 1");
    return TriggerBuilder
        .newTrigger()
        .forJob(compareYamlDetail())
        .withSchedule(cronSchedule)
        .withIdentity("compareYamlTrigger")
        .build();
  }
  
  @Bean
  public Trigger CronOpOnOffTrigger() {
    CronScheduleBuilder cronSchedule = CronScheduleBuilder.cronSchedule("0 0 8,20 ? * 2-6");
    return TriggerBuilder
        .newTrigger()
        .forJob(CronOpOnOffDetail())
        .withSchedule(cronSchedule)
        .withIdentity("CronOpOnOffTrigger")
        .build();
  }
  
  @Bean
  public Trigger ClearDeployListTrigger() {
    CronScheduleBuilder cronSchedule = CronScheduleBuilder.cronSchedule("0 0 8 * * ?");
    return TriggerBuilder
        .newTrigger()
        .forJob(ClearDeployListDetail())
        .withSchedule(cronSchedule)
        .withIdentity("ClearDeployListTrigger")
        .build();
  }
  
  @Bean
  public Trigger JoinDeployListTrigger() {
    CronScheduleBuilder cronSchedule = CronScheduleBuilder.cronSchedule("0 0 20 * * ?");
    return TriggerBuilder
        .newTrigger()
        .forJob(JoinDeployListDetail())
        .withSchedule(cronSchedule)
        .withIdentity("JoinDeployListTrigger")
        .build();
  }
}
