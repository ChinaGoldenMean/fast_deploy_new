package com.xc.fast_deploy.service.common.impl;

import com.alibaba.druid.sql.ast.statement.SQLIfStatement;
import com.sun.org.apache.bcel.internal.generic.NEW;
import com.xc.fast_deploy.dao.master_dao.ModuleDeployLogMapper;
import com.xc.fast_deploy.dao.master_dao.ModuleDeployMapper;
import com.xc.fast_deploy.dao.master_dao.ModuleEnvMapper;
import com.xc.fast_deploy.dto.k8s.K8sDeployCountDTO;
import com.xc.fast_deploy.dto.k8s.K8sDeployTrendData;
import com.xc.fast_deploy.dto.module.ModuleDeployStatisticsDTO;
import com.xc.fast_deploy.model.master_model.ModuleDeploy;
import com.xc.fast_deploy.model.master_model.ModuleDeployLog;
import com.xc.fast_deploy.model.master_model.ModuleEnv;
import com.xc.fast_deploy.model.master_model.example.ModuleDeployExample;
import com.xc.fast_deploy.model.master_model.example.ModuleDeployLogExample;
import com.xc.fast_deploy.myException.TransYaml2K8sVoException;
import com.xc.fast_deploy.myenum.ModuleDeployCountDataTypeEnum;
import com.xc.fast_deploy.myenum.k8sEnum.K8sKindTypeEnum;
import com.xc.fast_deploy.service.common.ModuleDeployLogService;
import com.xc.fast_deploy.service.k8s.K8sService;
import com.xc.fast_deploy.utils.constant.K8sNameSpace;
import com.xc.fast_deploy.vo.module_vo.ModuleDeployLogVo;
import com.xc.fast_deploy.vo.module_vo.param.ModuleDeployLogSelectParam;
import com.xc.fast_deploy.vo.module_vo.param.ModuleManageSelectParamVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.transaction.TransactionRequiredException;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.xc.fast_deploy.utils.constant.K8sOPActive.*;

@Service
@Slf4j
public class ModuleDeployLogServiceImpl extends BaseServiceImpl<ModuleDeployLog, Integer> implements ModuleDeployLogService {
  
  @Autowired
  private ModuleDeployLogMapper deployLogMapper;
  
  @Autowired
  private ModuleDeployMapper deployMapper;
  @Autowired
  private ModuleEnvMapper envMapper;
  @Autowired
  private K8sService k8sService;
  
  @PostConstruct
  public void init() {
    super.init(deployLogMapper);
  }
  
  @Override
  public List<ModuleDeployLog> selectInfoById(Integer moduleId, Integer envId, String record) {
    List<ModuleDeployLog> moduleDeployLogs = new ArrayList<>();
    if (StringUtils.isBlank(record) || record == null) {
      record = "50";
    }
    ModuleDeployExample deployExample = new ModuleDeployExample();
    ModuleDeployExample.Criteria criteria1 = deployExample.createCriteria();
    criteria1.andEnvIdEqualTo(envId).andModuleIdEqualTo(moduleId).andIsDeleteEqualTo(0);
    List<ModuleDeploy> moduleDeploys = deployMapper.selectByExample(deployExample);
    if (moduleDeploys != null && moduleDeploys.size() > 0) {
      Integer deployId = moduleDeploys.get(0).getId();
      ModuleDeployLogExample deployLogExample = new ModuleDeployLogExample();
      ModuleDeployLogExample.Criteria criteria = deployLogExample.createCriteria();
      criteria.andDeployIdEqualTo(deployId);
      deployLogExample.setOrderByClause("create_time DESC limit " + record);
      
      moduleDeployLogs = deployLogMapper.selectByExample(deployLogExample);
      moduleDeploys.clear();
    }
//        ModuleDeployLogExample deployLogExample = new ModuleDeployLogExample();
//        ModuleDeployLogExample.Criteria criteria = deployLogExample.createCriteria();
//        criteria.andEnvIdEqualTo(envId).andModuleIdEqualTo(moduleId);
//        deployLogExample.setOrderByClause("create_time DESC");
//        return deployLogMapper.selectByExample(deployLogExample);
    return moduleDeployLogs;
  }
  
