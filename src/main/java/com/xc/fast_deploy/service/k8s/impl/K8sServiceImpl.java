package com.xc.fast_deploy.service.k8s.impl;

import com.alibaba.fastjson.JSONObject;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.xc.fast_deploy.model.master_model.ModuleDeployYaml;
import com.xc.fast_deploy.model.master_model.ModuleEnv;
import com.xc.fast_deploy.myException.K8SDeployException;
import com.xc.fast_deploy.mycallback.WatchHandler;
import com.xc.fast_deploy.myenum.k8sEnum.K8sApiversionTypeEnum;
import com.xc.fast_deploy.myenum.k8sEnum.K8sKindTypeEnum;
import com.xc.fast_deploy.service.common.ModuleEnvService;
import com.xc.fast_deploy.service.k8s.K8sService;
import com.xc.fast_deploy.utils.constant.K8sHttpUrlConstants;
import com.xc.fast_deploy.utils.constant.K8sNameSpace;
import com.xc.fast_deploy.utils.constant.K8sPatchMirror;
import com.xc.fast_deploy.utils.k8s.K8sExceptionUtils;
import com.xc.fast_deploy.utils.k8s.K8sManagement;
import com.xc.fast_deploy.utils.k8s.K8sUtils;
import com.xc.fast_deploy.vo.K8sYamlVo;
import com.xc.fast_deploy.websocketConfig.K8sPodWatchWebsocketServer;
import io.kubernetes.client.custom.Quantity;
import io.kubernetes.client.custom.V1Patch;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.AppsV1Api;
import io.kubernetes.client.openapi.apis.BatchV1Api;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.apis.NetworkingV1Api;
import io.kubernetes.client.openapi.models.*;
import io.kubernetes.client.proto.V1beta1Extensions;
import io.kubernetes.client.util.Watch;
import io.kubernetes.client.util.Yaml;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.scanner.ScannerException;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ExecutorService;

import static com.xc.fast_deploy.utils.constant.K8sHttpUrlConstants.*;
import static com.xc.fast_deploy.utils.k8s.K8sManagement.getNetworkingV1Api;

@Slf4j
@Service
public class K8sServiceImpl implements K8sService {
  
  @Autowired
  private ModuleEnvService envService;
  
  @Value("${file.storge.path.prefix}")
  private String storgePrefix;
  
  @Value("${myself.pspass.prod}")
  private boolean isProdEnv;
  
  @Value("${myself.pspass.oldversion}")
  private Integer[] oldEnvId;
  
