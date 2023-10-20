package com.xc.fast_deploy.controller;

import com.alibaba.fastjson.JSONObject;
import com.xc.fast_deploy.dto.ResponseDTO;
import com.xc.fast_deploy.dto.CodeUpdateInfoDTO;
import com.xc.fast_deploy.dto.module.ModulePackageDTO;
import com.xc.fast_deploy.dto.module.permission.ModuleSubPackageDTO;
import com.xc.fast_deploy.model.master_model.ModuleManage;
import com.xc.fast_deploy.model.master_model.ModulePackage;
import com.xc.fast_deploy.myException.GitOwnException;
import com.xc.fast_deploy.myException.NotDeleteOkException;
import com.xc.fast_deploy.myException.SvnUpdateException;
import com.xc.fast_deploy.myenum.ModuleTypeEnum;
import com.xc.fast_deploy.service.common.ModuleManageService;
import com.xc.fast_deploy.service.common.ModulePackageService;
import com.xc.fast_deploy.service.common.ModuleUserService;
import com.xc.fast_deploy.shiro.token.JwtUtil;
import com.xc.fast_deploy.utils.PermissionJudgeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.tmatesoft.svn.core.SVNException;

import java.io.FileNotFoundException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

@RestController
@RequestMapping(value = "/module/package")
@Slf4j
public class ModulePackageController {
  
  @Autowired
  private ModulePackageService packageService;
  @Autowired
  private ModuleUserService userService;
  @Autowired
  private ModuleManageService manageService;
  
  /**
   * 根据模块id查询该模块包含的目录详情
   * module_manage_details 模块管理详情
   *
   * @param moduleId 模块id
   * @return
   */
  @GetMapping(value = "/getByModuleId")
  public String getByModuleId(Integer moduleId) {
    log.info("入参 moduleId: {}", moduleId);
    ResponseDTO responseDTO = new ResponseDTO();
    responseDTO.fail("未查询到数据");
    if (moduleId != null) {
      ModuleManage moduleManage = manageService.selectById(moduleId);
      if (moduleManage != null) {
        PermissionJudgeUtils.judgeUserPermission(userService, "module_manage_details",
            JwtUtil.getUserIdFromToken((String) SecurityUtils.getSubject().getPrincipal()), moduleManage.getEnvId());
        List<ModulePackageDTO> packageDTOS = packageService.selectByModuleId(moduleId);
        responseDTO.success(packageDTOS);
      }
    }
    
    return JSONObject.toJSONString(responseDTO);
  }
  
  /**
   * 新增一个package记录
   *
   * @param modulePackage package内容
   * @param moduleType    模块类型
   * @return json信息
   */
  @PostMapping(value = "/insertPackageInfo")
  public String addPackageInfo(@RequestBody ModuleSubPackageDTO modulePackage) {
    //添加一条记录
    log.info("addPackageInfo入参:  moduleId: {}, moduleType: {},param: {}", modulePackage.getModuleId(), modulePackage.getModuleType(),
        JSONObject.toJSONString(modulePackage));
    Integer moduleType = modulePackage.getModuleType();
    ResponseDTO responseDTO = new ResponseDTO();
    responseDTO.fail("添加失败");
    if (moduleType == null || modulePackage.getModuleId() == null || ModuleTypeEnum.getTypeByCode(moduleType) == null) {
      responseDTO.fail("参数不符合校验");
      return JSONObject.toJSONString(responseDTO);
    }
    ModuleManage moduleManage = manageService.selectById(modulePackage.getModuleId());
    if (moduleManage != null) {
      PermissionJudgeUtils.judgeUserPermission(userService,
          "module_manage_package_add",
          JwtUtil.getUserIdFromToken((String) SecurityUtils.getSubject().getPrincipal()),
          moduleManage.getEnvId());
      try {
        if (packageService.insertInfo(modulePackage)) {
          responseDTO.success("添加成功");
        }
      } catch (SvnUpdateException e) {
        responseDTO.fail(e.getMessage());
      }
    }
    
    return JSONObject.toJSONString(responseDTO);
  }
  
