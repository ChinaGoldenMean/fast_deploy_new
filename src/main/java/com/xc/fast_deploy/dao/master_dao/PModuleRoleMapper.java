package com.xc.fast_deploy.dao.master_dao;

import com.xc.fast_deploy.model.master_model.PModuleBasePermission;
import com.xc.fast_deploy.model.master_model.PModulePermission;
import com.xc.fast_deploy.model.master_model.PModuleRole;

import java.util.List;
import java.util.Set;

public interface PModuleRoleMapper {
  int deleteByPrimaryKey(Integer id);
  
  int insert(PModuleRole record);
  
  int insertSelective(PModuleRole record);
  
  PModuleRole selectByPrimaryKey(Integer id);
  
  int updateByPrimaryKeySelective(PModuleRole record);
  
  int updateByPrimaryKey(PModuleRole record);
  
  //根据权限id删除角色关联权限里的关联数据
//    void deleteRolePermissionByPermissionId(Integer permissionId);
  
  //根据角色id删除对应的角色权限关联的数据
//    void deleteRolePermissionByRoleId(Integer roleId);
  
  List<PModuleRole> selectAll();
  
  List<PModulePermission> selectPermissionByRoleId(Integer roleId);
  
  void deleteRoleBasePermByRoleId(Integer roleId);
  
  List<PModuleBasePermission> selectRoleBasePermByRoleId(Integer roleId);
  
  void deleteRoleBasePermByBaseId(Integer basePermissionId);
}