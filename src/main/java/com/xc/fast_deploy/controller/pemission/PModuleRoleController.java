package com.xc.fast_deploy.controller.pemission;

import com.alibaba.fastjson.JSONObject;
import com.xc.fast_deploy.dto.ResponseDTO;
import com.xc.fast_deploy.model.master_model.PModuleBasePermission;
import com.xc.fast_deploy.model.master_model.PModulePermission;
import com.xc.fast_deploy.model.master_model.PModuleRole;
import com.xc.fast_deploy.service.permission.PModuleRoleService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/role")
public class PModuleRoleController {
  
  @Autowired
  protected PModuleRoleService roleService;
  
  //添加角色
  @PostMapping(value = "/insert")
  public String addRole(PModuleRole role) {
    ResponseDTO responseDTO = new ResponseDTO();
    if (role == null
        || StringUtils.isBlank(role.getRoleName())
        || StringUtils.isBlank(role.getRoleCode())) {
      responseDTO.fail("输入参数不符合要求");
      return JSONObject.toJSONString(responseDTO);
    }
    if (roleService.addRole(role)) {
      responseDTO.success("添加成功");
    } else {
      responseDTO.fail("添加失败");
    }
    return JSONObject.toJSONString(responseDTO);
  }
  
  //查询所有角色
  @GetMapping(value = "/getAllRole")
  public String getAllRole() {
    ResponseDTO responseDTO = new ResponseDTO();
    List<PModuleRole> moduleRoleList = roleService.selectAllRole();
    responseDTO.success(moduleRoleList);
    return JSONObject.toJSONString(responseDTO);
  }
  
  //更新角色信息
  @PostMapping(value = "/updateRole")
  public String updateRoleData(PModuleRole role) {
    ResponseDTO responseDTO = new ResponseDTO();
    if (role == null
        || StringUtils.isBlank(role.getRoleName())
        || StringUtils.isBlank(role.getRoleCode())
        || role.getId() == null) {
      responseDTO.fail("输入参数不符合要求");
      return JSONObject.toJSONString(responseDTO);
    }
    if (roleService.updateRole(role)) {
      responseDTO.success("修改成功");
    }
    return JSONObject.toJSONString(responseDTO);
  }
  
  //    删除角色同时删除用户关联和权限关联
// 以及删除对应的角色权限对应关系
  @PostMapping(value = "/deleteRole")
  public String deleteRole(Integer roleId) {
    ResponseDTO responseDTO = new ResponseDTO();
    responseDTO.fail("操作失败");
    if (roleId != null) {
      responseDTO = roleService.deleteRoleData(roleId);
    }
    return JSONObject.toJSONString(responseDTO);
  }
  
  //添加角色关联基础权限信息
  @PostMapping(value = "/roleBindBasePermisson")
  public String addRoleBindPermission(Integer roleId, Integer[] basePermissionIds) {
    ResponseDTO responseDTO = new ResponseDTO();
    if (roleId == null || basePermissionIds == null || basePermissionIds.length <= 0) {
      responseDTO.fail("输入参数不符合要求");
    } else {
      responseDTO = roleService.addRoleBindBasePermission(roleId, basePermissionIds);
    }
    return JSONObject.toJSONString(responseDTO);
  }
  
  //修改角色关联基础权限信息
  @PostMapping(value = "/updateRolePermission")
  public String updateRolePermission(Integer roleId, Integer[] basePermissionIds) {
    ResponseDTO responseDTO = new ResponseDTO();
    if (roleId == null || basePermissionIds == null || basePermissionIds.length <= 0) {
      responseDTO.fail("输入参数不符合要求");
    } else {
      responseDTO = roleService.updateRoleBindPermission(roleId, basePermissionIds);
    }
    return JSONObject.toJSONString(responseDTO);
  }
  
  //查询角色关联的所有最小权限
  @GetMapping(value = "/getRolePermission")
  public String getAllRolePermissionBindByRoleId(Integer roleId) {
    ResponseDTO responseDTO = new ResponseDTO();
    List<PModulePermission> permissionList = roleService.getAllRolePermissionByRoleId(roleId);
    responseDTO.success(permissionList);
    return JSONObject.toJSONString(responseDTO);
  }
  
  //查询角色关联的所有基础权限
  @GetMapping(value = "/getRoleBasePerm")
  public String getAllRoleBasePermByRoleId(Integer roleId) {
    ResponseDTO responseDTO = new ResponseDTO();
    List<PModuleBasePermission> basePermissionList = roleService.getAllRoleBasePermByRoleId(roleId);
    responseDTO.success(basePermissionList);
    return JSONObject.toJSONString(responseDTO);
  }
}
