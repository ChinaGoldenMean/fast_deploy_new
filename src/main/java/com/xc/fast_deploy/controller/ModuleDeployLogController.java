package com.xc.fast_deploy.controller;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.xc.fast_deploy.dao.master_dao.ModuleEnvMapper;
import com.xc.fast_deploy.dto.ResponseDTO;
import com.xc.fast_deploy.dto.k8s.K8sDeployCountDTO;
import com.xc.fast_deploy.dto.k8s.K8sDeployTrendData;
import com.xc.fast_deploy.dto.module.ModuleDeployStatisticsDTO;
import com.xc.fast_deploy.model.master_model.ModuleDeployLog;
import com.xc.fast_deploy.model.master_model.ModuleEnv;
import com.xc.fast_deploy.myException.UnauthorizedException;
import com.xc.fast_deploy.service.common.ModuleDeployLogService;
import com.xc.fast_deploy.service.common.ModuleUserService;
import com.xc.fast_deploy.shiro.token.JwtUtil;
import com.xc.fast_deploy.utils.PermissionJudgeUtils;
import com.xc.fast_deploy.vo.module_vo.param.ModuleDeployLogSelectParam;
import com.xc.fast_deploy.vo.module_vo.param.ModuleManageSelectParamVo;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONArray;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.xc.fast_deploy.myenum.ModuleDeployCountDataTypeEnum.SELF_COUNT_DATA;

@RequestMapping(value = "/deploy/log")
@RestController
@Slf4j
public class ModuleDeployLogController {
  
  @Autowired
  private ModuleDeployLogService deployLogService;
  @Autowired
  private ModuleUserService userService;
  @Autowired
  private ModuleEnvMapper envMapper;
  
  /**
   * 查看发布的日志
   *
   * @param moduleId
   * @param envId
   * @return
   */
  @GetMapping(value = "/showDeployLogs")
  public String showDeployLogs(Integer moduleId, Integer envId, String record) {
    ResponseDTO responseDTO = new ResponseDTO();
    responseDTO.fail("未能获取数据");
    if (moduleId != null && envId != null) {
      PermissionJudgeUtils.judgeUserPermission(userService, "module_deploy_logs_show",
          JwtUtil.getUserIdFromToken((String) SecurityUtils.getSubject().getPrincipal()), envId);
      List<ModuleDeployLog> deployLogs = deployLogService.selectInfoById(moduleId, envId, record);
      if (deployLogs != null && deployLogs.size() > 0) {
        responseDTO.success(deployLogs);
      }
    }
    return JSONObject.toJSONString(responseDTO);
  }
  
  /**
   * 获取首页数据固定数值的的展示项
   *
   * @param envId
   * @return
   */
  @GetMapping(value = "/getDeployCountData")
  public String getDeployCountData(Integer envId) {
    ResponseDTO responseDTO = new ResponseDTO();
    responseDTO.fail("未能获取数据");
    if (envId != null) {
      //todo 权限另外添加进去
//            PermissionJudgeUtils.judgeUserPermission(userService, "",
//                    JwtUtil.getUserIdFromToken((String) SecurityUtils.getSubject().getPrincipal()), envId);
      K8sDeployCountDTO deployCountDTO = deployLogService.getHomeData(envId);
      responseDTO.success(deployCountDTO);
    }
    return JSONObject.toJSONString(responseDTO);
  }
  
  /**
   * 获取首页的趋势数据
   */
  @GetMapping(value = "/getTrendData")
  public String getTrendData(Integer envId, Integer type, String fromDate, String toDate) {
    log.info("getTrendData访问参数: envId: {} type:{},fromDate:{},toDate:{}",
        envId, type, fromDate, toDate);
    String userIdFromToken = JwtUtil.getUserIdFromToken(
        (String) SecurityUtils.getSubject().getPrincipal());
    ResponseDTO responseDTO = new ResponseDTO();
    responseDTO.fail("未能获取数据");
    //针对外部调用专门提供的接口调用
    if (StringUtils.isNotBlank(userIdFromToken)) {
      if (envId != null && type != null) {
        PermissionJudgeUtils.judgeUserPermission(userService,
            "publish_statistics", userIdFromToken, envId);
        List<K8sDeployTrendData> trendDataList =
            getDeployCountData(envId, type, fromDate, toDate, false);
        if (trendDataList.size() > 0) {
          responseDTO.success(trendDataList);
        }
      }
    }
    return JSONObject.toJSONString(responseDTO);
  }
  
