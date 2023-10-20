package com.xc.fast_deploy.vo;

import lombok.Data;
import org.yeauty.pojo.Session;

@Data
public class RunJobDataVo {
  
  private Integer jobId;
  private String token;
  private Session session;
  private Boolean isUpdateAllCode;
  private JenkinsManageVO jenkinsManageVO;
  private Boolean isNeedUpCode;
  private String needIdStr;
  private Boolean isPromptly;
  private Boolean isOffline;
}
