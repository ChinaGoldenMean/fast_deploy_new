package com.xc.fast_deploy.controller;

import com.alibaba.fastjson.JSONObject;
import com.xc.fast_deploy.dto.ResponseDTO;
import com.xc.fast_deploy.dto.module.*;
import com.xc.fast_deploy.myException.UnauthorizedException;
import com.xc.fast_deploy.service.common.ModuleDeployService;
import com.xc.fast_deploy.service.common.ModuleUserService;
import com.xc.fast_deploy.shiro.token.JwtUtil;
import com.xc.fast_deploy.vo.module_vo.ModuleDeployVo;
import com.xc.fast_deploy.vo.module_vo.ModuleEnvCenterManageVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
@RequestMapping(value = "/deploy")
@Slf4j
public class ModuleShowController {
  
  @Autowired
  private ModuleDeployService deployService;
  
  @Autowired
  private ModuleUserService userService;
  
  /**
   * 查询已经建立的发布环境和中心的关系数据
   */
  @GetMapping(value = "/env/center/module/info")
  public String getAllDeployModuleShow() {
    ResponseDTO responseDTO = new ResponseDTO();
    responseDTO.fail("获取数据失败");
    String userId = JwtUtil.getUserIdFromToken(
        (String) SecurityUtils.getSubject().getPrincipal());
    //首先获取权限数据,然后根据权限数据去判断
    Map<Integer, Set<String>> envPermissionMap =
        userService.selectEnvPermissionByUserId(userId);

//        Map<Integer, Set<String>> envPermissionMap =
//                userService.selectEnvPermissionByUserName(JwtUtil.getUserNameFromToken(
//                (String) SecurityUtils.getSubject().getPrincipal()));
    
    if (envPermissionMap.size() <= 0) {
      throw new UnauthorizedException();
    }
    Set<Integer> envIds = envPermissionMap.keySet();
    List<ModuleDeployEnvDTO> envDTOList = deployService.selectModuleEnvCenterAll(envIds, userId);
    if (envDTOList.size() > 0) {
      responseDTO.success(envDTOList);
    }
    return JSONObject.toJSONString(responseDTO);
  }
  
  /**
   * 根据关键词搜索模块列表
   *
   * @param keyName
   * @return
   */
  @GetMapping(value = "/module/list")
  public String getModuleList(String keyName) {
    log.info("参数接收 keyName: {}", keyName);
    ResponseDTO responseDTO = new ResponseDTO();
    
    Map<Integer, Set<String>> envPermissionMap =
        userService.selectEnvPermissionByUserId(JwtUtil.getUserIdFromToken(
            (String) SecurityUtils.getSubject().getPrincipal()));
    if (envPermissionMap.size() <= 0) {
      throw new UnauthorizedException();
    }
    Set<Integer> envIds = envPermissionMap.keySet();
    ModuleDeployVo deployVo = new ModuleDeployVo();
    deployVo.setEnvIds(envIds);
    
    List<ModuleEnvCenterManageVo> manageVoList = new ArrayList<>();
    if (StringUtils.isNotBlank(keyName)) {
      deployVo.setKeyName(keyName.trim());
      manageVoList = deployService.selectByDeployVO(deployVo);
    }
    responseDTO.success(manageVoList);
    return JSONObject.toJSONString(responseDTO);
  }
  
  /**
   * 根据上一步搜索到的模块
   *
   * @param envId
   * @param centerId
   * @param moduleId
   * @return
   */
  @GetMapping(value = "/module/location")
  public String getReadModulePath(Integer envId, Integer centerId, Integer moduleId) {
    log.info("参数接收 envId: {},centerId: {},moduleId: {}", envId, centerId, moduleId);
    ResponseDTO responseDTO = new ResponseDTO();
    String userId = JwtUtil.getUserIdFromToken(
        (String) SecurityUtils.getSubject().getPrincipal());
    Map<Integer, Set<String>> envPermissionMap =
        userService.selectEnvPermissionByUserId(userId);
    if (envPermissionMap.size() <= 0) {
      throw new UnauthorizedException();
    }
    Set<Integer> envIds = envPermissionMap.keySet();
    List<ModuleDeployEnvDTO> envDTOList = deployService.selectModuleEnvCenterAll(envIds, userId);
    StringBuilder pathSb = new StringBuilder();
    pathSb.append("/center");
    if (envDTOList != null && envDTOList.size() > 0) {
      for (int i = 0; i < envDTOList.size(); i++) {
        if (envDTOList.get(i).getEnvId().equals(envId)) {
          pathSb.append(i).append("/index");
          List<ModuleCenterEnvDTO> centerList = envDTOList.get(i).getCenterList();
          if (centerList != null && centerList.size() > 0) {
            for (int j = 0; j < centerList.size(); j++) {
              List<ModuleCenterDTO> centerDTOList =
                  centerList.get(j).getCenterDTOList();
              if (centerDTOList != null && centerDTOList.size() > 0) {
                for (int k = 0; k < centerDTOList.size(); k++) {
                  if (centerDTOList.get(k).getCenterId().equals(centerId)) {
                    pathSb.append(j);
                    ModuleLocationDTO locationDTO = new ModuleLocationDTO();
                    locationDTO.setPath(pathSb.toString());
                    locationDTO.setCenterEnvDTO(centerDTOList.get(k));
                    responseDTO.success(locationDTO);
                  }
                }
              }
            }
          }
        }
      }
    }
    return JSONObject.toJSONString(responseDTO);
  }

//    @GetMapping(value = "/home/resource")
//    public String getDeploySourceData(Integer envId) {
//        log.info("参数接收 envId: {}", envId);
//        ResponseDTO responseDTO = new ResponseDTO();
//        if (envId == null) {
//            responseDTO.argsNotOK();
//        } else {
//            ModuleDeployHomeDTO deployHomeDTO = deployService.getHomeDeploySourceData(envId);
//            responseDTO.success(deployHomeDTO);
//        }
//
//        return JSONObject.toJSONString(responseDTO);
//    }
}
