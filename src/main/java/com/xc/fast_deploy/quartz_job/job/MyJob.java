package com.xc.fast_deploy.quartz_job.job;

import com.xc.fast_deploy.service.common.ModuleMirrorService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;

@Slf4j
public class MyJob extends QuartzJobBean {
  
  @Autowired
  private ModuleMirrorService moduleMirrorService;
  
  /**
   * 定时任务执行 清除在harbor仓库中已经
   *
   * @param jobExecutionContext
   */
  @Override
  protected void executeInternal(JobExecutionContext jobExecutionContext) {
    //主要是为了同步数据库和真实harbor仓库中的镜像
    log.info("定时任务执行!");
    moduleMirrorService.clearNotInHaroborMirrorInfo();
  }
  
}
