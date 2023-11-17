package com.xc.fast_deploy.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import com.xc.fast_deploy.dao.master_dao.ModuleEnvMapper;
import com.xc.fast_deploy.dto.K8sYamlDTO;
import com.xc.fast_deploy.dto.ResponseDTO;
import com.xc.fast_deploy.dto.k8s.K8sPodDTO;
import com.xc.fast_deploy.dto.k8s.K8sServiceDTO;
import com.xc.fast_deploy.model.base.JsonResult;
import com.xc.fast_deploy.model.master_model.ModuleDeployYaml;
import com.xc.fast_deploy.model.master_model.ModuleEnv;
import com.xc.fast_deploy.model.master_model.ModuleUser;
import com.xc.fast_deploy.model.master_model.ModuleYamlDiff;
import com.xc.fast_deploy.myException.DeployIsOnlineExcetion;
import com.xc.fast_deploy.myException.K8SDeployException;
import com.xc.fast_deploy.myException.TransYaml2K8sVoException;
import com.xc.fast_deploy.myenum.k8sEnum.K8sDeploymentResourceEnum;
import com.xc.fast_deploy.myenum.k8sEnum.K8sKindTypeEnum;
import com.xc.fast_deploy.service.common.ModuleDeployService;
import com.xc.fast_deploy.service.common.ModuleDeployYamlService;
import com.xc.fast_deploy.service.common.ModuleUserService;
import com.xc.fast_deploy.shiro.token.JwtUtil;
import com.xc.fast_deploy.utils.PermissionJudgeUtils;
import com.xc.fast_deploy.utils.constant.K8sHttpUrlConstants;
import com.xc.fast_deploy.utils.k8s.K8sUtils;
import com.xc.fast_deploy.vo.k8s_vo.K8sStrategyParamVO;
import com.xc.fast_deploy.vo.k8s_vo.K8sUpdateResourceParamVO;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.xc.fast_deploy.utils.constant.K8sHttpUrlConstants.*;

@RestController
@RequestMapping(value = "/deploy")
@Slf4j
public class ModuleDeployController {
  
  @Autowired
  private ModuleDeployService deployService;
  @Autowired
  private ModuleUserService userService;
  @Autowired
  private ModuleEnvMapper envMapper;
  
  @Value("${myself.pspass.oldversion}")
  private Integer[] oldEnvId;
  
  /**
   * 获取某个模块的发布的pod信息
   *
   * @param moduleId
   * @param envId
   * @return
   */
  @GetMapping(value = "/module/info")
  public String getDeployModuleInfo(Integer moduleId, Integer envId) {
    // log.info("传入参数为moduleId: {},envid: {}", moduleId, envId);
    ResponseDTO responseDTO = new ResponseDTO();
    responseDTO.fail("未获取任何数据");
    if (envId != null && moduleId != null) {
      //权限判断,判断用户在该环境下是否存在权限关联即可
      PermissionJudgeUtils.judgeUserPermission(userService, null,
          JwtUtil.getUserIdFromToken((String) SecurityUtils.getSubject().getPrincipal()), envId);
      List<K8sPodDTO> k8sPodDTOS = deployService.getDeployModuleInfo(moduleId, envId);
      if (k8sPodDTOS != null && k8sPodDTOS.size() > 0) {
        responseDTO.success(k8sPodDTOS);
      }
    }
    return JSONObject.toJSONString(responseDTO);
  }
  
  /**
   * 上线某一个模块
   *
   * @return
   */
  @PostMapping(value = "/module/online")
  public String deployOneModule(Integer moduleId, Integer envId, Integer mirrorId, String mirrorName) {
    // log.info("传入参数为moduleId: {},envid: {}", moduleId, envId);
    ResponseDTO responseDTO = new ResponseDTO();
    responseDTO.fail("发布失败");
    if (moduleId != null && envId != null) {
      ModuleUser moduleUser = JwtUtil.getModuleUserInfo();
      //权限判断,判断用户在该环境下是否存在权限关联即可
      PermissionJudgeUtils.judgeUserPermission(userService, "module_module_online",
          moduleUser.getId(), envId);
      try {
        if (deployService.deployModuleInEnv(moduleId, envId, mirrorId, moduleUser, mirrorName)) {
          responseDTO.success("发布操作成功");
        }
      } catch (DeployIsOnlineExcetion | TransYaml2K8sVoException | K8SDeployException e) {
        responseDTO.fail(e.getMessage());
      }
    }
    return JSONObject.toJSONString(responseDTO);
  }
  
