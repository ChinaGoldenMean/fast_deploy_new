package com.xc.fast_deploy.service.k8s.impl;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.JsonSyntaxException;
import com.xc.fast_deploy.dto.k8s.K8sContainerDTO;
import com.xc.fast_deploy.dto.k8s.K8sDeploymentDTO;
import com.xc.fast_deploy.model.master_model.ModuleEnv;
import com.xc.fast_deploy.service.k8s.K8sDeploymentService;
import com.xc.fast_deploy.service.k8s.K8sService;
import com.xc.fast_deploy.utils.constant.K8sNameSpace;
import com.xc.fast_deploy.utils.k8s.K8sExceptionUtils;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.AppsV1Api;
import io.kubernetes.client.openapi.models.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class K8sDeploymentServiceImpl implements K8sDeploymentService {
  
  @Autowired
  private K8sService k8sService;
  
  /**
   * 获取某个环境的deployment信息
   *
   * @param env
   * @return
   */
  @Override
  public List<K8sDeploymentDTO> getEnvAllDeployment(ModuleEnv env) {
    List<K8sDeploymentDTO> deploymentDTOS = new ArrayList<>();
    AppsV1Api appsV1Api = k8sService.getAppsV1Api(env);
    if (appsV1Api != null) {
      try {
        V1DeploymentList deploymentList = appsV1Api.listNamespacedDeployment(K8sNameSpace.DEFAULT,
            null, null, null, null, null,
            null, null, null, 300, null);
        List<V1Deployment> listItems = deploymentList.getItems();
        for (V1Deployment deployment : listItems) {
          K8sDeploymentDTO deploymentDTO = new K8sDeploymentDTO();
          deploymentDTO.setDeploymentName(deployment.getMetadata().getName());
          deploymentDTO.setReplicas(deployment.getStatus().getReplicas());
          deploymentDTO.setUpdatedReplicas(deployment.getStatus().getUpdatedReplicas());
          deploymentDTO.setAvailableReplicas(deployment.getStatus().getAvailableReplicas());
          List<V1Container> containers = deployment.getSpec().getTemplate().getSpec().getContainers();
          if (containers != null && containers.size() > 0) {
            ArrayList<K8sContainerDTO> containerDTOS = new ArrayList<>();
            for (V1Container v1Container : containers) {
              K8sContainerDTO containerDTO = new K8sContainerDTO();
              containerDTO.setContainerName(v1Container.getName());
              containerDTO.setDescribeMsg(null);
              containerDTO.setImageName(v1Container.getImage());
              containerDTO.setRestartCount(null);
              containerDTO.setStatus(null);
              containerDTOS.add(containerDTO);
            }
            deploymentDTO.setContainerDTOList(containerDTOS);
          }
          deploymentDTOS.add(deploymentDTO);
        }
        log.info(JSONObject.toJSONString(listItems));
      } catch (ApiException e) {
        log.error("获取deployment error", e);
        e.printStackTrace();
      }
    }
    return deploymentDTOS;
  }
  
  /**
   * 删除deployment信息
   *
   * @param deploymentName
   * @param env
   * @return
   */
  @Override
  public boolean deleteDeployment(String deploymentName, ModuleEnv env) {
    if (StringUtils.isNotBlank(deploymentName)) {
      AppsV1Api appsV1Api = k8sService.getAppsV1Api(env);
      if (appsV1Api != null) {
        try {
          V1Status v1Status = appsV1Api.deleteNamespacedDeployment(deploymentName, K8sNameSpace.DEFAULT, null, null,
              null, null, null, new V1DeleteOptions());
          log.info("状态message : " + v1Status);
          return true;
        } catch (ApiException e) {
          log.error("something went wrong when delete deployment", e);
          e.printStackTrace();
        } catch (JsonSyntaxException e) {
          return K8sExceptionUtils.anylse(e);
        }
      }
      
    }
    return false;
  }
  
  @Override
  public V1Deployment getDeploymentByName(ModuleEnv env, String deploymentName) {
    if (StringUtils.isNotBlank(deploymentName) && env != null && StringUtils.isNotBlank(env.getK8sConfig())) {
      AppsV1Api appsV1Api = k8sService.getAppsV1Api(env);
      if (appsV1Api != null) {
        try {
          V1DeploymentList deploymentList = appsV1Api.listNamespacedDeployment(K8sNameSpace.DEFAULT, null, null, null, null, null,
              null, null, null, 300, null);
          List<V1Deployment> items = deploymentList.getItems();
          if (items != null && items.size() > 0) {
            for (V1Deployment deployment : items) {
              if (deploymentName.equals(deployment.getMetadata().getName())) {
                return deployment;
              }
            }
          }
        } catch (ApiException e) {
          log.error("获取deployment error", e);
          e.printStackTrace();
        }
      }
    }
    return null;
  }
}
