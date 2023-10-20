package com.xc.fast_deploy.vo;

import com.xc.fast_deploy.utils.jenkins.JenkinsManage;
import lombok.Data;

@Data
public class JenkinsManageVO {
  
  public JenkinsManageVO(JenkinsManage jenkinsManage, String jobName, Integer buildNumber) {
    this.jenkinsManage = jenkinsManage;
    this.jobName = jobName;
    this.buildNumber = buildNumber;
  }
  
  private JenkinsManage jenkinsManage;
  
  private String jobName;
  
  private Integer buildNumber;
}
