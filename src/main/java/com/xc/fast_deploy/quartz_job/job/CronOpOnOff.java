package com.xc.fast_deploy.quartz_job.job;

import com.xc.fast_deploy.service.slave.IBillingOpOnOffService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.quartz.QuartzJobBean;

@Slf4j
public class CronOpOnOff extends QuartzJobBean {
  
  @Autowired
  private IBillingOpOnOffService opOnOffService;
  @Value("${myself.pspass.prod}")
  private boolean isProd;
  
  /**
   * 定时打开或关闭发布开关    早8:00关  晚8:00开
   *
   * @param jobExecutionContext
   */
  @Override
  protected void executeInternal(JobExecutionContext jobExecutionContext) {
    if (isProd) {
      log.info("定时启停发布开关");
      opOnOffService.CronUpdateAble();
    }
  }
}
