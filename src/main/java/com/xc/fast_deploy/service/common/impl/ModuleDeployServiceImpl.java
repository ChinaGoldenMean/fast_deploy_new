package com.xc.fast_deploy.service.common.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.xc.fast_deploy.dao.master_dao.*;
import com.xc.fast_deploy.dto.ResponseDTO;
import com.xc.fast_deploy.dto.k8s.K8sContainerDTO;
import com.xc.fast_deploy.dto.k8s.K8sPodDTO;
import com.xc.fast_deploy.dto.k8s.K8sServiceDTO;
import com.xc.fast_deploy.dto.k8s.K8sServicePortDTO;
import com.xc.fast_deploy.dto.module.ModuleDeployEnvDTO;
import com.xc.fast_deploy.model.master_model.*;
import com.xc.fast_deploy.model.master_model.example.ModuleDeployExample;
import com.xc.fast_deploy.model.master_model.example.ModuleDeployYamlExample;
import com.xc.fast_deploy.model.slave_model.BillingOpOnOff;
import com.xc.fast_deploy.myException.DeployIsOnlineExcetion;
import com.xc.fast_deploy.myException.K8SDeployException;
import com.xc.fast_deploy.myException.TransYaml2K8sVoException;
import com.xc.fast_deploy.myenum.IsOnlineYamlEnum;
import com.xc.fast_deploy.myenum.ModuleMirrorUsedEnum;
import com.xc.fast_deploy.myenum.UserRoleTypeEnum;
import com.xc.fast_deploy.myenum.k8sEnum.K8sApiversionTypeEnum;
import com.xc.fast_deploy.myenum.k8sEnum.K8sContainerStatusEnum;
import com.xc.fast_deploy.myenum.k8sEnum.K8sDeploymentResourceEnum;
import com.xc.fast_deploy.myenum.k8sEnum.K8sKindTypeEnum;
import com.xc.fast_deploy.service.common.ModuleDeployLogService;
import com.xc.fast_deploy.service.common.ModuleDeployService;
import com.xc.fast_deploy.service.common.SyncService;
import com.xc.fast_deploy.service.k8s.K8sPodService;
import com.xc.fast_deploy.service.k8s.K8sService;
import com.xc.fast_deploy.service.slave.IBillingOpOnOffService;
import com.xc.fast_deploy.utils.BufferModuleMangeUtils;
import com.xc.fast_deploy.utils.DateUtils;
import com.xc.fast_deploy.utils.HttpUtils;
import com.xc.fast_deploy.utils.MyZipUtils;
import com.xc.fast_deploy.utils.constant.K8sHttpUrlConstants;
import com.xc.fast_deploy.utils.constant.K8sNameSpace;
import com.xc.fast_deploy.utils.constant.K8sPatchMirror;
import com.xc.fast_deploy.utils.k8s.K8sExceptionUtils;
import com.xc.fast_deploy.utils.k8s.K8sManagement;
import com.xc.fast_deploy.utils.k8s.K8sUtils;
import com.xc.fast_deploy.vo.K8sPatchVo;
import com.xc.fast_deploy.vo.K8sYamlVo;
import com.xc.fast_deploy.vo.k8s_vo.K8sNodeResourcesVO;
import com.xc.fast_deploy.vo.k8s_vo.K8sStrategyParamVO;
import com.xc.fast_deploy.vo.k8s_vo.K8sUpdateResourceParamVO;
import com.xc.fast_deploy.vo.module_vo.ModuleDeployVo;
import com.xc.fast_deploy.vo.module_vo.ModuleEnvCenterManageVo;
import com.xc.fast_deploy.vo.module_vo.permission.ModuleUserEnvRoleVo;
import io.kubernetes.client.Copy;
import io.kubernetes.client.Exec;
import io.kubernetes.client.custom.Quantity;
import io.kubernetes.client.custom.V1Patch;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.AutoscalingV1Api;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.apis.AppsV1Api;
import io.kubernetes.client.openapi.apis.AutoscalingV1Api;
import io.kubernetes.client.openapi.apis.CoreV1Api;

import io.kubernetes.client.openapi.models.*;
import io.kubernetes.client.util.Yaml;
import io.kubernetes.client.util.exception.CopyNotSupportedException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.yaml.snakeyaml.reader.ReaderException;
import org.yaml.snakeyaml.scanner.ScannerException;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static com.xc.fast_deploy.utils.constant.HarborContants.CONTACT;
import static com.xc.fast_deploy.utils.constant.K8sHttpUrlConstants.*;
import static com.xc.fast_deploy.utils.constant.K8sOPActive.*;

@Service
@Slf4j
public class ModuleDeployServiceImpl extends BaseServiceImpl<ModuleDeploy, Integer> implements ModuleDeployService {
  
  @Autowired
  private ModuleDeployMapper deployMapper;
  @Autowired
  private ModuleDeployYamlMapper deployYamlMapper;
  @Autowired
  private ModuleEnvMapper envMapper;
  @Autowired
  private PModuleUserMapper userMapper;
  @Autowired
  private ModuleMirrorMapper mirrorMapper;
  @Autowired
  private K8sService k8sService;
  @Autowired
  private IBillingOpOnOffService onOffService;
  
  @Autowired
  private K8sPodService k8sPodService;
  @Autowired
  private SyncService syncService;
  @Autowired
  private ModuleDeployLogService moduleDeployLogService;
  
  @Value("${file.storge.path.prefix}")
  private String storgePrefix;
  
  @Value("${file.storge.path.moduleExportPath}")
  private String moduleExportPath;
  
  @Value("${myself.httpUrl.getNodeResources}")
  private String getNodeResources;
  
  @Value("${myself.pspass.oldversion}")
  private Integer[] oldEnvId;
  
  @PostConstruct
  public void init() {
    super.init(deployMapper);
  }
  
  /**
   * 获取所有已经建立发布的模块和环境的关系数据
   *
   * @param envIds
   * @return
   */
  @Override
  public List<ModuleDeployEnvDTO> selectModuleEnvCenterAll(Set<Integer> envIds, String userId) {
    ModuleDeployVo deployVo = new ModuleDeployVo();
//        Set<Integer> centerIds = userMapper.selectUserIdAllCenters(userId,null);
//        centerIds.remove(null);
//        if (centerIds != null && centerIds.size() > 0) {
//            deployVo.setCenterIds(centerIds);
//        }
    deployVo.setEnvIds(envIds);
    List<ModuleEnvCenterManageVo> deployList = deployMapper.selectModuleEnvCenterAll(deployVo);
    return BufferModuleMangeUtils.bufferModuleEnvCenterManage(deployList);
  }
  
  /**
   * 获取已经发布的 模块对应的pod的信息
   *
   * @param moduleId
   * @param envId
   * @return
   */
  @Override
  public List<K8sPodDTO> getDeployModuleInfo(Integer moduleId, Integer envId) {
    List<K8sPodDTO> podDTOS = new ArrayList<>();
    //获取发布的yaml信息
    List<ModuleDeployYaml> deployYamls = getModuleDeployByModuleAndEnvId(moduleId, envId, true);
    //也是验证该模块在该 环境下只有一次发布
    if (deployYamls != null && deployYamls.size() > 0) {
      String yamlName = deployYamls.get(0).getYamlName();
      String containerName = deployYamls.get(0).getContainerName();
      StringBuilder sb = new StringBuilder();

//            if (StringUtils.isNotBlank(containerName)) {
//                sb.append(containerName);
//            } else {
      sb.append("name=").append(yamlName);
//            }
      //获取环境信息
      ModuleEnv moduleEnv = envMapper.selectOne(envId);
      //连接k8s api操作
      CoreV1Api coreV1Api = K8sManagement.getCoreV1Api(moduleEnv.getK8sConfig());
      
      List<V1Pod> v1Pods = k8sPodService.getPodInfoByName(coreV1Api,
          yamlName, deployYamls.get(0).getYamlNamespace(), sb.toString());
      
      //封装数据
      if (v1Pods != null && v1Pods.size() > 0) {
        for (V1Pod pod : v1Pods) {
          if (!"Failed".equals(pod.getStatus().getPhase())) {
            K8sPodDTO k8sPodDTO = new K8sPodDTO();
            k8sPodDTO.setPodName(pod.getMetadata().getName());
            k8sPodDTO.setContainerSize(pod.getSpec().getContainers().size());
            k8sPodDTO.setNodeIP(pod.getStatus().getHostIP());
            k8sPodDTO.setPodIP(pod.getStatus().getPodIP());
            k8sPodDTO.setYamlNamespace(deployYamls.get(0).getYamlNamespace());
            k8sPodDTO.setStatus(pod.getStatus().getPhase());
            if (pod.getStatus().getStartTime() != null) {
              k8sPodDTO.setStartTime(Date.from(pod.getStatus().getStartTime().toInstant()));
            }
            int containerRunningSize = 0;
            List<V1ContainerStatus> containerStatuses = pod.getStatus().getContainerStatuses();
            List<K8sContainerDTO> containerDTOList = new ArrayList<>();
            if (containerStatuses != null && containerStatuses.size() > 0) {
              for (V1ContainerStatus v1ContainerStatus : containerStatuses) {
                K8sContainerDTO containerDTO = new K8sContainerDTO();
                if (v1ContainerStatus.getName().equals(yamlName)) {
                  k8sPodDTO.setMainContainerName(v1ContainerStatus.getName());
                  k8sPodDTO.setMainContainerMirrorName(v1ContainerStatus.getImage());
                  k8sPodDTO.setMainContainerRestartCount(v1ContainerStatus.getRestartCount());
                } else {
                  k8sPodDTO.setMainContainerName(containerStatuses.get(0).getName());
                  k8sPodDTO.setMainContainerMirrorName(containerStatuses.get(0).getImage());
                  k8sPodDTO.setMainContainerRestartCount(containerStatuses.get(0).getRestartCount());
                }
                
                containerDTO.setContainerName(v1ContainerStatus.getName());
                containerDTO.setImageName(v1ContainerStatus.getImage());
                containerDTO.setRestartCount(v1ContainerStatus.getRestartCount());
                
                V1ContainerStateRunning running = v1ContainerStatus.getState().getRunning();
                V1ContainerStateTerminated terminated =
                    v1ContainerStatus.getState().getTerminated();
                V1ContainerStateWaiting waiting = v1ContainerStatus.getState().getWaiting();
                
                if (v1ContainerStatus.getReady() && running != null) {
                  containerRunningSize++;
                  Date date = Date.from(running.getStartedAt().toInstant());
                  SimpleDateFormat dateFormat =
                      new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                  String s = dateFormat.format(date);
                  containerDTO.setDescribeMsg("start time: " + s);
                  containerDTO.setStatus(K8sContainerStatusEnum.RUNNINT.getStatus());
                }
                if (terminated != null) {
                  containerDTO.setDescribeMsg(terminated.getReason());
                  containerDTO.setStatus(K8sContainerStatusEnum.TERMINATED.getStatus());
                  k8sPodDTO.setStatus(K8sContainerStatusEnum.TERMINATED.getStatus());
                }
                if (waiting != null) {
                  containerDTO.setDescribeMsg(waiting.getReason());
                  containerDTO.setStatus(K8sContainerStatusEnum.WAITING.getStatus());
                  k8sPodDTO.setStatus(waiting.getReason());
                }
                containerDTOList.add(containerDTO);
              }
              k8sPodDTO.setContainerDTOList(containerDTOList);
              k8sPodDTO.setContainerRunningSize(containerRunningSize);
            }
            if (StringUtils.isNotBlank(pod.getStatus().getReason())) {
              k8sPodDTO.setStatus(pod.getStatus().getReason());
            }
            podDTOS.add(k8sPodDTO);
          }
        }
        //清除数据
        v1Pods.clear();
      }
    }
    return podDTOS;
  }
  
