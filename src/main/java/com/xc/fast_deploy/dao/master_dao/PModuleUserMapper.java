package com.xc.fast_deploy.dao.master_dao;

import com.xc.fast_deploy.dto.module.permission.ModulePermissionDTO;
import com.xc.fast_deploy.model.master_model.PModuleUser;
import com.xc.fast_deploy.vo.module_vo.param.PModuleUserSelectParamVo;
import com.xc.fast_deploy.vo.module_vo.permission.ModuleUserEnvRoleDto;
import com.xc.fast_deploy.vo.module_vo.permission.ModuleUserEnvRoleParamVo;
import com.xc.fast_deploy.vo.module_vo.permission.ModuleUserEnvRoleVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

public interface PModuleUserMapper {
  
  int deleteByPrimaryKey(Integer id);
  
  int insert(PModuleUser record);
  
  int insertSelective(PModuleUser record);
  
  PModuleUser selectByPrimaryKey(Integer id);
  
  int updateByPrimaryKeySelective(PModuleUser record);
  
  int updateByPrimaryKeyWithBLOBs(PModuleUser record);
  
  int updateByPrimaryKey(PModuleUser record);
  
  PModuleUser selectByUserNameAndCname(@Param("username") String username, @Param("cname") String cname);
  
  PModuleUser selectByUserName(String username);
  
  PModuleUser selectByUserId(String userId);
  
  List<ModulePermissionDTO> selectPermissionByUserName(@Param("username") String username,
                                                       @Param("isProd") Integer isProd);
  
  List<ModuleUserEnvRoleVo> selectRoleBindUser(@Param("userId") Integer userId,
                                               @Param("roleId") Integer roleId,
                                               @Param("envId") Integer envId);
  
  List<ModuleUserEnvRoleVo> selectUserRoleBind(ModuleUserEnvRoleVo envRoleVo);
  
  int insertUserRoleEnvBatch(List<ModuleUserEnvRoleVo> envRoleVos);
  
  List<ModuleUserEnvRoleDto> selectUserEnvRoleVoPageByVo(ModuleUserEnvRoleParamVo userEnvRoleParamVo);
  
  List<ModuleUserEnvRoleDto> selectUserEnvRolePageByVo(ModuleUserEnvRoleParamVo userEnvRoleParamVo);
  
  ModuleUserEnvRoleVo selectUserRoleBindInfoById(Integer userRoleBindId);
  
  int deleteUserRoleDataByEnvRoleVo(ModuleUserEnvRoleVo envRoleVo);
  
  List<PModuleUser> selectUserPageByVo(PModuleUserSelectParamVo moduleUserSelectParamVo);
  
  List<PModuleUser> selectAllUserInfo();
  
  PModuleUser selectLastOne();
  
  Set<Integer> selectUserIdAllCenters(@Param("userId") String userId,
                                      @Param("envId") Integer envId);
  
  Set<Integer> selectUserAllCentersById(@Param("userId") Integer userId,
                                        @Param("envId") Integer envId);
  
  List<ModulePermissionDTO> selectPermissionByUserId(@Param("userId") String userId,
                                                     @Param("isProd") Integer isProd);
  
  int deleteUserRoleBind(Set<Integer> userRoleBindIds);
  
  Set<Integer> selectApproverEnvByUsername(String username);
}