package com.xc.fast_deploy.controller;

import com.alibaba.fastjson.JSONObject;
import com.xc.fast_deploy.dto.MyPageInfo;
import com.xc.fast_deploy.dto.ResponseDTO;
import com.xc.fast_deploy.dto.module.ModuleMirrorDTO;
import com.xc.fast_deploy.model.master_model.ModuleManage;
import com.xc.fast_deploy.model.master_model.ModuleMirror;
import com.xc.fast_deploy.myException.UnauthorizedException;
import com.xc.fast_deploy.service.common.ModuleManageService;
import com.xc.fast_deploy.service.common.ModuleMirrorService;
import com.xc.fast_deploy.service.common.ModuleUserService;
import com.xc.fast_deploy.shiro.token.JwtUtil;
import com.xc.fast_deploy.utils.PermissionJudgeUtils;
import com.xc.fast_deploy.vo.module_vo.param.ModuleMirrorSelectParamVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping(value = "/mirror")
@Slf4j
public class ModuleMirrorController {
  
  @Autowired
  private ModuleMirrorService moduleMirrorService;
  @Autowired
  private ModuleUserService userService;
  @Autowired
  private ModuleManageService manageService;
  
  //无权限的可以操作//  后面打算 根据token来判断权限
  @PostMapping(value = "/updateMirror")
  public String updateMirror(String mirrorId) {
    log.info("updateMirror 参数接收 mirrorId: {}", mirrorId);
    ResponseDTO responseDTO = new ResponseDTO();
    responseDTO.setMsg("response OK");
    responseDTO.setCode(200);
    responseDTO.setData("update fail!!!");
    try {
      Integer mId = Integer.valueOf(mirrorId);
      if (moduleMirrorService.updateMirror(mId)) {
        responseDTO.setData("update success!!!");
      }
    } catch (NumberFormatException e) {
      log.error("id参数不符合格式");
    }
    return JSONObject.toJSONString(responseDTO);
  }
  
  /**
   * 列出镜像列表
   * 列出镜像列表
   * * @param moduleId
   * * @param envId
   *
   * @return
   */
  @GetMapping(value = "/showMirrorList")
  public String showMirrorList(Integer moduleId, Integer envId) {
    log.info("showMirrorList 参数接收 moduleId: {} , envId: {}", moduleId, envId);
    ResponseDTO responseDTO = new ResponseDTO();
    responseDTO.fail("获取数据失败");
    if (moduleId != null && envId != null) {
//            PermissionJudgeUtils.judgeUserPermission(userService, "image_list_select",
//                    JwtUtil.getUserIdFromToken((String) SecurityUtils.getSubject().getPrincipal()), envId);
      List<ModuleMirror> mirrorList = moduleMirrorService.selectAvailableInfoById(moduleId, envId);
      if (mirrorList != null && mirrorList.size() > 0) {
        responseDTO.success(mirrorList);
      }
    }
    return JSONObject.toJSONString(responseDTO);
  }
  
  @GetMapping(value = "/module/pageInfo")
  public String getModuleMirrorList(
      @RequestParam(value = "pageNum", defaultValue = "1", required = false) int pageNum,
      @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize,
      ModuleMirrorSelectParamVo moduleMirrorSelectParamVo) {
    log.info("/module/pageInfo 参数接收 pageNum: {} , pageSize: {}, param", pageNum, pageSize,
        JSONObject.toJSON(moduleMirrorSelectParamVo));
    ResponseDTO responseDTO = new ResponseDTO();
    Map<Integer, Set<String>> envPermissionMap = userService.selectEnvPermissionByUserId(JwtUtil.getUserIdFromToken(
        (String) SecurityUtils.getSubject().getPrincipal()));
    boolean flag = false;
    if (envPermissionMap.size() > 0) {
      if (moduleMirrorSelectParamVo.getEnvId() != null) {
        if (envPermissionMap.containsKey(moduleMirrorSelectParamVo.getEnvId())) {
          flag = true;
        }
      } else {
        moduleMirrorSelectParamVo.setEnvIds(envPermissionMap.keySet());
        flag = true;
      }
    }
    if (!flag) {
      throw new UnauthorizedException();
    }
    MyPageInfo<ModuleMirrorDTO> myPageInfo = moduleMirrorService.selectAvailInfoPageByParam(pageNum, pageSize, moduleMirrorSelectParamVo);
    responseDTO.success(myPageInfo);
    return JSONObject.toJSONString(responseDTO);
  }
  
