package com.xc.fast_deploy.service.k8s.impl;

import com.google.gson.JsonSyntaxException;
import com.xc.fast_deploy.dao.master_dao.ModuleDeployMapper;
import com.xc.fast_deploy.dao.master_dao.ModuleEnvMapper;
import com.xc.fast_deploy.dto.k8s.K8sContainerDTO;
import com.xc.fast_deploy.dto.k8s.K8sPodDTO;
import com.xc.fast_deploy.model.master_model.ModuleDeployYaml;
import com.xc.fast_deploy.model.master_model.ModuleEnv;
import com.xc.fast_deploy.myenum.k8sEnum.K8sContainerStatusEnum;
import com.xc.fast_deploy.service.k8s.K8sPodService;
import com.xc.fast_deploy.service.k8s.K8sService;
import com.xc.fast_deploy.utils.constant.K8sNameSpace;
import com.xc.fast_deploy.utils.k8s.K8sExceptionUtils;
import com.xc.fast_deploy.utils.k8s.K8sUtils;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.*;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Call;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.xc.fast_deploy.utils.DateUtils.offsetDateTimeToDate;

@Service
@Slf4j
public class K8sPodServiceImpl implements K8sPodService {
  
  @Autowired
  private K8sService k8sService;
  @Autowired
  private ModuleEnvMapper envMapper;
  
  /**
   * 获取某个环境中的所有的pod
   *
   * @param env
   * @return
   */
  @Override
  public List<K8sPodDTO> getOneEnvAllPod(ModuleEnv env) {
    List<K8sPodDTO> k8sPodDTOList = new ArrayList<>();
    CoreV1Api coreV1Api = k8sService.getCoreV1ApiByConfig(env);
    if (coreV1Api != null) {
      try {
        V1PodList podList = coreV1Api.listNamespacedPod("default", null, null, null, null,
            null, null, null, null, 300, null);
        List<V1Pod> items = podList.getItems();
        for (V1Pod v1Pod : items) {
          K8sPodDTO k8sPodDTO = new K8sPodDTO();
          k8sPodDTO.setPodName(v1Pod.getMetadata().getName());
          k8sPodDTO.setPodIP(v1Pod.getStatus().getPodIP());
          k8sPodDTO.setNodeIP(v1Pod.getStatus().getHostIP());
          k8sPodDTO.setContainerSize(v1Pod.getSpec().getContainers().size());
          if (v1Pod.getStatus().getStartTime() != null) {
            k8sPodDTO.setStartTime(Date.from(v1Pod.getStatus().getStartTime().toInstant()));
  
          } else {
            k8sPodDTO.setStartTime(null);
          }
          List<V1ContainerStatus> containerStatuses = v1Pod.getStatus().getContainerStatuses();
          k8sPodDTO.setStatus(v1Pod.getStatus().getPhase());
          List<K8sContainerDTO> containerDTOList = new ArrayList<>();
          if (containerStatuses != null) {
            for (V1ContainerStatus containerStatus : containerStatuses) {
              String id = containerStatus.getContainerID();
              K8sContainerDTO containerDTO = new K8sContainerDTO();
              containerDTO.setContainerName(containerStatus.getName());
              containerDTO.setImageName(containerStatus.getImage());
              containerDTO.setRestartCount(containerStatus.getRestartCount());
              
              V1ContainerStateRunning running = containerStatus.getState().getRunning();
              V1ContainerStateTerminated terminated = containerStatus.getState().getTerminated();
              V1ContainerStateWaiting waiting = containerStatus.getState().getWaiting();
              if (running != null) {
                Date date = Date.from(running.getStartedAt().toInstant());
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String s = dateFormat.format(date);
                containerDTO.setDescribeMsg("start time: " + s);
                containerDTO.setStatus(K8sContainerStatusEnum.RUNNINT.getStatus());
              }
              if (terminated != null) {
                log.info(id + " is terminated");
                containerDTO.setDescribeMsg(terminated.getReason());
                containerDTO.setStatus(K8sContainerStatusEnum.TERMINATED.getStatus());
              }
              if (waiting != null) {
                log.info(id + " is waiting");
                containerDTO.setDescribeMsg(waiting.getReason());
                containerDTO.setStatus(K8sContainerStatusEnum.WAITING.getStatus());
              }
              containerDTOList.add(containerDTO);
            }
          }
          k8sPodDTO.setContainerDTOList(containerDTOList);
          k8sPodDTOList.add(k8sPodDTO);
        }
      } catch (ApiException e) {
        log.error("获取pod列表出错", e);
        e.printStackTrace();
        return null;
      }
    }
    return k8sPodDTOList;
  }
  
