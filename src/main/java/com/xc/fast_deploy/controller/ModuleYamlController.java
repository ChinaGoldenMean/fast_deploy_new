package com.xc.fast_deploy.controller;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.xc.fast_deploy.dto.MyPageInfo;
import com.xc.fast_deploy.dto.ResponseDTO;
import com.xc.fast_deploy.dto.module.ModuleDeployDTO;
import com.xc.fast_deploy.dto.module.ModuleDeployYamlDTO;
import com.xc.fast_deploy.dto.module.ModuleManageDTO;
import com.xc.fast_deploy.model.master_model.ModuleDeploy;
import com.xc.fast_deploy.myException.DeployIsOnlineExcetion;
import com.xc.fast_deploy.myException.FileStoreException;
import com.xc.fast_deploy.myException.TransYaml2K8sVoException;
import com.xc.fast_deploy.myException.UnauthorizedException;
import com.xc.fast_deploy.myenum.YamlFileTypeEnum;
import com.xc.fast_deploy.service.common.ModuleDeployService;
import com.xc.fast_deploy.service.common.ModuleDeployYamlService;
import com.xc.fast_deploy.service.common.ModuleJobService;
import com.xc.fast_deploy.service.common.ModuleUserService;
import com.xc.fast_deploy.shiro.token.JwtUtil;
import com.xc.fast_deploy.utils.PermissionJudgeUtils;
import com.xc.fast_deploy.utils.SessionCookieUtils;
import com.xc.fast_deploy.vo.k8s_vo.K8sResourceVo;
import com.xc.fast_deploy.vo.module_vo.DeployModuleVo;
import com.xc.fast_deploy.vo.module_vo.param.ModuleDeployYamlSelectParamVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping(value = "/deploy")
@Slf4j
public class ModuleYamlController {
  
  @Autowired
  private ModuleJobService jobService;
  
  @Autowired
  private ModuleDeployYamlService deployYamlService;
  
  @Autowired
  private ModuleDeployService moduleDeployService;
  
  @Autowired
  private ModuleUserService userService;
  
  @Value("${file.storge.path.prefix}")
  private String storgePrefix;
  
  /**
   * 根据环境id 获取已经有构建过的模块列表  并且未添加过对应发布信息的
   *
   * @return
   */
  @GetMapping(value = "/module/env")
  public String getJobModuleByEnvId(Integer envId) {
    ResponseDTO responseDTO = new ResponseDTO();
    responseDTO.fail("暂无数据");
    //首先获取权限数据,然后根据权限数据去判断
    PermissionJudgeUtils.judgeUserPermission(userService, null,
        JwtUtil.getUserIdFromToken((String) SecurityUtils.getSubject().getPrincipal()), envId);
    //默认用户对应环境的模块列表
    List<ModuleManageDTO> manageDTOList = jobService.selectJobModuleByEnvId(envId);
    if (manageDTOList != null && manageDTOList.size() > 0) {
      responseDTO.success(manageDTOList);
    }
    return JSONObject.toJSONString(responseDTO);
  }
  
  /**
   * 添加yaml文件对应的信息
   *
   * @param deployModuleVo
   * @param yamlFile
   * @param yamlPath
   * @param svcYamlFile
   * @param svcYamlPath
   * @return
   */
  @PostMapping(value = "/create/yaml")
  public String createDeployModule(DeployModuleVo deployModuleVo, MultipartFile yamlFile, String yamlPath,
                                   MultipartFile svcYamlFile, String svcYamlPath) {
    log.info("输入参数: deployModuleVo: {},yamlPath: {}", JSONObject.toJSONString(deployModuleVo), yamlPath);
    ResponseDTO responseDTO = new ResponseDTO();
    responseDTO.fail("建立发布失败");
    if (validate(deployModuleVo, yamlFile, yamlPath)) {
      PermissionJudgeUtils.judgeUserPermission(userService, "publish_manage_add",
          JwtUtil.getUserIdFromToken((String) SecurityUtils.getSubject().getPrincipal()),
          deployModuleVo.getEnvId());
      try {
        if (deployYamlService.createDeployModuleYaml(deployModuleVo, yamlFile, yamlPath,
            svcYamlFile, svcYamlPath)) {
          responseDTO.success("建立发布成功");
        }
      } catch (FileStoreException e) {
        responseDTO.fail(e.getMessage());
      } catch (TransYaml2K8sVoException e) {
        log.info("yaml文件格式错误");
        responseDTO.fail(e.getMessage());
      }
    } else {
      responseDTO.fail("输入参数校验失败");
    }
    return JSONObject.toJSONString(responseDTO);
  }
  
