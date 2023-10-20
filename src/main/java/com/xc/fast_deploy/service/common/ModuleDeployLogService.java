package com.xc.fast_deploy.service.common;

import com.xc.fast_deploy.dto.k8s.K8sDeployCountDTO;
import com.xc.fast_deploy.dto.k8s.K8sDeployTrendData;
import com.xc.fast_deploy.dto.module.ModuleDeployStatisticsDTO;
import com.xc.fast_deploy.dto.module.ModuleMirrorDTO;
import com.xc.fast_deploy.model.master_model.ModuleDeployLog;
import com.xc.fast_deploy.vo.module_vo.param.ModuleDeployLogSelectParam;
import com.xc.fast_deploy.vo.module_vo.param.ModuleDeployYamlSelectParamVo;
import com.xc.fast_deploy.vo.module_vo.param.ModuleManageSelectParamVo;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface ModuleDeployLogService extends BaseService<ModuleDeployLog, Integer> {
  
  List<ModuleDeployLog> selectInfoById(Integer moduleId, Integer envId, String record);
  
  K8sDeployCountDTO getHomeData(Integer envId);
  
  List<K8sDeployTrendData> getTrendHomeData(Integer id, Integer envId, Date fromDate, Date toDate);
  
  Map<String, List<ModuleDeployStatisticsDTO>> getDeploystatistics(ModuleManageSelectParamVo manageSelectParamVo);
  
  List<K8sDeployTrendData> getDeploySelfData(Integer envId, Integer type, Date fDate, Date tDate);
  
  List<ModuleDeployStatisticsDTO> getDeployChangeLog(ModuleDeployLogSelectParam moduleDeployLogSelectParam);
  
  ModuleDeployLog getLastOffline(Integer moduleId, Integer envId);
}