  /**
   * 根据podname名称查询pod的信息
   *
   * @param env
   * @param podName
   * @return
   */
  @Override
  public V1Pod getPodByPodName(ModuleEnv env, String podName, String namespace) {
    V1Pod v1Pod = null;
    if (StringUtils.isNotBlank(podName) && env != null && StringUtils.isNotBlank(env.getK8sConfig())) {
      CoreV1Api coreV1Api = k8sService.getCoreV1ApiByConfig(env);
      if (coreV1Api != null) {
        try {
          v1Pod = coreV1Api.readNamespacedPod(podName, namespace, null);
//                    V1PodList podList = coreV1Api.listNamespacedPod("default", null, null, null,
//                            null, null, null, null, 300, null);
//                    List<V1Pod> v1Pods = podList.getItems();
//                    for (V1Pod v1Pod : v1Pods) {
//                        if (podName.equals(v1Pod.getMetadata().getName())) {
//                            return v1Pod;
//                        }
//                    }
        } catch (ApiException e) {
          log.error("查询pod时出现error,未能查询到该podName:{},resoon:{}", podName, e.getMessage());
//                    e.printStackTrace();
        }
      }
    }
    return v1Pod;
  }
  
  /**
   * 根据podName删除指定环境中的pod
   *
   * @param podName
   * @param env
   * @return
   */
  @Override
  public boolean deletePod(String podName, ModuleEnv env, String namespace) {
    if (StringUtils.isNotBlank(podName) && env != null && StringUtils.isNotBlank(env.getK8sConfig())) {
      
      CoreV1Api coreV1Api = k8sService.getCoreV1ApiByConfig(env);
      if (coreV1Api != null) {
        try {
          V1DeleteOptions deleteOptions = new V1DeleteOptions();
          V1Pod v1Pod = coreV1Api.deleteNamespacedPod(podName, namespace, null, null, null, null, null, deleteOptions);
          log.info("状态message : " + v1Pod);
          log.info("delete pod " + podName + " ok");
          return true;
        } catch (ApiException e) {
          e.printStackTrace();
        } catch (JsonSyntaxException e) {
          return K8sExceptionUtils.anylse(e);
        }
      }
    }
    return false;
  }
  
  /**
   * 查询所有的信息
   *
   * @param env
   * @param podName
   * @param nodeIP
   * @param imageName
   * @return
   */
  @Override
  public List<K8sPodDTO> searchAll(ModuleEnv env, String podName, String nodeIP, String imageName) {
    List<K8sPodDTO> k8sPodDTOS = new ArrayList<>();
    List<K8sPodDTO> podDTOS = getOneEnvAllPod(env);
    
    if (podDTOS != null && podDTOS.size() > 0) {
      if (StringUtils.isNotBlank(podName)) {
        boolean flag = false;
        for (K8sPodDTO k8sPodDTO : podDTOS) {
          if (StringUtils.isNotBlank(k8sPodDTO.getPodName()) && k8sPodDTO.getPodName().contains(podName)) {
            k8sPodDTOS.add(k8sPodDTO);
            flag = true;
          }
        }
        if (!flag) {
          return null;
        }
      }
      if (StringUtils.isNotBlank(nodeIP)) {
        boolean flag = false;
        if (k8sPodDTOS.size() > 0) {
          List<K8sPodDTO> k8sPodDTOS2 = new ArrayList<>();
          for (K8sPodDTO k8sPodDTO : k8sPodDTOS) {
            if (StringUtils.isNotBlank(k8sPodDTO.getNodeIP()) && k8sPodDTO.getNodeIP().contains(nodeIP)) {
              k8sPodDTOS2.add(k8sPodDTO);
              flag = true;
            }
          }
          if (!flag) {
            return null;
          } else {
            k8sPodDTOS.retainAll(k8sPodDTOS2);
          }
        } else {
          for (K8sPodDTO k8sPodDTO : podDTOS) {
            if (StringUtils.isNotBlank(k8sPodDTO.getNodeIP()) && k8sPodDTO.getNodeIP().contains(nodeIP)) {
              k8sPodDTOS.add(k8sPodDTO);
              flag = true;
            }
          }
          if (!flag) {
            return null;
          }
        }
      }
      if (StringUtils.isNotBlank(imageName)) {
        boolean flag = false;
        if (k8sPodDTOS.size() > 0) {
          List<K8sPodDTO> k8sPodDTOS2 = new ArrayList<>();
          for (K8sPodDTO k8sPodDTO : k8sPodDTOS) {
            if (k8sPodDTO.getContainerDTOList() != null && k8sPodDTO.getContainerDTOList().size() > 0) {
              for (K8sContainerDTO k8sContainerDTO : k8sPodDTO.getContainerDTOList()) {
                if (k8sContainerDTO.getImageName() != null && k8sContainerDTO.getImageName().contains(imageName)) {
                  k8sPodDTOS2.add(k8sPodDTO);
                  flag = true;
                }
              }
            }
          }
          if (!flag) {
            return null;
          } else {
            k8sPodDTOS = k8sPodDTOS2;
          }
        } else {
          for (K8sPodDTO k8sPodDTO : k8sPodDTOS) {
            if (k8sPodDTO.getContainerDTOList() != null && k8sPodDTO.getContainerDTOList().size() > 0) {
              for (K8sContainerDTO k8sContainerDTO : k8sPodDTO.getContainerDTOList()) {
                if (k8sContainerDTO.getImageName() != null && k8sContainerDTO.getImageName().contains(imageName)) {
                  flag = true;
                }
              }
              if (!flag) {
                return null;
              }
            }
            
          }
        }
      }
      
    }
    return k8sPodDTOS;
  }
  
