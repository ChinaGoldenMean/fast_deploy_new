package com.xc.fast_deploy.service.k8s;

import com.xc.fast_deploy.dto.k8s.K8sServiceDTO;
import com.xc.fast_deploy.model.master_model.ModuleEnv;

import java.util.List;

public interface K8sSvcService {
  
  List<K8sServiceDTO> getEnvAllService(ModuleEnv env);
  
  K8sServiceDTO getServiceByPodName(ModuleEnv env, String podName, String yamlNamespace);
  
  boolean deleteService(String serviceName, ModuleEnv env);
  
}