  /**
   * 根据moduleId和envId 查询发布上线的yaml的信息
   *
   * @param moduleId
   * @param envId
   * @return
   */
  public List<ModuleDeployYaml> getModuleDeployByModuleAndEnvId(Integer moduleId,
                                                                Integer envId,
                                                                boolean IsOnlineYaml) {
    List<ModuleDeployYaml> moduleDeployYamls = null;
    if (moduleId != null && envId != null) {
      ModuleDeployExample deployExample = new ModuleDeployExample();
      ModuleDeployExample.Criteria criteria = deployExample.createCriteria();
      criteria.andModuleIdEqualTo(moduleId).andEnvIdEqualTo(envId).andIsDeleteEqualTo(0);
      List<ModuleDeploy> moduleDeploys = deployMapper.selectByExample(deployExample);
      //单模块在某个环境中只能做一次发布配置
      if (moduleDeploys != null && moduleDeploys.size() == 1) {
        Integer deployId = moduleDeploys.get(0).getId();
//                String genernateName = moduleDeploys.get(0).getGenernateName();
        ModuleDeployYamlExample deployYamlExample = new ModuleDeployYamlExample();
        
        ModuleDeployYamlExample.Criteria yamlCriteria = deployYamlExample.createCriteria();
        
        yamlCriteria.andDeployIdEqualTo(deployId);
        
        if (IsOnlineYaml) {
          yamlCriteria.andIsOnlineYamlEqualTo(IsOnlineYamlEnum.IS_ONLINE_YAML.getCode());
        }
//                moduleDeployYamls = deployYamlMapper.selectByExample(deployYamlExample);
        moduleDeployYamls = deployYamlMapper.selectByExampleWithBLOBs(deployYamlExample);
      }
    }
    return moduleDeployYamls;
  }
  
  /**
   * 创建自动扩缩容
   *
   * @param envId
   * @param moduleId
   * @param minReplicas
   * @param maxReplicas
   * @param cpuPercentage
   * @param moduleUser
   * @return
   */
  @Override
  public boolean createAutoScaleSize(Integer envId, Integer moduleId, Integer minReplicas,
                                     Integer maxReplicas, Integer cpuPercentage, ModuleUser moduleUser) {
    boolean success = false;
    ModuleEnv moduleEnv = envMapper.selectOne(envId);
    List<ModuleDeployYaml> deployYamls = getModuleDeployByModuleAndEnvId(moduleId, envId, true);
    if (moduleEnv != null && deployYamls != null && deployYamls.size() > 0) {
      AutoscalingV1Api autoscalingV1Api = K8sManagement.getAutoscalingV1Api(moduleEnv.getK8sConfig());
      V1HorizontalPodAutoscaler body = new V1HorizontalPodAutoscaler();
      V1HorizontalPodAutoscalerSpec autoscalerSpec = new V1HorizontalPodAutoscalerSpec();
      Map<String, Object> argsMap = new HashMap<>();
      autoscalerSpec.setMinReplicas(minReplicas);
      autoscalerSpec.setMaxReplicas(maxReplicas);
      autoscalerSpec.setTargetCPUUtilizationPercentage(cpuPercentage);
      V1CrossVersionObjectReference objectReference = new V1CrossVersionObjectReference();
      objectReference.setKind("Deployment");
      objectReference.setName(deployYamls.get(0).getYamlName());
      objectReference.setApiVersion("extensions/v1beta1");
      autoscalerSpec.setScaleTargetRef(objectReference);
      body.setSpec(autoscalerSpec);
      body.setApiVersion("autoscaling/v1");
      body.setKind("HorizontalPodAutoscaler");
      V1ObjectMeta objectMeta = new V1ObjectMeta();
      objectMeta.setName(deployYamls.get(0).getYamlName());
      body.setMetadata(objectMeta);
      argsMap.put("minReplicas", minReplicas);
      argsMap.put("maxReplicas", maxReplicas);
      argsMap.put("cpuPercentage", cpuPercentage);
      argsMap.put("deploymentName", deployYamls.get(0).getYamlName());
      try {
        autoscalingV1Api.createNamespacedHorizontalPodAutoscaler(deployYamls.get(0).getYamlNamespace(),
            body, null, null, null, null);
        success = true;
      } catch (ApiException e) {
        log.error("创建HPA出错：{}", e.getMessage());
      }
      //异步保存信息
      syncService.saveModuleDeployLog(moduleDeployLogService,
          moduleId, envId, deployYamls.get(0).getDeployId(), OP_AUTOSCALE_CREATE,
          argsMap, success, moduleUser.getId(), moduleUser.getUsername());
    }
    return success;
  }
  
  /**
   * 删除自动扩缩容
   *
   * @param envId
   * @param moduleId
   * @param moduleUser
   * @return
   */
  @Override
  public boolean deleteAutoScaleHpa(Integer envId, Integer moduleId, ModuleUser moduleUser) {
    boolean success = false;
    ModuleEnv moduleEnv = envMapper.selectOne(envId);
    List<ModuleDeployYaml> deployYamls = getModuleDeployByModuleAndEnvId(moduleId, envId, true);
    if (moduleEnv != null && deployYamls != null && deployYamls.size() > 0) {
      Map<String, Object> argsMap = new HashMap<>();
      AutoscalingV1Api autoscalingV1Api = K8sManagement.getAutoscalingV1Api(moduleEnv.getK8sConfig());
      V1DeleteOptions deleteOptions = new V1DeleteOptions();
      deleteOptions.setOrphanDependents(false);
      argsMap.put("deploymentName", deployYamls.get(0).getYamlName());
      try {
        autoscalingV1Api.deleteNamespacedHorizontalPodAutoscaler(deployYamls.get(0).getYamlName(), deployYamls.get(0).getYamlNamespace(),
            null, null, 10, null,
            null, deleteOptions);
        success = true;
      } catch (ApiException e) {
        log.error("删除hpa 出现错误");
        e.printStackTrace();
      } catch (JsonSyntaxException e) {
        log.error("出现某不知名的错误!!!");
        return K8sExceptionUtils.anylse(e);
      }
      syncService.saveModuleDeployLog(moduleDeployLogService,
          moduleId, envId, deployYamls.get(0).getDeployId(), OP_AUTOSCALE_DELETE,
          argsMap, success, moduleUser.getId(), moduleUser.getUsername());
    }
    return success;
  }
  
  @Override
  public String getAutoScaleHpa(Integer envId, Integer moduleId) {
//        V1HorizontalPodAutoscaler autoscaler = null;
    String back = null;
    ModuleEnv moduleEnv = envMapper.selectOne(envId);
    List<ModuleDeployYaml> deployYamls = getModuleDeployByModuleAndEnvId(moduleId, envId, true);
    if (moduleEnv != null && deployYamls != null && deployYamls.size() > 0) {
//            AutoscalingV1Api autoscalingV1Api = K8sManagement.getAutoscalingV1Api(moduleEnv.getK8sConfig());
      back = K8sUtils.okhttpGetBack(moduleEnv.getK8sConfig(),
          "/apis/autoscaling/v1/namespaces/" +
              deployYamls.get(0).getYamlNamespace() +
              "/horizontalpodautoscalers/" +
              deployYamls.get(0).getYamlName());
      
    }
    return back;
  }
  
  /**
   * 获取某个环境的所有configMaps
   *
   * @param envId
   */
  @Override
  public Map<String, Set<String>> getAllConfigMaps(Integer envId) {
    ModuleEnv moduleEnv = envMapper.selectOne(envId);
    Map<String, Set<String>> configMap = new HashMap<>();
    if (moduleEnv != null) {
      CoreV1Api coreV1Api = K8sManagement.getCoreV1Api(moduleEnv.getK8sConfig());
      try {
        V1ConfigMapList v1ConfigMapList = coreV1Api.listNamespacedConfigMap(K8sNameSpace.DEFAULT,
            null, null, null, null
            , null, null, null, null, null, null);
        if (v1ConfigMapList != null && v1ConfigMapList.getItems() != null
            && v1ConfigMapList.getItems().size() > 0) {
          for (V1ConfigMap v1ConfigMap : v1ConfigMapList.getItems()) {
            
            Map<String, String> data = v1ConfigMap.getData();
            if (data != null && data.size() > 0) {
              configMap.put(v1ConfigMap.getMetadata().getName(), data.keySet());
            } else {
              configMap.put(v1ConfigMap.getMetadata().getName(), null);
            }
            
          }
        }
      } catch (ApiException e) {
        e.printStackTrace();
      }
    }
    return configMap;
  }
  
