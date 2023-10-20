package com.xc.fast_deploy.controller;

import com.alibaba.fastjson.JSONObject;
import com.xc.fast_deploy.annotation.JudgePermission;
import com.xc.fast_deploy.dto.ResponseDTO;
import com.xc.fast_deploy.dto.module.ModuleEnvDTO;
import com.xc.fast_deploy.model.master_model.ModuleEnv;
import com.xc.fast_deploy.myException.UnauthorizedException;
import com.xc.fast_deploy.service.common.ModuleEnvService;
import com.xc.fast_deploy.service.common.ModuleUserService;
import com.xc.fast_deploy.service.permission.PModuleUserService;
import com.xc.fast_deploy.shiro.token.JwtUtil;
import com.xc.fast_deploy.utils.PermissionJudgeUtils;
import com.xc.fast_deploy.vo.module_vo.PsPaasEnvVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping(value = "/moduleEnv")
@Slf4j
public class ModuleEnvController {
  
  @Autowired
  private ModuleEnvService moduleEnvService;
  
  @Autowired
  private ModuleUserService userService;
  
  /**
   * 查询paas的所有数据
   * 只有超级管理员才有权限查看所有环境信息,添加环境的时候用的
   *
   * @return
   */
  @GetMapping(value = "/selectPsAll")
  public String selectPsAll() {
    ResponseDTO responseDTO = new ResponseDTO();
    List<PsPaasEnvVo> envVoList = moduleEnvService.selectPsAll();
//        List<ModuleEnv> envList = userService.selectPsPermissionByUserId(JwtUtil.getUserIdFromToken(
//                (String) SecurityUtils.getSubject().getPrincipal()));
    responseDTO.success(envVoList);
    return JSONObject.toJSONString(responseDTO);
  }
  
  /**
   * 获取所有环境的列表
   *
   * @param envId
   * @return
   */
  @GetMapping(value = "/getEnvAllNotAuth")
  public String getEnvAllNotAuto(Integer envId) {
    ResponseDTO responseDTO = new ResponseDTO();
    PermissionJudgeUtils.judgeUserPermission(userService, "",
        JwtUtil.getUserIdFromToken((String) SecurityUtils.getSubject().getPrincipal()), envId);
    List<ModuleEnv> allModuleEnv = moduleEnvService.getAllModuleEnv(null);
    if (allModuleEnv != null && allModuleEnv.size() > 0) {
      responseDTO.success(allModuleEnv);
    }
    return JSONObject.toJSONString(responseDTO);
  }
  
  /**
   * 查询env的所有数据
   *
   * @return
   */
  @GetMapping(value = "/selectEnvAll")
  public String selectEnvAll() {
    ResponseDTO responseDTO = new ResponseDTO();
    Map<Integer, Set<String>> envPermissionMap =
        userService.selectEnvPermissionByUserId(JwtUtil.getUserIdFromToken(
            (String) SecurityUtils.getSubject().getPrincipal()));
    if (envPermissionMap.size() <= 0) {
      throw new UnauthorizedException();
    }
    List<ModuleEnv> moduleEnvs = moduleEnvService.selectEnvAll(envPermissionMap.keySet());
    responseDTO.success(moduleEnvs);
    return JSONObject.toJSONString(responseDTO);
  }
  
  /**
   * 插入一条moduleEnv
   *
   * @param moduleEnv 实体类
   * @return
   */
  @PostMapping(value = "/insertOneEnv")
  @JudgePermission(permissionName = "base_config_env_add")
  public String insertOneEnv(ModuleEnv moduleEnv) {
    log.info("ModuleEnv调用添加环境模块,入参:" + JSONObject.toJSONString(moduleEnv));
    ResponseDTO responseDTO = new ResponseDTO();
    responseDTO.fail("操作失败");
    if (moduleEnv != null &&
        StringUtils.isNotBlank(moduleEnv.getEnvName())
        && StringUtils.isNotBlank(moduleEnv.getK8sConfig())
        && StringUtils.isNotBlank(moduleEnv.getHarborUrl())
        && moduleEnv.getCertificateId() != null) {
      if (moduleEnvService.insertOneEnv(moduleEnv)) {
        responseDTO.success("环境模块添加成功");
        //清除原有的环境对应的权限信息
//                userService.deletePermissionByUserId(JwtUtil.getUserIdFromToken(
//                        (String) SecurityUtils.getSubject().getPrincipal()));
      }
    } else {
      responseDTO.fail("参数校验失败");
    }
    return JSONObject.toJSONString(responseDTO);
  }
  
