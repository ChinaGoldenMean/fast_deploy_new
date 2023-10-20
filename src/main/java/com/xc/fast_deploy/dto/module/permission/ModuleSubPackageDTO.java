package com.xc.fast_deploy.dto.module.permission;

import lombok.Data;

/**
 * @Author litiewang
 * @Date 2023-05-08 18:39
 * @Version 1.0
 */
@Data
public class ModuleSubPackageDTO {
  
  private String gitBranch;
  private String programFileName;
  private Integer moduleId;
  
  private String contentName;
  
  private String codeUrl;
  Integer moduleType;
}
