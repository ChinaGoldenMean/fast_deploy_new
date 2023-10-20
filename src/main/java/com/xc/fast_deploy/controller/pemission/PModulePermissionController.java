package com.xc.fast_deploy.controller.pemission;

import com.alibaba.fastjson.JSONObject;
import com.xc.fast_deploy.annotation.JudgePermission;
import com.xc.fast_deploy.dto.MyPageInfo;
import com.xc.fast_deploy.dto.ResponseDTO;
import com.xc.fast_deploy.model.master_model.PModulePermission;
import com.xc.fast_deploy.service.permission.PModulePermissionService;
import com.xc.fast_deploy.vo.module_vo.ModuleJobVo;
import com.xc.fast_deploy.vo.module_vo.param.ModuleCenterSelectParamVo;
import com.xc.fast_deploy.vo.module_vo.permission.ModuleRolePermissionParamVo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/permission")
public class PModulePermissionController {
  
  @Autowired
  private PModulePermissionService permissionService;
  
  //添加权限
  @PostMapping(value = "/insert")
  public String addPermission(PModulePermission permission) {
    ResponseDTO responseDTO = new ResponseDTO();
    responseDTO.fail("操作失败");
    if (!validate(permission)) {
      responseDTO.fail("参数校验错误");
    } else if (permissionService.addPermission(permission)) {
      responseDTO.success("添加权限成功");
    }
    return JSONObject.toJSONString(responseDTO);
  }
  
  //校验添参数是否符合要求
  private boolean validate(PModulePermission permission) {
    if (permission == null
        || StringUtils.isBlank(permission.getAction())
        || StringUtils.isBlank(permission.getActionName())) {
      return false;
    }
    return true;
  }
  
  //修改权限名称和路径
  @PostMapping(value = "/update")
  public String updatePermission(PModulePermission permission) {
    ResponseDTO responseDTO = new ResponseDTO();
    responseDTO.fail("操作失败");
    if (permissionService.updatePermission(permission)) {
      responseDTO.success("修改权限成功");
    }
    return JSONObject.toJSONString(responseDTO);
  }
  
  //分页查询所有的权限列表
  @GetMapping(value = "/getAllPermission")
  public String getAllPermission(
      @RequestParam(value = "pageNum", defaultValue = "1", required = false) Integer pageNum,
      @RequestParam(value = "pageSize", defaultValue = "10", required = false) Integer pageSize,
      ModuleRolePermissionParamVo permissionParamVo) {
    ResponseDTO responseDTO = new ResponseDTO();
    responseDTO.fail("未查询到任何数据");
    MyPageInfo<PModulePermission> jobVoMyPageInfo = permissionService.selectPageAllInfo(pageNum,
        pageSize, permissionParamVo);
    if (jobVoMyPageInfo.getList() != null && jobVoMyPageInfo.getList().size() > 0) {
      responseDTO.success(jobVoMyPageInfo);
    }
    return JSONObject.toJSONString(responseDTO);
  }
  
  //删除权限同时删除基础关联权限表所有数据
  @PostMapping(value = "/delete")
  public String deletePermission(Integer permissionId) {
    ResponseDTO responseDTO = new ResponseDTO();
    responseDTO.fail("操作失败");
    if (permissionService.deleteById(permissionId)) {
      responseDTO.success("删除权限成功");
    }
    return JSONObject.toJSONString(responseDTO);
  }
  
  //批量删除权限同时删除基础关联权限表所有数据
  @PostMapping(value = "/deletePermissions")
  public String deletePermission(Integer[] permissionIds) {
    ResponseDTO responseDTO = new ResponseDTO();
    responseDTO.fail("操作失败");
    if (permissionService.deleteByIds(permissionIds)) {
      responseDTO.success("批量删除权限成功");
    }
    return JSONObject.toJSONString(responseDTO);
  }
  
}