  /**
   * 扩容缩容
   */
  @PostMapping(value = "/module/scale")
  public String scaleModuleSize(Integer moduleId, Integer moduleSize, Integer envId) {
    // log.info("传入参数为moduleId: {},envid: {},moduleSize: {}", moduleId, envId, moduleSize);
    ResponseDTO responseDTO = new ResponseDTO();
    responseDTO.fail("扩缩容失败");
    if (moduleId != null && envId != null && moduleSize != null) {
      ModuleUser moduleUser = JwtUtil.getModuleUserInfo();
      //权限判断,判断用户在该环境下是否存在权限关联即可
      PermissionJudgeUtils.judgeUserPermission(userService, "module_module_scale", moduleUser.getId(), envId);
      try {
        if (deployService.scaleModuleSize(moduleId, moduleSize, envId, moduleUser)) {
          responseDTO.success("操作成功");
        }
      } catch (DeployIsOnlineExcetion e) {
        responseDTO.fail(e.getMessage());
      }
    }
    return JSONObject.toJSONString(responseDTO);
  }
  
  /**
   * 下线模块
   *
   * @param moduleId
   * @param envId
   * @return
   */
  @PostMapping(value = "/module/offline")
  public String offlineModule(Integer moduleId, Integer envId) {
    // log.info("传入参数为moduleId: {},envid: {}", moduleId, envId);
    ResponseDTO responseDTO = new ResponseDTO();
    responseDTO.fail("下线失败");
    if (moduleId != null && envId != null) {
      ModuleUser moduleUser = JwtUtil.getModuleUserInfo();
      //权限判断,判断用户在该环境下是否存在权限关联即可
      PermissionJudgeUtils.judgeUserPermission(userService,
          "module_module_offline", moduleUser.getId(), envId);
      try {
        if (deployService.offline(moduleId, envId, moduleUser)) {
          responseDTO.success("下线成功");
        }
      } catch (DeployIsOnlineExcetion e) {
        responseDTO.fail(e.getMessage());
      }
    }
    return JSONObject.toJSONString(responseDTO);
  }
  
  /**
   * 更换镜像升级
   *
   * @param moduleId
   * @param envId
   * @param mirrorId
   * @return
   */
  @PostMapping(value = "/module/update/mirror")
  public String changeMirror(Integer moduleId, Integer envId, Integer mirrorId, String mirrorName) {
    // log.info("传入参数为moduleId: {},envid: {},mirrorId: {}", moduleId, envId, mirrorId);
    ResponseDTO responseDTO = new ResponseDTO();
    responseDTO.fail("发布失败");
    if (moduleId != null && envId != null) {
      ModuleUser moduleUser = JwtUtil.getModuleUserInfo();
      //权限判断,判断用户在该环境下是否存在权限关联即可
      PermissionJudgeUtils.judgeUserPermission(userService,
          "module_update_image", moduleUser.getId(), envId);
      responseDTO = deployService.changeMirror(moduleId, envId, mirrorId, moduleUser, mirrorName);
    }
    return JSONObject.toJSONString(responseDTO);
  }
  
  /**
   * 根据moduleId对应的yaml文件里面的svc文件发布svc
   *
   * @param moduleId
   * @param envId
   * @return
   */
  @PostMapping(value = "/module/svc")
  public String createSvc(Integer moduleId, Integer envId) {
    // log.info("传入参数为moduleId: {},envId: {},", moduleId, envId);
    ResponseDTO responseDTO = new ResponseDTO();
    responseDTO.fail("创建svc失败");
    if (moduleId != null && envId != null) {
      ModuleUser moduleUser = JwtUtil.getModuleUserInfo();
      //权限判断,判断用户在该环境下是否存在权限关联即可
      PermissionJudgeUtils.judgeUserPermission(userService, "module_create_svc", moduleUser.getId(), envId);
      try {
        if (deployService.deploySvc(moduleId, envId, moduleUser)) {
          responseDTO.success("发布svc成功");
        }
      } catch (DeployIsOnlineExcetion | TransYaml2K8sVoException | K8SDeployException e) {
        responseDTO.fail(e.getMessage());
      }
    }
    return JSONObject.toJSONString(responseDTO);
  }
  