  /**
   * 修改重新上传或者重新指定的yaml文件
   *
   * @param deployModuleVo
   * @param yamlFile
   * @param yamlPath
   * @return
   */
  @PostMapping(value = "/update/yaml")
  public String updateDeployYaml(DeployModuleVo deployModuleVo, MultipartFile yamlFile,
                                 String yamlPath, MultipartFile svcYamlFile, String svcYamlPath) {
    log.info("输入参数: deployModuleVo: {},yamlPath: {}",
        JSONObject.toJSONString(deployModuleVo), yamlPath);
    ResponseDTO responseDTO = new ResponseDTO();
    responseDTO.fail("更新失败");
    if (deployModuleVo != null && deployModuleVo.getDeployYamlId() != null) {
      Integer envId = deployYamlService.selectEnvIdByYamlId(deployModuleVo.getDeployYamlId());
      if (envId != null) {
        PermissionJudgeUtils.judgeUserPermission(userService,
            "publish_manage_update",
            JwtUtil.getUserIdFromToken((String) SecurityUtils.getSubject().getPrincipal()),
            envId);
        try {
          responseDTO = deployYamlService.updateDeployModuleYaml(deployModuleVo,
              yamlFile, yamlPath, svcYamlFile, svcYamlPath);
        } catch (FileStoreException e) {
          responseDTO.fail(e.getMessage());
        } catch (TransYaml2K8sVoException e) {
          log.error("yaml文件格式错误");
          responseDTO.fail(e.getMessage());
        }
      }
    }
    return JSONObject.toJSONString(responseDTO);
  }
  
  /**
   * 模块发布配置列表页面的分页显示
   *
   * @param pageNum
   * @param pageSize
   * @param yamlSelectParamVo
   * @return
   */
  @GetMapping(value = "/pageAll")
  public String getDeployYamlPageInfo(
      @RequestParam(value = "pageNum", defaultValue = "1", required = false) int pageNum,
      @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize,
      ModuleDeployYamlSelectParamVo yamlSelectParamVo) {
    
    //log.info("传入参数: pageNum: {} , pageSize: {} , 查询查询参数: {}",
    //      pageNum, pageSize, JSONObject.toJSON(yamlSelectParamVo));
    ResponseDTO responseDTO = new ResponseDTO();
    responseDTO.fail("获取数据失败");
    Map<Integer, Set<String>> envPermissionMap =
        userService.selectEnvPermissionByUserId(JwtUtil.getUserIdFromToken(
            (String) SecurityUtils.getSubject().getPrincipal()));
    boolean flag = false;
    if (envPermissionMap.size() > 0) {
      if (yamlSelectParamVo.getEnvId() != null) {
        if (envPermissionMap.containsKey(yamlSelectParamVo.getEnvId())) {
          flag = true;
        }
      } else {
        yamlSelectParamVo.setEnvIds(envPermissionMap.keySet());
        flag = true;
      }
    }
    if (!flag) {
      throw new UnauthorizedException();
    }
    
    MyPageInfo<ModuleDeployYamlDTO> myPageInfo =
        deployYamlService.selectDeployYamlAll(pageNum, pageSize, yamlSelectParamVo);
    
    if (myPageInfo != null && myPageInfo.getList() != null && myPageInfo.getList().size() > 0) {
      for (ModuleDeployYamlDTO moduleDeployYamlDTO : myPageInfo.getList()) {
        if (StringUtils.isNotBlank(moduleDeployYamlDTO.getYamlJson())) {
          moduleDeployYamlDTO.setCanEditYaml(true);
        } else {
          moduleDeployYamlDTO.setCanEditYaml(false);
        }
      }
      responseDTO.success(myPageInfo);
    }
    
    return JSONObject.toJSONString(responseDTO);
  }
  