  /**
   * 获取deployment的资源参数
   *
   * @param envId
   * @param moduleId
   * @param argsType
   * @return
   */
  @Override
  public Map<String, String> getDeploymentArgsByType(Integer envId, Integer moduleId, Integer argsType) {
    Map<String, String> backMap = new HashMap<>();
    ModuleEnv moduleEnv = envMapper.selectOne(envId);
    if (moduleEnv != null) {
      List<ModuleDeployYaml> moduleDeployYamls = getModuleDeployByModuleAndEnvId(moduleId,
          envId, true);
      if (moduleDeployYamls != null && moduleDeployYamls.size() > 0) {
        ModuleDeployYaml deployYaml = moduleDeployYamls.get(0);
        String yamlName = deployYaml.getYamlName();
        V1Deployment deployment =
            k8sService.readNameSpacedResource(yamlName,
                moduleEnv, deployYaml.getYamlNamespace(),
                K8sKindTypeEnum.DEPLOYMENT.getKindType(), V1Deployment.class);
        if (deployment != null) {
          K8sDeploymentResourceEnum resourceEnum = K8sDeploymentResourceEnum.getByCode(argsType);
          if (resourceEnum != null) {
            switch (resourceEnum) {
              case DEPLOYMENT_LABEL_ARGS:
                backMap = deployment.getSpec().getTemplate()
                    .getSpec().getNodeSelector();
                break;
              case DEPLOYMENT_LIMIT_ARGS:
                List<V1Container> containers = deployment.getSpec().getTemplate()
                    .getSpec().getContainers();
                if (containers != null && containers.size() > 0) {
                  for (V1Container container : containers) {
                    if (deployment.getMetadata().getName()
                        .equals(container.getName())) {
                      V1ResourceRequirements resources = container.getResources();
                      if (resources != null) {
                        Map<String, Quantity> limits = resources.getLimits();
                        if (limits != null) {
                          Set<String> keySet = limits.keySet();
                          for (String key : keySet) {
                            backMap.put(key, limits.get(key).toSuffixedString());
                          }
                        }
                      }
                      break;
                    }
                  }
                }
                break;
              default:
                break;
            }
          }
        }
      }
    }
    return backMap;
  }
  
  /**
   * 获取模块的挂载卷
   *
   * @param envId
   * @param moduleId
   * @return
   */
  @Override
  public List<Map<String, String>> getModuleVolumes(Integer envId, Integer moduleId) {
    List<Map<String, String>> volumeList = new ArrayList<>();
    ModuleEnv moduleEnv = envMapper.selectOne(envId);
    if (moduleEnv != null) {
      List<ModuleDeployYaml> deployYamls =
          getModuleDeployByModuleAndEnvId(moduleId, envId, true);
      if (deployYamls != null && deployYamls.size() > 0) {
        ModuleDeployYaml deployYaml = deployYamls.get(0);
        V1Deployment deployment =
            k8sService.readNameSpacedResource(deployYaml.getYamlName(),
                moduleEnv, deployYaml.getYamlNamespace(),
                K8sKindTypeEnum.DEPLOYMENT.getKindType(),
                V1Deployment.class);
        List<V1Volume> v1Volumes =
            deployment.getSpec().getTemplate().getSpec().getVolumes();
        List<V1VolumeMount> v1VolumeMounts =
            deployment.getSpec().getTemplate().getSpec().getContainers().get(0).getVolumeMounts();
        for (V1Volume v1Volume : v1Volumes) {
          Map<String, String> volumeMap = new LinkedHashMap<>();
          volumeMap.put("name", v1Volume.getName());
          volumeMap.put("volumePath", v1Volume.getHostPath().getPath());
          volumeList.add(volumeMap);
        }
        for (V1VolumeMount v1VolumeMount : v1VolumeMounts) {
          for (Map<String, String> map : volumeList) {
            if (map.containsValue(v1VolumeMount.getName())) {
              map.put("mountPath", v1VolumeMount.getMountPath());
              break;
            }
          }
        }
      }
    }
    return volumeList;
  }
  
  /**
   * 热配置修改deployment的配置参数
   *
   * @param envId
   * @param moduleId
   * @param moduleUser
   * @param argsType
   * @param paramVO
   * @return
   */
  @Override
  public boolean hotUpdateDeployments(Integer envId, Integer moduleId,
                                      ModuleUser moduleUser, Integer argsType,
                                      K8sUpdateResourceParamVO paramVO) {
    boolean success = false;
    Map<String, Object> argsMap = new HashMap<>();
    // argsMap.put("更新args:", paramVO);
    ModuleEnv moduleEnv = envMapper.selectOne(envId);
    if (moduleEnv != null) {
      List<ModuleDeployYaml> deployYamls =
          getModuleDeployByModuleAndEnvId(moduleId, envId, true);
      K8sDeploymentResourceEnum resourceEnum = K8sDeploymentResourceEnum.getByCode(argsType);
      Map<String, String> result = Maps.newHashMap();
      if (deployYamls != null && deployYamls.size() > 0 && resourceEnum != null) {
        //argsMap.put(resourceEnum.getMsg(), argsType);
        ModuleDeployYaml deployYaml = deployYamls.get(0);
        StringBuilder url = new StringBuilder();
        List<Integer> oldEnvList = Arrays.asList(oldEnvId);
        ArrayList<JsonObject> pathList = new ArrayList<>();
        V1Deployment deployment =
            k8sService.readNameSpacedResource(deployYaml.getYamlName(),
                moduleEnv, deployYaml.getYamlNamespace(),
                K8sKindTypeEnum.DEPLOYMENT.getKindType(),
                V1Deployment.class);
        try {
          switch (resourceEnum) {
            case DEPLOYMENT_LIMIT_ARGS:
              List<V1Container> containers =
                  deployment.getSpec().getTemplate().getSpec().getContainers();
              //获取原有的配置数据
              if (containers != null && containers.size() > 0) {
                V1ResourceRequirements resources = containers.get(0).getResources();
                if (resources != null) {
                  //  argsMap.put("原有的args: ", resources.getLimits());
                  Map<String, Quantity> limits = resources.getLimits();
                  Map<String, String> resourcesMap = new HashMap<>();
                  if (limits != null) {
                    for (String key : limits.keySet()) {
                      resourcesMap.put(key, limits.get(key).toSuffixedString());
                    }
                    argsMap.put("old", resourcesMap);
                  }
                } else {
                  throw new K8SDeployException("当前deployment未配置resource资源标签,无法更改");
                }
              }
              //判断新添加的配置数据
              if (StringUtils.isBlank(paramVO.getLimitCPU())
                  || StringUtils.isBlank(paramVO.getLimitMemory())) {
                result = null;
              } else {
                result.put("memory", paramVO.getLimitMemory());
                result.put("cpu", paramVO.getLimitCPU());
                argsMap.put("new", result);
              }
              //生成需要更改的数据
              pathList =
                  K8sUtils.generatePatchPath(K8sPatchMirror
                      .TEMPLATE_CONTAINERS_RESOURCE_LIMIT, result, null);
              break;
            case DEPLOYMENT_LABEL_ARGS:
              boolean flag = false;
              if (StringUtils.isBlank(paramVO.getLabelKey()) ||
                  StringUtils.isBlank(paramVO.getLabelValue())) {
                result = null;
              } else {
                result.put(paramVO.getLabelKey(), paramVO.getLabelValue());
                argsMap.put("new", result);
                if (deployment != null) {
                  Map<String, String> nodeSelector =
                      deployment.getSpec().getTemplate().getSpec().getNodeSelector();
                  argsMap.put("old", nodeSelector);
                  if (nodeSelector == null || nodeSelector.size() <= 0) {
                    flag = true;
                    pathList =
                        K8sUtils.generatePatchPath(K8sPatchMirror.TEMPLATE_SPEC_NODESELECTOR,
                            result, "add");
                  }
                }
              }
              
              if (!flag) {
                pathList =
                    K8sUtils.generatePatchPath(K8sPatchMirror.TEMPLATE_SPEC_NODESELECTOR,
                        result, null);
              }
              break;
            case DEPLOYMENT_VOLUME_MOUNTS:
//                            List<Map<String, String>> list = paramVO.getVolumeMapList();
//                            log.info("list:{}", list);
//                            List<V1Volume> v1VolumeList = new ArrayList<>();
//                            List<V1VolumeMount> v1VolumeMountList = new ArrayList<>();
//                            for (Map<String, String> map : list) {
//                                V1Volume v1Volume = new V1Volume();
//                                V1VolumeMount v1VolumeMount = new V1VolumeMount();
//                                V1HostPathVolumeSource v1HostPathVolumeSource = new V1HostPathVolumeSource();
//                                v1HostPathVolumeSource.setPath(map.get("volumePath"));
//                                v1Volume.setName(map.get("name"));
//                                v1Volume.setHostPath(v1HostPathVolumeSource);
//                                v1VolumeMount.setName(map.get("name"));
//                                v1VolumeMount.setMountPath(map.get("mountPath"));
//                                v1VolumeList.add(v1Volume);
//                                v1VolumeMountList.add(v1VolumeMount);
//                            }
//                            deployment.getSpec().getTemplate().getSpec().setVolumes(v1VolumeList);
//                            deployment.getSpec().getTemplate().getSpec().getContainers().get(0).setVolumeMounts(v1VolumeMountList);
//                            v1beta1Api.replaceNamespacedDeployment(deployYaml.getYamlName(), deployYaml.getYamlNamespace(),
//                                    deployment, null, null,null);
//                            success = true;
              break;
            case DEPLOYMENT_LABELS_NAME:
//                            if (StringUtils.isNotBlank(paramVO.getMetadataLabelsValue())) {
//                                result.put("name", paramVO.getMetadataLabelsValue());
//                                StringBuilder sb = new StringBuilder();
//                                String oldLabel = deployment.getSpec().getSelector().getMatchLabels().get("name");
//                                sb.append("name=").append(oldLabel);
//                                List<V1Pod> v1Pods = k8sPodService.getPodInfoByName(coreV1Api,
//                                        deployYaml.getYamlName(),
//                                        deployYamls.get(0).getYamlNamespace(), sb.toString());
//                                String podTemplateHash = v1Pods.get(0).getMetadata().getLabels().get("pod-template-hash");
//                                deployment.getMetadata().setLabels(result);
////                                deployment.getMetadata().setName(paramVO.getMetadataLabelsValue());
//                                deployment.getSpec().getSelector().setMatchLabels(result);
//                                deployment.getSpec().getTemplate().getMetadata().setLabels(result);
//                                deployment = v1beta1Api.replaceNamespacedDeployment(deployYaml.getYamlName(),
//                                        deployYaml.getYamlNamespace(), deployment, null, null,null);
//                                if (deployment.getMetadata().getLabels().get("name").equals(paramVO.getMetadataLabelsValue())) {
//
//                                }
//
////                                V1Service v1Service = k8sService.readNameSpacedResource(deployYaml.getYamlName(),
////                                                moduleEnv, deployYaml.getYamlNamespace(),
////                                                K8sKindTypeEnum.SERVICE.getKindType(),
////                                                V1Service.class);
//                                success = true;
//                            } else {
//                                throw new K8SDeployException("labels的name不能为空");
//                            }
              break;
            default:
              break;
          }
          if (oldEnvList.contains(envId)) {
            url.append(EXTENSION_V1BETA1_PREFIX);
          } else {
            url.append(APPS_V1);
          }
          url.append(deployYaml.getYamlNamespace())
              .append(EXTENSION_DEPLOYMENTS).append(deployYaml.getYamlName());
          K8sUtils.okhttpPatchBack(moduleEnv.getK8sConfig(), url.toString(), pathList);
          success = true;
        } catch (ApiException e) {
          syncService.saveModuleDeployLog(moduleDeployLogService, moduleId, envId,
              deployYaml.getDeployId(), OP_HOT_UPDATE_DEPLOYMENT,
              argsMap, false, moduleUser.getId(), moduleUser.getUsername());
          log.error("热配置修改出现错误 msg:{}", e.getMessage());
          throw new K8SDeployException(e.getMessage());
        }
        syncService.saveModuleDeployLog(moduleDeployLogService, moduleId, envId,
            deployYaml.getDeployId(), OP_HOT_UPDATE_DEPLOYMENT,
            argsMap, true, moduleUser.getId(), moduleUser.getUsername());
      }
    }
    return success;
  }
  