  /**
   * 删除上线的svc服务,如果svc未上线过则保持删除失败的信息
   *
   * @param moduleId
   * @param envId
   * @return
   */
  @PostMapping(value = "/module/delete/svc")
  public String delSvc(Integer moduleId, Integer envId) {
    //  log.info("传入参数为moduleId: {},envId: {},", moduleId, envId);
    ResponseDTO responseDTO = new ResponseDTO();
    responseDTO.fail("删除svc失败");
    if (moduleId != null && envId != null) {
      ModuleUser moduleUser = JwtUtil.getModuleUserInfo();
      //权限判断,判断用户在该环境下是否存在权限关联即可
      PermissionJudgeUtils.judgeUserPermission(userService,
          "module_delete_svc", moduleUser.getId(), envId);
      if (deployService.delSvc(moduleId, envId, moduleUser)) {
        responseDTO.success("删除svc");
      }
    }
    return JSONObject.toJSONString(responseDTO);
  }
  
  /**
   * 获取svc的信息
   */
  @GetMapping(value = "/module/svc/info")
  public String getSvcInfo(Integer moduleId, Integer envId) {
    //log.info("传入参数为moduleId: {},envId: {},", moduleId, envId);
    ResponseDTO responseDTO = new ResponseDTO();
    responseDTO.fail("暂无svc信息");
    if (moduleId != null && envId != null) {
      ModuleUser moduleUser = JwtUtil.getModuleUserInfo();
      log.info("用户信息:{}", JSONObject.toJSONString(moduleUser));
      //权限判断,判断用户在该环境下是否存在权限关联即可
      PermissionJudgeUtils.judgeUserPermission(userService,
          "module_svc_info_show", moduleUser.getId(), envId);
      K8sServiceDTO serviceDTO = deployService.getSvcInfo(moduleId, envId);
      responseDTO.success(serviceDTO);
    }
    return JSONObject.toJSONString(responseDTO);
  }
  
  /**
   * 删除pod
   *
   * @return
   */
  @PostMapping(value = "/module/del/pods")
  public String deletePod(Integer envId, Integer moduleId, String[] podNames) {
    //log.info("传入参数为podNames: {}", Arrays.toString(podNames));
    ResponseDTO responseDTO = new ResponseDTO();
    responseDTO.fail("操作失败");
    if (envId != null && moduleId != null && podNames != null && podNames.length > 0) {
      ModuleUser moduleUser = JwtUtil.getModuleUserInfo();
      //权限判断,判断用户在该环境下是否存在权限关联即可
      PermissionJudgeUtils.judgeUserPermission(userService,
          "module_delete_pod", moduleUser.getId(), envId);
      if (deployService.delPods(envId, moduleId, podNames, moduleUser)) {
        responseDTO.success("删除成功");
      }
    }
    return JSONObject.toJSONString(responseDTO);
  }
  
  /**
   * 针对pod清除缓存
   *
   * @param envId
   * @param moduleId
   * @param podNames
   * @return
   */
//    @PostMapping(value = "/clear/memory/pods")
  public String clearPodsMemorys(Integer envId, Integer moduleId, String[] podNames) {
    //log.info("传入参数为podNames: {}", Arrays.toString(podNames));
    ResponseDTO responseDTO = new ResponseDTO();
    responseDTO.fail("操作失败");
    if (envId != null && moduleId != null && podNames != null && podNames.length > 0) {
      ModuleUser moduleUser = JwtUtil.getModuleUserInfo();
      //权限判断,判断用户在该环境下是否存在权限关联即可
      PermissionJudgeUtils.judgeUserPermission(userService, null, moduleUser.getId(), envId);
      responseDTO.success(deployService.clearMemoryPods(envId, moduleId, podNames, moduleUser));
    }
    return JSONObject.toJSONString(responseDTO);
  }
  
