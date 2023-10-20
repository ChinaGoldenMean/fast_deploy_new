package com.xc.fast_deploy.controller;

import com.alibaba.fastjson.JSONObject;
import com.xc.fast_deploy.dto.MyPageInfo;
import com.xc.fast_deploy.dto.ResponseDTO;
import com.xc.fast_deploy.dto.module.BillingPodDeployDTO;
import com.xc.fast_deploy.model.master_model.ModuleEnv;
import com.xc.fast_deploy.model.slave_model.BillingImageDeploy;
import com.xc.fast_deploy.model.slave_model.BillingOpOnOff;
import com.xc.fast_deploy.model.slave_model.BillingPodDeploy;
import com.xc.fast_deploy.myenum.BillingImageReviewEnum;
import com.xc.fast_deploy.service.common.ModuleEnvService;
import com.xc.fast_deploy.service.common.ModuleUserService;
import com.xc.fast_deploy.service.slave.IBillingImageDeployService;
import com.xc.fast_deploy.service.slave.IBillingOpOnOffService;
import com.xc.fast_deploy.service.slave.IBillingPodDeployService;
import com.xc.fast_deploy.shiro.token.JwtUtil;
import com.xc.fast_deploy.utils.PermissionJudgeUtils;
import com.xc.fast_deploy.vo.module_vo.param.BillingImageSelectParamVo;
import com.xc.fast_deploy.vo.module_vo.param.BillingPodDeployParamVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

@RequestMapping(value = "/billing")
@RestController
@Slf4j
public class K8sBillingInfoController {
  
  @Autowired
  private IBillingImageDeployService imageDeployService;
  
  @Autowired
  private IBillingOpOnOffService opOnOffService;
  
  @Autowired
  private IBillingPodDeployService billingPodDeployService;
  
  @Autowired
  private ModuleUserService userService;
  
  @Autowired
  private ModuleEnvService envService;
  
  @Value("${myself.pspass.prod}")
  private boolean isProdEnv;
  
  /**
   * 查询billing对应的环境/billing/env/show
   *
   * @return
   */
  @GetMapping(value = "/env/show")
  public String getBillingEnv() {
    Set<Integer> envIdSet = opOnOffService.selectEnvIds(isProdEnv);
    ResponseDTO responseDTO = new ResponseDTO();
    List<ModuleEnv> envList = envService.selectEnvAll(envIdSet);
    responseDTO.success(envList);
    return JSONObject.toJSONString(responseDTO);
  }
  
  //分页展示镜像
  @GetMapping(value = "/image/show")
  public String showBillingDeployImage(
      @RequestParam(value = "pageNum", defaultValue = "1", required = false) int pageNum,
      @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize,
      BillingImageSelectParamVo imageSelectParamVo) {
    log.info("模块查询入参: pageNum: {}, pageSize: {}, param: {}",
        pageNum, pageSize, JSONObject.toJSONString(imageSelectParamVo));
    ResponseDTO responseDTO = new ResponseDTO();
    PermissionJudgeUtils.judgeUserPermission(userService, "",
        JwtUtil.getUserIdFromToken((String) SecurityUtils.getSubject().getPrincipal()),
        imageSelectParamVo.getEnvId());
    MyPageInfo<BillingImageDeploy> imageDeployPageInfo = imageDeployService.selectBillingImagePage(pageNum,
        pageSize, imageSelectParamVo);
    if (imageDeployPageInfo.getList() == null || imageDeployPageInfo.getList().size() <= 0) {
      responseDTO.fail("未查询到任何数据");
    } else {
      responseDTO.success(imageDeployPageInfo);
    }
    return JSONObject.toJSONString(responseDTO);
  }
  
  //分页展示pod
  @GetMapping(value = "/pod/show")
  public String showBillingPodDeploy(
      @RequestParam(value = "pageNum", defaultValue = "1", required = false) int pageNum,
      @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize,
      BillingPodDeployParamVo billingPodDeployParamVo) {
    log.info("模块查询入参: pageNum: {}, pageSize: {}, param: {}", pageNum, pageSize,
        JSONObject.toJSONString(billingPodDeployParamVo));
    ResponseDTO responseDTO = new ResponseDTO();
    PermissionJudgeUtils.judgeUserPermission(userService, "yx_manage_pod_show",
        JwtUtil.getUserIdFromToken((String) SecurityUtils.getSubject().getPrincipal()),
        billingPodDeployParamVo.getEnvId());
    MyPageInfo<BillingPodDeployDTO> billingPodDeployPage =
        billingPodDeployService.selectBillingPodDeployPage(pageNum,
            pageSize, billingPodDeployParamVo);
    
    if (billingPodDeployPage.getList() == null || billingPodDeployPage.getList().size() <= 0) {
      responseDTO.fail("未查询到任何数据");
    } else {
      responseDTO.success(billingPodDeployPage);
    }
    return JSONObject.toJSONString(responseDTO);
  }
  