  /**
   * 获取发布次数统计数据
   *
   * @param fromDate
   * @param toDate
   * @return
   */
  @GetMapping(value = "/getDeploySelfCountData")
  public String getDeployCountData(String fromDate, String toDate) {
    log.info("getTrendData访问参数: fromDate:{},toDate:{}", fromDate, toDate);
    String userIdFromToken = JwtUtil.getUserIdFromToken(
        (String) SecurityUtils.getSubject().getPrincipal());
    ResponseDTO responseDTO = new ResponseDTO();
    responseDTO.argsNotOK();
    if (StringUtils.isNotBlank(fromDate) && StringUtils.isNotBlank(toDate) &&
        StringUtils.isNotBlank(userIdFromToken) && userIdFromToken.equals(JwtUtil.SELF_USER_ID)) {
      List<ModuleEnv> envList = envMapper.selectAll();
      if (envList != null && envList.size() > 0) {
        Map<String, List<K8sDeployTrendData>> deployTrendDataMap = new HashMap<>();
        for (ModuleEnv moduleEnv : envList) {
          List<K8sDeployTrendData> trendDataList =
              getDeployCountData(moduleEnv.getId(),
                  SELF_COUNT_DATA.getType(), fromDate, toDate, true);
          deployTrendDataMap.put(moduleEnv.getEnvName(), trendDataList);
        }
        responseDTO.success(deployTrendDataMap);
      }
    }
    
    return JSONObject.toJSONString(responseDTO);
    
  }
  
  private List<K8sDeployTrendData> getDeployCountData(Integer envId, Integer type,
                                                      String fromDate, String toDate, boolean selfUser) {
    Date fDate = null;
    Date tDate = null;
    if (SELF_COUNT_DATA.getType().equals(type)) {
      try {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        fDate = dateFormat.parse(fromDate);
        tDate = dateFormat.parse(toDate);
      } catch (ParseException e) {
        e.printStackTrace();
      }
    }
    if (selfUser) {
      return deployLogService.getDeploySelfData(envId, type, fDate, tDate);
    } else {
      return deployLogService.getTrendHomeData(envId, type, fDate, tDate);
    }
  }
  
  /**
   * 获取各环境模块的发布TOP
   */
  @GetMapping(value = "getTop")
  public String getTop(ModuleManageSelectParamVo manageSelectParamVo) {
    log.info("模块查询入参: param: {}", JSONObject.toJSONString(manageSelectParamVo));
    ResponseDTO responseDTO = new ResponseDTO();
    String userIdFromToken = JwtUtil
        .getUserIdFromToken((String) SecurityUtils.getSubject().getPrincipal());
    if (StringUtils.isNotBlank(userIdFromToken) && userIdFromToken.equals(JwtUtil.SELF_USER_ID)) {
      List<ModuleEnv> envList = envMapper.selectAll();
      Set<Integer> envIds = new HashSet<>();
      if (envList != null && envList.size() > 0) {
        for (ModuleEnv env : envList) {
          envIds.add(env.getId());
        }
      }
      manageSelectParamVo.setEnvIds(envIds);
    } else {
      Map<Integer, Set<String>> envPermissionMap =
          userService.selectEnvPermissionByUserId(userIdFromToken);
      boolean flag = false;
      if (envPermissionMap.size() > 0) {
        if (manageSelectParamVo.getEnvIds() != null) {
          if (envPermissionMap.containsKey(manageSelectParamVo.getEnvId())) {
            flag = true;
          }
        } else {
          manageSelectParamVo.setEnvIds(envPermissionMap.keySet());
          flag = true;
        }
      }
      if (!flag) {
        throw new UnauthorizedException();
      }
    }
    Map<String, List<ModuleDeployStatisticsDTO>> statisticsDTOmap =
        deployLogService.getDeploystatistics(manageSelectParamVo);
    if (statisticsDTOmap.size() > 0) {
      responseDTO.success(statisticsDTOmap);
    }
    return JSONObject.toJSONString(responseDTO,
        SerializerFeature.DisableCircularReferenceDetect);
  }
  
  /**
   * 获取指定时间发布变更日志
   *
   * @param moduleDeployLogSelectParam
   * @return
   */
  @GetMapping(value = "getDeployChangeLog")
  public String getDeployChangeLog(ModuleDeployLogSelectParam moduleDeployLogSelectParam) {
    log.info("模块查询入参: param: {}", JSONObject.toJSONString(moduleDeployLogSelectParam));
    ResponseDTO responseDTO = new ResponseDTO();
    String userIdFromToken = JwtUtil
        .getUserIdFromToken((String) SecurityUtils.getSubject().getPrincipal());
    Map<Integer, Set<String>> envPermissionMap =
        userService.selectEnvPermissionByUserId(userIdFromToken);
    boolean flag = false;
    if (envPermissionMap.size() > 0) {
      if (moduleDeployLogSelectParam.getEnvId() != null) {
        if (envPermissionMap.containsKey(moduleDeployLogSelectParam.getEnvId())) {
          flag = true;
        }
      }
    }
    if (!flag) {
      throw new UnauthorizedException();
    }
    List<ModuleDeployStatisticsDTO> resultList =
        deployLogService.getDeployChangeLog(moduleDeployLogSelectParam);
    if (resultList.size() > 0) {
      responseDTO.success(resultList);
    }
    return StringEscapeUtils.unescapeEcmaScript(JSONObject.toJSONString(responseDTO));
  }
}