  @PostMapping(value = "update")
  @ApiOperation("更新资源")
  public JsonResult<String> putResourceInfo(@RequestBody JSONObject yamlJson, Integer envId) {
    log.debug("yamlJson: {} ", yamlJson);
    K8sYamlDTO k8SYamlDTO = K8sUtils.transJson2Vo(yamlJson);
    deployService.updateResource(k8SYamlDTO, envId);
    return JsonResult.success("成功!");
  }
  
  /**
   * 获取deployment相关的yaml文件显示
   *
   * @param moduleId
   * @param envId
   * @return
   */
  @GetMapping(value = "/getDeploymentYaml")
  public String getDeploymentYaml(Integer moduleId, Integer envId) {
    //log.info("传入参数为 moduleId: {} ,envId: {}", moduleId, envId);
    ResponseDTO responseDTO = new ResponseDTO();
    responseDTO.fail("未能获取到信息");
    ModuleEnv moduleEnv = envMapper.selectOne(envId);
    List<ModuleDeployYaml> deployYamls = deployService.getModuleDeployByModuleAndEnvId(moduleId,
        envId, true);
    if (moduleEnv != null && deployYamls != null && deployYamls.size() > 0) {
      ModuleDeployYaml deployYaml = deployYamls.get(0);
      List<Integer> oldEnvList = Arrays.asList(oldEnvId);
      StringBuilder url = new StringBuilder();
      String s;
      if ("Deployment".equals(deployYaml.getYamlType())) {
        if (oldEnvList.contains(envId)) {
          url.append(EXTENSION_V1BETA1_PREFIX);
        } else {
          url.append(APPS_V1);
        }
        url.append(deployYaml.getYamlNamespace()).append(EXTENSION_DEPLOYMENTS)
            .append(deployYaml.getYamlName()).append("/status");
        s = K8sUtils.okhttpGetBack(moduleEnv.getK8sConfig()
            , url.toString());
        if (StringUtils.isNotBlank(s)) {
          responseDTO.success(JSONObject.parseObject(s));
        }
      }
    }
    return JSONObject.toJSONString(responseDTO);
  }
  
  /**
   * 获取pod相关的yaml文件显示
   *
   * @param moduleId
   * @param envId
   * @param podName
   * @return
   */
  @GetMapping(value = "/getPodYaml")
  public String getPodYaml(Integer moduleId, Integer envId, String podName) {
    log.info("getPodYaml传入参数为 moduleId: {} ,envId: {},podName:{}", moduleId, envId, podName);
    ResponseDTO responseDTO = new ResponseDTO();
    responseDTO.fail("未能获取到信息");
    List<ModuleDeployYaml> deployYamls = deployService.getModuleDeployByModuleAndEnvId(moduleId,
        envId, true);
    if (moduleId != null && envId != null && deployYamls != null && deployYamls.size() > 0 && StringUtils.isNotBlank(podName)) {
      ModuleEnv moduleEnv = envMapper.selectOne(envId);
      if (moduleEnv != null) {
        String s = K8sUtils.okhttpGetBack(moduleEnv.getK8sConfig(),
            "/api/v1/namespaces/" + deployYamls.get(0).getYamlNamespace() + "/pods/" + podName + "/status");
        if (StringUtils.isNotBlank(s)) {
          responseDTO.success(JSONObject.parseObject(s));
        }
      }
    }
    return JSONObject.toJSONString(responseDTO);
  }
  
  /**
   * 获取configMap列表信息
   *
   * @param moduleId
   * @param envId
   * @return
   */
  @GetMapping(value = "/getConfigMapYaml")
  public String getConfigMapYaml(Integer moduleId, Integer envId, String yamlNamespace) {
    log.info("getConfigMapYaml传入参数为 moduleId: {} ,envId: {}", moduleId, envId);
    ResponseDTO responseDTO = new ResponseDTO();
    responseDTO.fail("未能获取到信息");
    if (moduleId != null && envId != null) {
      ModuleUser moduleUser = JwtUtil.getModuleUserInfo();
      //权限判断,判断用户在该环境下是否存在权限关联即可
      PermissionJudgeUtils.judgeUserPermission(userService, "", moduleUser.getId(), envId);
      ModuleEnv moduleEnv = envMapper.selectOne(envId);
      if (moduleEnv != null) {
        String s = K8sUtils.okhttpGetBack(moduleEnv.getK8sConfig(),
            "/api/v1/namespaces/" + yamlNamespace + "/configmaps");
        if (StringUtils.isNotBlank(s)) {
          responseDTO.success(JSONObject.parseObject(s));
        }
      }
    }
    return JSONObject.toJSONString(responseDTO);
  }
  