  /**
   * 删除某个package记录
   *
   * @param packageId
   * @return
   */
  @PostMapping(value = "/delPackageInfo")
  public String delPackageInfo(@RequestParam(value = "packageId") Integer packageId) {
    log.info("入参: packageId: {}", packageId);
    ResponseDTO responseDTO = new ResponseDTO();
    responseDTO.fail("删除失败");
    if (packageId != null) {
      Integer envId = packageService.selectEnvIdByPackageId(packageId);
      if (envId != null) {
        PermissionJudgeUtils.judgeUserPermission(userService, "module_manage_package_delete",
            JwtUtil.getUserIdFromToken((String) SecurityUtils.getSubject().getPrincipal()), envId);
        try {
          if (packageService.deletePackageInfo(packageId)) {
            responseDTO.success("删除成功");
          }
        } catch (NotDeleteOkException e) {
          responseDTO.fail(e.getMessage());
        }
      }
    }
    return JSONObject.toJSONString(responseDTO);
  }
  
  /**
   * 更新package记录
   *
   * @param packageId
   * @param codeUrl
   * @param contentName
   * @return
   */
  @PostMapping(value = "/updatePackageInfo")
  public String updatePackageInfo(@RequestParam(value = "packageId") Integer packageId,
                                  @RequestParam(value = "codeUrl") String codeUrl,
                                  @RequestParam(value = "contentName") String contentName,
                                  String gitBranch) {
    log.info("updatePackage入参: packageId: " + packageId + " codeUrl: " + codeUrl + " contentName: " + contentName);
    ResponseDTO responseDTO = new ResponseDTO();
    responseDTO.fail("更新失败");
    //更改package记录只能是以一种方式更改,也只能更改svnUrl和contentName
    if (packageId != null) {
      Integer envId = packageService.selectEnvIdByPackageId(packageId);
      if (envId != null) {
        PermissionJudgeUtils.judgeUserPermission(userService, "module_manage_svn_update",
            JwtUtil.getUserIdFromToken((String) SecurityUtils.getSubject().getPrincipal()), envId);
        try {
          if (packageService.updatePackageInfo(packageId, codeUrl, contentName, gitBranch)) {
            responseDTO.success("更新成功");
          }
        } catch (SvnUpdateException e) {
          responseDTO.fail(e.getMessage());
        }
      }
    }
    return JSONObject.toJSONString(responseDTO);
  }
  
  /**
   * 根据svn地址查看日志log信息,暂时未加任何权限
   * get
   *
   * @return
   */
  @RequestMapping(value = "/getCodeLogInfo")
  public String getBySvnUrl(@RequestParam(value = "packageId") Integer packageId) {
    ResponseDTO responseDTO = new ResponseDTO();
    responseDTO.fail("未能获取到数据");
    if (packageId != null) {
      Integer envId = packageService.selectEnvIdByPackageId(packageId);
      if (envId != null) {
        PermissionJudgeUtils.judgeUserPermission(userService, "module_manage_svn_log_show",
            JwtUtil.getUserIdFromToken((String) SecurityUtils.getSubject().getPrincipal()), envId);
        List<CodeUpdateInfoDTO> updateInfoDTOS = packageService.getCodeUpdateInfo(packageId);
        if (updateInfoDTOS != null && updateInfoDTOS.size() > 0) {
          responseDTO.success(updateInfoDTOS);
        }
      }
    }
    return JSONObject.toJSONString(responseDTO);
  }
  