  @PostMapping(value = "/deleteOne")
  public String deleteModuleMirror(Integer mirrorId) {
    log.info("delete harborMirror 参数接收 mirrorId: {}", mirrorId);
    ResponseDTO responseDTO = new ResponseDTO();
    responseDTO.fail("删除失败");
    if (mirrorId != null) {
      ModuleMirror moduleMirror = moduleMirrorService.selectById(mirrorId);
      if (moduleMirror != null) {
        PermissionJudgeUtils.judgeUserPermission(userService, "image_list_delete",
            JwtUtil.getUserIdFromToken((String) SecurityUtils
                .getSubject().getPrincipal()), moduleMirror.getModuleEnvId());
        if (moduleMirrorService.deleteMirrorInfo(mirrorId)) {
          responseDTO.success("成功");
        }
      }
    }
    return JSONObject.toJSONString(responseDTO);
  }
  
  /**
   * 获取harbor仓库对应的project_code
   *
   * @param envId
   * @return
   */
  @GetMapping(value = "/harbor/get/projectCode")
  public String getHarborProjectCode(Integer envId) {
    log.info("get harbor projectCode");
    ResponseDTO responseDTO = new ResponseDTO();
//        PermissionJudgeUtils.judgeUserPermission(userService, "",
//                JwtUtil.getUserIdFromToken((String) SecurityUtils.getSubject().getPrincipal()), envId);
    List<String> projectCodeList = moduleMirrorService.getHarborProjectCode(envId);
    responseDTO.success(projectCodeList);
    return JSONObject.toJSONString(responseDTO);
  }
  
  /**
   * 自己指定给模块可发布的镜像名
   *
   * @param moduleId
   * @param envId
   * @param mirrorName
   * @return
   */
  @PostMapping(value = "/selfAppoint")
  public String insertSelfMirror(Integer moduleId, Integer envId, String mirrorName) {
    log.info("selfAppoint指定的参数为: moduleId:{}, envId:{}, mirrorName:{}", moduleId, envId, mirrorName);
    ResponseDTO responseDTO = new ResponseDTO();
    PermissionJudgeUtils.judgeUserPermission(userService, "image_list_addImage",
        JwtUtil.getUserIdFromToken((String) SecurityUtils.getSubject().getPrincipal()), envId);
    //首先判断环境和模块的正确性
    ModuleManage moduleManage = manageService.selectById(moduleId);
    String userIdFromToken = JwtUtil.getUserIdFromToken((String) SecurityUtils.getSubject().getPrincipal());
    if (moduleManage != null && moduleManage.getEnvId().equals(envId) && StringUtils.isNotBlank(userIdFromToken)) {
      ModuleMirror moduleMirror = new ModuleMirror();
      moduleMirror.setIsAvailable(1);
      moduleMirror.setMirrorName(mirrorName);
      moduleMirror.setModuleId(moduleId);
      moduleMirror.setCreateTime(new Date());
      moduleMirror.setUpdateTime(new Date());
      moduleMirror.setOpUserId(userIdFromToken);
      moduleMirror.setModuleEnvId(envId);
      moduleMirrorService.insertSelective(moduleMirror);
      responseDTO.success("添加成功");
    }
    return JSONObject.toJSONString(responseDTO);
  }
  
  /**
   * 获取模块对应的harbor仓库镜像列表
   *
   * @param envId
   * @param moduleId
   * @return
   */
  @GetMapping(value = "/harbor/tags")
  public String getHarborMirrorList(Integer envId, Integer moduleId) {
    log.info("selfAppoint指定的参数为: moduleId:{}, envId:{}", moduleId, envId);
    ResponseDTO responseDTO = new ResponseDTO();
    List<String> mirrorList = moduleMirrorService.getHarborMirrorList(envId, moduleId);
    responseDTO.success(mirrorList);
    return JSONObject.toJSONString(responseDTO);
  }
  
  /**
   * 清理仓库没有被使用或者无效的镜像
   *
   * @param envIds
   * @return
   */
  @PostMapping(value = "/harbor/clear")
  public String clearHarbor(Integer[] envIds) {
    log.info("/harbor/clear/传入参数:{}", envIds.toString());
    ResponseDTO responseDTO = new ResponseDTO();
    String userId = (String) SecurityUtils.getSubject().getPrincipal();
    PermissionJudgeUtils.judgeUserPermission(userService, "image_list_delete",
        JwtUtil.getUserIdFromToken(userId));
    Map<Integer, Set<String>> envPermissionMap =
        userService.selectEnvPermissionByUserId(JwtUtil.getUserIdFromToken(userId));
    Set<Integer> envIdSet = new HashSet<>(Arrays.asList(envIds));
    if (envPermissionMap.size() > 0 && envIdSet.size() > 0 &&
        envPermissionMap.keySet().containsAll(envIdSet)) {
      moduleMirrorService.clearHarborNotUsedMirror(envIdSet);
      responseDTO.success("清理完成");
    } else {
      throw new UnauthorizedException();
    }
    return JSONObject.toJSONString(responseDTO);
  }
}
