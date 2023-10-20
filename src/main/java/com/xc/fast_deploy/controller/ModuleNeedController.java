package com.xc.fast_deploy.controller;

import com.alibaba.fastjson.JSONObject;
import com.xc.fast_deploy.dto.MyPageInfo;
import com.xc.fast_deploy.dto.ResponseDTO;
import com.xc.fast_deploy.model.base.JsonResult;
import com.xc.fast_deploy.model.master_model.ModuleDeployNeed;
import com.xc.fast_deploy.model.master_model.ModuleManage;
import com.xc.fast_deploy.model.master_model.ModuleUser;
import com.xc.fast_deploy.service.common.ModuleNeedService;
import com.xc.fast_deploy.service.common.ModuleUserService;
import com.xc.fast_deploy.shiro.token.JwtUtil;
import com.xc.fast_deploy.utils.PermissionJudgeUtils;
import com.xc.fast_deploy.vo.module_vo.ModuleEnvCenterManageVo;
import com.xc.fast_deploy.vo.module_vo.ModuleManageDeployVO;
import com.xc.fast_deploy.vo.module_vo.ModuleUpgradeVo;
import com.xc.fast_deploy.vo.module_vo.param.ModuleNeedManageParamVo;
import com.xc.fast_deploy.vo.module_vo.param.ModuleNeedSelectParamVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping(value = "/need")
@Slf4j
public class ModuleNeedController {
  
  @Autowired
  private ModuleNeedService moduleNeedService;
  @Autowired
  private ModuleUserService userService;
  
  /**
   * 新增一个需求
   *
   * @param moduleDeployNeed 需求相关内容
   * @param moduleIds        需求关联模块的Id
   * @return
   */
  @PostMapping(value = "/insert")
  public String insertOneNeed(ModuleDeployNeed moduleDeployNeed, Integer[] moduleIds,
                              MultipartFile reportFile) {
    log.info("insert传入参数:{}", JSONObject.toJSONString(moduleDeployNeed), moduleIds);
    ResponseDTO responseDTO = new ResponseDTO();
    responseDTO.fail("操作失败");
    ModuleUser moduleUser = JwtUtil.getModuleUserInfo();
    PermissionJudgeUtils.judgeUserPermission(userService,
        "deploy_need_developer", moduleUser.getId());
    if (moduleDeployNeed != null &&
        moduleDeployNeed.getEnvId() != null &&
        moduleDeployNeed.getDeployTime() != null) {
      moduleDeployNeed.setDeveloper(moduleUser.getUsername());
      List<Integer> moduleIdList = new ArrayList<>(Arrays.asList(moduleIds));
      responseDTO = moduleNeedService.insertOneNeed(moduleDeployNeed, moduleIdList, reportFile, moduleUser);
    } else {
      responseDTO.fail("参数校验失败");
    }
    return JSONObject.toJSONString(responseDTO);
  }
  
  /**
   * 修改需求
   *
   * @param moduleDeployNeed 需求相关内容
   * @return
   */
  @PostMapping(value = "/updateNeed")
  public String updateNeed(ModuleDeployNeed moduleDeployNeed, MultipartFile reportFile) {
    log.info("updateNeed传入参数:{}" + JSONObject.toJSONString(moduleDeployNeed));
    ResponseDTO responseDTO = new ResponseDTO();
    responseDTO.fail("操作失败");
    ModuleUser moduleUser = JwtUtil.getModuleUserInfo();
    PermissionJudgeUtils.judgeUserPermission(userService,
        "deploy_need_developer", moduleUser.getId());
    if (moduleDeployNeed != null && moduleDeployNeed.getId() != null) {
      responseDTO = moduleNeedService.updateNeed(moduleDeployNeed, reportFile, moduleUser);
    } else {
      responseDTO.fail("参数校验失败");
    }
    return JSONObject.toJSONString(responseDTO);
  }
  