  /**
   * 针对模块字目录package更换版本
   *
   * @param packageId
   * @param reversion
   * @return
   */
  @PostMapping(value = "/updateReversion")
  public String updatePackageReversion(Integer packageId, String reversion) {
    log.info("updatePackageReversion 参数接收: packageId: {}, reversion:{}", packageId, reversion);
    ResponseDTO responseDTO = new ResponseDTO();
    responseDTO.fail("更改失败");
    if (packageId != null && StringUtils.isNotBlank(reversion)) {
      Integer envId = packageService.selectEnvIdByPackageId(packageId);
      if (envId != null) {
        PermissionJudgeUtils.judgeUserPermission(userService, "module_manage_svn_version",
            JwtUtil.getUserIdFromToken((String) SecurityUtils.getSubject().getPrincipal()), envId);
        try {
          if (packageService.updatePackageReversion(packageId, reversion)) {
            responseDTO.success("更改成功");
          }
        } catch (SvnUpdateException e) {
          responseDTO.fail(e.getMessage());
        }
      }
    }
    return JSONObject.toJSONString(responseDTO);
  }
  
  /**
   * 更新某个模块下的所有代码
   *
   * @return
   */
  @PostMapping(value = "/updateAllCode")
  public String updateAllCode(Integer moduleId) {
    log.info("updatePackageReversion 参数接收: moduleId: {}, ", moduleId);
    ResponseDTO responseDTO = new ResponseDTO();
    responseDTO.fail("更新代码失败");
    ModuleManage moduleManage = manageService.selectById(moduleId);
    if (moduleManage != null && moduleManage.getIsDelete() == 0) {
      try {
        PermissionJudgeUtils.judgeUserPermission(userService, "module_manage_svn_version",
            JwtUtil.getUserIdFromToken((String) SecurityUtils.getSubject().getPrincipal()), moduleManage.getEnvId());
        if (packageService.updateModuleAllCode(moduleId, null)) {
          responseDTO.success("更新代码成功");
        }
      } catch (SvnUpdateException | SVNException | FileNotFoundException | GitOwnException e) {
        responseDTO.fail(e.getMessage());
      }
    }
    return JSONObject.toJSONString(responseDTO);
  }
  
  /**
   * 接口调用全部下拉svn代码到本地,根据已有的存储结构
   * 主要针对的是误删文件或者nfs不能用需要重新换地址或者路径的情况
   *
   * @return
   */
  @PostMapping(value = "/regenerate/package/info")
  public String rebuildCheckoutAllPackage() throws UnknownHostException {
    String hostAddress = InetAddress.getLocalHost().getHostAddress();
    log.info("本机IP地址为: {}", hostAddress);
    ResponseDTO responseDTO = new ResponseDTO();
    packageService.downSvnAll();
    return JSONObject.toJSONString(responseDTO);
  }
  
  /**
   * 获取某个子模块目录的分支列表
   *
   * @param packageId
   * @return
   */
  @GetMapping(value = "/git/remote/branchs")
  public String getRemoteBranches(Integer envId, Integer packageId) {
    log.info("参数获取 envId:{},packageId:{}", envId, packageId);
    ResponseDTO responseDTO = new ResponseDTO();
    List<String> branches = packageService.getRemoteBranches(envId, packageId);
    responseDTO.success(branches);
    return JSONObject.toJSONString(responseDTO);
  }
  
  /**
   * 切换某个子模块的分支
   *
   * @param envId
   * @param packageId
   * @return
   */
  @PostMapping(value = "/change/branch")
  public String changeBranch(Integer envId, Integer packageId, String branchName) {
    log.info("参数获取 envId:{},packageId:{},branchName: {}", envId, packageId, branchName);
    ResponseDTO responseDTO = new ResponseDTO();
    responseDTO.fail("切换分支失败");
    try {
      if (packageService.chanageBranch(envId, packageId, branchName)) {
        responseDTO.success("切换分支OK");
      }
    } catch (GitOwnException e) {
      responseDTO.fail(e.getMessage());
    }
    return JSONObject.toJSONString(responseDTO);
  }
}


