package com.xc.fast_deploy.service.k8s.impl;

import com.google.gson.JsonSyntaxException;
import com.xc.fast_deploy.dto.k8s.K8sServiceDTO;
import com.xc.fast_deploy.model.master_model.ModuleEnv;
import com.xc.fast_deploy.service.k8s.K8sPodService;
import com.xc.fast_deploy.service.k8s.K8sService;
import com.xc.fast_deploy.service.k8s.K8sSvcService;
import com.xc.fast_deploy.utils.constant.K8sNameSpace;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class K8sSvcServiceImpl implements K8sSvcService {
  
  @Autowired
  private K8sService k8sService;
  @Autowired
  private K8sPodService k8sPodService;
  
  @Override
  public List<K8sServiceDTO> getEnvAllService(ModuleEnv env) {
    List<K8sServiceDTO> serviceDTOS = new ArrayList<>();
    CoreV1Api coreV1Api = k8sService.getCoreV1ApiByConfig(env);
    if (coreV1Api != null) {
      try {
        V1ServiceList serviceList = coreV1Api.listNamespacedService(K8sNameSpace.DEFAULT, null, null, null, null,
            null, null, null, 300, null);
        List<V1Service> items = serviceList.getItems();
        if (items != null && items.size() > 0) {
          for (V1Service v1Service : items) {
            K8sServiceDTO serviceDTO = new K8sServiceDTO();
//                        serviceDTO.setName(v1Service.getMetadata().getName());
//                        serviceDTO.setClusterIP(v1Service.getSpec().getClusterIP());
//                        serviceDTO.setExternalIPs(v1Service.getSpec().getExternalIPs());
//                        serviceDTO.setSelector(v1Service.getSpec().getSelector());
//                        serviceDTO.setType(v1Service.getSpec().getType());
//
//                        ArrayList<K8sServicePortDTO> servicePortDTOS = new ArrayList<>();
//                        List<V1ServicePort> ports = v1Service.getSpec().getPorts();
//                        if (ports != null && ports.size() > 0) {
//                            for (V1ServicePort servicePort : ports) {
//                                K8sServicePortDTO servicePortDTO = new K8sServicePortDTO();
//                                servicePortDTO.setName(servicePort.getName());
//                                servicePortDTO.setNodePort(servicePort.getNodePort());
//                                servicePortDTO.setProtocol(servicePort.getProtocol());
//                                servicePortDTO.setTargetPort(servicePort.getTargetPort() == null ? null : servicePort.getTargetPort().toString());
//                                servicePortDTO.setPort(servicePort.getPort());
//                                servicePortDTOS.add(servicePortDTO);
//                            }
//                        }
//                        serviceDTO.setServicePortDTOList(servicePortDTOS);
            serviceDTOS.add(serviceDTO);
          }
        }
      } catch (ApiException e) {
        log.error("获取service error", e);
        e.printStackTrace();
      }
    }
    return serviceDTOS;
  }
  
  /**
   * 根据podName取得相关的service
   *
   * @param env
   * @param podName
   * @return
   */
  @Override
  public K8sServiceDTO getServiceByPodName(ModuleEnv env, String podName, String yamlNamespace) {
    V1Pod v1Pod = k8sPodService.getPodByPodName(env, podName, yamlNamespace);
    if (v1Pod != null) {
      List<K8sServiceDTO> serviceDTOS = getEnvAllService(env);
      if (serviceDTOS != null && serviceDTOS.size() > 0) {
        Map<String, String> podLabels = v1Pod.getMetadata().getLabels();
        if (podLabels != null && podLabels.size() > 0) {
          for (K8sServiceDTO serviceDTO : serviceDTOS) {
            Map<String, String> serviceDTOSelector = serviceDTO.getSelector();
            if (serviceDTOSelector != null && serviceDTOSelector.size() > 0) {
              for (String podLabelKey : podLabels.keySet()) {
                String s = serviceDTOSelector.get(podLabelKey);
                if (s != null && s.equals(podLabels.get(podLabelKey))) {
                  return serviceDTO;
                }
              }
            }
          }
        }
      }
    }
    return null;
  }
  
  @Override
  public boolean deleteService(String serviceName, ModuleEnv env) {
    
    if (StringUtils.isNotBlank(serviceName) && env != null && StringUtils.isNotBlank(env.getK8sConfig())) {
      CoreV1Api coreV1Api = k8sService.getCoreV1ApiByConfig(env);
      try {
        V1Status status = coreV1Api.deleteNamespacedService(serviceName, K8sNameSpace.DEFAULT, null, "false", null,
            null, null, new V1DeleteOptions());
        return true;
      } catch (ApiException e) {
        log.error("something went wrong when delete service", e);
        e.printStackTrace();
      } catch (JsonSyntaxException e) {
        if (e.getCause() instanceof IllegalStateException) {
          IllegalStateException ise = (IllegalStateException) e.getCause();
          if (ise.getMessage() != null && ise.getMessage().contains("Expected a string but was BEGIN_OBJECT")) {
            log.debug("Catching exception because of issue https://github.com/kubernetes-client/java/issues/86",
                e);
            log.info("delete service" + serviceName + " success");
            return true;
          } else throw e;
        } else throw e;
      }
    }
    return false;
  }
}
