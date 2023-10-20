package com.xc.fast_deploy.dto.jenkins;

import lombok.Data;

import java.io.Serializable;

@Data
public class JenkinsJobDTO implements Serializable {
  
  private static final long serialVersionUID = -5306049325558205044L;
  
  private String jobName;
  
  private String jobUrl;
  
  private Integer nextBuildNumber;
  
  private String description;
  
  private String displayName;
  
  private boolean inQueue;
  
}