  /**
   * 自动扩缩容的创建
   *
   * @param envId
   * @param moduleId
   * @param minReplicas
   * @param maxReplicas
   * @param cpuPercentage
   * @return
   */
  @PostMapping(value = "/autoScale/deployment")
  public String autoScale(Integer envId, Integer moduleId, Integer minReplicas, Integer maxReplicas, Integer cpuPercentage) {
    //log.info("传入参数为 moduleId: {} ,envId: {},minReplicas:{},maxReplicas:{},cpuPercentage:{}",
    //       moduleId, envId, minReplicas, maxReplicas, cpuPercentage);
    ResponseDTO responseDTO = new ResponseDTO();
    responseDTO.fail("创建失败");
    if (envId != null && moduleId != null && minReplicas > 0 && maxReplicas > 0
        && maxReplicas >= minReplicas && cpuPercentage > 0) {
      ModuleUser moduleUser = JwtUtil.getModuleUserInfo();
      //权限判断,判断用户在该环境下是否存在权限关联即可
      PermissionJudgeUtils.judgeUserPermission(userService,
          "module_auto_scale", moduleUser.getId(), envId);
      if (deployService.createAutoScaleSize(envId, moduleId,
          minReplicas, maxReplicas, cpuPercentage, moduleUser)) {
        responseDTO.success("创建成功--HPA");
      }
    }
    return JSONObject.toJSONString(responseDTO);
  }
  
  /**
   * 删除hpa 的自动扩缩容
   *
   * @param envId
   * @param moduleId
   * @return
   */
  @PostMapping(value = "/autoScale/deleteHpa")
  public String deleteAutoScale(Integer envId, Integer moduleId) {
    //log.info("传入参数为 moduleId: {} ,envId: {}", moduleId, envId);
    ResponseDTO responseDTO = new ResponseDTO();
    responseDTO.fail("删除失败");
    if (envId != null && moduleId != null) {
      ModuleUser moduleUser = JwtUtil.getModuleUserInfo();
      //权限判断,判断用户在该环境下是否存在权限关联即可
      PermissionJudgeUtils.judgeUserPermission(userService, "module_delete_auto_scale",
          moduleUser.getId(), envId);
      if (deployService.deleteAutoScaleHpa(envId, moduleId, moduleUser)) {
        responseDTO.success("删除成功");
      }
    }
    return JSONObject.toJSONString(responseDTO);
  }
  
  /**
   * 获取自动扩缩容的数据 hpa
   *
   * @param envId
   * @param moduleId
   * @return
   */
  @GetMapping(value = "/autoScale/getHpa")
  public String getAutoScaleHpa(Integer envId, Integer moduleId) {
    //log.info("传入参数为 moduleId: {} ,envId: {}", moduleId, envId);
    ResponseDTO responseDTO = new ResponseDTO();
    responseDTO.fail("获取失败");
    if (envId != null && moduleId != null) {
      ModuleUser moduleUser = JwtUtil.getModuleUserInfo();
      //权限判断,判断用户在该环境下是否存在权限关联即可
      PermissionJudgeUtils.judgeUserPermission(userService,
          "module_get_auto_scale", moduleUser.getId(), envId);
      String scaleHpa = deployService.getAutoScaleHpa(envId, moduleId);
      if (StringUtils.isNotBlank(scaleHpa)) {
        responseDTO.success(JSONObject.parseObject(scaleHpa));
      }
    }
    return JSONObject.toJSONString(responseDTO);
  }
  
