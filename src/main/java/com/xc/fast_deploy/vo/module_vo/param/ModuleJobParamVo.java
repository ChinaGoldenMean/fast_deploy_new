package com.xc.fast_deploy.vo.module_vo.param;

import lombok.Data;

import java.io.Serializable;

@Data
public class ModuleJobParamVo implements Serializable {
  
  private static final long serialVersionUID = -3565854109945016179L;
  
  private Integer jobId;
  
  private String jobName;
  
  private String crontabExpression;
  
  private Integer centerId;
  
  private Integer moduleId;
  
  private Integer envId;
  
  private Integer compileType;
  
  private String compileCommand;
  
  private String compileFilePath;
  
  private Integer dockerfileType;
  
  private String dockerfilePath;
  
}