  /**
   * 根据环境id获取首页数据
   *
   * @param envId
   * @return
   */
  @Override
  public K8sDeployCountDTO getHomeData(Integer envId) {
    K8sDeployCountDTO deployCountDTO = new K8sDeployCountDTO();
    Map<String, Integer> centerCountMap = new HashMap<>();
    
    ModuleEnv moduleEnv = envMapper.selectOne(envId);
    if (moduleEnv != null) {
      List<Object> objectList = k8sService.listAllNameSpacedResource(moduleEnv,
          K8sNameSpace.DEFAULT, K8sKindTypeEnum.NODE.getKindType());
      if (objectList != null && objectList.size() > 0) {
        deployCountDTO.setNodeCount(objectList.size());
        deployCountDTO.setRunNodeCount(objectList.size());
      }
      List<Object> podList = k8sService.listAllNameSpacedResource(moduleEnv,
          K8sNameSpace.DEFAULT, K8sKindTypeEnum.POD.getKindType());
      if (podList != null && podList.size() > 0) {
        deployCountDTO.setPodCount(podList.size());
      }
    }
    deployCountDTO.setBusyNodeCount(3);
    //获取累计所有的发布模块,即模块的最新状态是未下线的
    int deployAllModuleCount = 0;
    List<ModuleDeployLogVo> deployLogVoList = getModuleList(null, null, envId);
    if (deployLogVoList != null && deployLogVoList.size() > 0) {
      Map<Integer, ModuleDeployLogVo> moduleLogMap = new HashMap<>();
      for (ModuleDeployLogVo deployLogVo : deployLogVoList) {
        moduleLogMap.put(deployLogVo.getModuleId(), deployLogVo);
      }
      if (moduleLogMap.size() > 0) {
        Set<Integer> keySet = moduleLogMap.keySet();
        for (Integer moduleId : keySet) {
          if (!"下线模块".equals(moduleLogMap.get(moduleId).getOpActive())) {
            deployAllModuleCount++;
            if (!centerCountMap.containsKey(moduleLogMap.get(moduleId).getCenterName())) {
              centerCountMap.put(moduleLogMap.get(moduleId).getCenterName(), 1);
            } else {
              centerCountMap.put(moduleLogMap.get(moduleId).getCenterName(),
                  centerCountMap.get(moduleLogMap.get(moduleId).getCenterName()) + 1);
            }
          }
        }
      }
    }
    deployCountDTO.setCenterCountMap(centerCountMap);
    deployCountDTO.setDeployAllModuleCount(deployAllModuleCount);
    //获取本周发布的所有模块数量(即统计从当时开始7天前的数据)
    Calendar cal1 = Calendar.getInstance();
    cal1.set(Calendar.HOUR_OF_DAY, 0);
    cal1.set(Calendar.MINUTE, 0);
    cal1.set(Calendar.SECOND, 0);
    cal1.set(Calendar.MILLISECOND, 0);
    Date nowTime = cal1.getTime();
    cal1.add(Calendar.DATE, -7);
    Date before7Date = cal1.getTime();
    
    Calendar cal2 = Calendar.getInstance();
    cal2.set(Calendar.HOUR_OF_DAY, 0);
    cal2.set(Calendar.MINUTE, 0);
    cal2.set(Calendar.SECOND, 0);
    cal2.set(Calendar.MILLISECOND, 0);
    //上周的发布模块数量
    int deployNowModuleCount = getModuleCount(before7Date, nowTime, envId);
    deployCountDTO.setDeployNowModuelCount(deployNowModuleCount);
    cal2.add(Calendar.DATE, -14);
    Date before14Date = cal2.getTime();
    //上上周的发布模块数量
    int deployBeforeModuleCount = getModuleCount(before14Date, before7Date, envId);
    int k = deployNowModuleCount - deployBeforeModuleCount;
    double i;
    if (deployBeforeModuleCount == 0) {
      i = (double) k;
    } else {
      i = ((double) k / deployBeforeModuleCount) * 100;
    }
    deployCountDTO.setIncrPercent(String.format("%.2f", i));
    return deployCountDTO;
  }
  