  /**
   * 获取deployment的参数数据
   *
   * @param envId
   * @param moduleId
   * @param argsType
   * @return
   */
  @GetMapping(value = "/deployment/args")
  public String getOnlineDeploymentArgs(Integer envId, Integer moduleId, Integer argsType) {
    //log.info("传入参数为 moduleId: {} ,envId: {}", moduleId, envId);
    ResponseDTO responseDTO = new ResponseDTO();
    responseDTO.fail("获取失败");
    if (envId == null || moduleId == null || argsType == null ||
        K8sDeploymentResourceEnum.getByCode(argsType) == null) {
      responseDTO.argsNotOK();
    } else {
      ModuleUser moduleUser = JwtUtil.getModuleUserInfo();
      PermissionJudgeUtils.judgeUserPermission(userService,
          "module_resource_limit", moduleUser.getId(), envId);
      Map<String, String> argsMap = deployService.getDeploymentArgsByType(envId, moduleId, argsType);
      responseDTO.success(argsMap);
    }
    return JSONObject.toJSONString(responseDTO);
  }
  
  /**
   * 获取模块的挂载卷
   *
   * @param envId
   * @param moduleId
   * @return
   */
  @GetMapping(value = "/getVolumes")
  public String getModuleVolumes(Integer envId, Integer moduleId) {
    //log.info("传入参数为 moduleId:{} ,envId:{}",moduleId,envId);
    ResponseDTO responseDTO = new ResponseDTO();
    responseDTO.fail("获取失败");
    if (envId != null && moduleId != null) {
      List<Map<String, String>> result = deployService.getModuleVolumes(envId, moduleId);
      responseDTO.success(result);
    } else {
      responseDTO.argsNotOK();
    }
    return JSONObject.toJSONString(responseDTO);
  }
  
  /**
   * 热配置修改limit参数包含cpu和memory
   *
   * @param paramVO
   * @return
   */
  @PostMapping(value = "/hot/update/deployment")
  public String hotUpdateDeployments(K8sUpdateResourceParamVO paramVO) {
    //log.info("传入参数为 paramVo:{}", JSONObject.toJSONString(paramVO));
    ResponseDTO responseDTO = new ResponseDTO();
    responseDTO.fail("更改失败");
    if (paramVO.getEnvId() != null && paramVO.getModuleId() != null) {
      ModuleUser moduleUser = JwtUtil.getModuleUserInfo();
      PermissionJudgeUtils.judgeUserPermission(userService,
          "module_resource_limit", moduleUser.getId(), paramVO.getEnvId());
      //权限判断,判断用户在该环境下是否存在权限关联即可
      try {
        if (deployService.hotUpdateDeployments(paramVO.getEnvId(), paramVO.getModuleId(),
            moduleUser, paramVO.getArgsType(), paramVO)) {
          responseDTO.success("更改成功");
        }
      } catch (K8SDeployException e) {
        responseDTO.fail(e.getMessage());
      }
    } else {
      responseDTO.argsNotOK();
    }
    return JSONObject.toJSONString(responseDTO);
  }
  
  /**
   * 导出java的thread dump相关的日志
   *
   * @param podName
   * @param envId
   * @param moduleId
   */
  @GetMapping(value = "/threadDump/export")
  public void exportThreadDumpFile(String podName, Integer envId,
                                   String moduleId, HttpServletResponse response) {
    log.info("export dump传入参数为 moduleId: {} ,envId: {},podName:{}", moduleId,
        envId, podName);
    File file = deployService.getThreadDumpFileFromPod(podName, envId, moduleId);
    if (file != null) {
      response.setContentType("application/force-download");// 设置强制下载不打开
      response.addHeader("Content-Disposition", "attachment;fileName=" +
          file.getName());// 设置文件名
      FileInputStream inputStream = null;
      try {
        inputStream = new FileInputStream(file);
        IOUtils.copy(inputStream, response.getOutputStream());
        response.flushBuffer();
      } catch (IOException e) {
        log.error("文件下载出现错误", e);
      } finally {
        if (inputStream != null) {
          try {
            inputStream.close();
          } catch (IOException e) {
            e.printStackTrace();
          }
        }
      }
    }
  }
  