  /**
   * 删除一个需求
   *
   * @param needId
   * @return
   */
  @PostMapping(value = "/deleteNeed")
  public String deleteNeed(Integer needId) {
    log.info("deleteNeed传入参数:{}" + needId);
    ResponseDTO responseDTO = new ResponseDTO();
    responseDTO.fail("删除失败");
    ModuleUser moduleUser = JwtUtil.getModuleUserInfo();
    PermissionJudgeUtils.judgeUserPermission(userService,
        "deploy_need_developer", moduleUser.getId());
    if (moduleNeedService.deleteNeed(needId, moduleUser)) {
      responseDTO.success("删除成功");
    }
    return JSONObject.toJSONString(responseDTO);
  }
  
  /**
   * 查询需求   开发
   *
   * @return
   */
  @GetMapping(value = "/selectAllNeed")
  public String selectAllNeed(@RequestParam(value = "pageNum", defaultValue = "1", required = false) int pageNum,
                              @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize,
                              ModuleNeedSelectParamVo paramVo) {
    log.info("selectAllNeed传入参数：{}", JSONObject.toJSONString(paramVo));
    ResponseDTO responseDTO = new ResponseDTO();
    responseDTO.fail("操作失败");
    ModuleUser moduleUser = JwtUtil.getModuleUserInfo();
    PermissionJudgeUtils.judgeUserPermission(userService,
        "deploy_need_developer", moduleUser.getId());
    MyPageInfo<ModuleDeployNeed> moduleDeployNeedList =
        moduleNeedService.selectAllNeedByDeveloper(pageNum, pageSize, paramVo, moduleUser.getUsername());
    responseDTO.success(moduleDeployNeedList);
    return JSONObject.toJSONString(responseDTO);
  }
  
  /**
   * 查询需求   审核员
   *
   * @return
   */
  @GetMapping(value = "/selectAllNeedByApprover")
  public String selectAllNeedByApprover(@RequestParam(value = "pageNum", defaultValue = "1", required = false) int pageNum,
                                        @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize,
                                        ModuleNeedSelectParamVo paramVo) {
    log.info("selectAllNeed传入参数：{}", JSONObject.toJSONString(paramVo));
    ResponseDTO responseDTO = new ResponseDTO();
    responseDTO.fail("操作失败");
    ModuleUser moduleUser = JwtUtil.getModuleUserInfo();
    PermissionJudgeUtils.judgeUserPermission(userService,
        "deploy_need_approver", moduleUser.getId());
    MyPageInfo<ModuleDeployNeed> moduleDeployNeedList =
        moduleNeedService.selectAllNeedByApprover(pageNum, pageSize, paramVo, moduleUser.getUsername());
    responseDTO.success(moduleDeployNeedList);
    return JSONObject.toJSONString(responseDTO);
  }
  
  /**
   * 更改需求和模块的关联
   *
   * @param needId
   * @param moduleIds
   * @return
   */
  @PostMapping(value = "/updateModuleNeed")
  public String updateModuleNeed(Integer needId, Integer[] moduleIds) {
    log.info("updateModuleNeed传入参数:{}", needId, moduleIds);
    ResponseDTO responseDTO = new ResponseDTO();
    responseDTO.fail("操作失败");
    ModuleUser moduleUser = JwtUtil.getModuleUserInfo();
    PermissionJudgeUtils.judgeUserPermission(userService,
        "deploy_need_developer", moduleUser.getId());
    if (needId != null && moduleIds != null) {
      responseDTO = moduleNeedService.updateNeedModule(needId, moduleIds);
    } else {
      responseDTO.fail("参数校验失败");
    }
    return JSONObject.toJSONString(responseDTO);
  }
  
  /**
   * 审核需求
   *
   * @param needIds
   * @param pass    1代表通过，0代表不通过
   * @return
   */
  @PostMapping(value = "/approveNeed")
  public JsonResult<List<ModuleUpgradeVo>> approveNeed(@RequestParam(value = "pass", defaultValue = "0") int pass,
                                                       Integer[] needIds, Date deployTime, Boolean isPromptly) {
    log.info("approveNeed传入参数:{},{},{}", needIds, pass, deployTime);
    
    ModuleUser moduleUser = JwtUtil.getModuleUserInfo();
    PermissionJudgeUtils.judgeUserPermission(userService,
        "deploy_need_approver", moduleUser.getId());
    List<ModuleUpgradeVo> voList = new ArrayList<>();
    if (needIds != null && (pass == 0 || pass == 1)) {
      
      voList = moduleNeedService.approveNeed(needIds, pass, moduleUser, deployTime, isPromptly);
      
    } else {
      JsonResult.error("参数校验失败");
    }
    return JsonResult.success(voList);
  }
  
