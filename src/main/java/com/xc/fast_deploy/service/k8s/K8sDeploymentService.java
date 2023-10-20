package com.xc.fast_deploy.service.k8s;

import com.xc.fast_deploy.dto.k8s.K8sDeploymentDTO;
import com.xc.fast_deploy.model.master_model.ModuleEnv;
import io.kubernetes.client.openapi.models.V1Deployment;

import java.util.List;

public interface K8sDeploymentService {
  
  List<K8sDeploymentDTO> getEnvAllDeployment(ModuleEnv env);
  
  boolean deleteDeployment(String deploymentName, ModuleEnv env);
  
  V1Deployment getDeploymentByName(ModuleEnv env, String deploymentName);
}
