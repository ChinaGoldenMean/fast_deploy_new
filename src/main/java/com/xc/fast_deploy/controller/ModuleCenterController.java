package com.xc.fast_deploy.controller;

import com.alibaba.fastjson.JSONObject;
import com.xc.fast_deploy.dto.MyPageInfo;
import com.xc.fast_deploy.dto.ResponseDTO;
import com.xc.fast_deploy.dto.module.ModuleCenterEnvDTO;
import com.xc.fast_deploy.dto.module.ModuleDeployEnvDTO;
import com.xc.fast_deploy.model.master_model.ModuleCenter;
import com.xc.fast_deploy.myException.UnauthorizedException;
import com.xc.fast_deploy.service.common.ModuleCenterService;
import com.xc.fast_deploy.service.common.ModuleUserService;
import com.xc.fast_deploy.shiro.token.JwtUtil;
import com.xc.fast_deploy.utils.FoldUtils;
import com.xc.fast_deploy.utils.PermissionJudgeUtils;
import com.xc.fast_deploy.vo.module_vo.param.ModuleCenterSelectParamVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping(value = "/center")
@Slf4j
public class ModuleCenterController {
  
  @Autowired
  private ModuleCenterService centerService;
  @Autowired
  private ModuleUserService userService;
  
  /**
   * 添加一条module_center数据
   *
   * @param moduleCenter
   * @return
   */
  @PostMapping(value = "/insertOneCen")
  public String insertOneCen(ModuleCenter moduleCenter) {
    log.info("ModuleCenter调用添加中心配置模块,入参:" + JSONObject.toJSONString(moduleCenter));
    ResponseDTO responseDTO = new ResponseDTO();
    responseDTO.fail("参数校验失败");
    if (moduleCenter != null && moduleCenter.getEnvId() != null &&
        StringUtils.isNotBlank(moduleCenter.getCenterName()) &&
        StringUtils.isNotBlank(moduleCenter.getCenterCode()) &&
        StringUtils.isNotBlank(moduleCenter.getChildCenterName()) &&
        StringUtils.isNotBlank(moduleCenter.getChildCenterContentName()) &&
        FoldUtils.judgeContent(moduleCenter.getChildCenterContentName()) &&
        FoldUtils.judgeContent(moduleCenter.getCenterCode())) {
      //校验通过的情况下查询权限信息,如果没有权限直接抛出无权限错误
      PermissionJudgeUtils.judgeUserPermission(userService,
          "base_config_center_add",
          JwtUtil.getUserIdFromToken((String) SecurityUtils.getSubject().getPrincipal()),
          moduleCenter.getEnvId());
      responseDTO = centerService.insertOneInfo(moduleCenter);
    }
    return JSONObject.toJSONString(responseDTO);
  }
  
  /**
   * 根据id更新一条中心数据,不允许切环境的更改
   *
   * @param moduleCenter
   * @return
   */
  @PostMapping(value = "/update")
  public String update(ModuleCenter moduleCenter) {
    log.info("ModuleCenter调用更新中心配置模块,入参:" + JSONObject.toJSONString(moduleCenter));
    ResponseDTO responseDTO = new ResponseDTO();
    if (moduleCenter != null && moduleCenter.getId() != null) {
      ModuleCenter center = centerService.selectById(moduleCenter.getId());
      if (center != null) {
        //校验通过的情况下查询权限信息,如果没有权限直接抛出无权限错误
        PermissionJudgeUtils.judgeUserPermission(userService, "base_config_center_update",
            JwtUtil.getUserIdFromToken((String) SecurityUtils.getSubject().getPrincipal()), center.getEnvId());
        //不允许切环境的修改中心,只能修改中心对应的数据
        moduleCenter.setEnvId(null);
        responseDTO = centerService.updateCenterInfo(moduleCenter);
      }
    } else {
      responseDTO.fail("参数校验失败");
    }
    return JSONObject.toJSONString(responseDTO);
  }
  