  //展示开关
  @GetMapping(value = "/opoff/show")
  public String showDeployOnAndOff(Integer envId) {
    log.info("模块查询入参: envId: {}", envId);
    ResponseDTO responseDTO = new ResponseDTO();
    responseDTO.fail("暂无数据");
    Set<Integer> envIdSet = opOnOffService.selectEnvIds(isProdEnv);
    PermissionJudgeUtils.judgeUserPermission(userService, "",
        JwtUtil.getUserIdFromToken((String) SecurityUtils.getSubject().getPrincipal()), envId);
    List<BillingOpOnOff> opOnOffList = opOnOffService.selectAll(envIdSet);
    if (opOnOffList != null && opOnOffList.size() > 0) {
      responseDTO.success(opOnOffList);
    }
    return JSONObject.toJSONString(responseDTO);
  }
  
  /**
   * 管理环境是否开启
   *
   * @param envId
   * @param isAbleNext
   * @return
   */
  //操作开关 打开 or 关闭 开关  0关 1开
  @PostMapping(value = "/opoff/up")
  public String openOff(Integer envId, Integer isAbleNext) {
    log.info("模块查询入参: envId: {} isAbleNext:{}", envId, isAbleNext);
    ResponseDTO responseDTO = new ResponseDTO();
    responseDTO.fail("操作失败");
    if (envId != null && (isAbleNext == 0 || isAbleNext == 1)) {
      PermissionJudgeUtils.judgeUserPermission(userService, "yx_config_env_operation",
          JwtUtil.getUserIdFromToken((String) SecurityUtils.getSubject().getPrincipal()), envId);
      if (opOnOffService.updateAbleNext(envId, isAbleNext)) {
        responseDTO.success("操作成功");
      }
    }
    return JSONObject.toJSONString(responseDTO);
  }
  
  /**
   * 批量通过审核镜像
   *
   * @param envId
   * @param imageIds
   * @return
   */
  @PostMapping(value = "/batch/review/image")
  public String reviewImage(Integer envId, Integer[] imageIds, Integer reviewCode) {
    log.info("模块查询入参: envId: {} imageIds:{}", envId, Arrays.asList(imageIds));
    ResponseDTO responseDTO = new ResponseDTO();
    responseDTO.fail("操作失败");
    BillingImageReviewEnum reviewEnum = BillingImageReviewEnum.getTypeByCode(reviewCode);
    if (envId != null && imageIds.length > 0 && reviewEnum != null) {
      PermissionJudgeUtils.judgeUserPermission(userService, "yx_image_reviewed",
          JwtUtil.getUserIdFromToken((String) SecurityUtils.getSubject().getPrincipal()), envId);
      if (imageDeployService.updateBatchImageReviewed(envId, imageIds, reviewCode)) {
        responseDTO.success("操作成功");
      }
    }
    return JSONObject.toJSONString(responseDTO);
  }
  
  /**
   * 查询单条记录 包含args的json记录
   *
   * @param envId
   * @param billingPodId
   * @return
   */
  @GetMapping(value = "/pod/view/json")
  public String getPodJson(Integer envId, Long billingPodId) {
    log.info("模块查询入参: envId: {} billingPodId:{}", envId, billingPodId);
    ResponseDTO responseDTO = new ResponseDTO();
    responseDTO.fail("查询失败");
    BillingPodDeploy billingPodDeploy = billingPodDeployService.selectBillingPodBlobs(envId, billingPodId);
    if (billingPodDeploy != null) {
      responseDTO.success(billingPodDeploy);
    }
    return JSONObject.toJSONString(responseDTO);
  }
  
  /**
   * 获取某个环境的hostnames列表,即该环境所包含的可访问的hostnames列表
   *
   * @param envId
   * @return
   */
  @GetMapping(value = "/op/hostnames")
  public String getOpHostnames(Integer envId) {
    log.info("获取hostnames: envId: {}", envId);
    ResponseDTO responseDTO = new ResponseDTO();
    responseDTO.fail("获取出错");
    List<String> hostnameList = opOnOffService.selectHostNamesByEnvId(envId);
    responseDTO.success(hostnameList);
    return JSONObject.toJSONString(responseDTO);
  }
  
  /**
   * 更新对对某个环境可操作的hostnames
   *
   * @param envId
   * @param hostnames
   * @return
   */
  @PostMapping(value = "/op/hostnames")
  public String updateOpHostnames(Integer envId, String[] hostnames) {
    log.info("获取hostnames: envId: {} hostnames: {}", envId, JSONObject.toJSONString(hostnames));
    ResponseDTO responseDTO = new ResponseDTO();
    responseDTO.fail("操作失败");
    if (envId != null && hostnames != null && hostnames.length > 0) {
      opOnOffService.updateHostNames(envId, Arrays.asList(hostnames));
      responseDTO.success("更改成功");
    } else {
      responseDTO.argsNotOK();
    }
    return JSONObject.toJSONString(responseDTO);
  }
  
}