  /**
   * 获取发布的趋势数据
   *
   * @param
   * @param envId
   * @param fromDate
   * @param toDate
   */
  @Override
  public List<K8sDeployTrendData> getTrendHomeData(Integer envId, Integer type,
                                                   Date fromDate, Date toDate) {
    ModuleDeployCountDataTypeEnum countDataTypeEnum = ModuleDeployCountDataTypeEnum.getEnumByType(type);
    List<K8sDeployTrendData> trendDataList = new LinkedList<>();
    long nowTime = new Date().getTime();
    Date today = new Date();
    
    if (countDataTypeEnum != null) {
      switch (countDataTypeEnum) {
        //返回当前时间倒退24小时的数据 按小时统计数据
        case DAY_COUNT_DATA:
          //当前的时间
          Calendar calendar = Calendar.getInstance();
          //小时数据按照当前小时加一取整
          calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY) + 1);
          calendar.set(Calendar.MINUTE, 0);
          calendar.set(Calendar.SECOND, 0);
          calendar.set(Calendar.MILLISECOND, 0);
          Date today1 = calendar.getTime();
          long nowTime1 = toDate.getTime();
          
          Date f1Date = new Date(toDate.getTime() - 24L * 60 * 60 * 1000);
          //将24小时的数据统计出来
          List<ModuleDeployLogVo> deployLogList = getModuleList(f1Date, today1, envId);
          //然后另外统计数据
          for (long j = 24; j >= 1; j--) {
            //每次遍历获取一个数据
            Date beginDate = new Date(nowTime1 - (j * 60 * 60 * 1000));
            Date endDate = new Date(nowTime1 - ((j - 1) * 60 * 60 * 1000));
            K8sDeployTrendData trendData = getTrendData(deployLogList, beginDate, endDate);
            trendDataList.add(trendData);
          }
          deployLogList.clear();
          break;
        case WEEK_COUNT_DATA:
          //返回7天的数据
          Date f7Date = new Date(nowTime - 7L * 24 * 60 * 60 * 1000);
          //将最近7天的数据统计出来
          List<ModuleDeployLogVo> deployLogListWeek = getModuleList(f7Date, today, envId);
          for (long i = 7; i >= 1; i--) {
            Date beginTime = new Date(nowTime - (i * 24 * 60 * 60 * 1000));
            Date endDate = new Date(nowTime - ((i - 1) * 24 * 60 * 60 * 1000));
            K8sDeployTrendData trendData = getTrendData(deployLogListWeek, beginTime, endDate);
            trendDataList.add(trendData);
          }
          deployLogListWeek.clear();
          break;
        case YEAR_COUNT_DATA:
          //返回一年的每周数据
          Date fYearDate = new Date(nowTime - 364L * 24 * 60 * 60 * 1000);
          List<ModuleDeployLogVo> deployLogListYear = getModuleList(fYearDate, today, envId);
          for (long i = 364; i > 0; i -= 7) {
            Date beginDate = new Date(nowTime - (i * 24 * 60 * 60 * 1000));
            Date endDate = new Date(nowTime - ((i - 7) * 24 * 60 * 60 * 1000));
            K8sDeployTrendData trendData = getTrendData(deployLogListYear, beginDate, endDate);
            trendDataList.add(trendData);
          }
          deployLogListYear.clear();
          break;
        case MONTH_COUNT_DATA:
          //返回一月的每天数据
          Date fMonthDate = new Date(nowTime - 30L * 24 * 60 * 60 * 1000);
          List<ModuleDeployLogVo> deployLogListMonth = getModuleList(fMonthDate, today, envId);
          for (long i = 30; i > 0; i--) {
            Date beforeDate = new Date(nowTime - (i * 24 * 60 * 60 * 1000));
            Date endDate = new Date(nowTime - ((i - 1) * 24 * 60 * 60 * 1000));
            K8sDeployTrendData trendData = getTrendData(deployLogListMonth, beforeDate, endDate);
            trendDataList.add(trendData);
          }
          deployLogListMonth.clear();
          break;
        case SELF_COUNT_DATA:
          if (toDate != null && fromDate != null && toDate.compareTo(fromDate) > 0) {
            List<ModuleDeployLogVo> deployLogListSelf = getModuleList(fromDate, toDate, envId);
            long timeTrend = toDate.getTime() - fromDate.getTime();
            //不超过5天按照小时
            if (timeTrend > 0 && timeTrend <= 5L * 24 * 60 * 60 * 1000) {
              if (new Date(fromDate.getTime() + 60L * 60 * 1000).compareTo(toDate) >= 0) {
                K8sDeployTrendData trendData = getTrendData(deployLogListSelf, fromDate, toDate);
                trendDataList.add(trendData);
              } else {
                long i = 1;
                while (new Date(fromDate.getTime() + i * 60 * 60 * 1000).compareTo(toDate) <= 0) {
                  Date beginDate = new Date(fromDate.getTime() + (i - 1) * 60 * 60 * 1000);
                  Date endDate = new Date(fromDate.getTime() + i * 60 * 60 * 1000);
                  K8sDeployTrendData trendData;
                  if (endDate.compareTo(toDate) >= 0) {
                    trendData = getTrendData(deployLogListSelf, beginDate, toDate);
                  } else {
                    trendData = getTrendData(deployLogListSelf, beginDate, endDate);
                  }
                  trendDataList.add(trendData);
                  i++;
                }
              }
              //不超过35天按照天
            } else if (timeTrend > 5L * 24 * 60 * 60 * 1000 && timeTrend <= 35L * 24 * 60 * 60 * 1000) {
              long i = 1;
              while (new Date(fromDate.getTime() + i * 24 * 60 * 60 * 1000).compareTo(toDate) <= 0) {
                Date beginDate = new Date(fromDate.getTime() + (i - 1) * 24 * 60 * 60 * 1000);
                Date endDate = new Date(fromDate.getTime() + i * 24 * 60 * 60 * 1000);
                K8sDeployTrendData trendData;
                if (endDate.compareTo(toDate) >= 0) {
                  trendData = getTrendData(deployLogListSelf, beginDate, toDate);
                } else {
                  trendData = getTrendData(deployLogListSelf, beginDate, endDate);
                }
                trendDataList.add(trendData);
                i++;
              }
              //更多的按周
            } else if (timeTrend > 35L * 24 * 60 * 60 * 1000) {
              long i = 7;
              while (new Date(fromDate.getTime() + i * 24 * 60 * 60 * 1000).compareTo(toDate) <= 0) {
                Date beginDate = new Date(fromDate.getTime() + (i - 7) * 24 * 60 * 60 * 1000);
                Date endDate = new Date(fromDate.getTime() + i * 24 * 60 * 60 * 1000);
                K8sDeployTrendData trendData;
                if (endDate.compareTo(toDate) >= 0) {
                  trendData = getTrendData(deployLogListSelf, beginDate, toDate);
                } else {
                  trendData = getTrendData(deployLogListSelf, beginDate, endDate);
                }
                trendDataList.add(trendData);
                i += 7;
              }
            }
            deployLogListSelf.clear();
          }
          break;
        default:
          break;
      }
    }
    return trendDataList;
  }
  
  /**
   * 取的某个时间段的对应的发布数据,从取得的数据里面
   *
   * @param deployLogList
   * @param beginTime
   * @param endDate
   * @return
   */
  private K8sDeployTrendData getTrendData(List<ModuleDeployLogVo> deployLogList, Date beginTime, Date endDate) {
    K8sDeployTrendData trendData = new K8sDeployTrendData();
    int count = 0;
    Map<Integer, String> moduleLogMap = new HashMap<>();
    for (ModuleDeployLog deployLog : deployLogList) {
      if (deployLog.getCreateTime().compareTo(beginTime) > 0
          && deployLog.getCreateTime().compareTo(endDate) <= 0) {
        moduleLogMap.put(deployLog.getModuleId(), deployLog.getOpActive());
      }
    }
    if (moduleLogMap.size() > 0) {
      Set<Integer> keySet = moduleLogMap.keySet();
      for (Integer moduleId : keySet) {
        //判断如果最新的一条数据不是下线模块,即成功判断该条数据为成功发布模块数
        if (!OP_OFFLINE_MODULE.equals(moduleLogMap.get(moduleId))) {
          count++;
        }
      }
    }
    trendData.setCount(count);
    trendData.setTime(endDate);
    return trendData;
  }
  
  private int getModuleCount(Date begin, Date end, Integer envId) {
    int count = 0;
    List<ModuleDeployLogVo> deployLogList = getModuleList(begin, end, envId);
    if (deployLogList != null && deployLogList.size() > 0) {
      Map<Integer, String> moduleLogMap = new HashMap<>();
      //模块去重设置
      for (ModuleDeployLogVo deployLogVo : deployLogList) {
        moduleLogMap.put(deployLogVo.getModuleId(), deployLogVo.getOpActive());
      }
      if (moduleLogMap.size() > 0) {
        Set<Integer> keySet = moduleLogMap.keySet();
        for (Integer moduleId : keySet) {
          if (!"下线模块".equals(moduleLogMap.get(moduleId))) {
            count++;
          }
        }
      }
    }
    return count;
  }
  
  /**
   * 查询某个环境下时间段的成功操作的发布log主要取三种操作的记录
   *
   * @param begin
   * @param end
   * @param envId
   * @return
   */
  private List<ModuleDeployLogVo> getModuleList(Date begin, Date end, Integer envId) {
    List<String> opActives = new ArrayList<>();
    opActives.add(OP_OFFLINE_MODULE);
    opActives.add(OP_ONLINE_MODULE);
    opActives.add(OP_CHANGE_MIRROR);
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    ModuleDeployLogSelectParam logSelectParam = new ModuleDeployLogSelectParam();
    if (begin != null) {
      logSelectParam.setBeginTime(dateFormat.format(begin));
    }
    if (end != null) {
      logSelectParam.setEndTime(dateFormat.format(end));
    }
    logSelectParam.setOpActives(opActives);
    logSelectParam.setEnvId(envId);
    
    return deployLogMapper.selectAllDeployedModule(logSelectParam);
  }
  
  /**
   * 查询各环境模块发布次数TOP10
   */
  public Map<String, List<ModuleDeployStatisticsDTO>> getDeploystatistics(ModuleManageSelectParamVo manageSelectParamVo) {
    Map<String, List<ModuleDeployStatisticsDTO>> resultMap = new HashMap<>();
    List<ModuleDeployStatisticsDTO> moduleDeployStatisticsDTOS =
        deployLogMapper.selectModuleDeployOrderByTime(manageSelectParamVo);
    int topCount = 10;
    if (manageSelectParamVo.getTopCount() > 0 && manageSelectParamVo.getTopCount() <= 50) {
      topCount = manageSelectParamVo.getTopCount();
    }
    for (Integer envid : manageSelectParamVo.getEnvIds()) {
      List<ModuleDeployStatisticsDTO> remoduleDeployStatisticsDTOS = new ArrayList<>();
      for (ModuleDeployStatisticsDTO statisticsDTO : moduleDeployStatisticsDTOS) {
        if (envid.equals(statisticsDTO.getEnvId())) {
          remoduleDeployStatisticsDTOS.add(statisticsDTO);
          resultMap.put(statisticsDTO.getMark(), remoduleDeployStatisticsDTOS);
        }
        if (remoduleDeployStatisticsDTOS.size() == topCount) {
          break;
        }
      }
    }
    resultMap.put("总排行", moduleDeployStatisticsDTOS.subList(0,
        moduleDeployStatisticsDTOS.size() > topCount ? topCount : moduleDeployStatisticsDTOS.size()));
    return resultMap;
  }
  
  /**
   * 获取发布次数的统计数据
   *
   * @param envId
   * @param type
   * @param fDate
   * @param tDate
   * @return
   */
  @Override
  public List<K8sDeployTrendData> getDeploySelfData(Integer envId, Integer type,
                                                    Date fDate, Date tDate) {
    List<K8sDeployTrendData> deployTrendDataList = new LinkedList<>();
    if (fDate != null && tDate != null && envId != null) {
      List<String> opActives = new ArrayList<>();
      opActives.add(OP_ONLINE_MODULE);
      opActives.add(OP_CHANGE_MIRROR);
      SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
      ModuleDeployLogSelectParam logSelectParam = new ModuleDeployLogSelectParam();
      
      logSelectParam.setBeginTime(dateFormat.format(fDate));
      logSelectParam.setEndTime(dateFormat.format(tDate));
      
      logSelectParam.setOpActives(opActives);
      logSelectParam.setEnvId(envId);
      
      List<ModuleDeployLogVo> moduleDeployLogVoList =
          deployLogMapper.selectAllDeployedModule(logSelectParam);
      
      long i = 1;
      while (new Date(fDate.getTime() + i * 24 * 60 * 60 * 1000).compareTo(tDate) <= 0) {
        Date beginDate = new Date(fDate.getTime() + (i - 1) * 24 * 60 * 60 * 1000);
        Date endDate = new Date(fDate.getTime() + i * 24 * 60 * 60 * 1000);
        K8sDeployTrendData deployTrendData = new K8sDeployTrendData();
        int count = 0;
        for (ModuleDeployLog deployLog : moduleDeployLogVoList) {
          if (deployLog.getCreateTime().compareTo(beginDate) > 0
              && deployLog.getCreateTime().compareTo(endDate) <= 0) {
            count++;
          }
        }
        deployTrendData.setTime(endDate);
        deployTrendData.setCount(count);
        deployTrendDataList.add(deployTrendData);
        i++;
      }
    }
    return deployTrendDataList;
  }
  
  /**
   * 获取指定时间发布变更日志
   *
   * @param moduleDeployLogSelectParam
   * @return
   */
  @Override
  public List<ModuleDeployStatisticsDTO> getDeployChangeLog(ModuleDeployLogSelectParam moduleDeployLogSelectParam) {
    List<String> opActives = new ArrayList<>();
    opActives.add(OP_OFFLINE_MODULE);
    opActives.add(OP_ONLINE_MODULE);
    opActives.add(OP_CHANGE_MIRROR);
    opActives.add(OP_HOT_UPDATE_DEPLOYMENT);
    opActives.add(OP_SCALE_SIZE);
    moduleDeployLogSelectParam.setOpActives(opActives);
    return deployLogMapper.selectModuleDeployChangeLogByTime(moduleDeployLogSelectParam);
  }
  
  @Override
  public ModuleDeployLog getLastOffline(Integer moduleId, Integer envId) {
    return deployLogMapper.selectLastOffline(moduleId, envId);
  }
}
