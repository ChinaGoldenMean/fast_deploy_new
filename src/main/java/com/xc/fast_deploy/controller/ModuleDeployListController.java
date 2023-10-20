package com.xc.fast_deploy.controller;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.xc.fast_deploy.dto.ResponseDTO;
import com.xc.fast_deploy.model.master_model.ModuleUser;
import com.xc.fast_deploy.myException.FileStoreException;
import com.xc.fast_deploy.service.common.ModuleDeployListService;
import com.xc.fast_deploy.service.common.ModuleUserService;
import com.xc.fast_deploy.shiro.token.JwtUtil;
import com.xc.fast_deploy.utils.PermissionJudgeUtils;
import com.xc.fast_deploy.vo.module_vo.ModuleEnvCenterManageVo;
import com.xc.fast_deploy.vo.module_vo.param.ModuleDeployPodCenterManageVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

//处理模块清单的接口入口 即前端页面的统一发布清单
@RestController
@RequestMapping(value = "/deploys")
@Slf4j
public class ModuleDeployListController {
  
  @Autowired
  private ModuleDeployListService moduleDeployListService;
  @Autowired
  private ModuleUserService userService;
  
  //批量添加进入模块清单
  @PostMapping(value = "/modules")
  public String addModuleDeploy2List(Integer envId, Integer[] moduleIds) {
    log.info("批量导入模块清单数据 参数 envId: {},moduleIds: {}", envId, Arrays.toString(moduleIds));
    ResponseDTO responseDTO = new ResponseDTO();
    responseDTO.fail("添加失败");
    if (envId == null || moduleIds == null || moduleIds.length <= 0) {
      responseDTO.argsNotOK();
    } else {
      String userId = JwtUtil.getUserIdFromToken((String) SecurityUtils.getSubject().getPrincipal());
      PermissionJudgeUtils.judgeUserPermission(userService,
          "publish_manage_module_insert_deploys", userId, envId);
      boolean success = moduleDeployListService.insert2RedisModuleDeployList(envId,
          userId, moduleIds);
      if (success) {
        responseDTO.success("添加成功");
      }
    }
    return JSONObject.toJSONString(responseDTO);
  }
  
  //全量获取清单数据
  @GetMapping(value = "/modules/list")
  public String getModuleDeployListSelf(Integer envId, Integer centerId, String keyName) {
    log.info("全量获取模块清单数据 参数 envId: {},centerId: {},keyName:{}",
        envId, centerId, keyName);
    ResponseDTO responseDTO = new ResponseDTO();
    responseDTO.fail("获取失败");
    String userId = JwtUtil.getUserIdFromToken((String) SecurityUtils.getSubject().getPrincipal());
    
    PermissionJudgeUtils.judgeUserPermission(userService, "deploy_list_menu", userId);
    
    Set<ModuleEnvCenterManageVo> ModuleEnvCenterManageVoList =
        moduleDeployListService.getAllDeployListSelf(userId, envId, centerId, keyName);
    responseDTO.success(ModuleEnvCenterManageVoList);
    return JSONObject.toJSONString(responseDTO);
  }
  
  //更新redis中相关的数据 遇到模块名更改 模块删除 中心名称修改 环境名称修改需要更新显示
  
  //批量从清单中移除
  @PostMapping(value = "/modules/list/remove")
  public String removeModuleDeployList(Integer[] moduleIds) {
    log.info("批量从待发布清单移除相关的模块 参数moduleIds: {}", Arrays.toString(moduleIds));
    ResponseDTO responseDTO = new ResponseDTO();
    responseDTO.fail("获取失败");
    
    String userId = JwtUtil.getUserIdFromToken((String) SecurityUtils.getSubject().getPrincipal());
    PermissionJudgeUtils.judgeUserPermission(userService,
        "deploy_list_remove_modules", userId);
    if (moduleDeployListService.removeModuleDeployList(moduleIds)) {
      responseDTO.success("移除成功");
    }
    
    return JSONObject.toJSONString(responseDTO);
  }
  
