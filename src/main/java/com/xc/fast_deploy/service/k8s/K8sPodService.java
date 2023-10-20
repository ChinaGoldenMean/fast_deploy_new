package com.xc.fast_deploy.service.k8s;

import com.xc.fast_deploy.dto.k8s.K8sPodDTO;
import com.xc.fast_deploy.model.master_model.ModuleEnv;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1Pod;
import io.kubernetes.client.openapi.models.V1PodList;
import okhttp3.Call;

import java.io.InputStream;
import java.util.List;

public interface K8sPodService {
  
  List<K8sPodDTO> getOneEnvAllPod(ModuleEnv env);
  
  V1Pod getPodByPodName(ModuleEnv env, String podName, String namespace);
  
  boolean deletePod(String podName, ModuleEnv env, String namespace);
  
  List<K8sPodDTO> searchAll(ModuleEnv env, String podName, String nodeIP, String imageName);
  
  Call getConnect2PodLogs(Integer moduleEnvId, String namespace, String podName, String containerName);
  
  InputStream getConnect2PodLogsByInputStream(Integer moduleEnvId, String namespace, String podName, String containerName);
  
  V1PodList listAllPodsByEnvId(Integer billEnvId, String namespace);
  
  V1Pod createPod(V1Pod v1Pod, Integer billEnvId, String namespace) throws ApiException;
  
  boolean deleteBillPod(String podName, Integer billEnvId, String namespace, Integer gracePeriodSeconds);
  
  /**
   * 获取某个模块的发布的pod信息
   */
  List<V1Pod> getPodInfoByName(CoreV1Api coreV1Api, String yamlName, String namespace, String label);
  
}