  /**
   * 根据yaml文件存储的id删除 如果该yaml文件对应的是上线yaml则所有yaml文件信息全部删除
   *
   * @param deployYamlId
   * @return
   */
  @PostMapping(value = "/delete/yaml")
  public String deleteDeploy(Integer deployYamlId) {
    log.info("deleteDeploy传入参数 deployYamlId: {}", deployYamlId);
    ResponseDTO responseDTO = new ResponseDTO();
    responseDTO.fail("删除发布失败");
    if (deployYamlId != null) {
      Integer envId = deployYamlService.selectEnvIdByYamlId(deployYamlId);
      if (envId != null) {
        PermissionJudgeUtils.judgeUserPermission(userService, "publish_manage_delete",
            JwtUtil.getUserIdFromToken((String) SecurityUtils.getSubject().getPrincipal()), envId);
        try {
          responseDTO = deployYamlService.deleteByYamlId(deployYamlId);
        } catch (DeployIsOnlineExcetion e) {
          responseDTO.fail(e.getMessage());
        }
      }
    }
    return JSONObject.toJSONString(responseDTO);
  }
  
  /**
   * 查看yaml文件详情的接口
   */
  @GetMapping(value = "/getOneInfo/yaml")
  public String getOneYamlInfo(Integer deployId) {
    log.info("getOneYamlInfo 参数 deployId: {}", deployId);
    ResponseDTO responseDTO = new ResponseDTO();
    responseDTO.fail("暂无数据");
    if (deployId != null) {
      ModuleDeploy moduleDeploy = moduleDeployService.selectById(deployId);
      if (moduleDeploy != null) {
        PermissionJudgeUtils.judgeUserPermission(userService, "publish_manage_details",
            JwtUtil.getUserIdFromToken((String) SecurityUtils.getSubject().getPrincipal()), moduleDeploy.getEnvId());
        ModuleDeployDTO moduleDeployDTO = deployYamlService.getOneYamlInfoById(deployId);
        responseDTO.success(moduleDeployDTO);
      }
    }
    return JSONObject.toJSONString(responseDTO);
  }
  
  /**
   * 预览yaml文件
   */
  @GetMapping(value = "/getInfo/yaml")
  public String getYamlInfo(Integer deployYamlId) {
    log.info("getYamlInfo传入参数 deployYamlId: {}", deployYamlId);
    ResponseDTO responseDTO = new ResponseDTO();
    responseDTO.fail("获取数据失败");
    if (deployYamlId != null) {
      Integer envId = deployYamlService.selectEnvIdByYamlId(deployYamlId);
      if (envId != null) {
        PermissionJudgeUtils.judgeUserPermission(userService, "publish_manage_yaml_show",
            JwtUtil.getUserIdFromToken((String) SecurityUtils.getSubject().getPrincipal()), envId);
        Object o = deployYamlService.getYamlInfoById(deployYamlId);
        responseDTO.success(o);
      }
    }
    return JSONObject.toJSONString(responseDTO);
  }
  
  /**
   * 生成yaml文件  与模块相关的 创建全新的
   *
   * @return
   */
  @PostMapping(value = "/yaml/json")
  public String configYaml(@RequestBody K8sResourceVo resourceVo) {
    log.info("configYaml参数: envId: {} moduleId :{}", resourceVo.getEnvId(), resourceVo.getModuleId());
    log.info("json数据接收: {}", JSONObject.toJSONString(resourceVo));
    ResponseDTO responseDTO = new ResponseDTO();
    responseDTO.argsNotOK();
    //获取json数据串 //
    if (resourceVo.getEnvId() != null && resourceVo.getModuleId() != null
        && resourceVo.getType() != null && resourceVo.getDeployResource() != null
        && resourceVo.getIsNeedSvc() != null) {
      PermissionJudgeUtils.judgeUserPermission(userService, "",
          JwtUtil.getUserIdFromToken((String) SecurityUtils.getSubject().getPrincipal()), resourceVo.getEnvId());
      //保存资源的resource信息
      try {
        if (deployYamlService.saveYamlJson(resourceVo)) {
          responseDTO.success("操作成功");
        }
      } catch (TransYaml2K8sVoException e) {
        responseDTO.fail(e.getMessage());
      }
    }
    
    return JSONObject.toJSONString(responseDTO);
  }
  
