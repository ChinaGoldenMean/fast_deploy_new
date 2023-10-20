package com.xc.fast_deploy.service.common;

import com.xc.fast_deploy.dto.ResponseDTO;
import com.xc.fast_deploy.dto.k8s.K8sPodDTO;
import com.xc.fast_deploy.dto.k8s.K8sServiceDTO;
import com.xc.fast_deploy.dto.module.ModuleDeployEnvDTO;
import com.xc.fast_deploy.model.master_model.ModuleDeploy;
import com.xc.fast_deploy.model.master_model.ModuleDeployYaml;
import com.xc.fast_deploy.model.master_model.ModuleUser;
import com.xc.fast_deploy.model.master_model.ModuleYamlDiff;
import com.xc.fast_deploy.vo.k8s_vo.K8sStrategyParamVO;
import com.xc.fast_deploy.vo.k8s_vo.K8sUpdateResourceParamVO;
import com.xc.fast_deploy.vo.module_vo.ModuleDeployVo;
import com.xc.fast_deploy.vo.module_vo.ModuleEnvCenterManageVo;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface ModuleDeployService extends BaseService<ModuleDeploy, Integer> {
  
  List<ModuleDeployEnvDTO> selectModuleEnvCenterAll(Set<Integer> envIds, String userId);
  
  List<K8sPodDTO> getDeployModuleInfo(Integer moduleId, Integer envId);
  
  boolean deployModuleInEnv(Integer moduleId, Integer envId, Integer mirrorId, ModuleUser moduleUser);
  
  boolean scaleModuleSize(Integer moduleId, Integer moduleSize, Integer envId, ModuleUser moduleUser);
  
  boolean offline(Integer moduleId, Integer envId, ModuleUser moduleUser);
  
  ResponseDTO changeMirror(Integer moduleId, Integer envId, Integer mirrorId, ModuleUser moduleUser);
  
  boolean deploySvc(Integer moduleId, Integer envId, ModuleUser moduleUser);
  
  K8sServiceDTO getSvcInfo(Integer moduleId, Integer envId);
  
  boolean delPods(Integer envId, Integer moduleId, String[] podNames, ModuleUser moduleUser);
  
  boolean delSvc(Integer moduleId, Integer envId, ModuleUser moduleUser);
  
  Map<String, String> clearMemoryPods(Integer envId, Integer moduleId, String[] podNames, ModuleUser moduleUser);
  
  List<ModuleDeployYaml> getModuleDeployByModuleAndEnvId(Integer moduleId, Integer envId, boolean IsOnlineYaml);
  
  boolean createAutoScaleSize(Integer envId, Integer moduleId, Integer minReplicas,
                              Integer maxReplicas, Integer cpuPercentage, ModuleUser moduleUser);
  
  boolean deleteAutoScaleHpa(Integer envId, Integer moduleId, ModuleUser moduleUser);
  
  String getAutoScaleHpa(Integer envId, Integer moduleId);
  
  Map<String, Set<String>> getAllConfigMaps(Integer envId);
  
  Map<String, String> getDeploymentArgsByType(Integer envId, Integer moduleId, Integer argsType);
  
  boolean hotUpdateDeployments(Integer envId, Integer moduleId, ModuleUser moduleUser,
                               Integer argsType, K8sUpdateResourceParamVO paramVO);
  
  List<ModuleEnvCenterManageVo> selectByDeployVO(ModuleDeployVo deployVo);
  
  File getThreadDumpFileFromPod(String podName, Integer envId, String moduleId);
  
  List<Map<String, String>> getModuleVolumes(Integer envId, Integer moduleId);
  
  boolean replaceModuleYaml(Integer moduleId, Integer envId, ModuleUser moduleUser, Integer argsType);
  
  ResponseDTO compareYamlConfig(Integer moduleId, Integer envId);
  
  List<ModuleYamlDiff> getYamlCompareResult();
  
  ResponseDTO changeStrategyArgs(Integer moduleId, Integer envId, K8sStrategyParamVO strategyParamVO);
  
  ResponseDTO changeStrategyArgs2(Integer moduleId, Integer envId, String maxUnavailable);
  
  Map<String, Integer> getStrategyArgs(Integer moduleId, Integer envId);
//    ModuleDeployHomeDTO getHomeDeploySourceData(Integer envId);
}