  @Override
  public List<ModuleEnvCenterManageVo> selectByDeployVO(ModuleDeployVo deployVo) {
    List<ModuleEnvCenterManageVo> manageVoList = deployMapper.selectModuleEnvCenterAll(deployVo);
    final String keyName = deployVo.getKeyName();
    if (manageVoList != null && manageVoList.size() > 0) {
      manageVoList.sort((s1, s2) -> {
        if (StringUtils.isNotBlank(keyName) &&
            s1.getModuleName().equals(keyName)) {
          return -1;
        } else if (StringUtils.isNotBlank(keyName) &&
            s1.getModuleContentName().equals(keyName)) {
          return 0;
        } else {
          return 1;
        }
      });
    }
    return manageVoList;
  }
  
  /**
   * 从pod内copy java dump日志文件到本地
   *
   * @param podName
   * @param envId
   * @param moduleId
   * @return
   */
  @Override
  public File getThreadDumpFileFromPod(String podName, Integer envId, String moduleId) {
    if (StringUtils.isNotBlank(podName) && envId != null && moduleId != null) {
      ModuleEnv moduleEnv = envMapper.selectOne(envId);
      List<ModuleDeployYaml> deployYamls =
          getModuleDeployByModuleAndEnvId(Integer.valueOf(moduleId), envId, true);
      if (moduleEnv != null && deployYamls != null && deployYamls.size() > 0) {
        V1Pod v1Pod = k8sService.readNameSpacedResource(podName, moduleEnv,
            deployYamls.get(0).getYamlNamespace(), K8sKindTypeEnum.POD.getKindType(), V1Pod.class);
        if (v1Pod != null && v1Pod.getStatus() != null) {
          String mountLogPathFromPod = K8sUtils.getMountLogPathFromPod(v1Pod);
          if (StringUtils.isNotBlank(mountLogPathFromPod)) {
            try {
              StringBuilder sb = new StringBuilder();
              sb.append(mountLogPathFromPod)
                  .append(CONTACT)
                  .append(K8sUtils.THREAD_DUMP_JAVAPATH)
                  .append(v1Pod.getMetadata().getName())
                  .append(CONTACT);
              
              StringBuilder sourceSb = new StringBuilder();
              sourceSb.append(K8sUtils.THREAD_DUMP_JAVAPATH)
                  .append(DateUtils.generateDateOnlyString());
              Path path = Paths.get(moduleExportPath, sourceSb.toString());
              Copy copy = new Copy();
              copy.copyDirectoryFromPod(v1Pod,
                  v1Pod.getSpec().getContainers().get(0).getName(),
                  sb.toString(), path);
              sourceSb.append(".zip");
              File file = new File(moduleExportPath + CONTACT + sourceSb.toString());
              MyZipUtils.zipAllFile(path.toFile(), file);
              return file;
            } catch (ApiException | CopyNotSupportedException | IOException e) {
              e.printStackTrace();
              throw new K8SDeployException("copy文件出现错误");
            }
          }
        }
      }
    }
    return null;
  }
  
  /**
   * 获取发布概况的资源总体情况
   *
   * @param envId
   * @return
   */
//    @Override
//    public ModuleDeployHomeDTO getHomeDeploySourceData(Integer envId) {
//        ModuleDeployHomeDTO moduleDeployHomeDTO = new ModuleDeployHomeDTO();
//        ModuleEnv moduleEnv = envMapper.selectOne(envId);
//        if (moduleEnv != null) {
//            List<Object> objectList = k8sService.listAllNameSpacedResource(moduleEnv,
//                    K8sNameSpace.DEFAULT, K8sKindTypeEnum.NODE.getKindType());
//            if (objectList != null && objectList.size() > 0) {
//                moduleDeployHomeDTO.setNodeCount(objectList.size());
//                moduleDeployHomeDTO.setRunNodeCount(objectList.size());
//            }
//            List<Object> podList = k8sService.listAllNameSpacedResource(moduleEnv,
//                    K8sNameSpace.DEFAULT, K8sKindTypeEnum.POD.getKindType());
//            if (podList != null && podList.size() > 0) {
//                moduleDeployHomeDTO.setPodCount(podList.size());
//            }
//            moduleDeployHomeDTO.setModuleCount(100);
//        }
//
//        return moduleDeployHomeDTO;
//    }
  
  /**
   * 替换yaml文件
   *
   * @param moduleId
   * @param envId
   * @return
   */
  @Override
  @Transactional(rollbackFor = Exception.class)
  public boolean replaceModuleYaml(Integer moduleId, Integer envId, ModuleUser moduleUser, Integer argsType) {
    boolean success = false;
    Map<String, Object> argsMap = new HashMap<>();
    K8sKindTypeEnum typeEnum = K8sKindTypeEnum.getEnumByCode(argsType);
    ModuleDeployExample deployExample = new ModuleDeployExample();
    ModuleDeployExample.Criteria criteria = deployExample.createCriteria();
    criteria.andModuleIdEqualTo(moduleId).
        andEnvIdEqualTo(envId).andIsDeleteEqualTo(0).andIsDeployedEqualTo(1);
    List<ModuleDeployYaml> moduleDeployYamls =
        deployYamlMapper.selectYamlJsonByModuleId(moduleId);
    ModuleEnv moduleEnv = envMapper.selectOne(envId);
    AppsV1Api v1beta1Api =
        k8sService.getExtensionsV1beta1ApiByConfig(moduleEnv);
    CoreV1Api coreV1Api = k8sService.getCoreV1ApiByConfig(moduleEnv);
    if (typeEnum != null && deployMapper.countByExample(deployExample) > 0
        && moduleDeployYamls != null && moduleDeployYamls.size() > 0) {
      for (ModuleDeployYaml moduleDeployYaml : moduleDeployYamls) {
        if (typeEnum.getKindType().equals(moduleDeployYaml.getYamlType())) {
          if (StringUtils.isNotBlank(moduleDeployYaml.getYamlJson())) {
            K8sYamlVo k8sYamlVo = null;
            try {
              k8sYamlVo = K8sUtils.transObject2Vo(Yaml.load(moduleDeployYaml.getYamlJson()));
            } catch (IOException e) {
              argsMap.put("error", "yaml文件转换为k8svo 出现错误: " + e.getMessage());
              syncService.saveModuleDeployLog(moduleDeployLogService,
                  moduleId, envId, moduleDeployYaml.getDeployId(), OP_REPLACE_YAML,
                  argsMap, false, moduleUser.getId(), moduleUser.getUsername());
              e.printStackTrace();
            }
            try {
              switch (typeEnum) {
                case DEPLOYMENT:
                  V1Deployment newDeployment =
                      K8sUtils.getObject(k8sYamlVo.getO(), V1Deployment.class);
  
                  if (newDeployment != null) {
                    V1Deployment deployment =
                        k8sService.readNameSpacedResource(moduleDeployYaml.getYamlName(),
                            moduleEnv, moduleDeployYaml.getYamlNamespace(),
                            K8sKindTypeEnum.DEPLOYMENT.getKindType(),
                            V1Deployment.class);
                    newDeployment.getSpec().setReplicas(deployment.getSpec().getReplicas());
                    newDeployment.getSpec().getTemplate().getSpec().getContainers().get(0)
                        .setImage(deployment.getSpec().getTemplate().getSpec().getContainers().get(0).getImage());
                    v1beta1Api.replaceNamespacedDeployment(moduleDeployYaml.getYamlName(),
                        moduleDeployYaml.getYamlNamespace(), newDeployment, null, null, null, null);
                    success = true;
                  }
                  break;
                case SERVICE:
                  V1Service newV1Service = K8sUtils.getObject(k8sYamlVo.getO(), V1Service.class);
                  if (newV1Service != null) {
                    V1Service v1Service = k8sService.readNameSpacedResource(moduleDeployYaml.getYamlName(),
                        moduleEnv, moduleDeployYaml.getYamlNamespace(),
                        K8sKindTypeEnum.SERVICE.getKindType(),
                        V1Service.class);
                    log.info(v1Service.toString());
                    newV1Service.setSpec(v1Service.getSpec());
                    log.info(newV1Service.toString());
                    coreV1Api.replaceNamespacedService(moduleDeployYaml.getYamlName(),
                        moduleDeployYaml.getYamlNamespace(), newV1Service, null, null, null, null);
                    success = true;
                  }
                  break;
              }
            } catch (ApiException e) {
              log.error("k8s api 操作异常");
              e.printStackTrace();
            }
          } else {
            throw new K8SDeployException("yaml_json为空,请先编辑yaml");
          }
        }
      }
    }
    return success;
  }
  
