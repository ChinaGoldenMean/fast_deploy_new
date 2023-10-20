package com.xc.fast_deploy.controller;

import com.alibaba.fastjson.JSONObject;
import com.xc.fast_deploy.dto.ResponseDTO;
import com.xc.fast_deploy.dto.k8s.K8sNodeDTO;
import com.xc.fast_deploy.model.master_model.ModuleEnv;
import com.xc.fast_deploy.service.common.ModuleUserService;
import com.xc.fast_deploy.service.k8s.K8sNodeService;
import com.xc.fast_deploy.service.common.ModuleEnvService;
import com.xc.fast_deploy.shiro.token.JwtUtil;
import com.xc.fast_deploy.utils.PermissionJudgeUtils;
import com.xc.fast_deploy.utils.k8s.K8sUtils;
import com.xc.fast_deploy.vo.k8s_vo.K8sNodeVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/*
该类主要是为了获取k8s环境中的node节点的信息,并可以做一定的管理操作
 */
@RestController
@RequestMapping(value = "/node")
@Slf4j
public class K8sNodeController {
  
  @Autowired
  private K8sNodeService nodeService;
  @Autowired
  private ModuleEnvService envService;
  @Autowired
  private ModuleUserService userService;
  
  //获取node信息列表展示
  @GetMapping(value = "/infos")
  public String getNodeInfoList(Integer envId, String nodeIPKey, String labelKeyName) {
    log.info("/node/infos 参数接收: envId:{}", envId);
    ResponseDTO responseDTO = new ResponseDTO();
    PermissionJudgeUtils.judgeUserPermission(userService, "base_config_node_menu",
        JwtUtil.getUserIdFromToken((String) SecurityUtils.getSubject().getPrincipal()), envId);
    List<K8sNodeDTO> nodeDTOList = nodeService.selectInfo(envId, nodeIPKey, labelKeyName);
    responseDTO.success(nodeDTOList);
    return JSONObject.toJSONString(responseDTO);
  }
  
  //隔离node节点
  @PostMapping(value = "/unschedule")
  public String unScheduleNode(Integer envId, String nodeName) {
    log.info("/unschedule 参数接收: envId:{},nodeName:{}", envId, nodeName);
    PermissionJudgeUtils.judgeUserPermission(userService, "base_config_node_unSchedule",
        JwtUtil.getUserIdFromToken((String) SecurityUtils.getSubject().getPrincipal()), envId);
    ResponseDTO responseDTO = new ResponseDTO();
    responseDTO.argsNotOK();
    if (envId != null && StringUtils.isNotBlank(nodeName)) {
      if (nodeService.scheduleNode(envId, nodeName, true)) {
        responseDTO.success("操作成功");
      }
    }
    return JSONObject.toJSONString(responseDTO);
  }
  
  //恢复node节点
  @PostMapping(value = "/resume/schedule")
  public String resumeSchedulable(Integer envId, String nodeName) {
    log.info("/resume/schedule 参数接收: envId:{},nodeName:{}", envId, nodeName);
    PermissionJudgeUtils.judgeUserPermission(userService, "base_config_node_resumeSchedule",
        JwtUtil.getUserIdFromToken((String) SecurityUtils.getSubject().getPrincipal()), envId);
    ResponseDTO responseDTO = new ResponseDTO();
    responseDTO.argsNotOK();
    if (envId != null && StringUtils.isNotBlank(nodeName)) {
      if (nodeService.scheduleNode(envId, nodeName, false)) {
        responseDTO.success();
      }
    }
    return JSONObject.toJSONString(responseDTO);
  }
  
  //修改原有的展示的标签
  @PostMapping(value = "/updateLabel")
  public String updateLabel(@RequestBody K8sNodeVo k8sNodeVo) {
    log.info("updateLabel 参数接收: k8sNodeVo:{}", JSONObject.toJSON(k8sNodeVo));
    PermissionJudgeUtils.judgeUserPermission(userService, "base_config_node_updateLabel",
        JwtUtil.getUserIdFromToken((String) SecurityUtils.getSubject().getPrincipal()), k8sNodeVo.getEnvId());
    ResponseDTO responseDTO = new ResponseDTO();
    responseDTO.argsNotOK();
    if (k8sNodeVo.getEnvId() != null && StringUtils.isNotBlank(k8sNodeVo.getNodeName())
        && k8sNodeVo.getLabelMap() != null) {
      if (nodeService.updateNodeLabel(k8sNodeVo)) {
        responseDTO.success("修改成功");
      }
    }
    return JSONObject.toJSONString(responseDTO);
  }
  
  //node详情查看
  @GetMapping(value = "/detail")
  public String getNodeDetailInfo(Integer envId, String nodeName) {
    log.info("/resume/schedule 参数接收: envId:{},nodeName:{}", envId, nodeName);
    PermissionJudgeUtils.judgeUserPermission(userService, "base_config_node_details",
        JwtUtil.getUserIdFromToken((String) SecurityUtils.getSubject().getPrincipal()), envId);
    ResponseDTO responseDTO = new ResponseDTO();
    responseDTO.argsNotOK();
    if (envId != null && StringUtils.isNotBlank(nodeName)) {
      ModuleEnv moduleEnv = envService.selectOne(envId);
      if (moduleEnv != null) {
        String s = K8sUtils.okhttpGetBack(moduleEnv.getK8sConfig(),
            "/api/v1/nodes/" + nodeName + "/status");
        if (StringUtils.isNotBlank(s)) {
          responseDTO.success(JSONObject.parseObject(s));
        }
      }
    }
    return JSONObject.toJSONString(responseDTO);
  }
  
}
