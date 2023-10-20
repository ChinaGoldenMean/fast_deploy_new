package com.xc.fast_deploy.service.permission;

import com.xc.fast_deploy.dto.ResponseDTO;
import com.xc.fast_deploy.model.master_model.PModuleBasePermission;
import com.xc.fast_deploy.model.master_model.PModulePermission;
import com.xc.fast_deploy.model.master_model.PModuleRole;

import java.util.List;

public interface PModuleRoleService {
  boolean addRole(PModuleRole role);
  
  //添加角色关联权限内容
  ResponseDTO addRoleBindBasePermission(Integer roleId, Integer[] basePermissionIds);
  
  ResponseDTO deleteRoleData(Integer roleId);
  
  boolean updateRole(PModuleRole role);
  
  //修改角色关联权限数据的绑定
  ResponseDTO updateRoleBindPermission(Integer roleId, Integer[] basePermissionIds);
  
  //查询所有角色信息
  List<PModuleRole> selectAllRole();
  
  List<PModulePermission> getAllRolePermissionByRoleId(Integer roleId);
  
  List<PModuleBasePermission> getAllRoleBasePermByRoleId(Integer roleId);
}
