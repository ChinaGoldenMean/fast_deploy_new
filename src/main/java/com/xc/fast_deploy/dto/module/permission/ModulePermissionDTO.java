package com.xc.fast_deploy.dto.module.permission;

import lombok.Data;

@Data
public class ModulePermissionDTO {
  
  private String userId;
  private String envName;
  private String action;
  private String roleName;
  private Integer isProd;
  private String actionName;
  private Integer envId;
  private String username;
  private String centerName;
  private Integer centerId;
}
