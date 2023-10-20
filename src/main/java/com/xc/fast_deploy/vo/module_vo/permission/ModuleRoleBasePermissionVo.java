package com.xc.fast_deploy.vo.module_vo.permission;

import lombok.Data;

@Data
public class ModuleRoleBasePermissionVo {
  
  private Integer id;
  private Integer roleId;
  private Integer basePermId;
  
  public ModuleRoleBasePermissionVo(Integer roleId, Integer basePermId) {
    this.roleId = roleId;
    this.basePermId = basePermId;
  }
  
  public ModuleRoleBasePermissionVo() {
  }
}