  /**
   * 将需求关联的模块提交到发布清单
   *
   * @param needIds
   * @return
   */
  @PostMapping(value = "/commitToDeployList")
  public String commitToDeployList(Integer[] needIds) {
    log.info("commitToDeployList传入参数:{}", needIds);
    ResponseDTO responseDTO = new ResponseDTO();
    responseDTO.fail("操作失败");
    ModuleUser moduleUser = JwtUtil.getModuleUserInfo();
    PermissionJudgeUtils.judgeUserPermission(userService,
        "deploy_need_developer", moduleUser.getId());
    if (moduleNeedService.insertModuleDeployList(needIds, moduleUser)) {
      responseDTO.success("添加成功");
    }
    return JSONObject.toJSONString(responseDTO);
  }
  
  /**
   * 将需求提交给局方审核
   *
   * @param needIds
   * @return
   */
  @PostMapping(value = "/commitApprove")
  public String commitToApprove(Integer[] needIds) {
    log.info("commitApprove传入参数:{}", needIds);
    ResponseDTO responseDTO = new ResponseDTO();
    responseDTO.fail("操作失败");
    ModuleUser moduleUser = JwtUtil.getModuleUserInfo();
    PermissionJudgeUtils.judgeUserPermission(userService,
        "deploy_need_developer", moduleUser.getId());
    if (needIds != null) {
      responseDTO = moduleNeedService.commitApprove(needIds, moduleUser);
    } else {
      responseDTO.fail("参数校验失败");
    }
    return JSONObject.toJSONString(responseDTO);
  }

//    /**
//     * 上传测试报告
//     * @param needId
//     * @param reportFile
//     * @param envIds
//     * @return
//     */
//    @PostMapping(value = "/commitReport")
//    public String commitReport(@RequestParam(value = "drTest",defaultValue = "0")int drTest,
//                            Integer needId, MultipartFile reportFile, Integer[] envIds) {
//        log.info("commitReport传入参数为：{}" + needId);
//        ResponseDTO responseDTO = new ResponseDTO();
//        responseDTO.fail("操作失败");
//        String userId = JwtUtil.getUserIdFromToken((String) SecurityUtils.getSubject().getPrincipal());
//        PermissionJudgeUtils.judgeUserPermission(userService,
//                "deploy_need_developer",userId);
//        if (needId != null && reportFile != null && envIds != null) {
//            responseDTO = moduleNeedService.commitReport(needId,reportFile,envIds,drTest);
//        } else {
//            responseDTO.fail("参数校验失败");
//        }
//        return JSONObject.toJSONString(responseDTO);
//    }
  
  /**
   * 查询某个需求关联的模块
   *
   * @param needId
   * @return
   */
  @GetMapping(value = "/getOneNeedModules")
  public String getOneNeedModules(Integer needId) {
    log.info("getOneNeedModules传入参数：{}", needId);
    ResponseDTO responseDTO = new ResponseDTO();
    responseDTO.fail("操作失败");
    if (needId != null) {
      List<ModuleManageDeployVO> moduleManages = moduleNeedService.selectOneNeedModule(needId);
      responseDTO.success(moduleManages);
    }
    return JSONObject.toJSONString(responseDTO);
  }
  
