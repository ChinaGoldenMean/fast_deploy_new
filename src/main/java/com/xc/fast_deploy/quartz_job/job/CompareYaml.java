package com.xc.fast_deploy.quartz_job.job;

import com.xc.fast_deploy.model.master_model.ModuleDeployYaml;
import com.xc.fast_deploy.service.common.ModuleDeployYamlService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;

@Slf4j
public class CompareYaml extends QuartzJobBean {
  
  @Autowired
  private ModuleDeployYamlService deployYamlService;
  
  /**
   * 定时比对线上deployment配置是否与模块yaml文件一致
   *
   * @param jobExecutionContext
   */
  @Override
  protected void executeInternal(JobExecutionContext jobExecutionContext) {
    log.info("定时任务,比对线上yaml与yaml文件是否一致");
    deployYamlService.compareProdYaml();
  }
}
