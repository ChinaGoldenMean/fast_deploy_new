package com.xc.fast_deploy.service.permission;

import com.xc.fast_deploy.dto.MyPageInfo;
import com.xc.fast_deploy.dto.ResponseDTO;
import com.xc.fast_deploy.dto.module.permission.ModuleUserPermissionDTO;
import com.xc.fast_deploy.model.master_model.ModuleEnv;
import com.xc.fast_deploy.model.master_model.PModulePermission;
import com.xc.fast_deploy.model.master_model.PModuleUser;
import com.xc.fast_deploy.vo.module_vo.param.PModuleUserSelectParamVo;
import com.xc.fast_deploy.vo.module_vo.permission.ModuleUserEnvRoleDto;
import com.xc.fast_deploy.vo.module_vo.permission.ModuleUserEnvRoleParamVo;

import java.util.List;

public interface PModuleUserService {
  
  ResponseDTO addUser(PModuleUser moduleUser);
  
  List<ModuleUserPermissionDTO> getAllPermissionsByUserName(String username);
  
  List<ModuleUserPermissionDTO> getAllPermissionsByUserId(String userId);
  
  ResponseDTO addUserBindRole(Integer userId, Integer roleId, Integer[] envIds, Integer[] centerIds);
  
  ResponseDTO batchAddUserRoleBind(String userNames, Integer roleId,
                                   Integer envId, String centerNames);
  
  PModuleUser selectUserByName(String username);
  
  public int updateByPrimaryKeySelective(PModuleUser moduleUser);
  
  ResponseDTO updateUserRoleBind(Integer userRoleBindId, Integer userId, Integer roleId, Integer[] envIds, Integer[] centerIds);
  
  MyPageInfo<ModuleUserEnvRoleDto> selectUserRoleBindPageAllInfo(Integer pageNum, Integer pageSize,
                                                                 ModuleUserEnvRoleParamVo userEnvRoleParamVo);
  
  boolean deleteUserInfoByUserId(Integer userId);
  
  MyPageInfo<PModuleUser> selectPageAllInfo(Integer pageNum, Integer pageSize,
                                            PModuleUserSelectParamVo moduleUserSelectParamVo);
  
  ResponseDTO updateUserInfo(PModuleUser pModuleUser);
  
  PModuleUser selectUserInfoById(Integer id);
  
  ResponseDTO changePass(String userId, String pass);
  
  boolean deleteUserRoleBind(Integer[] userRoleBindIds);
  
}