  /**
   * 在某一个环境中发布某一个模块 替换镜像为此模块在该镜像仓库中最新的镜像
   *
   * @param moduleId
   * @param envId
   * @return
   */
  @Override
  @Transactional(rollbackFor = Exception.class)
  public boolean deployModuleInEnv(Integer moduleId, Integer envId, Integer mirrorId, ModuleUser moduleUser) {
    //验证该模块是否在此环境中发布过
//        if (judgeModuleIsOnlie(moduleId, envId)) {
//            throw new DeployIsOnlineExcetion("当前模块在此环境中已做发布");
//        }
    boolean success = false;
    //查询发布的yaml文件对应的信息,查出发布文件的信息
    Map<String, Object> argsMap = new HashMap<>();
    // 获取该模块对应的yaml文件信息
    List<ModuleDeployYaml> deployYamls =
        getModuleDeployByModuleAndEnvId(moduleId, envId, true);
    if (deployYamls != null && deployYamls.size() > 0) {
      ModuleEnv moduleEnv = envMapper.selectOne(envId);
      if (moduleEnv != null) {
        ModuleDeployYaml deployYaml = deployYamls.get(0);
        K8sYamlVo k8sYamlVo = null;
        String yamlPath = storgePrefix + deployYaml.getYamlPath();
        Integer deployId = deployYaml.getDeployId();
        
        if (StringUtils.isNotBlank(deployYaml.getYamlJson())) {
          try {
            k8sYamlVo = K8sUtils.transObject2Vo(Yaml.load(deployYaml.getYamlJson()));
          } catch (IOException e) {
            argsMap.put("error", "yaml文件转换为k8svo 出现错误: " + e.getMessage());
            syncService.saveModuleDeployLog(moduleDeployLogService,
                moduleId, envId, deployId, OP_ONLINE_MODULE,
                argsMap, false, moduleUser.getId(), moduleUser.getUsername());
            e.printStackTrace();
          }
        } else {
          try {
            k8sYamlVo = K8sUtils.transYaml2Vo(new File(yamlPath));
            //argsMap.put("yamlPath", yamlPath);
            if (k8sYamlVo == null) {
              throw new TransYaml2K8sVoException("请检查该yamlPath下未找到对应的yaml文件: "
                  + yamlPath);
            }
          } catch (IOException | ReaderException | ScannerException e) {
            argsMap.put("error", "yaml文件转换为k8svo 出现错误: " + e.getMessage());
            syncService.saveModuleDeployLog(moduleDeployLogService,
                moduleId, envId, deployId, OP_ONLINE_MODULE,
                argsMap, false, moduleUser.getId(), moduleUser.getUsername());
            throw new TransYaml2K8sVoException("yaml文件转换为k8svo 出现错误: " + e.getMessage());
          }
        }
        
        ModuleDeploy moduleDeploy = deployMapper.selectByPrimaryKey(deployId);
        
        String mirrorName = null;
        ModuleMirror moduleMirror = mirrorMapper.selectByPrimaryKey(mirrorId);
        if (moduleMirror != null && envId.equals(moduleMirror.getModuleEnvId())
            && moduleId.equals(moduleMirror.getModuleId())
            && moduleMirror.getIsAvailable() == 1) {
          mirrorName = moduleMirror.getMirrorName();
        } else {
          throw new K8SDeployException("镜像不存在,请重新选择镜像");
        }
        
        if (k8sYamlVo != null && moduleDeploy != null
            && k8sService.deploy(k8sYamlVo, moduleEnv, mirrorName)) {
          if ("offline deploy".equals(moduleDeploy.getDeployStatus())) {
            try {
              deploySvc(moduleId, envId, moduleUser);
            } catch (DeployIsOnlineExcetion | TransYaml2K8sVoException e) {
              argsMap.put("error", "发布svc失败 " + e.getMessage());
            }
          }
          if (K8sKindTypeEnum.DEPLOYMENT.getKindType().equals(k8sYamlVo.getKind())) {
            V1Deployment deployment;
            if (K8sApiversionTypeEnum.EXTENSIONAPI.getApiVersionType().equals(k8sYamlVo.getApiVersion())) {
              V1Deployment beta1Deployment =
                  K8sUtils.getObject(k8sYamlVo.getO(), V1Deployment.class);
              deployment = K8sUtils.toV1Deploy(beta1Deployment);
            } else {
              deployment = K8sUtils.getObject(k8sYamlVo.getO(), V1Deployment.class);
            }
            Map<String, String> limit = k8sService.getContainerLimit(deployment);
            argsMap.put("new", limit);
          }
          moduleDeploy.setDeployStatus("success");
          moduleDeploy.setIsDeployed(1);
          moduleDeploy.setLastDeployTime(new Date());
          deployYaml.setIsDeployed(1);
          deployYaml.setMirrorName(mirrorName);
          deployMapper.updateByPrimaryKeySelective(moduleDeploy);
          deployYamlMapper.updateByPrimaryKeySelective(deployYaml);
          //先将该模块下其他的镜像标记为未使用
          mirrorMapper.updateMirrorIsUsedByModuleId(moduleMirror.getModuleId());
          moduleMirror.setIsUsed(ModuleMirrorUsedEnum.ISUSED.getCode());
          mirrorMapper.updateByPrimaryKeySelective(moduleMirror);
          success = true;
          //argsMap.put("mirrorName", mirrorName);
        }
        syncService.saveModuleDeployLog(moduleDeployLogService,
            moduleId, envId, deployId, OP_ONLINE_MODULE,
            argsMap, success, moduleUser.getId(), moduleUser.getUsername());
      }
    }
    return success;
  }
  
  /**
   * 扩缩容某个模块
   *
   * @param moduleId
   * @param moduleSize
   * @param envId
   * @return
   */
  @Override
  public boolean scaleModuleSize(Integer moduleId, Integer moduleSize, Integer envId, ModuleUser moduleUser) {
    boolean success = false;
//        if (!judgeModuleIsOnlie(moduleId, envId)) {
//            throw new DeployIsOnlineExcetion("当前模块未上线");
//        }
    //扩缩容大小限制了
    if (moduleSize > 0 && moduleSize <= 50) {
      List<ModuleDeployYaml> deployYamls = getModuleDeployByModuleAndEnvId(moduleId, envId, true);
      if (deployYamls != null && deployYamls.size() > 0) {
        ModuleEnv moduleEnv = envMapper.selectOne(envId);
        if (moduleEnv != null) {
          ModuleDeployYaml deployYaml = deployYamls.get(0);
          String yamlType = deployYaml.getYamlType();
          String yamlName = deployYaml.getYamlName();
          Integer deployId = deployYaml.getDeployId();
          String yamlNamespace = deployYaml.getYamlNamespace();
          
          log.info("yamlType: {}", yamlType);
          if (K8sKindTypeEnum.DEPLOYMENT.getKindType().equals(yamlType)) {
            
            Integer replicas = k8sService.scaleModuleSize(yamlName, yamlNamespace,
                deployId, moduleEnv, moduleSize);
            if (replicas != null && replicas != 0) {
              success = true;
            }
            Map<String, Object> argsMap = new HashMap<>();
            argsMap.put("old", replicas);
            argsMap.put("new", moduleSize);
            syncService.saveModuleDeployLog(moduleDeployLogService, moduleId, envId, deployId,
                OP_SCALE_SIZE, argsMap, success, moduleUser.getId(), moduleUser.getUsername());
          }
        }
      }
    }
    return success;
  }
  