  @Override
  public InputStream getConnect2PodLogsByInputStream(Integer moduleEnvId, String namespace, String podName, String containerName) {
    InputStream call = null;
    if (moduleEnvId != null) {
      ModuleEnv moduleEnv = envMapper.selectOne(moduleEnvId);
      if (StringUtils.isNotBlank(podName) && StringUtils.isNotBlank(containerName)) {
        try {
          call = K8sUtils.okhttpGetBackByInputStream(moduleEnv.getK8sConfig(),
              "/api/v1/namespaces/" + namespace + "/pods/" + podName + "/log?container=" + containerName);
        } catch (Exception e) {
          log.error(e.getMessage(), e);
          e.printStackTrace();
        }
      }
    }
    return call;
  }
  
  @Override
  public Call getConnect2PodLogs(Integer moduleEnvId, String namespace, String podName, String containerName) {
    Call call = null;
    if (moduleEnvId != null) {
      ModuleEnv moduleEnv = envMapper.selectOne(moduleEnvId);
      if (StringUtils.isNotBlank(podName) && StringUtils.isNotBlank(containerName)) {
        CoreV1Api coreV1Api = k8sService.getCoreV1ApiByConfig(moduleEnv);
        if (coreV1Api != null) {
          try {
            call = coreV1Api.readNamespacedPodLogCall(podName, namespace, containerName,
                null, null, null, null, null,
                null, null, null, null);
            K8sUtils.okhttpGetBack(moduleEnv.getK8sConfig(),
                "/api/v1/namespaces/" + namespace + "/pods/" + podName + "/log?container=other-deploy-psot-billing-deploy-5g");
            //"/api/v1/namespaces/{namespace}/pods/{name}/log"
            // Request{method=GET, url=https://134.108.6.27:6443/api/v1/namespaces/default/pods/other-deploy-psot-billing-deploy-5g-7bdcbfbcd9-9qvc8/log?container=other-deploy-psot-billing-deploy-5g&follow=true&sinceSeconds=700, tag=null}
            ////   K8sUtils.okhttpGetBack(moduleEnv.getK8sConfig(),
            //       "/api/v1/namespaces/" + namespace + "/pods/" + podName + "/log?container=other-deploy-psot-billing-deploy-5g&follow=true&sinceSeconds=700");
          } catch (Exception e) {
            log.error(e.getMessage(), e);
            e.printStackTrace();
          }
        }
      }
    }
    return call;
  }
  
  @Override
  public V1PodList listAllPodsByEnvId(Integer billEnvId, String namespace) {
    V1PodList v1PodList = null;
    ModuleEnv moduleEnv = envMapper.selectOne(billEnvId);
    if (StringUtils.isNotBlank(namespace) && moduleEnv != null &&
        StringUtils.isNotBlank(moduleEnv.getK8sConfig())) {
      CoreV1Api coreV1Api = k8sService.getCoreV1ApiByConfig(moduleEnv);
      try {
        v1PodList = coreV1Api.listNamespacedPod(namespace, null, null, null,
            null, null, null, null, null, 60, null);
      } catch (ApiException e) {
        log.error("连接k8s API 出现错误");
        e.printStackTrace();
      }
    }
    return v1PodList;
  }
  
  @Override
  public V1Pod createPod(V1Pod v1Pod, Integer billEnvId, String namespace) throws ApiException {
    V1Pod podResult = null;
    if (v1Pod != null && StringUtils.isNotBlank(namespace) && StringUtils.isNotBlank(namespace.trim())) {
      ModuleEnv moduleEnv = envMapper.selectOne(billEnvId);
      if (StringUtils.isNotBlank(namespace) && moduleEnv != null &&
          StringUtils.isNotBlank(moduleEnv.getK8sConfig())) {
        CoreV1Api coreV1Api = k8sService.getCoreV1ApiByConfig(moduleEnv);
        log.info("开始创建pod操作");
        podResult = coreV1Api.createNamespacedPod(namespace, v1Pod, null, null, null, null);
  
      }
    }
    return podResult;
  }
  
