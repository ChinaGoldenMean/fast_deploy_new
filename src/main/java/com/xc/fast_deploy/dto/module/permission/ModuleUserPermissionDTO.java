package com.xc.fast_deploy.dto.module.permission;

import lombok.Data;

import java.util.Set;

@Data
public class ModuleUserPermissionDTO {
  
  private Integer envId;
  private String envName;
  private Set<String> permissionSet;
  private Set<Integer> centerIds;
  
}