  /**
   * 下线某个模块发布 同时下线svc
   *
   * @param moduleId
   * @param envId
   * @return
   */
  @Override
  @Transactional(rollbackFor = Exception.class)
  public boolean offline(Integer moduleId, Integer envId, ModuleUser moduleUser) {
    boolean success = false;
//        if (!judgeModuleIsOnlie(moduleId, envId)) {
//            throw new DeployIsOnlineExcetion("当前模块未上线");
//        }
    Integer deployId = null;
    Map<String, Object> argsMap = new HashMap<>();
    List<ModuleDeployYaml> deployYamls = getModuleDeployByModuleAndEnvId(moduleId, envId, false);
    if (deployYamls != null && deployYamls.size() > 0) {
      ModuleEnv moduleEnv = envMapper.selectOne(envId);
      if (moduleEnv != null) {
        for (ModuleDeployYaml deployYaml : deployYamls) {
          //查看yaml是否已经发布
          deployId = deployYaml.getDeployId();
          try {
            if (K8sKindTypeEnum.DEPLOYMENT.getKindType().equals(deployYaml.getYamlType())) {
              V1Deployment deployment =
                  k8sService.readNameSpacedResource(deployYaml.getYamlName(),
                      moduleEnv, deployYaml.getYamlNamespace(),
                      K8sKindTypeEnum.DEPLOYMENT.getKindType(),
                      V1Deployment.class);
              String image = deployment.getSpec().getTemplate().getSpec().getContainers()
                  .get(0).getImage();
              Map<String, String> limit = k8sService.getContainerLimit(deployment);
              argsMap.put("limit", limit);
              argsMap.put("replicas", deployment.getSpec().getReplicas());
              //截取镜像名中的tag
              String[] mirrorNames = image.split(":");
              argsMap.put("image", mirrorNames[mirrorNames.length - 1]);
            }
            k8sService.deleteNamespacedSource(deployYaml.getYamlName(), deployYaml.getYamlType(),
                deployYaml.getYamlNamespace(), moduleEnv);
          } catch (K8SDeployException e) {
            log.info(deployYaml.getYamlName() + ":" + deployYaml.getYamlType() + ":" + "下线失败");
          } finally {
            //module deploy 状态的更改只限一次
            if (!success) {
              ModuleDeploy moduleDeploy = new ModuleDeploy();
              moduleDeploy.setId(deployId);
              moduleDeploy.setIsDeployed(0);
              moduleDeploy.setDeployStatus("offline deploy");
              deployMapper.updateByPrimaryKeySelective(moduleDeploy);
            }
            deployYaml.setIsDeployed(0);
            deployYamlMapper.updateByPrimaryKeySelective(deployYaml);
            success = true;
            //argsMap.put("name", deployYaml.getYamlName());
            continue;
          }
        }
      }
    }
    syncService.saveModuleDeployLog(moduleDeployLogService, moduleId,
        envId, deployId, OP_OFFLINE_MODULE, argsMap,
        success, moduleUser.getId(), moduleUser.getUsername());
    return success;
  }
  
  /**
   * 替换镜像上线
   *
   * @param moduleId
   * @param envId
   * @param mirrorId
   * @param moduleUser
   * @return
   */
  @Override
  public ResponseDTO changeMirror(Integer moduleId, Integer envId, Integer mirrorId, ModuleUser moduleUser) {
    //首先查询是否已经该模块在该环境中是否已经上线
//        if (!judgeModuleIsOnlie(moduleId, envId)) {
//            throw new DeployIsOnlineExcetion("当前模块未上线");
//        }
    boolean success = false;
    ResponseDTO responseDTO = new ResponseDTO();
    responseDTO.fail("发布失败");
    //升级开关开启才能升级
    Set<Integer> envIdSet = new HashSet<>();
    envIdSet.add(envId);
    List<BillingOpOnOff> OpOnOffs = onOffService.selectAll(envIdSet);
    if (!OpOnOffs.isEmpty() && OpOnOffs.get(0).getIsAbleNext() == 0) {
      return responseDTO.fail("请在规定的时间发布");
    }
    //开发角色在准生产环境没权限升级
    //moduleUser里的id属性此时存的是user_id，在controller层通过getModuleUserInfo方法设置
    //如果user为空，则说明为自动发布，不校验权限
    if (moduleUser != null) {
      Integer id = userMapper.selectByUserId(moduleUser.getId()).getId();
      List<ModuleUserEnvRoleVo> userEnvRoleVos = userMapper.selectRoleBindUser(id,
          UserRoleTypeEnum.APPROVER_ROLE.getCode(), envId);
      if ((envId == 30 || envId == 35) && !userEnvRoleVos.isEmpty()) {
        return responseDTO.fail("没有权限");
      }
    }
    
    List<ModuleDeployYaml> deployYamls =
        getModuleDeployByModuleAndEnvId(moduleId, envId, true);
    Map<String, Object> argsMap = new HashMap<>();
    Integer deployId = null;
    if (deployYamls != null && deployYamls.size() > 0) {
      ModuleMirror mirror = new ModuleMirror();
      mirror.setId(mirrorId);
      mirror.setModuleEnvId(envId);
      mirror.setModuleId(moduleId);
      ModuleDeployYaml deployYaml = deployYamls.get(0);
      deployId = deployYaml.getDeployId();
      List<ModuleMirror> mirrorList = mirrorMapper.selectAvailableMirrorById(mirror);
      if (mirrorList != null && mirrorList.size() > 0) {
        ModuleEnv moduleEnv = envMapper.selectOne(envId);
        success = k8sService.replaceNamespacedSourceMirror(deployYaml,
            deployYaml.getYamlNamespace(), moduleEnv, mirrorList.get(0).getMirrorName());
        if (success) {
          //先将该模块下其他的镜像标记为未使用
          mirrorMapper.updateMirrorIsUsedByModuleId(mirror.getModuleId());
          mirror.setIsUsed(ModuleMirrorUsedEnum.ISUSED.getCode());
          mirrorMapper.updateByPrimaryKeySelective(mirror);
          responseDTO.success("发布成功");
        }
        argsMap.put("mirrorName", mirrorList.get(0).getMirrorName());
      }
    }
    syncService.saveModuleDeployLog(moduleDeployLogService, moduleId, envId, deployId,
        OP_CHANGE_MIRROR, argsMap, success, moduleUser.getId(), moduleUser.getUsername());
    return responseDTO;
  }
  
  /**
   * 发布svc的操作
   * 这里需要检查svc的yaml文件要和 配置的deployment的yaml有对应上的label匹配
   *
   * @param moduleId
   * @param envId
   * @return
   */
  @Override
  @Transactional(rollbackFor = Exception.class)
  public boolean deploySvc(Integer moduleId, Integer envId, ModuleUser moduleUser) {
//        if (!judgeModuleIsOnlie(moduleId, envId)) {
//            throw new DeployIsOnlineExcetion("当前模块未上线");
//        }
    boolean success = false;
    //找到非isOnlineYaml 中的svc 然后去做发布
    Map<String, Object> argsMap = new HashMap<>();
    List<ModuleDeployYaml> deployYamls = getModuleDeployByModuleAndEnvId(moduleId, envId, false);
    if (deployYamls != null && deployYamls.size() > 0) {
      for (ModuleDeployYaml deployYaml : deployYamls) {
        if (K8sKindTypeEnum.SERVICE.getKindType().equals(deployYaml.getYamlType())) {
          String yamlPath = deployYaml.getYamlPath();
          deployYaml.setYamlPath(yamlPath);
          ModuleEnv moduleEnv = envMapper.selectOne(envId);
          //获取可发布源数据 并进行vo的解析
          K8sYamlVo k8sYamlVo = getK8sYamlVoFromDeployYaml(deployYaml, argsMap);
          if (k8sYamlVo != null) {
//                        Map<String, String> labelMap = k8sYamlVo.getLabelMap();
            success = k8sService.deploy(k8sYamlVo, moduleEnv, null);
            argsMap.put("yamlName", k8sYamlVo.getMetadataName());
            if (success) {
              deployYaml.setIsDeployed(1);
              deployYaml.setYamlPath(null);
              deployYamlMapper.updateByPrimaryKeySelective(deployYaml);
            }
          }
          syncService.saveModuleDeployLog(moduleDeployLogService, moduleId,
              envId, deployYaml.getDeployId(),
              OP_ONLINE_SVC, argsMap, success, moduleUser.getId(),
              moduleUser.getUsername());
          break;
        }
        
      }
    }
    return success;
  }
  
  private K8sYamlVo getK8sYamlVoFromDeployYaml(ModuleDeployYaml deployYaml, Map<String, Object> argsMap) {
    K8sYamlVo k8sYamlVo = null;
    if (StringUtils.isNotBlank(deployYaml.getYamlJson())) {
      try {
        k8sYamlVo = K8sUtils.transObject2Vo(Yaml.load(deployYaml.getYamlJson()));
      } catch (IOException e) {
        e.printStackTrace();
        throw new TransYaml2K8sVoException("yaml文件转换为k8svo 出现错误: " + e.getMessage());
      }
    } else {
      String yamlPath = storgePrefix + deployYaml.getYamlPath();
      File file = new File(yamlPath);
      if (file.exists()) {
        try {
          k8sYamlVo = K8sUtils.transYaml2Vo(file);
        } catch (IOException | ReaderException | ScannerException e) {
          throw new TransYaml2K8sVoException("yaml文件转换为k8svo 出现错误: " + e.getMessage());
        }
      }
      if (argsMap != null) {
        argsMap.put("yamlPath", yamlPath);
      }
    }
    return k8sYamlVo;
  }
  
  private boolean judgeModuleIsOnlie(Integer moduleId, Integer envId) {
    boolean isOnline = false;
    if (moduleId != null && envId != null) {
      ModuleDeployExample deployExample = new ModuleDeployExample();
      ModuleDeployExample.Criteria criteria = deployExample.createCriteria();
      criteria.andModuleIdEqualTo(moduleId)
          .andEnvIdEqualTo(envId)
          .andIsDeleteEqualTo(0)
          .andIsDeployedEqualTo(1);
      List<ModuleDeploy> moduleDeploys = deployMapper.selectByExample(deployExample);
      if (moduleDeploys != null && moduleDeploys.size() == 1) {
        isOnline = true;
      }
    }
    return isOnline;
  }
  
