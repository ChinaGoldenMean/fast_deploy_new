package com.xc.fast_deploy.controller.pemission;

import com.alibaba.fastjson.JSONObject;
import com.xc.fast_deploy.dto.MyPageInfo;
import com.xc.fast_deploy.dto.ResponseDTO;
import com.xc.fast_deploy.model.master_model.PModuleBasePermission;
import com.xc.fast_deploy.model.master_model.PModulePermission;
import com.xc.fast_deploy.service.permission.PModuleBasePermissionService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/base/permission")
public class PModuleBasePermissionController {
  
  @Autowired
  private PModuleBasePermissionService basePermissionService;
  
  /**
   * 获取所有基础权限信息
   *
   * @return
   */
  @GetMapping(value = "/all")
  public String getAllBasePermssion(@RequestParam(value = "pageNum", defaultValue = "1", required = false) Integer pageNum,
                                    @RequestParam(value = "pageSize", defaultValue = "10", required = false) Integer pageSize,
                                    String keyName) {
    MyPageInfo<PModuleBasePermission> basePermissionList = basePermissionService.selectAll(pageNum, pageSize, keyName);
    return JSONObject.toJSONString(ResponseDTO.successReq(basePermissionList));
  }
  
  /**
   * 添加基础权限信息
   *
   * @param basePermission
   * @return
   */
  @PostMapping(value = "/addOne")
  public String allBasePermission(PModuleBasePermission basePermission) {
    ResponseDTO responseDTO = new ResponseDTO();
    responseDTO.fail("参数校验失败");
    if (basePermission != null
        && StringUtils.isNotBlank(basePermission.getBasePerName())) {
      basePermissionService.insertOne(basePermission);
      responseDTO.success("添加成功");
    }
    return JSONObject.toJSONString(responseDTO);
  }
  
  /**
   * 修改基础权限信息
   */
  @PostMapping(value = "/updateOne")
  public String updateBasePermission(PModuleBasePermission basePermission) {
    ResponseDTO responseDTO = new ResponseDTO();
    responseDTO.fail("参数校验失败");
    if (basePermission != null && basePermission.getId() != null
        && StringUtils.isNotBlank(basePermission.getBasePerName())
        && StringUtils.isNotBlank(basePermission.getBasePerCode())) {
      basePermissionService.updateOne(basePermission);
      responseDTO.success("修改成功");
    }
    return JSONObject.toJSONString(responseDTO);
  }
  
  /**
   * 获取单条基础权限信息
   */
  @GetMapping(value = "/getOne")
  public String getOneBasePermission(Integer basePermissionId) {
    ResponseDTO responseDTO = new ResponseDTO();
    responseDTO.fail("查询失败");
    if (basePermissionId != null) {
      PModuleBasePermission basePermission = basePermissionService.selectById(basePermissionId);
      responseDTO.success(basePermission);
    }
    return JSONObject.toJSONString(responseDTO);
  }
  
  /**
   * 根据基础权限信息获取所对应的所有权限信息
   *
   * @param basePermissionId
   * @return
   */
  @GetMapping(value = "/getBaseBindPermList")
  public String getPermissionList(Integer basePermissionId) {
    ResponseDTO responseDTO = new ResponseDTO();
    responseDTO.fail("查询失败");
    if (basePermissionId != null) {
      List<PModulePermission> permissionList =
          basePermissionService.selectPermissionList(basePermissionId);
      responseDTO.success(permissionList);
    }
    return JSONObject.toJSONString(responseDTO);
  }
  
  /**
   * 添加基础权限关联权限信息,修改绑定也可使用这个
   */
  @PostMapping(value = "/bindListBasePerm")
  public String bindBasePerm(Integer basePermissionId, Integer[] permIds) {
    ResponseDTO responseDTO = new ResponseDTO();
    responseDTO.fail("更新失败");
    if (basePermissionId == null || permIds == null || permIds.length <= 0) {
      responseDTO.fail("输入参数有误");
    }
    responseDTO = basePermissionService.bindBasePerm(basePermissionId, permIds);
    return JSONObject.toJSONString(responseDTO);
  }
  
  /**
   * 删除基础权限
   *
   * @param basePermissionId
   * @return
   */
  @PostMapping(value = "/delBasePerm")
  public String delBasePerm(Integer basePermissionId) {
    ResponseDTO responseDTO = new ResponseDTO();
    if (basePermissionId != null) {
      basePermissionService.delBasePerm(basePermissionId);
      responseDTO.success("删除成功");
    }
    return JSONObject.toJSONString(responseDTO);
  }
  
}
