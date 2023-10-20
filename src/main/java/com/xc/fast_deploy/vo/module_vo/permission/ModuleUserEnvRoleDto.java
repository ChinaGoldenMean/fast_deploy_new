package com.xc.fast_deploy.vo.module_vo.permission;

import lombok.Data;

import java.io.Serializable;

@Data
public class ModuleUserEnvRoleDto implements Serializable {
  
  private Integer userRoleBindId;
  private String username;
  private String roleName;
  private Integer userId;
  private Integer roleId;
  private String envId;
  private String envName;
  private String cname;
  private Integer centerId;
  private String centerNames;
}