  /**
   * 删除标记添加
   *
   * @param id
   * @return
   */
  @PostMapping(value = "/deleteOneCen")
  public String deleteOneCen(Integer id) {
    log.info("参数接收: centerId: {}", id);
    if (id != null) {
      ModuleCenter center = centerService.selectById(id);
      if (center != null) {
        //校验通过的情况下查询权限信息,如果没有权限直接抛出无权限错误
        PermissionJudgeUtils.judgeUserPermission(userService, "base_config_center_delete",
            JwtUtil.getUserIdFromToken((String) SecurityUtils.getSubject().getPrincipal()), center.getEnvId());
      }
    }
    ResponseDTO responseDTO = centerService.delInfoById(id);
    return JSONObject.toJSONString(responseDTO);
  }
  
  /**
   * 分页条件查询
   *
   * @param pageNum
   * @param pageSize
   * @param moduleCenterSelectParamVo
   * @return
   */
  @GetMapping(value = "/selectPageAll")
  public String selectPageAll(@RequestParam(value = "pageNum", defaultValue = "1", required = false) int pageNum,
                              @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize,
                              ModuleCenterSelectParamVo moduleCenterSelectParamVo) {
    log.info("模块查询入参: pageNum: " + pageNum + " pageSize: " + pageSize + " param: " +
        JSONObject.toJSONString(moduleCenterSelectParamVo));
    ResponseDTO responseDTO = new ResponseDTO();
    Integer envId = moduleCenterSelectParamVo.getEnvId();
    Map<Integer, Set<String>> envPermissionMap = userService.selectEnvPermissionByUserId(JwtUtil.getUserIdFromToken(
        (String) SecurityUtils.getSubject().getPrincipal()));
    boolean flag = false;
    if (envPermissionMap.size() > 0) {
      if (envId != null) {
        if (envPermissionMap.containsKey(envId)) {
          flag = true;
        }
      } else {
        moduleCenterSelectParamVo.setEnvIds(envPermissionMap.keySet());
        flag = true;
      }
    }
    if (!flag) {
      throw new UnauthorizedException();
    }
    MyPageInfo<ModuleCenterEnvDTO> centerMyPageInfo = centerService.selectPageCen(pageNum, pageSize, moduleCenterSelectParamVo);
    if (centerMyPageInfo.getList() == null || centerMyPageInfo.getList().size() <= 0) {
      responseDTO.fail("未查看到任何数据");
    } else {
      responseDTO.success(centerMyPageInfo);
    }
    return JSONObject.toJSONString(responseDTO);
  }
  
  /**
   * 查询所有的中心模块对应的信息todo
   *
   * @return
   */
  @GetMapping(value = "/show/modules")
  public String showCenterModule() {
    ResponseDTO responseDTO = new ResponseDTO();
    responseDTO.fail("暂无数据");
    Map<Integer, Set<String>> envPermissionMap = userService.selectEnvPermissionByUserId(JwtUtil.getUserIdFromToken(
        (String) SecurityUtils.getSubject().getPrincipal()));
    if (envPermissionMap.size() <= 0) {
      throw new UnauthorizedException();
    }
    Set<Integer> envIds = envPermissionMap.keySet();
    List<ModuleDeployEnvDTO> moduleCenters = centerService.selectAllCenterModule(envIds);
    responseDTO.success(moduleCenters);
    return JSONObject.toJSONString(responseDTO);
  }
  
  /**
   * 根据环境id查询该环境下添加的所有中心
   */
  @GetMapping(value = "/env/centers")
  public String getEnvCenterById(Integer envId) {
    log.info("getEnvCenterById: 参数传入: {}", envId);
    ResponseDTO responseDTO = new ResponseDTO();
    responseDTO.fail("暂无数据");
    String userId = JwtUtil.getUserIdFromToken((String) SecurityUtils.getSubject().getPrincipal());
    //这里用null的原因是表示该权限不需要具体的权限,只需要操作的环境权限该用户拥有就行了
    PermissionJudgeUtils.judgeUserPermission(userService, null, userId
        , envId);
    
    List<ModuleCenter> centerList = centerService.selectAllCenterByEnvId(envId, userId);
    if (centerList != null && centerList.size() > 0) {
      responseDTO.success(centerList);
    }
    return JSONObject.toJSONString(responseDTO);
  }
  
}
