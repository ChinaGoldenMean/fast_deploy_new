package com.xc.fast_deploy.dao.master_dao;

import com.xc.fast_deploy.model.master_model.PModulePermission;
import com.xc.fast_deploy.vo.module_vo.permission.ModuleRoleBasePermissionVo;
import com.xc.fast_deploy.vo.module_vo.permission.ModuleRolePermissionParamVo;

import java.util.List;
import java.util.Set;

public interface PModulePermissionMapper {
  int deleteByPrimaryKey(Integer id);
  
  int insert(PModulePermission record);
  
  int insertSelective(PModulePermission record);
  
  PModulePermission selectByPrimaryKey(Integer id);
  
  int updateByPrimaryKeySelective(PModulePermission record);
  
  int updateByPrimaryKey(PModulePermission record);
  
  Set<Integer> selectIds(Set<Integer> ids);
  
  int insertRoleBasePermissionBatch(List<ModuleRoleBasePermissionVo> rolePermission);
  
  void deleteByIds(Set<Integer> pemissionIdSet);
  
  List<PModulePermission> selectPermissionVoPageByVo(ModuleRolePermissionParamVo permissionParamVo);
  
  void insertBatch(List<PModulePermission> permissionList);
  
  List<PModulePermission> selectAll();
}