  /**
   * 根据环境id查询一条数据
   *
   * @param id
   * @return
   */
  @GetMapping(value = "/selectOne")
  public String selectOne(Integer id) {
    ResponseDTO responseDTO = new ResponseDTO();
    responseDTO.fail("暂无数据");
    if (id != null) {
      //权限判断,判断用户在该环境下是否存在权限关联即可
      PermissionJudgeUtils.judgeUserPermission(userService, "base_config_env_details",
          JwtUtil.getUserIdFromToken((String) SecurityUtils.getSubject().getPrincipal()), id);
      ModuleEnvDTO moduleEnvDTO = moduleEnvService.selectCerEnvInfoById(id);
      responseDTO.success(moduleEnvDTO);
    }
    return JSONObject.toJSONString(responseDTO);
  }
  
  /**
   * 更新一条moduleEnv
   *
   * @param moduleEnv
   * @return
   */
  
  @PostMapping(value = "/update")
  public String update(ModuleEnv moduleEnv) {
    log.info("ModuleEnv调用更新环境模块,入参:" + JSONObject.toJSONString(moduleEnv));
    ResponseDTO responseDTO = new ResponseDTO();
    responseDTO.fail("更新失败");
    if (moduleEnv.getId() != null) {
      PermissionJudgeUtils.judgeUserPermission(userService, "base_config_env_update",
          JwtUtil.getUserIdFromToken((String) SecurityUtils.getSubject().getPrincipal()), moduleEnv.getId());
      if (moduleEnvService.updateAll(moduleEnv)) {
        responseDTO.success("环境模块更新成功");
      }
    }
    return JSONObject.toJSONString(responseDTO);
  }
  
  /**
   * 根据id删除一条数据
   *
   * @param id
   * @return
   */
  @PostMapping(value = "/delectOne")
  public String delectOne(Integer id) {
    ResponseDTO responseDTO = new ResponseDTO();
    responseDTO.fail("删除失败");
    PermissionJudgeUtils.judgeUserPermission(userService, "base_config_env_delete",
        JwtUtil.getUserIdFromToken((String) SecurityUtils.getSubject().getPrincipal()), id);
    if (moduleEnvService.delectOne(id)) {
      responseDTO.success("环境模块删除成功");
      //清除原有的环境对应的权限信息
      userService.deletePermissionByUserId(JwtUtil.getUserIdFromToken(
          (String) SecurityUtils.getSubject().getPrincipal()));
    }
    return JSONObject.toJSONString(responseDTO);
  }
  
  /**
   * 查询所有数据,用于环境列表的展示
   *
   * @return
   */
  @GetMapping(value = "/getAllModuleEnv")
  public String getAllModuleEnv() {
    ResponseDTO responseDTO = new ResponseDTO();
    responseDTO.fail("暂无数据");

//        Map<Integer, Set<String>> envPermissionMap = userService.selectEnvPermissionByUserId(JwtUtil.getUserIdFromToken(
//                (String) SecurityUtils.getSubject().getPrincipal()));
//        if (envPermissionMap.size() <= 0) {
//            throw new UnauthorizedException();
//        }
    List<ModuleEnv> moduleEnvList = moduleEnvService.getAllModuleEnv(null);
    if (moduleEnvList != null && moduleEnvList.size() > 0) {
      responseDTO.success(moduleEnvList);
    }
    return JSONObject.toJSONString(responseDTO);
  }
  
}
