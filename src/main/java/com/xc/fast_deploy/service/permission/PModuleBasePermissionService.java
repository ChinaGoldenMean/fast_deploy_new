package com.xc.fast_deploy.service.permission;

import com.xc.fast_deploy.dto.MyPageInfo;
import com.xc.fast_deploy.dto.ResponseDTO;
import com.xc.fast_deploy.model.master_model.PModuleBasePermission;
import com.xc.fast_deploy.model.master_model.PModulePermission;

import java.util.List;

public interface PModuleBasePermissionService {
  MyPageInfo<PModuleBasePermission> selectAll(Integer pageNum, Integer pageSize, String keyName);
  
  Integer insertOne(PModuleBasePermission basePermission);
  
  void updateOne(PModuleBasePermission basePermission);
  
  PModuleBasePermission selectById(Integer basePermissionId);
  
  List<PModulePermission> selectPermissionList(Integer basePermissionId);
  
  ResponseDTO bindBasePerm(Integer basePermissionId, Integer[] permIds);
  
  void delBasePerm(Integer basePermissionId);
}