  /**
   * 导出需求
   *
   * @param needIds
   * @param response
   */
  @GetMapping(value = "/exportNeed")
  public void exportNeedFile(Integer[] needIds, HttpServletResponse response) {
    log.info("exportNeed传入参数:{}", needIds);
    File needFile = moduleNeedService.exportNeed(needIds);
    if (needFile == null) log.error("需求表格为空");
    response.setContentType("application/force-download");// 设置强制下载不打开
    response.addHeader("Content-Disposition", "attachment;fileName=" +
        needFile.getName());// 设置文件名
    FileInputStream inputStream = null;
    try {
      inputStream = new FileInputStream(needFile);
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
  
  /**
   * 查看测试报告
   *
   * @param needId
   * @param response
   */
  @GetMapping(value = "/downloadReport")
  public void viewReport(Integer needId, HttpServletResponse response) {
    log.info("downloadReport传入参数：{}", needId);
    File file = moduleNeedService.viewReport(needId);
    response.setContentType("application/force-download");// 设置强制下载不打开
    response.addHeader("Content-Disposition", "attachment;fileName=" +
        file.getName());// 设置文件名
    FileInputStream inputStream = null;
    try {
      inputStream = new FileInputStream(file);
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
  
  /**
   * 开发将需求流转到灾备或者生产环境
   *
   * @param needVo
   * @param reportFile
   * @return
   */
  @PostMapping(value = "/circulationNeed")
  public String circulationNeed(ModuleNeedManageParamVo needVo, MultipartFile reportFile) {
    log.info("circulationNeed传入参数:{}", needVo);
    ResponseDTO responseDTO = new ResponseDTO();
    responseDTO.fail("操作失败");
    ModuleUser moduleUser = JwtUtil.getModuleUserInfo();
    PermissionJudgeUtils.judgeUserPermission(userService,
        "deploy_need_developer", moduleUser.getId());
    if (needVo != null && reportFile != null) {
      responseDTO = moduleNeedService.circulationNeed(needVo, reportFile, moduleUser);
    } else {
      responseDTO.fail("参数校验失败");
    }
    return JSONObject.toJSONString(responseDTO);
  }
  
  /**
   * 审核员将需求回退到某个环境重新审核
   *
   * @param needVo
   * @return
   */
  @PostMapping(value = "/recallNeed")
  public String recallNeed(ModuleNeedManageParamVo needVo) {
    log.info("recallNeed传入参数:{}", needVo);
    ResponseDTO responseDTO = new ResponseDTO();
    responseDTO.fail("操作失败");
    ModuleUser moduleUser = JwtUtil.getModuleUserInfo();
    PermissionJudgeUtils.judgeUserPermission(userService,
        "deploy_need_approver", moduleUser.getId());
    if (needVo != null && moduleNeedService.recallNeed(needVo, null, moduleUser)) {
      responseDTO.success("操作成功");
    } else {
      responseDTO.fail("操作失败");
    }
    return JSONObject.toJSONString(responseDTO);
  }
  
  /**
   * 将需求关闭并清除发布清单相关的模块
   *
   * @param needId
   * @return
   */
  @PostMapping(value = "/closeNeed")
  public String closeNeed(Integer needId) {
    log.info("closeNeed传入参数:{}", needId);
    ResponseDTO responseDTO = new ResponseDTO();
    responseDTO.fail("操作失败");
    ModuleUser moduleUser = JwtUtil.getModuleUserInfo();
    PermissionJudgeUtils.judgeUserPermission(userService,
        "deploy_need_approver", moduleUser.getId());
    if (moduleNeedService.closeNeed(needId, moduleUser)) responseDTO.success("操作成功");
    return JSONObject.toJSONString(responseDTO);
  }
  
  /**
   * 从excel批量导入需求
   *
   * @param file
   * @return
   */
  @PostMapping(value = "/batchImportNeed")
  public String batchImportNeed(MultipartFile file) {
    log.info("batchImportNeed传入参数：{}", file.getOriginalFilename());
    ResponseDTO responseDTO = new ResponseDTO();
    responseDTO.fail("操作失败");
    ModuleUser moduleUser = JwtUtil.getModuleUserInfo();
    //PermissionJudgeUtils.judgeUserPermission(userService,
    //        "deploy_need_developer",moduleUser.getId());
    if (!file.isEmpty())
      responseDTO = moduleNeedService.insertNeedFromExcel(file, moduleUser);
    return JSONObject.toJSONString(responseDTO);
  }
}