  @Override
  public K8sServiceDTO getSvcInfo(Integer moduleId, Integer envId) {
    K8sServiceDTO serviceDTO = new K8sServiceDTO();
    List<ModuleDeployYaml> deployYamls = getModuleDeployByModuleAndEnvId(moduleId, envId, false);
    if (deployYamls != null && deployYamls.size() > 0) {
      for (ModuleDeployYaml deployYaml : deployYamls) {
        if (K8sKindTypeEnum.SERVICE.getKindType().equals(deployYaml.getYamlType())) {
          String yamlName = deployYaml.getYamlName();
          ModuleEnv moduleEnv = envMapper.selectOne(envId);
          V1Service v1Service =
              k8sService.getSvcInfoByName(moduleEnv, yamlName, deployYaml.getYamlNamespace());
          if (v1Service != null) {
            serviceDTO.setName(v1Service.getMetadata().getName());
            serviceDTO.setClusterIP(v1Service.getSpec().getClusterIP());
            serviceDTO.setExternalIPs(v1Service.getSpec().getExternalIPs());
            serviceDTO.setSelector(v1Service.getSpec().getSelector());
            serviceDTO.setType(v1Service.getSpec().getType());
            
            ArrayList<K8sServicePortDTO> servicePortDTOS = new ArrayList<>();
            List<V1ServicePort> ports = v1Service.getSpec().getPorts();
            if (ports != null && ports.size() > 0) {
              for (V1ServicePort servicePort : ports) {
                K8sServicePortDTO servicePortDTO = new K8sServicePortDTO();
                servicePortDTO.setName(servicePort.getName());
                servicePortDTO.setNodePort(servicePort.getNodePort());
                servicePortDTO.setProtocol(servicePort.getProtocol());
                servicePortDTO.setTargetPort(servicePort.getTargetPort()
                    == null ? null : servicePort.getTargetPort().toString());
                servicePortDTO.setPort(servicePort.getPort());
                servicePortDTOS.add(servicePortDTO);
              }
            }
            serviceDTO.setServicePortDTOList(servicePortDTOS);
          }
        }
      }
    }
    return serviceDTO;
  }
  
  /**
   * 删除已经上线的pods的操作
   *
   * @param envId
   * @param moduleId
   * @param podNames
   * @return
   */
  @Override
  public boolean delPods(Integer envId, Integer moduleId, String[] podNames, ModuleUser moduleUser) {
    boolean success = false;
    if (podNames != null && podNames.length > 0) {
      List<ModuleDeployYaml> deployYamls =
          getModuleDeployByModuleAndEnvId(moduleId, envId, true);
      if (deployYamls != null && deployYamls.size() > 0) {
        ModuleDeployYaml deployYaml = deployYamls.get(0);
        Integer deployId = deployYaml.getDeployId();
        deployYamls.clear();
        ModuleEnv moduleEnv = envMapper.selectOne(envId);
        success = true;
        for (String podName : podNames) {
          if (!k8sPodService.deletePod(podName, moduleEnv, deployYaml.getYamlNamespace())) {
            success = false;
            break;
          }
        }
        Map<String, Object> argsMap = new HashMap<>();
        argsMap.put("podNames", Arrays.toString(podNames));
        syncService.saveModuleDeployLog(moduleDeployLogService, moduleId, envId, deployId,
            OP_DELETE_PODS, argsMap, success, moduleUser.getId(), moduleUser.getUsername());
      }
    }
    return success;
  }
  
  /**
   * 下线svc的操作
   *
   * @param moduleId
   * @param envId
   * @return
   */
  @Override
  @Transactional(rollbackFor = Exception.class)
  public boolean delSvc(Integer moduleId, Integer envId, ModuleUser moduleUser) {
    boolean success = false;
    List<ModuleDeployYaml> deployYamls = getModuleDeployByModuleAndEnvId(moduleId, envId, false);
    if (deployYamls != null && deployYamls.size() > 0) {
      for (ModuleDeployYaml deployYaml : deployYamls) {
        if (K8sKindTypeEnum.SERVICE.getKindType().equals(deployYaml.getYamlType())) {
          String yamlName = deployYaml.getYamlName();
          ModuleEnv moduleEnv = envMapper.selectOne(envId);
          success = k8sService.deleteNamespacedSource(yamlName, deployYaml.getYamlType(),
              deployYaml.getYamlNamespace(), moduleEnv);
          if (success) {
            deployYaml.setIsDeployed(0);
            deployYamlMapper.updateByPrimaryKeySelective(deployYaml);
          }
          Map<String, Object> argsMap = new HashMap<>();
          argsMap.put("yamlName", yamlName);
          argsMap.put("yamlPath", storgePrefix + deployYaml.getYamlPath());
          syncService.saveModuleDeployLog(moduleDeployLogService,
              moduleId, envId, deployYaml.getDeployId(),
              OP_DELETE_SVC, argsMap, success, moduleUser.getId(), moduleUser.getUsername());
        }
      }
    }
    return success;
  }
  
  /**
   * 执行请缓存的shell命令 然后回传执行结果
   *
   * @param envId
   * @param moduleId
   * @param podNames
   * @param moduleUser
   * @return
   */
  @Override
  public Map<String, String> clearMemoryPods(Integer envId, Integer moduleId,
                                             String[] podNames, ModuleUser moduleUser) {
    Map<String, String> resultMap = new HashMap<>();
    if (!judgeModuleIsOnlie(moduleId, envId)) {
      throw new DeployIsOnlineExcetion("当前模块未上线");
    }
    Map<String, Object> argsMap = new HashMap<>();
    List<ModuleDeployYaml> deployYamls = getModuleDeployByModuleAndEnvId(moduleId, envId, false);
    ModuleEnv env = envMapper.selectOne(envId);
    boolean flag = false;
    argsMap.put("podNames", JSONObject.toJSONString(podNames));
    if (env != null && deployYamls != null && deployYamls.size() > 0) {
      if (podNames != null && podNames.length > 0) {
        for (String podName : podNames) {
          V1Pod pod = k8sPodService.getPodByPodName(env, podName, deployYamls.get(0).getYamlNamespace());
          if (pod != null) {
            StringBuffer sb = new StringBuffer();
            ThreadPoolExecutor poolExecutor =
                new ThreadPoolExecutor(podNames.length, podNames.length, 0L,
                    TimeUnit.SECONDS, new ArrayBlockingQueue<>(10));
            Exec exec = new Exec();
            List<V1Container> containers = pod.getSpec().getContainers();
            try {
              if (containers.size() > 0) {
                Process process = exec.exec(
                    pod,
//                                        new String[]{"env"},
                    new String[]{"sh", "/app/jmx-tool/jmx-monitor-12345.sh"},
                    containers.get(0).getName(),
                    false,
                    false);
                poolExecutor.submit(new Thread(() -> {
                  byte[] bytes = new byte[2048];
                  InputStream inputStream = process.getInputStream();
                  try {
                    int len;
                    while ((len = inputStream.read(bytes)) != -1) {
                      sb.append(new String(bytes, 0, len));
                    }
                  } catch (IOException e) {
                    log.info("出现相关错误:{}", e.getMessage());
                  } finally {
                    if (inputStream != null) {
                      try {
                        inputStream.close();
                      } catch (IOException e) {
                        e.printStackTrace();
                      }
                    }
                  }
                }));
                process.waitFor(2, TimeUnit.SECONDS);
                Thread.sleep(200);
                flag = true;
//                                PoolExcutorUtils.shutDownExcutor(poolExecutor, 1);
                process.destroy();
              }
            } catch (ApiException | IOException | InterruptedException e) {
              log.error("清除pod内存出现错误: {}", e.getMessage());
            }
            resultMap.put(podName, sb.toString());
            syncService.saveModuleDeployLog(moduleDeployLogService,
                moduleId, envId, deployYamls.get(0).getDeployId(),
                OP_CLEAR_POD_MEMORY, argsMap, flag, moduleUser.getId(),
                moduleUser.getUsername());
          }
        }
      }
    }
    return resultMap;
  }
  
  /**
   * 比对最近下线时的参数
   *
   * @param moduleId
   * @param envId
   * @return
   */
  @Override
  public ResponseDTO compareYamlConfig(Integer moduleId, Integer envId) {
    ResponseDTO responseDTO = new ResponseDTO();
    responseDTO.success("校验成功");
    Map result = new HashMap<>();
    List<ModuleDeployYaml> moduleDeployYamls =
        getModuleDeployByModuleAndEnvId(moduleId, envId, true);
    if (moduleDeployYamls != null) {
      List<Integer> oldEnvList = Arrays.asList(oldEnvId);
      V1Deployment deployment;
      K8sYamlVo k8sYamlVo = getK8sYamlVoFromDeployYaml(moduleDeployYamls.get(0), null);
      if (oldEnvList.contains(envId)) {
        V1Deployment object =
            K8sUtils.getObject(k8sYamlVo.getO(), V1Deployment.class);
        deployment = K8sUtils.toV1Deploy(object);
      } else {
        deployment =
            K8sUtils.getObject(k8sYamlVo.getO(), V1Deployment.class);
      }
      Integer replicas = deployment.getSpec().getReplicas();
      Map<String, String> limitMap = k8sService.getContainerLimit(deployment);
      ModuleDeployLog moduleDeployLog =
          moduleDeployLogService.getLastOffline(moduleId, envId);
      if (moduleDeployLog != null && StringUtils.isNotBlank(moduleDeployLog.getArgs())) {
        Map map = JSON.parseObject(moduleDeployLog.getArgs());
        for (Object obj : map.keySet()) {
          if ("replicas".equals(obj) && !replicas.equals(map.get(obj))) {
            result.put(obj, map.get(obj));
          }
          if ("limit".equals(obj) && !limitMap.equals(map.get(obj))) {
            result.put(obj, map.get(obj));
          }
          ;
        }
      }
    }
    if (result != null && result.size() > 0) responseDTO.fail(result);
    return responseDTO;
  }
  
  /**
   * 获取定时比对yaml任务的结果
   *
   * @return
   */
  @Override
  public List<ModuleYamlDiff> getYamlCompareResult() {
    return deployYamlMapper.getYamlDiff();
  }
  
