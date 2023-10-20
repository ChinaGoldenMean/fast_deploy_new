package com.xc.fast_deploy.vo.module_vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class DeployModuleVo implements Serializable {
  private static final long serialVersionUID = -7174606131491854074L;
  
  private Integer deployId;
  
  private Integer deployYamlId;
  
  private String deployAppName;
  
  private Integer moduleId;
  
  private Integer envId;
  
  private Integer yamlFileType;
  
  private Integer svcYamlFileType;
  
}