  /**
   * 根据yaml文件生成的vo类发布内容
   *
   * @param k8sYamlVo
   * @param env
   * @param mirrorName
   * @return
   */
  @Override
  public boolean deploy(K8sYamlVo k8sYamlVo, ModuleEnv env, String mirrorName) {
    if (k8sYamlVo != null && env != null && StringUtils.isNotBlank(env.getK8sConfig())) {
      log.info("k8sYamlVO为:{}", new Gson().toJson(k8sYamlVo));
      String kind = k8sYamlVo.getKind();
      String apiVersion = k8sYamlVo.getApiVersion();
      String namespace = k8sYamlVo.getNamespace();
      if (StringUtils.isBlank(namespace)) {
        k8sYamlVo.setNamespace(K8sNameSpace.DEFAULT);
      }
      K8sKindTypeEnum kindTypeEnum = K8sKindTypeEnum.getEnumByType(kind);
      K8sApiversionTypeEnum apiversionTypeEnum = K8sApiversionTypeEnum.getEnumByType(apiVersion);
      if (kindTypeEnum != null && apiversionTypeEnum != null) {
        try {
          switch (apiversionTypeEnum) {
            case COREAPIV1:
              CoreV1Api coreV1Api = getCoreV1ApiByConfig(env);
              if (coreV1Api != null) {
                switch (kindTypeEnum) {
                  case POD:
                    log.info("发布pod");
                    V1Pod pod = K8sUtils.getObject(k8sYamlVo.getO(), V1Pod.class);
                    if (StringUtils.isNotBlank(mirrorName)) {
                      pod.getSpec().getContainers().get(0).setImage(mirrorName);
                    }
                    V1Pod v1Pod = coreV1Api.createNamespacedPod(k8sYamlVo.getNamespace(),
                        pod, null, null, null, null);
                    if (v1Pod != null) {
                      return true;
                    }
                    break;
                  case CONFIG_MAP:
                    log.info("发布configMap");
                    V1ConfigMap configMap =
                        K8sUtils.getObject(k8sYamlVo.getO(), V1ConfigMap.class);
                    V1ConfigMap v1ConfigMap =
                        coreV1Api.createNamespacedConfigMap(k8sYamlVo.getNamespace(),
                            configMap, null, null, null, null);
                    if (v1ConfigMap != null) {
                      return true;
                    }
                    break;
                  case SERVICE:
                    log.info("发布service");
                    V1Service v1Service = coreV1Api.createNamespacedService(
                        k8sYamlVo.getNamespace(),
                        K8sUtils.getObject(k8sYamlVo.getO(), V1Service.class),
                        null, null, null, null);
                    if (v1Service != null) {
                      return true;
                    }
                    break;
                  case REPLICATIONCONTROLLER:
                    log.info("发布rc");
                    V1ReplicationController rc =
                        K8sUtils.getObject(k8sYamlVo.getO(),
                            V1ReplicationController.class);
  
                    if (StringUtils.isNotBlank(mirrorName)) {
                      rc.getSpec().getTemplate().getSpec()
                          .getContainers().get(0).setImage(mirrorName);
                    }
  
                    V1ReplicationController replicationController =
                        coreV1Api.createNamespacedReplicationController(
                            k8sYamlVo.getNamespace(), rc,
                            null, null, null, null);
                    if (replicationController != null) {
                      return true;
                    }
                    break;
                  case ENDPOINTS:
                    log.info("发布v1Endpoints");
                    V1Endpoints v1Endpoints = coreV1Api.createNamespacedEndpoints(
                        k8sYamlVo.getNamespace(),
                        K8sUtils.getObject(k8sYamlVo.getO(), V1Endpoints.class),
                        null, null, null, null);
                    if (v1Endpoints != null) {
                      return true;
                    }
                    break;
                  default:
                    break;
                }
              }
              break;
            case APPV1:
              AppsV1Api appsV1Api = getAppsV1Api(env);
              if (appsV1Api != null) {
                switch (kindTypeEnum) {
                  case DEPLOYMENT:
                    log.info("发布deployment");
                    V1Deployment beta1Deployment =
                        K8sUtils.getObject(k8sYamlVo.getO(),
                            V1Deployment.class);
                    if (StringUtils.isNotBlank(mirrorName)) {
                      beta1Deployment.getSpec().getTemplate()
                          .getSpec().getContainers().get(0).setImage(mirrorName);
                      // 6-29 针对 uat 和 bd 环境的发布是忽略掉nodeSelector标签
                      if (env.getId().equals(27) || env.getId().equals(26)
                          || env.getId().equals(33)) {
                        beta1Deployment.getSpec().getTemplate().getSpec()
                            .setNodeSelector(null);
                      }
  
                      V1LabelSelector v1LabelSelector = new V1LabelSelector();
                      v1LabelSelector.setMatchLabels(beta1Deployment.getSpec().getTemplate().getMetadata().getLabels());
                      beta1Deployment.getSpec().setSelector(v1LabelSelector);
                      //12-2 针对生产和灾备的发布 resource资源limit必须要
                      // 设置值才能发布否则发布报错
                      if (env.getId().equals(34) || env.getId().equals(30)) {
                        List<V1Container> containerList = beta1Deployment.getSpec()
                            .getTemplate().getSpec().getContainers();
                        boolean flag = false;
                        if (containerList != null && containerList.size() > 0) {
                          for (V1Container container : containerList) {
                            if (container.getResources() == null) {
                              flag = true;
                              break;
                            }
                            if (container.getResources().getLimits() == null) {
                              flag = true;
                              break;
                            }
                            if (container.getResources().getLimits()
                                .get("cpu") == null ||
                                container.getResources().getLimits()
                                    .get("memory") == null) {
                              flag = true;
                              break;
                            }
                          }
                        }
                        if (flag) {
                          throw new K8SDeployException("发布出错,生产和灾备环境下请检查" +
                              "yaml文件的resource的limit是否设置");
                        }
                      }
                    }
                    V1Deployment deployment =
                        appsV1Api.createNamespacedDeployment(
                            k8sYamlVo.getNamespace(), beta1Deployment,
                            null,
                            null, null, null);
  
                    if (deployment != null) {
                      return true;
                    }
                    break;
                  case DAEMONSET:
                    log.info("发布daemonset");
                    V1DaemonSet daemonSet1 = K8sUtils.getObject(k8sYamlVo.getO(),
                        V1DaemonSet.class);
                    if (StringUtils.isNotBlank(mirrorName)) {
                      daemonSet1.getSpec().getTemplate()
                          .getSpec().getContainers().get(0).setImage(mirrorName);
                    }
                    V1DaemonSet daemonSet =
                        appsV1Api.createNamespacedDaemonSet(k8sYamlVo.getNamespace(),
                            daemonSet1, null, null, null, null);
  
                    if (daemonSet != null) {
                      return true;
                    }
                    break;
                  case REPLICASET:
                    log.info("发布rs");
                    V1ReplicaSet replicaSet1 =
                        K8sUtils.getObject(k8sYamlVo.getO(), V1ReplicaSet.class);
                    if (StringUtils.isNotBlank(mirrorName)) {
                      replicaSet1.getSpec().getTemplate()
                          .getSpec().getContainers()
                          .get(0).setImage(mirrorName);
                    }
                    V1ReplicaSet replicaSet =
                        appsV1Api.createNamespacedReplicaSet(k8sYamlVo.getNamespace(),
                            replicaSet1, null, null, null, null);
  
                    if (replicaSet != null) {
                      return true;
                    }
                    break;
                  case STATEFULSET:
                    V1StatefulSet statefulSet = appsV1Api
                        .createNamespacedStatefulSet(k8sYamlVo.getNamespace(),
                            K8sUtils.getObject(k8sYamlVo.getO(), V1StatefulSet.class),
                            null, null, null, null);
  
                    if (statefulSet != null) {
                      return true;
                    }
                    break;
                  default:
                    break;
                }
              }
              break;
            case APPS_V1VETA2:
              break;
            case EXTENSIONAPI:
              AppsV1Api v1beta1Api = getExtensionsV1beta1ApiByConfig(env);
              NetworkingV1Api networkingV1Api = getNetworkingV1Api(env.getK8sConfig());
  
              switch (kindTypeEnum) {
                case INGRESS:
                  log.info("发布ingress");
                  V1Ingress v1beta1Ingress =
                      networkingV1Api.createNamespacedIngress(k8sYamlVo.getNamespace(),
                          K8sUtils.getObject(k8sYamlVo.getO(), V1Ingress.class),
                          null, null, null, null);
  
                  if (v1beta1Ingress != null) {
                    return true;
                  }
                  break;
                case DEPLOYMENT:
                  log.info("发布deployment");
                  V1Deployment beta1Deployment =
                      K8sUtils.getObject(k8sYamlVo.getO(),
                          V1Deployment.class);
  
                  if (StringUtils.isNotBlank(mirrorName)) {
                    beta1Deployment.getSpec().getTemplate()
                        .getSpec().getContainers().get(0).setImage(mirrorName);
                    // 6-29 针对 uat 和 bd 环境的发布是忽略掉nodeSelector标签
                    if (env.getId().equals(27) || env.getId().equals(26)
                        || env.getId().equals(33)) {
                      beta1Deployment.getSpec().getTemplate().getSpec()
                          .setNodeSelector(null);
                    }
                    //12-2 针对生产和灾备的发布 resource资源limit必须要
                    // 设置值才能发布否则发布报错
                    if (env.getId().equals(34) || env.getId().equals(30)) {
                      List<V1Container> containerList = beta1Deployment.getSpec()
                          .getTemplate().getSpec().getContainers();
                      boolean flag = false;
                      if (containerList != null && containerList.size() > 0) {
                        for (V1Container container : containerList) {
                          if (container.getResources() == null) {
                            flag = true;
                            break;
                          }
                          if (container.getResources().getLimits() == null) {
                            flag = true;
                            break;
                          }
                          if (container.getResources().getLimits()
                              .get("cpu") == null ||
                              container.getResources().getLimits()
                                  .get("memory") == null) {
                            flag = true;
                            break;
                          }
                        }
                      }
                      if (flag) {
                        throw new K8SDeployException("发布出错,生产和灾备环境下请检查" +
                            "yaml文件的resource的limit是否设置");
                      }
                    }
                  }
  
                  V1Deployment deployment =
                      v1beta1Api.createNamespacedDeployment(
                          k8sYamlVo.getNamespace(), beta1Deployment,
                          null,
                          null, null, null);
  
                  if (deployment != null) {
                    return true;
                  }
                  break;
                case DAEMONSET:
                  log.info("发布daemonset");
  
                  V1DaemonSet daemonSet1 = K8sUtils.getObject(k8sYamlVo.getO(),
                      V1DaemonSet.class);
  
                  if (StringUtils.isNotBlank(mirrorName)) {
                    daemonSet1.getSpec().getTemplate()
                        .getSpec().getContainers().get(0).setImage(mirrorName);
                  }
  
                  V1DaemonSet daemonSet =
                      v1beta1Api.createNamespacedDaemonSet(k8sYamlVo.getNamespace(),
                          daemonSet1, null, null, null, null);
  
                  if (daemonSet != null) {
                    return true;
                  }
                  break;
                case REPLICASET:
                  log.info("发布rs");
  
                  V1ReplicaSet replicaSet1 =
                      K8sUtils.getObject(k8sYamlVo.getO(), V1ReplicaSet.class);
  
                  if (StringUtils.isNotBlank(mirrorName)) {
                    replicaSet1.getSpec().getTemplate()
                        .getSpec().getContainers()
                        .get(0).setImage(mirrorName);
                  }
  
                  V1ReplicaSet replicaSet =
                      v1beta1Api.createNamespacedReplicaSet(k8sYamlVo.getNamespace(),
                          replicaSet1, null, null, null, null);
  
                  if (replicaSet != null) {
                    return true;
                  }
                  break;
                default:
                  break;
              }
              break;
            default:
              break;
          }
        } catch (ApiException e) {
          log.error(e.getMessage(), e);
          e.printStackTrace();
          throw new K8SDeployException("发布API出错,请检查yaml文件: " +
              e.getMessage() + e.getResponseBody());
        }
      }
  
    }
    return false;
  }
  