  /**
   * 编辑更改yaml文件
   *
   * @return
   */
  @PostMapping(value = "/yaml/json/update")
  public String updateConfigYaml(@RequestBody K8sResourceVo resourceVo) {
    log.info("configYaml参数: envId: {} moduleId :{}", resourceVo.getEnvId(), resourceVo.getModuleId());
    log.info("json数据接收: {}", JSONObject.toJSONString(resourceVo));
    ResponseDTO responseDTO = new ResponseDTO();
    responseDTO.argsNotOK();
    //获取json数据串 //
    if (resourceVo.getEnvId() != null && resourceVo.getModuleId() != null
        && resourceVo.getType() != null && resourceVo.getDeployResource() != null
        && resourceVo.getIsNeedSvc() != null) {
      PermissionJudgeUtils.judgeUserPermission(userService, "",
          JwtUtil.getUserIdFromToken((String)
              SecurityUtils.getSubject().getPrincipal()), resourceVo.getEnvId());
      //保存资源的resource信息
      try {
        synchronized (this) {
          if (deployYamlService.updateDeployModuleJSON(resourceVo)) {
            responseDTO.success("操作成功");
          }
        }
        
      } catch (TransYaml2K8sVoException e) {
        responseDTO.fail(e.getMessage());
      }
    }
    return JSONObject.toJSONString(responseDTO);
  }
  
  /**
   * 获取模块对应的json数据 type类型如下code为整型
   * DEPLOYMENT(1, "Deployment"),
   * POD(2, "pod"),
   * SERVICE(3, "Service"),
   * DAEMONSET(4, "DaemonSet"),
   * REPLICASET(6, "ReplicaSet"),
   * REPLICATIONCONTROLLER(7, "ReplicationController"),
   * INGRESS(8, "Ingress");
   *
   * @param envId    环境id
   * @param moduleId 模块id
   * @param type     类型
   * @return
   */
  @GetMapping(value = "/yaml/json")
  public String getYamlJson(Integer envId, Integer moduleId, Integer type) {
    log.info("参数接收: envId:{},moduleId:{},type:{}", envId, moduleId, type);
    ResponseDTO responseDTO = new ResponseDTO();
    responseDTO.argsNotOK();
    if (envId != null && moduleId != null && type != null) {
      PermissionJudgeUtils.judgeUserPermission(userService, "",
          JwtUtil.getUserIdFromToken((String) SecurityUtils.getSubject().getPrincipal()), envId);
      responseDTO.fail();
      try {
        Object o = deployYamlService.getYamlJsonInfo(envId, moduleId, type);
        if (o != null) {
          responseDTO.success(o);
        }
      } catch (IOException | TransYaml2K8sVoException e) {
        e.printStackTrace();
        responseDTO.fail(e.getMessage());
      }
    }
    return new Gson().toJson(responseDTO);
  }
  
  /**
   * 获取某个环境的所有configMap
   *
   * @param envId
   * @return
   */
  @GetMapping(value = "/configMaps")
  public String getAllConfigMaps(Integer envId) {
    ResponseDTO responseDTO = new ResponseDTO();
    responseDTO.fail("获取数据失败");
    if (envId != null) {
      PermissionJudgeUtils.judgeUserPermission(userService, "publish_manage_yaml_show",
          JwtUtil.getUserIdFromToken((String) SecurityUtils.getSubject().getPrincipal()), envId);
      Map<String, Set<String>> configMaps = moduleDeployService.getAllConfigMaps(envId);
      responseDTO.success(configMaps);
    }
    return JSONObject.toJSONString(responseDTO);
  }
  
  /**
   * 校验上传数据的正确性
   *
   * @param yamlFile
   * @param yamlPath
   * @return
   */
  private boolean validate(DeployModuleVo deployModuleVo, MultipartFile yamlFile, String yamlPath) {
    if (deployModuleVo != null && deployModuleVo.getYamlFileType() != null
        && deployModuleVo.getModuleId() != null
        && deployModuleVo.getEnvId() != null) {
      YamlFileTypeEnum yamlFileTypeEnum =
          YamlFileTypeEnum.getTypeByCode(deployModuleVo.getYamlFileType());
      if (yamlFileTypeEnum != null) {
        switch (yamlFileTypeEnum) {
          case YAML_FILE_APPOINT:
            if (StringUtils.isNotBlank(yamlPath) &&
                new File(storgePrefix + yamlPath).exists()) {
              return true;
            }
            break;
          case YAML_FILE_UPLOAD:
            if (yamlFile != null && !yamlFile.isEmpty()) {
              return true;
            }
            break;
        }
      }
    }
    return false;
  }
}