  /**
   * 替换yaml
   *
   * @param envId
   * @param moduleId
   * @param argsType
   * @return
   */
  @PostMapping(value = "/replace/yaml")
  public String replaceModuleYaml(Integer envId, Integer moduleId, Integer argsType) {
    //log.info("传入参数为 moduleId: {} ,envId: {} ,argsType: {}", moduleId, envId, argsType);
    ResponseDTO responseDTO = new ResponseDTO();
    responseDTO.fail("替换失败");
    if (envId == null || moduleId == null || argsType == null ||
        K8sKindTypeEnum.getEnumByCode(argsType) == null) {
      responseDTO.argsNotOK();
    } else {
      ModuleUser moduleUser = JwtUtil.getModuleUserInfo();
      PermissionJudgeUtils.judgeUserPermission(userService,
          "deploy_replace_yaml", moduleUser.getId(), envId);
      if (deployService.replaceModuleYaml(moduleId, envId, moduleUser, argsType)) {
        responseDTO.success("替换成功");
      }
    }
    return JSONObject.toJSONString(responseDTO);
  }
  
  /**
   * 比较本次上线配置与上次下线时的配置是否一致
   *
   * @param moduleId
   * @param envId
   * @return
   */
  @GetMapping(value = "/compare/yaml")
  public String compareYamlConfig(Integer moduleId, Integer envId) {
    log.info("compareYamlConfig传入参数moduleId:{},envId:{}", moduleId, envId);
    ResponseDTO responseDTO = new ResponseDTO();
    if (moduleId != null && envId != null) {
      responseDTO = deployService.compareYamlConfig(moduleId, envId);
    }
    return JSONObject.toJSONString(responseDTO);
  }
  
  /**
   * 查询yaml比对结果
   *
   * @return
   */
  @GetMapping(value = "/getYamlCompare")
  public String getYamlCompare() {
    ResponseDTO responseDTO = new ResponseDTO();
    List<ModuleYamlDiff> yamlCompareResult = deployService.getYamlCompareResult();
    responseDTO.success(yamlCompareResult);
    return JSONObject.toJSONString(responseDTO);
  }
  
  /**
   * 设置滚动升级参数
   *
   * @param moduleId
   * @param envId
   * @param k8sStrategyParamVO
   * @return
   */
  @PostMapping(value = "/changerollingUpdate")
  public String changeRollingUpdate(Integer moduleId, Integer envId,
                                    K8sStrategyParamVO k8sStrategyParamVO) {
    log.info("changeRollingUpdate传入参数moduleId:{},envId:{},param:{}",
        moduleId, envId, k8sStrategyParamVO);
    ResponseDTO responseDTO = new ResponseDTO();
    if (moduleId != null && envId != null && k8sStrategyParamVO.getMaxSurge() != null) {
      responseDTO = deployService.changeStrategyArgs(moduleId, envId, k8sStrategyParamVO);
    }
    return JSONObject.toJSONString(responseDTO);
  }
  
  @PostMapping(value = "/changerollingUpdate2")
  public String changeRollingUpdate(Integer moduleId, Integer envId,
                                    String maxUnavailable) {
    log.info("changeRollingUpdate传入参数moduleId:{},envId:{},param:{}",
        moduleId, envId, maxUnavailable);
    ResponseDTO responseDTO = new ResponseDTO();
    if (moduleId != null && envId != null && maxUnavailable != null) {
      responseDTO = deployService.changeStrategyArgs2(moduleId, envId, maxUnavailable);
    }
    return JSONObject.toJSONString(responseDTO);
  }
  
  /**
   * 获取滚动升级参数
   *
   * @param moduleId
   * @param envId
   * @return
   */
  @GetMapping(value = "/getStratgyArgs")
  public String getStratgyArgs(Integer moduleId, Integer envId) {
    log.info("getStratgyArgs传入参数moduleId:{},envid:{}", moduleId, envId);
    ResponseDTO responseDTO = new ResponseDTO();
    responseDTO.fail("获取失败");
    Map<String, Integer> map = new HashMap<>();
    if (moduleId != null && envId != null) {
      map = deployService.getStrategyArgs(moduleId, envId);
      if (map != null && map.size() > 0) responseDTO.success(map);
    }
    return JSONArray.toJSONString(map);
  }
}