  /**
   * 删除计费pod
   *
   * @param podName
   * @param billEnvId
   * @param namespace
   * @param gracePeriodSeconds
   * @return
   */
  @Override
  public boolean deleteBillPod(String podName, Integer billEnvId, String namespace, Integer gracePeriodSeconds) {
    if (StringUtils.isNotBlank(podName) && billEnvId != null && StringUtils.isNotBlank(namespace)) {
      ModuleEnv moduleEnv = envMapper.selectOne(billEnvId);
      if (moduleEnv != null && StringUtils.isNotBlank(moduleEnv.getK8sConfig())) {
        CoreV1Api coreV1Api = k8sService.getCoreV1ApiByConfig(moduleEnv);
        V1DeleteOptions deleteOptions = new V1DeleteOptions();
        deleteOptions.setOrphanDependents(false);
        try {
          V1Pod v1Status = coreV1Api.deleteNamespacedPod(podName, namespace, null, null,
              gracePeriodSeconds, null, null, deleteOptions);
          log.info("删除pod: {} status: {}", podName, v1Status.getStatus());
          return true;
        } catch (ApiException e) {
          e.printStackTrace();
        } catch (JsonSyntaxException e) {
          return K8sExceptionUtils.anylse(e);
        }
      }
    }
    return false;
  }
  
  /**
   * 获取某个环境中的metadataName 对应的pod信息
   *
   * @param coreV1Api    操作api
   * @param generateName 生成的名称
   * @param namespace    命名空间
   * @return
   */
//    @Override
//    public List<V1Pod> getPodInfoByGenerateName(CoreV1Api coreV1Api, String generateName, String namespace) {
//        log.info("根据generateName获取pod信息");
//        List<V1Pod> pods = new ArrayList<>();
//        if (coreV1Api != null && StringUtils.isNotBlank(generateName) && StringUtils.isNotBlank(namespace)) {
//            try {
//                V1PodList v1PodList = coreV1Api.listNamespacedPod(namespace, null, null, null, null,
//                        null, null, null, 300, null);
//                if (v1PodList != null && v1PodList.getItems().size() > 0) {
//                    List<V1Pod> items = v1PodList.getItems();
//                    for (V1Pod pod : items) {
//                        if (generateName.equals(pod.getMetadata().getGenerateName())) {
//                            pods.add(pod);
//                        }
//                    }
//                }
//            } catch (ApiException e) {
//                log.error("获取pod信息失败");
//            }
//        }
//        return pods;
//    }
  
  /**
   * 获取某个环境中的metadataName 对应的pod信息
   *
   * @param coreV1Api 操作api
   * @param yamlName  应用的名称
   * @param namespace 命名空间
   * @return
   */
  @Override
  public List<V1Pod> getPodInfoByName(CoreV1Api coreV1Api, String yamlName, String namespace, String label) {
    log.info("根据generateName获取pod信息,参数: {} label:{}", yamlName, label);
    List<V1Pod> pods = new ArrayList<>();
    if (coreV1Api != null && StringUtils.isNotBlank(yamlName) && StringUtils.isNotBlank(namespace)) {
      try {
        //首先根据label 取pod数据 如果没有取到 在去掉label去匹配数据
        V1PodList v1PodList = coreV1Api.listNamespacedPod(namespace, null, null,
            null, null, label, null, null,
            null, 300, null);
        if (v1PodList != null && v1PodList.getItems().size() > 0) {
          List<V1Pod> items = v1PodList.getItems();
          for (V1Pod pod : items) {
            String podName = pod.getMetadata().getName();
            if (StringUtils.isNotBlank(podName) && podName.startsWith(yamlName)) {
              pods.add(pod);
            }
          }
        } else {
          //针对ingress的pod查看信息添加
          if (yamlName.toLowerCase().contains("ingress")) {
            V1PodList v1PodList2 = coreV1Api.listNamespacedPod(namespace,
                null, null, null,
                null, null, null, null,
                null, 300, null);
            if (v1PodList2 != null && v1PodList2.getItems().size() > 0) {
              List<V1Pod> items = v1PodList2.getItems();
              for (V1Pod pod : items) {
                String podName = pod.getMetadata().getName();
                if (StringUtils.isNotBlank(podName) && podName.startsWith(yamlName + "-")) {
                  pods.add(pod);
                }
              }
            }
          }
        }
      } catch (ApiException e) {
        log.error("获取pod信息失败: " + e.getResponseBody());
      }
    }
    return pods;
  }
  
}
