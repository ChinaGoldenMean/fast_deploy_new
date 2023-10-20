package com.xc.fast_deploy.controller;

import com.alibaba.fastjson.JSONObject;
import com.xc.fast_deploy.dto.ResponseDTO;
import com.xc.fast_deploy.dto.module.ModuleDeploySelfConfDTO;
import com.xc.fast_deploy.myException.K8SDeployException;
import com.xc.fast_deploy.myException.TransYaml2K8sVoException;
import com.xc.fast_deploy.myenum.k8sEnum.K8sResourceStatusEnum;
import com.xc.fast_deploy.service.common.ModuleResourceService;
import com.xc.fast_deploy.service.common.ModuleUserService;
import com.xc.fast_deploy.shiro.token.JwtUtil;
import com.xc.fast_deploy.utils.PermissionJudgeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RequestMapping(value = "/k8s/resource")
@RestController
@Slf4j
public class K8sResourceController {
  
  @Autowired
  private ModuleResourceService resourceService;
  @Autowired
  private ModuleUserService userService;
  
  //获取资源列表  包括 ingress configMap svc deployment daemonset等资源的情况
  //每次获取资源列表的时候存储已有的资源到数据库当中已有的资源名称
  @GetMapping(value = "/all")
  public String getResourcePage(Integer envId,
                                @RequestParam(defaultValue = "default", required = false)
                                String namespace,
                                Integer k8sKindType, Integer k8sStatus, String keywords) {
    log.info("参数接收 envId:{},namespace:{},k8sKindType:{}", envId, namespace, k8sKindType);
    ResponseDTO responseDTO = new ResponseDTO();
    if (envId == null || k8sKindType == null || k8sStatus == null) {
      responseDTO.argsNotOK();
    } else {
      K8sResourceStatusEnum enumByCode = K8sResourceStatusEnum.getEnumByCode(k8sStatus);
      if (enumByCode == null) {
        responseDTO.argsNotOK();
      } else {
        String userIdFromToken = JwtUtil
            .getUserIdFromToken((String) SecurityUtils.getSubject().getPrincipal());
        PermissionJudgeUtils.judgeUserPermission(userService,
            "publish_manage_custom_menu", userIdFromToken
            , envId);
        List<ModuleDeploySelfConfDTO> deploySelfConfs =
            resourceService.listAllResource(envId, namespace, k8sKindType,
                k8sStatus, userIdFromToken, keywords);
        responseDTO.success(deploySelfConfs);
      }
    }
    return JSONObject.toJSONString(responseDTO);
  }
  
  //上传YAML文件作为资源并发布
  @PostMapping(value = "/insertOne")
  public String deployUseYaml(Integer envId, MultipartFile yamlFile) {
    log.info("参数接收 envId:{}", envId);
    ResponseDTO responseDTO = new ResponseDTO();
    if (envId == null || yamlFile == null || yamlFile.isEmpty()) {
      responseDTO.argsNotOK();
    } else {
      responseDTO.fail("发布失败");
      String userIdFromToken = JwtUtil.getUserIdFromToken((String)
          SecurityUtils.getSubject().getPrincipal());
      
      PermissionJudgeUtils.judgeUserPermission(userService,
          "publish_manage_custom_add", userIdFromToken, envId);
      try {
        responseDTO = resourceService.insertResourceInfo(envId, yamlFile, userIdFromToken);
      } catch (TransYaml2K8sVoException | K8SDeployException e) {
        responseDTO.fail(e.getMessage());
      }
    }
    return JSONObject.toJSONString(responseDTO);
  }
  
  //更新YAML文件资源替换  下线原有的资源 上线新的用YAML文件替换
  @PostMapping(value = "/updateOne")
  public String updateDeployYaml(Integer envId, Integer resourceId, MultipartFile yamlFile) {
    log.info("参数接收 envId:{},resourceId:{}", envId, resourceId);
    ResponseDTO responseDTO = new ResponseDTO();
    responseDTO.fail("替换上线失败");
    if (envId == null || yamlFile == null || yamlFile.isEmpty()) {
      responseDTO.argsNotOK();
    } else {
      responseDTO.fail("发布失败");
      String userIdFromToken = JwtUtil.getUserIdFromToken((String) SecurityUtils.getSubject().getPrincipal());
      PermissionJudgeUtils.judgeUserPermission(userService,
          "publish_manage_custom_update", userIdFromToken, envId);
      try {
        if (resourceService.updateOneResource(envId, resourceId, yamlFile, userIdFromToken)) {
          responseDTO.success("替换上线OK");
        }
      } catch (TransYaml2K8sVoException | K8SDeployException e) {
        responseDTO.fail(e.getMessage());
      }
    }
    return JSONObject.toJSONString(responseDTO);
  }
  
  //更改资源状态
  @PostMapping(value = "/status")
  public String updateResourceStatus(Integer envId, Integer resourceId, Integer k8sStatus) {
    log.info("更改资源状态：envId：{},resourceId:{},k8sStatus:{}", envId, resourceId, k8sStatus);
    ResponseDTO responseDTO = new ResponseDTO();
    responseDTO.fail("更改失败");
    if (envId == null || resourceId == null || k8sStatus == null) {
      responseDTO.argsNotOK();
    } else {
      PermissionJudgeUtils.judgeUserPermission(userService,
          "publish_manage_custom_status",
          JwtUtil.getUserIdFromToken((String) SecurityUtils.getSubject().getPrincipal()), envId);
      try {
        if (k8sStatus == 1) {
          if (resourceService.onlineResource(envId, resourceId)) {
            responseDTO.success("操作成功");
          }
        } else if (k8sStatus == 0) {
          if (resourceService.offlineResource(envId, resourceId)) {
            responseDTO.success("操作成功");
          }
        }
      } catch (K8SDeployException e) {
        responseDTO.fail(e.getMessage());
      }
      
    }
    return JSONObject.toJSONString(responseDTO);
  }
  
  //查看YAML资源详情 已经在线上显示实时YAML文件  已下线的展示
  @GetMapping(value = "/details")
  public String getResourceDetails(Integer envId, Integer resourceId) {
    log.info("参数接收 envId:{},resourceId:{}", envId, resourceId);
    ResponseDTO responseDTO = new ResponseDTO();
    responseDTO.fail("获取详情失败");
    if (envId == null || resourceId == null) {
      responseDTO.argsNotOK();
    } else {
      PermissionJudgeUtils.judgeUserPermission(userService,
          "publish_manage_custom_details",
          JwtUtil.getUserIdFromToken((String)
              SecurityUtils.getSubject().getPrincipal()), envId);
      ModuleDeploySelfConfDTO selfConfDTO =
          resourceService.getResourceDetails(envId, resourceId);
      responseDTO.success(selfConfDTO);
    }
    return JSONObject.toJSONString(responseDTO);
  }
  
  //删除已下线资源数据库
  @DeleteMapping(value = "/deleteOne")
  public String deleteResourceDataOffline(Integer envId, Integer resourceId) {
    ResponseDTO responseDTO = new ResponseDTO();
    if (envId == null || resourceId == null) {
      responseDTO.argsNotOK();
    } else {
      PermissionJudgeUtils.judgeUserPermission(userService,
          "publish_manage_custom_delete",
          JwtUtil.getUserIdFromToken((String) SecurityUtils.getSubject().getPrincipal()), envId);
      if (resourceService.deleteResourceData(envId, resourceId)) {
        responseDTO.success("删除成功");
      } else {
        responseDTO.fail("删除失败");
      }
    }
    return JSONObject.toJSONString(responseDTO);
  }
  
}
