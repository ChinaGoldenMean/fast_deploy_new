package com.xc.fast_deploy.service.permission;

import com.xc.fast_deploy.dto.MyPageInfo;
import com.xc.fast_deploy.model.master_model.PModulePermission;
import com.xc.fast_deploy.vo.module_vo.permission.ModuleRolePermissionParamVo;

public interface PModulePermissionService {
  
  //添加权限的接口操作
  boolean addPermission(PModulePermission permission);
  
  //修改权限的接口操作
  boolean updatePermission(PModulePermission permission);
  
  boolean deleteById(Integer permissionId);
  
  boolean deleteByIds(Integer[] permissionIds);
  
  MyPageInfo<PModulePermission> selectPageAllInfo(Integer pageNum, Integer pageSize,
                                                  ModuleRolePermissionParamVo permissionParamVo);
}
