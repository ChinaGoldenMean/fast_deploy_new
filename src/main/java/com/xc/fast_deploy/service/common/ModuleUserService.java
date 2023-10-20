package com.xc.fast_deploy.service.common;

import com.xc.fast_deploy.dto.module.permission.ModuleUserPermissionDTO;
import com.xc.fast_deploy.model.master_model.ModuleEnv;
import com.xc.fast_deploy.model.master_model.ModuleUser;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface ModuleUserService extends BaseService<ModuleUser, Integer> {
  
  Map<Integer, Set<String>> selectEnvPermissionByUserId(String userId);
  
  Set<String> getAllPermission(String userId);

//    List<ModuleUserPermissionDTO> getUserEnvPermission(String userId);

//    List<ModuleEnv> selectPsPermissionByUserId(String userId);
  
  void deletePermissionByUserId(String userId);
  
  Map<Integer, Set<String>> selectEnvPermissionByUserName(String userNameFromToken);
}
