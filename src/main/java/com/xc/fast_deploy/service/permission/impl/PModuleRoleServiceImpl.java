package com.xc.fast_deploy.service.permission.impl;

import com.xc.fast_deploy.dao.master_dao.PModuleBasePermissionMapper;
import com.xc.fast_deploy.dao.master_dao.PModulePermissionMapper;
import com.xc.fast_deploy.dao.master_dao.PModuleRoleMapper;
import com.xc.fast_deploy.dao.master_dao.PModuleUserMapper;
import com.xc.fast_deploy.dto.ResponseDTO;
import com.xc.fast_deploy.model.master_model.PModuleBasePermission;
import com.xc.fast_deploy.model.master_model.PModulePermission;
import com.xc.fast_deploy.model.master_model.PModuleRole;
import com.xc.fast_deploy.service.permission.PModuleRoleService;
import com.xc.fast_deploy.vo.module_vo.permission.ModuleRoleBasePermissionVo;
import com.xc.fast_deploy.vo.module_vo.permission.ModuleUserEnvRoleVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class PModuleRoleServiceImpl implements PModuleRoleService {
  
  @Autowired
  private PModuleRoleMapper roleMapper;
  
  @Autowired
  private PModulePermissionMapper permissionMapper;
  
  @Autowired
  private PModuleBasePermissionMapper basePermissionMapper;
  
  @Autowired
  private PModuleUserMapper pModuleUserMapper;
  
  @Override
  public boolean addRole(PModuleRole role) {
    role.setCreateTime(new Date());
    role.setUpdateTime(new Date());
    int i = roleMapper.insert(role);
    return i > 0;
  }
  
  @Override
  @Transactional(rollbackFor = Exception.class)
  public ResponseDTO addRoleBindBasePermission(Integer roleId, Integer[] basePermissionIds) {
    ResponseDTO responseDTO = new ResponseDTO();
    responseDTO.fail("操作失败");
    //校验角色id是否创建
    PModuleRole pModuleRole = roleMapper.selectByPrimaryKey(roleId);
    if (pModuleRole != null) {
      //校验权限id是否都存在
      Set<Integer> ids = new HashSet<>(Arrays.asList(basePermissionIds));
      Set<Integer> permissionIdSet = basePermissionMapper.selectIds(ids);
      if (permissionIdSet != null && permissionIdSet.size() != ids.size()) {
        responseDTO.fail("基础权限不存在");
        return responseDTO;
      }
      List<ModuleRoleBasePermissionVo> basePermissionVoList = new ArrayList<>();
      
      for (Integer basePermId : ids) {
        basePermissionVoList.add(new ModuleRoleBasePermissionVo(roleId, basePermId));
      }
      int i = permissionMapper.insertRoleBasePermissionBatch(basePermissionVoList);
      if (i == ids.size()) {
        responseDTO.success("添加成功");
      }
    }
    return responseDTO;
  }
  
  @Override
  @Transactional(rollbackFor = Exception.class)
  public ResponseDTO deleteRoleData(Integer roleId) {
    ResponseDTO responseDTO = new ResponseDTO();
    //首先查询对应的role是否存在
    PModuleRole pModuleRole = roleMapper.selectByPrimaryKey(roleId);
    if (pModuleRole == null) {
      responseDTO.fail("未找到该角色!");
      return responseDTO;
    }
    //首先删除角色本身数据
    roleMapper.deleteByPrimaryKey(roleId);
    //然后删除角色关联基础权限里的数据
    roleMapper.deleteRoleBasePermByRoleId(roleId);
    //再删除用户关联角色的数据
    ModuleUserEnvRoleVo envRoleVo = new ModuleUserEnvRoleVo();
    envRoleVo.setRoleId(roleId);
    pModuleUserMapper.deleteUserRoleDataByEnvRoleVo(envRoleVo);
    //然后操作成功后直接返回
    responseDTO.success();
    return responseDTO;
  }
  
  @Override
  public boolean updateRole(PModuleRole role) {
    return roleMapper.updateByPrimaryKeySelective(role) > 0;
  }
  
  @Override
  public ResponseDTO updateRoleBindPermission(Integer roleId, Integer[] basePermissionIds) {
    ResponseDTO responseDTO = new ResponseDTO();
    //校验角色id是否创建
    PModuleRole pModuleRole = roleMapper.selectByPrimaryKey(roleId);
    if (pModuleRole != null) {
      //校验权限id是否都存在
      Set<Integer> ids = new HashSet<>(Arrays.asList(basePermissionIds));
      Set<Integer> basePermIdSet = basePermissionMapper.selectIds(ids);
      if (basePermIdSet != null && basePermIdSet.size() != ids.size()) {
        responseDTO.fail("权限不存在异常");
        return responseDTO;
      }
      List<ModuleRoleBasePermissionVo> permissionVoList = new ArrayList<>();
      //先删除原有的角色关联权限数据
      roleMapper.deleteRoleBasePermByRoleId(roleId);
      //然后再将新添加的权限数据关联进去
      for (Integer basePermId : ids) {
        permissionVoList.add(new ModuleRoleBasePermissionVo(roleId, basePermId));
      }
      int i = permissionMapper.insertRoleBasePermissionBatch(permissionVoList);
      if (i == ids.size()) {
        responseDTO.success("更新成功");
      }
    }
    return responseDTO;
  }
  
  /**
   * 查询所有角色数据
   *
   * @return
   */
  @Override
  public List<PModuleRole> selectAllRole() {
    return roleMapper.selectAll();
  }
  
  //根据角色id查询对应的所有对应的权限信息 并清除掉重复数据
  @Override
  public List<PModulePermission> getAllRolePermissionByRoleId(Integer roleId) {
    return roleMapper.selectPermissionByRoleId(roleId);
  }
  
  @Override
  public List<PModuleBasePermission> getAllRoleBasePermByRoleId(Integer roleId) {
    return roleMapper.selectRoleBasePermByRoleId(roleId);
  }
  
}
