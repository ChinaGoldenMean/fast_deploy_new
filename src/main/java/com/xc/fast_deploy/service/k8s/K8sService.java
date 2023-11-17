package com.xc.fast_deploy.service.k8s;

import com.xc.fast_deploy.model.master_model.ModuleDeployYaml;
import com.xc.fast_deploy.model.master_model.ModuleEnv;
import com.xc.fast_deploy.vo.K8sYamlVo;
import com.xc.fast_deploy.websocketConfig.K8sPodWatchWebsocketServer;
import io.kubernetes.client.openapi.apis.AppsV1Api;
import io.kubernetes.client.openapi.apis.BatchV1Api;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.apis.NetworkingV1Api;
import io.kubernetes.client.openapi.models.V1Deployment;
import io.kubernetes.client.openapi.models.V1Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;

public interface K8sService {
  
  boolean deploy(K8sYamlVo k8sYamlVo, ModuleEnv env, String mirrorName);
  
  AppsV1Api getExtensionsV1beta1ApiByConfig(ModuleEnv env);
  
  CoreV1Api getCoreV1ApiByConfig(ModuleEnv env);
  
  NetworkingV1Api getNetworkingV1Api(ModuleEnv env);
  
  BatchV1Api getBatchV1Api(ModuleEnv env);
  
  AppsV1Api getAppsV1Api(ModuleEnv env);
  
  //AppsV1beta2Api getAppsV1beta2Api(ModuleEnv env);
  
  ExecutorService startWatchPods(K8sPodWatchWebsocketServer watchWebsocketServer,
                                 ExecutorService executorService);
  
  Integer scaleModuleSize(String deploymentName, String namespace, Integer deployId, ModuleEnv env, Integer modulePodSize);
  
  boolean deleteNamespacedSource(String sourceName, String kind, String namespace, ModuleEnv env);
  
  boolean replaceNamespacedSourceMirror(ModuleDeployYaml deployYaml, String aDefault, ModuleEnv moduleEnv, String mirrorName);
  
  V1Service getSvcInfoByName(ModuleEnv moduleEnv, String yamlName, String namespace);
  
  List<Object> listAllNameSpacedResource(ModuleEnv env, String namespace, String kind);
  
  String getResourceByNameUseOKHttp(String resourceName, ModuleEnv moduleEnv,
                                    String namespace, String kind);
  
  <T> T readNameSpacedResource(String resourceName, ModuleEnv moduleEnv,
                               String namespace, String kindType, Class<T> clazz);
  
  Map<String, String> getContainerLimit(V1Deployment deployment);
  
  Map<String, String> getContainerRequest(V1Deployment deployment);
}