  /**
   * 启动监控pods的操作
   */
  @Override
  public ExecutorService startWatchPods(K8sPodWatchWebsocketServer watchWebsocketServer, ExecutorService executorService) {
    ModuleEnv moduleEnv = envService.selectById(1);
    try {
      CoreV1Api coreV1Api = K8sManagement.getCoreV1Api(moduleEnv.getK8sConfig());
      ApiClient apiClient = K8sManagement.getApiClient();
      if (apiClient != null) {
        Watch<V1Pod> watch = Watch.createWatch(apiClient,
            coreV1Api.listNamespacedPodCall(K8sNameSpace.DEFAULT,
                null, null, null,
                null, null, null,
                null, null, null,
                null,
    
                null), new TypeToken<Watch.Response<V1Pod>>() {
            }.getType());
        executorService.execute(new WatchHandler(watch, watchWebsocketServer));
      } else {
        log.error("k8s-client connect error");
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return executorService;
  }
  
  /**
   * 只针对deployment 做的扩缩容
   *
   * @param deploymentName
   * @param namespace
   * @param deployId
   * @param env
   * @param modulePodSize
   * @return 返回的是原deployment的副本数
   */
  @Override
  public Integer scaleModuleSize(String deploymentName, String namespace, Integer deployId, ModuleEnv env, Integer modulePodSize) {
    Integer replicas = 0;
    StringBuilder sb = new StringBuilder();
    List<Integer> oldEnvList = Arrays.asList(oldEnvId);
    if (oldEnvList.contains(env.getId())) {
      sb.append(EXTENSION_V1BETA1_PREFIX);
    } else {
      sb.append(APPS_V1);
    }
    sb.append(namespace).append(EXTENSION_DEPLOYMENTS).append(deploymentName);
    ArrayList<JsonObject> jsonObjects =
        K8sUtils.generatePatchPath(K8sPatchMirror.SPEC_REPLICAS, modulePodSize, null);
    Object o = null;
    try {
      String back = K8sUtils.okhttpPatchBack(env.getK8sConfig(), sb.toString(), jsonObjects);
      o = Yaml.load(back);
    } catch (ApiException | IOException e) {
      log.info("扩缩容失败:{}", e.getMessage());
      return null;
    }
    if (oldEnvList.contains(env.getId())) {
  
      V1Deployment object = K8sUtils.getObject(o, V1Deployment.class);
  
      replicas = object.getStatus().getUpdatedReplicas();
    } else {
      V1Deployment object = K8sUtils.getObject(o, V1Deployment.class);
      replicas = object.getStatus().getUpdatedReplicas();
    }
    return replicas;
  }

//    public boolean deleteMySelfDeployment(String deploymentName, String namespace,
//                                          ModuleEnv env, Integer modulePodSize) {
//        ExtensionsV1beta1Api v1beta1Api = getExtensionsV1beta1ApiByConfig(env);
//        if (v1beta1Api != null) {
//
//        }
//
//        return false;
//    }
  
  /**
   * 下线对应的资源  删除资源 并删除级联的全部依赖譬如deployment即删除rs等资源
   *
   * @param sourceName
   * @param kind
   * @param namespace
   * @param env
   * @return
   */
  @Override
  public boolean deleteNamespacedSource(String sourceName, String kind,
                                        String namespace, ModuleEnv env) {
    log.info("开始下线某个应用: {}", sourceName);
    K8sKindTypeEnum enumByType = K8sKindTypeEnum.getEnumByType(kind);
    //设置3s的优雅下线机制
    Long gracePeriodSeconds = 3L;
    if (enumByType != null) {
      V1DeleteOptions v1DeleteOptions = new V1DeleteOptions();
      //v1DeleteOptions.setOrphanDependents(null);
      v1DeleteOptions.setPropagationPolicy("Foreground");
      v1DeleteOptions.setGracePeriodSeconds(gracePeriodSeconds);
      List<Integer> oldEnvList = Arrays.asList(oldEnvId);
      CoreV1Api coreV1Api = getCoreV1ApiByConfig(env);
      AppsV1Api appsV1Api = getAppsV1Api(env);
      AppsV1Api v1beta1Api = getExtensionsV1beta1ApiByConfig(env);
      NetworkingV1Api networkingV1Api = getNetworkingV1Api(env.getK8sConfig());
  
      if (coreV1Api != null && appsV1Api != null && v1beta1Api != null) {
        try {
          //老版本环境用http的方式操作K8S
          if (oldEnvList.contains(env.getId())) {
            StringBuilder url = new StringBuilder();
            switch (enumByType) {
              case DEPLOYMENT:
                //先将副本数设置为0 在进行后续的操作
                url.append(EXTENSION_V1BETA1_PREFIX).append(namespace)
                    .append(EXTENSION_DEPLOYMENTS).append(sourceName);
                K8sUtils.okhttpDeleteBack(env.getK8sConfig(), url.toString());
                return true;
              case DAEMONSET:
                //先将副本数设置为0 在进行后续的操作
                url.append(EXTENSION_V1BETA1_PREFIX).append(namespace)
                    .append(EXTENSION_DAEMONSETS).append(sourceName);
                K8sUtils.okhttpDeleteBack(env.getK8sConfig(), url.toString());
                return true;
              case REPLICASET:
                url.append(EXTENSION_V1BETA1_PREFIX).append(namespace)
                    .append(EXTENSION_REPLICASETS).append(sourceName);
                K8sUtils.okhttpDeleteBack(env.getK8sConfig(), url.toString());
                return true;
            }
          }
          switch (enumByType) {
            case POD:
              coreV1Api.deleteNamespacedPod(sourceName,
                  namespace, null, null, null,
                  null, null, v1DeleteOptions);
              return true;
            case SERVICE:
              coreV1Api.deleteNamespacedService(sourceName,
                  namespace, null, null, null,
                  null, null, v1DeleteOptions);
              return true;
            case REPLICATIONCONTROLLER:
              coreV1Api.deleteNamespacedReplicationController(sourceName,
                  namespace, null, null, null,
                  null, null, v1DeleteOptions);
              return true;
            case DEPLOYMENT:
              //先将副本数设置为0 在进行后续的操作
              appsV1Api.deleteNamespacedDeployment(sourceName,
                  namespace, null, null, null,
                  null, null, v1DeleteOptions);
              return true;
            case INGRESS:
              networkingV1Api.deleteNamespacedIngress(sourceName,
    
                  namespace, null, null, null,
                  null, null, v1DeleteOptions);
              return true;
            case DAEMONSET:
              appsV1Api.deleteNamespacedDaemonSet(sourceName,
                  namespace, null, null, null,
                  null, null, v1DeleteOptions);
              return true;
            case REPLICASET:
              appsV1Api.deleteNamespacedReplicaSet(sourceName,
                  namespace, null, null, null,
                  null, null, v1DeleteOptions);
              return true;
            case CONFIG_MAP:
              coreV1Api.deleteNamespacedConfigMap(sourceName,
                  namespace, null, null, null,
                  null, null, v1DeleteOptions);
              return true;
            case ENDPOINTS:
              coreV1Api.deleteNamespacedEndpoints(sourceName,
                  namespace, null, null, null,
                  null, null, v1DeleteOptions);
              return true;
            case STATEFULSET:
              appsV1Api.deleteNamespacedStatefulSet(sourceName,
                  namespace, null, null, null,
                  null, null, v1DeleteOptions);
              return true;
            default:
              break;
          }
        } catch (ApiException e) {
          e.printStackTrace();
        } catch (JsonSyntaxException e) {
          log.error("出现某不知名的错误!!!");
          e.printStackTrace();
          return K8sExceptionUtils.anylse(e);
        }
      }
    }
    return false;
  }
  
  /**
   * 更换镜像
   *
   * @param deployYaml
   * @param namespace
   * @param moduleEnv
   * @param mirrorName
   * @return
   */
  @Override
  public boolean replaceNamespacedSourceMirror(ModuleDeployYaml deployYaml, String namespace, ModuleEnv moduleEnv, String mirrorName) {
    boolean success = false;
    
    String yamlName = deployYaml.getYamlName();
    ArrayList<JsonObject> jsonObjects;
    List<Integer> oldEnvList = Arrays.asList(oldEnvId);
    K8sKindTypeEnum enumByType = K8sKindTypeEnum.getEnumByType(deployYaml.getYamlType());
    if (enumByType != null && StringUtils.isNotBlank(mirrorName) && StringUtils.isNotBlank(yamlName)) {
      StringBuilder url = new StringBuilder();
      try {
        switch (enumByType) {
          case POD:
            url.append(V1_API_PREFIX).append(namespace).append(V1_PODS).append(yamlName);
            jsonObjects = K8sUtils.generatePatchPath(K8sPatchMirror.CONTAINER_MIRROR_PATH, mirrorName, null);
            K8sUtils.okhttpPatchBack(moduleEnv.getK8sConfig(), url.toString(), jsonObjects);
            success = true;
            break;
          case REPLICASET:
            if (oldEnvList.contains(moduleEnv.getId())) {
              url.append(EXTENSION_V1BETA1_PREFIX);
            } else {
              url.append(APPS_V1);
            }
            url.append(namespace).append(EXTENSION_REPLICASETS).append(yamlName);
            jsonObjects = K8sUtils.generatePatchPath(K8sPatchMirror.TEMPLATE_CONTAINER_MIRROR_PATH, mirrorName, null);
            K8sUtils.okhttpPatchBack(moduleEnv.getK8sConfig(), url.toString(), jsonObjects);
            success = true;
            break;
          case DAEMONSET:
            if (oldEnvList.contains(moduleEnv.getId())) {
              url.append(EXTENSION_V1BETA1_PREFIX);
            } else {
              url.append(APPS_V1);
            }
            url.append(namespace).append(EXTENSION_DAEMONSETS).append(yamlName);
            jsonObjects = K8sUtils.generatePatchPath(K8sPatchMirror.TEMPLATE_CONTAINER_MIRROR_PATH, mirrorName, null);
            K8sUtils.okhttpPatchBack(moduleEnv.getK8sConfig(), url.toString(), jsonObjects);
            //
            //AppsV1Api appsV1Api = K8sManagement.getAppsV1Api(moduleEnv.getK8sConfig());
            //
            //try {
            //    V1Deployment v1Deployment   = appsV1Api.readNamespacedDeployment(yamlName, namespace, "true", true, null);
            //    V1PodTemplateSpec template=v1Deployment.getSpec().getTemplate();
            //    if(v1Deployment!=null&& v1Deployment.getSpec().getTemplate()!=null&& template.getSpec().getContainers().size()>0){
            //        V1Container container=  template.getSpec().getContainers().get(0);
            //        container.setImage(mirrorName);
            //        v1Deployment.getSpec().getTemplate().getSpec().getContainers().set(0,container);
            //        appsV1Api.replaceNamespacedDeployment(yamlName, namespace, v1Deployment, "true",null, null);
            //    }
            //
            //} catch (ApiException e) {
            //    log.error("k8s api 操作异常");
            //    e.printStackTrace();
            //}
            success = true;
            break;
          case DEPLOYMENT:
            if (oldEnvList.contains(moduleEnv.getId())) {
              url.append(EXTENSION_V1BETA1_PREFIX);
            } else {
              url.append(APPS_V1);
            }
            url.append(namespace).append(EXTENSION_DEPLOYMENTS).append(yamlName);
            jsonObjects = K8sUtils.generatePatchPath(K8sPatchMirror.TEMPLATE_CONTAINER_MIRROR_PATH, mirrorName, null);
            K8sUtils.okhttpPatchBack(moduleEnv.getK8sConfig(), url.toString(), jsonObjects);
            
            success = true;
            break;
          case REPLICATIONCONTROLLER:
            url.append(V1_API_PREFIX).append(namespace).append(V1_REPLICATIONCONTROLLERS).append(yamlName);
            jsonObjects = K8sUtils.generatePatchPath(K8sPatchMirror.TEMPLATE_CONTAINER_MIRROR_PATH, mirrorName, null);
            K8sUtils.okhttpPatchBack(moduleEnv.getK8sConfig(), url.toString(), jsonObjects);
            success = true;
            break;
        }
      } catch (ApiException e) {
        log.error("k8s api 操作异常");
        e.printStackTrace();
      }
    }
    return success;
  }
  
  /**
   * 获取svc信息通过yaml的名称
   *
   * @param moduleEnv
   * @param yamlName
   * @return
   */
  @Override
  public V1Service getSvcInfoByName(ModuleEnv moduleEnv, String yamlName, String namespace) {
    V1Service v1Service = null;
    if (StringUtils.isNotBlank(yamlName) && moduleEnv != null && StringUtils.isNotBlank(moduleEnv.getK8sConfig())) {
      CoreV1Api coreV1Api = getCoreV1ApiByConfig(moduleEnv);
      if (coreV1Api != null) {
        try {
          v1Service = coreV1Api.readNamespacedService(yamlName, namespace, null);
  
        } catch (ApiException e) {
          e.printStackTrace();
        }
      }
    }
    return v1Service;
  }
  
  /**
   * 获取某个环境的名称空间下的所有资源
   *
   * @param env
   * @param namespace
   * @return
   */
  @Override
  public List<Object> listAllNameSpacedResource(ModuleEnv env, String namespace, String kind) {
    if (env != null && StringUtils.isNotBlank(env.getK8sConfig())
        && StringUtils.isNotBlank(namespace)) {
      K8sKindTypeEnum enumByType = K8sKindTypeEnum.getEnumByType(kind);
      if (enumByType != null) {
        List<Integer> oldEnvList = Arrays.asList(oldEnvId);
        CoreV1Api coreV1Api = getCoreV1ApiByConfig(env);
        AppsV1Api appsV1Api = getAppsV1Api(env);
        AppsV1Api v1beta1Api = getExtensionsV1beta1ApiByConfig(env);
        NetworkingV1Api networkingV1Api = getNetworkingV1Api(env.getK8sConfig());
  
        if (coreV1Api != null && appsV1Api != null) {
          List<Object> objectList = new ArrayList<>();
          StringBuilder url = new StringBuilder(EXTENSION_V1BETA1_PREFIX + namespace);
          String back;
          try {
            if (oldEnvList.contains(env.getId())) {
              switch (enumByType) {
                case DEPLOYMENT:
                  url.append(EXTENSION_DEPLOYMENTS);
                  back = K8sUtils.okhttpGetBack(env.getK8sConfig(), url.toString());
                  V1DeploymentList deploymentList =
                      K8sUtils.getObject(Yaml.load(back), V1DeploymentList.class);
  
                  if (deploymentList != null && deploymentList.getItems().size() > 0
                      && objectList.addAll(
                      deploymentList.getItems())) {
                    return objectList;
                  }
                  break;
                case REPLICASET:
                  url.append(EXTENSION_REPLICASETS);
                  back = K8sUtils.okhttpGetBack(env.getK8sConfig(), url.toString());
                  V1ReplicaSetList replicaSetList =
                      K8sUtils.getObject(Yaml.load(back), V1ReplicaSetList.class);
  
                  if (replicaSetList != null && replicaSetList.getItems().size() > 0
                      && objectList.addAll(replicaSetList.getItems())) {
                    return objectList;
                  }
                  break;
                case DAEMONSET:
                  url.append(EXTENSION_DAEMONSETS);
                  back = K8sUtils.okhttpGetBack(env.getK8sConfig(), url.toString());
                  V1DaemonSetList daemonSetList =
                      K8sUtils.getObject(Yaml.load(back), V1DaemonSetList.class);
  
                  if (daemonSetList != null && daemonSetList.getItems().size() > 0
                      && objectList.addAll(daemonSetList.getItems())) {
                    return objectList;
                  }
                  break;
              }
            }
            switch (enumByType) {
              case SERVICE:
                V1ServiceList serviceList = coreV1Api.listNamespacedService(
                    namespace, null,
                    null, null, null,
                    null, null, null, null,
    
                    500, null);
                if (serviceList != null && serviceList.getItems().size() > 0
                    && objectList.addAll(serviceList.getItems())) {
                  return objectList;
                }
                break;
              case DEPLOYMENT:
                V1DeploymentList deploymentList =
                    appsV1Api.listNamespacedDeployment(
                        namespace, null,
                        null, null, null,
                        null, null, null, null,
    
                        500, null);
                if (deploymentList != null && deploymentList.getItems().size() > 0
                    && objectList.addAll(
                    deploymentList.getItems())) {
                  return objectList;
                }
                break;
              case REPLICASET:
                V1ReplicaSetList replicaSetList =
                    appsV1Api.listNamespacedReplicaSet(
                        namespace, null,
                        null, null, null, null,
    
                        null, null, null,
                        500, null);
                if (replicaSetList != null && replicaSetList.getItems().size() > 0
                    && objectList.addAll(replicaSetList.getItems())) {
                  return objectList;
                }
                break;
              case DAEMONSET:
                V1DaemonSetList daemonSetList =
                    appsV1Api.listNamespacedDaemonSet(
                        namespace, null,
                        null, null, null,
                        null, null, null, null,
    
                        500, null);
                if (daemonSetList != null && daemonSetList.getItems().size() > 0
                    && objectList.addAll(daemonSetList.getItems())) {
                  return objectList;
                }
                break;
              case INGRESS:
                V1IngressList ingressList =
                    networkingV1Api.listNamespacedIngress(
                        namespace, null, null,
    
                        null, null, null,
                        null, null, null,
                        500, null);
                if (ingressList != null && ingressList.getItems().size() > 0
                    && objectList.addAll(ingressList.getItems())) {
                  return objectList;
                }
                break;
              case REPLICATIONCONTROLLER:
                V1ReplicationControllerList replicationControllerList =
                    coreV1Api.listNamespacedReplicationController(
                        namespace, null,
                        null, null, null,
                        null, null, null, null,
    
                        500, null);
                if (replicationControllerList != null
                    && replicationControllerList.getItems().size() > 0
                    && objectList.addAll(replicationControllerList.getItems())) {
                  return objectList;
                }
                break;
              case POD:
                V1PodList v1PodList =
                    coreV1Api.listNamespacedPod(
                        namespace, null,
                        null, null, null,
                        null, null, null, null,
    
                        500, null);
                if (v1PodList != null && v1PodList.getItems().size() > 0
                    && objectList.addAll(v1PodList.getItems())) {
                  return objectList;
                }
                break;
              case CONFIG_MAP:
                V1ConfigMapList v1ConfigMapList =
                    coreV1Api.listNamespacedConfigMap(
                        namespace, null,
                        null, null, null,
                        null, null, null, null,
    
                        500, null);
                if (v1ConfigMapList != null && v1ConfigMapList.getItems().size() > 0
                    && objectList.addAll(v1ConfigMapList.getItems())) {
                  return objectList;
                }
  
                break;
              case NODE:
                V1NodeList v1NodeList = coreV1Api.listNode(null, null, null, null, null, null, null, null, null, null);
  
                if (v1NodeList != null && v1NodeList.getItems().size() > 0
                    && objectList.addAll(v1NodeList.getItems())) {
                  return objectList;
                }
                break;
              case ENDPOINTS:
                V1EndpointsList v1EndpointsList = coreV1Api.listNamespacedEndpoints(
                    namespace, null,
                    null, null, null,
                    null, null, null, null,
    
                    500, null);
                if (v1EndpointsList != null && v1EndpointsList.getItems().size() > 0
                    && objectList.addAll(v1EndpointsList.getItems())) {
                  return objectList;
                }
                break;
              default:
                break;
            }
          } catch (ApiException | IOException e) {
            log.error("获取资源失败 错误信息:{} response:{}",
                e.getMessage(), e.getCause());
            e.printStackTrace();
          }
        }
      }
    }
    return null;
  }
  
  /**
   * 根据资源名称获取资源的详细内容通过okHttp的方式
   *
   * @param resourceName
   * @param moduleEnv
   * @param namespace
   * @param kind
   * @return
   */
  @Override
  public String getResourceByNameUseOKHttp(String resourceName, ModuleEnv moduleEnv, String namespace, String kind) {
    String yamlJsonBack = null;
    if (StringUtils.isNotBlank(kind) && StringUtils.isNotBlank(resourceName) &&
        moduleEnv != null && StringUtils.isNotBlank(moduleEnv.getK8sConfig())) {
      if (StringUtils.isBlank(namespace)) {
        namespace = K8sNameSpace.DEFAULT;
      }
      K8sKindTypeEnum enumByType = K8sKindTypeEnum.getEnumByType(kind);
      List<Integer> oldEnvList = Arrays.asList(oldEnvId);
      StringBuilder url = new StringBuilder();
      if (enumByType != null) {
        switch (enumByType) {
          case SERVICE:
            url.append(V1_API_PREFIX).append(namespace).append(V1_SERVICES).append(resourceName);
            break;
          case CONFIG_MAP:
            url.append(V1_API_PREFIX).append(namespace)
                .append(V1_CONFIGMAPS).append(resourceName);
            break;
          case DEPLOYMENT:
            if (oldEnvList.contains(moduleEnv.getId())) {
              url.append(EXTENSION_V1BETA1_PREFIX);
            } else {
              url.append(APPS_V1);
            }
            url.append(namespace).append(EXTENSION_DEPLOYMENTS).append(resourceName);
            break;
          case REPLICASET:
            if (oldEnvList.contains(moduleEnv.getId())) {
              url.append(EXTENSION_V1BETA1_PREFIX);
            } else {
              url.append(APPS_V1);
            }
            url.append(namespace).append(EXTENSION_REPLICASETS).append(resourceName);
            break;
          case DAEMONSET:
            if (oldEnvList.contains(moduleEnv.getId())) {
              url.append(EXTENSION_V1BETA1_PREFIX);
            } else {
              url.append(APPS_V1);
            }
            url.append(namespace).append(EXTENSION_DAEMONSETS).append(resourceName);
            break;
          case INGRESS:
            url.append(EXTENSION_V1BETA1_PREFIX).append(namespace)
                .append(EXTENSION_INGRESSES).append(resourceName);
            break;
          case REPLICATIONCONTROLLER:
            url.append(V1_API_PREFIX).append(namespace)
                .append(V1_REPLICATIONCONTROLLERS).append(resourceName);
            break;
          case POD:
            url.append(V1_API_PREFIX).append(namespace).append(V1_PODS).append(resourceName);
            break;
          case ENDPOINTS:
            url.append(V1_API_PREFIX).append(namespace).append(V1_ENDPOINTS)
                .append(resourceName);
            break;
          case STATEFULSET:
            url.append(APPS_V1BETA2).append(namespace).append(APPS_STATEFUL)
                .append(resourceName);
            break;
          default:
            break;
        }
        yamlJsonBack = K8sUtils.okhttpGetBack(moduleEnv.getK8sConfig(), url.toString());
        log.info("ok_http 获取资源url:{} back:{}", url.toString(), yamlJsonBack);
        if (StringUtils.isNotBlank(yamlJsonBack)) {
          //判断是否为404的状态来确定资源是否找到
          JSONObject object = JSONObject.parseObject(yamlJsonBack);
          int code = object.getIntValue("code");
          if (code == 404) {
            yamlJsonBack = null;
          }
        }
      }
    }
    return yamlJsonBack;
  }
  
  /**
   * 根据资源名称获取资源object内容 这样不用转对象操作
   *
   * @param resourceName
   * @param moduleEnv
   * @param namespace
   * @param kindType
   * @return
   */
  @Override
  public <T> T readNameSpacedResource(String resourceName, ModuleEnv moduleEnv,
                                      String namespace, String kindType, Class<T> clazz) {
    T cast = null;
    Object resource = null;
    K8sKindTypeEnum typeEnum = K8sKindTypeEnum.getEnumByType(kindType);
    List<Integer> oldEnvList = Arrays.asList(oldEnvId);
    String back;
    StringBuilder url = new StringBuilder();
    if (moduleEnv != null && typeEnum != null
        && StringUtils.isNotBlank(moduleEnv.getK8sConfig())) {
      try {
        switch (typeEnum) {
          case POD:
            url.append(V1_API_PREFIX).append(namespace).append(V1_PODS).append(resourceName);
            back = K8sUtils.okhttpGetBack(moduleEnv.getK8sConfig(), url.toString());
            resource = Yaml.load(back);
            break;
          case REPLICATIONCONTROLLER:
            url.append(V1_API_PREFIX).append(namespace).append(V1_REPLICATIONCONTROLLERS).append(resourceName);
            back = K8sUtils.okhttpGetBack(moduleEnv.getK8sConfig(), url.toString());
            resource = Yaml.load(back);
            break;
          case INGRESS:
            url.append(EXTENSION_V1BETA1_PREFIX).append(namespace).append(EXTENSION_INGRESSES).append(resourceName);
            back = K8sUtils.okhttpGetBack(moduleEnv.getK8sConfig(), url.toString());
            resource = Yaml.load(back);
            break;
          case DAEMONSET:
            if (oldEnvList.contains(moduleEnv.getId())) {
              url.append(EXTENSION_V1BETA1_PREFIX);
            } else {
              url.append(APPS_V1);
            }
            url.append(namespace).append(EXTENSION_DAEMONSETS).append(resourceName);
            back = K8sUtils.okhttpGetBack(moduleEnv.getK8sConfig(), url.toString());
            resource = Yaml.load(back);
            break;
          case REPLICASET:
            if (oldEnvList.contains(moduleEnv.getId())) {
              url.append(EXTENSION_V1BETA1_PREFIX);
            } else {
              url.append(APPS_V1);
            }
            url.append(namespace).append(EXTENSION_REPLICASETS).append(resourceName);
            back = K8sUtils.okhttpGetBack(moduleEnv.getK8sConfig(), url.toString());
            resource = Yaml.load(back);
            break;
          case DEPLOYMENT:
            if (oldEnvList.contains(moduleEnv.getId())) {
              url.append(EXTENSION_V1BETA1_PREFIX);
            } else {
              url.append(APPS_V1);
            }
            url.append(namespace).append(EXTENSION_DEPLOYMENTS).append(resourceName);
            back = K8sUtils.okhttpGetBack(moduleEnv.getK8sConfig(), url.toString());
            //  final String json = new Gson().toJson(back);
            resource = Yaml.loadAs(back, clazz);
            return cast = clazz.cast(resource);
          case CONFIG_MAP:
            url.append(V1_API_PREFIX).append(namespace).append(V1_CONFIGMAPS).append(resourceName);
            back = K8sUtils.okhttpGetBack(moduleEnv.getK8sConfig(), url.toString());
            resource = Yaml.load(back);
            break;
          case SERVICE:
            url.append(V1_API_PREFIX).append(namespace).append(V1_SERVICES).append(resourceName);
            back = K8sUtils.okhttpGetBack(moduleEnv.getK8sConfig(), url.toString());
            resource = Yaml.load(back);
            break;
          default:
            break;
        }
      } catch (IOException e) {
        e.printStackTrace();
        log.error("获取资源出现错误  msg:{}", e.getMessage());
      }
    }
    
    if (resource != null) {
      if (resource.getClass().equals(clazz))
        cast = clazz.cast(resource);
      if (V1Deployment.class.equals(resource.getClass())) {
        V1Deployment deployment =
            K8sUtils.getObject(resource, V1Deployment.class);
  
        V1Deployment v1Deployment = K8sUtils.toV1Deploy(deployment);
        cast = clazz.cast(v1Deployment);
      }
  
    }
    return cast;
  }
  
  /**
   * 获取资源调度类
   *
   * @param env
   * @return
   */
  @Override
  public AppsV1Api getAppsV1Api(ModuleEnv env) {
    AppsV1Api appsV1Api = null;
    if (env != null && StringUtils.isNotBlank(env.getK8sConfig())) {
      try {
        appsV1Api = K8sManagement.getAppsV1Api(env.getK8sConfig());
      } catch (Exception e) {
        log.error("初始化k8sconfig error", e);
        e.printStackTrace();
      }
    }
    return appsV1Api;
  }
//
//    @Override
//    public AppsV1beta2Api getAppsV1beta2Api(ModuleEnv env) {
//        AppsV1beta2Api appsV1beta2Api = null;
//        if (env != null && StringUtils.isNotBlank(env.getK8sConfig())) {
//            try {
//                appsV1beta2Api = K8sManagement.getAppsV1beta2Api(env.getK8sConfig());
//            } catch (Exception e) {
//                log.error("初始化k8sconfig error", e);
//                e.printStackTrace();
//            }
//        }
//        return appsV1beta2Api;
//    }
  
  /**
   * 获取资源调度类
   *
   * @param env
   * @return
   */
  @Override
  public AppsV1Api getExtensionsV1beta1ApiByConfig(ModuleEnv env) {
    AppsV1Api v1beta1Api = null;
  
    if (env != null && StringUtils.isNotBlank(env.getK8sConfig())) {
      try {
        v1beta1Api = K8sManagement.getExtensionApi(env.getK8sConfig());
      } catch (Exception e) {
        log.error("初始化k8sconfig error", e);
        e.printStackTrace();
      }
    }
    return v1beta1Api;
  }
  
  public BatchV1Api getBatchV1Api(ModuleEnv env) {
    BatchV1Api coreV1Api = null;
    if (env != null && StringUtils.isNotBlank(env.getK8sConfig())) {
      try {
        coreV1Api = K8sManagement.getBatchV1Api(env.getK8sConfig());
      } catch (Exception e) {
        log.error("初始化k8sconfig error", e);
        e.printStackTrace();
      }
    }
    
    return coreV1Api;
  }
  
  public NetworkingV1Api getNetworkingV1Api(ModuleEnv env) {
    NetworkingV1Api coreV1Api = null;
    if (env != null && StringUtils.isNotBlank(env.getK8sConfig())) {
      return getNetworkingV1Api(env.getK8sConfig());
    }
    
    return coreV1Api;
  }
  
  public NetworkingV1Api getNetworkingV1Api(String config) {
    NetworkingV1Api coreV1Api = null;
    
    try {
      coreV1Api = K8sManagement.getNetworkingV1Api(config);
    } catch (Exception e) {
      log.error("初始化k8sconfig error", e);
      e.printStackTrace();
    }
    
    return coreV1Api;
  }
  
  /**
   * 获取资源调度类
   *
   * @param env
   * @return
   */
  @Override
  public CoreV1Api getCoreV1ApiByConfig(ModuleEnv env) {
    CoreV1Api coreV1Api = null;
    if (env != null && StringUtils.isNotBlank(env.getK8sConfig())) {
      try {
        coreV1Api = K8sManagement.getCoreV1Api(env.getK8sConfig());
      } catch (Exception e) {
        log.error("初始化k8sconfig error", e);
        e.printStackTrace();
      }
    }
    
    return coreV1Api;
  }
  
  /**
   * 获取容器的limit
   *
   * @param deployment
   * @return
   */
  @Override
  public Map<String, String> getContainerLimit(V1Deployment deployment) {
    Map<String, String> resourcesMap = new HashMap<>();
    if (deployment != null) {
      V1ResourceRequirements resources = deployment.getSpec().getTemplate().getSpec()
          .getContainers().get(0).getResources();
      if (resources != null) {
        Map<String, Quantity> limits = resources.getLimits();
        if (limits != null) {
          for (String key : limits.keySet()) {
            resourcesMap.put(key, limits.get(key).toSuffixedString());
          }
        }
      }
    }
    return resourcesMap;
  }
  
  /**
   * 获取应用容器的request
   *
   * @param deployment
   * @return
   */
  @Override
  public Map<String, String> getContainerRequest(V1Deployment deployment) {
    Map<String, String> resourcesMap = new HashMap<>();
    if (deployment != null) {
      V1ResourceRequirements resources = deployment.getSpec().getTemplate().getSpec()
          .getContainers().get(0).getResources();
      if (resources != null) {
        Map<String, Quantity> requests = resources.getRequests();
        if (requests != null) {
          for (String key : requests.keySet()) {
            resourcesMap.put(key, requests.get(key).toSuffixedString());
          }
        }
      }
    }
    return resourcesMap;
  }
}
