package com.xc.fast_deploy.service.k8s.impl;

import com.google.gson.JsonSyntaxException;
import com.xc.fast_deploy.dto.k8s.K8sContainerDTO;
import com.xc.fast_deploy.dto.k8s.K8sDaemonSetDTO;
import com.xc.fast_deploy.model.master_model.ModuleEnv;
import com.xc.fast_deploy.service.k8s.K8sDaemonSetService;
import com.xc.fast_deploy.service.k8s.K8sService;
import com.xc.fast_deploy.utils.constant.K8sHttpUrlConstants;
import com.xc.fast_deploy.utils.constant.K8sNameSpace;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.AppsV1Api;
import io.kubernetes.client.openapi.models.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.xc.fast_deploy.utils.constant.K8sHttpUrlConstants.*;

@Slf4j
@Service
public class K8sDaemonSetServiceImpl implements K8sDaemonSetService {
  
  @Autowired
  private K8sService k8sService;
  
  @Override
  public List<K8sDaemonSetDTO> getEnvAllDaemonSet(ModuleEnv env) {
    List<K8sDaemonSetDTO> k8sDaemonSetDTOList = new ArrayList<>();
    AppsV1Api appsV1Api = k8sService.getAppsV1Api(env);
    if (appsV1Api != null) {
      try {
        V1DaemonSetList beta1DaemonSetList = appsV1Api.listNamespacedDaemonSet(K8sNameSpace.DEFAULT,
            null, null, null, null, null, null,
            null, null, 300, null);
        List<V1DaemonSet> items = beta1DaemonSetList.getItems();
        if (items != null && items.size() > 0) {
          for (V1DaemonSet daemonSet : items) {
            K8sDaemonSetDTO k8sDaemonSetDTO = new K8sDaemonSetDTO();
            k8sDaemonSetDTO.setName(daemonSet.getMetadata().getName());
            k8sDaemonSetDTO.setAvailableNumber(daemonSet.getStatus().getNumberAvailable());
            k8sDaemonSetDTO.setCurrentNumber(daemonSet.getStatus().getCurrentNumberScheduled());
            k8sDaemonSetDTO.setDesiredNumber(daemonSet.getStatus().getDesiredNumberScheduled());
            k8sDaemonSetDTO.setReadyNumber(daemonSet.getStatus().getNumberReady());
            k8sDaemonSetDTO.setUpdateNumber(daemonSet.getStatus().getUpdatedNumberScheduled());
            List<V1Container> containerList = daemonSet.getSpec().getTemplate().getSpec().getContainers();
            List<K8sContainerDTO> containerDTOS = new ArrayList<>();
            if (containerList != null && containerList.size() > 0) {
              for (V1Container container : containerList) {
                K8sContainerDTO containerDTO = new K8sContainerDTO();
                containerDTO.setContainerName(container.getName());
                containerDTO.setImageName(container.getImage());
                containerDTOS.add(containerDTO);
              }
            }
            k8sDaemonSetDTO.setContainerDTOList(containerDTOS);
            k8sDaemonSetDTOList.add(k8sDaemonSetDTO);
          }
        }
      } catch (ApiException e) {
        log.error("something went wrong when get envAllDaemonset", e);
        e.printStackTrace();
      }
    }
    return k8sDaemonSetDTOList;
  }
  
  @Override
  public boolean deleteDaemonSet(String daemonSetName, ModuleEnv env) {
    if (StringUtils.isNotBlank(daemonSetName)) {
      AppsV1Api appsV1Api = k8sService.getAppsV1Api(env);
      if (appsV1Api != null) {
        try {
          V1Status status = appsV1Api.deleteNamespacedDaemonSet(daemonSetName,
              K8sNameSpace.DEFAULT, null, "false", null,
              null, null, new V1DeleteOptions());
          log.info("状态message : " + status);
          log.info("成功发布daemonSet");
          return true;
        } catch (ApiException e) {
          e.printStackTrace();
        } catch (JsonSyntaxException e) {
          if (e.getCause() instanceof IllegalStateException) {
            IllegalStateException ise = (IllegalStateException) e.getCause();
            if (ise.getMessage() != null && ise.getMessage().contains("Expected a string but was BEGIN_OBJECT")) {
              log.debug("Catching exception because of issue https://github.com/kubernetes-client/java/issues/86", e);
              log.info("成功发布daemonSet");
              return true;
            } else throw e;
          } else throw e;
        }
      }
    }
    return false;
  }
  
  @Override
  public V1DaemonSet getDaemonSetByName(ModuleEnv env, String daemonSetName) {
    if (StringUtils.isNotBlank(daemonSetName)) {
      AppsV1Api appsV1Api = k8sService.getAppsV1Api(env);
      if (appsV1Api != null) {
        V1DaemonSetList beta1DaemonSetList = null;
        try {
          beta1DaemonSetList = appsV1Api.listNamespacedDaemonSet(K8sNameSpace.DEFAULT,
              null, null, null, null, null,
              null, null, null, 300, null);
          List<V1DaemonSet> items = beta1DaemonSetList.getItems();
          if (items != null && items.size() > 0) {
            for (V1DaemonSet daemonSet : items) {
              if (daemonSetName.equals(daemonSet.getMetadata().getName())) {
                return daemonSet;
              }
            }
          }
        } catch (ApiException e) {
          e.printStackTrace();
        }
      }
    }
    return null;
  }
}