  /**
   * 设置滚动升级参数
   *
   * @param moduleId
   * @param envId
   * @param strategyParamVO
   * @return
   */
  @Override
  public ResponseDTO changeStrategyArgs(Integer moduleId, Integer envId,
                                        K8sStrategyParamVO strategyParamVO) {
    ResponseDTO responseDTO = new ResponseDTO();
    responseDTO.success("设置成功");
    ModuleEnv moduleEnv = envMapper.selectOne(envId);
    List<ModuleDeployYaml> yamlList =
        getModuleDeployByModuleAndEnvId(moduleId, envId, true);
    if (yamlList != null && yamlList.size() > 0) {
      V1Deployment deployment =
          k8sService.readNameSpacedResource(yamlList.get(0).getYamlName(),
              moduleEnv, yamlList.get(0).getYamlNamespace(),
              K8sKindTypeEnum.DEPLOYMENT.getKindType(), V1Deployment.class);
      Integer replicas = deployment.getSpec().getReplicas();
      if (strategyParamVO.getMaxSurge() + strategyParamVO.getMaxUnavailable() >= replicas) {
        return responseDTO.fail("取值超过副本数,设置失败");
      }
      if (replicas > 3 && strategyParamVO.getMaxSurge() + strategyParamVO.getMaxUnavailable() >= replicas / 2) {
        responseDTO.success("取值已达到副本数的一半");
      }
      Map<String, String> nodeSelector = deployment.getSpec().getTemplate().getSpec().getNodeSelector();
      if (nodeSelector != null && nodeSelector.containsKey("center")) {
        StringBuilder sb = new StringBuilder();
        String url = sb.append(getNodeResources).append("search=")
            .append("center").append(":").append(nodeSelector.get("center")).append("&")
            .append("paasid=").append(moduleEnv.getPaasId()).toString();
        String result = HttpUtils.httpGetUrl(url, null, null);
        Map map = JSON.parseObject(result);
        Integer total = (Integer) map.get("count");
        if (total > 100) {
          url = url.replace("page_size=100", "page_size=" + total);
          result = HttpUtils.httpGetUrl(url, null, null);
          map = JSON.parseObject(result);
        }
        Object results = map.get("results");
        float cpu_used_total = 0;
        float mem_used_total = 0;
        List<K8sNodeResourcesVO> list = JSONObject.parseArray(results.toString(), K8sNodeResourcesVO.class);
        if (list != null && list.size() > 0) {
          for (K8sNodeResourcesVO resourcesVO : list) {
            //被隔离的node节点不计算在内
            if (resourcesVO.getIs_isolatior() == 1) continue;
            String cpu_request = resourcesVO.getCpu_request();
            String mem_request = resourcesVO.getMemory_request();
            cpu_used_total += Float.valueOf(cpu_request.substring(cpu_request.indexOf("(") + 1, cpu_request.indexOf("%")));
            mem_used_total += Float.valueOf(mem_request.substring(mem_request.indexOf("(") + 1, mem_request.indexOf("%")));
          }
          if (cpu_used_total / total > 90 || mem_used_total / total > 90) {
            responseDTO.success("node资源紧张");
          }
        }
      }
      Map<String, Object> stratgyMap = new HashMap<>();
      Map<String, Integer> rollingMap = new HashMap<>();
      rollingMap.put("maxSurge", strategyParamVO.getMaxSurge());
      rollingMap.put("maxUnavailable", strategyParamVO.getMaxUnavailable());
      stratgyMap.put("type", "RollingUpdate");
      stratgyMap.put("rollingUpdate", rollingMap);
      String op = null;
      if (deployment.getSpec().getStrategy() == null) op = "add";
      AppsV1Api extensionsV1beta1Api = k8sService.getExtensionsV1beta1ApiByConfig(moduleEnv);
      try {
        ArrayList<JsonObject> jsonObjects = K8sUtils.generatePatchPath(K8sPatchMirror.SPEC_STRATEGY, stratgyMap, op);
        extensionsV1beta1Api.patchNamespacedDeployment(yamlList.get(0).getYamlName(),
            yamlList.get(0).getYamlNamespace(), new V1Patch(jsonObjects.toString()),
            null, null, null, null, null);
      } catch (ApiException e) {
        log.info("设置滚动升级参数失败");
        return responseDTO.fail("设置失败");
      }
    }
    return responseDTO;
  }
  
  @Override
  public ResponseDTO changeStrategyArgs2(Integer moduleId, Integer envId,
                                         String maxUnavailable) {
    ResponseDTO responseDTO = new ResponseDTO();
    responseDTO.success("设置成功");
    ModuleEnv moduleEnv = envMapper.selectOne(envId);
    List<ModuleDeployYaml> yamlList =
        getModuleDeployByModuleAndEnvId(moduleId, envId, true);
    if (yamlList != null && yamlList.size() > 0) {
      V1Deployment deployment =
          k8sService.readNameSpacedResource(yamlList.get(0).getYamlName(),
              moduleEnv, yamlList.get(0).getYamlNamespace(),
              K8sKindTypeEnum.DEPLOYMENT.getKindType(), V1Deployment.class);
      //Integer replicas = deployment.getSpec().getReplicas();
      
      Map<String, String> nodeSelector = deployment.getSpec().getTemplate().getSpec().getNodeSelector();
      if (nodeSelector != null && nodeSelector.containsKey("center")) {
        StringBuilder sb = new StringBuilder();
        String url = sb.append(getNodeResources).append("search=")
            .append("center").append(":").append(nodeSelector.get("center")).append("&")
            .append("paasid=").append(moduleEnv.getPaasId()).toString();
        String result = HttpUtils.httpGetUrl(url, null, null);
        Map map = JSON.parseObject(result);
        Integer total = (Integer) map.get("count");
        if (total > 100) {
          url = url.replace("page_size=100", "page_size=" + total);
          result = HttpUtils.httpGetUrl(url, null, null);
          map = JSON.parseObject(result);
        }
        Object results = map.get("results");
        float cpu_used_total = 0;
        float mem_used_total = 0;
        List<K8sNodeResourcesVO> list = JSONObject.parseArray(results.toString(), K8sNodeResourcesVO.class);
        if (list != null && list.size() > 0) {
          for (K8sNodeResourcesVO resourcesVO : list) {
            //被隔离的node节点不计算在内
            if (resourcesVO.getIs_isolatior() == null || resourcesVO.getIs_isolatior() == 1) continue;
            String cpu_request = resourcesVO.getCpu_request();
            String mem_request = resourcesVO.getMemory_request();
            cpu_used_total += Float.valueOf(cpu_request.substring(cpu_request.indexOf("(") + 1, cpu_request.indexOf("%")));
            mem_used_total += Float.valueOf(mem_request.substring(mem_request.indexOf("(") + 1, mem_request.indexOf("%")));
          }
          if (cpu_used_total / total > 90 || mem_used_total / total > 90) {
            responseDTO.success("node资源紧张");
          }
        }
      }
      Map<String, Object> stratgyMap = new HashMap<>();
      Map<String, String> rollingMap = new HashMap<>();
      rollingMap.put("maxUnavailable", maxUnavailable);
      stratgyMap.put("type", "RollingUpdate");
      stratgyMap.put("rollingUpdate", rollingMap);
      String op = null;
      if (deployment.getSpec().getStrategy() == null) op = "add";
  
      AppsV1Api extensionsV1beta1Api = k8sService.getExtensionsV1beta1ApiByConfig(moduleEnv);
      try {
        ArrayList<JsonObject> jsonObjects = K8sUtils.generatePatchPath(K8sPatchMirror.SPEC_STRATEGY, stratgyMap, op);
        extensionsV1beta1Api.patchNamespacedDeployment(
            yamlList.get(0).getYamlName(),
            yamlList.get(0).getYamlNamespace(),
            new V1Patch(jsonObjects.toString()),
            null, null, null, null, null);
      } catch (ApiException e) {
        log.info("设置滚动升级参数失败");
        return responseDTO.fail("设置失败");
      }
    }
    return responseDTO;
  }
  
  /**
   * 查询滚动升级参数
   *
   * @param moduleId
   * @param envId
   * @return
   */
  @Override
  public Map<String, Integer> getStrategyArgs(Integer moduleId, Integer envId) {
    ModuleEnv moduleEnv = envMapper.selectOne(envId);
    List<ModuleDeployYaml> yamlList =
        getModuleDeployByModuleAndEnvId(moduleId, envId, true);
    Map<String, Integer> resultMap = new HashMap<>();
    if (yamlList != null && yamlList.size() > 0) {
      V1Deployment deployment =
          k8sService.readNameSpacedResource(yamlList.get(0).getYamlName(),
              moduleEnv, yamlList.get(0).getYamlNamespace(),
              K8sKindTypeEnum.DEPLOYMENT.getKindType(), V1Deployment.class);
      V1DeploymentStrategy strategy = deployment.getSpec().getStrategy();
      Integer replicas = deployment.getSpec().getReplicas();
      if (strategy.getType().equals("RollingUpdate")) {
        String maxSurge = strategy.getRollingUpdate().getMaxSurge().toString();
        String maxUnavailable = strategy.getRollingUpdate().getMaxUnavailable().toString();
        if (maxSurge.contains("%")) {
          Integer maxS = Integer.valueOf(maxSurge.replace("%", ""));
          maxS = (int) Math.ceil(maxS / 100.0 * replicas);
          maxSurge = maxS.toString();
        }
        if (maxUnavailable.contains("%")) {
          Integer maxU = Integer.valueOf(maxUnavailable.replace("%", ""));
          maxU = (int) Math.floor(maxU / 100.0 * replicas);
          maxUnavailable = maxU.toString();
        }
        resultMap.put("maxSurge", Integer.valueOf(maxSurge));
        resultMap.put("maxUnavailable", Integer.valueOf(maxUnavailable));
      }
    }
    return resultMap;
  }
}