  //批量更换最新镜像
  @PostMapping(value = "/modules/replace/mirrors")
  public String replaceBatchMirrors(Integer[] moduleIds) {
    log.info("批量更新为最新镜像 参数moduleIds: {}", Arrays.toString(moduleIds));
    ResponseDTO responseDTO = new ResponseDTO();
    responseDTO.fail("获取失败");
    ModuleUser moduleUser = JwtUtil.getModuleUserInfo();
    
    PermissionJudgeUtils.judgeUserPermission(userService, "deploy_list_replace_mirrors",
        moduleUser.getId());
    
    Map<String, Object> resultMap = moduleDeployListService.replaceBatchMirrors(moduleUser, moduleIds);
    responseDTO.success(resultMap);
    return JSONObject.toJSONString(responseDTO);
  }
  
  //批量获取发布清单中所有的对应pod相关数据
  @GetMapping(value = "/modules/list/pods")
  public String getAllModulesListPods(Integer envId, Integer centerId, String keyName) {
    log.info("全量获取模块清单数据 参数 envId: {},centerId: {},keyName:{}",
        envId, centerId, keyName);
    ResponseDTO responseDTO = new ResponseDTO();
    responseDTO.fail("获取失败");
    String userId = JwtUtil.getUserIdFromToken((String) SecurityUtils.getSubject().getPrincipal());
    
    PermissionJudgeUtils.judgeUserPermission(userService,
        "deploy_list_menu", userId);
    List<ModuleDeployPodCenterManageVo> manageVoList =
        moduleDeployListService.getAllDeployListSelfPods(envId, userId, centerId, keyName);
    responseDTO.success(manageVoList);
    return JSONObject.toJSONString(responseDTO);
  }
  
  //从excel导入待发布模块到发布清单 region表示CRM域或者计费域
  @PostMapping(value = "/uploadExcel")
  public String batchAddModuleDeploy2List(String region, MultipartFile excelFile) {
    log.info("批量导入模块清单数据 参数 region: {},excelFile: {}", region, excelFile);
    ResponseDTO responseDTO = new ResponseDTO();
    List<Map<String, Object>> resultList = null;
    responseDTO.fail("添加失败");
    if (StringUtils.isBlank(region)) {
      responseDTO.argsNotOK();
    } else {
      String userId = JwtUtil.getUserIdFromToken((String) SecurityUtils.getSubject().getPrincipal());
      PermissionJudgeUtils.judgeUserPermission(userService,
          "publish_manage_module_insert_deploys", userId);
      try {
        resultList = moduleDeployListService.fromExcelInsertModuleDeployList(region,
            userId, excelFile);
        if (resultList.size() > 0) {
          responseDTO.success(resultList);
        } else {
          responseDTO.fail("excel文件解析出错，格式有误");
        }
      } catch (FileStoreException e) {
        responseDTO.fail(e.getMessage());
      }
    }
    return JSONObject.toJSONString(responseDTO, SerializerFeature.DisableCircularReferenceDetect);
  }
  
  //当用户不在发布清单发布时,判断该发布模块是否添加到发布清单
  
  @GetMapping(value = "/exportDeployList")
  public void exportDeployList(Integer[] moduleIds, HttpServletResponse response) {
    //权限验证
    PermissionJudgeUtils.judgeUserPermission(userService, "deploy_list_menu",
        JwtUtil.getUserIdFromToken((String) SecurityUtils.getSubject().getPrincipal()));
    //获取数据形成文件
    File excelFile = moduleDeployListService.exportModuleDeployList(moduleIds);
    //输出文件
    response.setContentType("application/force-download");// 设置强制下载不打开
    response.addHeader("Content-Disposition", "attachment;fileName=" +
        excelFile.getName());// 设置文件名
    FileInputStream inputStream = null;
    try {
      inputStream = new FileInputStream(excelFile);
      IOUtils.copy(inputStream, response.getOutputStream());
      response.flushBuffer();
    } catch (FileNotFoundException e) {
      log.error("文件下载出现错误fileNoteFound", e);
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
